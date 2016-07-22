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

package it.redev.parco.session.group;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.Group;
import it.redev.parco.model.Permission;
import it.redev.parco.service.PermissionService;
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
public class ManageGroupPermissionsHome implements Serializable 
{
	@Inject
	MessagesManager messages;
	
	@Inject
	Logger log;
	
	@Inject
	PermissionService ps;
	
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	@Managed
	private Group group;
	
	private List<Permission> assignedPermissions;
	private List<Permission> assignablePermissions;
	
	//--------------------------------------------------------------------------------
	
	@PostConstruct
	public void init()
	{	
		this.assignablePermissions = ps.findAll();	
		this.assignedPermissions = ps.findAssignedPermissions(group);	
	}

	public List<Permission> getAssignedPermissions() {
		return assignedPermissions;
	}

	public void setAssignedPermissions(List<Permission> assignedPermissions) {
		this.assignedPermissions = assignedPermissions;
	}

	public List<Permission> getAssignablePermissions() {
		return assignablePermissions;
	}

	public void setAssignablePermissions(List<Permission> assignablePermissions) {
		this.assignablePermissions = assignablePermissions;
	}
	
	public String save()
	{
		String result = "saved";
		try
		{
			ps.removeAllPermissionsByGroup(group);
		
			for ( Permission permission : assignedPermissions )
			{
				ps.addPermissionToGroup( group, permission );
			}
			
		}
		catch ( Exception ex )
		{
			result = "error";
		}
		return result;	
	}	
}
