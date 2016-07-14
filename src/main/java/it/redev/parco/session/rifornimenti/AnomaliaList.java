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

package it.redev.parco.session.rifornimenti;

import it.inspired.utils.DateUtils;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.ExcelExportableModelEntityQuery;
import it.redev.parco.model.asset.Gestore;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.service.AssegnazioneService;
import it.redev.parco.session.Period;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class AnomaliaList extends ExcelExportableModelEntityQuery<Anomalia>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	AssegnazioneService as;
	
	private String 		targa;
	private String 		carta;
	private String 		codiceBusta;
	private String 		codiceOperatore;
	private Integer		tipo = null;
	private Integer 	gestore;
	private Rifornimento rifornimento;
	private Postazione	postazione;
	private Period		period = Period.currentMonthPeriod();
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(ano.rifornimento.mezzo.targa) like concat('%', lower(#{anomaliaList.targa}),'%')",
		"ano.rifornimento = #{anomaliaList.rifornimento}",
		"ano.tipo = #{anomaliaList.tipo}",
		"ano.rifornimento.data >= #{anomaliaList.period.morningPeriodFrom}",
		"ano.rifornimento.data <= #{anomaliaList.period.midnighthPeriodTo}",
		"ano.rifornimento.gestore = #{anomaliaList.gestore}",
		//"ano.rifornimento.mezzo IN #{anomaliaList.mezziPostazione}"
		"ass.postazione = #{anomaliaList.postazione}",
		"lower(ano.rifornimento.carta.numero) like concat('%', lower(#{anomaliaList.carta}),'%')",
		"lower(ano.rifornimento.pinCard.codiceOperatore) like concat('%', lower(#{anomaliaList.codiceOperatore}),'%')",
		"lower(ano.rifornimento.pinCard.codiceBusta) like concat('%', lower(#{anomaliaList.codiceBusta}),'%')",
	};
	
	//--------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "Export_Anomalie_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}
	
	//--------------------------------------------------------------------------------
	
	public List<Integer> getTipi()
	{
		return Arrays.asList( Anomalia.Tipo.list );
	}
	
	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
	}
	
	//--------------------------------------------------------------------------------

	public String getCarta() {
		return carta;
	}

	public void setCarta(String carta) {
		this.carta = carta;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getCodiceBusta() {
		return codiceBusta;
	}

	public void setCodiceBusta(String codiceBusta) {
		this.codiceBusta = codiceBusta;
	}
	
	//--------------------------------------------------------------------------------

	public String getCodiceOperatore() {
		return codiceOperatore;
	}

	public void setCodiceOperatore(String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
	}

	//--------------------------------------------------------------------------------

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	
	public Integer getGestore() {
		return gestore;
	}

	//--------------------------------------------------------------------------------
	
	public void setGestore(Integer gestore) {
		this.gestore = gestore;
	}

	//--------------------------------------------------------------------------------

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}
	
	//--------------------------------------------------------------------------------
	
	public Postazione getPostazione() {
		return postazione;
	}
	
	public void setPostazione(Postazione postazione) {
		this.postazione = postazione;
	}
	
	public List<Mezzo> getMezziPostazione() {
		return ( postazione != null ? as.findMezzi( postazione ) : null );
	}
	
	//--------------------------------------------------------------------------------
	
	public Rifornimento getRifornimento() {
		return rifornimento;
	}

	public void setRifornimento(Rifornimento rifornimento) {
		this.rifornimento = rifornimento;
	}
	
	//--------------------------------------------------------------------------------
	
	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	//--------------------------------------------------------------------------------
	
	@Override
	public List<String> getRestrictionExpressionStrings() 
	{
		return Arrays.asList(RESTRICTIONS);
	}
	
	//--------------------------------------------------------------------------------
	
	@Override
	public String getOrderColumn() {
		return "ano.rifornimento.data";
	}
	
	@Override
	public String getOrderDirection() {
		return DIR_ASC;
	}
	
	//--------------------------------------------------------------------------------	

	@Override
	public String getEjbql() 
	{
//		String query = "FROM Anomalia ano";
//		
//		if ( !loggedUser.hasViewAll() )
//		{	
//			query = "SELECT ano " +
//					"FROM Anomalia ano, AssegnazioneMezzo ass " +
//					"WHERE ano.rifornimento.mezzo = ass.mezzo " +
//					"AND ano.rifornimento.data >= ass.dataInizio " +
//					"AND (ass.dataFine IS NULL OR ano.rifornimento.data<=ass.dataFine) ";
//			if ( loggedUser.getVisibleMezzi().isEmpty() )
//			{
//				query += " WHERE ano.id = -1";
//			}
//			else
//			{
//				query += " AND ass.postazione IN #{loggedUser.visiblePostazioni}";
//			}
//		}
		
		String query = "FROM Anomalia ano";
		if ( postazione != null || !loggedUser.hasViewAll()  ) {
			query = "SELECT ano " +
					"FROM Anomalia ano, AssegnazioneMezzo ass " +
					"WHERE ano.rifornimento.mezzo = ass.mezzo " +
					"AND ano.rifornimento.data >= ass.dataInizio " +
					"AND (ass.dataFine IS NULL OR ano.rifornimento.data<=ass.dataFine) ";
		}
		
		if ( !loggedUser.hasViewAll() )
		{
			if ( loggedUser.getVisiblePostazioni().isEmpty() )
			{
				query += " WHERE ano.id = -1";
			}
			else				
			{
				query += " AND ass.postazione IN #{loggedUser.visiblePostazioni}";
			}
		}
		
		return query;
	}
	
	@Override
	protected boolean isAnyParameterDirty() 
	{
		super.setEjbql( this.getEjbql() );
		return super.isAnyParameterDirty();
	}
}
