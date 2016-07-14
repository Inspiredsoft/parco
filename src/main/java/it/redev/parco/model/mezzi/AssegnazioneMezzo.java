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

import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.ExpoElement;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.oc.Postazione;

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

@ExpoElement( property ={ 
		@ExpoProperty("dataInizio"), 
		@ExpoProperty("siglaProvincia"), 
		@ExpoProperty("nomeArea"), 
		@ExpoProperty("nomePostazione"), 
		@ExpoProperty("centroCosto") } )
@SuppressWarnings("serial")
@Entity
@Table (name = "ASSEGNAZIONI_MEZZO")
public class AssegnazioneMezzo extends Identifiable implements Serializable
{
	private Integer 	id;
	private int 		version = 0;
	private Mezzo		mezzo;
	private	Postazione	postazione;
	private Date		dataInizio;
	private Date		dataFine;
	private Integer		statoSerbatoio;
	private Integer		chilometraggio;
	
	//---------------------------------------------------------------------------------------
	
	public static class StatoSerbatoio
	{
		public static final Integer VUOTO 		= 1;
		public static final Integer UN_QUARTO 	= 2;
		public static final Integer META 		= 3;
		public static final Integer TRE_QUARTI 	= 4;
		public static final Integer PIENO 		= 5;
		
		public static final Integer list[] = 
		{
			VUOTO,
			UN_QUARTO,
			META,
			TRE_QUARTI,
			PIENO
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
	@JoinColumn( name = "ID_POSTAZIONE" )
	public Postazione getPostazione() {
		return postazione;
	}

	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}

	@NotNull
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

	@Column( name="STATO_SERBATOIO" )
	public Integer getStatoSerbatoio() {
		return statoSerbatoio;
	}

	public void setStatoSerbatoio(Integer statoSerbatoio) {
		this.statoSerbatoio = statoSerbatoio;
	}
	
	@Column( name="CHILOMETRAGGIO" )
	public Integer getChilometraggio() {
		return chilometraggio;
	}

	public void setChilometraggio(Integer chilomnetraggio) {
		this.chilometraggio = chilomnetraggio;
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public boolean isCurrent()
	{
		Date today = new Date();
		return !today.before( this.getDataInizio() ) && ( this.getDataFine() == null || !today.after( this.getDataFine() ) );
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public boolean isCurrent( Date day )
	{
		return !day.before( this.getDataInizio() ) && ( this.getDataFine() == null || !day.after( this.getDataFine() ) );
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public boolean before( AssegnazioneMezzo ass )
	{
		return ( this.dataFine != null && this.dataFine.before( ass.getDataInizio() ) ) ||
				( this.dataFine == null && this.dataInizio.before( ass.getDataInizio() ) );
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public boolean after( AssegnazioneMezzo ass )
	{
		return ( ass.getDataFine() != null && this.dataInizio.after( ass.getDataFine() ) ) ||
				( ass.getDataFine() == null && this.dataInizio.after( ass.getDataInizio() ) );
	}
	
	//---------------------------------------------------------------------------------------
	
	@Transient
	public String getSiglaProvincia()
	{
		return this.postazione.getArea().getProvincia().getSigla();
	}
	
	@Transient
	public String getNomeArea()
	{
		return this.postazione.getArea().getNome();
	}
	
	@Transient
	public String getNomePostazione()
	{
		return this.postazione.getNome();
	}
	
	@Transient
	public String getCentroCosto()
	{
		return this.postazione.getCentroCosto();
	}
}
