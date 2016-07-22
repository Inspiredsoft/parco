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

package it.redev.parco.session.oc;

import it.redev.parco.ext.ModelEvent;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.ext.annotation.Saved;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.oc.Postazione;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class PostazioneHome extends ModelHome<Postazione> 
{
	//------------------------------------------------------------------------
	// Observers
	//------------------------------------------------------------------------
		
	public void onSaveModel( @Observes @Saved ModelEvent modelEvent )
	{
		if ( modelEvent.getObject() instanceof DotazioneMezzo )
		{
			DotazioneMezzo dm = (DotazioneMezzo) modelEvent.getObject();
			if ( dm.isDotazionePostazione() && dm.getPostazione().equals( getInstance() ) )
			{
				getInstance().getDotazioni().add( dm );
			}
		}
	}
}
