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

package it.redev.parco.service;

import it.redev.parco.model.Event;
import it.redev.parco.model.Group;
import it.redev.parco.model.GroupEvent;
import it.redev.parco.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class EventService extends BaseService 
{
	@Inject
	UserService us;
	
	@SuppressWarnings("unchecked")
	public List<Event> findAll()
	{
		Query query = em.createQuery("FROM " + Event.class.getSimpleName() );
		return query.getResultList();
	}
	
	//-------------------------------------------------------------------------------
	
	public Event findByName( String name )
	{
		Query query = em.createQuery("FROM " + Event.class.getSimpleName() + " WHERE name = :name");
		query.setParameter( "name", name );
		return (Event) super.getSingleResult( query );
	}
	
	//-------------------------------------------------------------------------------
	
	public boolean hasEvent( User user, Event event )
	{
		List<Event> events = findObservedEvent( user );
		return events.contains( event );
	}
	
	//-------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Event> findAssignedEvents( Group group ) 
	{
		Query query = em.createQuery( 
				"SELECT ge.event " +
				"FROM " + GroupEvent.class.getSimpleName() + " ge " +
				"WHERE ge.group = :group " );
		
		query.setParameter( "group", group );
		
		return query.getResultList();
	}
	
	//-------------------------------------------------------------------------------
	
	public void removeAllEventsByGroup(Group group) 
	{
		Query query = em.createQuery( 
				"DELETE FROM " + GroupEvent.class.getSimpleName() + " ge " +
				"WHERE ge.group = :group ");
		
		query.setParameter( "group", group);
		
		query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------
	
	public void addEventToGroup(Group group, Event event) {

		GroupEvent ge = new GroupEvent();
		ge.setEvent( event );
		ge.setGroup( group );
		save(ge);
		
	}
	
	//-------------------------------------------------------------------------------
	
	public List<Event> findObservedEvent( User user )
	{
		List<Event> events =  new ArrayList<Event>();
		for ( Group group : user.getGroups() )
		{
			events.addAll( group.getGroupEvents() );
		}
		return events;
	}
	
	//-------------------------------------------------------------------------------
	// Methods to fine user by event
	//-------------------------------------------------------------------------------
	/*
	public List<User> findObserver( Safe safe, Event event )
	{
		List<User> users = us.findAllowedUsers( safe );
		
		Iterator<User> iter = users.iterator();
		
		while ( iter.hasNext() )
		{
			if ( !hasEvent( iter.next(), event ) )
			{
				iter.remove();
			}
		}
		
		return users;
	}
	*/
	//-------------------------------------------------------------------------------
	/*
	public List<User> findObserver( Safe safe, String name )
	{
		return findObserver( safe, findByName( name ) );
	}
	*/
	//-------------------------------------------------------------------------------
	/*
	public boolean observeCassetteRemoved( User user )
	{
		Event event = findByName( Events.CASSETTE_REMOVED_BYME );
		return hasEvent( user, event );
	}
	*/
	//-------------------------------------------------------------------------------
	/*
	public List<User> findObserverCassetteRemoved( Safe safe )
	{
		return findObserver( safe, Events.CASSETTE_REMOVED_BYANY );
	}
	*/
}
