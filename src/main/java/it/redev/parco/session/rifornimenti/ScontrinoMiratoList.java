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

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.rifornimenti.Scontrino;
import it.inspired.utils.DateUtils;

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
public class ScontrinoMiratoList extends LazyModelEntityQuery<Scontrino>
{
	@Inject
	LoggedUser loggedUser;
	
	
	private String 	targa;
	private String 	scontrino;
	private Date 	data		= new Date();
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(scon.rifornimento.mezzo.targa) = lower(#{scontrinoMiratoList.targa})",
		"lower(scon.rifornimento.numeroScontrino) = #{scontrinoMiratoList.scontrino}",
		"scon.rifornimento.data >= #{scontrinoMiratoList.from}",
		"scon.rifornimento.data <= #{scontrinoMiratoList.to}"
	};

	//--------------------------------------------------------------------------------
	
	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//--------------------------------------------------------------------------------

	public String getScontrino() {
		return scontrino;
	}

	public void setScontrino(String carta) {
		this.scontrino = carta;
	}
	
	//--------------------------------------------------------------------------------
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	public Date getFrom()
	{
		return DateUtils.toMorning( data );
	}
	
	public Date getTo()
	{
		return DateUtils.toMidnight( data );
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
		String query = "FROM Scontrino scon ";
		
		if ( !loggedUser.hasViewAll() )
		{	
			if ( !loggedUser.hasPerm("ACTION.SCONTRINO.EDIT") )
			{
				query += " WHERE scon.id = -1";
			}
		}
		
		return query;
	}
}
