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

package it.redev.parco.ext;

import it.inspired.exporter.ExcelExporter;
import it.redev.parco.core.MessagesManager;
import it.inspired.utils.DateUtils;
import it.inspired.utils.StringUtils;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@SuppressWarnings("serial")
public class ExcelExportableModelEntityQuery<T> extends LazyModelEntityQuery<T> 
{
	@Inject
	MessagesManager message;
	
	private int first 	= 0;
	private int pageSize= 10;
	
	private ExcelExporter exporter = new ExcelExporter(){
		protected String getText(String key) 
		{
			return message.getMessage(key);
		};
		protected List<String> getExcludedProperties() {
			return Arrays.asList( new String[]{"removed","version","removalDate"} );
		};
	};
	
	//--------------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "export_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}
	
	protected List<String> getExcludedProperties() {
		return Arrays.asList( new String[]{"removed","version","removalDate"} );
	}
	
	//--------------------------------------------------------------------------------------
	
	private List<T> loadNext() {
		List<T> nextList = getDataModel().load( first, pageSize, null, null, null);
		first += pageSize;
		return nextList;
	}
	
	private void export() throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		first 	= 0;
		pageSize= 10;
		List<T> list = loadNext();
		exporter.init();
		if ( list != null && !list.isEmpty() )
		{
			while ( list != null && !list.isEmpty() )
			{
				exporter.export( list );
				list = loadNext();
			}
		}
		exporter.finalyze();
	}
	
	//--------------------------------------------------------------------------------------
	
		public void download()
		{
			refresh();
			
			FacesContext fc = FacesContext.getCurrentInstance();
		    ExternalContext ec = fc.getExternalContext();

		    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
		    ec.setResponseContentType("application/vnd.ms-excel"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
		    //ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
		    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + getFileName() + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

		    // Now you can write the InputStream of the file to the above OutputStream the usual way.
		    // ...
		    try 
		    {
		    	
		    	export();
				OutputStream output = ec.getResponseOutputStream();
				exporter.write( output );
			} 
		    catch (IOException e) {
				e.printStackTrace();
			}
		   
		    catch (IllegalArgumentException e) {
				log.error( StringUtils.getStackTrace( e ) );
			} catch (IntrospectionException e) {
				log.error( StringUtils.getStackTrace( e ) );
			} catch (IllegalAccessException e) {
				log.error( StringUtils.getStackTrace( e ) );
			} catch (InvocationTargetException e) {
				log.error( StringUtils.getStackTrace( e ) );
			}

		    fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
		}

}
