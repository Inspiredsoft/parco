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
import it.redev.parco.ext.ModelHome;
import it.redev.parco.model.Group;
import it.redev.parco.service.GroupService;
import it.redev.parco.session.Managed;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class GroupHome extends ModelHome<Group>
{
	@Inject
	MessagesManager bundleManager;
	
	@Inject
	GroupService gs;
	
	@Inject
	Logger log;
	
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Managed
	@RequestScoped
	Group getManagedGroup()
	{
		return getInstance();
	}
	
}
