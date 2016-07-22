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
import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.service.RifornimentoService;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
@Named
@ConversationScoped
public class AnomaliaHome extends ModelHome<Anomalia> 
{
	@Inject
	Logger log;
	
	private List<Anomalia> collegate;
	
	@Inject
	RifornimentoService rs;
	
	public List<Anomalia> getAnomalieCollegate()
	{
		if ( collegate == null )
		{
			collegate = rs.findAnomalieCollegate( getInstance() );
		}
		return collegate;
	}
	
	@Override
	public void clearInstance() 
	{
		super.clearInstance();
		collegate = null;
	}
}
