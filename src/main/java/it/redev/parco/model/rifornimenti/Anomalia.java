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

package it.redev.parco.model.rifornimenti;

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.Identifiable;

import java.io.Serializable;

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

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table( name="ANOMALIE" )
public class Anomalia extends Identifiable implements Serializable
{
	private Integer 		id;
	private int 			version = 0;
	private String			descrizione;
	private String			nota;
	private Rifornimento 	rifornimento;
	private Rifornimento 	rifornimentoPrecedente;
	private Integer			tipo;
	private Integer			stato;
	
	//---------------------------------------------------------------------------------------
	
	public static class Stato
	{
		public static Integer RILEVATA 		= 1;
		public static Integer RETTIFICATA 	= 2;
		public static Integer CONSEGUENTE	= 3;
		
		public static final Integer list[] = 
		{
			RILEVATA,
			RETTIFICATA,
			CONSEGUENTE
		};
	}
	
	//---------------------------------------------------------------------------------------
	
	public static class Tipo
	{
		public static Integer CHILOMETRAGGIO 	= 1; 	// Chilometraggio inferiore al minimo stabilito
		public static Integer FREQUENZA 		= 2;	// Rifornimento avvenuto entro un numero minimo di ore dal precedente
		public static Integer CARTA				= 3;	// Utilizzo della carta su un mezzo differente da quello associato
		public static Integer CARBURANTE		= 4;	// Il carburante utilizzato e' differente da quello previsto per il mezzo
		public static Integer CONSUMO			= 5;	// Consumo inferiore al consumo ubano definito per il tipo di mezzo
		public static Integer SERBATOIO			= 6;	// Rifornimento superiore alla capacita' del serbatoio
		public static Integer STATO				= 7;	// Mezzo non in servizio alla data del rifornimento
		public static Integer CARBURANTE_AMMESSO= 8;	// Tipo di carburante non consentito
		public static Integer FREQUENZA_PIN 	= 9;	// Pin utilizzato entro un numero minimo di ore dal precedente
		public static Integer PIN_NON_ATTIVO	= 10;	// Uso di un pin non attivo
		public static Integer CARTA_NON_ATTIVA	= 11;	// Uso di una carta non attiva
		
		public static final Integer list[] = 
		{
			CHILOMETRAGGIO,
			FREQUENZA,
			CARTA,
			CARBURANTE,
			CONSUMO,
			SERBATOIO,
			STATO,
			CARBURANTE_AMMESSO,
			FREQUENZA_PIN,
			PIN_NON_ATTIVO,
			CARTA_NON_ATTIVA
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

	@Length(max=1000)
	@Column( name="DESCRIZIONE" )
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Length(max=65535)
	@Column( name="NOTA" )
	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ID_RIFORNIMENTO")
	public Rifornimento getRifornimento() {
		return rifornimento;
	}

	public void setRifornimento(Rifornimento rifornimento) {
		this.rifornimento = rifornimento;
	}

	@Unexportable
	@ManyToOne
	@JoinColumn(name = "ID_RIFORNIMENTO_PRECEDENTE")
	public Rifornimento getRifornimentoPrecedente() {
		return rifornimentoPrecedente;
	}

	public void setRifornimentoPrecedente(Rifornimento rifornimento) {
		this.rifornimentoPrecedente = rifornimento;
	}

	@ExpoProperty(prefixKey="anomalia.tipo.")
	@NotNull
	@Column( name="TIPO" )
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	@ExpoProperty(prefixKey="anomalia.stato.")
	@NotNull
	@Column( name="STATO" )
	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	//---------------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public boolean isTipoCarburante()
	{
		return this.tipo.equals( Tipo.CARBURANTE );
	}
	
	@Unexportable
	@Transient
	public boolean isTipoCarburanteAmmesso()
	{
		return this.tipo.equals( Tipo.CARBURANTE_AMMESSO );
	}
	
}
