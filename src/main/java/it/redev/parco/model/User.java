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
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Entity
@Table( name="USERS" )
public class User extends Identifiable implements Serializable, Eligible, Removable
{
	private Integer id;
	private int 	version = 0;
	private String  username;
	private String  password;
	private String  name;
	private String  surname;
	private String  email;
	private String  phone;
	private boolean active;
	private Date	lastAccess;
	private boolean	removed;
	private Date 	removalDate;
	
	private List<Group> groups = new ArrayList<Group>();
	
	//-------------------------------------------------------------------------------------------
	
	// Reserved usernname
	public static final String admin 	= "admin";
	public static final String system 	= "system";
	
	//-------------------------------------------------------------------------------------------
	
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
	
	@Column( name="USERNAME" )
	@Length( min = 4, max = 100  )
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column( name="PASSWORD" )
	@Length( min = 6, max = 100  )
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@NotNull
	@Column( name="NAME" )
	@Length( max = 50 )
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@NotNull
	@Column( name="SURNAME" )
	@Length( max = 50 )
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@Email
	@Column( name="EMAIL" )
	@Length( max = 200 )
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column( name="PHONE" )
	@Length( max = 20 )
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column( name="ACTIVE" )
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Column( name="LAST_ACCESS" )
	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	@NotNull
	@Column( name="REMOVED" )
	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	@Override
	@Column( name="REMOVAL_DATE" )
	public Date getRemovalDate() {
		return removalDate;
	}

	public void setRemovalDate(Date removalDate) {
		this.removalDate = removalDate;
	}
	
	@ManyToMany
	@JoinTable(name="USERS_GROUPS", joinColumns={@JoinColumn(name="ID_USERS")},
	inverseJoinColumns={@JoinColumn(name="ID_GROUPS")})
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	//-------------------------------------------------------------------------------------------
	
	@Transient
	public String getFullName()
	{
		return this.name + " " + this.surname;
	}
	
	public String toString()
	{
		return this.getFullName() + " (" + this.username + ")";
	}
}
