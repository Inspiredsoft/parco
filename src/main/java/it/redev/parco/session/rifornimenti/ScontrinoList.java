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

package it.redev.parco.session.rifornimenti;

import it.inspired.utils.DateUtils;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.rifornimenti.Scontrino;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.session.Period;

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
public class ScontrinoList extends ExcelExportableModelEntityQuery<Scontrino>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String 	targa;
	private String 	carta;
	private String 	codiceBusta;
	private String 	codiceOperatore;
	private Integer gestore;
	private Integer stato;
	private Period	period = Period.currentMonthPeriod();
	private Postazione	postazione;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(scon.rifornimento.mezzo.targa) like concat('%', lower(#{scontrinoList.targa}),'%')",
		"lower(scon.rifornimento.carta.numero) like concat('%', lower(#{scontrinoList.carta}),'%')",
		"scon.rifornimento.gestore = #{scontrinoList.gestore}",
		"scon.rifornimento.data >= #{scontrinoList.period.morningPeriodFrom}",
		"scon.rifornimento.data <= #{scontrinoList.period.midnighthPeriodTo}",
		"scon.stato.stato = #{scontrinoList.stato}",
		//"scon.rifornimento.mezzo IN #{scontrinoList.mezziPostazione}"
		"ass.postazione = #{scontrinoList.postazione}",
		"lower(scon.rifornimento.pinCard.codiceOperatore) like concat('%', lower(#{scontrinoList.codiceOperatore}),'%')",
		"lower(scon.rifornimento.pinCard.codiceBusta) like concat('%', lower(#{scontrinoList.codiceBusta}),'%')",
	};
	
	//--------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "Export_Scontrini_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}
	
	//--------------------------------------------------------------------------------

	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
	}
	
	public List<Integer> getStati()
	{
		return Arrays.asList( Scontrino.Stato.list );
	}

	//--------------------------------------------------------------------------------

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}
	
	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//--------------------------------------------------------------------------------
	
	public Postazione getPostazione() {
		return postazione;
	}
	
	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}
	
	public List<Mezzo> getMezziPostazione() {
		return ( postazione != null ? as.findMezzi( postazione, period.getMorningPeriodFrom(), period.getMidnighthPeriodTo() ) : null );
	}
	
	//--------------------------------------------------------------------------------

	public String getCarta() {
		return carta;
	}

	public void setCarta(String carta) {
		this.carta = carta;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getCodiceBusta() {
		return codiceBusta;
	}

	public void setCodiceBusta(String codiceBusta) {
		this.codiceBusta = codiceBusta;
	}
	
	//--------------------------------------------------------------------------------

	public String getCodiceOperatore() {
		return codiceOperatore;
	}

	public void setCodiceOperatore(String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
	}
	
	//--------------------------------------------------------------------------------
	
	public Integer getGestore() {
		return gestore;
	}

	public void setGestore(Integer gestore) {
		this.gestore = gestore;
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
	
	@Override
	public String getOrderColumn() {
		return "scon.rifornimento.data";
	}
	
	@Override
	public String getOrderDirection() {
		return DIR_ASC;
	}
		
	//--------------------------------------------------------------------------------	

	@Override
	public String getEjbql() 
	{
//		String query = "FROM Scontrino scon";
//		
//		if ( !loggedUser.hasViewAll() )
//		{	
//			
//			query = "SELECT scon " +
//					"FROM Scontrino scon, AssegnazioneMezzo ass " +
//					"WHERE scon.rifornimento.mezzo = ass.mezzo " +
//					"AND scon.rifornimento.data >= ass.dataInizio " +
//					"AND (ass.dataFine IS NULL OR scon.rifornimento.data<=ass.dataFine) ";
//			
//			if ( loggedUser.getVisibleMezzi().isEmpty() )
//			{
//				query += " WHERE scon.id = -1";
//			}
//			else
//			{
//				query += " AND ass.postazione IN #{loggedUser.visiblePostazioni}";
//			}
//		}
		
		String query = "FROM Scontrino scon";
		if ( postazione != null || !loggedUser.hasViewAll()  ) {
			query = "SELECT scon " +
					"FROM Scontrino scon, AssegnazioneMezzo ass " +
					"WHERE scon.rifornimento.mezzo = ass.mezzo " +
					"AND scon.rifornimento.data >= ass.dataInizio " +
					"AND (ass.dataFine IS NULL OR scon.rifornimento.data<=ass.dataFine) ";
		}
		
		if ( !loggedUser.hasViewAll() )
		{
			if ( loggedUser.getVisiblePostazioni().isEmpty() )
			{
				query += " WHERE scon.id = -1";
			}
			else				
			{
				query += " AND ass.postazione IN #{loggedUser.visiblePostazioni}";
			}
		}
		return query;
	}
	
	@Override
	protected boolean isAnyParameterDirty() 
	{
		super.setEjbql( this.getEjbql() );
		return super.isAnyParameterDirty();
	}
}
