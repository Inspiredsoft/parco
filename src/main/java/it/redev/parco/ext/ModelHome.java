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

package it.redev.parco.ext;

import it.redev.parco.core.view.ViewChain;
import it.redev.parco.ext.annotation.Deleted;
import it.redev.parco.ext.annotation.Loaded;
import it.redev.parco.ext.annotation.Persisted;
import it.redev.parco.ext.annotation.Saved;
import it.redev.parco.ext.annotation.Undeleted;
import it.redev.parco.ext.annotation.Updated;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.Removable;
import it.inspired.utils.StringUtils;

import java.util.Date;

import javax.enterprise.context.Conversation;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.http.RequestParam;

import pl.itcrowd.seam3.persistence.EntityHome;

@SuppressWarnings("serial")
public class ModelHome<T> extends EntityHome<T>
{
	@Inject
	Logger log;
	
	@Inject
	Conversation conversation;
	
	@Inject @RequestParam("id")
	private Instance<String> modelIdResolver;
	
	@Inject
	ViewChain viewChain;
	
	@Inject 
	@Saved
	Event<ModelEvent> savedEvent;
	
	@Inject 
	@Persisted
	Event<ModelEvent> persistedEvent;
	
	@Inject 
	@Updated
	Event<ModelEvent> updatedEvent;
	
	@Inject 
	@Deleted
	Event<ModelEvent> deletedEvent;
	
	@Inject 
	@Undeleted
	Event<ModelEvent> undeletedEvent;
	
	@Inject 
	@Loaded
	Event<ModelEvent> loadedEvent;
	
	//------------------------------------------------------------------------------------------
	
	public String getRequestParamId()
	{
		return ( modelIdResolver != null ? modelIdResolver.get() : null );
	}
	
	//------------------------------------------------------------------------------------------
	
	@Override
	public Object getId() 
	{
		if( super.getId() == null )
		{
			String entityId = getRequestParamId();
			if ( !StringUtils.isEmpty( entityId ) )
			{
				log.debugv( "Set id {0} from RequestParam", entityId );
				super.setId( Integer.parseInt( entityId ) );
			}
		}
		return super.getId();
	}
	
	//------------------------------------------------------------------------------------------
	
	protected ModelEvent eventModel()
	{
		return new ModelEvent( (Identifiable) getInstance() );
	}
	
	//------------------------------------------------------------------------------------------
	
	@Override
	public void create() 
	{
		if ( log.isDebugEnabled() )
		{
			if ( conversation.getId() != null )
			{
				log.debugv( "Init instance id {0} for conversation id {1}", getId(), conversation.getId() );
			}
			else
			{
				log.debugv( "Init instance id {0}", getId() );
			}
		}
		super.create();
	}
	
	//------------------------------------------------------------------------------------------
	
	@Override
	protected T createInstance() 
	{
		log.debug( "Created new instance" );
		return super.createInstance();
	}
	
	//-------------------------------------------------------------------------------
	
	public void clearInstance( ActionEvent event ) 
	{
		log.debug( "Instance cleared" );
		clearInstance();
	}
	
	//------------------------------------------------------------------------------------------
	
	@Override
	protected void initInstance() 
	{
		super.initInstance();
		if ( isManaged() )
		{
			loadedEvent.fire( eventModel() );
		}
		log.debug( "Instance init" );
	}
	
	//------------------------------------------------------------------------------------------
	
	@Override
	public boolean persist()
	{
		return super.persist();
	}
	
	//------------------------------------------------------------------------------------------
	
	@Override
	public boolean update() 
	{
		return super.update();
	}
	
	//------------------------------------------------------------------------------------------
	
	public String save()
	{
		String result = "error";
		if ( super.isManaged() )
		{
			if( update() )
			{
				result = "saved";
				savedEvent.fire( eventModel() );
				updatedEvent.fire( eventModel() );
			}
		}
		else
		{
			if( persist() )
			{
				result = "saved";
				savedEvent.fire( eventModel() );
				persistedEvent.fire( eventModel() );
			}
		}
		return result;
	}
	
	//------------------------------------------------------------------------------------------
	
	public String undelete()
	{
		String result = "error";
		if ( super.isManaged() )
		{
			if ( getInstance() instanceof Removable )
			{
				Removable rem = (Removable) getInstance();
				rem.setRemoved( false );
				rem.setRemovalDate( null );
				result = "uddeleted";
				undeletedEvent.fire( eventModel() );
			}
		}
		return result;
	}
	
	//------------------------------------------------------------------------------------------
	
	public String delete()
	{
		String result = "error";
		if ( super.isManaged() )
		{
			if ( getInstance() instanceof Removable )
			{
				Removable rem = (Removable) getInstance();
				rem.setRemoved( true );
				rem.setRemovalDate( new Date() );
				result = "removed";
				deletedEvent.fire( eventModel() );
			}
			else
			{
				if( super.remove() )
				{
					result = "deleted";
					deletedEvent.fire( eventModel() );
				}
			}
		}
		return result;
	}
	
	//------------------------------------------------------------------------------------------
	
	public boolean isRemoved()
	{
		boolean removed = false;
		if ( super.isManaged() )
		{
			if ( getInstance() instanceof Removable )
			{
				Removable rem = (Removable) getInstance();
				removed = rem.isRemoved();
			}
		}
		return removed;
	}
	
	//------------------------------------------------------------------------------------------
	
	public void refresh()
	{
		log.debug("Refrshing object" );
		getEntityManager().refresh( getInstance() );
	}
}
