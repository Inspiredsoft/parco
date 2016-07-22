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

package it.redev.parco.job;

import it.redev.parco.core.AbstractScheduler;
import it.redev.parco.core.ConfigManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.core.Job;
import it.redev.parco.model.core.JobMessage;
import it.redev.parco.model.mezzi.CodificaCarburante;
import it.redev.parco.service.JobService;
import it.redev.parco.service.UserService;
import it.redev.parco.session.MonthPeriod;
import it.redev.parco.session.Period;
import it.inspired.utils.DateUtils;
import it.inspired.utils.StringUtils;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.seam.cron.api.scheduling.Every;
import org.jboss.seam.cron.api.scheduling.Interval;
import org.jboss.seam.cron.api.scheduling.Trigger;
import org.jboss.seam.international.status.MessageFactory;
import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class JobManager extends AbstractScheduler 
{
	@Inject
	Logger log;
	
	@Inject
	LoggedUser loggedUser;
	
	@PersistenceContext
	protected EntityManager em;
	
	//@Resource
	@Inject
	UserTransaction utx;
	
	@Inject
	JobService js;
	
	@Inject
	ConfigManager cm;
	
	@Inject
	UserService us;
	
	@Inject
    private MessageFactory factory;
	
	@Inject
	protected Event<Job> jobEndedEvent;
	
	//----------------------------------------------------------------------------------------
	
	public void init()
	{
		super.init();
		
		log.debug( "JobManager activated" );
		
		int res = js.resetOnStartup();
		
		log.debugv( "{0} running job restored to waiting", res );
	}
	
	//----------------------------------------------------------------------------------------
	
	@Override
	public void launcher( @Observes @Every(value=Interval.SECOND, nth=20) Trigger trigger )
	{
		if ( activable() )
		{
			try
			{
				run();
			} 
			catch ( Throwable e )
			{
				log.error( "------------------- JOB Launcher Error -------------------\n" + StringUtils.getStackTrace( e ) );
			}
			deactivate();
		}
	}
		
	//----------------------------------------------------------------------------------------
	
	public void run()
	{
		if ( !cm.isJobEnabled() )
		{
			return;
		}
		
		log.info( "Job scan started" );
		
		while ( true )
		{
			List<Job> executables = js.findExecutable();
			
			Job job = getExecutable( executables );
			
			if ( job != null )
			{
				log.infov( "Executing job id: {0}", job.getId() );
				
				long start = System.currentTimeMillis();
				try 
				{
					begin();
					
					job.setStatus( Job.Status.RUNNING );
					job.setStartDate(new Date());
					job.setTries( job.getTries() + 1 );
					
					em.merge( job );
					
					commitBegin();
					
					execute( job.getId() );
					
					job.setStatus( Job.Status.ENDED );
					job.setException( null );
				} 
				catch (RemoteSafeException e) 
				{
					abort();
					
					log.warn( "------------------- JOB RemoteSafeException -------------------\n" 
							+ StringUtils.getStackTrace( e ) );
					
					if( job.getTries() >= cm.getJobTries() )
					{
						job.setStatus( Job.Status.ERROR );
						job.setException( StringUtils.getStackTrace(e) );
						addMessage( job, JobMessage.Level.ERR, "Job error: " + e.getMessage() );
					}
					else
					{
						job.setStatus( Job.Status.DELAYED );
						job.setException( StringUtils.getStackTrace(e) );
						addMessage( job, JobMessage.Level.WARN, "Job delayed: " + e.getMessage() );
					}
				}
				
				catch (DatabaseException e) 
				{
					abort();
					
					log.warn( "------------------- JOB DatabaseException -------------------\n" 
							+ StringUtils.getStackTrace( e ) );
					
					job.setStatus( Job.Status.ERROR );
					job.setException( StringUtils.getStackTrace(e) );
					addMessage( job, JobMessage.Level.ERR, "Job error: " + e.getMessage() );
				}
				catch (Exception e) 
				{
					abort();
					
					log.warn( "------------------- JOB Exception -------------------\n" 
							+ StringUtils.getStackTrace( e ) );
					
					job.setStatus( Job.Status.ERROR );
					job.setException( StringUtils.getStackTrace(e) );
					addMessage( job, JobMessage.Level.ERR, "Job error: " + e.getMessage() );
				}
				finally
				{
					long end = System.currentTimeMillis();
					int exeTime = (int)(end - start) / 1000;
					job.setExeTime( exeTime );
					job.setEndDate( new Date() );
					em.merge( job );
					
					log.infov("Job executed in {0} secs", exeTime );
					
					try 
					{
						commit();
					} 
					catch (Throwable e) 
					{
						log.warn( "------------------- JOB COMMIT ERROR -------------------\n" 
								+ StringUtils.getStackTrace( e ) );
					}
					
					jobEndedEvent.fire( job );
				}
			}
			else
			{
				break;
			}
		}
		log.info( "Job scan ended" );
	}
	
	//----------------------------------------------------------------------------------------
	
	private Job getExecutable( List<Job> jobs )
	{
		if( jobs != null && !jobs.isEmpty() )
		{
			for ( Job job : jobs )
			{
				if ( canExecute( job ) && isFirst( job, jobs ) )
				{
					return job;
				}
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------------
	
	private boolean isFirst( Job job, List<Job> jobs )
	{
		boolean first = true;
		
		for ( Job j : jobs )
		{
			if ( //j.getSafe().equals( job.getSafe() ) && 
					j.getId() < job.getId() )
			{
				first = false;
				break;
			}
		}
		
		return first;
	}
	
	//----------------------------------------------------------------------------------------
	
	private boolean canExecute( Job job )
	{
		boolean exe = job.isWaiting();
		
		if ( !exe )
		{
			int delay = cm.getJobDelay();
			Date start = job.getStartDate();
			
			exe = DateUtils.addMin( start, delay ).before( new Date() );
		}
		
		return exe;
	}
	
	//----------------------------------------------------------------------------------------
	
	private void execute( Integer jobId ) throws Exception
	{
		AbstractJob ab = null;
		
		Job job = em.find( Job.class, jobId );
		
		ab = AbstractJob.create( utx, em, cm, factory, job );
		
		ab.execute();
		
		ab.addInfoMessage("Job ended successfully", false);
		
		commitBegin();
	}
	
	//----------------------------------------------------------------------------------------
	
	private void log( AbstractJob job )
	{
		log.debugv( "Enqueued job {0}", job.getClass().getSimpleName() );
	}
	
	//-----------------------------------------------------------------------------
	
	private void addMessage( Job job, String level, String message)
	{
		JobMessage jobMessage = new JobMessage();
		jobMessage.setMessage(message);
		jobMessage.setJob(job);
		jobMessage.setLevel(level);
		jobMessage.setDate( new Date() );
		em.persist(jobMessage);
	}
	
	//----------------------------------------------------------------------------------------
	
	private User getEnqueueUser()
	{
		User user = loggedUser.getUser();
		if ( user == null )
		{
			user = us.findSystemUser();
		}
		return user;
	}
	
	//----------------------------------------------------------------------------------------
	// Enqueue methods
	//----------------------------------------------------------------------------------------
	
	public void enqueueQ8ParserJob( ImportInfo info )
	{
		Q8ParserJob job = new Q8ParserJob( info, getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueAgipParserJob( ImportInfo info )
	{
		AgipParserJob job = new AgipParserJob( info, getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueSoccorsiParserJob( ImportInfo info, Integer month, Integer year )
	{
		SoccorsiParserJob job = new SoccorsiParserJob( info, month, year, getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueRemoveRifornimentiJob( ImportInfo info )
	{
		RemoveRifornimentiJob job = new RemoveRifornimentiJob( info, getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueRemoveSoccorsiJob( ImportInfo info )
	{
		RemoveSoccorsiJob job = new RemoveSoccorsiJob( info, getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueSoccorsiJob( Integer mese, Integer anno )
	{
		SoccorsiJob job = new SoccorsiJob( mese, anno , getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueAnomalieJob(  Period period )
	{
		AnomalieJob job = new AnomalieJob( period , getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueAnomalieCarburanteJob( CodificaCarburante codifica )
	{
		AnomalieCarburanteJob job = new AnomalieCarburanteJob( codifica, getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}

	//----------------------------------------------------------------------------------------
	
	public void enqueueAnagraficaParserJob(ImportInfo info) 
	{
		AnagraficaParserJob job = new AnagraficaParserJob( info , getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueRiepilogoJob( Integer mese, Integer anno )
	{
		RiepilogoJob job = new RiepilogoJob( new MonthPeriod( mese, anno ), getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
	
	//----------------------------------------------------------------------------------------
	
	public void enqueueConsumiJob(  Period period )
	{
		ConsumiJob job = new ConsumiJob( period , getEnqueueUser() );
		em.persist( job.toJob() );
		log( job );
	}
}
