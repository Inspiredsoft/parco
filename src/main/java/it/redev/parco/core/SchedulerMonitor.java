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

package it.redev.parco.core;

import java.util.Date;
import java.util.HashMap;

public class SchedulerMonitor 
{
	private class MonitoredItem
	{
		public Date startDate;
		public Date lastExecution;
		public long executions;
		public boolean active;
	}
	
	@SuppressWarnings("rawtypes")
	private HashMap<Class, MonitoredItem> items = new HashMap<Class, MonitoredItem>();
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	private MonitoredItem get( Class clazz )
	{
		MonitoredItem mi = items.get( clazz );
		if ( mi == null )
		{
			mi = new MonitoredItem();
			mi.startDate = new Date();
			mi.executions = 0;
			mi.active = false;
			items.put( clazz, mi );
		}
		return mi;
	}
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public synchronized void started(Class clazz)
	{
		get( clazz );
	}
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public synchronized void executed(Class clazz)
	{
		MonitoredItem mi = get( clazz );
		mi.lastExecution = new Date();
		mi.executions++;
	}
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public synchronized Date getStartDate( Class clazz )
	{
		return get( clazz ).startDate;
	}
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public synchronized Date getLastExecution( Class clazz )
	{
		return get( clazz ).lastExecution;
	}
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public synchronized long getExecutions( Class clazz )
	{
		return items.get( clazz ).executions;
	}
	
	//-------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public synchronized boolean isActive( Class clazz )
	{
		return items.get( clazz ).active;
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void activate( Class clazz )
	{
		items.get( clazz ).active = true;
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void deactivate( Class clazz )
	{
		items.get( clazz ).active = false;
	}
	
	
}
