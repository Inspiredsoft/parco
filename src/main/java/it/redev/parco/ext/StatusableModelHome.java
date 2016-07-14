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

package it.redev.parco.ext;

import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.StatoInfo;
import it.redev.parco.model.Statusable;
import it.inspired.utils.DateUtils;

import java.util.Date;

import javax.inject.Inject;


@SuppressWarnings("serial")
public class StatusableModelHome<T extends Statusable> extends ModelHome<T>
{
	@Inject
	LoggedUser lu;
	
	private StatoInfo 	newStatus;
	private Date 		minStatusDate;
	
	//--------------------------------------------------------------------------
	
	public StatoInfo getNewStatus() {
		return newStatus;
	}
	
	public void setNewStatus(StatoInfo newStatus) {
		this.newStatus = newStatus;
	}
	
	public Date getMinStatusDate() {
		return minStatusDate;
	}
	
	public void setMinStatusDate(Date minStatusDate) {
		this.minStatusDate = minStatusDate;
	}
	
	//--------------------------------------------------------------------------
	
	private void initStatus()
	{	
		minStatusDate = DateUtils.addDay( getInstance().getStato().getDataStato(), 1 );
		newStatus = new StatoInfo( DateUtils.max( new Date(), minStatusDate ) );
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected void initInstance() 
	{
		super.initInstance();
		if ( isManaged() )
		{
			initStatus();
		}
	}

	//--------------------------------------------------------------------------
	
	public void changeStatus()
	{
		if ( newStatus.getStato() != null )
		{
			newStatus.setUtenteStato( lu.getUser() );
			getInstance().changeStatus( newStatus );
			initStatus();
		}
	}
}
