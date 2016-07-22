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

import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.rifornimenti.Distributore;
import it.redev.parco.model.rifornimenti.RifornimentoAgip;
import it.inspired.utils.DateUtils;
import it.inspired.utils.StringUtils;
import it.redev.parco.utils.LineInterator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class AgipParserJob extends FileParserJob
{
	private static final String recordCode1 = "10";
	private static final String recordCode2 = "DT";
	private static final BigDecimal CENTO = new BigDecimal("100");
	
	//-------------------------------------------------------------------------------------
	
	public AgipParserJob(){}
	
	public AgipParserJob(ImportInfo info, User user) 
	{
		super.setInfo( info );
		setUser( user );
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException 
	{
		try 
		{
			addInfoMessage( "Caricamento file " +  getFilePath() );
			
			InputStream file = super.openStream();
			
			DataInputStream in = new DataInputStream( file );
			
			BufferedReader br = new BufferedReader( new InputStreamReader(in) );
			  
			String strLine = br.readLine();
			
			// La prima riga e' scartata
			while ( ( strLine = br.readLine() ) != null )   
			{
				RifornimentoAgip rif = parse( strLine );
				if ( rif != null )
				{
					super.addRifornimento( rif );
					super.nuovoScontrino( rif );
				}
				else
				{
					addWarningMessage( "Scartato record '" + strLine + "'" );
				}
			}
			
			super.save();
			
			file.close();
		} 
		catch (FileNotFoundException e) 
		{
			throw new DatabaseException( e );
		} 
		catch (IOException e) 
		{
			throw new DatabaseException( e );
		} 
		catch (Exception e)
		{
			throw new DatabaseException( e );
		}
		finally
		{
			super.removeFileQuietly();
		}
		
		enqueueLinkedJob();
	}
	
	//-------------------------------------------------------------------------------------

	private RifornimentoAgip parse(String str) throws Exception
	{
		RifornimentoAgip rif = new RifornimentoAgip();
		rif.setGestore( Gestore.AGIP );
		rif.setImportInfo( getInfo() );
		
		String extra = "";
		String value = "";
		String tmp = "";
		try
		{
			LineInterator line = new LineInterator( str );
			
			value = line.next( 2 );
			if ( !value.equals( recordCode1 ) )
			{
				return null;
			}
			extra +=  addExtra( value, line.getPosition() );
			
			value =  line.next( 8 );
			rif.setDataFattura( DateUtils.parse( value, "yyyyMMdd" ) );
			
			value = line.next( 36 );
			extra +=  addExtra( value, line.getPosition() );
			
			tmp = line.next( 21 );
						
			value = line.next( 2 );
			if ( !value.equals( recordCode2 ) )
			{	
				return null;
			}
			extra +=  addExtra( value, line.getPosition() );
			
			// Certo la carta dopo per evitare errori nel persist
			CartaCarburante carta = super.findCartaCarburante( tmp, Gestore.AGIP );
			rif.setCarta( carta );
			
			// Numero scontrino
			value = line.next( 8 );
			rif.setNumeroScontrino( value );
			
			// Data rifornimento
			value = line.next( 12 );
			rif.setData( DateUtils.parse( value, "yyyyMMddHHmm" ) );
			rif.setAaaammgg( DateUtils.format( rif.getData(), "yyyyMMdd" ) );
			
			// Ora ho la data rif. posso fare il confronto
			if ( !carta.isManaged() )
			{
				if (  rif.getData().before( carta.getStato().getDataStato() ) )
				{
					carta.getStato().setDataStato( rif.getData() );
				}
			}
			
			// Mezzo
			value = line.next( 7 );
			Mezzo mezzo = super.findMezzo( value );
			rif.setMezzo( mezzo );
			if ( carta.getMezzo() == null )
			{
				carta.setMezzo( mezzo );
			}
			if ( !mezzo.isManaged() )
			{
				if (  rif.getData().before( mezzo.getStato().getDataStato() ) )
				{
					mezzo.getStato().setDataStato( rif.getData() );
				}
			}
			
			// Spazi vuoti
			value = line.next( 20 ); 
			extra +=  addExtra( value, line.getPosition() );
			
			// Codice Operatore
			value = line.next( 4 );
			if ( StringUtils.isEmpty( value ) ) 
			{
				addErrorMessage( "Il rifornimento con scontrino " + rif.getNumeroScontrino() + " non ha associato alcuna pin card." );
			}
			else
			{
				PinCard pincard = super.findPincardByOperatore( value, Gestore.AGIP );
				rif.setPinCard( pincard );
				if ( !pincard.isManaged() ) 
				{
					if ( rif.getData().before( pincard.getStato().getDataStato() ) ) 
					{
						pincard.getStato().setDataStato( rif.getData() );
					}
				}
			}
			
			value = line.next( 7 );
			rif.setChilometraggio( Integer.parseInt( value ) );
			
			value = line.next( 18 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 10 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 8 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 11 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 9 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 32 );
			extra +=  addExtra( value, line.getPosition() );
			
			String indirizzo = line.next( 32 );
			String cap = line.next( 5 );
			value = line.next( 7 );
			extra +=  addExtra( value, line.getPosition() );
			String localita = line.next( 32 );
			Distributore dist = super.findDistributore( Gestore.AGIP, indirizzo, localita, null, cap );
			rif.setDistributore( dist );
			
			value = line.next( 10 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 32 );
			rif.setProdotto( value );
			Integer tipo = super.findCarburante( value );
			if ( tipo != null ) {
				rif.setTipoCarburante( tipo );
			}
			
			value = line.next( 6 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 10 );
			rif.setQuantita( new BigDecimal( value ).divide( CENTO ) );
			
			value = line.next( 1 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 85 );
			extra +=  addExtra( value, line.getPosition() );
			
			value = line.next( 12 );
			rif.setImporto( new BigDecimal( value ).divide( CENTO ) );
			
			value = line.next( 53 );
			extra +=  addExtra( value, line.getPosition() );
			
			rif.setExtra( "<extra>" + extra + "</extra>" );
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + value );
			throw e;
		}
		return rif;
	}
	
	//-------------------------------------------------------------------------------------
	
	private String addExtra( String str, int index )
	{
		return "<extra" + index + ">" + str + "</extra" + index + ">";
	}
}
