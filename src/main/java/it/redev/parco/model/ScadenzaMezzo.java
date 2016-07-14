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

package it.redev.parco.model;

import java.text.ParseException;

import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Polizza;
import it.redev.parco.session.Period;
import it.inspired.utils.DateUtils;

public class ScadenzaMezzo
{
	private Mezzo 	mezzo;
	private Polizza polizza;
	private Period	period;
	
	public ScadenzaMezzo(Mezzo mezzo, Polizza polizza) 
	{
		super();
		this.mezzo = mezzo;
		this.polizza = polizza;
	}
	
	public ScadenzaMezzo(String period, Mezzo mezzo, Polizza polizza) throws ParseException 
	{
		super();
		this.mezzo = mezzo;
		this.polizza = polizza;
		this.period = Period.parse( period );
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Mezzo getMezzo() {
		return mezzo;
	}
	
	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}
	
	public Polizza getPolizza() {
		return polizza;
	}
	
	public void setPolizza(Polizza polizza) {
		this.polizza = polizza;
	}
	
	public boolean isScadenzaBollo()
	{
		return mezzo.getScadenzaBollo() != null && DateUtils.inRange( mezzo.getScadenzaBollo(), period.getMorningPeriodFrom(), period.getMidnighthPeriodTo() );
	}
	
	public boolean isScadenzaRevisione()
	{
		return mezzo.getScadenzaRevisione() != null && DateUtils.inRange( mezzo.getScadenzaRevisione(), period.getMorningPeriodFrom(), period.getMidnighthPeriodTo() );
	}
	
	public boolean isScadenzaPolizza()
	{
		return polizza != null && DateUtils.inRange( polizza.getDataScadenza(), period.getMorningPeriodFrom(), period.getMidnighthPeriodTo() );
	}
}
