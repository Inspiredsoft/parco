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

package it.redev.parco.session.rifornimenti;

import it.inspired.utils.DateUtils;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.rifornimenti.Rifornimento;
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
public class RifornimentoList extends ExcelExportableModelEntityQuery<Rifornimento>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String order = "rif.data " + DIR_ASC;
	
	private String 	targa;
	private String 	carta;
	private String 	codiceBusta;
	private String 	codiceOperatore;
	private String 	scontrino;
	private Mezzo	mezzo;
	private Integer gestore;
	private Period	period = Period.currentMonthPeriod();
	private Postazione	postazione;
	private CartaCarburante cartaCarburante;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(rif.mezzo.targa) like concat('%', lower(#{rifornimentoList.targa}),'%')",
		"lower(rif.carta.numero) like concat('%', lower(#{rifornimentoList.carta}),'%')",
		"lower(rif.numeroScontrino) like concat('%', lower(#{rifornimentoList.scontrino}),'%')",
		"rif.mezzo = #{rifornimentoList.mezzo}",
		"rif.gestore = #{rifornimentoList.gestore}",
		"rif.data >= #{rifornimentoList.period.morningPeriodFrom}",
		"rif.data <= #{rifornimentoList.period.midnighthPeriodTo}",
		//"rif.mezzo IN #{rifornimentoList.mezziPostazione}",
		"ass.postazione = #{rifornimentoList.postazione}",
		"rif.carta = #{rifornimentoList.cartaCarburante}",
		"lower(rif.pinCard.codiceBusta) like concat('%', lower(#{rifornimentoList.codiceBusta}),'%')",
		"lower(rif.pinCard.codiceOperatore) like concat('%', lower(#{rifornimentoList.codiceOperatore}),'%')",
	};
	
	//--------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "Export_Rifornimenti_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}
	
	//--------------------------------------------------------------------------------
	
	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
	}

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


	public void setTarga(String targa) {
		this.targa = targa;
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

	public CartaCarburante getCartaCarburante() {
		return cartaCarburante;
	}

	public void setCartaCarburante(CartaCarburante cartaCarburante) {
		this.cartaCarburante = cartaCarburante;
	}
	
	//--------------------------------------------------------------------------------

	public String getScontrino() {
		return scontrino;
	}

	public void setScontrino(String scontrino) {
		this.scontrino = scontrino;
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
	
	public void orderByDateDescending() {
		order = "rif.data " + DIR_DESC;
	}
	
	@Override
	public String getOrder() {
		return order;
	}
	
	//--------------------------------------------------------------------------------	

	@Override
	public String getEjbql() 
	{
//		String query = "FROM Rifornimento rif";
//		
//		if ( !loggedUser.hasViewAll() )
//		{	
//			query = 
//					"SELECT rif " +
//					"FROM Rifornimento rif, AssegnazioneMezzo ass " +
//					"WHERE rif.mezzo = ass.mezzo " +
//					"AND rif.data >= ass.dataInizio " +
//					"AND (ass.dataFine IS NULL OR rif.data<=ass.dataFine) ";
//			
//			if ( loggedUser.getVisibleMezzi().isEmpty() )
//			{
//				query += " WHERE rif.id = -1";
//			}
//			else
//			{
//				query += " AND ass.postazione IN #{loggedUser.visiblePostazioni}";
//			}
//		}
		String query = "FROM Rifornimento rif";
		if ( postazione != null || !loggedUser.hasViewAll()  ) {
			query = 
					"SELECT rif " +
					"FROM Rifornimento rif, AssegnazioneMezzo ass " +
					"WHERE rif.mezzo = ass.mezzo " +
					"AND rif.data >= ass.dataInizio " +
					"AND (ass.dataFine IS NULL OR rif.data<=ass.dataFine) ";
		}
		
		if ( !loggedUser.hasViewAll() )
		{
			if ( loggedUser.getVisiblePostazioni().isEmpty() )
			{
				query += " WHERE rif.id = -1";
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
