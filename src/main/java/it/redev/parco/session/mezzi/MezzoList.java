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

import it.inspired.utils.DateUtils;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.TipoMezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.service.MezzoService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class MezzoList extends ExcelExportableModelEntityQuery<Mezzo>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	MezzoService ms;
	
	@Inject
	AssegnazioneService as;
	
	private String 		targa;
	private String 		codiceRadio;
	private boolean		showRemoved = false;
	private TipoMezzo	tipo;
	private Integer		stato;
	private Postazione	postazione;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(mezzo.targa) like concat('%', lower(#{mezzoList.targa}),'%')",
		"lower(mezzo.codiceRadio) like concat('%', lower(#{mezzoList.codiceRadio}),'%')",
		"mezzo.tipoMezzo = #{mezzoList.tipo}",
		"mezzo.stato.stato = #{mezzoList.stato}",
		"mezzo IN #{mezzoList.mezziPostazione}"
	};
	
	//--------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "Export_Mezzi_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}
	
	//-----------------------------------------------------------------------------
	
	public List<Mezzo> getMezziPostazione()
	{
		return ( postazione == null ? null : as.findMezzi( postazione ) );
	}
	
	//-----------------------------------------------------------------------------
	
	public List<Integer> getStati()
	{
		return Lists.newArrayList( Mezzo.Stati.list );
	}
		
	//--------------------------------------------------------------------------------

	public String getTarga() {
		return targa;
	}

	public void setTarga(String nome) {
		this.targa = nome;
	}
	
	//--------------------------------------------------------------------------------

	public String getCodiceRadio() {
		return codiceRadio;
	}

	public void setCodiceRadio(String codiceRadio) {
		this.codiceRadio = codiceRadio;
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
	
	public TipoMezzo getTipo() {
		return tipo;
	}

	public void setTipo(TipoMezzo tipo) {
		this.tipo = tipo;
	}
	
	//--------------------------------------------------------------------------------

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}
	
	//--------------------------------------------------------------------------------

	public Postazione getPostazione() {
		return postazione;
	}

	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
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
		String query = "FROM Mezzo mezzo";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query += " WHERE mezzo.removed = false";
			
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " AND mezzo.id = -1";
			}
			else
			{
				query += " AND mezzo IN #{loggedUser.visibleMezzi}";
			}
		}
		else
		{
			query += " WHERE ( #{mezzoList.showRemoved} = true OR mezzo.removed = false )";
		}
		
		return query;
	}
}
