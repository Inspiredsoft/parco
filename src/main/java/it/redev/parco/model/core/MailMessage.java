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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Length;


@SuppressWarnings("serial")
@Entity
@Table( name="MAIL_MESSAGE" )
public class MailMessage implements Serializable
{
	private Integer		id;
	private Mail		mail;
	private String		message;
	private String		level;
	private Date		date;
	
	//------------------------------------------------------------------------
	
	public static class Level
	{
		public static final String ERR 	= "E";
		public static final String INFO = "I";
		public static final String WARN = "W";
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
	@JoinColumn( name = "ID_Mail", nullable = false )
	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	
	@NotNull
	@Column( name="MESSAGE" )
	@Length( max = 200 )
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name="DATE" )
	@XmlTransient
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	
	@NotNull
	@Column( name="LEVEL" )
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	//------------------------------------------------------------------
	
	@Transient
	public boolean isInfo()
	{
		return this.level.equals( Level.INFO );
	}
	
	@Transient
	public boolean isError()
	{
		return this.level.equals( Level.ERR );
	}
	
	@Transient
	public boolean isWarning()
	{
		return this.level.equals( Level.WARN );
	}
}
