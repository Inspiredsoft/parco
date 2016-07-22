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

import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Entity
@Table( name="DOTAZIONE_MEZZO" )
//@DiscriminatorValue( "3" )
public class DotazioneMezzo extends Asset 
{
	private Postazione 	postazione;
	private String 		matricola;
	private Mezzo 		mezzo;
	
	//----------------------------------------------------------------------
	
	public static class Stato
	{
		public static final Integer ATTIVO		= 1;
		public static final Integer RITIRATO	= 2;
		public static final Integer RUBATO		= 3;
		
		public static final Integer list[] = 
		{
			ATTIVO,
			RITIRATO,
			RUBATO
		};
		
		public static Integer parse( String value )
		{
			if ( value.equalsIgnoreCase( "ATTIVO") )
			{
				return ATTIVO;
			}
			else if ( value.equalsIgnoreCase( "RITIRATO" ) )
			{
				return RITIRATO;
			}
			else if ( value.equalsIgnoreCase( "RUBATO" ) )
			{
				return RUBATO;
			}
			return null;
		}
	}
	
	//------------------------------------------------------------------------------------
	
	@ManyToOne
	@JoinColumn( name="ID_POSTAZIONE")
	public Postazione getPostazione() {
		return postazione;
	}

	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}
	
	@ManyToOne
	@JoinColumn( name="ID_MEZZO")
	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}
	
	@Length( max = 100 )
	@Column( name="MATRICOLA" )
	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}
}
