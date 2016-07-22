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

package it.redev.parco.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class Parco implements Serializable 
{
	private static final String name = "application.properties";
	
	@Inject
	private Logger log;
	
	private Properties props;
	
	//-------------------------------------------------------------------------------

	@PostConstruct
	public void load()
	{
		props = new Properties();
		InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		if (configStream != null)
		{
			try
			{
				props.load(configStream);
				log.info( props.toString() );
			} 
			catch (IOException e) 
			{
				log.error( "Impossibile caricare file di properties: " + name );
				e.printStackTrace();
			}
		}
		else
		{
			log.error( "Impossibile caricare file di properties: " + name );
		}
	}
	
	//------------------------------------------------------------------------
	
	public String get( String key )
	{
		return props.getProperty( key );
	}
	
	public Properties getProps() {
		return props;
	}
	
	//------------------------------------------------------------------------
	
	public boolean isDashboardEnabled() {
		return Boolean.valueOf( get( "dashboard.enabled" ) );
	}
	
	//------------------------------------------------------------------------
	
	public String getEmailSeparator() {
		return get("email.separator");
	}
}
