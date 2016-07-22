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

package it.redev.parco.converters;

import it.redev.parco.model.oc.Persona;
import it.redev.parco.service.OcService;
import it.inspired.utils.StringUtils;

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
@FacesConverter("personaConverter")
public class PersonaConverter implements Converter
{
	@Inject
	OcService oc;
	
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) 
	{
		if ( !StringUtils.isEmpty( value) )
		{
			Persona persona = Persona.parseNomeMatricola( value.trim() );
			List<Persona> persone = null;
			if ( persona != null )
			{
				persone = oc.findNotRemovedPersonaByMatricola( persona.getMatricola() );
			}
			return ( (persone == null || persone.isEmpty() ) ? new Persona( value ) : persone.get( 0 ) );
		}
		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value)
	{
		if ( value != null && value instanceof Persona )
		{  
            return ((Persona) value).getNomeMatricola();  
        } 
		else 
		{  
            return "";  
        }  
	}
}
