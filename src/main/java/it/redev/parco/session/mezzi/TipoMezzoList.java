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

import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.mezzi.TipoMezzo;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class TipoMezzoList extends LazyModelEntityQuery<TipoMezzo>
{
	private String descrizione;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(tm.descrizione) like concat('%', lower(#{tipoMezzoList.descrizione}),'%')"
	};

	//--------------------------------------------------------------------------------

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String nome) {
		this.descrizione = nome;
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
		String query = "FROM TipoMezzo tm";
		return query;
	}
}
