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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@ExpoElement
@SuppressWarnings("serial")
@Entity
@Table (name = "TIPI_MEZZO")
public class TipoMezzo  extends Identifiable implements Serializable
{
	private Integer 	id;
	private int 		version = 0;
	private String		descrizione;
	private BigDecimal	serbatoio = null;
	private	BigDecimal 	consumoUrbano = null;
	private	BigDecimal 	consumoExtraurbano = null;
	private Integer		tipoCarburante;
	
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
	@Length( max = 200 )
	@Column( name="DESCRIZIONE" )
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column( name="SERBATOIO" )
	public BigDecimal getSerbatoio() {
		return serbatoio;
	}

	public void setSerbatoio(BigDecimal serbatoio) {
		this.serbatoio = serbatoio;
	}

	@Column( name="CONSUMO_URBANO" )
	public BigDecimal getConsumoUrbano() {
		return consumoUrbano;
	}

	public void setConsumoUrbano(BigDecimal consumoUrbano) {
		this.consumoUrbano = consumoUrbano;
	}

	@Column( name="CONSUMO_EXTRAURBANO" )
	public BigDecimal getConsumoExtraurbano() {
		return consumoExtraurbano;
	}

	public void setConsumoExtraurbano(BigDecimal consumoExtraurbano) {
		this.consumoExtraurbano = consumoExtraurbano;
	}

	@ExpoProperty( prefixKey = "carburante.tipo." )
	@Column( name="TIPO_CARBURANTE" )
	public Integer getTipoCarburante() {
		return tipoCarburante;
	}

	public void setTipoCarburante(Integer tipoCaburante) {
		this.tipoCarburante = tipoCaburante;
	}
	
	
}
