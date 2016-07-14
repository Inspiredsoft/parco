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

import it.redev.parco.core.ConfigManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.inject.Inject;

import org.apache.poi.util.IOUtils;
import org.richfaces.model.UploadedFile;

@SuppressWarnings("serial")
public class BaseFileUpload implements Serializable
{
	@Inject
	ConfigManager cm;
	
	public String saveFile( UploadedFile file ) throws IOException
	{
		String name = file.getName().replace(" ", "_");
		
		//ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
		File result = new File( cm.getImportPath() + "//" + name );
		
		FileOutputStream output = new FileOutputStream(result);
		
		InputStream input = file.getInputStream();
		
		IOUtils.copy( input, output );
		
		input.close( );
		
		output.close( );
		
		return name;
	}
}
