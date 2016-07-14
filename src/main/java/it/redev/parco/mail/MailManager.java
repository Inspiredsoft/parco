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

package it.redev.parco.mail;

import it.redev.parco.core.AbstractScheduler;
import it.redev.parco.core.ConfigManager;
import it.redev.parco.model.User;
import it.redev.parco.model.UserContact;
import it.redev.parco.model.core.Mail;
import it.redev.parco.service.EventService;
import it.redev.parco.service.MailService;
import it.inspired.utils.DateUtils;
import it.inspired.utils.StringUtils;

import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.seam.cron.api.scheduling.Every;
import org.jboss.seam.cron.api.scheduling.Interval;
import org.jboss.seam.cron.api.scheduling.Trigger;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.mail.core.MailConfig;
import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@Singleton
public class MailManager extends AbstractScheduler 
{
	@Inject
	Logger log;
	
	@Inject
	MailService ms;
	
	@PersistenceContext
	protected EntityManager em;
	
	@Inject
	EventService es;
	
	//@Resource
	@Inject
	UserTransaction utx;
	
	@Inject
	ConfigManager cm;
	
	@Inject
	private Instance<MailMessage> mailMessage;
	
	//----------------------------------------------------------------------------------------
	
	public void init()
	{
		super.init();
		
		log.debug( "MailManager activated" );
		
		int res = ms.resetOnStartup();
		
		log.debugv( "{0} running mail restored to waiting", res );
	}
	
	//----------------------------------------------------------------------------------------
	
	@Override
	public void launcher(@Observes @Every(value=Interval.MINUTE, nth=1) Trigger trigger)
	{
		if ( activable() )
		{
			try
			{
				run();
			} 
			catch ( Throwable e )
			{
				log.error( "------------------- MAIL Launcher Error -------------------\n" + StringUtils.getStackTrace( e ) );
			}
			deactivate();
		}
	}
		
	//----------------------------------------------------------------------------------------
	
	private MailConfig getMailConfig()
	{
		MailConfig mc = new MailConfig();
		
		mc.setServerHost( cm.getMailServerHost() );
		mc.setServerPort( cm.getMailServerPort() );
		mc.setUsername(   cm.getMailUsername() );
		mc.setPassword(   cm.getMailPassword() );
		mc.setEnableSsl(  cm.getMailEnableSsl() );
		mc.setAuth(       cm.getMailAuth() );
		mc.setDomainName( cm.getMailDomainName() ); 
		mc.setRequireTls( cm.getMailRequireTls() );
		mc.setEnableTls(  cm.getMailEnableTls() );

		return mc;
	}
	
	//----------------------------------------------------------------------------------------
	
	public void run()
	{
		if( !cm.isMailEnabled() )
		{
			return;
		}

		log.info( "Mail scan started" );

		while ( true )
		{
			List<Mail> mails = ms.findExecutable();
			
			Mail mail = getSendable( mails );
			
			if ( mail != null )
			{
				log.infov( "Sending mail id: {0}", mail.getId() );
				
				long start = System.currentTimeMillis();
				
				try 
				{
					begin();
					
					mail.setStatus( Mail.Status.RUNNING );
					mail.setStartDate(new Date());
					mail.setTries( mail.getTries() + 1 );
					
					em.merge( mail );
					
					commitBegin();
					
					send( mail.getId() );
					
					mail.setStatus( Mail.Status.ENDED );
					mail.setException( null );
				} 
				catch (Throwable e) 
				{
					log.warn( "------------------- Mail Exception -------------------\n" 
							+ StringUtils.getStackTrace( e ) );
					
					if( mail.getTries() >= cm.getMailTries() )
					{
						mail.setStatus( Mail.Status.ERROR );
						mail.setException( StringUtils.getStackTrace(e) );
						addMessage( mail, it.redev.parco.model.core.MailMessage.Level.ERR, "Mail error: " + e.getMessage() );
					}
					else
					{
						mail.setStatus( Mail.Status.DELAYED );
						mail.setException( StringUtils.getStackTrace(e) );
						addMessage( mail, it.redev.parco.model.core.MailMessage.Level.WARN, "Mail delayed: " + e.getMessage() );
					}
				}
				finally
				{
					long end = System.currentTimeMillis();
					int exeTime = (int)(end - start) / 1000;
					mail.setExeTime( exeTime );
					mail.setEndDate( new Date() );
					em.merge( mail );
					
					log.infov("Mail sended in {0} secs", exeTime );
					
					try 
					{
						commit();
					} 
					catch (Throwable e) 
					{
						log.warn( "------------------- MAIL COMMIT ERROR -------------------\n" 
								+ StringUtils.getStackTrace( e ) );
					}
				}
			}
			else
			{
				break;
			}
		}
		log.info( "Mail scan ended" );
	}
	
