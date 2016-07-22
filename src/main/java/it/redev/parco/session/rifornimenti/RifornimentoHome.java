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

package it.redev.parco.session.rifornimenti;

import it.redev.parco.ext.ModelHome;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.session.Viewed;
import it.redev.parco.session.report.ConsumiReport;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class RifornimentoHome extends ModelHome<Rifornimento> 
{
	@Inject
	Logger log;
	
	//----------------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	AnomaliaList getAnomalieRifornimento( AnomaliaList anomaliaList )
	{
		anomaliaList.setRifornimento( getInstance() );
		log.debugv( "Produced anomaliaList for rifornimento id {0} with {1} rows", getId(), anomaliaList.getRowCount() );
		return anomaliaList;
	}
	
	//--------------------------------------------------------------------------
	
	@Produces
	@Named
	@Viewed
	@RequestScoped
	ConsumiReport getConsumiRifornimento( ConsumiReport consumiReport )
	{
		consumiReport.setMezzo( getInstance().getMezzo() );
		consumiReport.orderByDate();
		consumiReport.setPeriod( null );
		log.debugv( "Produced consumiRifornimento for mezzo id {0} with {1} rows", getId(), consumiReport.getRowCount() );
		return consumiReport;
	}
}
