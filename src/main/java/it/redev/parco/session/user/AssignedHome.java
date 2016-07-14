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

import it.redev.parco.model.AssignedEntity;
import it.redev.parco.model.Eligible;
import it.redev.parco.model.Group;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.User;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;
import it.redev.parco.service.GroupService;
import it.redev.parco.service.UserService;
import it.redev.parco.session.Managed;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class AssignedHome implements Serializable 
{
	@Inject
	UserService us;
	
	@Inject
	GroupService gs;
	
	@Inject
	@Managed
	private User user;
	
	@Inject
	@Managed
	private Group group;
	
	//--------------------------------------------------------------------------------
	
	private boolean managed( Identifiable entity )
	{
		return entity != null &&  entity.getId() != null;
	}
	
	//--------------------------------------------------------------------------------
	
	protected Eligible getSubject()
	{
		if ( managed( user ) )
		{
			return user;
		}
		else 
		if ( managed( group ) )
		{
			return group;
		}
		return null;
	}
	
	//--------------------------------------------------------------------------------
	
	protected  List<AssignedEntity> findAssignedEnty()
	{
		if ( managed( user ) )
		{
			return us.findAssignedEnty( user );
		}
		else if ( managed( group ) )
		{
			return gs.findAssignedEnty( group );
		}
		return null;
	}
	
	//--------------------------------------------------------------------------------
	
	protected void remove()
	{
		if (  managed( user ) )
		{
			us.removeAssignedEntity( user );
		}
		else 
		if ( managed( group ) )
		{
			gs.removeAssignedEntity( group );
		}
	}
	
	//--------------------------------------------------------------------------------
	
	protected void save( Eligible entity )
	{
		if (  managed( user ) )
		{
			if ( entity instanceof Provincia )
			{
				us.assign( user, (Provincia) entity );
			}
			else if ( entity instanceof Area )
			{
				us.assign( user, (Area) entity );
			}
			else if ( entity instanceof Postazione )
			{
				us.assign( user, (Postazione) entity );
			}
		}
		else if ( managed( group ) )
		{
			if ( entity instanceof Provincia )
			{
				gs.assign( group, (Provincia) entity );
			}
			else if ( entity instanceof Area )
			{
				gs.assign( group, (Area) entity );
			}
			else if ( entity instanceof Postazione )
			{
				gs.assign( group, (Postazione) entity );
			}
		}
	}

}
