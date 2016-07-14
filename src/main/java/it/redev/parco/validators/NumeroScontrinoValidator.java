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

package it.redev.parco.validators;

import it.redev.parco.core.MessagesManager;
import it.inspired.utils.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@RequestScoped
@Named
@FacesValidator("numeroScontrinoValidator")
public class NumeroScontrinoValidator implements Validator 
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager bundleManager;
	
    public void validate(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        String ns = (String) value;

        String nsr = (String) component.getAttributes().get("numeroScontrinoRichiesta");

        if ( StringUtils.isEmpty( ns ) || StringUtils.isEmpty( nsr ) )
        {
            return; 
        }

        if ( !StringUtils.equalsIgnoreZeros( ns, nsr ) )
        {
        	((UIInput) component).setValid( false );
        	log.debugv( "Scontrino {0} diverso dal numero scontrino {1} sul rifornimento", ns, nsr );
        	
            String text = bundleManager.getMessage( "valodator.scon.wrong" );
            
            throw new ValidatorException( new FacesMessage( text ) );
        }
    }
}
