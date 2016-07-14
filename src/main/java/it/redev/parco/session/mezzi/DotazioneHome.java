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

package it.redev.parco.session.mezzi;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.AssetModelHome;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.asset.GenereAsset;
import it.redev.parco.model.asset.MovimentoAsset;
import it.redev.parco.service.AssetService;
import it.redev.parco.session.oc.PostazioneHome;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class DotazioneHome extends AssetModelHome<DotazioneMezzo> 
{
	@Inject
	LoggedUser lu;
	
	@Inject
	Logger log;
	
	@Inject
	MezzoHome mh;
	
	@Inject
	PostazioneHome ph;
	
	@Inject
	AssetService as;
	
	//-----------------------------------------------------------------------------

	public List<Integer> getStatiDisponibili()
	{
		List<Integer> stati = Lists.newArrayList( DotazioneMezzo.Stato.list );
		stati.remove( getInstance().getStato().getStato() );
		return stati;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isLastMovimento( MovimentoAsset movimento )
	{
		return movimento.equals( getInstance().getLastMovimentoAsset() );
	}
	
	//--------------------------------------------------------------------------

	public List<GenereAsset> getGeneri()
	{
		return ( instance.isDotazioneMezzo() ? as.findGeneriDotazioneMezzo() : as.findGeneriDotazionePostazione() );
	}
	
	//--------------------------------------------------------------------------
	
//	@Override
//	protected void initInstance() {
//		super.initInstance();
//		if ( !isManaged() )
//		{
//			instance.setMezzo( mh.getInstance() );
//			instance.setTipo( Asset.Tipo.MEZZO );
//		}
//	}
	
	public void clearForMezzo() {
		if ( !isManaged() )
		{
			instance.setMezzo( mh.getInstance() );
			instance.setTipo( Asset.Tipo.MEZZO );
		}
	}
	
	public void clearForPostazione() {
		if ( !isManaged() )
		{
			instance.setPostazione( ph.getInstance() );
			instance.setTipo( Asset.Tipo.POSTAZIONE );
		}
	}
	
	//--------------------------------------------------------------------------
	
	public String save()
	{
		if ( !isManaged() )
		{
			instance.cambiaStato( DotazioneMezzo.Stato.ATTIVO, new Date(), lu.getUser(), null );
		}
		return super.save();
	}
}
