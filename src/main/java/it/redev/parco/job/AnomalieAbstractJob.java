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

package it.redev.parco.job;

import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.mezzi.CodificaCarburante;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.utils.AnomaliaUtils;
import it.inspired.utils.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

public abstract class AnomalieAbstractJob extends RifornimentiAbstractJob 
{
	protected int totAnomalie= 0;
	
	private Integer minKm 	= 100;
	private Integer	minHour = 8;
	
	private List<Anomalia> anomalie;
	
	private List<CodificaCarburante> codifiche;
	
	private HashMap<String, String> errors = new HashMap<String, String>();

	//------------------------------------------------------------------------------------------
	
	protected void writeErrors() throws Exception
	{
		for ( String msg : errors.values() )
		{
			addErrorMessage( msg );
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected List<CodificaCarburante> getCodifiche()
	{
		if ( codifiche == null )
		{
			codifiche = getEntityManager().createQuery("FROM CodificaCarburante").getResultList();
		}
		return codifiche;
	}
	
	//------------------------------------------------------------------------------------------

	protected CodificaCarburante findCodifica( String valore )
	{
		for ( CodificaCarburante cc : getCodifiche() )
		{
			if ( valore.trim().equalsIgnoreCase( cc.getValore().trim() ) )
			{
				return cc;
			}
		}
		return null;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected Integer findTipoCarburante( Rifornimento rif )
	{
		if ( rif.getTipoCarburante() == null )
		{
			CodificaCarburante cc = findCodifica( rif.getProdotto() );
			if ( cc != null && cc.getTipo() != null )
			{
				rif.setTipoCarburante( cc.getTipo() );
			}
		}
		return rif.getTipoCarburante();
	}
			
	//------------------------------------------------------------------------------------------
	
	protected void removeAnomalie( Rifornimento rif )
	{
		for ( Anomalia anomalia : rif.getAnomalie() )
		{
			getEntityManager().remove( anomalia );
		}
		rif.getAnomalie().clear();
	}
	
	//------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected CartaCarburante findCartaAttiva( Mezzo mezzo )
	{
		Query query = getEntityManager().createQuery( 
				"FROM CartaCarburante cc " +
				"WHERE cc.mezzo = :mezzo " +
				"AND   cc.removed = false " +
				"AND   cc.stato.stato = :stato ")
			.setParameter( "mezzo", mezzo )
			.setParameter( "stato", CartaCarburante.Stato.ATTIVA );
		
		List<CartaCarburante> carte = query.getResultList();
				
		return ( carte.size() == 1 ? carte.get( 0 ) : null );
	}
	
	//------------------------------------------------------------------------------------------
	
	protected void addAnomalia( Anomalia anomalia )
	{
		if ( anomalie == null )
		{
			anomalie = new ArrayList<Anomalia>();
		}
		if ( anomalia != null )
		{
			anomalie.add( anomalia );
			totAnomalie++;
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	protected void addAnomalie( List<Anomalia> anos )
	{
		if ( anos != null )
		{
			for ( Anomalia ano : anos )
			{
				addAnomalia( ano );
			}
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	protected void flushAnomalie() throws Exception
	{
		if ( anomalie != null )
		{
			for ( Anomalia ano : anomalie )
			{
				persist( ano );
			}
			commit();
			anomalie.clear();
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaChilometraggio( Rifornimento rif )
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			if ( rif.getChilometraggio() < minKm )
			{
				ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CHILOMETRAGGIO, Anomalia.Stato.RILEVATA );
				ano.setDescrizione( "Il chilometraggio (" + rif.getChilometraggio() + ") è inferiore a " + minKm + " Km" );
				anos.add( ano );
			}
			
			Integer kmScontrino = rif.getScontrino().getChilometraggioRettificato();	
			if ( kmScontrino != null  )
			{
				if ( kmScontrino >= minKm )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CHILOMETRAGGIO, Anomalia.Stato.CONSEGUENTE );
					ano.setDescrizione( "Il chilometraggio rettificato (" + kmScontrino + ") è inferiore a " + minKm + " Km" );
					anos.add( ano );
				}
				else if ( ano != null )
				{
					ano.setStato( Anomalia.Stato.RETTIFICATA );
				}
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaChilometraggioPred( Rifornimento rif, Rifornimento pred )
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null && pred != null ) 
		{
			if ( pred.getChilometraggio() >= rif.getChilometraggio() )
			{
				ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.CHILOMETRAGGIO, Anomalia.Stato.RILEVATA ); 
				ano.setDescrizione( "Il chilometraggio (" + rif.getChilometraggio() + ") è inferiore o uguale a quello precedente (" + pred.getChilometraggio() + ")" );
				anos.add( ano );
			}
			
			/*
			 * Verifica rettifica km
			 */
			Integer kmScon 		= rif.getScontrino().getChilometraggioRettificato();
			Integer kmSconPred 	= pred.getScontrino().getChilometraggioRettificato();
				
			if ( kmScon != null && kmSconPred != null )
			{
				if ( kmSconPred >= kmScon )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.CHILOMETRAGGIO, Anomalia.Stato.CONSEGUENTE );
					ano.setDescrizione( "Il chilometraggi rettificato (" + kmScon + ") è inferiore o uguale a quello precedente reffiticato (" + kmSconPred + ")" );
					anos.add( ano );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
			else if ( kmScon != null )
			{
				if ( pred.getChilometraggio() >= kmScon )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.CHILOMETRAGGIO, Anomalia.Stato.CONSEGUENTE );
					ano.setDescrizione( "Il chilometraggi rettificato (" + kmScon + ") è inferiore o uguale a quello precedente (" + pred.getChilometraggio() + ")" );
					anos.add( ano );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
			else if ( kmSconPred != null )
			{
				if ( kmSconPred >= rif.getChilometraggio() )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.CHILOMETRAGGIO, Anomalia.Stato.CONSEGUENTE );
					ano.setDescrizione( "Il chilometraggi (" + rif.getChilometraggio() + ") è inferiore o uguale a quello precedente rettificato (" + kmSconPred + ")" );
					anos.add( ano );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
			
			/**
			 * Verifico rettifica mezzo
			 */
			Mezzo mezzoRett 	= rif.getScontrino().getMezzoRettificato();
			Mezzo mezzoRettPred = pred.getScontrino().getMezzoRettificato();
				
			if ( mezzoRett != null && !mezzoRett.equals( pred.getMezzo() ) )
			{
				// Il rifornimento ha un mezzo rettificato e per evitare ricorsione infinita
				// verifico che il mezzo rettificato non sia quello del rifornimento precedente
				
				//Rifornimento newPred = findPred( mezzoScon, rif.getData(), null );
				Rifornimento newPred = findPredNonRettificato( mezzoRett, rif.getData() );
				List<Anomalia> newAnos = verificaChilometraggioPred( rif, newPred );
				if ( !newAnos.isEmpty() )
				{
					AnomaliaUtils.cambiaStato( newAnos, Anomalia.Stato.CONSEGUENTE );
					anos.addAll( newAnos );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
				
			}
			else if ( mezzoRettPred != null )
			{
				//Rifornimento newPred = findPred( rif.getMezzo(), rif.getData(), pred );
				Rifornimento newPred = findPredNonRettificato( rif.getMezzo(), rif.getData() );
				List<Anomalia> newAnos = verificaChilometraggioPred( rif, newPred );
				if ( !newAnos.isEmpty() )
				{
					AnomaliaUtils.cambiaStato( newAnos, Anomalia.Stato.CONSEGUENTE );
					anos.addAll( newAnos );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaFrequenza( Rifornimento rif, Rifornimento pred )
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null && pred != null ) 
		{
			Date limite = DateUtils.addHour( rif.getData(), - minHour );
			if ( limite.before( pred.getData() ) )
			{
				ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.FREQUENZA, Anomalia.Stato.RILEVATA );
				ano.setDescrizione( "Il rifornimento è avvenuto entro le " + minHour + " ore dal precedente" );
				anos.add( ano );
			}
			
			/**
			 * Verifico rettifica mezzo
			 */
			Mezzo mezzoRett 	= rif.getScontrino().getMezzoRettificato();
			Mezzo mezzoRettPred = pred.getScontrino().getMezzoRettificato();
				
			if ( mezzoRett != null && !mezzoRett.equals( pred.getMezzo() ) )
			{
				//Rifornimento newPred = findPred( mezzoRett, rif.getData(), null );
				Rifornimento newPred = findPredNonRettificato( mezzoRett, rif.getData() );
				List<Anomalia> newAnos = verificaFrequenza( rif, newPred );
				if ( !newAnos.isEmpty() )
				{
					AnomaliaUtils.cambiaStato( newAnos, Anomalia.Stato.CONSEGUENTE );
					anos.addAll( newAnos );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
				
			}
			else if ( mezzoRettPred != null )
			{
				//Rifornimento newPred = findPred( rif.getMezzo(), rif.getData(), pred );
				Rifornimento newPred = findPredNonRettificato( rif.getMezzo(), rif.getData() );
				List<Anomalia> newAnos = verificaFrequenza( rif, newPred );
				if ( !newAnos.isEmpty() )
				{
					AnomaliaUtils.cambiaStato( newAnos, Anomalia.Stato.CONSEGUENTE );
					anos.addAll( newAnos );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
			
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaCartaMezzo( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			CartaCarburante cc = findCartaAttiva( rif.getMezzo() );
			if ( cc != null )
			{
				if ( !cc.getNumero().equals( rif.getCarta().getNumero() ) )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CARTA, Anomalia.Stato.RILEVATA ); 
					ano.setDescrizione( "La carta carburante utilizzata (" + rif.getCarta().getNumero() + ") è diversa da quella associata al mezzo (" + cc.getNumero() + ")" );
					anos.add( ano );
				}
			}
			else
			{
				errors.put( "CARTA:" + rif.getMezzo().getTarga() , "Impossibile trovare la carta attiva del mezzo " + rif.getMezzo().getTarga() );
			}
			
			/**
			 * Verifico rettifica mezzo
			 */
			Mezzo mezzoScon = rif.getScontrino().getMezzoRettificato();	
			if ( mezzoScon != null )
			{
				cc = findCartaAttiva( mezzoScon );
				if ( !cc.getNumero().equals( rif.getCarta().getNumero() ) )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CARTA, Anomalia.Stato.CONSEGUENTE );
					ano.setDescrizione( "La carta carburante utilizzata (" + rif.getCarta().getNumero() + ") è diversa da quella associata al mezzo rettificato (" + cc.getNumero() + ")" );
					anos.add( ano );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
			
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaSerbatoio( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			if ( rif.getMezzo().getTipoMezzo() != null )
			{
				BigDecimal serbatoio = rif.getMezzo().getTipoMezzo().getSerbatoio();
				if ( serbatoio != null )
				{
					if ( rif.getQuantita().compareTo( serbatoio ) > 0 )
					{
						ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.SERBATOIO, Anomalia.Stato.RILEVATA );
						ano.setDescrizione( "La quanità del rifornimento (" + rif.getQuantita() + ") è superiore alla capacità del serbatoio (" + serbatoio + ") del mezzo " + rif.getMezzo().getTarga() );
						anos.add( ano );
					}
				}
				else
				{
					errors.put( "SERB:" + rif.getMezzo().getTipoMezzo().getDescrizione(), "Il tipo mezzo " + rif.getMezzo().getTipoMezzo().getDescrizione() + " non ha definito il serbatoio" );
				}
			}
			else
			{
				errors.put( "TIPO:" + rif.getMezzo().getTarga(), "Il mezzo " + rif.getMezzo().getTarga() + " non ha tipologia associata" );
			}
			
			/**
			 * Verifico rettifica mezzo
			 */
			Mezzo mezzoScon = rif.getScontrino().getMezzoRettificato();
			if ( mezzoScon != null )
			{
				if ( mezzoScon.getTipoMezzo() != null )
				{
					BigDecimal serbatoio = mezzoScon.getTipoMezzo().getSerbatoio();
					if ( serbatoio != null )
					{
						if ( rif.getQuantita().compareTo( serbatoio ) > 0 )
						{
							ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.SERBATOIO, Anomalia.Stato.CONSEGUENTE );
							ano.setDescrizione( "La quanità del rifornimento (" + rif.getQuantita() + ") è superiore alla capacità del serbatoio (" + serbatoio + ") del mezzo " + mezzoScon.getTarga() );
							anos.add( ano );
						}
						else if ( ano != null )
						{
							ano.setTipo( Anomalia.Stato.RETTIFICATA );
						}
					}
					else
					{
						errors.put( "SERB:" + mezzoScon.getTipoMezzo().getDescrizione(), "Il tipo mezzo " + mezzoScon.getTipoMezzo().getDescrizione() + " non ha definito il serbatoio" );
					}
				}
				else
				{
					errors.put( "TIPO:" + mezzoScon.getTarga(), "Il mezzo rettificato " + mezzoScon.getTarga() + " non ha tipologia associata" ); 
				}
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaServizio( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			Integer stato = rif.getMezzo().findStato( rif.getData() );
			if ( stato != null )
			{
				if ( !stato.equals( Mezzo.Stati.IN_SERVIZIO )  )
				{
					ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.STATO, Anomalia.Stato.RILEVATA );
					ano.setDescrizione( "Il mezzo (" + rif.getMezzo().getTarga() + ") non era IN SERVIZIO alla data del rifornimento (" + DateUtils.format( rif.getData(), "dd/MM/yyyy") + ")" );
					anos.add( ano );
				}
			}
			else
			{
				errors.put( "STATO:" + rif.getMezzo().getTarga(), "Lo stato del mezzo " + rif.getMezzo().getTarga() + " alla data " + DateUtils.format( rif.getData(), "dd/MM/yyyy") + " non è noto");
			}
			
			/**
			 * Verifico rettifica mezzo
			 */
			Mezzo mezzoScon = rif.getScontrino().getMezzoRettificato();
			if ( mezzoScon != null )
			{
				stato = rif.getMezzo().findStato( rif.getData() );
				if (  stato != null )
				{

					if ( !stato.equals( Mezzo.Stati.IN_SERVIZIO )  )
					{
						ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.STATO, Anomalia.Stato.RILEVATA );
						ano.setDescrizione( "Il mezzo rettificato (" + rif.getMezzo().getTarga() + ") non era IN SERVIZIO alla data del rifornimento (" + DateUtils.format( rif.getData(), "dd/MM/yyyy") + ")" );
						anos.add( ano );
					}
					else if ( ano != null )
					{
						ano.setTipo( Anomalia.Stato.RETTIFICATA );
					}
				}
				else
				{
					errors.put( "STATO:" + rif.getMezzo().getTarga(), "Lo stato del mezzo rettificato " + rif.getMezzo().getTarga() + " alla data " + DateUtils.format( rif.getData(), "dd/MM/yyyy") + " non è noto");
				}
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaCarburanteAmmesso( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			CodificaCarburante cc = findCodifica( rif.getProdotto() );
			if ( cc!= null && !cc.isConsentito() )
			{
				ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CARBURANTE_AMMESSO, Anomalia.Stato.RILEVATA );
				ano.setDescrizione( "Il carburante (" + rif.getProdotto() + ") non è consentito" );
				anos.add( ano );
			}
		}
		return anos;
	}
	
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaCarburante( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			Integer carbRif = findTipoCarburante( rif );
			if( carbRif != null )
			{
				if ( rif.getMezzo().getTipoMezzo() != null )
				{
					Integer carbMezzo = rif.getMezzo().getTipoMezzo().getTipoCarburante();
					if ( carbMezzo != null )
					{
						if ( carbRif != carbMezzo )
						{
							ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CARBURANTE, Anomalia.Stato.RILEVATA );
							ano.setDescrizione( "Il tipo carburante del rifornimento è differente da quello del mezzo" );
							anos.add( ano );
						}
					}
					else
					{
						errors.put( "CARBO:" + rif.getMezzo().getTipoMezzo().getDescrizione(), "Il tipo mezzo '" + rif.getMezzo().getTipoMezzo().getDescrizione() + "' non ha definito il carburante" );
					}
				}
				else
				{
					errors.put( "TIPO:" + rif.getMezzo().getTarga(), "Il mezzo " + rif.getMezzo().getTarga() + " non ha tipologia associata" );
				}
				
				/**
				 * Verifico rettifica mezzo
				 */
				Mezzo mezzoScon = rif.getScontrino().getMezzoRettificato();
				if ( mezzoScon != null )
				{
					if ( mezzoScon.getTipoMezzo() != null )
					{
						Integer carbMezzo = mezzoScon.getTipoMezzo().getTipoCarburante();
						if ( carbMezzo != null )
						{
							if ( carbRif != carbMezzo )
							{
								ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CARBURANTE, Anomalia.Stato.CONSEGUENTE );
								ano.setDescrizione( "Il tipo carburante del rifornimento è differente da quello del mezzo rettificato" );
								anos.add( ano );
							}
							else if ( ano != null )
							{
								ano.setTipo( Anomalia.Stato.RETTIFICATA );
							}
						}
						else
						{
							errors.put( "CARBO:" + mezzoScon.getTipoMezzo().getDescrizione(), "Il tipo mezzo '" + mezzoScon.getTipoMezzo().getDescrizione() + "' non ha definito il carburante" );
						}
					}
					else
					{
						errors.put( "TIPO:" + mezzoScon.getTarga(), "Il mezzo rettificato " + mezzoScon.getTarga() + " non ha tipologia associata" ); 
					}
				}
			}
			else
			{
				errors.put( rif.getProdotto(), "Manca la trascodifica del prodotto " + rif.getProdotto() );
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaConsumo( Rifornimento rif, Rifornimento pred ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null && pred != null ) 
		{
			Integer diffKm = rif.getChilometraggio() - pred.getChilometraggio();
			if ( diffKm > 0 )
			{
				if ( rif.getMezzo().getTipoMezzo() != null )
				{
					BigDecimal consumo = ( new BigDecimal( diffKm ) ).divide( rif.getQuantita(), 2, RoundingMode.HALF_UP );
					BigDecimal urbano = rif.getMezzo().getTipoMezzo().getConsumoUrbano();
					BigDecimal extra = rif.getMezzo().getTipoMezzo().getConsumoExtraurbano();
					
					if ( urbano != null )
					{
						if ( urbano.compareTo( consumo ) > 0 )
						{
							ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.CONSUMO, Anomalia.Stato.RILEVATA );
							ano.setDescrizione( "Il consumo (" + consumo +") è inferiore al consumo urbano (" + urbano + ") per il tipo mezzo " + rif.getMezzo().getTipoMezzo().getDescrizione() );
							anos.add( ano );
						}
					}
					else
					{
						addErrorMessage( "Il tipo mezzo '" + rif.getMezzo().getTipoMezzo().getDescrizione() + "' non ha definito il consumo urbano" );
					}
					
					if ( extra != null )
					{
						if ( extra.compareTo( consumo ) < 0 )
						{
							ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.CONSUMO, Anomalia.Stato.RILEVATA );
							ano.setDescrizione( "Il consumo (" + consumo +") è superiore al consumo extraurbano (" + extra + ") per il tipo mezzo " + rif.getMezzo().getTipoMezzo().getDescrizione() );
							anos.add( ano );
						}
					}
					else
					{
						errors.put( "URB:" + rif.getMezzo().getTipoMezzo().getDescrizione(), "Il tipo mezzo '" + rif.getMezzo().getTipoMezzo().getDescrizione() + "' non ha definito il consumo extraurbano" );
					}
				}
				else
				{
					errors.put( "TIPO:" +rif.getMezzo().getTarga(), "Il mezzo " + rif.getMezzo().getTarga() + " non ha tipologia associata" );
				}
			}
			
			/**
			 * Verifico rettifica mezzo
			 */
			Mezzo mezzoRett = rif.getScontrino().getMezzoRettificato();
			if ( mezzoRett != null && !mezzoRett.equals( pred.getMezzo() ) )
			{
				//Rifornimento newPred = findPred( mezzoRett, rif.getData(), null );
				Rifornimento newPred = findPredNonRettificato( mezzoRett, rif.getData() );
				List<Anomalia> newAnos = verificaConsumo( rif, newPred );
				if ( !newAnos.isEmpty() )
				{
					AnomaliaUtils.cambiaStato( newAnos, Anomalia.Stato.CONSEGUENTE );
					anos.addAll( newAnos );
				}
				else if ( ano != null )
				{
					ano.setTipo( Anomalia.Stato.RETTIFICATA );
				}
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaFrequenzaPinCard( Rifornimento rif, Rifornimento pred )
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null && pred != null ) 
		{
			Date limite = DateUtils.addHour( rif.getData(), - minHour );
			if ( limite.before( pred.getData() ) )
			{
				ano = AnomaliaUtils.nuovaAnomalia( rif, pred, Anomalia.Tipo.FREQUENZA_PIN, Anomalia.Stato.RILEVATA );
				ano.setDescrizione( "Il rifornimento con codice operatore " + rif.getPinCard().getCodiceOperatore() + " è avvenuto entro le " + minHour + " ore dal precedente" );
				anos.add( ano );
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaPinCard( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			if ( !rif.getPinCard().isAttiva( rif.getData() ) ) {
				ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.PIN_NON_ATTIVO, Anomalia.Stato.RILEVATA );
				ano.setDescrizione( "La pin card con codice operatore " + rif.getPinCard().getCodiceOperatore() + " non risulta attivo alla data del " + 
										DateUtils.format( rif.getData(), "dd/MM/yyyy") );
				anos.add( ano );
			}
		}
		return anos;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected List<Anomalia> verificaCarta( Rifornimento rif ) throws Exception
	{
		List<Anomalia> anos = new ArrayList<Anomalia>();
		Anomalia ano = null;
		if ( rif != null )
		{
			if ( !rif.getCarta().isAttiva( rif.getData() ) ) {
				ano = AnomaliaUtils.nuovaAnomalia( rif, Anomalia.Tipo.CARTA_NON_ATTIVA, Anomalia.Stato.RILEVATA );
				ano.setDescrizione( "La carta " + rif.getCarta().getNumero() + " non risulta attiva alla data del " +
										DateUtils.format( rif.getData(), "dd/MM/yyyy"));
				anos.add( ano );
			}
		}
		return anos;
	}
}
