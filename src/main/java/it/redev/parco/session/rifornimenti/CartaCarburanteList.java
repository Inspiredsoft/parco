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
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.AssegnazioneService;

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
public class CartaCarburanteList extends ExcelExportableModelEntityQuery<CartaCarburante>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String 	targa;
	private String 	numero;
	private String 	identificativo;
	private Mezzo	mezzo;
	private Integer gestore;
	private boolean	showRemoved = false;
	private Postazione	postazione;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(cc.mezzo.targa) like concat('%', lower(#{cartaCarburanteList.targa}),'%')",
		"lower(cc.identificativo) like concat('%', lower(#{cartaCarburanteList.identificativo}),'%')",
		"lower(cc.numero) like concat('%', lower(#{cartaCarburanteList.numero}),'%')",
		"cc.mezzo = #{cartaCarburanteList.mezzo}",
		"cc.gestore = #{cartaCarburanteList.gestore}",
		"cc.mezzo IN #{cartaCarburanteList.mezziPostazione}"
	};
	
	//--------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "Export_CarteCarburante_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}

	//--------------------------------------------------------------------------------
	
	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
	}
	
	//--------------------------------------------------------------------------------

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
		return ( postazione != null ? as.findMezzi( postazione ) : null );
	}
	
	//--------------------------------------------------------------------------------

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getIdentificativo() {
		return identificativo;
	}

	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}

	//--------------------------------------------------------------------------------

	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}
	
	//--------------------------------------------------------------------------------
	
	public Integer getGestore() {
		return gestore;
	}

	public void setGestore(Integer gestore) {
		this.gestore = gestore;
	}
	
	//--------------------------------------------------------------------------------

	public boolean isShowRemoved() 
	{
		return showRemoved;
	}

	public void setShowRemoved(boolean showRemoved)
	{
		this.showRemoved = showRemoved;
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
		String query = "FROM CartaCarburante cc";
		
		if ( !loggedUser.hasViewAll() )
		{	
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " WHERE cc.id = -1";
			}
			else
			{
				query += " WHERE cc.mezzo IN #{loggedUser.visibleMezzi}";
			}
		}
		else
		{
			query += " WHERE ( #{cartaCarburanteList.showRemoved} = true OR cc.removed = false )";
		}
		
		return query;
	}
}
