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

package it.redev.parco.session.event;

import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.model.Event;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;


@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class EventList extends LazyModelEntityQuery<Event>
{
	private String name;
	
	public EventList()
	{
		super();
	}
	
	//--------------------------------------------------------------------------------
	
	private static final String[] RESTRICTIONS = 
	{
		"lower(e.name) like concat('%', lower(#{eventList.name}),    '%')",
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
		return "FROM Event e";
	}
}
