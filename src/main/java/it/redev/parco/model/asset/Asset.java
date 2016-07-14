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

import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.Removable;
import it.redev.parco.model.StatoInfo;
import it.redev.parco.model.Statusable;
import it.redev.parco.model.User;
import it.inspired.utils.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@Table( name="ASSET" )
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Asset extends Identifiable implements Statusable, Serializable, Removable
{
	private Integer 	id;
	private int 		version = 0;
	private boolean		removed = false;
	private Date 		removalDate;
	private Integer		tipo;
	private GenereAsset genere;
	private StatoInfo	stato = new StatoInfo();
	
	private List<StatoAsset> stati = new ArrayList<StatoAsset>();
	
	private List<MovimentoAsset> movimenti = new ArrayList<MovimentoAsset>();
	
	//-------------------------------------------------------------------------------------------
	
	/**
	 * Indica se e' una dotazione del mezzo, della postazione o una fuel/pin card
	 */
	public static class Tipo
	{
		public static Integer FUEL_CARD 	= 1;
		public static Integer MEZZO 		= 2; // Dotazione di un mezzo
		public static Integer POSTAZIONE 	= 3; // Dotazione du una postazione
		public static Integer PINCARD 		= 4;
		
		public static final Integer list[] = 
		{
			Tipo.FUEL_CARD,
			Tipo.MEZZO,
			Tipo.POSTAZIONE,
			Tipo.PINCARD
		};
		
		public static Integer parse(String value)
		{
			if ( value.equalsIgnoreCase( "CARTA CARBURANTE" ) )
			{
				return FUEL_CARD;
			}
			else if ( value.equalsIgnoreCase( "MEZZO" ) )
			{
				return MEZZO;
			}
			else if ( value.equalsIgnoreCase( "POSTAZIONE" ) )
			{
				return POSTAZIONE;
			}
			else if ( value.equalsIgnoreCase( "PIN CARD" ) )
			{
				return PINCARD;
			}
			return null;
		}
	}
		
	//-------------------------------------------------------------------------------------------
	
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

	@Embedded
	public StatoInfo getStato() {
		return stato;
	}

	public void setStato(StatoInfo status) {
		this.stato = status;
	}

	@Unexportable
	@NotNull
	@Column( name="TIPO" )
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	@OrderBy("dataInizio DESC")
	@OneToMany(mappedBy="asset", cascade={CascadeType.PERSIST, CascadeType.MERGE})
	public List<StatoAsset> getStati() {
		return stati;
	}

	public void setStati(List<StatoAsset> statuses) {
		this.stati = statuses;
	}
	
	@OrderBy("data DESC")
	@OneToMany(mappedBy="asset", cascade={CascadeType.PERSIST, CascadeType.MERGE})
	public List<MovimentoAsset> getMovimenti() {
		return movimenti;
	}
	
	public void setMovimenti(List<MovimentoAsset> movimenti) {
		this.movimenti = movimenti;
	}
	
	@ManyToOne
	@JoinColumn(name = "ID_GENERE_ASSET", nullable = true)
	public GenereAsset getGenere() {
		return genere;
	}
	
	public void setGenere(GenereAsset genere) {
		this.genere = genere;
	}
		
	//----------------------------------------------------------------------------------
	

	@Transient
	public MovimentoAsset getLastMovimentoAsset()
	{
		MovimentoAsset last = null;
		for ( MovimentoAsset ma : movimenti )
		{
			if ( last == null || last.getData().before( ma.getData() ) )
			{
				last = ma;
			}
		}
		return last;
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
			StatoAsset sa = new StatoAsset( this.stato );
			sa.setAsset( this );
			sa.setDataFine(  DateUtils.addDay( data, -1 ) );
			stato.cambia( newStatus, data, user, note );
			stati.add( sa );
		}
	}

	//----------------------------------------------------------------------------------
	
	@Override
	public void changeStatus(StatoInfo status) 
	{
		cambiaStato( status.getStato(), status.getDataStato(), status.getUtenteStato(), status.getNoteStato() );
	}
	
	//----------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public boolean isConsegnato()
	{
		MovimentoAsset last = getLastMovimentoAsset();
		return ( last != null && last.isConsegnata() );
	}
	
	//----------------------------------------------------------------------------------
	
	@Unexportable
	@Transient
	public boolean isFuelCard() {
		return Tipo.FUEL_CARD.equals( this.tipo );
	}
	
	@Unexportable
	@Transient
	public boolean isDotazioneMezzo() {
		return Tipo.MEZZO.equals( this.tipo );
	}
	
	@Unexportable
	@Transient
	public boolean isDotazionePostazione() {
		return Tipo.POSTAZIONE.equals( this.tipo );
	}
	
	@Unexportable
	@Transient
	public boolean isPinCard() {
		return Tipo.PINCARD.equals( this.tipo );
	}
	
	//----------------------------------------------------------------------------------
	
	public Integer getStato( Date when ) {
		if ( stato.getDataStato().compareTo( when ) <= 0 ) {
			return stato.getStato();
		} else {
			for ( StatoAsset stato : stati ) {
				if ( stato.getDataInizio().before( when ) && stato.getDataFine().after( when ) ) {
					return stato.getStato();
				}
			}
		}
		return null;
	}
}
