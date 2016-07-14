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
import it.redev.parco.model.asset.PinCard;

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
public class PinCartaList extends ExcelExportableModelEntityQuery<PinCard> {

	@Inject
	LoggedUser loggedUser;
	
	private String 	codiceOperatore;
	private String 	codiceBusta;
	private Integer gestore;
	private boolean	showRemoved = false;

	public String getCodiceOperatore() {
		return codiceOperatore;
	}

	public void setCodiceOperatore(String codiceOperatore) {
		this.codiceOperatore = codiceOperatore;
	}
	
	public String getCodiceBusta() {
		return codiceBusta;
	}

	public void setCodiceBusta(String codiceBusta) {
		this.codiceBusta = codiceBusta;
	}
	
	private static final String[] RESTRICTIONS = 
	{
		"lower(pin.codiceOperatore) like concat('%', lower(#{pinCartaList.codiceOperatore}),'%')",
		"lower(pin.codiceBusta) like concat('%', lower(#{pinCartaList.codiceBusta}),'%')",
		"pin.gestore = #{pinCartaList.gestore}",
	};
	
	//--------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "Export_PinCard_" + DateUtils.format( new Date(), "dd-MM-yyyy" ) + ".xls";
	}

	//--------------------------------------------------------------------------------

	public boolean isShowRemoved() 
	{
		return showRemoved;
	}

	public void setShowRemoved(boolean showRemoved)
	{
		this.showRemoved = showRemoved;
	}
	
	//----------------------------------------------------------------------------------

	public Integer getGestore() {
		return gestore;
	}

	public void setGestore(Integer gestore) {
		this.gestore = gestore;
	}

	//----------------------------------------------------------------------------------
	
	public List<Integer> getGestori()
	{
		return Arrays.asList( Gestore.list );
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
		String query = "FROM PinCard pin";
		
		if ( !loggedUser.hasViewAll() )
		{	
			query += " WHERE pin.id = -1";
		}
		else
		{
			query += " WHERE ( #{pinCartaList.showRemoved} = true OR pin.removed = false )";
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
