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

package it.redev.parco.model.rifornimenti;

public class TipoCarburante
{
	public static Integer BENZINA	= 1;
	public static Integer DIESEL	= 2;
	public static Integer GPL		= 3;
	
	public static final Integer list[] = 
	{
		BENZINA,
		DIESEL,
		GPL
	};
	
	public static Integer parse( String tipo )
	{
		if ( tipo.equalsIgnoreCase( "BENZINA" ) )
		{
			return BENZINA;
		}
		else if ( tipo.equalsIgnoreCase( "DIESEL" ) || tipo.equalsIgnoreCase( "GASOLIO" ) )
		{
			return DIESEL;
		}
		else if ( tipo.equalsIgnoreCase( "GPL" ) )
		{
			return GPL;
		}
		return null;
	}
}
