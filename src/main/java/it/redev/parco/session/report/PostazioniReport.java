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

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.report.RiepilogoRifornimentiPostazione;
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
public class PostazioniReport extends ExcelExportableModelEntityQuery<RiepilogoRifornimentiPostazione>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	PostazioneMezziReport pmr;
	
	private Postazione postazione;
	
	private MonthPeriod period = MonthPeriod.current();
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"rip.postazione = #{postazioniReport.postazione}",
		"rip.mese = #{postazioniReport.period.month}",
		"rip.anno = #{postazioniReport.period.year}"
	};
		
	//--------------------------------------------------------------------------------
	
	public Postazione getPostazione() {
		return postazione;
	}
	
	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
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
	public void refresh() {
		pmr.clear();
		super.refresh();
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
		String query = "FROM RiepilogoRifornimentiPostazione rip";
		
		if ( !loggedUser.hasViewAll() )
		{	
			if ( loggedUser.getAllowedPostazioni().isEmpty() )
			{
				query += " WHERE rip.id = -1";
			}
			else
			{
				query += " WHERE rip.postazione IN #{loggedUser.visiblePostazioni}";
			}
		}
		
		return query;
	}
		
}
