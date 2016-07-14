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

import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.mezzi.CompagniaAssicurazione;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Polizza;
import it.redev.parco.model.mezzi.TipoMezzo;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class MezzoService extends BaseService
{
	@SuppressWarnings("unchecked")
	public List<TipoMezzo> findAllTipoMezzo()
	{
		Query query = em.createQuery("FROM " + TipoMezzo.class.getSimpleName() );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<CompagniaAssicurazione> findAllCompagnie()
	{
		Query query = em.createQuery("FROM " + CompagniaAssicurazione.class.getSimpleName() );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<CartaCarburante> findAllCarte()
	{
		Query query = em.createQuery("FROM " + CartaCarburante.class.getSimpleName() );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<CartaCarburante> findCarta( String numero, Integer gestore )
	{
		Query query = em.createQuery(
				"FROM " + CartaCarburante.class.getSimpleName() + " c " +
				"WHERE c.numero = :numero " +
				"AND   c.gestore = :gestore ")
				.setParameter("numero", numero)
				.setParameter("gestore", gestore);
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<CartaCarburante> findNotRemovedCarta( String numero, Integer gestore )
	{
		Query query = em.createQuery(
				"FROM " + CartaCarburante.class.getSimpleName() + " c " +
				"WHERE c.numero = :numero " +
				"AND   c.gestore = :gestore " +
				"AND   c.removed = false ")
				.setParameter("numero", numero)
				.setParameter("gestore", gestore);
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<PinCard> findNotRemovedPinCartaByOperatore( String codiceOperatore, Integer gestore )
	{
		Query query = em.createQuery(
				"FROM " + PinCard.class.getSimpleName() + " pin " +
				"WHERE pin.codiceOperatore = :codice " +
				"AND   pin.gestore = :gestore " +
				"AND   pin.removed = false ")
				.setParameter("codice", codiceOperatore)
				.setParameter("gestore", gestore);
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<PinCard> findNotRemovedPinCartaByBusta( String codiceBusta, Integer gestore )
	{
		Query query = em.createQuery(
				"FROM " + PinCard.class.getSimpleName() + " pin " +
				"WHERE pin.codiceBusta = :codice " +
				"AND   pin.gestore = :gestore " +
				"AND   pin.removed = false ")
				.setParameter("codice", codiceBusta)
				.setParameter("gestore", gestore);
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Mezzo> findNotRemovedMezzoLikeTarga( String targa )
	{
		Query query = em.createQuery(
				"FROM " + Mezzo.class.getSimpleName() + " m " +
				"WHERE m.targa LIKE :targa " +
				"AND   m.removed = false ")
				.setParameter("targa", "%" + targa + "%");
		return query.getResultList();
	}

	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Mezzo> findNotRemovedMezzoByTarga( String targa )
	{
		Query query = em.createQuery(
				"FROM " + Mezzo.class.getSimpleName() + " m " +
				"WHERE m.targa = :targa " +
				"AND   m.removed = false ")
				.setParameter("targa", targa );
		return query.getResultList();
	}

	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<Polizza> findPolizze( Mezzo mezzo )
	{
		Query query = em.createQuery(
				"FROM " + Polizza.class.getSimpleName() + " p " +
				"WHERE p.mezzo = :mezzo ")
				.setParameter("mezzo", mezzo );
		return query.getResultList();
	}
	
	//----------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<CartaCarburante> findNotRemovedAttive( Mezzo mezzo )
	{
		Query query = em.createQuery(
				"FROM " + CartaCarburante.class.getSimpleName() + " c " +
				"WHERE c.removed = false " +
				"AND c.stato.stato = :stato " +
				"AND c.mezzo = :mezzo ")
				.setParameter("stato", CartaCarburante.Stato.ATTIVA )
				.setParameter("mezzo", mezzo );
		return query.getResultList();
	}
}
