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

package it.redev.parco.service;

import java.util.Date;

import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;

import javax.ejb.Stateless;
import javax.inject.Named;

@Named
@Stateless
public class ImportService extends BaseService
{
	public ImportInfo newImport( Integer tipo, String fileName, User user )
	{
		ImportInfo info = new ImportInfo();
		info.setFile( fileName );
		info.setTipo( tipo );
		info.setData( new Date() );
		info.setUtente( user );
		
		super.save( info );
		
		return info;
	}
}
