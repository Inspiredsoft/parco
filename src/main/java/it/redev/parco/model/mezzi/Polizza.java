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

package it.redev.parco.model.mezzi;

import it.redev.parco.model.Identifiable;
import it.inspired.utils.DateUtils;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Table (name = "POLIZZE")
public class Polizza extends Identifiable implements Serializable
{
	private Integer 				id;
	private int 					version = 0;
	private Mezzo					mezzo;
	private CompagniaAssicurazione 	compagnia;
	private String					numero;
	private Date					dataDecorrenza;
	private Date					dataScadenza;
	
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
	
	@Version
	@Column( name="VERSIONE" )
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_MEZZO" )
	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_COMPAGNIA" )
	public CompagniaAssicurazione getCompagnia() {
		return compagnia;
	}

	public void setCompagnia(CompagniaAssicurazione compagnia) {
		this.compagnia = compagnia;
	}

	@NotNull
	@Column( name="NUMERO" )
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numeroPolizza) {
		this.numero = numeroPolizza;
	}

	@NotNull
	@Column( name="DATA_DECORRENZA" )
	public Date getDataDecorrenza() {
		return dataDecorrenza;
	}

	public void setDataDecorrenza(Date dataDecorrenza) {
		this.dataDecorrenza = dataDecorrenza;
	}

	@NotNull
	@Column( name="DATA_SCADENZA" )
	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public int getDurataMesi()
	{
		return DateUtils.months( dataDecorrenza, dataScadenza );
	}
	
	//---------------------------------------------------------------------------------------
	
	public Polizza clone()
	{
		Polizza polizza = new Polizza();
		polizza.setCompagnia( getCompagnia() );
		polizza.setMezzo( getMezzo() );
		polizza.setNumero( getNumero() );
		polizza.setDataDecorrenza( getDataDecorrenza() );
		polizza.setDataScadenza( getDataScadenza() );
		return polizza;
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public boolean isCurrent()
	{
		Date today = new Date();
		return !( today.before( this.getDataDecorrenza() ) || today.after( this.getDataScadenza() ) );
	}
}
