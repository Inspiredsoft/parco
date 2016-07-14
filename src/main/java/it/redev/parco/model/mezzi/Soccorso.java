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
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.rifornimenti.Rifornimento;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Table (name = "SOCCORSI")
public class Soccorso extends Identifiable implements Serializable
{
	private Integer 		id;
	private Mezzo			mezzo;
	private Integer			mese;
	private Integer			anno;
	private Integer			numero;
	private BigDecimal		mediaChilometri;
	private Rifornimento	rifInizio;
	private Rifornimento	rifFine;
	private ImportInfo		importInfo;
	
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
	@JoinColumn( name = "ID_MEZZO" )
	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}

	@NotNull
	@Column( name="MESE" )
	public Integer getMese() {
		return mese;
	}

	public void setMese(Integer mese) {
		this.mese = mese;
	}

	@NotNull
	@Column( name="ANNO" )
	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	@NotNull
	@Column( name="NUMERO" )
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column( name="MEDIA_CHILOMETRI" )
	public BigDecimal getMediaChilometri() {
		return mediaChilometri;
	}

	public void setMediaChilometri(BigDecimal mediaChilometri) {
		this.mediaChilometri = mediaChilometri;
	}

	@ManyToOne
	@JoinColumn( name = "ID_RIF_INIZIO" )
	public Rifornimento getRifInizio() {
		return rifInizio;
	}

	public void setRifInizio(Rifornimento rifInizio) {
		this.rifInizio = rifInizio;
	}

	@ManyToOne
	@JoinColumn( name = "ID_RIF_FINE" )
	public Rifornimento getRifFine() {
		return rifFine;
	}

	public void setRifFine(Rifornimento rifFine) {
		this.rifFine = rifFine;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_IMPORT_INFO" )
	public ImportInfo getImportInfo() {
		return importInfo;
	}

	public void setImportInfo(ImportInfo importInfo) {
		this.importInfo = importInfo;
	}
}
