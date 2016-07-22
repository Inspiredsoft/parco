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

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.Unexportable;
import it.inspired.utils.StringUtils;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.Removable;
import it.redev.parco.model.asset.MovimentoAsset;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "PERSONE")
public class Persona extends Identifiable implements Serializable, Removable
{
	private Integer 	id;
	private int 		version = 0;
	private boolean		removed = false;
	private Date 		removalDate;
	private	String		nome;
	private String		cognome;
	private String		qualifica;
	private String		matricola;
	private Postazione 	postazione;
	
	private List<MovimentoAsset> assegnazioni; 
	
	//---------------------------------------------------------------------------------------
	
	public Persona() {
		super();
	}
	
	public Persona(String cognome) {
		super();
		this.cognome = cognome;
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
	
	@NotNull
	@Column( name="REMOVED" )
	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	@Override
	@Column( name="REMOVAL_DATE" )
	public Date getRemovalDate() {
		return removalDate;
	}

	public void setRemovalDate(Date removalDate) {
		this.removalDate = removalDate;
	}

	@ExpoProperty( position = 3 )
	@NotNull
	@Length( max = 100 )
	@Column( name="NOME" )
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@ExpoProperty( position = 2 )
	@NotNull
	@Length( max = 100 )
	@Column( name="COGNOME" )
	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@ExpoProperty( position = 4 )
	@Length( max = 100 )
	@Column( name="QUALIFICA" )
	public String getQualifica() {
		return qualifica;
	}

	public void setQualifica(String qualifica) {
		this.qualifica = qualifica;
	}

	@ExpoProperty( position = 1 )
	@NotNull
	@Length( max = 100 )
	@Column( name="MATRICOLA" )
	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}
	
	@ExpoProperty( position = 4, value = "nome", labelKey = "label.persona.postazione" )
	@ManyToOne
	@JoinColumn( name="ID_POSTAZIONE")
	public Postazione getPostazione() {
		return postazione;
	}

	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}
	
	@OrderBy("data DESC")
	@OneToMany(mappedBy="assegnatario")
	public List<MovimentoAsset> getAssegnazioni() {
		return assegnazioni;
	}
	
	public void setAssegnazioni(List<MovimentoAsset> assegnazioni) {
		this.assegnazioni = assegnazioni;
	}	
	
	//---------------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public String getNomeCompleto()
	{
		return ( StringUtils.isEmpty(this.nome) ? "" : this.nome + " " ) + ( StringUtils.isEmpty(this.cognome) ? "" : this.cognome );
	}
	
	//---------------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public String getNomeMatricola()
	{
		return getNomeCompleto() + " (" + this.matricola + ")";
	}
	
	//---------------------------------------------------------------------------------------
	
	public static Persona parseNomeMatricola( String value )
	{
		Persona p = null;
		if ( !StringUtils.isEmpty( value ) )
		{
			int start = value.indexOf("(");
			int end   = value.indexOf(")");
			
			if ( start<end )
			{
				p = new Persona();
				p.setMatricola( value.substring( start+1 , end ).trim() );
				
				end = value.indexOf( " " );
				if ( end > 0 )
				{
					p.setNome( value.substring( 0, end ).trim() );
					p.setCognome( value.substring( end+1, start ).trim() );
				}
				else
				{
					p.setCognome( value.substring( 0, value.indexOf("(") - 1 ).trim() );
				}
			}
		}
		return p;
	}
}
