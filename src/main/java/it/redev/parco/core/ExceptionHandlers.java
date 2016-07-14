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

package it.redev.parco.core;

import java.io.IOException;

import it.redev.parco.core.view.ViewChain;

import javax.enterprise.context.NonexistentConversationException;
import javax.inject.Inject;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.logging.Logger;

@HandlesExceptions
public class ExceptionHandlers
{
	@Inject
	Logger log;
	
	@Inject
	ViewChain viewChain;
 
    public void onNonexistentConversation(@Handles CaughtException<NonexistentConversationException> evt)
    {
        log.debug("--------- NonexistentConversationException! ------------\n" 
        		+ evt.getException().getMessage(), evt.getException());
       
        evt.handled();
       
        try {
			viewChain.goTo( "/private/home.xhtml" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
