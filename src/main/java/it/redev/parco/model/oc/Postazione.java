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

package it.redev.parco.model.oc;

import it.redev.parco.model.Eligible;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.asset.DotazioneMezzo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@Entity
@Table (name = "POSTAZIONE")
public class Postazione extends Identifiable implements Serializable, Eligible
{
	private Integer 	id;
	private int 		version = 0;
	private Area		area;
	private String		nome;
	private String		descrizione;
	private String		centroCosto;
	
	private List<DotazioneMezzo> dotazioni = new ArrayList<DotazioneMezzo>();
	
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
	@JoinColumn( name = "ID_AREA" )
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	@NotNull
	@Length( max = 50 )
	@Column( name="NOME" )
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Length( max = 200 )
	@Column( name="DESCRIZIONE" )
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Length( max = 200 )
	@Column( name="CENTRO_COSTO" )
	public String getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}
	
	@OneToMany(mappedBy="postazione")
	public List<DotazioneMezzo> getDotazioni() {
		return dotazioni;
	}
	
	public void setDotazioni(List<DotazioneMezzo> dotazioni) {
		this.dotazioni = dotazioni;
	}
	
}
