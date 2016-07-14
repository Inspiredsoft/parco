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

package it.redev.parco.session.rifornimenti;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.job.JobManager;
import it.redev.parco.model.mezzi.CodificaCarburante;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class CodificaCarburanteHome extends ModelHome<CodificaCarburante> 
{
	@Inject
	JobManager jobManager;
	
	@Inject
	MessagesManager message;
	
	@Override
	public String save() 
	{
		String res = super.save();
		if ( res.equals("saved") )
		{
			jobManager.enqueueAnomalieCarburanteJob( getInstance() );
			message.info( "message.job.new.anocarb", getInstance().getValore() );
		}
		return res;
	}
}
