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

package it.redev.parco.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.User;
import it.redev.parco.model.core.JobParameter;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Soccorso;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.inspired.utils.DateUtils;

public class SoccorsiJob extends AbstractJob
{
	private Integer month;
	private Integer year;
	
	//-------------------------------------------------------------------------------------
	
	public SoccorsiJob(){}
	
	//-------------------------------------------------------------------------------------
	
	public SoccorsiJob(Integer month, Integer year, User user) {
		super();
		this.month 	= month;
		this.year 	= year;
		setUser( user );
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException 
	{
		try 
		{
			for ( Soccorso soc : findSoccorsi() )
			{
				elabora( soc );
			}
			flush();
		}
		catch (Exception e) 
		{
			throw new DatabaseException( e );
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	private List<Soccorso> findSoccorsi()
	{
		Query query = getEntityManager().createQuery(
				"FROM " + Soccorso.class.getName() + " soc " +
				"WHERE soc.anno = :anno " +
				"AND   soc.mese = :mese ")
			.setParameter( "anno", year )
			.setParameter( "mese", month );
		return query.getResultList();
	}
	
	//-------------------------------------------------------------------------------------
	
	private void elabora( Soccorso soc ) throws Exception
	{
		if ( soc.getNumero() > 0 )
		{
			Rifornimento primo = findFirst( soc.getMezzo() );
			
			if ( primo != null )
			{
				Rifornimento ultimo = findLast( soc.getMezzo() );
				
				if ( ultimo != null )
				{
					soc.setRifInizio( primo );
					soc.setRifFine( ultimo );
					Integer km = ultimo.getChilometraggio() - primo.getChilometraggio();
					BigDecimal media = new BigDecimal( km ).divide( new BigDecimal( soc.getNumero() ), 2, RoundingMode.HALF_UP );
					soc.setMediaChilometri( media );
				}
				else
				{
					addErrorMessage( "Impossibile trovare l'ultimo rifornimento del mese " + month + "/" + year + " per il mezzo " + soc.getMezzo().getTarga() );
				}	
			}
			else
			{
				addErrorMessage( "Impossibile trovare il primo rifornimento del mese " + month + "/" + year + " per il mezzo " + soc.getMezzo().getTarga() );
			}
		}
		else
		{
			addInfoMessage( "Il mezzo " + soc.getMezzo().getTarga() + " ha 0 soccorsi" );
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	private Rifornimento findFirst( Mezzo mezzo )
	{
		Date data = DateUtils.toMorning( DateUtils.create( year, month-1, 1 ) );
		
		Query query = getEntityManager().createQuery(
				"FROM " + Rifornimento.class.getName() + " rif " +
				"WHERE rif.data >= :data " +
				"AND   rif.mezzo = :mezzo " +
				"ORDER BY rif.data ASC ")
			.setParameter( "data", data )
			.setParameter( "mezzo", mezzo );
		query.setMaxResults( 1 );
		
		List<Rifornimento> rif = query.getResultList();
		
		return ( rif.size() == 1 ? rif.get(0) : null );
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	private Rifornimento findLast( Mezzo mezzo )
	{
		Date data = DateUtils.toMidnight( DateUtils.lastDayOfMoth( DateUtils.create( year, month-1, 1 ) ) );
		
		Query query = getEntityManager().createQuery(
				"FROM " + Rifornimento.class.getName() + " rif " +
				"WHERE rif.data <= :data " +
				"AND   rif.mezzo = :mezzo " +
				"ORDER BY rif.data DESC ")
			.setParameter( "data", data )
			.setParameter( "mezzo", mezzo );
		query.setMaxResults( 1 );
		
		List<Rifornimento> rif = query.getResultList();
		
		return ( rif.size() == 1 ? rif.get(0) : null );
	}
	
	
	//-------------------------------------------------------------------------------------
	
	@Override
	protected List<JobParameter> getState( ) 
	{
		List<JobParameter> params =super.getState();
		params.add( new JobParameter( "month",  "Mese", month ) );
		params.add( new JobParameter( "year",  "Anno", year ) );
		return params;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected void setState(HashMap<String, String> params)
	{
		super.setState( params );
		this.month = Integer.parseInt( params.get( "month" ) );
		this.year  = Integer.parseInt( params.get( "year" ) );
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	public boolean isRepeatable() 
	{
		return true;
	}
}
