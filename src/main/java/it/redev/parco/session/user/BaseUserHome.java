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

package it.redev.parco.session.user;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.model.User;
import it.redev.parco.service.UserService;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
public class BaseUserHome extends ModelHome<User>
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager bundleManager;
	
	@Inject
	protected UserService us;
	
	//----------------------------------------------------------------------------------

	public void validateUsername(FacesContext context, UIComponent component, Object value) throws ValidatorException
	{
		String username = value.toString();
		
		User user = us.findNotRemovedByUsername( username );
		
		if(  user != null && user.getId() != getInstance().getId() )
		{
			log.debugv( "Username already in user: {0}", username );
			String text = bundleManager.getMessage( "validator.userused" );
			FacesMessage msg = new FacesMessage( text );
	        throw new ValidatorException(msg);
		}
	}
}
