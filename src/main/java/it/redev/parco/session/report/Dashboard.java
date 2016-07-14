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

package it.redev.parco.session.report;

import it.inspired.utils.DateUtils;
import it.redev.parco.model.report.RiepilogoAnomalia;
import it.redev.parco.model.report.RiepilogoRifornimento;
import it.redev.parco.service.RiepiloghiService;
import it.redev.parco.session.MonthPeriod;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class Dashboard implements Serializable {  
	
	@Inject
	RiepiloghiService dashboardService;
	
	private static final int len = 12;
	private MonthPeriod start = new  MonthPeriod( DateUtils.addMonth( new Date(), -11) );

	List<RiepilogoRifornimento> riepiloghi 	= null;
	List<RiepilogoAnomalia> 	anomalie 	= null;
	
	private CartesianChartModel chartNumero = null;
	private CartesianChartModel chartImporto = null;
	private CartesianChartModel chartQuantita = null;
	private CartesianChartModel chartAnomalie = null;
	
	@PostConstruct
	public void init() {
		
		if ( riepiloghi == null ) {
			
			chartNumero = new CartesianChartModel();
			chartImporto = new CartesianChartModel();
			chartQuantita = new CartesianChartModel();
			chartAnomalie = new CartesianChartModel();
			
			riepiloghi 	= dashboardService.findRiepilogoRifornimento( start.clone(), len );
			anomalie 	= dashboardService.findTotaleRiepilogoAnomalia( start.clone(), len );
			
			ChartSeries csNum = new ChartSeries();
			ChartSeries csImp = new ChartSeries();
			ChartSeries csQua = new ChartSeries();
			ChartSeries csAno = new ChartSeries();
			
			csNum.setLabel( "Rifornimenti" );
			csImp.setLabel( "Spesa" );
			csQua.setLabel( "Litri" );
			csAno.setLabel( "Anomalie" );
			
			for ( RiepilogoRifornimento riepilogo : riepiloghi ) {
				csNum.set( riepilogo.getLabel(), riepilogo.getNumero() );
				csImp.set( riepilogo.getLabel(), riepilogo.getImporto() );
				csQua.set( riepilogo.getLabel(), riepilogo.getQuantita() );
			}
			
			for ( RiepilogoAnomalia anomalia : anomalie ) {
				csAno.set( anomalia.getLabel(), anomalia.getNumero() );
			}
			
			chartNumero.addSeries( csNum );
			chartImporto.addSeries( csImp );
			chartQuantita.addSeries( csQua );
			chartAnomalie.addSeries( csAno );
		}
	}

	public CartesianChartModel getChartNumero() {
		return chartNumero;
	}

	public CartesianChartModel getChartImporto() {
		return chartImporto;
	}

	public CartesianChartModel getChartQuantita() {
		return chartQuantita;
	}
	
	public CartesianChartModel getChartAnomalie() {
		return chartAnomalie;
	}

	public void itemSelect(ItemSelectEvent event) {
		
		FacesMessage msg = new FacesMessage();
		
		int index = event.getItemIndex();
		
		RiepilogoRifornimento riepilogo = riepiloghi.get( index );
		RiepilogoAnomalia anomalia = anomalie.get( index );
		
		msg.setSummary( "Dettagli " + riepilogo.getLabel() );
		
		StringBuilder message = new StringBuilder();
		message.append("<table>");
		message.append("<tr><th colspan=2>" + riepilogo.getLabel() +"</th><tr>");
		message.append("<tr><td>Rifornimenti</td><td>" + riepilogo.getNumero() + "</td></tr>" );
		message.append("<tr><td>Spesa</td><td>" + riepilogo.getImporto() + "</td></tr>" );
		message.append("<tr><td>Litri</td><td>" + riepilogo.getQuantita() + "</td></tr>" );
		message.append("<tr><td>Anomalie</td><td>" + anomalia.getNumero() + "</td></tr>" );
		message.append("</table>");
			
		msg.setSummary( message.toString() );
		
		
		FacesContext.getCurrentInstance().addMessage("chartmsg", msg);
		
	}
}
