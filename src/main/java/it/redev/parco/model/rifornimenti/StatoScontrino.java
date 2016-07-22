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

package it.redev.parco.model.rifornimenti;

import it.redev.parco.model.Identifiable;
import it.redev.parco.model.StatoInfo;
import it.redev.parco.model.User;

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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Entity
@Table( name="STATI_SCONTRINO" )
public class StatoScontrino extends Identifiable implements Serializable
{
	private Integer id;
	private Scontrino	scontrino;
	private Integer stato;
	private String  note;
	private User    utente;
	private Date    dataInizio;
	private Date    dataFine;
	
	//-----------------------------------------------------------------------------------
	
	public StatoScontrino(){}
	
	public StatoScontrino( StatoInfo statoInfo ) 
	{
		this.stato 		= statoInfo.getStato();
		this.dataInizio	= statoInfo.getDataStato();
		this.note		= statoInfo.getNoteStato();
		this.utente		= statoInfo.getUtenteStato();
	}
	
	//-----------------------------------------------------------------------------------
	
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
	@Column( name="STATO" )
	public Integer getStato() {
		return stato;
	}
	public void setStato(Integer stato) {
		this.stato = stato;
	}
	
	@Length(max=65535)
	@Column( name="NOTE" )
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "ID_UTENTE", nullable = false)
	public User getUtente() {
		return utente;
	}
	public void setUtente(User utente) {
		this.utente = utente;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "ID_SCONTRINO", nullable = false)
	public Scontrino getScontrino() {
		return scontrino;
	}
	public void setScontrino(Scontrino scontrino) {
		this.scontrino = scontrino;
	}
	
	@NotNull
	@Column( name="DATA_INIZIO" )
	public Date getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	
	@NotNull
	@Column( name="DATA_FINE" )
	public Date getDataFine() {
		return dataFine;
	}
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	
	
}
