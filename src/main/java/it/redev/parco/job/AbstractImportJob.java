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

import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.core.Job;
import it.redev.parco.session.MonthPeriod;
import it.redev.parco.session.Period;

import java.util.Date;

public abstract class AbstractImportJob extends AbstractJob 
{
	private ImportInfo info;
	
	//---------------------------------------------------------------------------------------------------------
	
	@Override
	public Job toJob() 
	{
		Job job = super.toJob();
		job.setImportInfo( info );
		return job;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@Override
	protected void init(Job job) 
	{
		super.init(job);
		this.info = job.getImportInfo();
	}
	
	//---------------------------------------------------------------------------------------------------------

	public ImportInfo getInfo() {
		return info;
	}

	public void setInfo(ImportInfo info) {
		this.info = info;
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected String getFilePath()
	{
		return getConfigManager().getImportPath() + "//" + getInfo().getFile();
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void verifyDate( Date date )
	{
		if ( info.getDataInizio() == null || info.getDataFine() == null )
		{
			info.setDataInizio( date );
			info.setDataFine( date );
		}
		else
		{
			if ( date.before( info.getDataInizio() ) )
			{
				info.setDataInizio( date );
			}
			else if ( date.after( info.getDataFine() ) )
			{
				info.setDataFine( date );
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void enqueueLinkedJob()
	{
		// Enqueue job anomalie
		AnomalieJob anojob = new AnomalieJob( new Period( getInfo().getDataInizio(), getInfo().getDataFine() ), getUser() );
		getEntityManager().persist( anojob.toJob() );
		
		enqueueRicalcoli();
		
		// Enqueue job consumi
		ConsumiJob cjob = new ConsumiJob( new Period( getInfo().getDataInizio(), getInfo().getDataFine() ), getUser() );
		getEntityManager().persist( cjob.toJob() );
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	protected void enqueueRicalcoli()
	{
		// Enqueue job riepiloghi
		RiepilogoJob rjob;
		MonthPeriod period = new MonthPeriod( getInfo().getDataInizio() );
		do
		{
			rjob = new RiepilogoJob( period , getUser() );
			getEntityManager().persist( rjob.toJob() );
			period.next();
		}
		while ( !period.after( getInfo().getDataFine() ) );
	}
}