	//----------------------------------------------------------------------------------------
	
	private Mail getSendable( List<Mail> mails )
	{
		if( mails != null && !mails.isEmpty() )
		{
			for ( Mail mail : mails )
			{
				if ( canSend( mail ) )
				{
					return mail;
				}
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------------
	
	private boolean canSend( Mail mail )
	{
		boolean send = mail.isWaiting();
		
		if ( !send )
		{
			int delay = cm.getMailDelay();
			Date start = mail.getStartDate();
			
			send = DateUtils.addMin( start, delay ).before( new Date() );
		}
		
		return send;
	}
		
	
	//----------------------------------------------------------------------------------------
	
	private void send( Integer mailId ) throws Exception
	{
		Mail mail = em.find( Mail.class, mailId );
		
		AbstractMail am = AbstractMail.create( mail, em, es, cm );
		
		if ( am.getRecipients().size() == 0 )
		{
			am.addInfoMessage( "No recipient defined" );
			return;
		}
		
		for ( UserContact userContact : am.getRecipients() )
		{
			if ( userContact.getUser()==null || 
					(userContact.getUser().isActive() && !userContact.getUser().isRemoved() ) )
			{
				if ( userContact.getUser()==null || !StringUtils.isEmpty( userContact.getUser().getEmail() ) ) 
				{
					MailMessage message = mailMessage.get();
					message.from( cm.getMailSender() );
					message.to( userContact.getName() + "<" + userContact.getAddress() + ">" )
					  	   .importance( am.getPriority() )
					  	   .subject( am.getSubject() );
					
					if ( am.getHtmlContent() != null )
					{
						String text = ( am.getTextContent() != null ? am.getTextContent() : "This message can be read only using an HTML client." );
					  	message.bodyHtmlTextAlt( am.getHtmlContent(), text );
					}
					else if ( am.getTextContent() != null )
					{
						message.bodyText( am.getTextContent() );	
					}
					else
					{
						am.addErrorMessage( "No content defined" );
						throw new Exception( "No content defined" );
					}
					
					//message.setMailTransporter( new Trasporter() );
					
					message.send( getMailConfig() );
					
					am.addInfoMessage( "Sent email to " + userContact );
					
				}
				else
				{
					am.addWarningMessage( "User " + userContact.getName() + " has empty email" );
				}
			}
		}
	}
	
	//----------------------------------------------------------------------------------------
	
	private void log( AbstractMail mail )
	{
		log.debugv( "Enqueued mail {0}", mail.getClass().getSimpleName() );
	}
	
	//-----------------------------------------------------------------------------
	
	private void addMessage( Mail mail, String level, String message)
	{
		it.redev.parco.model.core.MailMessage mailMessage = new it.redev.parco.model.core.MailMessage();
		mailMessage.setMessage( message );
		mailMessage.setMail( mail );
		mailMessage.setLevel( level );
		mailMessage.setDate( new Date() );
		em.persist( mailMessage );
	}
	
	//----------------------------------------------------------------------------------------
	// Enqueue methods
	//----------------------------------------------------------------------------------------
	
	public void enqueueSimpleMail( User user, String subject, String text )
	{
		SimpleMail mail = new SimpleMail( user.getId(), subject, text );
		em.persist( mail.toMail() );
		log( mail );
	}
}
