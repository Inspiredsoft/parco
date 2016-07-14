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

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.Removable;
import it.redev.parco.model.StatoInfo;
import it.redev.parco.model.Statusable;
import it.redev.parco.model.User;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.inspired.utils.DateUtils;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "MEZZI")
public class Mezzo extends Identifiable implements Statusable, Serializable, Removable
{
	private Integer 	id;
	private int 		version = 0;
	private boolean		removed = false;
	private Date 		removalDate;
	private String		targa;
	private String		codiceRadio;
	private String 		note;
	private TipoMezzo	tipoMezzo;
	private Date 		scadenzaRevisione;
	private Date 		scadenzaBollo;
	private StatoInfo	stato = new StatoInfo();
	
	private List<AssegnazioneMezzo>	assegnazioni = new ArrayList<AssegnazioneMezzo>();
	
	private List<Polizza> polizze = new ArrayList<Polizza>();
	
	private List<StatoMezzo> stati = new ArrayList<StatoMezzo>();
	
	private List<DotazioneMezzo> dotazioni = new ArrayList<DotazioneMezzo>();
	
	//---------------------------------------------------------------------------------------
	
	public static class Stati
	{
		public static Integer IN_SERVIZIO		= 1;
		public static Integer ROTTAMATO			= 2;
		public static Integer RIPARAZIONE		= 3;
		public static Integer RUBATA			= 4;
		public static Integer FUORI_SERVIZIO	= 5;
		
		public static final Integer list[] = 
		{
			IN_SERVIZIO,
			ROTTAMATO,
			RIPARAZIONE,
			RUBATA,
			FUORI_SERVIZIO
		};
		
		public static Integer parse( String stato )
		{
			if ( stato.equalsIgnoreCase( "SERVIZIO" ) || stato.equalsIgnoreCase( "IN SERVIZIO" ))
			{
				return IN_SERVIZIO;
			}
			else if ( stato.equalsIgnoreCase( "ROTTAMATO" ) || stato.equalsIgnoreCase( "ROTTAMATA" ) )
			{
				return ROTTAMATO;
			}
			else if ( stato.equalsIgnoreCase( "RIPARAZIONE" ) || stato.equalsIgnoreCase( "IN RIPARAZIONE" ) )
			{
				return RIPARAZIONE;
			}
			else if ( stato.equalsIgnoreCase( "RUBATA" ) || stato.equalsIgnoreCase( "RUBATO" ) )
			{
				return RUBATA;
			}
			else if ( stato.equalsIgnoreCase( "FUORI_SERVIZIO" ) || stato.equalsIgnoreCase( "FUORI SERVIZIO" ))
			{
				return FUORI_SERVIZIO;
			}
			return null;
		}
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

	@NotNull
	@Column( name="TARGA" )
	@Length(max=50)
	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	@Column( name="CODICE_RADIO" )
	@Length(max=50)
	public String getCodiceRadio() {
		return codiceRadio;
	}

	public void setCodiceRadio(String codiceRadio) {
		this.codiceRadio = codiceRadio;
	}

	@Length(max=65535)
	@Column( name="NOTE" )
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@ManyToOne
	@JoinColumn(name = "ID_TIPO_MEZZO")
	public TipoMezzo getTipoMezzo() {
		return tipoMezzo;
	}

	public void setTipoMezzo(TipoMezzo tipoMezzo) {
		this.tipoMezzo = tipoMezzo;
	}

	@Column( name="DATA_SCADENZA_REVISIONE" )
	public Date getScadenzaRevisione() {
		return scadenzaRevisione;
	}

	public void setScadenzaRevisione(Date scadenzaRevisione) {
		this.scadenzaRevisione = scadenzaRevisione;
	}

	@Column( name="DATA_SCADENZA_BOLLO" )
	public Date getScadenzaBollo() {
		return scadenzaBollo;
	}

	public void setScadenzaBollo(Date scadenzaBollo) {
		this.scadenzaBollo = scadenzaBollo;
	}

	@Embedded
	@ExpoProperty(value="stato",prefixKey="mezzo.stato.")
	public StatoInfo getStato() {
		return stato;
	}

	public void setStato(StatoInfo stato) {
		this.stato = stato;
	}

