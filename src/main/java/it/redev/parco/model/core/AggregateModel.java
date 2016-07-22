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

package it.redev.parco.model.core;

import java.util.HashMap;
import java.util.Map;

public class AggregateModel 
{
	private Map<Integer, Object> column = new HashMap<Integer,Object>();

	public AggregateModel( Object objs )
	{
		if ( objs instanceof Object[] )
		{
			for ( int i = 0; i < ((Object[])objs).length; i++ )
			{
				column.put( i, ((Object[])objs)[i] );
			}
		}
		else
		{
			column.put( 0, objs );
		}
	}
	
	public Map<Integer, Object> getColumn()
	{
		return column;
	}

	public void setColumn(Map<Integer, Object> column)
	{
		this.column = column;
	}
	
	public Object get( Integer index )
	{
		return column.get( index );
	}
}
