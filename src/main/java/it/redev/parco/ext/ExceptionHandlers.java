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

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.WebRequest;

@HandlesExceptions
public class ExceptionHandlers 
{
	
	@Inject
	Logger log;

    void handleAll(@Handles @WebRequest CaughtException<Throwable> caught, HttpServletResponse response)
    {
    	log.error( "-------- " + caught.getException().getMessage() + " --------" );
 
        try 
        {
			response.sendError(500, "You've been caught by Solder!");
		} 
        catch (Exception e)
        {
			e.printStackTrace();
		} 

    }

}
