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

package it.redev.parco.job;

import it.redev.parco.core.AbstractScheduler;
import it.redev.parco.core.ConfigManager;
import it.redev.parco.model.core.Job;
import it.redev.parco.model.core.Mail;
import it.redev.parco.service.JobService;
import it.redev.parco.service.MailService;
import it.inspired.utils.StringUtils;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.cron.api.scheduling.Every;
import org.jboss.seam.cron.api.scheduling.Interval;
import org.jboss.seam.cron.api.scheduling.Trigger;
import org.jboss.seam.transaction.Transactional;
import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class CleanerManager extends AbstractScheduler 
{
	@Inject
	Logger log;
	
	@Inject
	ConfigManager cm;
	
	@Inject
	JobService js;
	
	@Inject
	MailService ms;
	
	//----------------------------------------------------------------------------------------
	
	public void init()
	{
		super.init();
		
		log.debug( "CleanerManager activated" );
	}
	
	//----------------------------------------------------------------------------------------
	
	@Override
	public void launcher( @Observes @Every(value=Interval.HOUR, nth=6) Trigger trigger ) 
	{
		if ( activable() )
		{
			try
			{
				run();
			} 
			catch ( Throwable e )
			{
				log.error( "------------------- Cleaner Launcher Error -------------------\n" + StringUtils.getStackTrace( e ) );
			}
			deactivate();
		}	
	}

	//----------------------------------------------------------------------------------------
	
	@Transactional
	public void run() 
	{
		log.info( "Cleaner started" );
		
		cleanJob();
		cleanMail();
		
		log.info( "Cleaner ended" );
	}
	
	//----------------------------------------------------------------------------------------
	
	private void cleanJob()
	{
		Date limit = cm.getJobKeepDateLimit();
		
		log.debugv( "Removing job older than {0}", limit );
		
		int total = 0;
		
		try 
		{ 
			begin();
			total += js.removeOlder( limit, Job.Status.ENDED );
			total += js.removeOlder( limit, Job.Status.ERROR );
			total += js.removeOlder( limit, Job.Status.STOPPED );
			commit();
		} 
		catch (Exception e)
		{
			log.error( StringUtils.getStackTrace( e ) );
			abort();
		}
		
		log.debugv( "Removed {0} jobs", total );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void cleanMail()
	{
		Date limit = cm.getMailKeepDateLimit();
		
		log.debugv( "Removing mail older than {0}", limit );
		
		int total = 0;
		
		try 
		{ 
			begin();
			total += ms.removeOlder( limit, Mail.Status.ENDED );
			total += ms.removeOlder( limit, Mail.Status.ERROR );
			total += ms.removeOlder( limit, Mail.Status.STOPPED );
			commit();
		} 
		catch (Exception e)
		{
			log.error( StringUtils.getStackTrace( e ) );
			abort();
		}
		
		log.debugv( "Removed {0} mails", total );
	}
}
