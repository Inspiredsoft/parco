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

package it.redev.parco.validators;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.service.OcService;
import it.inspired.utils.StringUtils;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@RequestScoped
@Named
@FacesValidator("personaValidator")
public class PersonaValidator implements Validator
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager bundleManager;
	
	@Inject
	OcService oc;
    
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
	{
		if ( value != null && value instanceof Persona )
		{
			Persona persona = (Persona) value;
			List<Persona> persone = null;
			if ( !StringUtils.isEmpty( persona.getMatricola() ) )
			{
				persone = oc.findNotRemovedPersonaByMatricola( persona.getMatricola() );
			}
			
			if( persone == null || persone.isEmpty() || persone.size() > 1 )
			{
				log.debugv( "persone non trovata: {0}", persona.getNomeMatricola() );
				String text = bundleManager.getMessage( "validator.persona.notfound", persona.getNomeCompleto() );
				FacesMessage msg = new FacesMessage( text );
		        throw new ValidatorException( msg );
			}
		}
	}
}
