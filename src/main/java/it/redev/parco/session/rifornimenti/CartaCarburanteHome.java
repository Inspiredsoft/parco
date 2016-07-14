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
import it.redev.parco.ext.AssetModelHome;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.service.MezzoService;
import it.redev.parco.session.Viewed;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class CartaCarburanteHome extends AssetModelHome<CartaCarburante> 
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager message;
	
	@Inject
	MezzoService ms;
	
	@Inject
	LoggedUser lu;
	
	private String targa;
	
	//--------------------------------------------------------------------------
	
	public String getTarga() {
		return targa;
	}
	
	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//----------------------------------------------------------------------------------
	
	public boolean isAttiva()
	{
		return getInstance().isAttiva();
	}
	
	//----------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
	}
	
	//-----------------------------------------------------------------------------

	public List<Integer> getStatiDisponibili()
	{
		List<Integer> stati = Lists.newArrayList( CartaCarburante.Stato.list );
		stati.remove( getInstance().getStato().getStato() );
		return stati;
	}
	
	//--------------------------------------------------------------------------
	
	public List<Mezzo> autocompleteMezzo( String filter )
	{
		return ms.findNotRemovedMezzoLikeTarga( filter );
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void clearInstance()
	{
		super.clearInstance();
		targa = null;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected void initInstance() 
	{
		super.initInstance();
		if ( isManaged() )
		{
			this.targa = getInstance().getMezzo().getTarga();
		}
		else
		{
			getInstance().setTipo( Asset.Tipo.FUEL_CARD );
			getInstance().cambiaStato( CartaCarburante.Stato.ATTIVA, new Date(), lu.getUser(), "" );
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String save() 
	{
		CartaCarburante carta = getInstance();
		
		List<CartaCarburante> carte = ms.findNotRemovedCarta( carta.getNumero(), carta.getGestore() );
		
		if ( carte != null && !carte.isEmpty() && !carte.get(0).getId().equals( carta.getId() ) )
		{
			message.error( "validator.carta.notunique", carta.getNumero(), message.getMessage( "gestore." + carta.getGestore() ) );
			return "error";
		}

		List<Mezzo> mezzi = ms.findNotRemovedMezzoByTarga( targa );
		
		if ( mezzi.isEmpty() )
		{
			message.error( "validator.targa.notfound", targa );
			return "error";
		}
		
		carta.setMezzo( mezzi.get( 0 ) );
		
		carte = ms.findNotRemovedAttive( mezzi.get( 0 ) );
		if ( carte != null && !carte.isEmpty() && !carte.get(0).getId().equals( carta.getId() ) )
		{
			message.error( "validator.mezzocarta", targa, carte.get(0).getNumero(), message.getMessage( "gestore." + carte.get(0).getGestore() ) );
			return "error";
		}
		
		return super.save();
	}
	
	//-----------------------------------------------------------------------------
	
	@Override
	public String undelete()
	{
		CartaCarburante carta = getInstance();
		
		List<CartaCarburante> carte = ms.findNotRemovedCarta( carta.getNumero(), carta.getGestore() );
		
		if ( carte != null && !carte.isEmpty() )
		{
			message.error( "validator.carta.notunique", carta.getNumero(), message.getMessage( "gestore." + carta.getGestore() ) );
			return "error";
		}
		return super.undelete();
	}
	
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	RifornimentoList getRifornimentiCarta( RifornimentoList rifornimentoList )
	{
		rifornimentoList.setCartaCarburante( getInstance() );
		rifornimentoList.setPeriod(null);
		rifornimentoList.orderByDateDescending();
		log.debugv( "Produced rifornimentiCarta for carta id {0} with {1} rows", getId(), rifornimentoList.getRowCount() );
		return rifornimentoList;
	}
}
