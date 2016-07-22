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

package it.redev.parco.session.report;

import java.util.Date;
import java.util.List;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.ScadenzaMezzo;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.session.Period;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class ScadenzeList extends LazyModelEntityQuery<ScadenzaMezzo>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String 		targa;
	private Postazione	postazione;
	
	private Period period = Period.monthPeriod( new Date() );
		
	//--------------------------------------------------------------------------------

	public String getTarga() {
		return targa;
	}

	public void setTarga(String nome) {
		this.targa = nome;
	}
	
	//--------------------------------------------------------------------------------
	
	public Postazione getPostazione() {
		return postazione;
	}
	
	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}
	
	public List<Mezzo> getMezziPostazione() {
		return ( postazione != null ? as.findMezzi( postazione ) : null );
	}

	//--------------------------------------------------------------------------------
	
	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}
	
	//--------------------------------------------------------------------------------	
	
	@Override
	public String getOrderColumn() 
	{
		return "mezzo.id";
	}
	
	@Override
	protected boolean isAnyParameterDirty() 
	{
		super.setEjbql( this.getEjbql() );
		return super.isAnyParameterDirty();
	}

	//--------------------------------------------------------------------------------	
	
	@Override
	public String getEjbql() 
	{
		String query =
				"SELECT new " + ScadenzaMezzo.class.getName() + "('" + period.toString() + "', mezzo, polizza) " +
				"FROM Mezzo mezzo LEFT OUTER JOIN mezzo.polizze polizza  " +
				"WHERE mezzo.removed = false " +
				"AND mezzo.stato.stato IN (" + Mezzo.Stati.IN_SERVIZIO + ", " + Mezzo.Stati.RIPARAZIONE + ") ";
		
		if ( targa != null )
		{
			query += " AND lower(mezzo.targa) like concat('%', lower(#{scadenzeList.targa}),'%')";
		}
		if ( postazione != null )
		{
			query += " AND mezzo IN #{scadenzeList.mezziPostazione}";
		}
		
		if ( !loggedUser.hasViewAll() )
		{	
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " AND mezzo.id = -1";
			}
			else
			{
				query += " AND mezzo IN #{loggedUser.visibleMezzi}";
			}
		}
		
		query += " AND ( (" + queryDate( "mezzo.scadenzaBollo" ) + ") OR (" + queryDate( "mezzo.scadenzaRevisione" ) + ") OR (" + queryDate( "polizza.dataScadenza" ) + ") )";
		
		return query;
	}
	
	private String queryDate( String parameter )
	{
		return parameter + " >= #{scadenzeList.period.morningPeriodFrom}  AND " + parameter + " <= #{scadenzeList.period.midnighthPeriodTo}";
	}
		
}
