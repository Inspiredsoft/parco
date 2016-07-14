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

package it.redev.parco.session.assicurazione;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Polizza;

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
public class PolizzaList extends LazyModelEntityQuery<Polizza>
{
	@Inject
	LoggedUser loggedUser;
	
	private String 	numero;
	private String 	targa;
	private Mezzo	mezzo;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(p.numero) like concat('%', lower(#{polizzaList.numero}),'%')",
		"p.mezzo = #{polizzaList.mezzo}",
		"lower(p.mezzo.targa) like concat('%', lower(#{polizzaList.targa}),'%')"
	};

	//--------------------------------------------------------------------------------

	public String getTarga() {
		return targa;
	}
	
	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//--------------------------------------------------------------------------------

	public String getNumero() {
		return numero;
	}


	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	//--------------------------------------------------------------------------------

	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
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
		String query = "FROM Polizza p";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query += " WHERE p.mezzo.removed = false";
			
			if ( loggedUser.getVisibleMezzi().isEmpty() )
			{
				query += " AND p.id = -1";
			}
			else
			{
				query += " AND p.mezzo IN #{loggedUser.visibleMezzi}";
			}
		}
		
		return query;
	}
}
