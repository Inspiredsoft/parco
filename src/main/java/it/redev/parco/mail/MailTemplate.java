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

package it.redev.parco.mail;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.mail.templating.velocity.VelocityTemplate;

public class MailTemplate 
{
	String templateText = null;
	VelocityTemplate template;
	Map<String, Object> values = new HashMap<String, Object>();
	
	private static final String subjectTag 	= "[SUBJECT]";
	private static final String textTag 	= "[TEXT CONTENT]";
	private static final String htmlTag 	= "[HTML CONTENT]";
	
	private static final String tagList[] =
	{
		subjectTag, htmlTag, textTag
	};
	
	//---------------------------------------------------------------------------------------
	
	public MailTemplate( String fileName ) throws FileNotFoundException
	{
		String name = fileName + ".template";
		InputStream res = this.getClass().getResourceAsStream( name );
		if ( res != null )
		{
			this.template = new VelocityTemplate( res );
		}
		else
		{
			throw new FileNotFoundException( name );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public MailTemplate( Class clazz ) throws FileNotFoundException
	{
		String name = clazz.getSimpleName() + ".template";
		InputStream res = this.getClass().getResourceAsStream( name );
		if ( res != null )
		{
			this.template = new VelocityTemplate( res );
		}
		else
		{
			throw new FileNotFoundException( name );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void put( String key, Object value )
	{
		values.put( key, value );
	}
	
	//---------------------------------------------------------------------------------------
	
	public String getSubject()
	{
		return findContent( MailTemplate.subjectTag );
	}

	//---------------------------------------------------------------------------------------
	
	public String getTextContent()
	{
		return findContent( MailTemplate.textTag );
	}

	//---------------------------------------------------------------------------------------
	
	public String getHtmlContent()
	{
		return findContent( MailTemplate.htmlTag );
	}

	//---------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//---------------------------------------------------------------------------------------

	private String findContent(String tag) 
	{
		String content = "";
		
		build();
		int start = this.templateText.indexOf( tag );
		
		if ( start >= 0 )
		{
			start += tag.length();
			
			int end = this.templateText.length();
			
			for ( String t : tagList )
			{
				int pos = this.templateText.indexOf( t, start );
				
				if ( pos >=0 && pos < end )
				{
					end = pos;
				}
			}
			content = this.templateText.substring( start, end ).trim();
		}
		return content;
	}

	//---------------------------------------------------------------------------------------
	
	private void build()
	{
		if ( this.templateText == null )
		{
			this.templateText = this.template.merge( this.values );
		}
	}
}
