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
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.mezzi.Soccorso;
import it.inspired.utils.DateUtils;

import javax.persistence.Query;

public class RemoveSoccorsiJob extends AbstractImportJob 
{	
	public RemoveSoccorsiJob(){}
		
	public RemoveSoccorsiJob(ImportInfo info, User user) 
	{
		super.setInfo( info );
		setUser( user );
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException 
	{
		try
		{
			Integer year = DateUtils.getYear( getInfo().getDataInizio() );
			Integer month = DateUtils.getMonth( getInfo().getDataInizio() ) + 1;

			int soc = removeSoccorsi( month, year );
			
			addInfoMessage( "Eliminazione soccorsi del periodo " + month + "/" + year );
			addInfoMessage( "Eliminati " + soc + " soccorsi " );
		} 
		catch (Exception e) 
		{
			throw new DatabaseException( e );
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	private int removeSoccorsi(Integer month, Integer year )
	{
		Query query = getEntityManager().createQuery( 
				"DELETE FROM " + Soccorso.class.getName() + " soc " +
				"WHERE soc.mese = :mese " +
				"AND   soc.anno = :anno " )
				.setParameter( "mese", month )
				.setParameter( "anno", year );
		
		return query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public boolean isRepeatable() 
	{
		return true;
	}
	
}
