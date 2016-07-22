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

import java.util.ArrayList;
import java.util.List;

import it.redev.parco.model.mezzi.CodificaCarburante;
import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.model.rifornimenti.Rifornimento;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

import org.jboss.seam.transaction.Transactional;

@Named
@Stateless
public class RifornimentoService extends BaseService
{
	@Transactional
	public void updateCodificaCarburante( CodificaCarburante codifica )
	{
		Query query = getEntityManager().createQuery(
				"UPDATE " + CodificaCarburante.class.getName() + " cc " +
				"SET cc.tipo = :tipo " +
				"WHERE cc.valore = :valore" )
				.setParameter( "tipo", codifica.getTipo() )
				.setParameter( "valore", codifica.getValore() );
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<Anomalia> findAnomalieCollegate( Anomalia anomalia )
	{
		List<Rifornimento> rif = new ArrayList<Rifornimento>();
		rif.add( anomalia.getRifornimento() );
		if ( anomalia.getRifornimentoPrecedente() != null )
		{
			rif.add( anomalia.getRifornimentoPrecedente() );
		}
		
		Query query = getEntityManager().createQuery(
				"FROM Anomalia ano " +
				"WHERE ano != :anomalia " +
				"AND (ano.rifornimento IN (:rifornimenti) " +
				"OR    ano.rifornimentoPrecedente IN (:rifornimenti) )" )
			.setParameter( "anomalia", anomalia )
			.setParameter( "rifornimenti", rif );
		
		return query.getResultList();
	}

}
