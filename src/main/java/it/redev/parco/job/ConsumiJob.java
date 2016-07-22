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

import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.User;
import it.redev.parco.model.core.JobParameter;
import it.redev.parco.model.report.Consumo;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.session.Period;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ConsumiJob extends RifornimentiAbstractJob
{
	private Period period;
	private long totConsumi = 0;
	
	private List<Consumo> consumi;
	
	//-------------------------------------------------------------------------------------
	
	public ConsumiJob() {}
	
	public ConsumiJob( Period period, User user ) 
	{
		setUser( user );
		this.period = period;
	}
	
	public ConsumiJob( Date dataInizio, Date dataFine, User user ) 
	{
		this( new Period( dataInizio, dataFine ), user );
	}
	
	//-------------------------------------------------------------------------------------
	
	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
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
			this.period = Period.parse( params.get( "period" ) );
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	public boolean isRepeatable() 
	{
		return true;
	}
		
	//-------------------------------------------------------------------------------------
	
	public int deleteConsumi() throws Exception
	{
		int del = getEntityManager().createNativeQuery( 
				"DELETE FROM CONSUMI " +
				"WHERE DATA >= :dataInizio " +
				"AND   DATA <= :dataFine ")
			.setParameter( "dataInizio", period.getMorningPeriodFrom() )
			.setParameter( "dataFine", period.getMidnighthPeriodTo() )
			.executeUpdate();
		
		if ( del > 0 )
		{
			addInfoMessage( "Rimossi " + del + " consumi del periodo" );
		}
		return del;
	}
	
	//------------------------------------------------------------------------------------------
	
	protected void addConsumo( Consumo consumo )
	{
		if ( consumi == null )
		{
			consumi = new ArrayList<Consumo>();
			totConsumi = 0;
		}
		if ( consumo != null )
		{
			consumi.add( consumo );
			totConsumi++;
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	protected void flushConsumi() throws Exception
	{
		if ( consumi != null )
		{
			for ( Consumo con : consumi )
			{
				persist( con );
			}
			commit();
			consumi.clear();
		}
	}

	//-------------------------------------------------------------------------------------
	
	public Consumo calcolaConsumo( Rifornimento rif, Rifornimento rifPred) throws Exception
	{
		Consumo con = new Consumo();
		con.setRifornimento( rif );
		con.setRifornimentoPrecedente( rifPred );
		con.setMezzo( rif.getMezzo() );
		con.setData( rif.getData() );
		con.setChilometri( rif.getChilometraggio() - rifPred.getChilometraggio() );
		con.setQuantita( rif.getQuantita() );
		
		BigDecimal consumo = ( new BigDecimal( con.getChilometri() ) ).divide( con.getQuantita(), 2, RoundingMode.HALF_UP );
		con.setConsumo( consumo );
		
		return con;
	}
	
	//-------------------------------------------------------------------------------------
	
	private void elabora(Rifornimento rif) throws Exception 
	{
		Rifornimento pred = super.findPred( rif );
		
		if ( pred != null )
		{
			addConsumo( calcolaConsumo( rif, pred ) );
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException
	{
		try 
		{
			deleteConsumi();
		} 
		catch (Exception e)
		{
			throw new DatabaseException( e );
		}
		
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
				flushConsumi();
			} 
			catch (Exception e) 
			{
				throw new DatabaseException( e );
			}
			rifornimenti = super.findNext(from, to);
		}
		try 
		{
			if ( totConsumi != 0 )
			{
				addInfoMessage( "Generati " + totConsumi + " consumi " );
			}
		}
		catch (Exception e) {}
	}
}
