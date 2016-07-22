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
import it.redev.parco.model.core.JobParameter;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Soccorso;
import it.inspired.utils.DateUtils;
import it.redev.parco.utils.ExcelUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SoccorsiParserJob extends FileParserJob
{
	private Integer month;
	private Integer year;
	
	//-------------------------------------------------------------------------------------
	
	public SoccorsiParserJob(){}
	
	public SoccorsiParserJob(ImportInfo info, Integer month, Integer year, User user) 
	{
		this.month = month;
		this.year  = year;
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
			
			HSSFWorkbook workbook = new HSSFWorkbook( file );
			
			Sheet sheet = workbook.getSheetAt(0); 
			
			int rows = sheet.getLastRowNum();
					
			for ( int i = 1; i < rows; i++ )
			{
				Row row = sheet.getRow( i );
				super.addSoccorso( parse( row ) );
			}

			getInfo().setDataInizio( DateUtils.create(year, month-1, 1 ) );
			getInfo().setDataFine( DateUtils.lastDayOfMoth( getInfo().getDataInizio() ) );
			super.saveSoccorsi();
			
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
		
		// Enqueue job soccorsi
		SoccorsiJob job = new SoccorsiJob( month, year , getUser() );
		getEntityManager().persist( job.toJob() );
	}
	
	//-------------------------------------------------------------------------------------

	private Soccorso parse( Row row ) throws Exception
	{
		Cell cell = null;
		String value = "";
		Soccorso soc = new Soccorso();
		soc.setImportInfo( getInfo() );
		try
		{
			cell = row.getCell( 0 );
			
			if ( cell == null )
			{
				return null;
			}
			
			value = ExcelUtils.getCellValue( cell );
			
			if ( StringUtils.isEmpty( value ) )
			{
				return null;
			}
			
			value = value.replaceAll( "\\D", "" );
			
			Mezzo mezzo = super.findMezzoByCodice( value );
			
			if ( mezzo == null )
			{
				addErrorMessage( "Mezzo con codice radio " + value + " non trovato" );
				return null;
			}
			
			soc.setMezzo( mezzo );
			
			cell = row.getCell( 1 );
			if ( ExcelUtils.isCellEmpty( cell ) )
			{
				addErrorMessage( "Soccorsi per il mezzo " + mezzo.getTarga() + " non definiti" );
				return null;
			}
			value = ExcelUtils.getCellValue( cell );
			int numero;
			try 
			{
				numero = new Double( value ).intValue();
			} 
			catch (NumberFormatException e)
			{
				addErrorMessage( "Errore di formattazione del numero " + value );
				return null;
			}
			
			soc.setAnno( this.year );
			soc.setMese( this.month );
			soc.setNumero( numero );
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + value );
			throw e;
		}
		return soc;
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	protected List<JobParameter> getState( ) 
	{
		List<JobParameter> params =super.getState();
		params.add( new JobParameter( "month",  "Mese", month ) );
		params.add( new JobParameter( "year",  "Anno", year ) );
		return params;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected void setState(HashMap<String, String> params)
	{
		super.setState( params );
		this.month = Integer.parseInt( params.get( "month" ) );
		this.year  = Integer.parseInt( params.get( "year" ) );
	}
}
