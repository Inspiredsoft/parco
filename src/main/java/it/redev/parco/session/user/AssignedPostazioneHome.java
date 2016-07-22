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

package it.redev.parco.session.user;

import it.redev.parco.comparators.PostazioneComparator;
import it.redev.parco.core.security.AllowedEntities;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.oc.Postazione;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@Stateful
@RequestScoped
public class AssignedPostazioneHome extends AssignedHome 
{
	@Inject
	Logger log;
	
	@Inject
	LoggedUser loggedUser;
	
	
	private List<Postazione> assignedPostazione 	= new ArrayList<Postazione>();
	private List<Postazione> assignablePostazione	= new ArrayList<Postazione>();
	private List<Postazione> hiddenPostazione		= new ArrayList<Postazione>();
	
	//--------------------------------------------------------------------------------
	
	@PostConstruct
	public void init()
	{
			
		assignablePostazione.addAll( PostazioneComparator.sort( loggedUser.getAllowedPostazioni() ) );
		
		assignedPostazione 	= AllowedEntities.load( findAssignedEnty() ).getAssignedPostazioni();
		
		hiddenPostazione 	= new ArrayList<Postazione>();
		
		for ( Postazione post : assignedPostazione )
		{
			if ( !assignablePostazione.contains( post ) )
			{
				hiddenPostazione.add( post );
			}
		}
		
		log.debugv( "{0} has {1} assigable, {2} already assigned and {3} hiddent.", 
				getSubject(), assignablePostazione.size(), assignedPostazione.size(), hiddenPostazione.size() );
	}

	//--------------------------------------------------------------------------------

	public List<Postazione> getAssignedPostazione()
	{
		return assignedPostazione;
	}

	public void setAssignedPostazione(List<Postazione> posts)
	{
		this.assignedPostazione = posts;
	}
	
	//--------------------------------------------------------------------------------
	
	public List<Postazione> getAssignablePostazione()
	{
		return assignablePostazione;
	}

	public void setAssignablePostazione(List<Postazione> posts)
	{
		this.assignablePostazione = posts;
	}

	//--------------------------------------------------------------------------------
	
	public void save()
	{
		for ( Postazione area : assignedPostazione )
		{
			save( area );
		}
		
		for ( Postazione area : hiddenPostazione )
		{
			save( area );
		}
	}
	
}
