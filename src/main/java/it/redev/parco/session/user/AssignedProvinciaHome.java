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

package it.redev.parco.session.user;

import it.redev.parco.comparators.ProvinciaComparator;
import it.redev.parco.core.security.AllowedEntities;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.model.oc.Provincia;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@Stateful
@RequestScoped
public class AssignedProvinciaHome extends AssignedHome 
{
	@Inject
	Logger log;
	
	@Inject
	LoggedUser loggedUser;
	
	
	private List<Provincia> assignedProvince 	= new ArrayList<Provincia>();
	private List<Provincia> assignableProvince	= new ArrayList<Provincia>();
	private List<Provincia> hiddenProvince		= new ArrayList<Provincia>();
	
	//--------------------------------------------------------------------------------
	
	@PostConstruct
	public void init()
	{
			
		assignableProvince.addAll( ProvinciaComparator.sort( loggedUser.getAllowedProvincie() ) );
		
		assignedProvince 	= AllowedEntities.load( findAssignedEnty() ).getAssignedProvince();
		
		hiddenProvince 	= new ArrayList<Provincia>();
		
		for ( Provincia prov : assignedProvince )
		{
			if ( !assignableProvince.contains( prov ) )
			{
				hiddenProvince.add( prov );
			}
		}
		
		log.debugv( "{0} has {1} assigable, {2} already assigned and {3} hiddent.", 
				getSubject(), assignableProvince.size(), assignedProvince.size(), hiddenProvince.size() );
	}

	//--------------------------------------------------------------------------------

	public List<Provincia> getAssignedProvince()
	{
		return assignedProvince;
	}

	public void setAssignedProvince(List<Provincia> province)
	{
		this.assignedProvince = province;
	}
	
	//--------------------------------------------------------------------------------
	
	public List<Provincia> getAssignableProvince()
	{
		return assignableProvince;
	}

	public void setAssignableProvince(List<Provincia> province)
	{
		this.assignableProvince = province;
	}

	//--------------------------------------------------------------------------------
	
	public void save()
	{
		for ( Provincia prov : assignedProvince )
		{
			save( prov );
		}
		
		for ( Provincia prov : hiddenProvince )
		{
			save( prov );
		}
	}
	
}
