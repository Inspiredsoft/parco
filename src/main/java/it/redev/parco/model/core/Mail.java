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

package it.redev.parco.model.core;

import it.redev.parco.model.Identifiable;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Entity
@Table( name="MAIL" )
public class Mail extends Identifiable implements Serializable
{
	private Integer		id;
	private Date 		addDate = new Date();
	private Date 		startDate;
	private Date 		endDate;
	private String		javaClass;
	private String		exception;
	private String		status;
	private Integer		tries;
	private Integer		exeTime;
	
	private List<MailMessage> 	messages	= new ArrayList<MailMessage>();
	private List<MailParameter> parameters	= new ArrayList<MailParameter>();
	
	//------------------------------------------------------------------------
	
	public class Status
	{
		public static final String WAITING 	= "W";
		public static final String RUNNING 	= "R";
		public static final String ENDED	= "N";
		public static final String ERROR	= "E";
		public static final String DELAYED	= "D";	
		public static final String STOPPED	= "S";
	}
	
	//-------------------------------------------------------------------------
	
	public static final String StatusList[] =
	{
		Status.WAITING,
		Status.RUNNING,
		Status.ENDED,
		Status.ERROR,
		Status.DELAYED,
		Status.STOPPED
	};
	
	//-------------------------------------------------------------------------
	
	public static final String errorStatuses[] =
	{
		Status.ERROR
	};
	
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

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name="ADD_DATE" )
	public Date getAddDate() {
		return addDate;
	}

	
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name="START_DATE" )
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name="END_DATE" )
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@NotNull
	@Column( name="JAVA_CLASS" )
	@Length( max = 200 )
	public String getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	@Column( name="EXCEPTION" )
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	@NotNull
	@Column( name="STATUS" )
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@NotNull
	@Column( name="TRIES" )
	public Integer getTries() {
		return tries;
	}

	public void setTries(Integer tries) {
		this.tries = tries;
	}
		
	@Column( name="EXE_TIME" )
	public Integer getExeTime() {
		return exeTime;
	}

	public void setExeTime(Integer exeTime) {
		this.exeTime = exeTime;
	}

	@OneToMany ( mappedBy = "mail", cascade = CascadeType.ALL )
	@OrderBy( "id ASC" )
	public List<MailMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<MailMessage> messages) {
		this.messages = messages;
	}
	
	@OneToMany ( mappedBy = "mail", cascade = CascadeType.ALL )
	@OrderBy( "id" )
	public List<MailParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<MailParameter> parameters) {
		this.parameters = parameters;
	}
	
	//---------------------------------------------------------
	
	@Transient
	public boolean isWaiting()
	{
		return this.status.equals( Status.WAITING );
	}
	
	@Transient
	public boolean isRunning()
	{
		return this.status.equals( Status.RUNNING );
	}
	
	@Transient
	public boolean isEnded()
	{
		return this.status.equals( Status.ENDED );
	}
	
	@Transient
	public boolean isError()
	{
		return this.status.equals( Status.ERROR );
	}
	
	@Transient
	public boolean isDelayed()
	{
		return this.status.equals( Status.DELAYED );
	}
	
	@Transient
	public boolean isStopped()
	{
		return this.status.equals( Status.STOPPED );
	}
	
	//---------------------------------------------------------
	
	@Transient
	public String getExeTimeStr()
	{
		return ( exeTime == null) ? "" : StringUtils.secToHHMMSS( exeTime );
	}	
	
	//---------------------------------------------------------
	
	@Transient
	public static List<String> getErrorStatuses()
	{
		return Arrays.asList( errorStatuses );
	}
}
