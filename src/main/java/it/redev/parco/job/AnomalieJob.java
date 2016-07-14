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

import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.User;
import it.redev.parco.model.core.JobParameter;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.session.Period;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AnomalieJob extends AnomalieAbstractJob 
{
	private Period 		period;
	
	//-------------------------------------------------------------------------------------
	
	public AnomalieJob(){}
			
	public AnomalieJob( Period period,  User user )
	{
		this.period = period;
		setUser( user );
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException 
	{
		Date from 	= period.getMorningPeriodFrom();
		Date to		= period.getMidnighthPeriodTo();
		List<Rifornimento> rifornimenti = super.findNext(from, to);
		
		while ( !rifornimenti.isEmpty() )
		{
			try 
			{
				for ( Rifornimento rif : rifornimenti )
				{
					elabora( rif );
				}
				flushAnomalie();
			} 
			catch (Exception e) 
			{
				throw new DatabaseException( e );
			}
			rifornimenti = super.findNext(from, to);
		}
		try 
		{
			addInfoMessage( "Generate " + totAnomalie + " anomalie " );
			writeErrors();
		}
		catch (Exception e) {}
	}

	//-------------------------------------------------------------------------------------
	
	private void elabora(Rifornimento rif) throws Exception 
	{
		removeAnomalie( rif );
		
		addAnomalie( verificaChilometraggio( rif ) );
		
		Rifornimento pred = super.findPred( rif );
		addAnomalie( verificaChilometraggioPred( rif, pred ) );
		
		addAnomalie( verificaFrequenza( rif, pred ) );
		
		addAnomalie( verificaConsumo( rif, pred ) );
		
		addAnomalie( verificaCartaMezzo( rif ) );
		
		addAnomalie( verificaCarburante( rif ) );
		
		addAnomalie( verificaSerbatoio( rif ) );
		
		addAnomalie( verificaServizio( rif ) );
		
		addAnomalie( verificaCarburanteAmmesso( rif ) );
		
		Rifornimento predPinCard = super.findPredPinCard(rif);
		addAnomalie( verificaFrequenzaPinCard( rif, predPinCard ) );
		
		addAnomalie( verificaPinCard( rif ) );
		
		addAnomalie( verificaCarta( rif ) );
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected List<JobParameter> getState( ) 
	{
		List<JobParameter> params =super.getState();
		params.add( new JobParameter( "period",  "Periodo", period.toString() ) );
		return params;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected void setState(HashMap<String, String> params)
	{
		super.setState( params );
		try 
		{
			period = Period.parse( params.get( "period" ) );
		} 
		catch (ParseException e) 
		{
			try {
				addErrorMessage( e.getLocalizedMessage() );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public boolean isRepeatable() 
	{
		return true;
	}
}
