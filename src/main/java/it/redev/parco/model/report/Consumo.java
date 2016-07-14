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

package it.redev.parco.model.report;

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.rifornimenti.Rifornimento;

import java.io.Serializable;
import java.math.BigDecimal;
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

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "CONSUMI")
public class Consumo implements Serializable
{
	private Integer 		id;
	private Mezzo 			mezzo;
	private Date			data;
	private Rifornimento 	rifornimento;
	private Rifornimento 	rifornimentoPrecedente;
	private BigDecimal		quantita;
	private Integer			chilometri;
	private BigDecimal		consumo;
	
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

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_MEZZO" )
	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}

	// Data del in rifornimento
	@NotNull
	@Column( name="DATA" )
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Unexportable
	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_RIF" )
	public Rifornimento getRifornimento() {
		return rifornimento;
	}

	public void setRifornimento(Rifornimento rifornimento) {
		this.rifornimento = rifornimento;
	}

	@Unexportable
	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_RIF_PRED" )
	public Rifornimento getRifornimentoPrecedente() {
		return rifornimentoPrecedente;
	}

	public void setRifornimentoPrecedente(Rifornimento rifornimentoPrecedente) {
		this.rifornimentoPrecedente = rifornimentoPrecedente;
	}

	// Quantita' in rifornimento
	@NotNull
	@Column( name="QUANTITA" )
	public BigDecimal getQuantita() {
		return quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
	}

	// Differenza tra chilometraggio in rifornimento e rifornimento precedente
	@NotNull
	@Column( name="CHILOMETRI" )
	public Integer getChilometri() {
		return chilometri;
	}

	public void setChilometri(Integer chilometri) {
		this.chilometri = chilometri;
	}

	// Rapporto tra chilometri e quantita' 
	@NotNull
	@Column( name="CONSUMO" )
	public BigDecimal getConsumo() {
		return consumo;
	}

	public void setConsumo(BigDecimal consumo) {
		this.consumo = consumo;
	}
	
	
}
