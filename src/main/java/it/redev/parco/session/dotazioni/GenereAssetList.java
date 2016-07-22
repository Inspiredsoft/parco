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

package it.redev.parco.session.dotazioni;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.service.MezzoService;

import java.util.Arrays;
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
public class GenereAssetList extends ExcelExportableModelEntityQuery<Mezzo>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	MezzoService ms;
	
	@Inject
	AssegnazioneService as;
	
	private String 		descrizione;
	private Integer		tipo;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(ga.descrizione) like concat('%', lower(#{genereAssetList.descrizione}),'%')",
		"ga.tipo = #{genereAssetList.tipo}"
	};
	
	//-----------------------------------------------------------------------------
	
	public List<Integer> getTipi()
	{
		List<Integer> tipi = Lists.newArrayList( Asset.Tipo.list );
		tipi.remove( Asset.Tipo.FUEL_CARD );
		return tipi;
	}
		
	//--------------------------------------------------------------------------------

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String nome) {
		this.descrizione = nome;
	}
	
	//--------------------------------------------------------------------------------

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer stato) {
		this.tipo = stato;
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
		String query = "FROM GenereAsset ga";
		return query;
	}
}
