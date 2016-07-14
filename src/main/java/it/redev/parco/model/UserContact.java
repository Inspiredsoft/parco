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

package it.redev.parco.model;

import org.jboss.seam.mail.core.EmailContact;

public class UserContact implements EmailContact {
	
	private String name;
	private String address;
	private User user;
	
	public UserContact( User user ){
		this.name = user.getFullName();
		this.address = user.getEmail();
		this.user = user;
	}
	
	public UserContact( String address ) {
		this.name="";
		this.address = address;
	}

	@Override
	public String getAddress() {
		return this.address;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public User getUser() {
		return user;
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		if ( user != null )
		{
			str.append( "'" );
			str.append( user.getFullName() );
			str.append( "'" );
			str.append( " <" );
		}
		str.append( address );
		if ( user != null )
		{
			str.append( ">" );
		}
		return str.toString();
	}
}
