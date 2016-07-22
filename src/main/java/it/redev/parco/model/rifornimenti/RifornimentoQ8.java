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

import it.inspired.exporter.annotation.ExportType;
import it.inspired.exporter.annotation.ExpoElement;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement( ExportType.SUPERCLASS )
@Entity
@Table (name = "RIFORNIMENTO_Q8")
@DiscriminatorValue( "2" )
public class RifornimentoQ8 extends Rifornimento
{
	private BigDecimal	importoNonScontato;
	private BigDecimal	importoScontato;
	private BigDecimal	prezzoNonScontato;
	private BigDecimal	prezzoScontato;
	private String		utilizzatore;
	private String		codiceTerminale;
	private String		cartissima;
	
	@Column( name="IMPORTO_NON_SCONTATO" )
	public BigDecimal getImportoNonScontato() {
		return importoNonScontato;
	}
	
	public void setImportoNonScontato(BigDecimal importoNonScontato) {
		this.importoNonScontato = importoNonScontato;
	}
	
	@Column( name="IMPORTO_SCONTATO" )
	public BigDecimal getImportoScontato() {
		return importoScontato;
	}
	
	public void setImportoScontato(BigDecimal importoScontato) {
		this.importoScontato = importoScontato;
	}
	
	@Column( name="PREZZO_NON_SCONTATO" )
	public BigDecimal getPrezzoNonScontato() {
		return prezzoNonScontato;
	}
	
	public void setPrezzoNonScontato(BigDecimal prezzoNonScontato) {
		this.prezzoNonScontato = prezzoNonScontato;
	}
	
	@Column( name="PREZZO_SCONTATO" )
	public BigDecimal getPrezzoScontato() {
		return prezzoScontato;
	}
	
	public void setPrezzoScontato(BigDecimal prezzoScontato) {
		this.prezzoScontato = prezzoScontato;
	}
	
	@Length( max = 100 )
	@Column( name="UTILIZZATORE" )
	public String getUtilizzatore() {
		return utilizzatore;
	}
	
	public void setUtilizzatore(String utilizzatore) {
		this.utilizzatore = utilizzatore;
	}
	
	@Length( max = 100 )
	@Column( name="CODICE_TERMINALE" )
	public String getCodiceTerminale() {
		return codiceTerminale;
	}
	public void setCodiceTerminale(String codiceTerminale) {
		this.codiceTerminale = codiceTerminale;
	}

	@Length( max = 100 )
	@Column( name="CARTISSIMA" )
	public String getCartissima() {
		return cartissima;
	}

	public void setCartissima(String cartissima) {
		this.cartissima = cartissima;
	}
	
	
}
