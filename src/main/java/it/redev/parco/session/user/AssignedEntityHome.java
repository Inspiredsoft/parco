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

package it.redev.parco.session.user;

import it.redev.parco.core.MessagesManager;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@RequestScoped
public class AssignedEntityHome extends AssignedHome
{
	@Inject
	MessagesManager messages;
	
	@Inject
	AssignedProvinciaHome aph;
	
	@Inject
	AssignedAreaHome aah;
	
	@Inject
	AssignedPostazioneHome apoh;
	
	public String apply()
	{
		if( save() == "saved" )
		{
			return "applied";
		}
		return "error";
	}
	
	public String save()
	{
		String result = "saved";
		try
		{
			remove();
			aph.save();
			aah.save();
			apoh.save();
			/*
			 * INSERIRE ALTRI SAVE
			*/
			messages.info( "message.assigned.save" );
		}
		catch ( Exception ex )
		{
			result = "error";
			messages.error( ex.getMessage() );
		}
		return result;
	}
}
