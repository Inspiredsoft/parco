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

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.mezzi.Mezzo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "CARTE_CARBURANTE")
//@DiscriminatorValue( "1" )
public class CartaCarburante extends Asset
{
	private String 	numero;
	private String 	identificativo;
	private String 	pinCode;
	private Date    scadenza;
	private Integer	gestore;
	private Mezzo	mezzo;
	private String 	note;
	
	//----------------------------------------------------------------------
	
	public static class Stato
	{
		public static final Integer ATTIVA		= 1;
		public static final Integer REVOCATA	= 2;
		public static final Integer SCADUTA		= 3;
		
		public static final Integer list[] = 
		{
			ATTIVA,
			REVOCATA,
			SCADUTA
		};
		
		public static Integer parse( String value )
		{
			if ( value.equalsIgnoreCase( "ATTIVA") )
			{
				return ATTIVA;
			}
			else if ( value.equalsIgnoreCase( "REVOCATA" ) )
			{
				return REVOCATA;
			}
			else if ( value.equalsIgnoreCase( "SCADUTA" ) )
			{
				return SCADUTA;
			}
			return null;
		}
	}
	
	//----------------------------------------------------------------------
	
	@NotNull
	@Column( name="NUMERO" )
	@Length( max = 100 )
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@Column( name="IDENTIFICATIVO" )
	@Length( max = 100 )
	public String getIdentificativo() {
		return identificativo;
	}
	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}
	
	@Column( name="PIN_CODE" )
	@Length( max = 50 )
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pin) {
		this.pinCode = pin;
	}
	
	@Column( name="SCADENZA" )
	public Date getScadenza() {
		return scadenza;
	}
	
	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}
	
	@ExpoProperty( prefixKey = "gestore." )
	@NotNull
	@Column( name="GESTORE" )
	public Integer getGestore() {
		return gestore;
	}
	public void setGestore(Integer gestore) {
		this.gestore = gestore;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn( name="ID_MEZZO", nullable=false )
	public Mezzo getMezzo() {
		return mezzo;
	}
	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}
	
	@Column( name="NOTE" )
	@Length(max=65535)
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	//----------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public boolean isAttiva()
	{
		return Stato.ATTIVA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isRevocata() {
		return Stato.REVOCATA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isScaduta() {
		return Stato.SCADUTA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isAttiva( Date when ) { 
		Integer stato = getStato(when);
		return stato != null && Stato.ATTIVA.equals( stato );
	}
	
}
