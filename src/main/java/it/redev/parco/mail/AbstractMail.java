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

package it.redev.parco.mail;

import it.redev.parco.core.ConfigManager;
import it.redev.parco.core.SystemConstant;
import it.redev.parco.model.User;
import it.redev.parco.model.UserContact;
import it.redev.parco.model.core.Job;
import it.redev.parco.model.core.Mail;
import it.redev.parco.model.core.MailMessage;
import it.redev.parco.model.core.MailParameter;
import it.redev.parco.service.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.mail.core.enumerations.MessagePriority;

public  abstract class AbstractMail 
{
	private Mail			mail;
	private String 			subject;
	private String 			textContent;
	private String 			htmlContent;
	private List<UserContact> 		recipients = new ArrayList<UserContact>();
	private EntityManager 	em;
	private EventService 	es;
	private ConfigManager	cm;
	private MessagePriority priority = MessagePriority.NORMAL;
	
	//---------------------------------------------------------------------------
	
	public Mail getMail() 
	{
		return mail;
	}

	public void setMail(Mail mail) 
	{
		this.mail = mail;
	}
	
	//---------------------------------------------------------------------------
	
	public String getSubject() 
	{
		return subject;
	}

	public void setSubject(String subject) 
	{
		this.subject = subject;
	}

	//---------------------------------------------------------------------------
	
	public String getTextContent() 
	{
		return textContent;
	}

	public void setTextContent(String content)
	{
		this.textContent = content;
	}
	
	//---------------------------------------------------------------------------
	
	public String getHtmlContent() 
	{
		return htmlContent;
	}

	public void setHtmlContent(String content)
	{
		this.htmlContent = content;
	}
	
	//---------------------------------------------------------------------------
	
	public List<UserContact> getRecipients() 
	{
		return recipients;
	}

	public void setRecipients(List<UserContact> recipient) 
	{
		this.recipients = recipient;
	}
	
	public void addRecipient( UserContact user )
	{
		this.recipients.add( user );
	}
	
	public void addRecipient( User user )
	{
		this.recipients.add( new UserContact( user ) );
	}
	
	public void addRecipient( String email )
	{
		for ( String em : email.trim().split( SystemConstant.EMAIL_SEPARATOR ) )
		{
			this.recipients.add( new UserContact( em ) );
		}
	}
	
	//-----------------------------------------------------------------------------

	public EntityManager getEntityManager() {
		return em;
	}

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	//-----------------------------------------------------------------------------

	public ConfigManager getConfigManager() {
		return cm;
	}

	public void setConfigManager(ConfigManager cm) {
		this.cm = cm;
	}
	
	//-----------------------------------------------------------------------------

	public EventService getEventService() {
		return es;
	}

	public void setEventService(EventService es) {
		this.es = es;
	}
	
	//-----------------------------------------------------------------------------
	
	public Mail toMail()
	{
		Mail mail = new Mail();
		
		mail.setJavaClass( this.getClass().getName() );
		mail.setStatus( Job.Status.WAITING );
		mail.setTries( 0 );
		//mail.setSafe( getSafe() );
		
		mail.setParameters( getState() );
		
		for ( MailParameter param : mail.getParameters() )
		{
			param.setMail( mail );
		}
		
		return mail;
	}
	
	//-----------------------------------------------------------------------------
	
	protected List<MailParameter> getState( ) 
	{
		return new ArrayList<MailParameter>();
	}

	//-----------------------------------------------------------------------------
	
	protected void setState(HashMap<String, String> params)
	{
		 
	}
	
	//---------------------------------------------------------------------------
	
	protected abstract void init() throws Exception;
	
	//---------------------------------------------------------------------------
	
	protected MessagePriority getPriority()
	{
		return priority;
	}
	
	public void setPriority(MessagePriority priority) 
	{
		this.priority = priority;
	}
	
	//---------------------------------------------------------------------------

	public void addInfoMessage(String message) throws Exception
	{
		addMessage( MailMessage.Level.INFO, message );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addWarningMessage(String message) throws Exception
	{
		addMessage( MailMessage.Level.WARN, message );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addErrorMessage(String message) throws Exception
	{
		addMessage( MailMessage.Level.ERR, message );
	}
	
	//-----------------------------------------------------------------------------
	// Factory
	//-----------------------------------------------------------------------------
	
	public static AbstractMail create( Mail mail, EntityManager em, EventService es, ConfigManager cm ) throws Exception
	{
		AbstractMail am = (AbstractMail) Class.forName( mail.getJavaClass() ).newInstance();
		
		am.mail = mail;
		//am.safe = mail.getSafe();
		am.em = em;
		am.es = es;
		am.cm = cm;
		
		am.setState( hashMapParameterFromList( mail.getParameters() ) );
		
		am.init();
		
		return am;
	}
	
	//-----------------------------------------------------------------------------
	// Private Methods
	//-----------------------------------------------------------------------------
	
	private static HashMap<String, String> hashMapParameterFromList( List<MailParameter> parametersList )
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(MailParameter jobParameter : parametersList)
		{
			map.put( jobParameter.getName(), jobParameter.getValue() );
		}
		
		return map;
	}
	
	//-----------------------------------------------------------------------------
	
	private void addMessage( String level, String message) throws Exception
	{
		MailMessage mailMessage = new MailMessage();
		mailMessage.setMessage(message);
		mailMessage.setMail( mail );
		mailMessage.setLevel(level);
		mailMessage.setDate( new Date() );
		em.persist(mailMessage);
	}
}
