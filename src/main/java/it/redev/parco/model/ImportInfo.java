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

import it.redev.parco.model.core.Job;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Table (name = "IMPORT_INFO")
public class ImportInfo extends Identifiable implements Serializable
{
	private Integer 	id;
	private Integer		tipo;
	private String		file;
	private Date		data;
	private User		utente;
	private List<Job>	jobs;
	private Date		dataInizio;
	private Date		dataFine;
	
	//---------------------------------------------------------------------------------------
	
	public static class Tipo
	{
		public static Integer RIF_Q8	= 1;
		public static Integer RIF_AGIP	= 2;
		public static Integer SOCCORSI	= 3;
		public static Integer ANAGRAFICA= 4;
		
		public static final Integer list[] = 
		{
			Tipo.RIF_Q8,
			Tipo.RIF_AGIP,
			Tipo.SOCCORSI,
			Tipo.ANAGRAFICA
		};
	}
	
	//---------------------------------------------------------------------------------------
	
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
	@Column( name="TIPO" )
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	@NotNull
	@Column( name="FILE" )
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Date getData() {
		return data;
	}

	@NotNull
	@Column( name="DATA" )
	public void setData(Date data) {
		this.data = data;
	}

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_UTENTE" )
	public User getUtente() {
		return utente;
	}

	public void setUtente(User utente) {
		this.utente = utente;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="importInfo")
	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	@Column( name="DATA_INIZIO" )
	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	@Column( name="DATA_FINE" )
	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	
	//-------------------------------------------------------------
	
	@Transient
	public boolean isRifornimentoQ8()
	{
		return Tipo.RIF_Q8.equals( this.tipo );
	}
	
	@Transient
	public boolean isRifornimentoAgip()
	{
		return Tipo.RIF_AGIP.equals( this.tipo );
	}
	
	@Transient
	public boolean isSoccorsi()
	{
		return Tipo.SOCCORSI.equals( this.tipo );
	}
	
	@Transient
	public boolean isAnagrafica()
	{
		return Tipo.ANAGRAFICA.equals( this.tipo );
	}
}
