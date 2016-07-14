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

package it.redev.parco.core.security;

import it.redev.parco.comparators.AreaComparator;
import it.redev.parco.comparators.PostazioneComparator;
import it.redev.parco.comparators.ProvinciaComparator;
import it.redev.parco.model.AssignedEntity;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;
import it.inspired.thread.Chrono;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class AllowedEntities implements Serializable
{
	private static final Logger log = Logger.getLogger(AllowedEntities.class);
	/**
	 * Le entita' assigned sono quelle assegnate
	 * Le entita' allowed sono quelle che posso gestire e sono le assegnate e tutti i figli
	 * Le entita' visible sono quelle che posso vedere perche' sono avi di entita' assegnate
	 */

	private List<Provincia> assignedProvince = new ArrayList<Provincia>();
	private List<Provincia> allowedProvince = new ArrayList<Provincia>();
	private List<Provincia> visibleProvince = new ArrayList<Provincia>();

	private List<Area> assignedAree = new ArrayList<Area>();
	private List<Area> allowedAree = new ArrayList<Area>();
	private List<Area> visibleAree = new ArrayList<Area>();
	
	private List<Postazione> assignedPostazioni = new ArrayList<Postazione>();
	private List<Postazione> allowedPostazioni = new ArrayList<Postazione>();
	private List<Postazione> visiblePostazioni = new ArrayList<Postazione>();
	
	//---------------------------------------------------------------------------------------
	
	public static AllowedEntities load( List<AssignedEntity> allowedEntityList )
	{
		Chrono crono = Chrono.newStart();
		AllowedEntities entities = new AllowedEntities();
		
		for ( AssignedEntity entity : allowedEntityList )
		{
			if ( entity.getPostazione() != null )
			{
				entities.addAssignedPostazione( entity.getPostazione() );
			}
			else if ( entity.getArea() != null )
			{
				entities.addAssignedArea( entity.getArea() );
			}
			else if ( entity.getProvincia() != null )
			{
				entities.addAssignedProvincia( entity.getProvincia() );
			}
		}
		
		logEntities( entities );
		
		log.debug( "Loaded entries in " + crono.elapsed() + "ms" );
		
		return entities;
	}
	
	//---------------------------------------------------------------------------------------
	
	public void sort()
	{
		 AreaComparator.sort( this.allowedAree );
		 AreaComparator.sort( this.visibleAree );
		 
		 PostazioneComparator.sort( this.allowedPostazioni );
		 PostazioneComparator.sort( this.visiblePostazioni );
		 
		 ProvinciaComparator.sort( this.allowedProvince );
		 ProvinciaComparator.sort( this.visibleProvince );
	}
	
	//---------------------------------------------------------------------------------------
	
	private static void logEntities( AllowedEntities entities )
	{
		if ( log.isDebugEnabled() )
		{
			log.debug( "Allowed P: " + entities );
		}
	}
	
	//---------------------------------------------------------------------------------------
	// PROVINCE
	//---------------------------------------------------------------------------------------
	
	public void addAssignedProvincia( Provincia provincia )
	{
		if ( provincia != null ) 
		{
			if ( !assignedProvince.contains( provincia ) )
			{
				assignedProvince.add( provincia );
			}
			addAllowedProvincia( provincia );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void addAllowedProvincia( Provincia provincia )
	{
		if ( provincia != null ) 
		{
			if ( !allowedProvince.contains( provincia ) )
			{
				allowedProvince.add( provincia );
			}
			addVisibleProvincia( provincia );
			addAllowedAree( provincia.getAree() );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void addVisibleProvincia( Provincia provincia )
	{
		if( provincia != null )
		{
			if ( !visibleProvince.contains( provincia ) )
			{
				visibleProvince.add( provincia );
			}
		}
	}
	
	//---------------------------------------------------------------------------------------
	// AREE
	//---------------------------------------------------------------------------------------
	
	public void addAssignedArea( Area area )
	{
		if ( area != null ) 
		{
			if ( !assignedAree.contains( area ) )
			{
				assignedAree.add( area );
			}
			addAllowedArea( area );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void addAllowedArea( Area area )
	{
		if ( area != null ) 
		{
			if ( !allowedAree.contains( area ) )
			{
				allowedAree.add( area );
			}
			addVisibleArea( area );
			addVisibleProvincia( area.getProvincia() );
			addAllowedPostazioni( area.getPostazioni() );
		}
	}
	
	public void addAllowedAree( List<Area> aree )
	{
		for ( Area area : aree )
		{
			addAllowedArea( area );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void addVisibleArea( Area area )
	{
		if( area != null )
		{
			if ( !visibleAree.contains( area ) )
			{
				visibleAree.add( area );
			}
			addVisibleProvincia( area.getProvincia() );
		}
	}
	
	//---------------------------------------------------------------------------------------
	// POSTAZIONI
	//---------------------------------------------------------------------------------------
	
	public void addAssignedPostazione( Postazione postazione )
	{
		if ( postazione != null ) 
		{
			if ( !assignedPostazioni.contains( postazione ) )
			{
				assignedPostazioni.add( postazione );
			}
			addAllowedPostazione( postazione );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void addAllowedPostazione( Postazione postazione )
	{
		if ( postazione != null ) 
		{
			if ( !allowedPostazioni.contains( postazione ) )
			{
				allowedPostazioni.add( postazione );
			}
			addVisiblePostazione( postazione );
			addVisibleArea( postazione.getArea() );
		}
	}
	
	public void addAllowedPostazioni( List<Postazione> postazioni )
	{
		for ( Postazione postazione : postazioni )
		{
			addAllowedPostazione( postazione );
		}
	}
	
	//---------------------------------------------------------------------------------------
	
	public void addVisiblePostazione( Postazione postazione )
	{
		if( postazione != null )
		{
			if ( !visiblePostazioni.contains( postazione ) )
			{
				visiblePostazioni.add( postazione );
			}
		}
	}
	
	//---------------------------------------------------------------------------------------
	// GETTER & SETTER
	//---------------------------------------------------------------------------------------
	
	public List<Provincia> getAllowedProvince() {
		return allowedProvince;
	}

	public void setAllowedProvince(List<Provincia> allowed) {
		this.allowedProvince = allowed;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Provincia> getVisibleProvince() {
		return visibleProvince;
	}

	public void setVisibleProvince(List<Provincia> visible) {
		this.visibleProvince = visible;
	}
		
	//---------------------------------------------------------------------------------------
	
	public List<Provincia> getAssignedProvince() {
		return assignedProvince;
	}

	public void setAssignedProvince(List<Provincia> assigned) {
		this.assignedProvince = assigned;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Area> getAssignedAree() {
		return assignedAree;
	}

	public void setAssignedAree(List<Area> assignedAree) {
		this.assignedAree = assignedAree;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Area> getAllowedAree() {
		return allowedAree;
	}

	public void setAllowedAree(List<Area> allowedAree) {
		this.allowedAree = allowedAree;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Area> getVisibleAree() {
		return visibleAree;
	}

	public void setVisibleAree(List<Area> visibleAree) {
		this.visibleAree = visibleAree;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Postazione> getAssignedPostazioni() {
		return assignedPostazioni;
	}

	public void setAssignedPostazioni(List<Postazione> assignedPostazioni) {
		this.assignedPostazioni = assignedPostazioni;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Postazione> getAllowedPostazioni() {
		return allowedPostazioni;
	}

	public void setAllowedPostazioni(List<Postazione> allowedPostazioni) {
		this.allowedPostazioni = allowedPostazioni;
	}

	//---------------------------------------------------------------------------------------
	
	public List<Postazione> getVisiblePostazioni() {
		return visiblePostazioni;
	}

	public void setVisiblePostazioni(List<Postazione> visiblePostazioni) {
		this.visiblePostazioni = visiblePostazioni;
	}
	
	
}
