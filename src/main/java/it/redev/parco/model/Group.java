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

package it.redev.parco.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Table( name="GROUPS" )
public class Group extends Identifiable implements Serializable, Eligible
{
	private Integer id;
	private String  name;
	private String  description;
	private int 	version = 0;
	
	private List<Permission> groupPermissions;
	private List<Event> groupEvents;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name="ID" )
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Version
	@Column( name="VERSION" )
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	@NotNull
	@Column( name="NAME" )
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column( name="DESCRIPTION" )
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	//------------------------------------------------------------------------
	
	public String toString()
	{
		return name;
	}

	@ManyToMany
	@JoinTable(name="GROUP_PERMISSION", joinColumns={@JoinColumn(name="ID_GROUPS")},
	inverseJoinColumns={@JoinColumn(name="ID_PERMISSIONS")})
	public List<Permission> getGroupPermissions() {
		return groupPermissions;
	}

	public void setGroupPermissions(List<Permission> groupPermissions) {
		this.groupPermissions = groupPermissions;
	}
	
	@ManyToMany
	@JoinTable(name="GROUP_EVENT", joinColumns={@JoinColumn(name="ID_GROUPS")},
	inverseJoinColumns={@JoinColumn(name="ID_EVENTS")})
	public List<Event> getGroupEvents() {
		return groupEvents;
	}

	public void setGroupEvents(List<Event> groupEvents) {
		this.groupEvents = groupEvents;
	}

}
