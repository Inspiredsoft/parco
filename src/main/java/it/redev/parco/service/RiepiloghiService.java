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

import it.redev.parco.model.report.RiepilogoAnomalia;
import it.redev.parco.model.report.RiepilogoRifornimento;
import it.redev.parco.session.MonthPeriod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

@Named
@Stateless
public class RiepiloghiService extends BaseService {

	public RiepilogoRifornimento findRiepilogoRifornimento( MonthPeriod period ) {
		
		Query query = em.createQuery( 
				"SELECT rif " +
				"FROM " + RiepilogoRifornimento.class.getSimpleName() + " rif " +
				"WHERE rif.mese = :mese " +
				"AND   rif.anno = :anno" );
		
		query.setParameter( "mese", period.getMonth() );
		query.setParameter( "anno", period.getYear() );
		
		return (RiepilogoRifornimento) super.getSingleResult(query);
	}
	
	/**
	 * Carica/crea i riepiloghi rifornimento a partire dal mese indicato fino al numero di mesi richiesti
	 * @param start Periodo di partenza
	 * @param number Numero di mesi richiesti
	 * @return Lista di riepiloghi
	 */
	public List<RiepilogoRifornimento> findRiepilogoRifornimento( MonthPeriod start, int number) {
		
		List<RiepilogoRifornimento> list = new ArrayList<RiepilogoRifornimento>();
		for ( int i = 0; i < number; i++ ) {
			RiepilogoRifornimento riepilogo = findRiepilogoRifornimento(start);
			if ( riepilogo != null ) {
				list.add( riepilogo  );
			} else {
				riepilogo = new RiepilogoRifornimento();
				riepilogo.setAnno( start.getYear() );
				riepilogo.setMese( start.getMonth() );
				riepilogo.setImporto( BigDecimal.ZERO );
				riepilogo.setNumero( 0 );
				riepilogo.setQuantita( BigDecimal.ZERO );
				list.add( riepilogo );
			}
			start.next();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<RiepilogoAnomalia> findRiepilogoAnomalia( MonthPeriod period ) {
		Query query = em.createQuery( 
				"SELECT ra " +
				"FROM " + RiepilogoAnomalia.class.getSimpleName() + " ra " +
				"WHERE ra.mese = :mese " +
				"AND   ra.anno = :anno" );
		
		query.setParameter( "mese", period.getMonth() );
		query.setParameter( "anno", period.getYear() );
		
		return query.getResultList();
	}
	
	public Long findTotaleRiepilogoAnomalie( MonthPeriod period ) {
		Query query = em.createQuery( 
				"SELECT SUM(ra.numero) " +
				"FROM " + RiepilogoAnomalia.class.getSimpleName() + " ra " +
				"WHERE ra.mese = :mese " +
				"AND   ra.anno = :anno" );
		
		query.setParameter( "mese", period.getMonth() );
		query.setParameter( "anno", period.getYear() );
		
		return (Long) super.getSingleResult(query);
	}
	
	/**
	 * Carica/crea i riepiloghi anomalie a partire dal mese indicato fino al numero di mesi richiesti
	 * @param start Periodo di partenza
	 * @param number Numero di mesi richiesti
	 * @return Lista di riepiloghi
	 */
	public List<RiepilogoAnomalia> findTotaleRiepilogoAnomalia( MonthPeriod start, int number) {
		
		List<RiepilogoAnomalia> list = new ArrayList<RiepilogoAnomalia>();
		for ( int i = 0; i < number; i++ ) {
			
			Long totale = findTotaleRiepilogoAnomalie(start);
			
			RiepilogoAnomalia riepilogo = new RiepilogoAnomalia();
			riepilogo.setAnno( start.getYear() );
			riepilogo.setMese( start.getMonth() );
			list.add( riepilogo );
			
			list.add( riepilogo  );

			if ( totale != null ) {
				riepilogo.setNumero( totale.intValue() );
			} else {
				riepilogo.setNumero( 0 );
			}
			
			start.next();
		}
		return list;
	}
}
