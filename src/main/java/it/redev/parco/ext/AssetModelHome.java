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

package it.redev.parco.ext;

import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.MovimentoAsset;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.service.OcService;
import it.inspired.utils.DateUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class AssetModelHome<T extends Asset> extends StatusableModelHome<T>
{
	@Inject
	OcService oc;
	
	private MovimentoAsset movimento;
	private MovimentoAsset selectedMovimento;
	private Date minDataMovimento;
	
	//--------------------------------------------------------------------------
	
	public void initMovimento()
	{
		movimento = new MovimentoAsset();
		
		MovimentoAsset last = getInstance().getLastMovimentoAsset();
		if ( last != null )
		{
			minDataMovimento = DateUtils.addDay( last.getData(), 1 );
		}

		movimento.setTipo( MovimentoAsset.Tipo.CONSEGNA );
		if ( getInstance().isConsegnato() )
		{
			movimento.setTipo( MovimentoAsset.Tipo.RICONSEGNA );
			if ( last != null )
			{
				movimento.setAssegnatario( last.getAssegnatario() );
			}
		}
		if ( minDataMovimento == null )
		{
			movimento.setData( new Date() );
		}
		else
		{
			movimento.setData( DateUtils.max( new Date(), minDataMovimento ) );
		}
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isLastMovimento( MovimentoAsset movimento )
	{
		return movimento.equals( getInstance().getLastMovimentoAsset() );
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected void initInstance() {
		super.initInstance();
		if ( isManaged() )
		{
			initMovimento();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public MovimentoAsset getMovimento() {
		return movimento;
	}

	public void setMovimento(MovimentoAsset movimento) {
		this.movimento = movimento;
	}

	//--------------------------------------------------------------------------
	
	public Date getMinDataMovimento() {
		return minDataMovimento;
	}

	public void setMinDataMovimento(Date minDataMovimento) {
		this.minDataMovimento = minDataMovimento;
	}
	
	//--------------------------------------------------------------------------

	public MovimentoAsset getSelectedMovimento() {
		return selectedMovimento;
	}

	public void setSelectedMovimento(MovimentoAsset removedMovimento) {
		this.selectedMovimento = removedMovimento;
	}
	
	//--------------------------------------------------------------------------
	
	public List<Persona> autocompletePersona( String filter )
	{
		List<Persona> persone = oc.findNotRemovedPersonaLike( filter ); 
		return  persone;
	}
	
	//--------------------------------------------------------------------------
	
	public String riconsegna()
	{
		return consegna();
	}

	//-----------------------------------------------------------------------------
	
	public String consegna()
	{
		if ( movimento.getAssegnatario() != null )
		{
			movimento.setAsset( getInstance() );
			getInstance().getMovimenti().add( movimento );
			initMovimento();
		}
		return "saved";
	}

	//--------------------------------------------------------------------------
	
	public void rimuoviMovimento()
	{
		if ( selectedMovimento != null )
		{
			getInstance().getMovimenti().remove( selectedMovimento );
			if ( getEntityManager().contains( selectedMovimento ) )
			{
				getEntityManager().remove( selectedMovimento );
			}
			initMovimento();
		}
	} 
}
