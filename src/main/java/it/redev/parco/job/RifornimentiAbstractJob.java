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

import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.rifornimenti.Rifornimento;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

public abstract class RifornimentiAbstractJob extends AbstractJob 
{
	private int queryIndex 	= 0;
	private int queryMax	= 10;
			
	//------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected List<Rifornimento> findNext( Date from, Date to )
	{
		Query query = getEntityManager().createQuery(
						"FROM Rifornimento rif " +
						"WHERE rif.data >= :from " +
						"AND   rif.data <= :to ")
					.setParameter( "from", from )
					.setParameter( "to", to );
		
		query.setFirstResult( queryIndex );
		query.setMaxResults( queryMax );
		
		queryIndex += queryMax;
		
		return query.getResultList();
	}
	
	//------------------------------------------------------------------------------------------
	
	protected Rifornimento findPred( Rifornimento rif )
	{
		return findPred( rif.getMezzo(), rif.getData(), null );
	}
	
	//------------------------------------------------------------------------------------------
	
//	protected Rifornimento findPred( Rifornimento rif, Rifornimento exclude )
//	{
//		return findPred( rif.getMezzo(), rif.getData(), exclude );
//	}
	
	//------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Rifornimento findPred( Mezzo mezzo, Date data, Rifornimento exclude )
	{
		Query query = getEntityManager().createQuery(
				"FROM Rifornimento rif " +
				"WHERE rif.mezzo = :mezzo " +	
				"AND   rif.data < :date " +
				( exclude != null ? " AND rif != :exclude " : " " ) +
				"ORDER BY rif.data DESC ")
			.setParameter( "date", data )
			.setParameter( "mezzo", mezzo );
		
		if ( exclude != null )
		{
			query.setParameter( "exclude", exclude );
		}
		
		query.setMaxResults( 1 );
		
		List<Rifornimento> rifs = query.getResultList();
		
		return (rifs.isEmpty() ? null : rifs.get(0) );
	}
	
	//------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Rifornimento findPredNonRettificato( Mezzo mezzo, Date data )
	{
		Query query = getEntityManager().createQuery(
				"FROM Rifornimento rif " +
				"WHERE rif.mezzo = :mezzo " +	
				"AND   rif.data < :date " +
				"AND   rif.scontrino.mezzoRettificato is NULL " +
				"ORDER BY rif.data DESC ")
			.setParameter( "date", data )
			.setParameter( "mezzo", mezzo );
				
		query.setMaxResults( 1 );
		
		List<Rifornimento> rifs = query.getResultList();
		
		return (rifs.isEmpty() ? null : rifs.get(0) );
	}
	
	//------------------------------------------------------------------------------------------
	
	protected Rifornimento findPredPinCard( Rifornimento rif ) {
		return findPredPinCard( rif.getPinCard(), rif.getData(), null );
	}
	
	//------------------------------------------------------------------------------------------
	
	protected Rifornimento findPredPinCard( PinCard pinCard, Date data, Rifornimento exclude ) {
		Query query = getEntityManager().createQuery(
				"FROM Rifornimento rif " +	
				"WHERE rif.pinCard = :pinCard " +
				"AND   rif.data < :date " +
				( exclude != null ? " AND rif != :exclude " : " " ) +
				"ORDER BY rif.data DESC ")
			.setParameter( "date", data )
			.setParameter( "pinCard", pinCard );
		
		if ( exclude != null )
		{
			query.setParameter( "exclude", exclude );
		}
		
		query.setMaxResults( 1 );
		
		@SuppressWarnings("unchecked")
		List<Rifornimento> rifs = query.getResultList();
		
		return (rifs.isEmpty() ? null : rifs.get(0) );
	}
}
