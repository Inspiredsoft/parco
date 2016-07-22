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

import it.redev.parco.comparators.AreaComparator;
import it.redev.parco.core.security.AllowedEntities;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.oc.Area;

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
public class AssignedAreaHome extends AssignedHome 
{
	@Inject
	Logger log;
	
	@Inject
	LoggedUser loggedUser;
	
	
	private List<Area> assignedAree 	= new ArrayList<Area>();
	private List<Area> assignableAree	= new ArrayList<Area>();
	private List<Area> hiddenAree		= new ArrayList<Area>();
	
	//--------------------------------------------------------------------------------
	
	@PostConstruct
	public void init()
	{
			
		assignableAree.addAll( AreaComparator.sort( loggedUser.getAllowedAree() ) );
		
		assignedAree 	= AllowedEntities.load( findAssignedEnty() ).getAssignedAree();
		
		hiddenAree 	= new ArrayList<Area>();
		
		for ( Area prov : assignedAree )
		{
			if ( !assignableAree.contains( prov ) )
			{
				hiddenAree.add( prov );
			}
		}
		
		log.debugv( "{0} has {1} assigable, {2} already assigned and {3} hiddent.", 
				getSubject(), assignableAree.size(), assignedAree.size(), hiddenAree.size() );
	}

	//--------------------------------------------------------------------------------

	public List<Area> getAssignedAree()
	{
		return assignedAree;
	}

	public void setAssignedAree(List<Area> aree)
	{
		this.assignedAree = aree;
	}
	
	//--------------------------------------------------------------------------------
	
	public List<Area> getAssignableAree()
	{
		return assignableAree;
	}

	public void setAssignableAree(List<Area> aree)
	{
		this.assignableAree = aree;
	}

	//--------------------------------------------------------------------------------
	
	public void save()
	{
		for ( Area area : assignedAree )
		{
			save( area );
		}
		
		for ( Area area : hiddenAree )
		{
			save( area );
		}
	}
	
}
