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

import it.inspired.utils.DateUtils;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.report.RiepilogoRifornimentiMezzo;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.session.MonthPeriod;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class MezziReport extends ExcelExportableModelEntityQuery<RiepilogoRifornimentiMezzo>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	RifornimentiReport rifornimentiReport;
	
	@Inject
	AssegnazioneService as;
	
	private String 		targa;
	private Postazione	postazione;
	
	private MonthPeriod period = MonthPeriod.current();
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(rip.mezzo.targa) like concat('%', lower(#{mezziReport.targa}),'%')",
		"rip.mese = #{mezziReport.period.month}",
		"rip.anno = #{mezziReport.period.year}",
		"rip.mezzo IN #{mezziReport.mezziPostazione}"
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
	public void refresh() {
		rifornimentiReport.clear();
		super.refresh();
	}

	//--------------------------------------------------------------------------------	
	
	@Override
	public String getEjbql() 
	{
		String query = "FROM RiepilogoRifornimentiMezzo rip ";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query = "SELECT rip " +
					"FROM RiepilogoRifornimentiMezzo rip, AssegnazioneMezzo ass " +
					"WHERE rip.mezzo = ass.mezzo " +
					"AND ass.dataInizio <= #{mezziReport.period.midnighthPeriodTo} " +
					"AND (ass.dataFine IS NULL OR ass.dataFine >= #{mezziReport.period.morningPeriodFrom}) ";
			
			query += " AND rip.mezzo.removed = false ";
			
			if ( loggedUser.getVisiblePostazioni().isEmpty() )
			{
				query += " AND rip.id = -1";
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
		return "rip.id";
	}
		
}
