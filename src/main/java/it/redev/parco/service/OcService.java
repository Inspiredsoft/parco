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

package it.redev.parco.service;

import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class OcService extends BaseService 
{
	@SuppressWarnings("unchecked")
	public List<Provincia> findAllProvince()
	{
		Query query = em.createQuery("FROM " + Provincia.class.getSimpleName() );
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Area> findAllAree()
	{
		Query query = em.createQuery("FROM " + Area.class.getSimpleName() );
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Postazione> findAllPostazioni()
	{
		Query query = em.createQuery("FROM " + Postazione.class.getSimpleName() );
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Persona> findNotRemovedPersonaLike( String filtro )
	{
		Query query = em.createQuery(
				"FROM " + Persona.class.getSimpleName() + " p " +
				"WHERE ( p.matricola LIKE :filtro OR p.cognome LIKE :filtro OR p.nome LIKE :filtro ) " +
				"AND   p.removed = false ")
				.setParameter("filtro", "%" + filtro + "%");
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Persona> findNotRemovedPersonaByMatricola( String matricola )
	{
		Query query = em.createQuery(
				"FROM " + Persona.class.getSimpleName() + " p " +
				"WHERE p.matricola = :matricola " +
				"AND   p.removed = false ")
				.setParameter("matricola", matricola);
		return query.getResultList();
	}
}
