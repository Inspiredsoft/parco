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

package it.redev.parco.model.report;

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperties;
import it.inspired.exporter.annotation.ExpoProperty;
import it.redev.parco.model.oc.Postazione;

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
@ExpoElement
@Entity
@Table (name = "RIEPILOGO_RIF_POSTAZIONI")
public class RiepilogoRifornimentiPostazione implements Serializable
{
	private Integer 	id;
	private	Postazione	postazione;
	private Integer 	mese;
	private Integer 	anno;
	private Integer 	numero;
	private BigDecimal	quantita;
	private BigDecimal	importo;
	
	//------------------------------------------------------------------------------
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name="ID" )
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ExpoProperties( property = { @ExpoProperty("nome"), @ExpoProperty("centroCosto") } )
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

	@NotNull
	@Column( name="QUANTITA" )
	public BigDecimal getQuantita() {
		return quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
	}

	@NotNull
	@Column( name="IMPORTO" )
	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}
}
