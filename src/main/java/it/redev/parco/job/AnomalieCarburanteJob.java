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
import it.redev.parco.model.mezzi.CodificaCarburante;
import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.model.rifornimenti.Rifornimento;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

public class AnomalieCarburanteJob extends AnomalieAbstractJob 
{
	private int queryIndex 	= 0;
	private int queryMax	= 10;
	
	private CodificaCarburante codifica;
	
	//-------------------------------------------------------------------------------------
	
	public AnomalieCarburanteJob(){}
			
	public AnomalieCarburanteJob( CodificaCarburante codifica,  User user )
	{
		this.codifica = codifica;
		setUser( user );
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected List<Rifornimento> findNext()
	{
		Query query = getEntityManager().createQuery(
						"FROM Rifornimento rif " +
						"WHERE rif.prodotto = :prodotto " )
					.setParameter( "prodotto", codifica.getValore() );
		
		query.setFirstResult( queryIndex );
		query.setMaxResults( queryMax );
		
		queryIndex += queryMax;
		
		return query.getResultList();
	}
	
	//-------------------------------------------------------------------------------------
	
	protected void removeAnomalieCarburante( Rifornimento rif )
	{
		for ( Anomalia anomalia : rif.getAnomalie() )
		{
			if ( anomalia.isTipoCarburante() || anomalia.isTipoCarburanteAmmesso() )
			{
				getEntityManager().remove( anomalia );
			}
		}
		rif.getAnomalie().clear();
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException
	{
		List<Rifornimento> rifornimenti = findNext();
		
		while ( !rifornimenti.isEmpty() )
		{
			try 
			{
				for ( Rifornimento rif : rifornimenti )
				{
					removeAnomalieCarburante( rif );
					addAnomalie( verificaCarburante( rif ) );
					addAnomalie( verificaCarburanteAmmesso( rif ) );
				}
				flushAnomalie();
			} 
			catch (Exception e) 
			{
				throw new DatabaseException( e );
			}
			rifornimenti = findNext();
		}
		try 
		{
			addInfoMessage( "Generate " + totAnomalie + " anomalie " );
			writeErrors();
		}
		catch (Exception e) {}
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	protected List<JobParameter> getState( ) 
	{
		List<JobParameter> params = super.getState();
		params.add( new JobParameter( "codificaCarburanteId",  "Codifica Carburante Id", codifica.getId() ) );
		return params;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected void setState(HashMap<String, String> params)
	{
		super.setState( params );
		codifica = getEntityManager().find( CodificaCarburante.class, Integer.parseInt( params.get( "codificaCarburanteId" ) ) );
	}

}
