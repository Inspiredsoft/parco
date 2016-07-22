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

package it.redev.parco.model.rifornimenti;

import it.redev.parco.model.Identifiable;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
@Entity
@Table (name = "DISTRIBUTORI")
public class Distributore extends Identifiable implements Serializable
{
	private Integer id;
	private int 	version = 0;
	private Integer	gestore;
	private String 	indirizzo;
	private String	cap;
	private String	provincia;
	private String	localita;
	
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
	@Column( name = "GESTORE" )
	public Integer getGestore() {
		return gestore;
	}

	public void setGestore(Integer gestore) {
		this.gestore = gestore;
	}

	@NotNull
	@Column( name = "INDIRIZZO" )
	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	@Column( name = "CAP" )
	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	@Column( name = "PROVINCIA" )
	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Column( name = "LOCALITA" )
	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}
	
	@Transient
	public String getFullAddress()
	{
		StringBuilder str = new StringBuilder();
		
		str.append( this.indirizzo );
		if ( !StringUtils.isEmpty( this.cap ) )
		{
			str.append( " " );
			str.append( this.cap );
		}
		if ( !StringUtils.isEmpty( this.localita ) )
		{
			str.append( " " );
			str.append( this.localita );
		}
		if ( !StringUtils.isEmpty( this.provincia ) )
		{
			str.append( "(" );
			str.append( this.provincia );
			str.append( ")" );
		}
		
		return str.toString();
		
	}
}
