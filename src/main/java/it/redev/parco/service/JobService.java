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

package it.redev.parco.service;

import it.redev.parco.model.core.Job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class JobService extends BaseService
{	
	public int removeOlder( Date date, String status )
	{
		Query query = em.createQuery( 
				"DELETE FROM JobMessage jm " +
				"WHERE jm.job IN (SELECT j FROM Job j where j.addDate < :date AND j.status = :status)" )
				.setParameter( "date", date )
				.setParameter( "status", status );
		query.executeUpdate();
		
		query = em.createQuery( 
				"DELETE FROM JobParameter jp " +
				"WHERE jp.job IN (SELECT j FROM Job j where j.addDate < :date AND j.status = :status)" )
				.setParameter( "date", date )
				.setParameter( "status", status );
		query.executeUpdate();
		
		query = em.createQuery( 
					"DELETE FROM Job j " +
					"WHERE j.addDate < :date " +
					"AND j.status = :status" )
					.setParameter( "date", date )
					.setParameter( "status", status );
		return query.executeUpdate();
	}

	//------------------------------------------------------------
		
	@SuppressWarnings("unchecked")
	public List<Job> findOlder( Date date )
	{
		Query query = em.createQuery( 
					"FROM Job j " +
					"WHERE j.addDate < :date " )
					.setParameter( "date", date );
		return query.getResultList();
	}
	
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Job> findByState( String status )
	{
		Query query = em.createQuery( 
					"FROM Job j " +
					"WHERE j.status = :status " +
					"ORDER BY id" )
					.setParameter( "status", status );
		return query.getResultList();
	}
	
	//------------------------------------------------------------
	
	public List<Job> findWaiting()
	{
		return findByState( Job.Status.WAITING );
	}
	
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Job> findExecutable()
	{
		Query query = em.createQuery( 
				"FROM Job j " +
				"WHERE j.status = :wait " +
				"OR    j.status = :delayed " +
				"ORDER BY id" )
				.setParameter( "wait", 		Job.Status.WAITING )
				.setParameter( "delayed", 	Job.Status.DELAYED );
		return query.getResultList();
	}
	
	//------------------------------------------------------------
	
	public int resetOnStartup()
	{
		Query query = em.createQuery( "UPDATE Job j SET j.status = :waiting WHERE j.status = :running" )
					.setParameter( "waiting", Job.Status.WAITING )
					.setParameter( "running", Job.Status.RUNNING );
		return query.executeUpdate();
	}
}
