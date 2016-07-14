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

package it.redev.parco.core.view;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.FacesContext;

@SuppressWarnings("serial")
public class View implements Serializable
{
	private String 	id;
	private boolean marked;
	private String 	selection;
	private String  conversationId;
	
	//--------------------------------------------------------------------------------
	
	public View( String conversationId )
	{
		try 
		{
			this.id = FacesContext.getCurrentInstance().getViewRoot().getViewId();
			this.conversationId = conversationId;
			marked = false;
		} 
		catch ( Exception ex )
		{
			
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public String getId() 
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getPath()
	{
		String path = "";
		int index = id.lastIndexOf( "/" );
		if ( index >= 0 )
		{
			path = id.substring( 0, index ); 
		}
		return path;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getName()
	{	String name = "";
		int index = id.lastIndexOf( "/" );
		if ( index >= 0 )
		{
			name = id.substring( index + 1 ).replace( "." + getExtention(), "" );
		}
		return name;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getExtention()
	{
		String ext = "";
		int index = id.lastIndexOf( "." );
		if ( index >= 0 )
		{
			ext = id.substring( index + 1 );
		}
		return ext;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getConversationId() 
	{
		return conversationId;
	}
	
	public void setConversationId(String conversationId)
	{
		this.conversationId = conversationId;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isMarked() 
	{
		return marked;
	}

	public void setMarked(boolean marked) 
	{
		this.marked = marked;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getSelection() 
	{
		return selection;
	}

	public void setSelection(String selection) 
	{
		this.selection = selection;
	}

	//--------------------------------------------------------------------------------
	
	public boolean isSeamPage()
	{
		return this.id != null && ( this.id.endsWith( ".xhtml" ) || this.id.endsWith( ".seam" ) );
	}
	
	//--------------------------------------------------------------------------------

	public boolean equals( View view )
	{
		return view != null && this.id.equals( view.getId() ) && this.conversationId.equals( view.getConversationId() );
	}
	
	//--------------------------------------------------------------------------------

	public boolean equalsId( View view )
	{
		return view != null && this.id.equals( view.getId() );
	}
	
	//--------------------------------------------------------------------------------

	public boolean equalsConversationId( View view )
	{
		return view != null && this.conversationId.equals( view.getConversationId() );
	}
	
	//--------------------------------------------------------------------------------
	
	public void goTo( FacesContext context ) throws IOException
	{
		String path = context.getExternalContext().getRequestContextPath();
		context.getExternalContext().redirect( path +  this.id );
	}
	
	//--------------------------------------------------------------------------------
	
	public String toString()
	{
		return this.id;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isSame( View view )
	{
		return ( view == null ? false : this.id.equals( view.getId() ) );
	}
}
