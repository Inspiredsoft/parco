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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.report.RiepilogoRifornimentiMezzo;
import it.redev.parco.model.report.RiepilogoRifornimentiMezzoPostazione;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.inspired.utils.DateUtils;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class RifornimentiReport extends ExcelExportableModelEntityQuery<Rifornimento>
{
	@Inject
	LoggedUser loggedUser;
	
	private RiepilogoRifornimentiMezzoPostazione 	selectedRiepilogoRifornimentiMezzoPostazione;
	private RiepilogoRifornimentiMezzo				selectedRiepilogoRifornimentiMezzo;
	
	//--------------------------------------------------------------------------------
	
	private static final String[] RESTRICTIONS = 
	{
		"rif.mezzo = #{rifornimentiReport.mezzo}",
		"rif.data >= #{rifornimentiReport.minDate}",
		"rif.data <= #{rifornimentiReport.maxDate}",
		"ass.postazione IN #{rifornimentiReport.postazioni}"
	};
	
	//--------------------------------------------------------------------------------
	
	public void clear()
	{
		this.selectedRiepilogoRifornimentiMezzoPostazione = null;
		this.selectedRiepilogoRifornimentiMezzo = null;
	}
	
	public boolean isSelected()
	{
		return this.selectedRiepilogoRifornimentiMezzoPostazione != null ||
				this.selectedRiepilogoRifornimentiMezzo != null;
	}
	
	//--------------------------------------------------------------------------------
	
	public Date getMinDate()
	{
		Date date = DateUtils.create( 
					getAnno(), 
					getMese()-1, 1 );
		return DateUtils.toMorning( date );
	}
	
	//--------------------------------------------------------------------------------
	
	public Date getMaxDate()
	{
		Date date = DateUtils.lastDayOfMoth( DateUtils.create( 
					getAnno(), 
					getMese()-1, 1 ) );
		return DateUtils.toMidnight( date );
	}
	
	//--------------------------------------------------------------------------------
	
	public List<Postazione> getPostazioni()
	{
		if ( selectedRiepilogoRifornimentiMezzoPostazione != null )
		{
			List<Postazione> postazioni = new ArrayList<Postazione>();
			postazioni.add( selectedRiepilogoRifornimentiMezzoPostazione.getPostazione() );
			return postazioni;
		}
		else
		{
			return null;
			//return loggedUser.getVisiblePostazioni();
		}	
	}
	
	public Mezzo getMezzo()
	{
		if ( selectedRiepilogoRifornimentiMezzoPostazione != null )
		{
			return selectedRiepilogoRifornimentiMezzoPostazione.getMezzo();
		}
		else
		{
			return selectedRiepilogoRifornimentiMezzo.getMezzo();
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public Integer getMese()
	{
		if ( selectedRiepilogoRifornimentiMezzoPostazione != null )
		{
			return selectedRiepilogoRifornimentiMezzoPostazione.getMese();
		}
		else
		{
			return selectedRiepilogoRifornimentiMezzo.getMese();
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public Integer getAnno()
	{
		if ( selectedRiepilogoRifornimentiMezzoPostazione != null )
		{
			return selectedRiepilogoRifornimentiMezzoPostazione.getAnno();
		}
		else
		{
			return selectedRiepilogoRifornimentiMezzo.getAnno();
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public RiepilogoRifornimentiMezzoPostazione getSelectedRiepilogoRifornimentiMezzoPostazione() {
		return selectedRiepilogoRifornimentiMezzoPostazione;
	}
	
	public void setSelectedRiepilogoRifornimentiMezzoPostazione(
			RiepilogoRifornimentiMezzoPostazione selectedRiepilogoRifornimentiMezzoPostazione) {
		this.selectedRiepilogoRifornimentiMezzoPostazione = selectedRiepilogoRifornimentiMezzoPostazione;
	}
	
	public RiepilogoRifornimentiMezzo getSelectedRiepilogoRifornimentiMezzo() {
		return selectedRiepilogoRifornimentiMezzo;
	}
	
	public void setSelectedRiepilogoRifornimentiMezzo(
			RiepilogoRifornimentiMezzo selectedRiepilogoRifornimentiMezzo) {
		this.selectedRiepilogoRifornimentiMezzo = selectedRiepilogoRifornimentiMezzo;
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
		String query = 
				"SELECT rif " +
				"FROM Rifornimento rif, AssegnazioneMezzo ass " +
				"WHERE rif.mezzo = ass.mezzo " +
				"AND rif.data >= ass.dataInizio " +
				"AND (ass.dataFine IS NULL OR rif.data<=ass.dataFine) ";
		
		/*
		if ( !loggedUser.hasViewAll() )
		{	
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " AND rif.id = -1";
			}
			else
			{
				query += " AND rif.mezzo IN #{loggedUser.visibleMezzi}";
			}
		}
		*/
		return query;
	}
	
	@Override
	public String getOrderColumn() {
		return "rif.data";
	}
	
}
