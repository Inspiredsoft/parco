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

package it.redev.parco.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Embeddable
public class StatoInfo implements Serializable, Cloneable 
{
	private Integer stato;
	private Date    dataStato;
	private User    utenteStato;
	private String  noteStato;
	
	//--------------------------------------------------------------------------
	
	public StatoInfo(){}
	
	public StatoInfo( Date dataStato )
	{
		this.dataStato = dataStato;
	}
	
	//--------------------------------------------------------------------------

	@NotNull
	@Column(name = "STATO")
	public Integer getStato() { return stato; }
	
	public void setStato(Integer stato) 
	{ 
		this.stato = stato; 
	}

	//--------------------------------------------------------------------------
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_STATO")
	public Date getDataStato() { return dataStato; }

	public void setDataStato(Date dataStato) 
	{ 
		this.dataStato = dataStato;
	}

	//--------------------------------------------------------------------------
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "ID_UTENTE_STATO", nullable = false)
	public User getUtenteStato() { return utenteStato; }

	public void setUtenteStato(User user) 
	{ 
		utenteStato = user;
	}

	//--------------------------------------------------------------------------
	
	@Length(max=65535)
	@Column(name = "NOTE_STATO")
	public String getNoteStato() { return noteStato; }
	
	public void setNoteStato(String noteStato)
	{
		if (noteStato != null && noteStato.trim().isEmpty())
			noteStato = null;

		this.noteStato = noteStato;
	}

	//--------------------------------------------------------------------------

	public void cambia(Integer stato, Date data, User user, String note)
	{
		setStato(stato);
		setDataStato( data );
		setUtenteStato(user);
		setNoteStato(note);
	}
	
	//--------------------------------------------------------------------------

	public void change(Integer stato, User user, String note)
	{
		setStato(stato);
		setDataStato( new Date() );
		setUtenteStato(user);
		setNoteStato(note);
	}
	
	//--------------------------------------------------------------------------
	
	public StatoInfo clone()
	{
		StatoInfo si = new StatoInfo();
		
		si.stato     	= stato;
		si.dataStato 	= dataStato;
		si.utenteStato  = utenteStato;
		si.noteStato 	= noteStato;
		
		return si;
	}
}
