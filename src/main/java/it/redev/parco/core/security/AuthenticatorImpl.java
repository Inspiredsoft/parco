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

package it.redev.parco.core.security;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.model.User;
import it.redev.parco.service.UserService;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.jboss.solder.logging.Logger;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class AuthenticatorImpl extends BaseAuthenticator implements Authenticator, Serializable 
{
	@Inject
	Logger log;
	
	@Inject
    private Credentials credentials;
	
	@Inject
	UserService us;
	
	@Inject
	MessagesManager message;
	
	@Inject
	@Loggedin
    Event<User> loginEvent;
    
	@Override
	public void authenticate() 
	{
		User user = us.findNotRemovedByUsername( credentials.getUsername() );
		
		if ( user != null && user.isActive() )
		{
			if ( credentials.getCredential() instanceof PasswordCredential )
			{
				String pwd = ((PasswordCredential) credentials.getCredential()).getValue();
				pwd = StringUtils.scramble( pwd );
				if ( pwd.equals( user.getPassword() ) )
				{
					message.info( "message.welcome" , user.getFullName() , user.getLastAccess() );
					
					setStatus(AuthenticationStatus.SUCCESS);
					setUser( new SimpleUser( user.getSurname() ) );
					user.setLastAccess( new Date() );
					loginEvent.fire( user );
				}
				else
				{
					message.warn( "message.login.failed" );
					setStatus(AuthenticationStatus.FAILURE);
				}
			}
		}
		else
		{
			message.warn( "message.login.failed" );
			setStatus(AuthenticationStatus.FAILURE);
		}
	}
}
