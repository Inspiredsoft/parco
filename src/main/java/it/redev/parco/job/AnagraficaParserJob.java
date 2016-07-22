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

import it.inspired.utils.DateUtils;
import it.inspired.utils.StringUtils;
import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.asset.GenereAsset;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.asset.MovimentoAsset;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.mezzi.AssegnazioneMezzo;
import it.redev.parco.model.mezzi.CompagniaAssicurazione;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Polizza;
import it.redev.parco.model.mezzi.TipoMezzo;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;
import it.redev.parco.model.rifornimenti.TipoCarburante;
import it.redev.parco.utils.ExcelUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class AnagraficaParserJob extends FileParserJob
{
	private ImportService service;
	
	private List<String> mezziImportati = new ArrayList<String>();
	private List<String> postazioniImportate = new ArrayList<String>();
	private List<String> personeImportate = new ArrayList<String>();
	
	private static final String MEZZI_SHEET 		= "mezzi";
	private static final String TIPO_MEZZI_SHEET 	= "tipi mezzo";
	private static final String CARTE_SHEET 		= "carte";
	private static final String POLIZZE_SHEET 		= "polizze";
	private static final String POSTAZIONI_SHEET	= "postazioni";
	private static final String PERSONE_SHEET		= "persone";
	private static final String DOTAZIONI_SHEET		= "dotazioni";
	private static final String PIN_SHEET			= "pin";
	
	//-------------------------------------------------------------------------------------
	
	public AnagraficaParserJob(){}
	
	public AnagraficaParserJob(ImportInfo info, User user) 
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
			service = new ImportService( getEntityManager(), getUser() );
			
			addInfoMessage( "Caricamento file " +  getFilePath() );
			
			InputStream file = super.openStream();
			
			HSSFWorkbook workbook = new HSSFWorkbook( file );
			
			// POSTAZIONI
			Integer parsed = 0;
			int rows, tot;
			Sheet sheet = workbook.getSheet( POSTAZIONI_SHEET ); 
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovate " + rows + " righe nel file postazioni" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if ( row != null && parsePostazioni( row ) );
						parsed++;
				}
				tot = saveOc();
				if ( tot > 0 )
					addInfoMessage( "Create " + tot + " nuove postazioni e/o aree e/o province" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornate " + (parsed-tot) + " postazioni e/o aree e/o province" );
				commit();
			}
			
			// PERSONE
			parsed = 0;
			sheet = workbook.getSheet( PERSONE_SHEET ); 
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovate " + rows + " persone nel file" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if ( row != null && parsePersona( row ) );
						parsed++;
				}
				tot = savePersone();
				if ( tot > 0 )
					addInfoMessage( "Create " + tot + " nuove persone" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornate " + (parsed-tot) + " persone" );
				commit();
			}
						
			// TIPO MEZZO
			parsed = 0;
			sheet = workbook.getSheet( TIPO_MEZZI_SHEET ); 
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovati " + rows + " tipi mezzo nel file" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if ( row != null && parseTipoMezzo( row ) );
						parsed++;
				}
				tot = saveTipoMezzo();
				if ( tot > 0 )
					addInfoMessage( "Creati " + tot + " nuovi tipi mezzo" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornati " + (parsed-tot) + " tipi mezzo" );
				commit();
			}
			
			// MEZZO
			parsed = 0;
			sheet = workbook.getSheet( MEZZI_SHEET ); 
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovati " + rows + " mezzi" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if ( row != null && parseMezzo( row ) )
						parsed++;
				}
				tot = saveMezzi();
				if ( tot > 0 )
					addInfoMessage( "Creati " + tot + " nuovi mezzi" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornati " + (parsed-tot) + " mezzi" );
				commit();
			}
			
			// CARTE
			parsed = 0;
			sheet = workbook.getSheet( CARTE_SHEET ); 
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovate " + rows + " carte" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if( row != null && parseCarta( row ) ) 
						parsed++;
				}
				tot = saveCarte( false );
				if ( tot > 0 )
					addInfoMessage( "Create " + tot + " nuove carte" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornate " + (parsed-tot) + " carte" );
				commit();
			}

			// POLIZZE
			parsed = 0;
			sheet = workbook.getSheet( POLIZZE_SHEET );
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovate " + rows + " polizze" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if( row != null && parsePolizza( row ) ) 
						parsed++;
				}
				saveCompagnie();
				tot = savePolizze();
				if ( tot > 0 )
					addInfoMessage( "Create " + tot + " nuove polizze" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornate " + (parsed-tot) + " polizze" );
				commit();
			}
			
			// DOTAZIONI
			parsed = 0;
			sheet = workbook.getSheet( DOTAZIONI_SHEET );
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovate " + rows + " dotazioni" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if( row != null && parseDotazione( row ) ) 
						parsed++;
				}
				tot = saveDotazioni();
				if ( tot > 0 )
					addInfoMessage( "Create " + tot + " nuove dotazioni" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornate " + (parsed-tot) + " dotazioni" );
				commit();
			}
			
			// PIN CARD
			parsed = 0;
			sheet = workbook.getSheet( PIN_SHEET );
			if ( sheet != null )
			{
				rows = sheet.getLastRowNum();
				addInfoMessage( "Trovati " + rows + " pin card" );						
				for ( int i = 1; i <= rows; i++ )
				{
					Row row = sheet.getRow( i );
					if( row != null && parsePincard( row ) ) 
						parsed++;
				}
				tot = savePincard( true );
				if ( tot > 0 )
					addInfoMessage( "Create " + tot + " nuove pin card" );
				if ( (parsed-tot) > 0 )
					addInfoMessage( "Aggiornate " + (parsed-tot) + " pin card" );
				commit();
			}
			
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
	}
	
	
	
	//-------------------------------------------------------------------------------------

	private boolean parseTipoMezzo( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		try
		{
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + TIPO_MEZZI_SHEET + "' verrà ignorta in quanto ha la descrizione del tipo mezzo non definita." );
				return false;
			}
			TipoMezzo tipo = service.findTipoMezzo( ExcelUtils.getCellValue( cell ) );
			
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				tipo.setSerbatoio( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			}
			
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				tipo.setConsumoUrbano( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			}
			
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				tipo.setConsumoExtraurbano( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			}
			
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				Integer carb = TipoCarburante.parse( ExcelUtils.getCellValue( cell ) );
				if ( carb != null )
				{
					tipo.setTipoCarburante( carb );
				}
				else
				{
					addErrorMessage( "Tipo carburante '" + ExcelUtils.getCellValue( cell ) + "' non riconosciuto" );
				}
			}
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
	
	//-------------------------------------------------------------------------------------
	
	private boolean parseMezzo( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		try
		{
			// TARGA
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + MEZZI_SHEET + "' verrà ignorta in quanto ha la targa del mezzo non definita." );
				return false;
			}
			Mezzo mezzo = super.findMezzo( ExcelUtils.getCellValue( cell ) );
			mezziImportati.add( mezzo.getTarga() );
			
			// CODICE RADIO
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				String codice = StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' );
				Mezzo mcodice = findMezzoByCodice( codice );
				if ( mcodice != null )
				{
					if ( !mcodice.equals( mezzo ) )
					{
						addErrorMessage( "Il codice radio " + codice + " indicato per il mezzo " + mezzo.getTarga() + " è associato anche al mezzo " + mcodice.getTarga() );
					}
				}
				mezzo.setCodiceRadio( codice );
			}
			
			// SCAD BOLLO
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				mezzo.setScadenzaBollo( ExcelUtils.getAsDate( cell , "dd/MM/yyyy" ) );
			}
			
			// SCAD REVISIONE
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				mezzo.setScadenzaRevisione( ExcelUtils.getAsDate( cell , "dd/MM/yyyy" ) );
			}
			
			// NOTE
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				mezzo.setNote( ExcelUtils.getCellValue( cell ) );
			}
			
			// TIPO MEZZO
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + MEZZI_SHEET + "' verrà ignorta in quanto ha il tipo mezzo non definito." );
				getMezzi().remove( mezzo.getTarga() );
				mezziImportati.remove( mezzo.getTarga() );
				return false;
			}
			TipoMezzo tipo = service.findTipoMezzo( ExcelUtils.getCellValue( cell ) );
			mezzo.setTipoMezzo( tipo );
			
			// ASSEGNAZIONE
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				String npos = ExcelUtils.getCellValue( cell );
				Postazione pos = service.findPostazione( npos );
				if ( pos == null && !postazioniImportate.contains( npos ) )
				{
					addErrorMessage( "La postazione indicata nella riga " + row.getRowNum() + " del foglio '" + MEZZI_SHEET + "' non è stata trovata o non è unica" );
					col++;
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataAss = ExcelUtils.getAsDate( cell , "dd/MM/yyyy" );
					
					AssegnazioneMezzo last = mezzo.getLastAssegnazione();
					if ( last == null || !last.getPostazione().equals( pos ))
					{
						AssegnazioneMezzo am = new AssegnazioneMezzo(); 
						am.setPostazione( pos );
						am.setStatoSerbatoio( AssegnazioneMezzo.StatoSerbatoio.PIENO );
						am.setDataInizio( dataAss );
						am.setMezzo( mezzo );
						
						if ( last != null && !am.after( last ) )
						{
							addErrorMessage( "La data sassegnazione " + DateUtils.format( dataAss, "dd/MM/yyyy") + " indicata nella riga " + row.getRowNum() + " del foglio '" + MEZZI_SHEET + "' " +
									"è precedente o uguale alla data assegnazione corrente " + DateUtils.format( last.getDataInizio(), "dd/MM/yyyy") );
						}
						else
						{
							mezzo.assegna( am );
						}
					}
				}
			}
			else
			{
				col++;
			}
			
			// STATO MEZZO
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				Integer stato = Mezzo.Stati.parse( ExcelUtils.getCellValue( cell ) );
				
				if ( stato == null )
				{
					addErrorMessage( "Stato mezzo '" + ExcelUtils.getCellValue( cell ) + "' non riconosciuto" );
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataStato = new Date();
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						dataStato = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
					}
					
					cell = row.getCell( col++ );
					String noteStato = "";
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						noteStato = ExcelUtils.getCellValue( cell );
					}
				
					// se il mezzo e' nuovo aggiorno lo stato
					// se il mezzo e' presente nel db aggiorno lo stato solo se differente da quello attuale 
					if ( !mezzo.isManaged() )
					{
						mezzo.getStato().setStato( stato );
						mezzo.getStato().setDataStato( dataStato );
						mezzo.getStato().setNoteStato( noteStato );
						mezzo.getStato().setUtenteStato( getUser() );
					}
					else if ( !mezzo.getStato().getStato().equals( stato ) )
					{
						if ( dataStato.after( mezzo.getStato().getDataStato() ) )
						{
							mezzo.cambiaStato( stato, dataStato, getUser(), noteStato );
						}
						else
						{
							addErrorMessage( "Lo stato del mezzo " + mezzo.getTarga() + " non può essere modificato perchè " +
									"la data stato indicata " + DateUtils.format( dataStato, "dd/MM/yyyy") + 
									" non è successiva alla data stato attuale " + DateUtils.format( mezzo.getStato().getDataStato(), "dd/MM/yyyy") );
						}
					}
				}
			}
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	private boolean parseCarta( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		try
		{
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' verrà ignorta in quanto ha la targa del mezzo non definita." );
				return false;
			}
			Mezzo mezzo = super.findMezzo( ExcelUtils.getCellValue( cell ) );
			if ( !mezzo.isManaged() && !mezziImportati.contains( mezzo.getTarga() ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' verrà ignorta in quanto il mezzo non è presente nè nel sistema nè nel foglio '" + MEZZI_SHEET + "'." );
				getMezzi().remove( mezzo );
				return false;
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' verrà ignorta in quanto ha il numero carta non definito." );
				return false;
			}
			String numero = StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' );
			
			String id = null;
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				id = StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' );
			}
			
			String pin = null;
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				pin = StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' );
			}
			
			Date scadenza = null;
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				scadenza = ExcelUtils.getAsDate( cell, getConfigManager().getShortDateFormat() );
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' verrà ignorta in quanto ha il gestore non definito." );
				return false;
			}
			Integer gestore = Gestore.parse( ExcelUtils.getCellValue( cell ) );
			if ( gestore == null )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' verrà ignorta in quanto il gestore '" + ExcelUtils.getCellValue( cell ) + "' è sconosciuto." );
				return false;
			}
			
			CartaCarburante carta = findCartaCarburante( numero, gestore );
			if ( !carta.isManaged() )
			{
				carta.setMezzo( mezzo );
			}
			else
			{
				if ( !carta.getMezzo().equals( mezzo ) )
				{
					addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' verrà ignorta in quanto " +
							"il mezzo indicato " + mezzo.getTarga() + " è diverso da quello sul sistema " + carta.getMezzo().getTarga() );
					removeCartaCarburante( carta );
					return false;
				}
			}
			
			// Setto il pin e la scedenza letti prima
			if ( pin != null ) carta.setPinCode( pin );
			if ( scadenza != null ) carta.setScadenza( scadenza );
			if ( id != null ) carta.setIdentificativo( id );
			
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				carta.setNote( ExcelUtils.getCellValue( cell ) );
			}
			
			// STATO
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				Integer stato = CartaCarburante.Stato.parse( ExcelUtils.getCellValue( cell ) );
				if ( stato == null )
				{
					addErrorMessage( "Stato carta carburante '" + ExcelUtils.getCellValue( cell ) + "' non riconosciuto" );
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataStato = new Date();
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						dataStato = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
					}
					
					cell = row.getCell( col++ );
					String noteStato = "";
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						noteStato = ExcelUtils.getCellValue( cell );
					}
					// se la carta e' nuovo aggiorno lo stato
					// se la carta e' presente nel db aggiorno lo stato solo se differente da quello attuale 
					if ( !carta.isManaged() )
					{
						carta.getStato().setStato( stato );
						carta.getStato().setDataStato( dataStato );
						carta.getStato().setNoteStato( noteStato );
						carta.getStato().setUtenteStato( getUser() );
					}
					else if ( !carta.getStato().getStato().equals( stato ) )
					{
						if ( dataStato.after( carta.getStato().getDataStato() ) )
						{
							carta.cambiaStato( stato, dataStato, getUser(), noteStato );
						}
						else
						{
							addErrorMessage( "Lo stato della carta " + carta.getNumero() + " non può essere modificato perchè " +
									"la data stato indicata " + DateUtils.format( dataStato, "dd/MM/yyyy") + 
									" non è successiva alla data stato attuale " + DateUtils.format( carta.getStato().getDataStato(), "dd/MM/yyyy") );
						}
					}
				}
			}
			else
			{
				col++;
				col++;
			}
			
			// ASSEGNAZIONE
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				String matricola = ExcelUtils.getCellValue( cell );
				Persona pers = service.findPersona( matricola );
				if ( !pers.isManaged() && !personeImportate.contains( matricola ) )
				{
					addErrorMessage( "La persona indicata nella riga " + row.getRowNum() + " del foglio '" + CARTE_SHEET + "' non è stata trovata o non è unica" );
					col++;
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataAss = new Date();
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						dataAss = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
					}
					dataAss = DateUtils.toMorning( dataAss );
					
					cell = row.getCell( col++ );
					String noteAss = "";
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						noteAss = ExcelUtils.getCellValue( cell );
					}
					
					MovimentoAsset last = carta.getLastMovimentoAsset();
					
					if ( last == null )
					{
						last = new MovimentoAsset();
						last.setTipo( MovimentoAsset.Tipo.CONSEGNA );
						last.setData( dataAss );
						last.setNote( noteAss );
						last.setAsset( carta );
						last.setAssegnatario( pers );
						carta.getMovimenti().add( last );
					}
					else if ( !last.getAssegnatario().equals( pers ) )
					{
						if ( dataAss.after( last.getData() ) )
						{
							if ( last.isConsegnata() )
							{
								MovimentoAsset ric = new MovimentoAsset();
								ric.setTipo( MovimentoAsset.Tipo.RICONSEGNA );
								ric.setData( DateUtils.toMidnight( DateUtils.addDay( dataAss, -1 ) ) );
								ric.setAsset( carta );
								ric.setAssegnatario( last.getAssegnatario() );
								carta.getMovimenti().add( ric );
							}
							MovimentoAsset cons = new MovimentoAsset();
							cons.setTipo( MovimentoAsset.Tipo.CONSEGNA );
							cons.setData( dataAss );
							cons.setNote( noteAss );
							cons.setAsset( carta );
							cons.setAssegnatario( pers );
							carta.getMovimenti().add( cons );
						}
						else
						{
							addErrorMessage( "L'assegnazione della carta " + carta.getNumero() + " non può essere modificata perchè " +
									"la data assegnazione indicata " + DateUtils.format( dataAss, "dd/MM/yyyy") + 
									" non è successiva alla data assegnazione attuale " + DateUtils.format( last.getData(), "dd/MM/yyyy") );
						}
					}
				}
			}
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	private boolean parsePincard( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		Cell	cellCb	= null;
		Cell	cellCo	= null;
		int 	col		= 0;
		try
		{
			// Codice Busta, Codice Operatore
			cellCb = row.getCell( col++ );
			cellCo = row.getCell( col++ );
			
			// Gestore
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PIN_SHEET + "' verrà ignorta in quanto ha il gestore non valorizzato." );
				return false;
			}
			Integer gestore = Gestore.parse( ExcelUtils.getCellValue( cell ) );
			if ( gestore == null )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PIN_SHEET + "' verra' ignorta in quanto il gestore '" + ExcelUtils.getCellValue( cell ) + "' è sconosciuto." );
				return false;
			}
			
			if ( ExcelUtils.isCellEmpty( cellCb ) && Gestore.AGIP.equals( gestore ) )
			{
				// Agip non ammette codici busta vuoti
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PIN_SHEET + "' per gestore AGIP verrà ignorta in quanto ha il codice busta non valorizzato." );
				return false;
			}
			String codiceBusta = ExcelUtils.getCellValue( cellCb );
			
			// Codice Operatore non puo' essere vuoto
			if ( ExcelUtils.isCellEmpty( cellCo ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PIN_SHEET + "' verrà ignorta in quanto ha il codice operatore non valorizzato." );
				return false;
			}
			String codiceOperatore = ExcelUtils.getCellValue( cellCo );
			
			PinCard pincard = super.findPincardByOperatore( codiceOperatore, gestore );
			pincard.setCodiceBusta( codiceBusta );
			
			// Note
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				pincard.setNote( ExcelUtils.getCellValue( cell ) );
			}
			
			// STATO
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
			Integer stato = PinCard.Stato.parse( ExcelUtils.getCellValue( cell ) );
			if ( stato == null )
			{
				addErrorMessage( "Stato pin card '" + ExcelUtils.getCellValue( cell ) + "' non riconosciuto" );
			}
			else
			{
				cell = row.getCell( col++ );
				Date dataStato = new Date();
				if ( !ExcelUtils.isCellEmpty( cell ) )
				{
					dataStato = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
				}
				
				cell = row.getCell( col++ );
				String noteStato = "";
				if ( !ExcelUtils.isCellEmpty( cell ) )
				{
					noteStato = ExcelUtils.getCellValue( cell );
				}
					// se la pin card e' nuovo aggiorno lo stato
					// se la pin card e' presente nel db aggiorno lo stato solo se differente da quello attuale 
					if ( !pincard.isManaged() )
					{
						pincard.getStato().setStato( stato );
						pincard.getStato().setDataStato( dataStato );
						pincard.getStato().setNoteStato( noteStato );
						pincard.getStato().setUtenteStato( getUser() );
					}
					else if ( !pincard.getStato().getStato().equals( stato ) )
					{
						if ( dataStato.after( pincard.getStato().getDataStato() ) )
						{
							pincard.cambiaStato( stato, dataStato, getUser(), noteStato );
						}
						else
						{
							addErrorMessage( "Lo stato della pin card con codice operatore " + pincard.getCodiceOperatore() + " non può essere modificato perchè " +
									"la data stato indicata " + DateUtils.format( dataStato, "dd/MM/yyyy") + 
									" non è successiva alla data stato attuale " + DateUtils.format( pincard.getStato().getDataStato(), "dd/MM/yyyy") );
						}
					}
				}
			}
			else
			{
				col++;
				col++;
			}
			
			// ASSEGNAZIONE
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				String matricola = ExcelUtils.getCellValue( cell );
				Persona pers = service.findPersona( matricola );
				if ( !pers.isManaged() && !personeImportate.contains( matricola ) )
				{
					addErrorMessage( "La persona indicata nella riga " + row.getRowNum() + " del foglio '" + PIN_SHEET + "' non è stata trovata o non è unica" );
					col++;
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataAss = new Date();
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						dataAss = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
					}
					dataAss = DateUtils.toMorning( dataAss );
					
					cell = row.getCell( col++ );
					String noteAss = "";
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						noteAss = ExcelUtils.getCellValue( cell );
					}
					
					MovimentoAsset last = pincard.getLastMovimentoAsset();
					
					if ( last == null )
					{
						last = new MovimentoAsset();
						last.setTipo( MovimentoAsset.Tipo.CONSEGNA );
						last.setData( dataAss );
						last.setNote( noteAss );
						last.setAsset( pincard );
						last.setAssegnatario( pers );
						pincard.getMovimenti().add( last );
					}
					else if ( !last.getAssegnatario().equals( pers ) )
					{
						if ( dataAss.after( last.getData() ) )
						{
							if ( last.isConsegnata() )
							{
								MovimentoAsset ric = new MovimentoAsset();
								ric.setTipo( MovimentoAsset.Tipo.RICONSEGNA );
								ric.setData( DateUtils.toMidnight( DateUtils.addDay( dataAss, -1 ) ) );
								ric.setAsset( pincard );
								ric.setAssegnatario( last.getAssegnatario() );
								pincard.getMovimenti().add( ric );
							}
							MovimentoAsset cons = new MovimentoAsset();
							cons.setTipo( MovimentoAsset.Tipo.CONSEGNA );
							cons.setData( dataAss );
							cons.setNote( noteAss );
							cons.setAsset( pincard );
							cons.setAssegnatario( pers );
							pincard.getMovimenti().add( cons );
						}
						else
						{
							addErrorMessage( "L'assegnazione della pin card " + pincard.getCodiceOperatore() + " non può essere modificata perchè " +
									"la data assegnazione indicata " + DateUtils.format( dataAss, "dd/MM/yyyy") + 
									" non è successiva alla data assegnazione attuale " + DateUtils.format( last.getData(), "dd/MM/yyyy") );
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	private boolean parseDotazione( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		try
		{
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + DOTAZIONI_SHEET + "' verrà ignorta in quanto ha la targa del mezzo non è definita." );
				return false;
			}
			Mezzo mezzo = super.findMezzo( ExcelUtils.getCellValue( cell ) );
			if ( !mezzo.isManaged() && !mezziImportati.contains( mezzo.getTarga() ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + DOTAZIONI_SHEET + "' verrà ignorta in quanto il mezzo non è presente nè nel sistema nè nel foglio '" + MEZZI_SHEET + "'." );
				getMezzi().remove( mezzo );
				return false;
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + DOTAZIONI_SHEET + "' verrà ignorta in quanto ha il tipo dotazione non definito." );
				return false;
			}
			//Integer genere = Asset.Tipo.parse( ExcelUtils.getCellValue( cell ) );
			GenereAsset genere = service.findGenereDotazioneMezzo( ExcelUtils.getCellValue( cell ) );
			if ( genere == null )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + DOTAZIONI_SHEET + "' verrà ignorta in quanto il genere/tipo '" + ExcelUtils.getCellValue( cell ) + "' non è stato riconosciuto." );
				return false;
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + DOTAZIONI_SHEET + "' verrà ignorta in quanto ha la matricola non definita." );
				return false;
			}
			String matricola = ExcelUtils.getCellValue( cell );
			
			DotazioneMezzo dotazione = service.findDotazioneMezzo( matricola, genere, mezzo );
			
			// STATO
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				Integer stato = DotazioneMezzo.Stato.parse( ExcelUtils.getCellValue( cell ) );
				if ( stato == null )
				{
					addErrorMessage( "Stato dotazione '" + ExcelUtils.getCellValue( cell ) + "' non riconosciuto" );
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataStato = new Date();
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						dataStato = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
					}
					dataStato = DateUtils.toMorning( dataStato );
					
					cell = row.getCell( col++ );
					String noteStato = "";
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						noteStato = ExcelUtils.getCellValue( cell );
					}
					// se la dotazione e' nuova aggiorno lo stato
					// se la dotazione e' presente nel db aggiorno lo stato solo se differente da quello attuale 
					if ( !dotazione.isManaged() )
					{
						dotazione.getStato().setStato( stato );
						dotazione.getStato().setDataStato( dataStato );
						dotazione.getStato().setNoteStato( noteStato );
						dotazione.getStato().setUtenteStato( getUser() );
					}
					else if ( !dotazione.getStato().getStato().equals( stato ) )
					{
						if ( dataStato.after( dotazione.getStato().getDataStato() ) )
						{
							dotazione.cambiaStato( stato, dataStato, getUser(), noteStato );
						}
						else
						{
							addErrorMessage( "Lo stato della dotazione " + dotazione.getMatricola() + " non può essere modificato perchè " +
									"la data stato indicata " + DateUtils.format( dataStato, "dd/MM/yyyy") + 
									" non è successiva alla data stato attuale " + DateUtils.format( dotazione.getStato().getDataStato(), "dd/MM/yyyy") );
						}
					}
				}
			}
			else
			{
				col++;
				col++;
			}
			
			// ASSEGNAZIONE
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				matricola = ExcelUtils.getCellValue( cell );
				Persona pers = service.findPersona( matricola );
				if ( !pers.isManaged() && !personeImportate.contains( matricola ) )
				{
					addErrorMessage( "La persona indicata nella riga " + row.getRowNum() + " del foglio '" + DOTAZIONI_SHEET + "' non è stata trovata o non è unica" );
					col++;
				}
				else
				{
					cell = row.getCell( col++ );
					Date dataAss = new Date();
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						dataAss = ExcelUtils.getAsDate( cell, "dd/MM/yyyy" );
					}
					dataAss = DateUtils.toMorning( dataAss );
					
					cell = row.getCell( col++ );
					String noteAss = "";
					if ( !ExcelUtils.isCellEmpty( cell ) )
					{
						noteAss = ExcelUtils.getCellValue( cell );
					}
					
					MovimentoAsset last = dotazione.getLastMovimentoAsset();
					
					if ( last == null )
					{
						last = new MovimentoAsset();
						last.setTipo( MovimentoAsset.Tipo.CONSEGNA );
						last.setData( dataAss );
						last.setNote( noteAss );
						last.setAsset( dotazione );
						last.setAssegnatario( pers );
						dotazione.getMovimenti().add( last );
					}
					else if ( !last.getAssegnatario().equals( pers ) )
					{
						if ( dataAss.after( last.getData() ) )
						{
							if ( last.isConsegnata() )
							{
								MovimentoAsset ric = new MovimentoAsset();
								ric.setTipo( MovimentoAsset.Tipo.RICONSEGNA );
								ric.setData( DateUtils.toMidnight( DateUtils.addDay( dataAss, -1 ) ) );
								ric.setAsset( dotazione );
								ric.setAssegnatario( last.getAssegnatario() );
								dotazione.getMovimenti().add( ric );
							}
							MovimentoAsset cons = new MovimentoAsset();
							cons.setTipo( MovimentoAsset.Tipo.CONSEGNA );
							cons.setData( dataAss );
							cons.setNote( noteAss );
							cons.setAsset( dotazione );
							cons.setAssegnatario( pers );
							dotazione.getMovimenti().add( cons );
						}
						else
						{
							addErrorMessage( "L'assegnazione della dotazione " + dotazione.getMatricola() + " non può essere modificata perchè " +
									"la data assegnazione indicata " + DateUtils.format( dataAss, "dd/MM/yyyy") + 
									" non è successiva alla data assegnazione attuale " + DateUtils.format( last.getData(), "dd/MM/yyyy") );
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
		
	
	//---------------------------------------------------------------------------------------------------------
	
	private boolean parsePolizza( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		try
		{
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto ha la targa del mezzo non è definita." );
				return false;
			}
			Mezzo mezzo = super.findMezzo( ExcelUtils.getCellValue( cell ) );
			if ( !mezzo.isManaged() && !mezziImportati.contains( mezzo.getTarga() ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto il mezzo non è presente nè nel sistema nè nel foglio '" + MEZZI_SHEET + "'." );
				getMezzi().remove( mezzo );
				return false;
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto la compagnia non è definita." );
				return false;
			}
			CompagniaAssicurazione comp = service.findCompagnia( ExcelUtils.getCellValue( cell ) );
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto il numero polizza non è definito." );
				return false;
			}
			Polizza polizza = service.findPolizza( ExcelUtils.getCellValue( cell ), comp );
			
			if ( polizza.isManaged() )
			{
				if ( !polizza.getMezzo().equals( mezzo ) )
				{
					addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta " +
							"in quanto il numero polizza " + polizza.getNumero() + " della compagnia "+ comp.getNome() + " è già presente sul sistema " +
									"associata al mezzo " + polizza.getMezzo().getTarga() + "." );
					service.removePolizza( polizza );
					return false;
				}
			}
			else
			{
				polizza.setMezzo( mezzo );
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto la data decorrenza non è definita." );
				service.removePolizza( polizza );
				return false;
			}
			polizza.setDataDecorrenza( ExcelUtils.getAsDate( cell, "dd/MM/yyyy" ) );
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto il la data scadenza non è definita." );
				service.removePolizza( polizza );
				return false;
			}
			polizza.setDataScadenza( ExcelUtils.getAsDate( cell, "dd/MM/yyyy" ) );
			
			if ( polizza.getDataDecorrenza().after( polizza.getDataScadenza() ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POLIZZE_SHEET + "' verrà ignorta in quanto il la data decorrenza è successiva alla data scadenza." );
				service.removePolizza( polizza );
				return false;
			}
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	private boolean parsePostazioni( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		String posName, centroCosto = null, areaName, provName;
		try
		{
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POSTAZIONI_SHEET + "' verrà ignorta in quanto la postazione non è definita." );
				return false;
			}
			posName = ExcelUtils.getCellValue( cell );
			
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				centroCosto = ExcelUtils.getCellValue( cell );
			}
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POSTAZIONI_SHEET + "' verrà ignorta in quanto l'area non è definita." );
				return false;
			}
			areaName = ExcelUtils.getCellValue( cell );
			
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + POSTAZIONI_SHEET + "' verrà ignorta in quanto la provincia non è definita." );
				return false;
			}
			provName = ExcelUtils.getCellValue( cell );

			Provincia prov = service.findProvincia( provName );
			Area area = service.findArea( areaName, prov );
			Postazione post = service.findPostazione( posName, area );
			post.setCentroCosto( centroCosto );
			postazioniImportate.add( post.getNome() );
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	private boolean parsePersona( Row row ) throws Exception
	{
		Cell 	cell 	= null;
		int 	col		= 0;
		String matricola, nome, cognome, qualifica = null;
		try
		{
			// Matricola
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PERSONE_SHEET + "' verrà ignorta in quanto la matricola non è definita." );
				return false;
			}
			matricola = ExcelUtils.getCellValue( cell );
			
			// Cognome
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PERSONE_SHEET + "' verrà ignorta in quanto il cognome non è definito." );
				return false;
			}
			cognome = ExcelUtils.getCellValue( cell );
			
			// Nome
			cell = row.getCell( col++ );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "La riga " + row.getRowNum() + " del foglio '" + PERSONE_SHEET + "' verrà ignorta in quanto il nome non è definito." );
				return false;
			}
			nome = ExcelUtils.getCellValue( cell );
			
			// Qualifica
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				qualifica = ExcelUtils.getCellValue( cell ); 
			}
			
			// Postazione
			Postazione pos = null;
			cell = row.getCell( col++ );
			if ( !ExcelUtils.isCellEmpty( cell ) )
			{
				String npos = ExcelUtils.getCellValue( cell );
				pos = service.findPostazione( npos );
				if ( pos == null && !postazioniImportate.contains( npos ) )
				{
					addErrorMessage( "La postazione indicata nella riga " + row.getRowNum() + " del foglio '" + PERSONE_SHEET + "' non è stata trovata o non è unica, la persona sarà inserita senza assegnazione" );
				}
			}
			
			Persona pers = service.findPersona( matricola );
			pers.setNome( nome );
			pers.setCognome(cognome);
			pers.setQualifica( qualifica );
			pers.setPostazione( pos );
			personeImportate.add( matricola );
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return true;
	}
		
	//---------------------------------------------------------------------------------------------------------

	protected int saveMezzi() throws Exception
	{
		int num = 0;
		for ( Mezzo mezzo : getMezzi().values() )
		{
			if ( !mezzo.isManaged() )
			{
				getEntityManager().persist( mezzo );
				num++;
			}
			else
			{
				getEntityManager().merge( mezzo );
			}
		}
		return num;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int saveTipoMezzo()
	{
		int tot = 0;
		for ( TipoMezzo tipo : service.getTipiMezzo().values() )
		{
			if ( !tipo.isManaged() )
			{
				getEntityManager().persist( tipo );
				tot++;
			}
			else
			{
				getEntityManager().merge( tipo );
			}
		}
		return tot;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int saveCompagnie()
	{
		int tot = 0;
		for ( CompagniaAssicurazione comp : service.getCompagnie().values() )
		{
			if ( !comp.isManaged() )
			{
				getEntityManager().persist( comp );
				tot++;
			}
		}
		return tot;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int savePolizze()
	{
		int tot = 0;
		for ( Polizza polizza : service.getPolizze().values() )
		{
			if ( !polizza.getCompagnia().isManaged() )
			{
				getEntityManager().persist( polizza.getCompagnia() );
			}
			if ( !polizza.isManaged() )
			{
				getEntityManager().persist( polizza );
				tot++;
			}
		}
		return tot;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int savePersone()
	{
		int tot = 0;
		for ( Persona persona : service.getPersone().values() )
		{
			if ( !persona.isManaged() )
			{
				getEntityManager().persist( persona );
				tot++;
			}
		}
		return tot;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int saveDotazioni()
	{
		int tot = 0;
		for ( DotazioneMezzo dotazione : service.getDotazioni().values() )
		{
			if ( !dotazione.isManaged() )
			{
				getEntityManager().persist( dotazione );
				tot++;
			}
		}
		return tot;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int saveOc()
	{
		int tot = 0;
		for ( Provincia prov : service.getProvince().values() )
		{
			if ( !prov.isManaged() )
			{
				getEntityManager().persist( prov );
			}
		}
		for ( Area area : service.getAree().values() )
		{
			if ( !area.isManaged() )
			{
				getEntityManager().persist( area );
				tot++;
			}
		}
		for ( Postazione pos : service.getPostazioni().values() )
		{
			if ( !pos.isManaged() )
			{
				getEntityManager().persist( pos );
				tot++;
			}
			else
			{
				getEntityManager().merge( pos );
			}
		}
		return tot;
	}
}
