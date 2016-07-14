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

package it.redev.parco.model.asset;

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperties;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.oc.Persona;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "PIN_CARD")
//@DiscriminatorValue( "4" )
public class PinCard extends Asset {

	private String 	codiceBusta;
	private String 	codiceOperatore;
	private Integer	gestore;
	private String 	note;
	
	//----------------------------------------------------------------------
	
	public static class Stato
	{
		public static final Integer ATTIVA		= 1;
		public static final Integer REVOCATA	= 2;
		public static final Integer SMARRITA	= 3;
		public static final Integer BLOCCATA	= 4;
		
		public static final Integer list[] = 
		{
			ATTIVA,
			REVOCATA,
			SMARRITA,
			BLOCCATA
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
			else if ( value.equalsIgnoreCase( "SMARRITA" ) )
			{
				return SMARRITA;
			}
			else if ( value.equalsIgnoreCase( "BLOCCATA" ) )
			{
				return BLOCCATA;
			}
			return null;
		}
	}
	
	//----------------------------------------------------------------------
	
	@Column( name="CODICE_BUSTA" )
	@Length( max = 100 )
	public String getCodiceBusta() {
		return codiceBusta;
	}

	public void setCodiceBusta(String codiceBusta) {
		this.codiceBusta = codiceBusta;
	}
	
	@Column( name="CODICE_OPERATORE" )
	@Length( max = 100 )
	public String getCodiceOperatore() {
		return codiceOperatore;
	}

	public void setCodiceOperatore(String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
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
	
	@Column( name="NOTE" )
	@Length(max=65535)
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	//----------------------------------------------------------------------
	
	@ExpoProperty( labelKey = "label.attiva", prefixKey = "label.attiva."  )
	@Transient
	public boolean isAttiva() {
		return Stato.ATTIVA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isBloccata() {
		return Stato.BLOCCATA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isRevocato() {
		return Stato.REVOCATA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isSmarrita() {
		return Stato.SMARRITA.equals( getStato().getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isAttiva( Date when ) { 
		Integer stato = getStato(when);
		return stato != null && Stato.ATTIVA.equals( stato );
	}
	
	//-----------------------------------------------------------------------------

//	@ExpoProperties( 
//			labelKey = "label.assegnazione", 
//			property = { 	@ExpoProperty("id"), 
//							@ExpoProperty("matricola"),
//							@ExpoProperty("cognome"), 
//							@ExpoProperty("nome"), 
//							@ExpoProperty("qualifica") } )
	@Transient
	public Persona getAssegnazione()
	{
		MovimentoAsset movimento = super.getLastMovimentoAsset();
		if ( movimento != null )
		{
			return movimento.getAssegnatario();
		}
		return null;
	}
	
}
