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

package it.redev.parco.ext;

import it.redev.parco.model.Identifiable;

public class ModelEvent
{
	private Identifiable object;
	
	public ModelEvent(){} 

	public ModelEvent(Identifiable object) 
	{
		super();
		this.object = object;
	}

	public Identifiable getObject()
	{
		return object;
	}

	public void setObject(Identifiable object)
	{
		this.object = object;
	}
}
