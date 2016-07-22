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

package it.redev.parco.model.asset;

import it.redev.parco.model.Identifiable;
import it.redev.parco.model.oc.Persona;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Entity
@Table (name = "MOVIMENTO_ASSET")
public class MovimentoAsset extends Identifiable implements Serializable
{
	private Integer 	id;
	private Asset		asset;
	private Integer		tipo;
	private Persona		assegnatario;
	private Persona		consegnatario;
	private String		note;
	private Date		data;
	
	//---------------------------------------------------------------------------------------
	
	public static class Tipo
	{
		public static Integer CONSEGNA = 1;
		public static Integer RICONSEGNA = 2;
		
		public static final Integer list[] = 
		{
			CONSEGNA,
			RICONSEGNA
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
	@ManyToOne
	@JoinColumn(name = "ID_ASSET")
	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	@Column( name="TIPO" )
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ID_ASSEGNATARIO")
	public Persona getAssegnatario() {
		return assegnatario;
	}

	public void setAssegnatario(Persona assegnatario) {
		this.assegnatario = assegnatario;
	}

	@ManyToOne
	@JoinColumn(name = "ID_CONSEGNATARIO")
	public Persona getConsegnatario() {
		return consegnatario;
	}

	public void setConsegnatario(Persona consegnatario) {
		this.consegnatario = consegnatario;
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
	@Column( name="DATA" )
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public boolean isConsegnata()
	{
		return Tipo.CONSEGNA.equals( this.tipo );
	}
	
	@Transient
	public boolean isRiconsegnata()
	{
		return Tipo.RICONSEGNA.equals( this.tipo );
	}
}
