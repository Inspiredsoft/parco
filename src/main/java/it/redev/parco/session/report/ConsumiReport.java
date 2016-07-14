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

package it.redev.parco.session.report;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.report.Consumo;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.session.Period;

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
public class ConsumiReport extends ExcelExportableModelEntityQuery<Consumo>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String 		targa;
	private Mezzo 		mezzo;
	private Postazione	postazione;
	
	private Period  period = Period.currentMonthPeriod();
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(cons.mezzo.targa) like concat('%', lower(#{consumiReport.targa}),'%')",
		"cons.data >= #{consumiReport.period.morningPeriodFrom}",
		"cons.data <= #{consumiReport.period.midnighthPeriodTo}",
		"cons.mezzo = #{consumiReport.mezzo}",
		"cons.mezzo IN #{consumiReport.mezziPostazione}"
	};
		
	//--------------------------------------------------------------------------------

	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
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
	
	public String getTarga() {
		return targa;
	}

	public void setTarga(String nome) {
		this.targa = nome;
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
	public List<String> getRestrictionExpressionStrings() 
	{
		return Arrays.asList(RESTRICTIONS);
	}

	//--------------------------------------------------------------------------------	
	
	public void orderByDate()
	{
		super.setOrderColumn( "cons.data" );
		super.setOrderDirection( "desc" );
	}
	
	//--------------------------------------------------------------------------------
	
	@Override
	public String getEjbql() 
	{
		String query = "FROM Consumo cons";
		if ( !loggedUser.hasViewAll() )
		{	
			query = "SELECT cons " +
				"FROM Consumo cons, AssegnazioneMezzo ass " +
				"WHERE cons.mezzo = ass.mezzo " +
				"AND cons.data >= ass.dataInizio " +
				"AND (ass.dataFine IS NULL OR cons.data<=ass.dataFine) ";
			
			query += " AND cons.mezzo.removed = false ";
			
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " AND cons.id = -1";
			}
			else
			{
				query += " AND ass.postazione IN #{loggedUser.visiblePostazioni}";
			}
		}
		
		return query;
	}
	
	@Override
	public String getOrder() {
		return "cons.id";
	}
		
}
