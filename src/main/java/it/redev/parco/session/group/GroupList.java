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

package it.redev.parco.session.group;

import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.Group;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;


@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class GroupList extends LazyModelEntityQuery<Group>
{
	private String name;
	
	public GroupList()
	{
		super();
	}
	
	//--------------------------------------------------------------------------------
	
	private static final String[] RESTRICTIONS = 
	{
		"lower(g.name) like concat('%', lower(#{groupList.name}),    '%')"
	};
	
	//--------------------------------------------------------------------------------
	
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	

	//--------------------------------------------------------------------------------
	
	@Override
	public List<String> getRestrictionExpressionStrings() 
	{
		return Arrays.asList(RESTRICTIONS);  
	}
	
	@Override
	public String getEjbql() 
	{
		return "FROM Group g";
	}
	
}
