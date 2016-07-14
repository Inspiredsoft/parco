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

import it.inspired.exporter.annotation.ExportType;
import it.inspired.exporter.annotation.ExpoElement;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

@SuppressWarnings("serial")
@ExpoElement( ExportType.SUPERCLASS )
@Entity
@Table (name = "RIFORNIMENTO_AGIP")
@DiscriminatorValue( "1" )
public class RifornimentoAgip extends Rifornimento
{
	private Date	dataFattura;
	private String	extra;
	
	@Column( name="DATA_FATTURA" )
	public Date getDataFattura() {
		return dataFattura;
	}
	public void setDataFattura(Date dataFattura) {
		this.dataFattura = dataFattura;
	}
	
	@Length(max=65535)
	@Column( name="EXTRA" )
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	
}
