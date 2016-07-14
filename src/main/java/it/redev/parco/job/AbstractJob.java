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

import it.redev.parco.core.ConfigManager;
import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.User;
import it.redev.parco.model.core.Job;
import it.redev.parco.model.core.JobMessage;
import it.redev.parco.model.core.JobParameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.seam.international.status.MessageFactory;


public abstract class AbstractJob 
{
	private EntityManager 	em;
	private Job				job;
	private User 			user;
	private UserTransaction utx;
	private ConfigManager	cm;
	
	//-----------------------------------------------------------------------------
	
	public AbstractJob() {}
	
	//-----------------------------------------------------------------------------
	
	public abstract void execute() throws DatabaseException, RemoteSafeException;
	
	//-----------------------------------------------------------------------------
	
	protected void init( Job job )
	{
		this.job			= job;
		this.user			= job.getUser();
	}
	
	//-----------------------------------------------------------------------------
	
	public Job toJob()
	{
		Job job = new Job();
		
		job.setJavaClass( this.getClass().getName() );
		job.setUser( getUser() );
		job.setStatus( Job.Status.WAITING );
		job.setTries( 0 );
		
		job.setParameters( getState() );
		
		for ( JobParameter param : job.getParameters() )
		{
			param.setJob( job );
		}
		
		return job;
	}
	
	//-----------------------------------------------------------------------------
	
	protected List<JobParameter> getState( ) 
	{
		return new ArrayList<JobParameter>();
	}

	//-----------------------------------------------------------------------------
	
	protected void setState(HashMap<String, String> params)
	{
		 
	}

	//-----------------------------------------------------------------------------
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	
	//-----------------------------------------------------------------------------
	
	public boolean isExecutable()
	{
		return true;
	}
	
	public boolean isRepeatable()
	{
		return false;
	}
	
	//-----------------------------------------------------------------------------

	public <T> T find(Class<T> aClass, Object obj) 
	{
		return em.find(aClass, obj);
	}
	
	//-----------------------------------------------------------------------------
	
	public synchronized void commit() throws Exception 
	{ 
		em.flush();
		utx.commit();
		utx.begin();
		em.joinTransaction();
	}
	
	//-----------------------------------------------------------------------------
	
	public void abort() 
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
	
	//-----------------------------------------------------------------------------
	
	public void flush() 
	{
		em.flush();
	}
	
	//-----------------------------------------------------------------------------
	
	public synchronized void persist(Object entity) 
	{
		em.persist(entity);
	}

	//-----------------------------------------------------------------------------
	
	public void addInfoMessage(String message) throws Exception
	{
		addMessage( JobMessage.Level.INFO, message, false );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addWarningMessage(String message) throws Exception
	{
		addMessage( JobMessage.Level.WARN, message, false );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addErrorMessage(String message) throws Exception
	{
		addMessage( JobMessage.Level.ERR, message, false );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addInfoMessage(String message, boolean doCommit) throws Exception
	{
		addMessage( JobMessage.Level.INFO, message, doCommit );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addWarningMessage(String message, boolean doCommit) throws Exception
	{
		addMessage( JobMessage.Level.WARN, message, doCommit );
	}
	
	//-----------------------------------------------------------------------------
	
	public void addErrorMessage(String message, boolean doCommit) throws Exception
	{
		addMessage( JobMessage.Level.ERR, message, doCommit );
	}
	
	//-----------------------------------------------------------------------------
	// Factory
	//-----------------------------------------------------------------------------
	
	public static AbstractJob create( UserTransaction utx, EntityManager em, ConfigManager cm, MessageFactory factory, Job job ) throws Exception
	{
		AbstractJob aj = (AbstractJob) Class.forName( job.getJavaClass() ).newInstance();
		aj.init( job );
		aj.em			= em;
		aj.utx			= utx;
		aj.cm			= cm;
		aj.setState( hashMapParameterFromList( job.getParameters() ) );
		
		return aj;
	}
	
	//-----------------------------------------------------------------------------
	// Private Methods
	//-----------------------------------------------------------------------------
	
	private static HashMap<String, String> hashMapParameterFromList( List<JobParameter> parametersList )
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(JobParameter jobParameter : parametersList)
		{
			map.put( jobParameter.getName(), jobParameter.getValue() );
		}
		return map;
	}
	
	//-----------------------------------------------------------------------------
	
	private void addMessage( String level, String message, boolean commit) throws Exception
	{
		JobMessage jobMessage = new JobMessage();
		jobMessage.setMessage(message);
		jobMessage.setJob(job);
		jobMessage.setLevel(level);
		jobMessage.setDate( new Date() );
		em.persist(jobMessage);
		
		if (commit)
			commit();
	}
	
}
