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

import it.redev.parco.core.MessagesManager;
import it.redev.parco.core.security.LoggedUser;
import it.redev.parco.ext.LazyModelEntityQuery;
import it.redev.parco.job.JobManager;
import it.redev.parco.model.mezzi.CodificaCarburante;
import it.redev.parco.model.rifornimenti.TipoCarburante;
import it.redev.parco.service.RifornimentoService;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;

@SuppressWarnings("serial")
@Named
@Stateful
@ConversationScoped
public class CodificaCarburanteList extends LazyModelEntityQuery<CodificaCarburante>
{
	@Inject
	LoggedUser loggedUser;
	
	@Inject
	RifornimentoService rs;
	
	@Inject
	JobManager jobManager;
	
	@Inject
	MessagesManager message;
	
	private String 	valore;
	private Integer tipo;
	
	//--------------------------------------------------------------------------------

	private static final String[] RESTRICTIONS = 
	{
		"lower(cc.valore) like concat('%', lower(#{codificaCarburanteList.valore}),'%')",
		"cc.tipo = #{codificaCarburanteList.tipo}"
	};

	//--------------------------------------------------------------------------------
	
	public List<Integer> getTipi()
	{
		return Arrays.asList( TipoCarburante.list );
	}
	
	//--------------------------------------------------------------------------------

	public String getValore() {
		return valore;
	}

	public void setValore(String targa) {
		this.valore = targa;
	}
	
	//--------------------------------------------------------------------------------
	
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	
	//--------------------------------------------------------------------------------
	
	@Override
	public List<String> getRestrictionExpressionStrings() 
	{
		return Arrays.asList(RESTRICTIONS);
	}
	
	//--------------------------------------------------------------------------------	

	@Override
	public String getEjbql() 
	{
		String query = "FROM CodificaCarburante cc";
		return query;
	}
	
	//--------------------------------------------------------------------------
	
	public void onEdit( RowEditEvent event ) 
	{
		CodificaCarburante cc = (CodificaCarburante) event.getObject();
		rs.updateCodificaCarburante( cc );
		jobManager.enqueueAnomalieCarburanteJob( cc );
		message.info( "message.job.new.anocarb", cc.getValore() );
	}
}
