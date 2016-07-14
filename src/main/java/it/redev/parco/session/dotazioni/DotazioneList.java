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

package it.redev.parco.session.dotazioni;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.asset.GenereAsset;
import it.redev.parco.service.AssetService;

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
public class DotazioneList extends LazyModelEntityQuery<DotazioneMezzo> {

	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssetService as;
	
	private String matricola;
	private Integer tipo;
	private GenereAsset genere;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(d.matricola) like concat('%', lower(#{dotazioneList.matricola}),'%')",
		"d.tipo = #{dotazioneList.tipo}",
		"d.genere = #{dotazioneList.genere}"
	};
	
	//--------------------------------------------------------------------------------
	
	public String getMatricola() {
		return matricola;
	}
	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	public GenereAsset getGenere() {
		return genere;
	}
	public void setGenere(GenereAsset genere) {
		this.genere = genere;
	}
	
	//---------------------------------------------------------------------------------
	
	public List<Integer> getTipi()
	{
		List<Integer> tipi = Lists.newArrayList( Asset.Tipo.MEZZO, Asset.Tipo.POSTAZIONE );
		return tipi;
	}
	
	//--------------------------------------------------------------------------

	public List<GenereAsset> getGeneri()
	{
		return as.findGeneriDotazione( tipo );
	}
	
	//--------------------------------------------------------------------------------
	
	public void changeTipo() {
		setGenere(null);
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
		String query = "FROM DotazioneMezzo d";
		if ( !loggedUser.hasViewAll() )
		{	
			String mezzisql = " d.mezzo.id = -1";
			if ( !loggedUser.getVisibleMezzi().isEmpty() ) {
				mezzisql = " d.mezzo.removed = false AND d.mezzo IN (#{loggedUser.visibleMezzi}) ";
			}
			
			String possql = " d.postazione.id = -1";
			if ( !loggedUser.getVisiblePostazioni().isEmpty() ) {
				possql = " d.postazione IN (#{loggedUser.visiblePostazioni}) ";
			}
			
			query += " WHERE ( d.tipo = " + Asset.Tipo.MEZZO + " AND " + mezzisql + " ) OR " +
					 " ( d.tipo = " + Asset.Tipo.POSTAZIONE + " AND " + possql +" )";
		}
		return query;
	}
	
}
