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

import it.redev.parco.model.Group;
import it.redev.parco.model.GroupPermission;
import it.redev.parco.model.Permission;
import it.redev.parco.model.User;
import it.redev.parco.model.UsersGroups;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class PermissionService extends BaseService 
{

	@SuppressWarnings("unchecked")
	public List<Permission> findAll()
	{
		Query query = em.createQuery("FROM " + Permission.class.getSimpleName() );
		return query.getResultList();
	}
	
	//-------------------------------------------------------------------------------
	
	public Permission findByName( String name )
	{
		Query query = em.createQuery("FROM " + Permission.class.getSimpleName() + " WHERE name = :name")
						.setParameter( "name", name );
		return (Permission) super.getSingleResult(query);
	}
	
	//-------------------------------------------------------------------------------
	
	public Permission findById( Integer id )
	{
		return em.find( Permission.class, id );
	}

	//-------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Permission> findAssignedPermissions(Group group)
	{
		Query query = em.createQuery( 
				"SELECT gp " +
				"FROM " + GroupPermission.class.getSimpleName() + " gp " +
				"WHERE gp.group = :group " );
		
		query.setParameter( "group", group );
		
		List<GroupPermission> groupsPermissionsList =  query.getResultList();
		
		List<Permission> assignedPermissionList = new ArrayList<Permission>();
		
		for (GroupPermission gp : groupsPermissionsList) {
			assignedPermissionList.add(gp.getPermission());
		}
		
		
		return assignedPermissionList;
	}

	//-------------------------------------------------------------------------------
	
	public void removeAllPermissionsByGroup(Group group) {
		
		Query query = em.createQuery( 
				"DELETE FROM " + GroupPermission.class.getSimpleName() + " gp " +
				"WHERE gp.group = :group ");
		
		query.setParameter( "group", group);
		
		query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------

	public void addPermissionToGroup(Group group, Permission permission) {

		GroupPermission gp = new GroupPermission();
		gp.setPermission(permission);
		gp.setGroup(group);
		save(gp);
		
	}
	
	//------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<User> findUserByPermission( String permission )
	{
		List<User> users = new ArrayList<User>();
		
		Permission perm = findByName( permission );
		
		Query query = em.createQuery( 
				"SELECT ug.user " +
				"FROM " + GroupPermission.class.getSimpleName() + " gp, " + UsersGroups.class.getSimpleName() + " ug " +
				"WHERE gp.group = ug.group " +
				"AND gp.permission = :perm " );
		
		query.setParameter( "perm", perm);
		
		users.addAll( query.getResultList() );
		
		return users;
	}
	
	//------------------------------------------------------------------------------------
	
	public List<User> findViewAllUsers()
	{
		List<User> users = new ArrayList<User>();
		users.addAll( findUserByPermission( "VIEW.ALL" ) );
		users.addAll( findUserByPermission( "*" ) );
		return users;
	}
}
