/*******************************************************************************
* Parco è un applicativo per la gestione dei consumi del parco auto
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

import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;
import it.inspired.utils.DateUtils;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class AssegnazioneService extends BaseService
{
	@SuppressWarnings("unchecked")
	public List<Provincia> findAllProvince()
	{
		Query query = em.createQuery("FROM " + Provincia.class.getSimpleName() );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Area> findAllAree()
	{
		Query query = em.createQuery("FROM " + Area.class.getSimpleName() );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Postazione> findAllPostazioni()
	{
		Query query = em.createQuery("FROM " + Postazione.class.getSimpleName() );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	/**
	 * Restituisce i mezzi visibili alle postazioni date 
	 * considerandoli tali anche se lo sono nel mese precedente
	 * @param postazioni
	 * @return
	 */
	public List<Mezzo> findMezziVisibili( List<Postazione> postazioni )
	{
		Date today = new Date();
		Query query = em.createQuery( 
				"SELECT am.mezzo " +
				"FROM AssegnazioneMezzo am " +
				"WHERE am.dataInizio <= :today " +
				"AND  ( am.dataFine IS NULL OR am.dataFine >= :firstDayPrevMonth ) " +
				"AND am.postazione IN :postazioni ")
				.setParameter( "today", today )
				.setParameter( "firstDayPrevMonth", DateUtils.firstDayOfMoth( DateUtils.addMonth( today, -1 ) ) )
				.setParameter( "postazioni", postazioni );
		
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	/**
	 * Recupera i mezzi visibili alla postazione alla data attuale
	 * @param postazione
	 * @return
	 */
	public List<Mezzo> findMezzi( Postazione postazione )
	{
		Query query = em.createQuery( 
				"SELECT am.mezzo " +
				"FROM AssegnazioneMezzo am " +
				"WHERE am.dataInizio <= :today " +
				"AND  ( am.dataFine IS NULL OR am.dataFine >= :today ) " +
				"AND  am.postazione = :postazione ")
				.setParameter( "today", new Date() )
				.setParameter( "postazione", postazione );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
		@SuppressWarnings("unchecked")
		public List<Mezzo> findMezzi( Postazione postazione, Date from, Date to )
		{
			Query query = em.createQuery( 
					"SELECT am.mezzo " +
					"FROM AssegnazioneMezzo am " +
					"WHERE am.dataInizio <= :from " +
					"AND  ( am.dataFine IS NULL OR am.dataFine >= :to ) " +
					"AND  am.postazione = :postazione ")
					.setParameter( "from", from )
					.setParameter( "to", to )
					.setParameter( "postazione", postazione );
			return query.getResultList();
		}
}
