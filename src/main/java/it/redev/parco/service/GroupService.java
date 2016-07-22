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
import it.redev.parco.model.UsersGroups;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class GroupService extends BaseService{

	@SuppressWarnings("unchecked")
	public List<Group> findAll()
	{
		Query query = em.createQuery("FROM Group");
		return query.getResultList();
	}
	
	public Group findByName( String name )
	{
		Query query = em.createQuery("FROM Group Where name = :name")
						.setParameter( "name", name );
		return (Group) super.getSingleResult(query);
	}
	
	//-------------------------------------------------------------------------------
	
	public Group findById( Integer id )
	{
		return em.find( Group.class, id );
	}
	
	@SuppressWarnings("unchecked")
	public List<Group> findAssignedGroupsByUserId( User user ){
		
		Query query = em.createQuery( 
				"SELECT ug " +
				"FROM UsersGroups ug " +
				"WHERE ug.user = :user " );
		
		query.setParameter( "user", user );
		
		List<UsersGroups> userGroupList =  query.getResultList();
		
		List<Group> assignedGroupList = new ArrayList<Group>();
		
		for (UsersGroups ug : userGroupList) {
			assignedGroupList.add(ug.getGroup());
		}
		
		return assignedGroupList;
	}

	@SuppressWarnings("unchecked")
	public List<Group> findAssignableGroupsByUserId(User user) {
		
		Query query = em.createQuery( 
				"SELECT g " +
				"FROM Group g " +
				"WHERE g.id NOT IN (SELECT ug.id FROM UsersGroups ug WHERE ug.user = :user )" );
		query.setParameter( "user", user );
		
		return (List<Group>)  query.getResultList();
		
	}

	public void addGroupToUser(User user, Group group) {
			
		UsersGroups ug = new UsersGroups();
		ug.setGroup(group);
		ug.setUser(user);
		save(ug);
		
	}
	
	//-------------------------------------------------------------------------------
	public void removeAllGroupByUser( User user )
	{
		Query query = em.createQuery( 
				"DELETE FROM UsersGroups ug " +
				"WHERE ug.user = :user ");
		
		query.setParameter( "user", user );
		
		query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------
	
	public void removeAssignedEntity( Group group )
	{
		Query query = em.createQuery( 
				"DELETE FROM AssignedEntity ae " +
				"WHERE ae.group = :group ");
		
		query.setParameter( "group", group );
		
		query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------
	
	public void assign( Group group, Provincia prov )
	{
		AssignedEntity ae = new AssignedEntity();
		ae.setGroup( group );
		ae.setProvincia( prov );
		save( ae );
	}
	
	//-------------------------------------------------------------------------------
	
	public void assign( Group group, Area area )
	{
		AssignedEntity ae = new AssignedEntity();
		ae.setGroup( group );
		ae.setArea( area );
		save( ae );
	}
	
	//-------------------------------------------------------------------------------
	
	public void assign( Group group, Postazione post )
	{
		AssignedEntity ae = new AssignedEntity();
		ae.setGroup( group );
		ae.setPostazione( post );
		save( ae );
	}
	
	//-------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<AssignedEntity> findAssignedEnty( Group group )
	{
		Query query = em.createQuery( 
				"SELECT ae " +
				"FROM AssignedEntity ae " +
				"WHERE ae.group = :group" );
		
		query.setParameter( "group", group );
		
		return (List<AssignedEntity>) query.getResultList();
	}
}
