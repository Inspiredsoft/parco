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

import it.redev.parco.comparators.PostazioneComparator;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.model.asset.MovimentoAsset;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.MezzoService;
import it.redev.parco.service.OcService;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class PersonaHome extends ModelHome<Persona> 
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager message;

	@Inject
	MessagesManager bundleManager;

	@Inject
	MezzoService ms;
	
	@Inject
	LoggedUser lu;
	
	@Inject
	OcService oc;
	
	List<MovimentoAsset> carte = null;
	List<MovimentoAsset> pin = null;
	
	private List<Postazione> postazioni;
	
	//----------------------------------------------------------------------------------
	
	@Override
	public void clearInstance() {
		super.clearInstance();
		carte = null;
		pin = null;
		postazioni = null;
	}
	
	@Override
	protected void initInstance() {
		super.initInstance();
	}
	
	//----------------------------------------------------------------------------------
	
	public List<MovimentoAsset> getCarteCorrenti() {
		if ( carte == null ) {
			carte = new ArrayList<MovimentoAsset>();
			
			for ( MovimentoAsset movimento : getInstance().getAssegnazioni() ) {
				if ( movimento.getAsset().isFuelCard() && movimento.isConsegnata() ) {
					carte.add( movimento );
					for ( MovimentoAsset test : getInstance().getAssegnazioni() ) {
						if ( test.isRiconsegnata() && 
								test.getAsset().getId().equals( movimento.getAsset().getId() ) && 
										test.getData().after( movimento.getData() ) ) {
							carte.remove( movimento );
						}
					}
				}
			}
		}
		return carte;
	}
	
	//----------------------------------------------------------------------------------
	
	public List<MovimentoAsset> getPinCorrenti() {
		if ( pin == null ) {
			pin = new ArrayList<MovimentoAsset>();
			
			for ( MovimentoAsset movimento : getInstance().getAssegnazioni() ) {
				if ( movimento.getAsset().isPinCard() && movimento.isConsegnata() ) {
					pin.add( movimento );
					for ( MovimentoAsset test : getInstance().getAssegnazioni() ) {
						if ( test.isRiconsegnata() && 
								test.getAsset().getId().equals( movimento.getAsset().getId() ) && 
										test.getData().after( movimento.getData() ) ) {
							pin.remove( movimento );
						}
					}
				}
			}
		}
		return pin;
	}

	//----------------------------------------------------------------------------------

	public List<Persona> autocompletePersona( String filter )
	{
		List<Persona> persone = oc.findNotRemovedPersonaLike( filter ); 
		return  persone;
	}
	
	//----------------------------------------------------------------------------------
	
	public List<Postazione> getAllowedPostazioni()
	{
		if ( postazioni == null )
		{
			postazioni = oc.findAllPostazioni();
			PostazioneComparator.sort( postazioni );
		}
		return postazioni;
	}

}
