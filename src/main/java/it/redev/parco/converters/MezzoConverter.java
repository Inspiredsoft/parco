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

package it.redev.parco.converters;

import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.service.MezzoService;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named
@FacesConverter("mezzoConverter")
public class MezzoConverter implements Converter
{
	@Inject
	MezzoService ms;
	
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) 
	{
		List<Mezzo> mezzi = ms.findNotRemovedMezzoByTarga( value );
		return ( mezzi.isEmpty() ? null : mezzi.get( 0 ) );
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value)
	{
		if ( value == null || value.equals(""))
		{  
            return "";  
        } else {  
            return String.valueOf(((Mezzo) value).getTarga());  
        }  
	}
}
