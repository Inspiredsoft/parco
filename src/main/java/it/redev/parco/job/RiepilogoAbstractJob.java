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

package it.redev.parco.job;

import it.redev.parco.model.core.JobParameter;
import it.redev.parco.session.MonthPeriod;

import java.util.HashMap;
import java.util.List;

public abstract class RiepilogoAbstractJob extends AbstractImportJob 
{
	private MonthPeriod period;
	
	//-------------------------------------------------------------------------------------
	
	public MonthPeriod getPeriod() {
		return period;
	}

	public void setPeriod(MonthPeriod period) {
		this.period = period;
	}
	
	//-------------------------------------------------------------------------------------

	@Override
	protected List<JobParameter> getState( ) 
	{
		List<JobParameter> params =super.getState();
		params.add( new JobParameter( "period",  "Periodo", period.toString() ) );
		return params;
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	protected void setState(HashMap<String, String> params)
	{
		super.setState( params );
		this.period = MonthPeriod.parse( params.get( "period" ) );
	}

	//-------------------------------------------------------------------------------------
	
	@Override
	public boolean isRepeatable() 
	{
		return true;
	}
		
	//-------------------------------------------------------------------------------------
	
	public int deleteRiepilogoRifMezzi() throws Exception
	{
		int del = getEntityManager().createNativeQuery( 
				"DELETE FROM RIEPILOGO_RIF_MEZZI " +
				"WHERE MESE = :mese " +
				"AND ANNO = :anno ")
			.setParameter( "anno", period.getYear() )
			.setParameter( "mese", period.getMonth() )
			.executeUpdate();
		
		if ( del > 0 )
		{
			addInfoMessage( "Rimossi " + del + " riepiloghi rifornimenti mezzo" );
		}
		return del;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int calcolaRiepilogoRifMezzi() throws Exception
	{
		
		String query = 
				"INSERT INTO RIEPILOGO_RIF_MEZZI(NUMERO, ID_MEZZO, MESE, ANNO, QUANTITA, IMPORTO) " +
				"SELECT count(rif.ID_MEZZO), rif.ID_MEZZO, MONTH(rif.DATA), YEAR(rif.DATA), SUM(rif.QUANTITA), SUM(rif.IMPORTO) " +
				"FROM RIFORNIMENTI rif " +
				"WHERE MONTH(rif.DATA) = :mese " +
				"AND YEAR(rif.DATA) = :anno " + 
				"GROUP BY rif.ID_MEZZO ";

		int rip= getEntityManager().createNativeQuery( query )
				.setParameter( "anno", period.getYear() )
				.setParameter( "mese", period.getMonth() )
				.executeUpdate();
		
		if ( rip > 0 )
		{
			addInfoMessage( "Generati " + rip + " riepiloghi rifornimenti mezzo" );
		}
		return rip;
	}
	
	//-------------------------------------------------------------------------------------
	
	public void calcolaChilometriRiepilogoRifMezzi()
	{
		
	}
	
	//-------------------------------------------------------------------------------------
	
	public int deleteRiepilogoRifMezzoPostazioni() throws Exception
	{
		int del = getEntityManager().createNativeQuery( 
				"DELETE FROM RIEPILOGO_RIF_MEZZO_POSTAZIONI " +
				"WHERE MESE = :mese " +
				"AND ANNO = :anno ")
			.setParameter( "anno", period.getYear() )
			.setParameter( "mese", period.getMonth() )
			.executeUpdate();
		
		if ( del > 0 )
		{
			addInfoMessage( "Rimossi " + del + " riepiloghi rifornimenti mezzo postazione" );
		}
		return del;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int calcolaRiepilogoRifMezzoPostazioni() throws Exception
	{
		
		String query = 
				"INSERT INTO RIEPILOGO_RIF_MEZZO_POSTAZIONI(NUMERO, ID_MEZZO, ID_POSTAZIONE, MESE, ANNO, QUANTITA, IMPORTO) " +
				"SELECT count(rif.ID_MEZZO), rif.ID_MEZZO, ass.ID_POSTAZIONE, MONTH(rif.DATA), YEAR(rif.DATA), SUM(rif.QUANTITA), SUM(rif.IMPORTO) " +
				"FROM RIFORNIMENTI rif, ASSEGNAZIONI_MEZZO ass " +
				"WHERE MONTH(rif.DATA) = :mese " +
				"AND YEAR(rif.DATA) = :anno " +
				"AND rif.ID_MEZZO = ass.ID_MEZZO " +
				"AND rif.DATA>=ass.DATA_INIZIO " +
				"AND (ass.DATA_FINE IS NULL OR rif.DATA<=ass.DATA_FINE) " +
				"GROUP BY rif.ID_MEZZO, ass.ID_POSTAZIONE";

		int rip= getEntityManager().createNativeQuery( query )
				.setParameter( "anno", period.getYear() )
				.setParameter( "mese", period.getMonth() )
				.executeUpdate();
		
		if ( rip > 0 )
		{
			addInfoMessage( "Generati " + rip + " riepiloghi rifornimenti mezzo postazione" );
		}
		return rip;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int deleteRiepilogoRifPostazioni() throws Exception
	{
		int del = getEntityManager().createNativeQuery( 
				"DELETE FROM RIEPILOGO_RIF_POSTAZIONI " +
				"WHERE MESE = :mese " +
				"AND ANNO = :anno ")
			.setParameter( "anno", period.getYear() )
			.setParameter( "mese", period.getMonth() )
			.executeUpdate();
		
		if ( del > 0 )
		{
			addInfoMessage( "Rimossi " + del + " riepiloghi rifornimenti postazione" );
		}
		return del;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int calcolaRiepilogoRifPostazioni() throws Exception
	{
		
		String query = 
				"INSERT INTO RIEPILOGO_RIF_POSTAZIONI(NUMERO, ID_POSTAZIONE, MESE, ANNO, QUANTITA, IMPORTO) " +
				"SELECT SUM(rip.NUMERO), rip.ID_POSTAZIONE, rip.MESE, rip.ANNO, SUM(rip.QUANTITA), SUM(rip.IMPORTO) " +
				"FROM RIEPILOGO_RIF_MEZZO_POSTAZIONI rip " +
				"WHERE rip.MESE = :mese " +
				"AND rip.ANNO = :anno " +
				"GROUP BY rip.ID_POSTAZIONE";

		int rip= getEntityManager().createNativeQuery( query )
				.setParameter( "anno", period.getYear() )
				.setParameter( "mese", period.getMonth() )
				.executeUpdate();
		
		if ( rip > 0 )
		{
			addInfoMessage( "Generati " + rip + " riepiloghi rifornimenti mezzo postazione" );
		}
		return rip;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int deleteRiepilogoRifornimenti() throws Exception
	{
		int del = getEntityManager().createNativeQuery( 
				"DELETE FROM RIEPILOGO_RIF " +
				"WHERE MESE = :mese " +
				"AND ANNO = :anno ")
			.setParameter( "anno", period.getYear() )
			.setParameter( "mese", period.getMonth() )
			.executeUpdate();
		
		if ( del > 0 )
		{
			addInfoMessage( "Rimossi " + del + " riepiloghi rifornimento" );
		}
		return del;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int calcolaRiepilogoRifornimenti() throws Exception {
		String query = 
				"INSERT INTO RIEPILOGO_RIF(NUMERO, MESE, ANNO, QUANTITA, IMPORTO) " +
				"SELECT count(*), MONTH(rif.DATA), YEAR(rif.DATA), SUM(rif.QUANTITA), SUM(rif.IMPORTO) " +
				"FROM RIFORNIMENTI rif " +
				"WHERE MONTH(rif.DATA) = :mese " +
				"AND YEAR(rif.DATA) = :anno ";
		
		int rip= getEntityManager().createNativeQuery( query )
				.setParameter( "anno", period.getYear() )
				.setParameter( "mese", period.getMonth() )
				.executeUpdate();
		
		if ( rip > 0 )
		{
			addInfoMessage( "Generati " + rip + " riepiloghi rifornimento" );
		}
		return rip;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int deleteRiepilogoAnomalie() throws Exception
	{
		int del = getEntityManager().createNativeQuery( 
				"DELETE FROM RIEPILOGO_ANOMALIE " +
				"WHERE MESE = :mese " +
				"AND ANNO = :anno ")
			.setParameter( "anno", period.getYear() )
			.setParameter( "mese", period.getMonth() )
			.executeUpdate();
		
		if ( del > 0 )
		{
			addInfoMessage( "Rimossi " + del + " riepiloghi anomalie" );
		}
		return del;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int calcolaRiepilogoAnomalie() throws Exception {
		
		String query = 
				"INSERT INTO RIEPILOGO_ANOMALIE(NUMERO, TIPO, MESE, ANNO) " +
				"SELECT count(*), ano.TIPO, MONTH(rif.DATA), YEAR(rif.DATA) "+
				"FROM RIFORNIMENTI rif join ANOMALIE ano on rif.ID = ano.ID_RIFORNIMENTO " +
				"WHERE MONTH(rif.DATA) = :mese " + 
				"AND YEAR(rif.DATA) = :anno " +
				"GROUP BY ano.TIPO";
	
		
		int rip= getEntityManager().createNativeQuery( query )
				.setParameter( "anno", period.getYear() )
				.setParameter( "mese", period.getMonth() )
				.executeUpdate();
		
		if ( rip > 0 )
		{
			addInfoMessage( "Generati " + rip + " riepiloghi anomalie" );
		}
		return rip;
	}
}
