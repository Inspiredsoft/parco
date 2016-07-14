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

package it.redev.parco.service;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.solder.logging.Logger;

public class BaseService 
{
	@Inject
	protected Logger log;
	
	@PersistenceContext
	protected EntityManager em;
	
	//-------------------------------------------------------------------------------
	
	public EntityManager getEntityManager() 
	{
		return em;
	}

	public void setEntityManager(EntityManager em) 
	{
		this.em = em;
	}
	
	//-------------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	protected Object getSingleResult( Query query )
	{
		Object result = null;
		List objs = query.getResultList();
    	
    	if ( objs.size() == 1 )
    	{
    		result = objs.get( 0 );
    	}
    	else if ( objs.size() > 1 )
    	{
    		throw new NonUniqueResultException();
    	}
		return result;
	}
	
	//-------------------------------------------------------------------------------
	
	public void save( Object entity )
	{
		em.persist( entity );
	}
	
	//-------------------------------------------------------------------------------
	
	public void remove( Object entity )
	{
		em.remove( entity );
	}
	
	//-------------------------------------------------------------------------------
	
	public void refresh( Object entity )
	{
		em.refresh( entity );
	}
	
	//-------------------------------------------------------------------------------
	
	public void merge( Object entity )
	{
		entity = em.merge( entity );
	}
}
