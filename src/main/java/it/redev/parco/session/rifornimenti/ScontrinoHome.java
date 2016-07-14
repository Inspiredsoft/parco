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

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.rifornimenti.Scontrino;
import it.redev.parco.service.MezzoService;
import it.redev.parco.session.Viewed;
import it.redev.parco.session.report.ConsumiReport;
import it.inspired.utils.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class ScontrinoHome extends ModelHome<Scontrino> 
{
	@Inject
	Logger log;
	
	@Inject
	MezzoService ms;
	
	@Inject
	LoggedUser loggedUser;
	
	private String 	targa;
	private Integer	stato;
		
	//--------------------------------------------------------------------------
	
	public String getTarga() {
		return targa;
	}
	
	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//--------------------------------------------------------------------------
	
	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	//--------------------------------------------------------------------------
	
	public List<Mezzo> autocompleteMezzo( String filter )
	{
		return ms.findNotRemovedMezzoLikeTarga( filter );
	}
	
	//--------------------------------------------------------------------------
	
	public List<Integer> getStatiAmmessi()
	{
		return Arrays.asList( new Integer[]{Scontrino.Stato.SMARRITO, Scontrino.Stato.ILLEGIBILE, Scontrino.Stato.ALTRO} );
	}
	
	//----------------------------------------------------------------------------------
	 
	public boolean isEditable()
	{
		//return scontrinoView.isEditable( getInstance() );
		return  getInstance().isDaGiustificare() || loggedUser.getUser().equals( getInstance().getStato().getUtenteStato() );
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void clearInstance() 
	{
		this.targa = null;
		this.stato = null;
		super.clearInstance();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String save() 
	{
		if ( !StringUtils.isEmpty( targa ) )
		{
			List<Mezzo> mezzi = ms.findNotRemovedMezzoByTarga( targa );
			getInstance().setMezzoRettificato( mezzi.get( 0 ) );
		}
		else
		{
			getInstance().setMezzoRettificato( null );
		}
		
		if ( stato != null )
		{
			getInstance().cambiaStato( stato, new Date(), loggedUser.getUser(), getInstance().getNote() );
			getInstance().setNumero( null );
		}
		else if ( getInstance().getChilometraggioRettificato() != null || getInstance().getMezzoRettificato() != null )
		{
			getInstance().cambiaStato( Scontrino.Stato.RETTIFICATO, new Date(), loggedUser.getUser(), getInstance().getNote() );
		}
		else if ( !getInstance().isGiustificato() && !getInstance().isWrongNumber() )
		{
			getInstance().cambiaStato( Scontrino.Stato.GIUSTIFICATO, new Date(), loggedUser.getUser(), getInstance().getNote() );
		}
		
		return super.save();
	}
	
	//--------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	ConsumiReport getConsumiScontrino( ConsumiReport consumiReport )
	{
		consumiReport.setMezzo( getInstance().getRifornimento().getMezzo() );
		consumiReport.orderByDate();
		consumiReport.setPeriod( null );
		log.debugv( "Produced consumiScontrino for mezzo id {0} with {1} rows", getId(), consumiReport.getRowCount() );
		return consumiReport;
	}
	
}
