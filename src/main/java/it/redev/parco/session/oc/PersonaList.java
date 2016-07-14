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

package it.redev.parco.session.oc;

import it.redev.parco.comparators.PostazioneComparator;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.OcService;

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
public class PersonaList extends LazyModelEntityQuery<Persona>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	OcService oc;
	
	private String 	matricola;
	private String 	cognome;
	private Postazione postazione;
	private boolean	showRemoved = false;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(p.cognome) like concat('%', lower(#{personaList.cognome}),'%')",
		"lower(p.matricola) like concat('%', lower(#{personaList.matricola}),'%')",
		"p.postazione = #{personaList.postazione}"
	};

	//--------------------------------------------------------------------------------

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}
	
	//--------------------------------------------------------------------------------

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	//--------------------------------------------------------------------------------

	public Postazione getPostazione() {
		return postazione;
	}

	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}
	
	public List<Postazione> getPostazioni()
	{
		return PostazioneComparator.sort( oc.findAllPostazioni() );
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
		String query = "FROM Persona p";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query += " WHERE p.removed = false";
		}
		else
		{
			query += " WHERE ( #{personaList.showRemoved} = true OR p.removed = false )";
		}
		
		return query;
	}
}
