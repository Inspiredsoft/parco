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

import it.redev.parco.model.core.Mail;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class MailService extends BaseService
{
	public int removeOlder( Date date, String status )
	{
		Query query = em.createQuery( 
				"DELETE FROM MailMessage mm " +
				"WHERE mm.mail IN (SELECT m FROM Mail m WHERE m.addDate < :date AND m.status = :status)" )
				.setParameter( "date", date )
				.setParameter( "status", status );
		query.executeUpdate();
		
		query = em.createQuery( 
				"DELETE FROM MailParameter mp " +
				"WHERE mp.mail IN (SELECT m FROM Mail m WHERE m.addDate < :date AND m.status = :status)" )
				.setParameter( "date", date )
				.setParameter( "status", status );
		query.executeUpdate();
		
		query = em.createQuery( 
					"DELETE FROM Mail m " +
					"WHERE m.addDate < :date " +
					"AND m.status = :status" )
					.setParameter( "date", date )
					.setParameter( "status", status );
		return query.executeUpdate();
	}

	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Mail> findByState( String status )
	{
		Query query = em.createQuery( 
					"FROM Mail m " +
					"WHERE m.status = :status " +
					"ORDER BY id" )
					.setParameter( "status", status );
		return query.getResultList();
	}
	
	//------------------------------------------------------------
	
	public List<Mail> findWaiting()
	{
		return findByState( Mail.Status.WAITING );
	}
	
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Mail> findExecutable()
	{
		Query query = em.createQuery( 
				"FROM Mail m " +
				"WHERE m.status = :wait " +
				"OR    m.status = :delayed " +
				"ORDER BY id" )
				.setParameter( "wait", 		Mail.Status.WAITING )
				.setParameter( "delayed", 	Mail.Status.DELAYED );
		return query.getResultList();
	}
	
	//------------------------------------------------------------
	
	public int resetOnStartup()
	{
		Query query = em.createQuery( "UPDATE Mail m SET m.status = :waiting WHERE m.status = :running" )
					.setParameter( "waiting", Mail.Status.WAITING )
					.setParameter( "running", Mail.Status.RUNNING );
		return query.executeUpdate();
	}
	
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Mail> findOlder( Date date )
	{
		Query query = em.createQuery( 
					"FROM Mail m " +
					"WHERE m.addDate < :date " )
					.setParameter( "date", date );
		return query.getResultList();
	}
}
