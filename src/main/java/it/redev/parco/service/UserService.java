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

package it.redev.parco.service;

import it.redev.parco.model.AssignedEntity;
import it.redev.parco.model.Group;
import it.redev.parco.model.User;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class UserService extends BaseService
{
	@Inject
	PermissionService ps;
	
	public User findByUsername( String username )
	{
		Query query = em.createQuery("FROM User Where username = :username")
						.setParameter( "username", username );
		return (User) super.getSingleResult(query);
	}
	
	//-------------------------------------------------------------------------------
	
	public User findNotRemovedByUsername( String username )
	{
		Query query = em.createQuery("FROM User " +
									 "WHERE username = :username " +
									 "AND removed = false")
						.setParameter( "username", username );
		return (User) super.getSingleResult(query);
	}
	
	//-------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<User> findByPrefix( String prefix )
	{
		Query query = em.createQuery(
					"FROM User " +
					"WHERE username LIKE :prefix " +
					"OR name LIKE :prefix " +
					"OR surname LIKE :prefix " +
					"OR email LIKE :prefix")
				.setParameter( "prefix", "%" + prefix + "%" );
		return query.getResultList();
	}
	
	//-------------------------------------------------------------------------------
	
	public User findById( Integer id )
	{
		return em.find( User.class, id );
	}
	
	//-------------------------------------------------------------------------------
	
	public User findSystemUser()
	{
		return findByUsername( "system" );
	}
	
	//-------------------------------------------------------------------------------
	
	public void removeAssignedEntity( User user )
	{
		Query query = em.createQuery( 
				"DELETE FROM AssignedEntity ae " +
				"WHERE ae.user = :user ");
		
		query.setParameter( "user", user );
		
		query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------
	
	public void assign( User user, Provincia provincia )
	{
		AssignedEntity ae = new AssignedEntity();
		ae.setUser( user );
		ae.setProvincia( provincia );
		save( ae );
	}

	//-------------------------------------------------------------------------------
	
	public void assign( User user, Area area )
	{
		AssignedEntity ae = new AssignedEntity();
		ae.setUser( user );
		ae.setArea( area );
		save( ae );
	}
	
	//-------------------------------------------------------------------------------
	
	public void assign( User user, Postazione postazione )
	{
		AssignedEntity ae = new AssignedEntity();
		ae.setUser( user );
		ae.setPostazione( postazione );
		save( ae );
	}
	//-------------------------------------------------------------------------------
	/*
	public void unassign( User user, Safe safe )
	{
		Query query =getEntityManager().createQuery( 
				"DELETE FROM AssignedEntity ae " +
				"WHERE ae.user = :user " +
				"AND   ae.safe = :safe " )
				.setParameter( "user", user )
				.setParameter( "safe", safe );

		query.executeUpdate();
	}
	*/
	//-------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<AssignedEntity> findAssignedEnty( Group group )
	{
		Query query = em.createQuery( 
				"SELECT ae " +
				"FROM AssignedEntity ae " +
				"WHERE ae.group = :group " );
		
		query.setParameter( "group", group );
		
		return (List<AssignedEntity>) query.getResultList();
	}
	
	//-------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<AssignedEntity> findAssignedEnty( User user )
	{
		Query query = em.createQuery( 
				"SELECT ae " +
				"FROM AssignedEntity ae " +
				"WHERE ae.user = :user " );
		
		query.setParameter( "user", user );
		
		return (List<AssignedEntity>) query.getResultList();
	}
		
	//-------------------------------------------------------------------------------
	/*
	@SuppressWarnings("unchecked")
	public List<User> findAllowedUsers( Safe safe )
	{
		List<User> users = new ArrayList<User>();
		
		SalePoint		point		= safe.getSalePoint();
		SalePointGroup 	group 		= (  point != null ? point.getGroup() : null );
		Customer 		customer 	= ( point != null ? point.getCustomer() : null );
		
		Query query = em.createQuery( 
				"SELECT ae.user " +
				"FROM AssignedEntity ae " +
				"WHERE ae.safe = :safe " +
				( point != null 	? "OR ae.salePoint = :salePoint " : "" ) +
				( group != null 	? "OR ae.salePointGroup = :salePointGroup " : "" ) +
				( customer != null 	? "OR ae.customer = :customer " : "" ) ); 
		
		query.setParameter( "safe", 			safe );
		if ( point != null )
		{
			query.setParameter( "salePoint", point );
		}
		if ( group != null ) 
		{
			query.setParameter( "salePointGroup", group );
		}
		if ( customer != null )
		{
			query.setParameter( "customer",  customer );
		}
		
		users.addAll( query.getResultList() );
		
		query = em.createQuery( 
				"SELECT ug.user " +
				"FROM AssignedEntity ae, UsersGroups ug " +
				"WHERE ae.group = ug.group " +
				"AND ( ae.safe = :safe " +
				( point != null 	? "OR ae.salePoint = :salePoint " : "" ) +
				( group != null 	? "OR ae.salePointGroup = :salePointGroup " : "" ) +
				( customer != null 	? "OR ae.customer = :customer " : "" ) + ")" ); 
				
		query.setParameter( "safe", 			safe );
		if ( point != null )
		{
			query.setParameter( "salePoint", point );
		}
		if ( group != null ) 
		{
			query.setParameter( "salePointGroup", group );
		}
		if ( customer != null )
		{
			query.setParameter( "customer", customer );
		}
		
		users.addAll( query.getResultList() );
		
		users.addAll( ps.findViewAllUsers() );
		
		return users;
	}
	*/
}
