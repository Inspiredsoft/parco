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

package it.redev.parco.session.mail;

import it.redev.parco.ext.ModelEvent;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.ext.annotation.Restarted;
import it.redev.parco.ext.annotation.Stopped;
import it.redev.parco.model.core.Mail;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;


@SuppressWarnings("serial")
@Named
@ConversationScoped
public class MailHome extends ModelHome<Mail> 
{
	@Inject 
	@Restarted
	Event<ModelEvent> redoEvent;
	
	@Inject 
	@Stopped
	Event<ModelEvent> stopEvent;
	
	public void stop()
	{
		if ( !getInstance().isStopped() && !getInstance().isEnded() )
		{
			getInstance().setStatus( Mail.Status.STOPPED );
			getInstance().setStartDate( null );
			getInstance().setEndDate( null );
			getInstance().setException( null );
			getInstance().setExeTime( null );
		
			redoEvent.fire( eventModel() );
		}
	}
	
	public void redo()
	{
		if ( !getInstance().isRunning() && !getInstance().isWaiting() )
		{
			getInstance().setStatus( Mail.Status.WAITING );
			getInstance().setStartDate( null );
			getInstance().setEndDate( null );
			getInstance().setException( null );
			getInstance().setExeTime( null );
			getInstance().setTries( 0 );
			
			stopEvent.fire( eventModel() );
		}
	}
}
