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

package it.redev.parco.session.mezzi;

import it.redev.parco.core.ConfigManager;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.model.asset.CartaCarburante;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.asset.MovimentoAsset;
import it.redev.parco.model.asset.PinCard;
import it.redev.parco.model.oc.Postazione;
import it.inspired.utils.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

import org.jboss.seam.reports.Report;
import org.jboss.seam.reports.ReportCompiler;
import org.jboss.seam.reports.ReportDefinition;
import org.jboss.seam.reports.ReportRenderer;
import org.jboss.seam.reports.jasper.annotations.Jasper;
import org.jboss.seam.reports.output.PDF;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.resourceLoader.ResourceProvider;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class StampaConsegnaDotazione implements Serializable
{
	@Inject
	FacesContext context;
	
	@Inject
	@Jasper
	private transient ReportCompiler compiler;

	@Inject
	@PDF
	@Jasper
	private transient ReportRenderer pdfRenderer;
	
	@Inject
	private ResourceProvider resourceProvider;

	@Inject
	Logger log;
	
	@Inject
	ConfigManager cm;
	
	@Inject
	MessagesManager mm;
	
	
	private MovimentoAsset selectedMovimento;
	
	//--------------------------------------------------------------------------

		public MovimentoAsset getSelectedMovimento() {
			return selectedMovimento;
		}

		public void setSelectedMovimento(MovimentoAsset removedMovimento) {
			this.selectedMovimento = removedMovimento;
		}
	
	//--------------------------------------------------------------------------
	
	public void stampa() throws IOException
	{
		if ( selectedMovimento != null )
		{
			stampa ( selectedMovimento );
		}
	}
	
	public void stampa(  MovimentoAsset selectedMovimento ) throws IOException
	{
		if ( selectedMovimento != null )
		{
			ExternalContext externalContext = context.getExternalContext();
			externalContext.setResponseContentType("application/pdf");

			OutputStream _out = externalContext.getResponseOutputStream();

			InputStream sourceReport = resourceProvider.loadResourceStream("consegna.jrxml");

			JRDataSource jasperDataSource = new JREmptyDataSource();

			// source
			ReportDefinition report = compiler.compile(sourceReport);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put( "parco_direttore", cm.getResponsabile() );
			
			Postazione postazione = null;
			if ( selectedMovimento.getAsset().isFuelCard() )
			{
				CartaCarburante carta = (CartaCarburante) selectedMovimento.getAsset();
				params.put( "parco_tipo", mm.getMessage( "asset.tipo.1" ) + " " + mm.getMessage( "gestore." + carta.getGestore() ) );
				params.put( "parco_matricola", carta.getNumero() );
				postazione = ( carta.getMezzo().getAssegnazione() != null ? carta.getMezzo().getAssegnazione().getPostazione() : null ); 
			}
			else if ( selectedMovimento.getAsset().isPinCard() )
			{
				PinCard pin = (PinCard) selectedMovimento.getAsset();
				params.put( "parco_tipo", mm.getMessage( "asset.tipo.3" ) + " " + mm.getMessage( "gestore." + pin.getGestore() ) );
				params.put( "parco_matricola", pin.getCodiceBusta() );
				postazione = null; 
			}
			else
			{
				DotazioneMezzo dotazione = (DotazioneMezzo) selectedMovimento.getAsset();
				params.put( "parco_tipo", dotazione.getGenere().getDescrizione() );
				params.put( "parco_matricola", dotazione.getMatricola() );
				if ( selectedMovimento.getAsset().isDotazioneMezzo() )
				{
					postazione = ( dotazione.getMezzo().getAssegnazione() != null ? dotazione.getMezzo().getAssegnazione().getPostazione() : null ); 
				}
				else
				{
					postazione = dotazione.getPostazione();
				}
			}	
			
			if ( postazione != null )
			{
				params.put( "parco_postazione", postazione.getNome() );
				params.put( "parco_area", postazione.getArea().getNome() );
				params.put( "parco_provincia", postazione.getArea().getProvincia().getSigla() );
				params.put( "parco_centrocosto", postazione.getCentroCosto() );
			}
			params.put( "parco_operazione", ( selectedMovimento.isConsegnata() ? "CONSEGNA a" : "RITIRA da" ) ); 
			params.put( "parco_dataconsegna", DateUtils.format( selectedMovimento.getData(), cm.getShortDateFormat() ) );
			params.put( "parco_assegnatario", selectedMovimento.getAssegnatario().getNomeCompleto() );
			
			params.put( "parco_datacorrente", DateUtils.format( new Date(), cm.getShortDateFormat() ) );
			params.put( "parco_ricevente", (selectedMovimento.getConsegnatario()!=null ? selectedMovimento.getConsegnatario().getNomeCompleto() : selectedMovimento.getAssegnatario().getNomeCompleto() ) );
			params.put( "parco_note", selectedMovimento.getNote() );
			
			Report reportInstance = report.fill(jasperDataSource, params);

			// ByteArrayOutputStream os = new ByteArrayOutputStream(_out); //
			// OutputStream
			// Render output as the desired content
			pdfRenderer.render(reportInstance, _out);
			//_out.flush();
			externalContext.responseFlushBuffer();

			context.responseComplete();
		}
	}
}
