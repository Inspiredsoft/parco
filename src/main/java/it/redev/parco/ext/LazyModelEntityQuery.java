/*******************************************************************************
* Inspired Model Exporter is a framework to export data from pojo class.
* Copyright (C) 2016 Inspired Soft
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.    
*******************************************************************************/

package it.redev.parco.ext;

import it.redev.parco.model.core.AggregateModel;
import it.inspired.utils.ReflactionUtils;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.Query;

import org.jboss.solder.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import pl.com.it_crowd.seam.framework.EntityQuery;
import pl.com.it_crowd.seam.framework.QueryParser;
/**
 * 
 * @author Massimo Romano
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class LazyModelEntityQuery<T> extends EntityQuery<T> implements Serializable
{
	@Inject
	Logger log;
	
	private LazyDataModel<T> lazyModel;
	
	AggregateModel aggregate = null;
	
	private static final Pattern FROM_PATTERN = Pattern.compile("(^|\\s)(from)\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern ORDER_PATTERN = Pattern.compile("\\s(order)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);
	
	//-----------------------------------------------------------------------------------------------
	
	public LazyModelEntityQuery()
	{
		super();
	    constructLazyModel();
	}
	
	//-----------------------------------------------------------------------------------------------
	
	protected void constructLazyModel() 
	{
		final EntityQuery<T> instance = this;
	      
		lazyModel = new LazyDataModel<T>()
		{
			@Override
		    public void setRowIndex(int rowIndex) 
			{
		        /*
		         * The following is in ancestor (LazyDataModel):
		         * this.rowIndex = rowIndex == -1 ? rowIndex : (rowIndex % pageSize);
		         */
		        if ( rowIndex == -1 || super.getPageSize() == 0 ) 
		        {
		           super.setRowIndex( -1 );
		        }
		        else
		        {
		        	super.setRowIndex( rowIndex );
		        }
		    }
			
			@Override
			public int getRowCount() 
			{
				if ( getRestrictions().isEmpty() )
				{
					setRestrictionExpressionStrings( getRestrictionExpressionStrings() );
				}
				
				return instance.getResultCount().intValue();
			}
	
			@Override
			public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) 
			{
				log.debugv( "First={0}, PageSize={1}, SortField={2}, SortOrder={3}", first, pageSize, sortField, sortOrder );
				
				if ( getRestrictions().isEmpty() )
				{
					setRestrictionExpressionStrings( getRestrictionExpressionStrings() );
				}
				
				if ( !StringUtils.isEmpty(sortField) && sortOrder != null && !sortOrder.equals( SortOrder.UNSORTED ) )
				{
					instance.setOrderColumn( sortField );
					String sortDir = ( sortOrder.equals( SortOrder.ASCENDING) ? "ASC" : "DESC" );
					instance.setOrderDirection( sortDir );
				}
				else
				{
					instance.setOrderColumn( "id" );
					instance.setOrderDirection( "DESC" );
				}
				
				super.setPageSize( pageSize );
				super.setRowIndex( first );
				
				instance.setFirstResult(first);
				instance.setMaxResults(pageSize);
				
				return instance.getResultList();
			}
		         
		};
	}
	
	//-----------------------------------------------------------------------------------------------
	   
	public LazyDataModel<T> getDataModel() 
	{
		return lazyModel;
	}
	
	//-----------------------------------------------------------------------------------------------
	
	public boolean isEmpty()
	{
		return getRowCount() == 0;
	}
	
	//-----------------------------------------------------------------------------------------------
	
	public void clearParameters()
	{
		ReflactionUtils.clearAccessibleField( this );
	}
	
	//-----------------------------------------------------------------------------------------------
	
	/*
	 * TODO: da far funzionare
	public void onSave( @Observes @Saved ModelEvent modelEvent )
	{
		log.debugv( "Refreshing after saved object: {0}", modelEvent.getObject() );
		refresh();
	}
	*/
	
	//-----------------------------------------------------------------------------------------------
	
	@Override
	protected String getRenderedEjbql() 
	{
		String query = super.getRenderedEjbql();
		log.debug( query );
		return query;
	}
		
	//-----------------------------------------------------------------------------------------------
	
	public String getAggregateEjbql() 
	{
		return null;
	}
	
	//-----------------------------------------------------------------------------------------------
	
	protected Query createAggregateQuery()
	{
		String aggregateEjbql = getAggregateEjbql();
		if ( StringUtils.isEmpty( aggregateEjbql ) )
		{
			throw new IllegalArgumentException("no aggrgegate query defined");
		}
		
		String renderedEjbql = getRenderedEjbql();
		
		Matcher fromMatcher = FROM_PATTERN.matcher(renderedEjbql);
		if (!fromMatcher.find()) 
		{
            throw new IllegalArgumentException("no from clause found in query");
        }
		int fromLoc = fromMatcher.start(2);
		
		Matcher orderMatcher = ORDER_PATTERN.matcher(renderedEjbql);
        int orderLoc = orderMatcher.find() ? orderMatcher.start(1) : renderedEjbql.length();
		
		String query = aggregateEjbql + " " + renderedEjbql.substring(fromLoc, orderLoc);
		
		log.debug( "AGGREGATE QUERY: " + query );
		
		return getEntityManager().createQuery( query );
	}
	
	//-----------------------------------------------------------------------------------------------
	
	private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
    {
        for (int i = 0; i < parameters.size(); i++) {
            Object parameterValue = parameters.get(i);
            if (isRestrictionParameterSet(parameterValue)) {
                query.setParameter(QueryParser.getParameterName(start + i), parameterValue);
            }
        }
    }
	
	//-----------------------------------------------------------------------------------------------
	
	protected void initAggregate()
	{
		if ( aggregate == null )
		{
			Query query = createAggregateQuery();
			setParameters(query, getQueryParameterValues(), 0);
	        setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
			aggregate = new AggregateModel( query.getSingleResult() );
		}
	}
	
	//-----------------------------------------------------------------------------------------------
	
	public AggregateModel getAggregate()
	{
		if (isAnyParameterDirty())
		{
			aggregate = null;
        }
		initAggregate();
		return aggregate;
	}
	
	//-----------------------------------------------------------------------------------------------
	
	@Override
	public void refresh() 
	{
		aggregate = null;
		super.refresh();
	}
	
	//-----------------------------------------------------------------------------------------------
	
	public int getRowCount() 
	{
		return getDataModel().getRowCount();
	}
}
