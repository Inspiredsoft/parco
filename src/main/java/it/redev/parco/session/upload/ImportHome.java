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

package it.redev.parco.session.upload;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.ext.ModelHome;
import it.redev.parco.job.JobManager;
import it.redev.parco.model.ImportInfo;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class ImportHome extends ModelHome<ImportInfo>
{
	@Inject
	JobManager jm;
	
	@Inject
	MessagesManager message;
	
	public void removeJob()
	{
		if ( getInstance().isRifornimentoAgip() || getInstance().isRifornimentoQ8() )
		{
			jm.enqueueRemoveRifornimentiJob( getInstance() );
		}
		else if ( getInstance().isSoccorsi() )
		{
			jm.enqueueRemoveSoccorsiJob( getInstance() );
		}
		message.info( "message.upload.removed" );
	}
	
}
