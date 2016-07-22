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

import it.redev.parco.model.core.Configuration;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;

@Named
@Stateless
public class ConfigService extends BaseService
{
	@SuppressWarnings("unchecked")
	public List<Configuration> findAll()
	{
		return em.createQuery( "FROM Configuration" ).getResultList();
	}
	
	public void remove(Configuration conf) 
	{
		em.createQuery( "DELETE FROM Configuration WHERE id = :id" ).setParameter( "id", conf.getId() ).executeUpdate();
	}
	
	public void updateByName(Configuration conf) 
	{
		em.createQuery( 
				"UPDATE Configuration " +
				"SET value = :value, version = :version " +
				"WHERE name = :name" )
			.setParameter( "name", conf.getName() )
			.setParameter( "value", conf.getValue() )
			.setParameter( "version", conf.getVersion() ).executeUpdate();
	}
}
