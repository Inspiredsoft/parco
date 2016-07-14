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

import it.inspired.utils.StringUtils;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.AssetModelHome;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.service.MezzoService;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class PinCartaHome extends AssetModelHome<PinCard> {

	@Inject
	LoggedUser lu;
	
	@Inject
	MezzoService ms;
	
	@Inject
	MessagesManager message;
	
	//--------------------------------------------------------------------------
	
	@Override
	protected void initInstance() 
	{
		super.initInstance();
		if ( !isManaged() )
		{
			getInstance().setTipo( Asset.Tipo.PINCARD );
			getInstance().cambiaStato( PinCard.Stato.ATTIVA, new Date(), lu.getUser(), "" );
		}
	}
	
	//-----------------------------------------------------------------------------

	public List<Integer> getStatiDisponibili()
	{
		List<Integer> stati = Lists.newArrayList( PinCard.Stato.list );
		stati.remove( getInstance().getStato().getStato() );
		return stati;
	}
	
	//----------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
	}
	
	//----------------------------------------------------------------------------------
	
	@Override
	public String save() {
		
		PinCard carta = getInstance();
		
		if ( !StringUtils.isEmpty( carta.getCodiceBusta() ) ) {
			// Deve essere univoco
			List<PinCard> carteb = ms.findNotRemovedPinCartaByBusta( carta.getCodiceBusta(), carta.getGestore() );

			if ( carteb != null && !carteb.isEmpty() && !carteb.get(0).getId().equals( carta.getId() ) )
			{
				message.error( "validator.pincarta.cb.notunique", carta.getCodiceBusta(), message.getMessage( "gestore." + carta.getGestore() ) );
				return "error";
			}
		}
		
		List<PinCard> carte = ms.findNotRemovedPinCartaByOperatore( carta.getCodiceOperatore(), carta.getGestore() );
		if ( carte != null && !carte.isEmpty() && !carte.get(0).getId().equals( carta.getId() ) )
		{
			message.error( "validator.pincarta.co.notunique", carta.getCodiceOperatore(), message.getMessage( "gestore." + carta.getGestore() ) );
			return "error";
		}
		
		return super.save();
	}
	
	//-----------------------------------------------------------------------------
	
	@Override
	public String undelete()
	{
		PinCard carta = getInstance();
		
		if ( !StringUtils.isEmpty( carta.getCodiceBusta() ) ) {
			List<PinCard> carteb = ms.findNotRemovedPinCartaByBusta( carta.getCodiceBusta(), carta.getGestore() );
			
			if ( carteb != null && !carteb.isEmpty() )
			{
				message.error( "validator.carta.cb.notunique", carta.getCodiceBusta(), message.getMessage( "gestore." + carta.getGestore() ) );
				return "error";
			}
		}
		
		List<PinCard> carte = ms.findNotRemovedPinCartaByOperatore( carta.getCodiceOperatore(), carta.getGestore() );
		if ( carte != null && !carte.isEmpty() && !carte.get(0).getId().equals( carta.getId() ) )
		{
			message.error( "validator.pincarta.co.notunique", carta.getCodiceOperatore(), message.getMessage( "gestore." + carta.getGestore() ) );
			return "error";
		}
		
		return super.undelete();
	}
	
	//----------------------------------------------------------------------------------
	
	public boolean isAttiva()
	{
		return getInstance().getStati().isEmpty() && getInstance().isAttiva();
	}
}
