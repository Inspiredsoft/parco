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

package it.redev.parco.utils;

import it.inspired.utils.StringUtils;

public class LineInterator 
{
	int pos = 0;
	String line = "";
	
	public LineInterator( String line )
	{
		this.line = line;
	}
	
	/**
	 * Recupera i successivi caratteri dalla posizione attuale	
	 * @param len Numero di caratteri da recuperare
	 * @return I caratteri richiesti
	 */
	public String next( int len )
	{
		String value = StringUtils.subStringLength( line, pos, len );
		pos = pos + len;
		return value;
	}
	
	public int getPosition()
	{
		return pos;
	}
}
