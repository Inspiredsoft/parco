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

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.User;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;


@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class UserList extends LazyModelEntityQuery<User>
{
	@Inject
	LoggedUser loggedUser;

	private String username;
	private String name;
	private String surname;
	private boolean	showRemoved = false;
	
	public UserList()
	{
		super();
	}
	
	//--------------------------------------------------------------------------------
	
	private static final String[] RESTRICTIONS = 
	{
		"lower(user.username) like concat('%', lower(#{userList.username}),'%')",
		"lower(user.surname)  like concat('%', lower(#{userList.surname}), '%')",
		"lower(user.name)     like concat('%', lower(#{userList.name}),    '%')",
	};
	
	//--------------------------------------------------------------------------------
	
	public String getUsername() 
	{
		return username;
	}
	
	public void setUsername(String username) 
	{
		this.username = username;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getSurname()
	{
		return surname;
	}
	
	public void setSurname(String surname) 
	{
		this.surname = surname;
	}
	
	public boolean isShowRemoved() 
	{
		return showRemoved;
	}

	public void setShowRemoved(boolean showRemoved)
	{
		this.showRemoved = showRemoved;
	}
	
	//--------------------------------------------------------------------------------
	
	@Override
	public List<String> getRestrictionExpressionStrings() 
	{
		return Arrays.asList(RESTRICTIONS);  
	}
	
	//--------------------------------------------------------------------------------
	
	@Override
	public String getEjbql() 
	{
		String query = "FROM User user ";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query += " WHERE user.removed = false";
		}
		else
		{
			query += " WHERE ( #{userList.showRemoved} = true OR user.removed = false )";
		}
		
		return query;
	}
	
	@Override
	public void refresh() 
	{
		super.refresh();
	}
	
}
