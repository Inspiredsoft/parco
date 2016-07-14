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

package it.redev.parco.core;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ModelEvent;
import it.redev.parco.ext.annotation.Updated;
import it.redev.parco.mail.MailManager;
import it.redev.parco.model.core.Configuration;
import it.redev.parco.service.ConfigService;
import it.inspired.utils.DateUtils;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class ConfigManager implements Serializable
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager message;
	
	@Inject
	ConfigService cs;
	
	@Inject
	MailManager mm;
	
	@Inject
	LoggedUser loggedUser;
	
	@Inject 
	@Updated
	Event<ModelEvent> updatedEvent;
	
	private HashMap<String, Configuration> configMap;
	
	//-------------------------------------------------------------------------------
	
	@PostConstruct
	public void load()
	{
		List<Configuration> configs = cs.findAll();
		
		configMap = new HashMap<String, Configuration>();
		
		for ( Configuration conf : configs )
		{
			log.infov( "Config for {0} = {1}", conf.getName(), conf.getValue() );
			put( conf );
		}
	}
	
	//-------------------------------------------------------------------------------
	
	public void save()
	{
		for ( Configuration conf : configMap.values() )
		{
			save( conf );
			
			log.infov( "Saved config {0} = {1}", conf.getName(), conf.getValue() );
			
			updatedEvent.fire( new ModelEvent( conf ) );
		}
		
		message.info( "message.config.saved" );
	}
	
	//-------------------------------------------------------------------------------
	
	public void put( Configuration conf )
	{
		configMap.put( conf.getName(), conf );
	}
	
	//-------------------------------------------------------------------------------
	
	public Configuration get( String key )
	{
		return configMap.get( key );
	}
	
	//-------------------------------------------------------------------------------
	
	private void save(Configuration conf) 
	{
		if ( conf.getValue() == null )
		{
			conf.setValue( "" );
		}
	   conf.setVersion( conf.getVersion() + 1 );
	   cs.updateByName( conf );
	}
	
	//-------------------------------------------------------------------------------
	
	private void setValue( String key, String value )
	{
		Configuration conf = get( key );
		conf.setValue( value );
	}
	
	private String getValue( String key )
	{
		return get( key ).getValue();
	}
	
	//-------------------------------------------------------------------------------
	
	public void sendTestEmail()
	{
		String email = loggedUser.getUser().getEmail();
		
		if ( !StringUtils.isEmpty( email ) )
		{
			mm.enqueueSimpleMail( loggedUser.getUser(), "Test email", "This is a test email from PARCO sent in date " + new Date() );
			message.info( "message.email.sent", email, loggedUser.getUser().getFullName() );
		}
		else
		{
			message.info( "message.email.empty", loggedUser.getUser().getFullName() );
		}
	}
	
	//-------------------------------------------------------------------------------
	// Config methods
	//-------------------------------------------------------------------------------
	
	public String getShortDateFormat()
	{
		return get( "date.format.short" ).getValue();
	}
	
	public void setShortDateFormat( String format )
	{
		Configuration conf = get( "date.format.short" );
		conf.setValue( format );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getLongDateFormat()
	{
		return get( "date.format.long" ).getValue();
	}
	
	public void setLongDateFormat( String format )
	{
		Configuration conf = get( "date.format.long" );
		conf.setValue( format );
	}
	
	//-------------------------------------------------------------------------------

	public boolean isJobEnabled()
	{
		return Boolean.parseBoolean( getValue( "job.enabled" ) );
	}
	
	public void setJobEnabled( boolean enabled )
	{
		setValue( "job.enabled", Boolean.valueOf( enabled ).toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getJobDelay()
	{
		return Integer.parseInt( get( "job.delay.min" ).getValue() );
	}
	
	public void setJobDelay( Integer delay )
	{
		Configuration conf = get( "job.delay.min" );
		conf.setValue( delay.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getJobTries()
	{
		return Integer.parseInt( get( "job.tries" ).getValue() );
	}
	
	public void setJobTries( Integer tries )
	{
		Configuration conf = get( "job.tries" );
		conf.setValue( tries.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getJobKeep()
	{
		return Integer.parseInt( get( "job.keep.day" ).getValue() );
	}
	
	public void setJobKeep( Integer keep )
	{
		Configuration conf = get( "job.keep.day" );
		conf.setValue( keep.toString() );
	}

	//-------------------------------------------------------------------------------

	public boolean isMailEnabled()
	{
		return Boolean.parseBoolean( getValue( "mail.enabled" ) );
	}
	
	public void setMailEnabled( boolean enabled )
	{
		setValue( "mail.enabled", Boolean.valueOf( enabled ).toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getMailServerHost()
	{
		return getValue( "mail.serverHost" );
	}
	
	public void setMailServerHost( String serverHost )
	{
		setValue( "mail.serverHost", serverHost );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getMailServerPort()
	{
		return Integer.parseInt( getValue( "mail.serverPort" ) );
	}
	
	public void setMailServerPort( Integer serverPost )
	{
		setValue( "mail.serverPort", serverPost.toString() );
	}

	//-------------------------------------------------------------------------------
	
	public String getMailUsername()
	{
		return getValue( "mail.username" );
	}
	
	public void setMailUsername( String username )
	{
		setValue( "mail.username", username );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getMailSender()
	{
		return getValue( "mail.sender" );
	}
	
	public void setMailSender( String sender )
	{
		setValue( "mail.sender", sender );
	}
	
	//-------------------------------------------------------------------------------

	public String getMailPassword()
	{
		return getValue( "mail.password" );
	}
	
	public void setMailPassword( String password )
	{
		setValue( "mail.password", password );
	}
	
	//-------------------------------------------------------------------------------
	
	public Boolean getMailEnableSsl()
	{
		return Boolean.valueOf( getValue( "mail.enableSsl" ) );
	}
	
	public void setMailEnableSsl( Boolean enableSsl )
	{
		setValue( "mail.enableSsl", enableSsl.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Boolean getMailAuth()
	{
		return Boolean.valueOf( getValue( "mail.auth" ) );
	}
	
	public void setMailAuth( Boolean auth )
	{
		setValue( "mail.auth", auth.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getMailDomainName()
	{
		return getValue( "mail.domainName" );
	}
	
	public void setMailDomainName( String domainName )
	{
		setValue( "mail.domainName", domainName );
	}
	
	//-------------------------------------------------------------------------------
	
	public Boolean getMailEnableTls()
	{
		return Boolean.valueOf( getValue( "mail.enableTls" ) );
	}
	
	public void setMailEnableTls( Boolean enableTls )
	{
		setValue( "mail.enableTls", enableTls.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Boolean getMailRequireTls()
	{
		return Boolean.valueOf( getValue( "mail.requireTls" ) );
	}
	
	public void setMailRequireTls( Boolean requireTls )
	{
		setValue( "mail.requireTls", requireTls.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getMailDelay()
	{
		return Integer.parseInt( getValue( "mail.delay.min" ) );
	}
	
	public void setMailDelay( Integer delay )
	{
		setValue( "mail.delay.min", delay.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getMailTries()
	{
		return Integer.parseInt( getValue( "mail.tries" ) );
	}
	
	public void setMailTries( Integer tries )
	{
		setValue( "mail.tries", tries.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public Integer getMailKeep()
	{
		return Integer.parseInt( getValue( "mail.keep.day" ) );
	}
	
	public void setMailKeep( Integer keep )
	{
		setValue( "mail.keep.day", keep.toString() );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getImportPath()
	{
		return getValue( "import.path" );
	}
	
	public void setImportPath( String path )
	{
		setValue( "import.path", path );
	}
	
	//-------------------------------------------------------------------------------
	
	public String getResponsabile()
	{
		return getValue( "responsabile.uo" );
	}
	
	public void setResponsabile( String path )
	{
		setValue( "responsabile.uo", path );
	}
	
	//--------------------------------------------------------------------------------
	// Constants
	//--------------------------------------------------------------------------------
		
	public int getAlertRangeDay()
	{
		return 2;
	}
	
	//--------------------------------------------------------------------------------
	
	public Long getRefreshInterval()
	{
		return 15000L;
	}
	
	//--------------------------------------------------------------------------------
	// Date limit
	//--------------------------------------------------------------------------------
	
	public Date getJobKeepDateLimit()
	{
		return DateUtils.addDay( new Date(), -getJobKeep() );
	}

	//--------------------------------------------------------------------------------

	public Date getMailKeepDateLimit()
	{
		return DateUtils.addDay( new Date(), -getMailKeep() );
	}
}
