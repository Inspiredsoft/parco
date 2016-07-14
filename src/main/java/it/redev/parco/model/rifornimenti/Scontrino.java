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

import it.inspired.exporter.annotation.ExpoProperties;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.StatoInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.mezzi.AssegnazioneMezzo;
import it.redev.parco.model.mezzi.Mezzo;
import it.inspired.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "SCONTRINI")
public class Scontrino extends Identifiable implements Serializable
{
	private Integer 		id;
	private int 			version = 0;
	private String			numero;
	private Mezzo 			mezzoRettificato;
	private Integer			chilometraggioRettificato; 
	private StatoInfo		stato = new StatoInfo();
	private Rifornimento 	rifornimento;
	private String			note;
	
	private List<StatoScontrino> stati = new ArrayList<StatoScontrino>();
	
	//---------------------------------------------------------------------------------------
	
	public static class Stato
	{
		public static Integer	DA_GIUSTIFICARE		= 1;
		public static Integer	GIUSTIFICATO		= 2;
		public static Integer	RETTIFICATO			= 3;
		public static Integer	ILLEGIBILE			= 4;
		public static Integer	SMARRITO			= 5;
		public static Integer	ALTRO				= 6;
		
		public static final Integer list[] = 
		{
			DA_GIUSTIFICARE,
			GIUSTIFICATO,
			RETTIFICATO,
			ILLEGIBILE,
			SMARRITO,
			ALTRO
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

	@Column( name="NUMERO" )
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@ExpoProperty( "targa" )
	@ManyToOne
	@JoinColumn(name = "ID_MEZZO_RETTIFICATO")
	public Mezzo getMezzoRettificato() {
		return mezzoRettificato;
	}

	public void setMezzoRettificato(Mezzo mezzoRettificato) {
		this.mezzoRettificato = mezzoRettificato;
	}

	@Column( name="CHILOMETRAGGIO_RETTIFICATO" )
	public Integer getChilometraggioRettificato() {
		return chilometraggioRettificato;
	}

	public void setChilometraggioRettificato(Integer chilometraggioRettigicato) {
		this.chilometraggioRettificato = chilometraggioRettigicato;
	}

	@ExpoProperties( property ={ @ExpoProperty( value = "stato", prefixKey = "scontrino.stato."  ), @ExpoProperty("dataStato") } )
	@Embedded
	public StatoInfo getStato() {
		return stato;
	}

	public void setStato(StatoInfo stato) {
		this.stato = stato;
	}

	@OrderBy("dataInizio DESC")
	@OneToMany(mappedBy="scontrino", cascade={CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
	public List<StatoScontrino> getStati() {
		return stati;
	}

	public void setStati(List<StatoScontrino> stati) {
		this.stati = stati;
	}
	
	@NotNull
	@JoinColumn( name = "ID_RIFORNIMENTO" )
	@OneToOne(fetch=FetchType.LAZY)
	public Rifornimento getRifornimento() {
		return rifornimento;
	}
	
	public void setRifornimento(Rifornimento rifornimento) {
		this.rifornimento = rifornimento;
	}
	
	@Length(max=65535)
	@Column(name = "NOTE")
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	//----------------------------------------------------------------------------------

	@Transient
	public List<Anomalia> getAnomalie()
	{
		return rifornimento.getAnomalie();
	}
	
	//----------------------------------------------------------------------------------

	@Transient
	public boolean isAnomalo()
	{
		return !getAnomalie().isEmpty();
	}
	
	@Unexportable
	@Transient
	public boolean isGiustificato()
	{
		return Stato.GIUSTIFICATO.equals( this.stato.getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isDaGiustificare()
	{
		return Stato.DA_GIUSTIFICARE.equals( this.stato.getStato() );
	}
	
	@Unexportable
	@Transient
	public boolean isWrongNumber()
	{
		return this.getNumero() != null && !StringUtils.equalsIgnoreZeros( this.getNumero(), this.rifornimento.getNumeroScontrino() );
	}
	
	//----------------------------------------------------------------------------------

	public void cambiaStato(Integer newStatus, Date data, User user, String note) 
	{
		if ( stato.getDataStato() == null )
		{
			stato.cambia( newStatus, data, user, note );
		}
		else
		{
			StatoScontrino ss = new StatoScontrino( this.stato );
			ss.setScontrino( this );
			ss.setDataFine( data );
			stato.cambia( newStatus, data, user, note );
			stati.add( ss );
		}
	}	
	
	//--------------------------------------------------------------------
	
	@Transient
	public AssegnazioneMezzo getAssegnazione()
	{
		return rifornimento.getAssegnazione();
	}
}
