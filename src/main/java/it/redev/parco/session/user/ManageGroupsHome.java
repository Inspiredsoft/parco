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

import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.Group;
import it.redev.parco.model.User;
import it.redev.parco.service.GroupService;
import it.redev.parco.session.Managed;

import java.io.Serializable;
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
public class ManageGroupsHome implements Serializable 
{
	@Inject
	MessagesManager messages;
	
	@Inject
	Logger log;
	
	@Inject
	GroupService gs;
	
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	@Managed
	private User user;
	
	private List<Group> assignedGroups;
	private List<Group> assignableGroups;

	
	//--------------------------------------------------------------------------------
	
	@PostConstruct
	public void init()
	{
		
		assignableGroups = gs.findAll(); //.findAssignableGroupsByUserId(user);
				
		assignedGroups = gs.findAssignedGroupsByUserId(user);
		
		
	}

	
	public String save()
	{
		String result = "saved";
		try
		{
			gs.removeAllGroupByUser(user);
		
			for ( Group group : assignedGroups )
			{
				gs.addGroupToUser( user, group );
			}
			
		}
		catch ( Exception ex )
		{
			result = "error";
		}
		return result;
		
	}

	public List<Group> getAssignedGroups() {
		return assignedGroups;
	}

	public void setAssignedGroups(List<Group> assignedGroups) {
		this.assignedGroups = assignedGroups;
	}

	public List<Group> getAssignableGroups() {
		return assignableGroups;
	}

	public void setAssignableGroups(List<Group> assignableGroups) {
		this.assignableGroups = assignableGroups;
	}
	
	
	
}
