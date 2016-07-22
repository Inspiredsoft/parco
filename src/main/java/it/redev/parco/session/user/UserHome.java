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
import it.redev.parco.model.User;
import it.redev.parco.service.GroupService;
import it.redev.parco.session.Managed;
import it.inspired.utils.StringUtils;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class UserHome extends BaseUserHome
{	
	@Inject
	Logger log;
	
	@Inject
	MessagesManager message;
	
	@Inject
	GroupService gs;
	
	private String confirm;

	//----------------------------------------------------------------------------------
	
	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
		
	//----------------------------------------------------------------------------------
	
	@Override
	public String save() 
	{
		if ( !super.isManaged() )
		{
			getInstance().setPassword( StringUtils.scramble( getInstance().getPassword() ) );
		}
		return super.save();
	}
	
	//----------------------------------------------------------------------------------
	
	@Override
	public String undelete() 
	{
		User user = us.findNotRemovedByUsername( getInstance().getUsername() );
		
		if(  user != null && user.getId() != getInstance().getId() )
		{
			log.debugv( "Username already in user: {0}", getInstance().getUsername() );
			message.error( "validator.user.undelete" );
			return "error";
		}
		return super.undelete();
	}
	
	//----------------------------------------------------------------------------------
	
	public String savePwd()
	{
		log.debugv( "Changing password for {0}", getInstance().getUsername() );
		if ( super.isManaged() )
		{
			getInstance().setPassword( StringUtils.scramble( getInstance().getPassword() ) );
		}
		return super.save();
	}
	
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Managed
	@RequestScoped
	User getManagedUser()
	{
		return getInstance();
	}
	
}
