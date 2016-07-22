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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.seam.cron.api.scheduling.Every;
import org.jboss.seam.cron.api.scheduling.Interval;
import org.jboss.seam.cron.api.scheduling.Trigger;

@SuppressWarnings("serial")
public abstract class AbstractScheduler implements Serializable
{
	private static SchedulerMonitor monitor = new SchedulerMonitor();
	
	@PersistenceContext
	EntityManager em;
	
	@Inject
	UserTransaction utx;
	
	private Every every;
	
	//----------------------------------------------------------------------------------------
	
	public abstract void launcher(Trigger trigger );
	
	//----------------------------------------------------------------------------------------
	
	@PostConstruct
	public void init()
	{
		monitor.started( this.getClass() );
	}
	
	//----------------------------------------------------------------------------------------
	
	protected synchronized boolean activable()
	{
		if ( monitor.isActive( this.getClass() ) )
		{
			return false;
		}
		else
		{
			monitor.activate(this.getClass() );
			monitor.executed( this.getClass() );
			return true;
		}
	}
	
	//----------------------------------------------------------------------------------------

	protected synchronized void deactivate()
	{
		monitor.deactivate(this.getClass() );
	}
	
	//----------------------------------------------------------------------------------------
	
	public Date getStartDate() 
	{
		return monitor.getStartDate( this.getClass() );
	}

	public Date getLastExecution()
	{
		return monitor.getLastExecution( this.getClass() );
	}

	public Long getExecutions() 
	{
		return monitor.getExecutions( this.getClass() );
	}

	//----------------------------------------------------------------------------------------

	public Every getEveryAnnotation()
	{
		if ( every == null )
		{
			try 
			{
				Method method = this.getClass().getMethod( "launcher", Trigger.class );
				Annotation[][] annots = method.getParameterAnnotations();
				for ( int i=0; i < annots.length; i++ )
				{
					for ( int j=0; j < annots[i].length; j++ )
					{
						if ( annots[i][j] instanceof Every )
						{
							every = (Every) annots[i][j];
							break;
						}
					}
				}
			} 
			catch (SecurityException e) 
			{
			} catch (NoSuchMethodException e)
			{
			}
		}
		return every;
	}
	
	//----------------------------------------------------------------------------------------
	
	public Integer getNth()
	{
		return ( getEveryAnnotation() != null ? getEveryAnnotation().nth() : null );
	}
	
	//----------------------------------------------------------------------------------------
	
	public Interval getInterval()
	{
		return ( getEveryAnnotation() != null ? getEveryAnnotation().value() : null );
	}
	
	//----------------------------------------------------------------------------------------
	
	protected synchronized void begin() throws Exception 
	{
		utx.begin();
		em.joinTransaction();
	}
	
	//----------------------------------------------------------------------------------------
	
	protected synchronized void commit() throws Exception 
	{ 
		em.flush();
		utx.commit();
	}
	
	//----------------------------------------------------------------------------------------
	
	protected synchronized void commitBegin() throws Exception 
	{ 
		em.flush();
		utx.commit();
		utx.begin();
		em.joinTransaction();
	}
	
	//----------------------------------------------------------------------------------------
	
	protected void abort() 
	{
		try
		{
			utx.rollback();
			utx.begin();
			em.joinTransaction();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------------------------------------
	
	protected void persist(Object entity)
	{
		em.persist(entity);
	}
}
