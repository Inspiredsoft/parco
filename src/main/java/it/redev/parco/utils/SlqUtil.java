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

package it.redev.parco.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlqUtil 
{
	private static final Pattern GROUP_PATTERN = Pattern.compile("\\s(group)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern ORDER_PATTERN = Pattern.compile("\\s(order)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern WHERE_PATTERN = Pattern.compile("\\s(where)\\s", Pattern.CASE_INSENSITIVE);

	//-----------------------------------------------------------------------------------------------
	
	public String getRenderedWhere( String sql )
	{
		Matcher orderMatcher = ORDER_PATTERN.matcher( sql );
        int orderLoc = orderMatcher.find() ? orderMatcher.start(1) :  sql.length();

        Matcher groupMatcher = GROUP_PATTERN.matcher( sql );
        int groupLoc = groupMatcher.find() ? groupMatcher.start(1) : orderLoc;

        Matcher whereMatcher = WHERE_PATTERN.matcher( sql );
        int whereLoc = whereMatcher.find() ? whereMatcher.start(1) : groupLoc;
        
        return sql.substring(whereLoc, groupLoc);
	}
}
