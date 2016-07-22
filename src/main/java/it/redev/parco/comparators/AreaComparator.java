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

package it.redev.parco.comparators;

import it.redev.parco.model.oc.Area;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AreaComparator implements Comparator<Area> {
	@Override
	public int compare(Area o1, Area o2) {
		return o1.getNome().compareTo( o2.getNome() );
	}
	
	public static List<Area> sort( List<Area> aree ) {
		Collections.sort(aree, new AreaComparator());
		return aree;
	}
}
