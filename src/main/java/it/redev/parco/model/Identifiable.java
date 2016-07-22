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

package it.redev.parco.model;

import it.inspired.exporter.annotation.ExportType;
import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.Unexportable;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("serial")
@ExpoElement(ExportType.IGNORE)
public abstract class Identifiable implements Serializable
{
	public abstract Integer getId();

	@Override
	public boolean equals(Object that) 
	{
		if (this == that) 
		{
			return true;
		}
		if (that == null)
		{
			return false;
		}
		if (getClass() != that.getClass()) 
		{
			return false;
		}
		if (getId() != null) 
		{
			return getId().equals(((Identifiable) that).getId());
		}
		return super.equals(that);
	}

	@Override
	public int hashCode()
	{
		if (getId() != null) 
		{
			return getId().hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public String toString() 
	{
		return ReflectionToStringBuilder.reflectionToString( this, ToStringStyle.SHORT_PREFIX_STYLE, false );
	}
	
	@Unexportable
	public boolean isManaged()
	{
		return getId() != null;
	}
}
