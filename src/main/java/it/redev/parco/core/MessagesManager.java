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

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.MessageFactory;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;

@SuppressWarnings("serial")
@Named
public class MessagesManager implements Serializable
{
	@Inject
	Messages messages;
	
	@Inject
    private MessageFactory factory;
	
	public static final String MESSAGES_BUNDLE_NAME = "messages";
	
	//-------------------------------------------------------------------------------
	
	public BundleKey getBundleKey( String key )
	{
		return new BundleKey( MESSAGES_BUNDLE_NAME, key );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getMessage( String key, Object...params )
	{
		return factory.info( getBundleKey( key ), params ).build().getText();
	}
	
	//-------------------------------------------------------------------------------
	
	public void info( String key, Object ... objs )
	{
		messages.info( getBundleKey( key ), objs );
	}
	
	//-------------------------------------------------------------------------------
	
	public void error( String key, Object ... objs )
	{
		messages.error( getBundleKey( key ), objs );
	}
	
	//-------------------------------------------------------------------------------
	
	public void warn( String key, Object ... objs )
	{
		messages.warn( getBundleKey( key ), objs );
	}
	
}
