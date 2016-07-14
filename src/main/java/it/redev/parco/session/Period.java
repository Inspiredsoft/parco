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

package it.redev.parco.session;

import it.inspired.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class Period 
{
	private static final String format = "dd/MM/yyyy HH:mm";
	
	private Date periodFrom = new Date();
	private Date periodTo = new Date();
	
	//--------------------------------------------------------------------------------------
	
	public Period(){}
	
	public Period( Date from, Date to)
	{
		this.periodFrom = from;
		this.periodTo 	= to;
	}
	
	//--------------------------------------------------------------------------------------
	
	public Date getPeriodFrom() 
	{
		return periodFrom;
	}

	public void setPeriodFrom(Date periodFrom)
	{
		this.periodFrom = periodFrom;
	}
	
	//--------------------------------------------------------------------------------------

	public Date getPeriodTo() 
	{
		return periodTo;
	}

	public void setPeriodTo(Date periodTo)
	{
		this.periodTo = periodTo;
	}	

	//--------------------------------------------------------------------------------------

	public Date getMorningPeriodFrom()
	{
		return ( periodFrom != null ? DateUtils.toMorning( periodFrom ) : periodFrom );
	}
	
	public Date getMidnighthPeriodTo()
	{
		return ( periodTo != null ? DateUtils.toMidnight( periodTo ) : periodTo );
	}
	
	//--------------------------------------------------------------------------------------
	
	public String toString()
	{
		return DateUtils.format( periodFrom, format ) + " - " + DateUtils.format( periodTo, format );
	}
	
	//--------------------------------------------------------------------------------------
	
	public static Period parse( String value ) throws ParseException
	{
		Period period = new Period();
		String[] dates = value.split("-");
		if ( dates.length == 2 )
		{
			period.periodFrom 	= DateUtils.parse( dates[0].trim(), format );
			period.periodTo	= DateUtils.parse( dates[1].trim(), format );
		}
		else
		{
			throw new ParseException("Character '-' not found", 0);
		}
		return period;
	}
	
	//--------------------------------------------------------------------------------------
	
	public static Period monthPeriod( Date date )
	{
		Period period = new Period();
		
		period.periodFrom 	= DateUtils.firstDayOfMoth( date );
		period.periodTo		= DateUtils.lastDayOfMoth( date );
		
		return period;
	}
	
	//--------------------------------------------------------------------------------------
	
	public static Period currentMonthPeriod()
	{
		Period period = new Period();
		
		period.periodFrom 	= DateUtils.firstDayOfMoth( new Date() );
		period.periodTo		= DateUtils.lastDayOfMoth( new Date() );
		
		return period;
	}
	
	//--------------------------------------------------------------------------------------
	
	public boolean isValid()
	{
		return !( this.periodTo.before( this.periodFrom) );
	}
}
