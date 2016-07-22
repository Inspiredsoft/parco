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

package it.redev.parco.session.mezzi;

import it.redev.parco.comparators.PostazioneComparator;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ModelEvent;
import it.redev.parco.ext.StatusableModelHome;
import it.redev.parco.ext.annotation.Saved;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.mezzi.AssegnazioneMezzo;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.TipoMezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.service.MezzoService;
import it.redev.parco.service.OcService;
import it.redev.parco.session.Viewed;
import it.redev.parco.session.assicurazione.PolizzaList;
import it.redev.parco.session.rifornimenti.CartaCarburanteList;
import it.redev.parco.session.rifornimenti.RifornimentoList;
import it.inspired.utils.DateUtils;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class MezzoHome extends StatusableModelHome<Mezzo> 
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
	OcService oc;
	
	@Inject
	LoggedUser lu;
	
	private List<TipoMezzo> tipiMezzo;
	
	private List<Postazione> postazioni;
	
	private AssegnazioneMezzo newAssegnazione;
	
	private AssegnazioneMezzo assToRemove;

	//-----------------------------------------------------------------------------
	
	public void initAssegnazione()
	{
		postazioni = null;
		newAssegnazione = new AssegnazioneMezzo();
		newAssegnazione.setStatoSerbatoio( AssegnazioneMezzo.StatoSerbatoio.PIENO );
		if ( getMinAssDate() != null )
		{
			newAssegnazione.setDataInizio( DateUtils.max( new Date(), getMinAssDate() ) );
		}
		else
		{
			newAssegnazione.setDataInizio( new Date() );
		}
	}
	
	//-----------------------------------------------------------------------------

	@Override
	protected void initInstance() 
	{
		super.initInstance();
		initAssegnazione();
	}
	
	//-----------------------------------------------------------------------------

	public AssegnazioneMezzo getNewAssegnazione() {
		return newAssegnazione;
	}

	public void setNewAssegnazione(AssegnazioneMezzo assegnazione) {
		this.newAssegnazione = assegnazione;
	}
	
	//-----------------------------------------------------------------------------
	
	public AssegnazioneMezzo getAssToRemove() {
		return assToRemove;
	}

	public void setAssToRemove(AssegnazioneMezzo assToRemove) {
		this.assToRemove = assToRemove;
	}

	//-----------------------------------------------------------------------------
	
	public Date getMinAssDate()
	{
		if ( getInstance().getLastAssegnazione() != null )
		{
			// Aggiunge un giorno
			//return DateUtils.addDay( getInstance().getLastAssegnazione().getDataInizio(), 1 );
			return DateUtils.addMin( getInstance().getLastAssegnazione().getDataInizio(), 1 );
		}
		return null;
	}
	
	//-----------------------------------------------------------------------------
	
	public void deassegna()
	{
		if ( assToRemove != null && assToRemove.getDataFine() == null )
		{
			getInstance().deassegna( assToRemove );
			getEntityManager().remove( assToRemove );
		}
	}

	//-----------------------------------------------------------------------------
	
	public void assegna()
	{
		if ( newAssegnazione.getPostazione() != null )
		{
			// Sposta alla mattina
			//newAssegnazione.setDataInizio( DateUtils.toMorning( newAssegnazione.getDataInizio() ) );
			newAssegnazione.setMezzo( getInstance() );
			getInstance().assegna( newAssegnazione );
			initAssegnazione();
		}
	}
	
	//----------------------------------------------------------------------------------
	
	public List<Postazione> getAllowedPostazioni()
	{
		if ( postazioni == null )
		{
			postazioni = oc.findAllPostazioni();
			if ( getInstance().getLastAssegnazione() != null )
			{
				postazioni.remove( getInstance().getLastAssegnazione().getPostazione() );
			}
			PostazioneComparator.sort( postazioni );
		}
		return postazioni;
	}

	//-----------------------------------------------------------------------------
	
	public List<Integer> getStatiDisponibili()
	{
		List<Integer> stati = Lists.newArrayList( Mezzo.Stati.list );
		stati.remove( getInstance().getStato().getStato() );
		return stati;
	}

	//-----------------------------------------------------------------------------
	
	public List<TipoMezzo> getTipiMezzo()
	{
		if ( tipiMezzo == null )
		{
			tipiMezzo = ms.findAllTipoMezzo();
		}
		return tipiMezzo;
	}
	
	//-----------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Integer> getStatiSerbatoio()
	{
		return Arrays.asList( AssegnazioneMezzo.StatoSerbatoio.list );
	}
	
	//----------------------------------------------------------------------------------

	public void validateTarga(FacesContext context, UIComponent component, Object value) throws ValidatorException
	{
		String targa = value.toString();
		
		List<Mezzo> mezzi = ms.findNotRemovedMezzoByTarga( targa );
		
		if( !mezzi.isEmpty() && !mezzi.get(0).getId().equals( getInstance().getId() ) )
		{
			log.debugv( "Mezzo gia' presente: {0}", targa );
			String text = bundleManager.getMessage( "validator.targa.notunique" );
			FacesMessage msg = new FacesMessage( text );
	        throw new ValidatorException(msg);
		}
	}

	
	//-----------------------------------------------------------------------------
	
	@Override
	public boolean persist() 
	{
		if ( !isManaged() )
		{
			getInstance().cambiaStato( Mezzo.Stati.IN_SERVIZIO, new Date(), lu.getUser(), null );
		}
		return super.persist();
	}
	
	//-----------------------------------------------------------------------------
	
	
	@Override
	public String undelete()
	{
		List<Mezzo> mezzi = ms.findNotRemovedMezzoByTarga( getInstance().getTarga() );
		
		if( !mezzi.isEmpty() && mezzi.get(0).getId() != getInstance().getId() )
		{
			log.debugv( "Mezzo gia' presente: {0}", getInstance().getTarga() );
			message.error( "validator.targa.notunique" );
			return "error";
		}
		return super.undelete();
	}
	
	//------------------------------------------------------------------------
	// Observers
	//------------------------------------------------------------------------
		
	public void onSaveModel( @Observes @Saved ModelEvent modelEvent )
	{
		if ( modelEvent.getObject() instanceof DotazioneMezzo )
		{
			DotazioneMezzo dm = (DotazioneMezzo) modelEvent.getObject();
			if ( dm.isDotazioneMezzo() && dm.getMezzo().equals( getInstance() ) )
			{
				getInstance().getDotazioni().add( dm );
			}
		}
	}
	
	//----------------------------------------------------------------------------------
	// PRODUCERS
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	RifornimentoList getRifornimentiMezzo( RifornimentoList rifornimentoList )
	{
		rifornimentoList.setMezzo( getInstance() );
		log.debugv( "Produced rifornimentiMezzo for mezzo id {0} with {1} rows", getId(), rifornimentoList.getRowCount() );
		return rifornimentoList;
	}

	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	PolizzaList getPolizzeMezzo( PolizzaList polizzaList )
	{
		polizzaList.setMezzo( getInstance() );
		log.debugv( "Produced polizzeMezzo for mezzo id {0} with {1} rows", getId(), polizzaList.getRowCount() );
		return polizzaList;
	}
	
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	CartaCarburanteList getCarteMezzo( CartaCarburanteList cartaCarburanteList )
	{
		cartaCarburanteList.setMezzo( getInstance() );
		log.debugv( "Produced cartaCarburanteList for mezzo id {0} with {1} rows", getId(), cartaCarburanteList.getRowCount() );
		return cartaCarburanteList;
	}
	
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	SoccorsoList getSoccorsiMezzo( SoccorsoList soccorsoList )
	{
		soccorsoList.setMezzo( getInstance() );
		log.debugv( "Produced soccorsoList for mezzo id {0} with {1} rows", getId(), soccorsoList.getRowCount() );
		return soccorsoList;
	}

}
