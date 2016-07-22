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

package it.redev.parco.core.security;

import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.jboss.solder.logging.Logger;

public abstract class ProfiledUser 
{
	@Inject
	Logger log;
	
	public abstract List<String> getPermissions();
	public abstract List<String> getEvents();
	
	//--------------------------------------------------------------------------
	
	public static class Permissions
	{
		public static String ALL 							= "*";
		public static String VIEW_ALL 						= "VIEW.ALL";
	}
	
	//--------------------------------------------------------------------------
	
	public boolean hasPerm( String permission )
	{
		return verifyPermission( permission );
	}
	
	//--------------------------------------------------------------------------
	
	public boolean verifyPermission( String permission )
	{
		boolean result = false;
		if ( hasFullAccess() ) 
		{
			result = true;
		} 
		else if ( this.getPermissions().contains(permission) )
		{
			result = true;
		}
		else 
		{
			StringTokenizer st = new StringTokenizer(permission,".");
			String stConcat = "";
			
			while (st.hasMoreTokens()) 
			{	
				if (stConcat.equals("")) 
				{
					stConcat = stConcat + st.nextToken();
				} 
				else 
				{
					stConcat = stConcat + "." + st.nextToken();		
				}
				
				if (this.getPermissions().contains( stConcat + ".*"))
				{
					result = true;
					break;
		        }
		   
			}  
		}
		return result; 
	}
	
	//--------------------------------------------------------------------------
	
	public boolean hasViewAll()
	{
		return hasFullAccess() || hasPerm( Permissions.VIEW_ALL );
	}
	
	//--------------------------------------------------------------------------
	
	public boolean hasFullAccess()
	{
		return this.getPermissions().contains( Permissions.ALL );
	}
}
