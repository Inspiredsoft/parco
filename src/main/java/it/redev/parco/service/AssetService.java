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

import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.GenereAsset;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class AssetService extends BaseService {
	
	@SuppressWarnings("unchecked")
	public List<GenereAsset> findGeneriDotazioneMezzo() {
			Query query = em.createQuery(
					"FROM GenereAsset ga " +
					"WHERE ga.tipo = :tipo")
					.setParameter("tipo", Asset.Tipo.MEZZO);
			return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<GenereAsset> findGeneriDotazionePostazione() {
			Query query = em.createQuery(
					"FROM GenereAsset ga " +
					"WHERE ga.tipo = :tipo")
					.setParameter("tipo", Asset.Tipo.POSTAZIONE);
			return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GenereAsset> findGeneriDotazione(Integer tipo) 
	{
		Query query = em.createQuery(
				"FROM GenereAsset ga " +
				"WHERE ga.tipo = :tipo")
				.setParameter("tipo", tipo);
		return query.getResultList();
	}

}
