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

import it.redev.parco.core.view.ViewChain;
import it.redev.parco.ext.ModelEvent;
import it.redev.parco.ext.annotation.Saved;
import it.redev.parco.model.AssignedEntity;
import it.redev.parco.model.Event;
import it.redev.parco.model.Group;
import it.redev.parco.model.Permission;
import it.redev.parco.model.User;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.service.UserService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.security.Identity;
import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class LoggedUser extends ProfiledUser implements Serializable 
{
	@Inject
	Logger log;

	@Inject
	UserService us;
	
	@Inject
	Identity identity;
	
	@Inject
	ViewChain viewChain;
	
	@Inject
	AssegnazioneService as;
	
	@Inject
    private HttpServletRequest httpRequest; 
	
	private User user;
	
	private AllowedEntities entities;
	
	private List<String> permissions;
	private List<String> events;
	
	private List<Mezzo> mezzi;
	
	@Inject
	@Loggedout
	javax.enterprise.event.Event<User> logoutEvent;
	
	//--------------------------------------------------------------------------
	
	public LoggedUser() {}

	//------------------------------------------------------------------------

	public void onLogin(@Observes @Loggedin User user) 
	{
		log.debug("Utente "+ user.getId() + " ha associato " + user.getGroups().size()+" gruppi");
		for (Group group : user.getGroups()) {
			
			log.debug( "Il gruppo " + group.getName() + " ha associato " + group.getGroupPermissions().size() + " permessi");
			log.debug( "Il gruppo " + group.getName() + " ha associato " + group.getGroupEvents() + " eventi");
			
		}
		
		this.user = user;
	}
	
	public String logout() 
	{
        log.debugv( "Logout user: {0}", getUser().getId());
        viewChain.end();
        identity.logout();
        httpRequest.getSession().invalidate();
        logoutEvent.fire( getUser() );
        return "logout";
    }
	
	//------------------------------------------------------------------------
	
	public User getUser()
	{
		return user;
	}
	
	//------------------------------------------------------------------------
	// Allowed
	//------------------------------------------------------------------------
	
	public List<Provincia> getAllowedProvincie()
	{
		if ( entities == null )
		{
			loadEntities();
		}
		return entities.getAllowedProvince();
	}
	
	//------------------------------------------------------------------------
	
	public List<Area> getAllowedAree()
	{
		if ( entities == null )
		{
			loadEntities();
		}
		return entities.getAllowedAree();
	}
	
	//------------------------------------------------------------------------
	
	public List<Postazione> getAllowedPostazioni()
	{
		if ( entities == null )
		{
			loadEntities();
		}
		return entities.getAllowedPostazioni();
	}
	
	//------------------------------------------------------------------------
	// Visible
	//------------------------------------------------------------------------
	
	public List<Provincia> getVisibleProvincie()
	{
		if ( entities == null )
		{
			loadEntities();
		}
		return entities.getVisibleProvince();
	}
	
	//------------------------------------------------------------------------
	
	public List<Area> getVisibleAree()
	{
		if ( entities == null )
		{
			loadEntities();
		}
		return entities.getVisibleAree();
	}
	
	//------------------------------------------------------------------------
	
	public List<Postazione> getVisiblePostazioni()
	{
		if ( entities == null )
		{
			loadEntities();
		}
		return entities.getVisiblePostazioni();
	}
	
	//------------------------------------------------------------------------
	
	public List<Mezzo> getVisibleMezzi()
	{
		if ( mezzi == null )
		{
			mezzi = as.findMezziVisibili( getVisiblePostazioni() );
		}
		return mezzi;
	}
	
	//------------------------------------------------------------------------
	
	public boolean hasMoreVisiblePostazioni() {
		return getVisiblePostazioni().size() > 1;
	}
	
	//------------------------------------------------------------------------
	// Observers
	//------------------------------------------------------------------------
	
	public void onSaveModel( @Observes @Saved ModelEvent modelEvent )
	{
		if ( 	modelEvent.getObject() instanceof Postazione || 
				modelEvent.getObject() instanceof Area || 
				modelEvent.getObject() instanceof Provincia )
		{
			this.entities = null;
		}
	}
		
	//------------------------------------------------------------------------
	// Private Methods
	//------------------------------------------------------------------------
	
	private void loadEntities()
	{
		log.debugv( "Getting allowed entities for {0}", user.getUsername() );
		
		if ( hasViewAll() )
		{
			entities = new AllowedEntities();
			
			entities.setAllowedAree( as.findAllAree() );
			entities.setVisibleAree( entities.getAllowedAree() );
			
			entities.setAllowedPostazioni( as.findAllPostazioni() );
			entities.setVisiblePostazioni( entities.getAllowedPostazioni() );
			
			entities.setAllowedProvince( as.findAllProvince() );
			entities.setVisibleProvince( entities.getAllowedProvince() );
		}
		else
		{
			List<AssignedEntity> aes = us.findAssignedEnty( user );
			
			for ( Group group : user.getGroups() )
			{
				aes.addAll( us.findAssignedEnty( group ) );
			}
			
			entities = AllowedEntities.load( aes );
		}
		entities.sort();
	}
	
	@Transient
	public List<String> getPermissions() 
	{	
		if ( permissions == null )
		{
			permissions 	=  new ArrayList<String>();
			for (Group g : user.getGroups()) 
			{	
				for (Permission p : g.getGroupPermissions())
				{
					permissions.add(p.getName());
				}
			}
		}
		return permissions;
	}

	public void setPermissions(List<String> permissions) 
	{
		this.permissions = permissions;
	}
	
	@Transient
	public List<String> getEvents() 
	{
		if ( events == null )
		{
			events = new ArrayList<String>();
			
			for (Group g : user.getGroups()) 
			{
				for ( Event event : g.getGroupEvents() )
				{
					events.add( event.getName() );
				}
			}
		}
		return events;
	}
}
