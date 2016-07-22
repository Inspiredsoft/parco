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
import it.redev.parco.session.MonthPeriod;

public class RiepilogoJob extends RiepilogoAbstractJob 
{
	public RiepilogoJob(){}
	
	public RiepilogoJob( MonthPeriod period, User user)
	{
		super();
		setUser( user );
		setPeriod( period );
	}

	//------------------------------------------------------------------------------------

	@Override
	public void execute() throws DatabaseException, RemoteSafeException
	{
		try
		{
			deleteRiepilogoRifMezzi();
			calcolaRiepilogoRifMezzi();
			commit();
			
			deleteRiepilogoRifMezzoPostazioni();
			calcolaRiepilogoRifMezzoPostazioni();
			commit();
			
			deleteRiepilogoRifPostazioni();
			calcolaRiepilogoRifPostazioni();
			commit();
			
			deleteRiepilogoRifornimenti();
			calcolaRiepilogoRifornimenti();
			commit();
			
			deleteRiepilogoAnomalie();
			calcolaRiepilogoAnomalie();
			commit();
		} 
		catch (Exception e) 
		{
			throw new DatabaseException( e );
		}
	}
}
