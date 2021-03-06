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
import it.inspired.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@FacesValidator("decimalValidator")
public class DecimalValidator implements Validator
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager bundleManager;
	
	private Pattern pattern;
    private Matcher matcher;
 
    private static final String NUMBER_PATTERN = "^[0-9]\\d*(\\.\\d+)?$";
    
    public DecimalValidator() 
    {
    	pattern = Pattern.compile(NUMBER_PATTERN);
	}
    
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
	{
		if ( value != null )
		{
			String number = value.toString().trim();
			
			if ( !StringUtils.isEmpty( number ) )
			{
				log.debugv( "Validating number: {0}", number );
				
				matcher = pattern.matcher( number );
				if( !matcher.matches() )
				{
					log.debugv( "Invalid number: {0}", number );
					String text = bundleManager.getMessage( "validator.badnumber" );
					FacesMessage msg = new FacesMessage( text );
			        throw new ValidatorException( msg );
				}
			}
		}
	}

}
