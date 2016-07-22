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

package it.redev.parco.session;

import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.view.View;
import it.redev.parco.core.view.ViewChain;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
public class BreadcrumbsManager implements Serializable 
{
	@Inject
	ViewChain viewChain;
	
	@Inject
	MessagesManager message;
	
	List<Crumb> crumbs = null;
	
	private static final String prefix = "view.name.";
	
	//------------------------------------------------------------------------------------------
	
	public class Crumb 
	{
		private Integer position;
		private String 	name;
		private boolean last;
		
		public Crumb( View view ) {
			this.name = view.getName();
			this.last = false;
		}
		
		public Integer getPosition() {
			return position;
		}
		public void setPosition(Integer position) {
			this.position = position;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isLast() {
			return last;
		}
		public void setLast(boolean last) {
			this.last = last;
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	public List<Crumb> getCrumbs()
	{
		if ( crumbs == null )
		{
			crumbs = new ArrayList<Crumb>();
			Stack<View> views = viewChain.getViews();
			View pred = null;
			Integer pos = 0;
			for ( View view : views )
			{
				pos++;
				if ( !view.isSame( pred ) )
				{
					pred = view;
					Crumb crumb = new Crumb( view );
					crumb.setPosition( views.size() - pos );
					crumb.setLast( views.size() == pos );
					String name = message.getMessage(  prefix + view.getName() );
					if ( !name.startsWith( "BundleKey" ) )
					{
						crumb.setName( name );
					}
					else
					{
						crumb.setName( StringUtils.capitalizeMethodName( crumb.getName() ) );
					}
					crumbs.add( crumb );
				}
			}
		}
		return crumbs;
	}
	
	//------------------------------------------------------------------------------------------
	
	public boolean hasElement() {
		return getCrumbs().size() > 1 ;
	}
}