	@OrderBy("dataInizio DESC")
	@OneToMany(mappedBy="mezzo", cascade={CascadeType.PERSIST, CascadeType.MERGE})
	public List<StatoMezzo> getStati() {
		return stati;
	}

	public void setStati(List<StatoMezzo> stati) {
		this.stati = stati;
	}

	@OrderBy("dataInizio DESC")
	@OneToMany(mappedBy="mezzo", cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.EAGER)
	public List<AssegnazioneMezzo> getAssegnazioni() {
		return assegnazioni;
	}

	public void setAssegnazioni(List<AssegnazioneMezzo> assegnazioni) {
		this.assegnazioni = assegnazioni;
	}
	
	@OneToMany(mappedBy="mezzo")
	public List<Polizza> getPolizze() {
		return polizze;
	}
	
	public void setPolizze(List<Polizza> polizze) {
		this.polizze = polizze;
	}
	
	@OneToMany(mappedBy="mezzo")
	public List<DotazioneMezzo> getDotazioni() {
		return dotazioni;
	}
	
	public void setDotazioni(List<DotazioneMezzo> dotazioni) {
		this.dotazioni = dotazioni;
	}

		
	//----------------------------------------------------------------------------------

	public void changeStatus( StatoInfo stato ) 
	{
		cambiaStato( stato.getStato(), stato.getDataStato(), stato.getUtenteStato(), stato.getNoteStato() );
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
			StatoMezzo sm = new StatoMezzo( this.stato );
			sm.setMezzo( this );
			sm.setDataFine( DateUtils.addDay( data, -1 ) );
			stato.cambia( newStatus, data, user, note );
			stati.add( sm );
		}
	}
	
	//----------------------------------------------------------------------------------
	
	public Integer findStato( Date data )
	{
		if ( !data.before( this.stato.getDataStato() ) )
		{
			return this.getStato().getStato();
		}
		else
		{
			for ( StatoMezzo stato : this.getStati() )
			{
				if ( !data.before( stato.getDataInizio() ) && !data.after( stato.getDataFine() ) )
				{
					return stato.getStato();
				}
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------
	
	public void deassegna( AssegnazioneMezzo ass )
	{
		if ( ass.getDataFine() == null )
		{
			AssegnazioneMezzo prev = getPrevAssegnazione( ass );
			if ( prev != null )
			{
				prev.setDataFine( null );
			}
			assegnazioni.remove( ass );
		}
	}

	//----------------------------------------------------------------------------------
	
	public void assegna( AssegnazioneMezzo ass )
	{
		AssegnazioneMezzo last = getLastAssegnazione();
		if ( last != null )
		{
			if ( last.getDataInizio().before( ass.getDataInizio() ) )
			{
				//last.setDataFine( DateUtils.toMidnight( DateUtils.addDay( ass.getDataInizio(),-1  ) ) );
				last.setDataFine( DateUtils.addMin( ass.getDataInizio(),-1  ) );
				assegnazioni.add( ass );
			}
		}
		else
		{
			assegnazioni.add( ass );
		}
		
	}
	
	//----------------------------------------------------------------------------------
	
	@Transient
	public AssegnazioneMezzo getAssegnazione()
	{
		for ( AssegnazioneMezzo ass : assegnazioni )
		{
			if ( ass.isCurrent() )
			{
				return ass;
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------
	
	@Transient
	public AssegnazioneMezzo getAssegnazione( Date data )
	{
		for ( AssegnazioneMezzo ass : assegnazioni )
		{
			if ( ass.isCurrent( data ) )
			{
				return ass;
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public AssegnazioneMezzo getLastAssegnazione()
	{
		for ( AssegnazioneMezzo ass : assegnazioni )
		{
			if ( ass.getDataFine() == null )
			{
				return ass;
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public AssegnazioneMezzo getPrevAssegnazione( AssegnazioneMezzo assegnazione )
	{
		AssegnazioneMezzo prev = null;
		for ( AssegnazioneMezzo ass : assegnazioni )
		{
			if ( ass.before( assegnazione ) )
			{
				if ( prev == null || ass.after(prev))
				{
					prev = ass;
				}
			}
		}
		return prev;
	}
}
