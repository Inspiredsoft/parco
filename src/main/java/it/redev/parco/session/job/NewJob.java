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

package it.redev.parco.session.job;

import it.redev.parco.core.ConfigManager;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.job.JobManager;
import it.redev.parco.session.MonthPeriod;
import it.redev.parco.session.Period;
import it.inspired.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class NewJob implements Serializable
{
	@Inject
	JobManager jm;
	
	@Inject
	MessagesManager message;
	
	@Inject
	ConfigManager cm;
	
	private Period 			anomaliePeriod 	= Period.monthPeriod( new Date() );
	private Period  		consumiPeriod 	= Period.monthPeriod( new Date() );
	private MonthPeriod  	soccorsiPeriod 	= MonthPeriod.current();
	private MonthPeriod  	riepiloghiPeriod= MonthPeriod.current();
	
	
	//---------------------------------------------------------------------------
	
	public void init()
	{
		anomaliePeriod 	= Period.monthPeriod( new Date() );
		consumiPeriod 	= Period.monthPeriod( new Date() );
		soccorsiPeriod 	= MonthPeriod.current();
		riepiloghiPeriod= MonthPeriod.current();
	}
	
	//---------------------------------------------------------------------------------
	
	public List<Integer> getMonths()
	{
		return soccorsiPeriod.getMonths();
	}
	
	//---------------------------------------------------------------------------
	
	public Period getAnomaliePeriod() {
		return anomaliePeriod;
	}

	public void setAnomaliePeriod(Period anomaliePeriod) {
		this.anomaliePeriod = anomaliePeriod;
	}

	public Period getConsumiPeriod() {
		return consumiPeriod;
	}

	public void setConsumiPeriod(Period consumiPeriod) {
		this.consumiPeriod = consumiPeriod;
	}

	public MonthPeriod getSoccorsiPeriod() {
		return soccorsiPeriod;
	}

	public void setSoccorsiPeriod(MonthPeriod soccorsiPeriod) {
		this.soccorsiPeriod = soccorsiPeriod;
	}

	public MonthPeriod getRiepiloghiPeriod() {
		return riepiloghiPeriod;
	}

	public void setRiepiloghiPeriod(MonthPeriod riepiloghiPeriod) {
		this.riepiloghiPeriod = riepiloghiPeriod;
	}
	
	//---------------------------------------------------------------------------

	public void anomalieJob()
	{
		if ( anomaliePeriod.isValid() )
		{
			jm.enqueueAnomalieJob( anomaliePeriod );
		
			message.info( "message.job.new.anomalie", DateUtils.format( anomaliePeriod.getPeriodFrom(), cm.getShortDateFormat() ), DateUtils.format( anomaliePeriod.getPeriodTo(), cm.getShortDateFormat() ) );
		}
	}

	public void soccorsiJob()
	{
		jm.enqueueSoccorsiJob( soccorsiPeriod.getMonth(), soccorsiPeriod.getYear() );
		
		message.info( "message.job.new.soccorsi", soccorsiPeriod.getMonth()+"/"+soccorsiPeriod.getYear() );
	}
	
	public void riepilogoJob()
	{
		jm.enqueueRiepilogoJob( riepiloghiPeriod.getMonth(), riepiloghiPeriod.getYear() );
		
		message.info( "message.job.new.riepilogo", riepiloghiPeriod.getMonth()+"/"+riepiloghiPeriod.getYear() );
	}
	
	public void consumiJob()
	{
		if ( consumiPeriod.isValid() )
		{
			jm.enqueueConsumiJob( consumiPeriod );
		
			message.info( "message.job.new.consumi", DateUtils.format( consumiPeriod.getPeriodFrom(), cm.getShortDateFormat() ), DateUtils.format( consumiPeriod.getPeriodTo(), cm.getShortDateFormat() ) );
		}
	}

}
