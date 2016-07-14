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

package it.redev.parco.session.rifornimenti;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.rifornimenti.Scontrino;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;
import org.primefaces.event.RowEditEvent;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class ScontrinoInlineEdit implements Serializable
{
	@Inject
	Logger log;
	
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	MessagesManager bundleManager;

	//--------------------------------------------------------------------------
	
	public void onEdit( RowEditEvent event ) 
	{  
	        Scontrino scon = (Scontrino) event.getObject();  
	        if ( !scon.isAnomalo() )
	        {
	        	if ( StringUtils.equalsIgnoreZeros( scon.getNumero(), scon.getRifornimento().getNumeroScontrino() ) )
	        	{
	        		scon.cambiaStato( Scontrino.Stato.GIUSTIFICATO, new Date(), loggedUser.getUser(), "" );
	        	}
	        	else
	        	{
	        		log.debugv( "Scontrino {0} diverso dal numero scontrino {1} sul rifornimento", scon.getNumero(), scon.getRifornimento().getNumeroScontrino() );
	        		
	        		bundleManager.error( "valodator.scon.wrong" );
	        		
	        		FacesContext context = FacesContext.getCurrentInstance();
	    			FacesMessage fm = new FacesMessage();
	    			fm.setSeverity( FacesMessage.SEVERITY_ERROR );
	    			fm.setSummary( bundleManager.getMessage( "valodator.scon.wrong" ) );
	    			context.addMessage( "msgGrowl", fm );
	        		
	        		scon.setNumero( null );
	        	}
	        }
	}  
}
