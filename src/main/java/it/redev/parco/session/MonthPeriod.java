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

package it.redev.parco.session;

import it.inspired.utils.DateUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MonthPeriod 
{
	private Integer year;
	private Integer month;
	
	//--------------------------------------------------------------------------------------
	
	public MonthPeriod() {}
	
	public MonthPeriod( int month, int year )
	{
		this.month = month;
		this.year  = year;
	}
	
	//--------------------------------------------------------------------------------------
	
	public MonthPeriod( Date date )
	{
		this.month = DateUtils.getMonth( date ) + 1;
		this.year  = DateUtils.getYear( date );
	}
	
	//--------------------------------------------------------------------------------------
	
	public MonthPeriod( MonthPeriod period )
	{
		this.month = period.month;
		this.year  = period.year;
	}

	//--------------------------------------------------------------------------------------
	
	public static MonthPeriod current()
	{
		return new MonthPeriod( DateUtils.todayMonth() + 1, DateUtils.todayYear() );
	}
	
	//--------------------------------------------------------------------------------------
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public void setMonth(Integer month) {
		this.month = month;
	}
	
	//--------------------------------------------------------------------------------------
	
	public List<Integer> getMonths()
	{
		return Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
	}
	
	//--------------------------------------------------------------------------------------
	
	public void next()
	{
		if ( month < 12 )
		{
			month++;
		}
		else
		{
			month = 1;
			year++;
		}
	}
	
	//--------------------------------------------------------------------------------------
	
	public boolean include( Date date )
	{
		return month.equals( DateUtils.getMonth( date ) ) && year.equals( DateUtils.getYear( date ) );
	}
	
	public boolean after( Date date )
	{
		return DateUtils.create( year, month, 1 ).after( date );
	}
	
	public boolean after( MonthPeriod period )
	{
		return DateUtils.create( year, month, 1 ).after( DateUtils.create( period.year, period.month, 1 ) );
	}
	
	//--------------------------------------------------------------------------------------

	public Date getMorningPeriodFrom()
	{
		Calendar cal = Calendar.getInstance();
		if ( month != null ) {
			cal.set( Calendar.MONTH, month - 1 );
		} else {
			cal.set( Calendar.MONTH, 0 );
		}
		if ( year != null ) {
			cal.set( Calendar.YEAR, year );
		} else {
			cal.set( Calendar.YEAR, DateUtils.getYear( new Date(Long.MIN_VALUE) ) );
		}
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		return DateUtils.toMorning( cal.getTime() );
	}
	
	public Date getMidnighthPeriodTo()
	{
		Calendar cal = Calendar.getInstance();
		if ( month != null ) {
			cal.set( Calendar.MONTH, month - 1 );
		} else {
			cal.set( Calendar.MONTH, 11 );
		}
		if ( year != null ) {
			cal.set( Calendar.YEAR, year );
		} else {
			cal.set( Calendar.YEAR, DateUtils.getYear( new Date(Long.MAX_VALUE) ) );
		}
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		Date date = DateUtils.lastDayOfMoth( cal.getTime() );
		return DateUtils.toMidnight( date );
	}
	
	//--------------------------------------------------------------------------------------
	
	public String toString()
	{
		return month + "/" + year;
	}
	
	//--------------------------------------------------------------------------------------
	
	public boolean equals( Object period ) {
		if ( period instanceof MonthPeriod ) {
			return this.month.equals( ((MonthPeriod)period).month ) && this.year.equals( ((MonthPeriod)period).year );
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------
	
	public static MonthPeriod parse( String value )
	{
		MonthPeriod period = new MonthPeriod();
		String[] values = value.split( "/" );
		if ( values.length == 2 )
		{
			period.month = Integer.parseInt( values[0].trim() );
			period.year = Integer.parseInt(  values[1].trim() );
		}
		return period;
	}

	//--------------------------------------------------------------------------------------
	
	public MonthPeriod clone() {
		return new MonthPeriod( this );
	}
	
}
