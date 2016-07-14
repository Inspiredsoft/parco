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

package it.redev.parco.job;

import it.inspired.utils.StringUtils;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.mezzi.CodificaCarburante;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Soccorso;
import it.redev.parco.model.rifornimenti.Distributore;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.model.rifornimenti.Scontrino;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;


public abstract class FileParserJob extends AbstractImportJob 
{
	private List<Rifornimento> 					rifornimenti;
	private List<Soccorso> 						soccorsi;
	private List<Scontrino>						scontrini;
	private HashMap<String, Mezzo> 				mezzi 		= new HashMap<String, Mezzo>();
	private HashMap<String, CartaCarburante>	carte 		= new HashMap<String, CartaCarburante>();
	private HashMap<String, Distributore> 		distributori= new HashMap<String, Distributore>();
	private HashMap<String, CodificaCarburante> carburanti  = new HashMap<String, CodificaCarburante>();
	private HashMap<String, PinCard>			pincards 	= new HashMap<String, PinCard>();
	
	//---------------------------------------------------------------------------------------------------------
	
	protected HashMap<String, Mezzo> getMezzi()
	{
		return mezzi;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected InputStream openStream() throws FileNotFoundException
	{
		return new FileInputStream( getFilePath() );
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void removeFileQuietly()
	{
		try 
		{
			File f = new File( getFilePath() );
			if ( f.canWrite() )
			{
				if ( !f.delete() )
				{
					addWarningMessage( "Impossibile rimuovere il file " + getInfo().getFile() );
				}
			}
		} 
		catch (Exception e) 
		{
		}
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void addRifornimento( Rifornimento rifornimento )
	{
		if ( rifornimenti == null )
		{
			rifornimenti = new ArrayList<Rifornimento>();
		}
		if ( rifornimento != null )
		{
			rifornimenti.add( rifornimento );
			verifyDate( rifornimento.getData() );
		}
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void addSoccorso( Soccorso soccorso )
	{
		if ( soccorsi == null )
		{
			soccorsi = new ArrayList<Soccorso>();
		}
		if ( soccorso != null )
		{
			soccorsi.add( soccorso );
		}
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Mezzo findMezzo( String targa )
	{
		targa = targa.toUpperCase();
		Mezzo mezzo = mezzi.get( targa );
		if ( mezzo == null )
		{
			List<Mezzo> lista = getEntityManager().createQuery( 
					"FROM Mezzo m " +
					"WHERE m.targa = :targa " +
					"AND   m.removed = false" )
					.setParameter( "targa", targa )
					.getResultList();
			if ( lista.size() > 0 )
			{
				mezzo = lista.get( 0 );
			}
			else
			{
				mezzo = new Mezzo();
				mezzo.setTarga( targa );
				mezzo.cambiaStato( Mezzo.Stati.IN_SERVIZIO, new Date(), getUser(), "Inserito da job di importazione" );
			}
			mezzi.put( targa, mezzo );
		}
		return mezzo;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Mezzo findMezzoByCodice( String codice )
	{
		Mezzo mezzo = mezzi.get( codice );
		if ( mezzo == null )
		{
			List<Mezzo> lista = getEntityManager().createQuery( 
					"FROM Mezzo m " +
					"WHERE m.codiceRadio = :codice " +
					"AND   m.removed = false" )
					.setParameter( "codice", codice )
					.getResultList();
			if ( lista.size() > 0 )
			{
				mezzo = lista.get( 0 );
				mezzi.put( codice, mezzo );
			}
		}
		return mezzo;
	}
	
	//---------------------------------------------------------------------------------------------------------

	protected int saveMezzi( boolean warn ) throws Exception
	{
		int num = 0;
		for ( Mezzo mezzo : mezzi.values() )
		{
			if ( !mezzo.isManaged() )
			{
				getEntityManager().persist( mezzo );
				if ( warn )
					addWarningMessage( "Il mezzo con targa " + mezzo.getTarga() + " non e' nell'archivio" );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void removeCartaCarburante( CartaCarburante carta )
	{
		String key = carta.getGestore() + ":" + carta.getNumero();
		carte.remove( key );
	}
	
	//---------------------------------------------------------------------------------------------------------
	
//	protected PinCard findPincardByBusta( String codiceBusta, Integer gestore ) {
//		String key = gestore + ":CB:" + codiceBusta;
//		PinCard pin = pincards.get( key );
//		if ( pin == null ) {
//			@SuppressWarnings("unchecked")
//			List<PinCard> lista = getEntityManager().createQuery( 
//					"FROM PinCard p " +
//					"WHERE p.codiceBusta = :codice " +
//					"AND   p.removed = false " +
//					"AND   p.gestore = :gestore " )
//					.setParameter( "codice", codiceBusta )
//					.setParameter( "gestore", gestore )
//					.getResultList();
//			if ( lista.size() > 0 )
//			{
//				pin = lista.get( 0 );
//			}
//			else
//			{
//				pin = new PinCard();
//				pin.setGestore( gestore );
//				pin.setCodiceBusta( codiceBusta );
//				pin.setTipo( Asset.Tipo.PINCARD );
//				pin.cambiaStato( CartaCarburante.Stato.ATTIVA, new Date(), getUser(), "Inserito da job di importazione" );
//			}
//			pincards.put( key, pin );
//		}
//		return pin;
//	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected PinCard findPincardByOperatore( String codiceOperatore, Integer gestore ) {
		String key = gestore + ":CO:" + codiceOperatore;
		PinCard pin = pincards.get( key );
		if ( pin == null ) {
			@SuppressWarnings("unchecked")
			List<PinCard> lista = getEntityManager().createQuery( 
					"FROM PinCard p " +
					"WHERE p.codiceOperatore = :codice " +
					"AND   p.removed = false " +
					"AND   p.gestore = :gestore " )
					.setParameter( "codice", codiceOperatore )
					.setParameter( "gestore", gestore )
					.getResultList();
			if ( lista.size() > 0 )
			{
				pin = lista.get( 0 );
			}
			else
			{
				pin = new PinCard();
				pin.setGestore( gestore );
				pin.setCodiceOperatore( codiceOperatore );
				pin.setTipo( Asset.Tipo.PINCARD );
				pin.cambiaStato( CartaCarburante.Stato.ATTIVA, new Date(), getUser(), "Inserito da job di importazione" );
			}
			pincards.put( key, pin );
		}
		return pin;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected CartaCarburante findCartaCarburante( String numero, Integer gestore )
	{
		String key = gestore + ":" + numero;
		CartaCarburante carta = carte.get( key );
		if ( carta == null )
		{
			List<CartaCarburante> lista = getEntityManager().createQuery( 
					"FROM CartaCarburante c " +
					"WHERE c.numero = :numero " +
					"AND   c.removed = false " +
					"AND   c.gestore = :gestore " )
					.setParameter( "numero", numero )
					.setParameter( "gestore", gestore )
					.getResultList();
			if ( lista.size() > 0 )
			{
				carta = lista.get( 0 );
			}
			else
			{
				carta = new CartaCarburante();
				carta.setGestore( gestore );
				carta.setNumero( numero );
				carta.setTipo( Asset.Tipo.FUEL_CARD );
				carta.cambiaStato( CartaCarburante.Stato.ATTIVA, new Date(), getUser(), "Inserito da job di importazione" );
			}
			carte.put( key, carta );
		}
		return carta;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int saveCarte( boolean warn ) throws Exception
	{
		int num = 0;
		for ( CartaCarburante carta : carte.values() )
		{
			if ( carta.getId() == null )
			{
				getEntityManager().persist( carta );
				if( warn)
					addWarningMessage( "La carta numero " + carta.getNumero() + " non e' nell'archivio" );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int savePincard(boolean warn) throws Exception {
		int num = 0;
		for ( PinCard pin : pincards.values() )
		{
			if ( pin.getId() == null )
			{
				getEntityManager().persist( pin );
				if( warn ) 
				{
					if ( StringUtils.isEmpty( pin.getCodiceBusta() ) ) 
					{
						addWarningMessage( "La pin card con codice operatore " + pin.getCodiceOperatore() + " non e' nell'archivio" );
					}
					else 
					{
						addWarningMessage( "La pin card con codice operatore " + pin.getCodiceOperatore() + 
								" e codice busta " + pin.getCodiceBusta() + " non e' nell'archivio" );
					}
				}
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Distributore findDistributore( Integer gestore, String indirizzo, String localita, String provincia, String cap )
	{
		String key = gestore + ":" + indirizzo + ":" + provincia + ":" + localita;
		Distributore dist = distributori.get( key );
		if ( dist == null )
		{
			Query query = getEntityManager().createQuery( 
					"FROM Distributore d " +
					"WHERE d.indirizzo = :indirizzo " +
					"AND   d.localita = :localita " +
					( provincia != null ? "AND d.provincia = :provincia " : "" ) +
					( cap != null ? "AND d.cap = :cap " : "" ) )
					.setParameter( "indirizzo", indirizzo )
					.setParameter( "localita", localita );
			
			if ( provincia != null )
			{
					query.setParameter( "provincia", provincia );
			}
			if ( cap != null )
			{
				query.setParameter( "cap", cap );
			}
			
			List<Distributore> lista = query.getResultList();
			
			if ( lista.size() > 0 )
			{
				dist = lista.get( 0 );
			}
			else
			{
				dist = new Distributore();
				dist.setIndirizzo( indirizzo );
				dist.setLocalita( localita );
				dist.setProvincia( provincia );
				dist.setGestore( gestore );
			}
			distributori.put( key, dist );
		}
		return dist;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int saveDistributori() throws Exception
	{
		int num = 0;
		for ( Distributore dist : distributori.values() )
		{
			if ( dist.getId() == null )
			{
				getEntityManager().persist( dist );
				addWarningMessage( "Il distributore " + dist.getIndirizzo() + " non e' nell'archivio" );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Integer findCarburante( String prodotto )
	{
		prodotto = prodotto.trim();
		CodificaCarburante carburante = carburanti.get( prodotto );
		if ( carburante == null )
		{
			List<CodificaCarburante> lista = getEntityManager().createQuery( 
					"FROM CodificaCarburante cc " +
					"WHERE cc.valore = :prodotto ")
					.setParameter( "prodotto", prodotto )
					.getResultList();
			if ( lista.size() > 0 )
			{
				carburante = lista.get( 0 );
			}
			else
			{
				carburante = new CodificaCarburante();
				carburante.setValore( prodotto );
				carburante.setConsentito( true );
			}
			carburanti.put( prodotto, carburante );
		}
		return carburante.getTipo();
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int saveCarburanti() throws Exception
	{
		int num = 0;
		for ( CodificaCarburante codifica : carburanti.values() )
		{
			if ( codifica.getId() == null )
			{
				getEntityManager().persist( codifica );
				addWarningMessage( "Il prodotto " + codifica.getValore() + " non e' nell'archivio" );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int saveRifornimenti()
	{
		int num = 0;
		for ( Rifornimento rif : rifornimenti )
		{
			if ( rif.getId() == null )
			{
				getEntityManager().persist( rif );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int saveSoccorsi()
	{
		int num = 0;
		for ( Soccorso soc : soccorsi )
		{
			if ( soc.getId() == null )
			{
				getEntityManager().persist( soc );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected int saveScontrini()
	{
		int num = 0;
		for ( Scontrino scontrino : scontrini )
		{
			if ( scontrino.getId() == null )
			{
				getEntityManager().persist( scontrino );
				num++;
			}
		}
		return num;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unused")
	protected void save() throws Exception
	{
		int ncarb 	= saveCarburanti();
		int ndist	= saveDistributori();
		int nmezzi	= saveMezzi( true );
		int ncarte 	= saveCarte( true );
		int npin 	= savePincard( true );
		int nrif 	= saveRifornimenti();
		int nscon	= saveScontrini();
		
		if ( ncarb > 0 ) {
			addInfoMessage( "Generati " + ncarb + " nuovi tipi di carburante" );
		}
		if ( ncarte > 0 ) {
			addInfoMessage( "Generate " + ncarte + " nuove carte" );
		}
		if ( ndist > 0 ) {
			addInfoMessage( "Generati " + ndist + " nuovi  distributori" );
		}
		if ( nmezzi > 0 ) {
			addInfoMessage( "Generati " + nmezzi + " nuovi mezzi" );
		}
		if ( npin > 0 ) {
			addInfoMessage( "Generati " + npin + " nuove pin card" );
		}
		addInfoMessage( "Importati " + nrif + " rifornimenti" );
	}
	
	//---------------------------------------------------------------------------------------------------------

	protected Scontrino nuovoScontrino( Rifornimento rif )
	{
		if ( scontrini == null )
		{
			scontrini = new ArrayList<Scontrino>();
		}
		Scontrino scontrino = new Scontrino();
		scontrino.setRifornimento( rif );
		scontrino.cambiaStato( Scontrino.Stato.DA_GIUSTIFICARE, new Date(), getUser(), "Inserito da job di importazione" );
		
		scontrini.add( scontrino );
		
		return scontrino;
	}
}
