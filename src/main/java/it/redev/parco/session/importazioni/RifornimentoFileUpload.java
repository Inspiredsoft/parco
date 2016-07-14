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

package it.redev.parco.session.importazioni;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.job.JobManager;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.service.ImportService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class RifornimentoFileUpload extends BaseFileUpload 
{
	@Inject
	MessagesManager message;

	@Inject
	JobManager jm;
	
	@Inject
	LoggedUser lu;
	
	@Inject
	ImportService is;

	//---------------------------------------------------------------------------------
	
	public void listenerQ8(FileUploadEvent event) throws Exception 
	{
		String name = saveFile( event.getUploadedFile() );
		
		ImportInfo info = is.newImport( ImportInfo.Tipo.RIF_Q8, name, lu.getUser() );
		
		jm.enqueueQ8ParserJob( info );

		message.info( "message.upload.q8", name );
	}
	
	//---------------------------------------------------------------------------------
	
	public void listenerAgip(FileUploadEvent event) throws Exception 
	{
		String name = saveFile( event.getUploadedFile() );
		
		ImportInfo info = is.newImport( ImportInfo.Tipo.RIF_AGIP, name, lu.getUser() );
		
		jm.enqueueAgipParserJob( info );

		message.info( "message.upload.agip", name );
	}
}
