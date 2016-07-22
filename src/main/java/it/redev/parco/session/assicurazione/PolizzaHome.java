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

package it.redev.parco.session.assicurazione;

import java.util.List;

import it.redev.parco.core.ConfigManager;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.model.mezzi.CompagniaAssicurazione;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Polizza;
import it.redev.parco.service.MezzoService;
import it.inspired.utils.DateUtils;
import it.redev.parco.utils.MezzoUtils;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class PolizzaHome extends ModelHome<Polizza> 
{
	@Inject
	MezzoService ms;
	
	@Inject
	MessagesManager message;
	
	@Inject
	ConfigManager cm;
	
	private String targa;
	
	
	//--------------------------------------------------------------------------
	
	public String getTarga() {
		return targa;
	}
	
	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//--------------------------------------------------------------------------
	
	public List<Mezzo> autocompleteMezzo( String filter )
	{
		return ms.findNotRemovedMezzoLikeTarga( filter );
	}
	
	//--------------------------------------------------------------------------
	
	public List<CompagniaAssicurazione> getCompagnie()
	{
		return ms.findAllCompagnie();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void clearInstance() 
	{
		super.clearInstance();
		this.targa = null;
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
	}
	
	//--------------------------------------------------------------------------
	
	public void rinnova()
	{
		Polizza polizza = getInstance().clone();
		polizza.setDataDecorrenza( DateUtils.toMorning( DateUtils.addDay( getInstance().getDataScadenza(), 1 ) ) );
		polizza.setDataScadenza(  DateUtils.toMidday( DateUtils.addMonth( polizza.getDataDecorrenza(), getInstance().getDurataMesi() ) ) );
		ms.save( polizza );
		super.setInstance( polizza );
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String save() 
	{
		getInstance().setDataDecorrenza( DateUtils.toMorning( getInstance().getDataDecorrenza() ) );
		getInstance().setDataScadenza( DateUtils.toMidnight( getInstance().getDataScadenza() ) );
		
		if ( getInstance().getDataDecorrenza().after( getInstance().getDataScadenza() ) )
		{
			message.error( "message.polizza.dateerrate" );
			return "error";
		}
		
		List<Mezzo> mezzi = ms.findNotRemovedMezzoByTarga( targa );
		getInstance().setMezzo( mezzi.get( 0 ) );
		
		List<Polizza> polizze = ms.findPolizze( mezzi.get(0) );
		
		Polizza sovr = MezzoUtils.cercaSovrapposizione( getInstance(), polizze );
		
		if ( sovr != null )
		{
			message.error( "message.polizza.sovrapposta", 
						sovr.getNumero(), 
						DateUtils.format( sovr.getDataDecorrenza(), cm.getShortDateFormat() ), 
						DateUtils.format( sovr.getDataScadenza(), cm.getShortDateFormat() ) );
			return "error";
		}
		
		return super.save();
	}
}
