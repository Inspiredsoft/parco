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

package it.redev.parco.model.core;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


@SuppressWarnings("serial")
@Entity
@Table( name="MAIL_PARAMETER" )
public class MailParameter implements Serializable
{
	private Integer		id;
	private Mail 		mail;
	private String		name;
	private String		value;
	private String		label;
	
	//------------------------------------------------------------------------
	
	public MailParameter(){}
	
	public MailParameter(String name, String label, Object parameter)
	{
		this.name  = name;
		this.label = label;
		
		if(parameter instanceof File)
			value = ((File)parameter).getAbsolutePath();
		
		else if (parameter instanceof Integer)
			value = String.valueOf(((Integer)parameter));
		
		else
			value = parameter.toString();
	}
	
	//------------------------------------------------------------------------
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name="ID" )
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn( name = "ID_MAIL", nullable = false )
	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	@NotNull
	@Column( name="NAME" )
	@Length( max = 200 )
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Column( name="VALUE" )
	@Length( max = 200 )
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@NotNull
	@Column( name="LABEL" )
	@Length( max = 200 )
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
}
