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

package it.redev.parco.session.job;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

import it.redev.parco.ext.ModelEvent;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.ext.annotation.Restarted;
import it.redev.parco.ext.annotation.Stopped;
import it.redev.parco.job.AbstractJob;
import it.redev.parco.model.core.Job;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class JobHome extends ModelHome<Job> 
{
	@Inject
	Logger log;
	
	@Inject 
	@Restarted
	Event<ModelEvent> redoEvent;
	
	@Inject 
	@Stopped
	Event<ModelEvent> stopEvent;
	
	@Inject
	protected Event<Job> jobEndedEvent;
	
	public void stop()
	{
		if ( isStoppable() )
		{
			getInstance().setStatus( Job.Status.STOPPED );
			getInstance().setStartDate( null );
			getInstance().setEndDate( null );
			getInstance().setException( null );
			getInstance().setExeTime( null );
			
			jobEndedEvent.fire( getInstance() );
			stopEvent.fire( eventModel() );
		}
	}
	
	public void redo()
	{
		if ( isRepeatable() )
		{
			getInstance().setStatus( Job.Status.WAITING );
			getInstance().setStartDate( null );
			getInstance().setEndDate( null );
			getInstance().setException( null );
			getInstance().setExeTime( null );
			getInstance().setTries( 0 );
			
			jobEndedEvent.fire( getInstance() );
			redoEvent.fire( eventModel() );
		}
	}

	public boolean isRepeatable()
	{
		boolean repeat = false;
		if ( getInstance().isError() || getInstance().isDelayed() || getInstance().isEnded() || getInstance().isStopped() )
		{
			try
			{
				String clazz = getInstance().getJavaClass();
			
				AbstractJob aj = (AbstractJob) Class.forName(clazz).newInstance();
				
				repeat = aj.isRepeatable();
			}
			catch(Exception e)
			{
				log.error( e.getMessage() );
				repeat = false;
			}
		}
		return repeat;
	}
	
	public boolean isStoppable()
	{
		return getInstance().isWaiting() || getInstance().isDelayed();
	}
}
