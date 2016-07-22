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

package it.redev.parco.session.mezzi;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Soccorso;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.session.MonthPeriod;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class SoccorsoList extends LazyModelEntityQuery<Soccorso>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String 		targa;
	private String 		codiceRadio;
	private Mezzo		mezzo;
	private Postazione	postazione;
	
	private MonthPeriod period = new MonthPeriod();
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(soc.mezzo.targa) like concat('%', lower(#{soccorsoList.targa}),'%')",
		"lower(soc.mezzo.codiceRadio) like concat('%', lower(#{soccorsoList.codiceRadio}),'%')",
		"soc.mezzo = #{soccorsoList.mezzo}",
		"soc.mese = #{soccorsoList.period.month}",
		"soc.anno = #{soccorsoList.period.year}",
		"soc.mezzo IN #{soccorsoList.mezziPostazione}"
	};

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

	public String getCodiceRadio() {
		return codiceRadio;
	}

	public void setCodiceRadio(String codiceRadio) {
		this.codiceRadio = codiceRadio;
	}
	
	//--------------------------------------------------------------------------------

	public Mezzo getMezzo() {
		return mezzo;
	}
	
	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}
	
	//--------------------------------------------------------------------------------

	public MonthPeriod getPeriod() {
		return period;
	}
	
	public void setPeriod(MonthPeriod period) {
		this.period = period;
	}
	
	//--------------------------------------------------------------------------------

	@Override
	public List<String> getRestrictionExpressionStrings() 
	{
		return Arrays.asList(RESTRICTIONS);
	}
	
	//--------------------------------------------------------------------------------	

	@Override
	public String getEjbql() 
	{
		String query = "FROM Soccorso soc";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query += " WHERE soc.mezzo.removed = false";
			
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " AND soc.id = -1";
			}
			else
			{
				query += " AND soc.mezzo IN #{loggedUser.visibleMezzi}";
			}
		}
		
		return query;
	}
}
