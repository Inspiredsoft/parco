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
import it.redev.parco.model.report.RiepilogoRifornimentiMezzoPostazione;
import it.redev.parco.model.report.RiepilogoRifornimentiPostazione;

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
public class PostazioneMezziReport extends ExcelExportableModelEntityQuery<RiepilogoRifornimentiMezzoPostazione>
{
	@Inject
	LoggedUser loggedUser;
	
	private RiepilogoRifornimentiPostazione selectedRiepilogo;
		
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"rip.postazione = #{postazioneMezziReport.selectedRiepilogo.postazione}",
		"rip.mese = #{postazioneMezziReport.selectedRiepilogo.mese}",
		"rip.anno = #{postazioneMezziReport.selectedRiepilogo.anno}"
	};
	
	//--------------------------------------------------------------------------------
	
	public void clear()
	{
		this.selectedRiepilogo = null;
	}
	
	public boolean isSelected()
	{
		return this.selectedRiepilogo != null;
	}
	
	//--------------------------------------------------------------------------------
	
	public RiepilogoRifornimentiPostazione getSelectedRiepilogo() {
		return selectedRiepilogo;
	}

	public void setSelectedRiepilogo(
			RiepilogoRifornimentiPostazione selectedRiepilogo) {
		this.selectedRiepilogo = selectedRiepilogo;
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
		String query = "FROM RiepilogoRifornimentiMezzoPostazione rip";
		
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
