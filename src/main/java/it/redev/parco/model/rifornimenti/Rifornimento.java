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

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperties;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.model.Identifiable;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.mezzi.AssegnazioneMezzo;
import it.redev.parco.model.mezzi.Mezzo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement
@Entity
@Table (name = "RIFORNIMENTI")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name = "GESTORE", discriminatorType= DiscriminatorType.INTEGER)
public class Rifornimento extends Identifiable implements Serializable
{
	private Integer 		id;
	private Integer 		gestore;
	private Mezzo			mezzo;
	private CartaCarburante carta;
	private Distributore	distributore;
	private String			numeroScontrino;
	private	Date			data;
	private Integer			chilometraggio;
	private Integer			tipoCarburante;
	private BigDecimal		quantita;
	private BigDecimal		importo;
	private Scontrino		scontrino;
	private ImportInfo		importInfo;
	private String			prodotto;
	private String 			aaaammgg;
	private PinCard 		pinCard;
	
	private List<Anomalia>  anomalie;
	
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
	@JoinColumn( name = "ID_MEZZO" )
	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_CARTA" )
	public CartaCarburante getCarta() {
		return carta;
	}

	public void setCarta(CartaCarburante carta) {
		this.carta = carta;
	}

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_DISTRIBUTORE" )
	public Distributore getDistributore() {
		return distributore;
	}

	public void setDistributore(Distributore distributore) {
		this.distributore = distributore;
	}

	@NotNull
	@Column( name="NUMERO_SCONTRINO" )
	public String getNumeroScontrino() {
		return numeroScontrino;
	}

	public void setNumeroScontrino(String numeroScontrino) {
		this.numeroScontrino = numeroScontrino;
	}

	@NotNull
	@Column( name="DATA" )
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	@Length( max = 8 )
	@Column( name="AAAAMMGG" )
	public String getAaaammgg() {
		return aaaammgg;
	}
	
	public void setAaaammgg(String aaaammgg) {
		this.aaaammgg = aaaammgg;
	}

	@NotNull
	@Column( name="CHILOMETRAGGIO" )
	public Integer getChilometraggio() {
		return chilometraggio;
	}

	public void setChilometraggio(Integer chilomnetraggio) {
		this.chilometraggio = chilomnetraggio;
	}

	@ExpoProperties( property = { @ExpoProperty( prefixKey = "carburante.tipo.") } ) 
	@Column( name="TIPO_CARBURANTE" )
	public Integer getTipoCarburante() {
		return tipoCarburante;
	}

	public void setTipoCarburante(Integer tipoCarburante) {
		this.tipoCarburante = tipoCarburante;
	}

	@NotNull
	@Column( name="QUANTITA" )
	public BigDecimal getQuantita() {
		return quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
	}

	@NotNull
	@Column( name="IMPORTO" )
	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	@OneToOne(mappedBy="rifornimento", fetch=FetchType.LAZY, cascade={CascadeType.REMOVE})
	public Scontrino getScontrino() {
		return scontrino;
	}

	public void setScontrino(Scontrino scontrino) {
		this.scontrino = scontrino;
	}

	@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_IMPORT_INFO" )
	public ImportInfo getImportInfo() {
		return importInfo;
	}

	public void setImportInfo(ImportInfo importInfo) {
		this.importInfo = importInfo;
	}
	
	@OneToMany( mappedBy="rifornimento" )
	public List<Anomalia> getAnomalie() {
		return anomalie;
	}
	
	public void setAnomalie(List<Anomalia> anomalie) {
		this.anomalie = anomalie;
	}
	
	@Length( max = 100 )
	@Column( name="PRODOTTO" )
	public String getProdotto() {
		return prodotto;
	}
	
	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}
	
	//@ExpoProperties( property = { @ExpoProperty("codiceOperatore"), @ExpoProperty("codiceBusta") } )
	//@NotNull
	@ManyToOne
	@JoinColumn( name = "ID_PIN_CARD" )
	public PinCard getPinCard() {
		return pinCard;
	}
	
	public void setPinCard(PinCard pinCard) {
		this.pinCard = pinCard;
	}
	
		
	//--------------------------------------------------------------------
	
	/*@Transient
	public Integer getChilometraggioDichiarato()
	{
		Integer km = getChilometraggio();
		if ( scontrino.getChilometraggioRettificato() != null )
		{
			km = scontrino.getChilometraggioRettificato();
		}
		return km;
	}*/
	
	//--------------------------------------------------------------------



	@Unexportable
	@Transient
	public boolean isAgip()
	{
		return this.gestore.equals( Gestore.AGIP );
	}
	
	@Unexportable
	@Transient
	public boolean isQ8()
	{
		return this.gestore.equals( Gestore.Q8 );
	}
	
	//--------------------------------------------------------------------
	
	@Transient
	public AssegnazioneMezzo getAssegnazione()
	{
		return mezzo.getAssegnazione( this.data );
	}
}
