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

package it.redev.parco.utils;

import it.inspired.utils.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtils 
{
		public static String getCellValue( Cell cell )
		{
			String value = null;
			switch ( cell.getCellType() )
			{
				case Cell.CELL_TYPE_BOOLEAN:
					value = new Boolean( cell.getBooleanCellValue() ).toString();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					value = new BigDecimal(cell.getNumericCellValue() ).toPlainString();
					break;
				case Cell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
			}
			return value;
		}
		
		//-------------------------------------------------------------------------------------
		
		public static boolean isCellEmpty( Cell cell )
		{
			return cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK;
		}
		
		//-------------------------------------------------------------------------------------
		
		public static Date getAsDate( Cell cell, String format ) throws ParseException
		{
			if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC )
			{
				return cell.getDateCellValue();
			}
			return DateUtils.parse( ExcelUtils.getCellValue( cell ), format );
		}
}
