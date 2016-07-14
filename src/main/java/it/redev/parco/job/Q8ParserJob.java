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

import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.rifornimenti.Distributore;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.model.rifornimenti.RifornimentoQ8;
import it.inspired.utils.DateUtils;
import it.redev.parco.utils.ExcelUtils;
import it.inspired.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Q8ParserJob extends FileParserJob
{
	
	//-------------------------------------------------------------------------------------
	
	public Q8ParserJob(){}
	
	public Q8ParserJob(ImportInfo info, User user) 
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
			
			HSSFWorkbook workbook = new HSSFWorkbook( file );
			
			Sheet sheet = workbook.getSheetAt(0); 
			
			int rows = sheet.getLastRowNum();
					
			for ( int i = 10; i <= rows; i++ )
			{
				Row row = sheet.getRow( i );
				Rifornimento rif = parse( row );
				if ( rif != null )
				{
					super.addRifornimento( rif );
					super.nuovoScontrino( rif );
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

	private RifornimentoQ8 parse( Row row ) throws Exception
	{
		int 	col 		= 0;
		Cell 	cell 		= null;
		String 	numero 		= null;
		String 	prodotto 	= null;
		String 	targa 		= null;
		String 	indirizzo	= null;
		String 	provincia	= null;
		String 	localita	= null;
		
		RifornimentoQ8 rif = new RifornimentoQ8();
		rif.setGestore( Gestore.Q8 );
		rif.setImportInfo( getInfo() );
		
		try
		{
			cell = row.getCell( col++ );
			Date data = DateUtils.parse( ExcelUtils.getCellValue( cell ), "dd/MM/yyyy - HH:mm" );
			rif.setData( data );
			rif.setAaaammgg( DateUtils.format( rif.getData(), "yyyyMMdd" ) );
			
			cell = row.getCell( col++ );
			rif.setNumeroScontrino( StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' ) );
			
			cell = row.getCell( col++ );
			rif.setImportoNonScontato( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			
			cell = row.getCell( col++ );
			rif.setImportoScontato( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			rif.setImporto( rif.getImportoScontato() );
			
			cell = row.getCell( col++ );
			rif.setPrezzoNonScontato( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			
			cell = row.getCell( col++ );
			rif.setPrezzoScontato( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			
			cell = row.getCell( col++ );
			rif.setQuantita( new BigDecimal( ExcelUtils.getCellValue( cell ) ) );
			
			cell = row.getCell( col++ );
			prodotto = ExcelUtils.getCellValue( cell );
			rif.setProdotto( prodotto );
			
			Integer tipo = super.findCarburante( prodotto );
			if ( tipo != null ) {
				rif.setTipoCarburante( tipo );
			}
			
			cell = row.getCell( col++ );
			numero = StringUtils.padLeft( StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' ), '0', 7 );
			cell = row.getCell( col++ );	
			numero += StringUtils.padLeft( StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' ), '0', 5 );
			cell = row.getCell( col++ );	
			numero += StringUtils.padLeft( StringUtils.cutTo( ExcelUtils.getCellValue( cell ), '.' ), '0', 2 );
			
			CartaCarburante carta = super.findCartaCarburante( numero, Gestore.Q8 );
			rif.setCarta( carta );
			
			//Codice: non utilizzato
			cell = row.getCell( col++ );

			cell = row.getCell( col++ );
			rif.setChilometraggio( Double.valueOf( ExcelUtils.getCellValue( cell ) ).intValue() );

			cell = row.getCell( col++ );
			targa = ExcelUtils.getCellValue( cell );
			
			Mezzo mezzo = super.findMezzo( targa );
			rif.setMezzo( mezzo );
			if ( carta.getMezzo() == null )
			{
				carta.setMezzo( mezzo );
			}
			if ( mezzo.getId() == null )
			{
				if (  rif.getData().before( mezzo.getStato().getDataStato() ) )
				{
					mezzo.getStato().setDataStato( rif.getData() );
				}
			}

			cell = row.getCell( col++ );
			rif.setUtilizzatore( ExcelUtils.getCellValue( cell ) );
			
			cell = row.getCell( col++ );
			rif.setCodiceTerminale( ExcelUtils.getCellValue( cell ) );
			
			//Causale	Causale1
			cell = row.getCell( col++ );
			cell = row.getCell( col++ );
			cell = row.getCell( col++ );
			cell = row.getCell( col++ );
			
			cell = row.getCell( col++ );
			rif.setCartissima( ExcelUtils.getCellValue( cell ) );
			
			cell = row.getCell( col++ );
			indirizzo = ExcelUtils.getCellValue( cell );
			
			cell = row.getCell( col++ );
			provincia = ExcelUtils.getCellValue( cell );
			
			cell = row.getCell( col++ );
			localita = ExcelUtils.getCellValue( cell );
			
			Distributore dist = super.findDistributore( Gestore.Q8, indirizzo, localita, provincia, null );
			rif.setDistributore( dist );
		} 
		catch (Exception e)
		{
			addErrorMessage( "Error parsing: " + ExcelUtils.getCellValue( cell ) );
			throw e;
		}
		return rif;
	}
}
