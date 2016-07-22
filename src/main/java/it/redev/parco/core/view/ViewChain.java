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

package it.redev.parco.core.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

import org.jboss.seam.faces.event.qualifier.Before;


@SuppressWarnings("serial")
@Named
@SessionScoped
public class ViewChain implements Serializable
{
	public static final String SELECTED_EVENT = "viewChain.selected."; 
	
	@Inject
	Conversation conversation;
	
	@Inject
	private Logger log;
	
	private Stack<View> views = new Stack<View>();
	
	private Integer selectedId = null;
	
	private String home = "/faces/private/home.xhtml";
	
	//--------------------------------------------------------------------------------
	//--
	//-- API METHODS
	//--
	//--------------------------------------------------------------------------------
	
	public Stack<View> getViews()
	{
		return views;
	}
	
	//--------------------------------------------------------------------------------
	
	public void goBackView() throws IOException
	{
		View view = getSecondLastView();
		view.goTo( FacesContext.getCurrentInstance() );
	}
	
	//--------------------------------------------------------------------------------
	
	public void goBack( Integer numView ) throws IOException
	{
		View view = null;
		if ( numView == 1 )
		{
			goBackView();
		}
		else
		{
			while ( numView >= 0 )
			{
				view = popView();
				numView--;
			}
			if ( view != null )
			{
				view.goTo( FacesContext.getCurrentInstance() );
			}
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public String getBackPage()
	{
		View backView = getBackView();
		return ( backView != null ? backView.getId() : "login" );
	}
	
	//--------------------------------------------------------------------------------
	
	public View getBackView()
	{
		return getSecondLastView();
	}
	
	//--------------------------------------------------------------------------------
	
	public String getTopPage()
	{
		View view = getTopView();
		return view.getId();
	}
	
	//--------------------------------------------------------------------------------
	
	public String getMarkedPage()
	{
		String marked = null;
		if ( !views.isEmpty() )
		{
			for ( int i = views.size() - 1; i >= 0; i-- )
			{
				if ( views.get( i ).isMarked() )
				{
					marked = views.get( i ).getId();
					break;
				}
			}
		}
		return marked;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getListPage()
  	{
  		View top = getTopView();
  		if ( top != null )
  		{
  			String base = top.getName()
  							.replace( "edit", "" )
  							.replace( "view", "" );
  			base = top.getPath() + "/" + base + "List." + top.getExtention();
  			return base;
  		}
  		return home;
  	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isSelectingForOrMarkingMode()
	{
		return isSelectingFor() || isMarkingMode();
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isMarkingMode()
	{
		return ( getMarkedPage() != null );
	}
	
	//--------------------------------------------------------------------------------
	
	public void mark()
	{
		View top = getTopView();
		if ( top != null )
		{
			top.setMarked( true );
			log.debug( "Marked view " + top );
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public String getTopSelectionPage()
	{
		String select = null;
		if ( !views.isEmpty() )
		{
			for ( int i = views.size() - 1; i >= 0; i-- )
			{
				if ( views.get( i ).getSelection() != null )
				{
					select = views.get( i ).getId();
					break;
				}
			}
		}
		return select;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getSelectionPage( String selection )
	{
		String select = null;
		if ( !views.isEmpty() && selection != null && !selection.isEmpty() )
		{
			for ( int i = views.size() - 1; i >= 0; i-- )
			{
				if ( selection.equals( views.get( i ).getSelection() ) )
				{
					select = views.get( i ).getId();
					break;
				}
			}
		}
		return select;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isSelectingFor()
	{
		return ( getTopSelectionPage() != null );
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isSelectingFor( String ...selections )
	{
		boolean select = false;
		for ( String selection : selections )
		{
			if ( getSelectionPage( selection ) != null )
			{
				select = true;
				break;
			} 
		}
		return select;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isSelectingFor( String selection1, String selection2 )
	{
		return ( getSelectionPage( selection1 ) != null ) || ( getSelectionPage( selection2 ) != null );
	}
	
	//--------------------------------------------------------------------------------
	
	public void selectFor( String selection )
	{
		View view = getTopView();
		if ( view != null )
		{
			selectedId = null;
			view.setSelection( selection );
			log.debug( "View "+ view + " is selectiong for '" + selection + "'");
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public void selected( String selection, Integer id )
	{
		if ( selection != null && id != null && !selection.isEmpty() )
		{
			setSelectedId( id );
			//TODO: Events.instance().raiseEvent( SELECTED_EVENT + selection, id );
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public void setSelectedId( Integer id )
	{
		this.selectedId = id;
	}
	
	public Integer getSelectedId()
	{
		return this.selectedId;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isFromList()
	{
		boolean fromList = false;
		View top 	= getTopView();
		View back 	= getBackView();
		if ( top != null && back != null )
		{
			String base = top.getName().toLowerCase()
							.replace( "edit", "" )
							.replace( "view", "" );
			base = base + "list";
			fromList = back.getName().equalsIgnoreCase( base );
		}
		return fromList;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isFromEdit()
	{
		boolean fromList = false;
		View top 	= getTopView();
		View back 	= getBackView();
		if ( top != null && back != null )
		{
			String base = top.getName().toLowerCase()
							.replace( "list", "" )
							.replace( "view", "" );
			base = base + "edit";
			fromList = back.getName().equalsIgnoreCase( base );
		}
		return fromList;
	}
	
	//--------------------------------------------------------------------------------
	
	public boolean isFromOuterView()
	{
		return !isFromEdit() && ! isFromList();
	}
	
	//--------------------------------------------------------------------------------
    
    public void goTo( String viewId ) throws IOException
    {
    	String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    	FacesContext.getCurrentInstance().getExternalContext().redirect( path + "/faces" + viewId );
    }
    
    //--------------------------------------------------------------------------------
    
    public void goToSelectionPage( String selection ) throws IOException
    {

    	String viewId = getSelectionPage(selection).replace("xhtml", "seam");
    	popUntilView( viewId );
		
		View top = getTopView();
		if ( top != null )
		{
			top.setMarked( false );
			top.setSelection( null );
		}
    	
    	String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    	FacesContext.getCurrentInstance().getExternalContext().redirect( path + viewId );
    }
    
    //--------------------------------------------------------------------------------
    
    public void begin()
	{
		if ( conversation.isTransient() ) 
		{
			conversation.begin();
			beginConversation();
			log.debugv( "Begin long running conversation id {0}", conversation.getId() );
		}
	}
    
    public void begin( ActionEvent event )
    {
    	begin();
    }
	
    //--------------------------------------------------------------------------------
	
	public void end()
	{
		if ( !conversation.isTransient() ) 
		{
			String id = conversation.getId();
			conversation.end();
			endConversation();
			log.debugv( "End long running conversation id {0}", id );
		}
	}
	
	//--------------------------------------------------------------------------------
	
	public void end( ActionEvent event )
	{
		end();
	}
   
	//--------------------------------------------------------------------------------
	//-
	// 	PRIVATE METHODS
	//-
	//--------------------------------------------------------------------------------
   	
	private View getSecondLastView()
	{
		View view = null;
		if ( views.size() > 1 )
		{
			int pos = views.size() - 2;
			view = views.get( pos );
		}
		return view;
	}
	
	//--------------------------------------------------------------------------------

	private boolean isOnTop( View view )
	{
		boolean result = false;
		View top = getTopView();
		if ( top != null )
		{
			result = top.getId().equals( view.getId() );
		}
		return result;
	}
	
	//--------------------------------------------------------------------------------
	
	private View getTopView()
	{
		View result = null;
		if ( !views.isEmpty() )
		{
			result = views.peek();
		}
		return result;
	}
	
	//--------------------------------------------------------------------------------
	
	private boolean isSecondLast( View view )
	{
		boolean result = false;
		View secondLast = getSecondLastView();
		if ( secondLast != null )
		{
			result = secondLast.equalsId( view );
		}
		return result;
	}
	
	//--------------------------------------------------------------------------------
	
	private void popUntilView( View view )
	{
		if ( !views.isEmpty() )
		{
			View v = views.peek();
			
			while ( !views.isEmpty() && !view.equalsId( v ) )
			{
				views.pop();
				if ( !views.isEmpty() )
				{
					v = views.peek();
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------------
	
	private View popView()
	{
		if ( !views.isEmpty() )
		{
			return views.pop();
		}
		return null;
	}
	
	//--------------------------------------------------------------------------------
	
	private void popUntilView( String viewId )
	{
		if ( !views.isEmpty() )
		{
			View v = views.peek();
			
			while ( !views.isEmpty() && !viewId.equals( v.getId()))
			{
				views.pop();
				if ( !views.isEmpty() )
				{
					v = views.peek();
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------------
	
	private void resetViews()
	{
		for ( View view : views )
    	{
    		view.setMarked( false );
    		view.setSelection( null );
    	}
	}
	
	//--------------------------------------------------------------------------------
	//--
	//-- LISTENER
	//--
	//--------------------------------------------------------------------------------
    
    public void beforePhase( @Observes @Before PhaseEvent e )
    {
    	View view = new View( conversation.getId() );
    	if ( view.isSeamPage() && !isOnTop( view ) )
	    {
    		if ( isSecondLast( view ) )
			{
				popUntilView( view );
				
				View top = getTopView();
				if ( top != null )
				{
					top.setMarked( false );
					top.setSelection( null );
				}
				
				log.debug("BACK TO [" + view.getConversationId() + "]: " + view   );
			}
			else
    		{
    			views.push( view );

    			log.debug( "NEXT TO [" + view.getConversationId() + "]: " + view  );
    		}
    	}
    }
    
    //--------------------------------------------------------------------------------
    //@Observer("org.jboss.seam.beginConversation")
    //@Observer("org.jboss.seam.beginConversation")
    private void beginConversation() 
    {
    	View view = new View( conversation.getId() );
    	
    	if ( view.isSeamPage() && !view.equalsConversationId( getTopView() ) )
    	{
	    	View top = null;
	    	if ( !views.isEmpty() )
	    	{
	    		top = views.peek();
	    	}
	
	        views.clear();
	        
	        if ( top != null )
	        {
	        	views.push( top );
	        	log.debugv( "START [{0}]: {1}", top.getConversationId(), top );
	        }
	        
	        views.add( view );
	        
	        selectedId = null;
	        
	        log.debugv( "BEGIN [{0}]: {1}", view.getConversationId(), view );
    	}
    }
   
    //--------------------------------------------------------------------------------
    //@Observer("org.jboss.seam.endConversation")
    //@Observer("org.jboss.seam.endConversation")
    private void endConversation() 
    {
    	resetViews();
    	
    	View view = new View( conversation.getId() );
    	
    	log.debugv( "END [{0}]: {1}", view.getConversationId(), view  );
    }
}

