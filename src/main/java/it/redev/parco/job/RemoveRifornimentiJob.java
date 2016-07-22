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

package it.redev.parco.job;

import it.redev.parco.job.exception.DatabaseException;
import it.redev.parco.job.exception.RemoteSafeException;
import it.redev.parco.model.ImportInfo;
import it.redev.parco.model.User;
import it.redev.parco.model.mezzi.Soccorso;
import it.redev.parco.model.report.Consumo;
import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.model.rifornimenti.Rifornimento;
import it.redev.parco.model.rifornimenti.Scontrino;

import java.util.List;

import javax.persistence.Query;

public class RemoveRifornimentiJob extends AbstractImportJob 
{	
	List<Integer> rifIds;
	
	public RemoveRifornimentiJob(){}
		
	public RemoveRifornimentiJob(ImportInfo info, User user) 
	{
		super.setInfo( info );
		setUser( user );
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public void execute() throws DatabaseException, RemoteSafeException 
	{
		try
		{
			int soc = clearSoccorsi();
			int ano = removeAnomalie();
			int sco = removeScontrini();
			int con = removeConsumi();
			int rif = removeRifornimenti( );
			
			addInfoMessage( "Aggiornati " + soc + " soccorsi " );
			addInfoMessage( "Rimossi " + sco + " scontrini " );
			addInfoMessage( "Rimosse " + ano + " anomalie " );
			addInfoMessage( "Rimossi " + con + " consumi " );
			addInfoMessage( "Rimossi " + rif + " rifornimenti " );
		} 
		catch (Exception e) 
		{
			throw new DatabaseException( e );
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	private List<Integer> getRifornimentiId()
	{
		if ( rifIds == null )
		{
			Query query = getEntityManager().createQuery( "SELECT rif.id FROM " + Rifornimento.class.getName() + " rif WHERE rif.importInfo = :importInfo )" )
							.setParameter( "importInfo", getInfo() );
			rifIds = query.getResultList();
		}
		return rifIds;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int clearSoccorsi()
	{
		int tot = 0;
		
		Query query = getEntityManager().createQuery( 
				"UPDATE " + Soccorso.class.getName() + " soc " +
				"SET soc.mediaChilometri = null, soc.rifInizio = null, soc.rifFine = null " +
				"WHERE soc.rifInizio.id IN ( :rifids )" )
				.setParameter( "rifids", getRifornimentiId() );
		
		tot = query.executeUpdate();
		
		query = getEntityManager().createQuery( 
				"UPDATE " + Soccorso.class.getName() + " soc " +
				"SET soc.mediaChilometri = null, soc.rifInizio = null, soc.rifFine = null " +
				"WHERE soc.rifFine.id IN ( :rifids )" )
				.setParameter( "rifids", getRifornimentiId() );
		
		tot += query.executeUpdate();
		
		return tot;
	}
	
	//-------------------------------------------------------------------------------------
	
	private int removeScontrini()
	{
		Query query = getEntityManager().createNativeQuery(		
						"DELETE st.* " +
						"FROM stati_scontrino st INNER JOIN scontrini sco ON st.id_scontrino = sco.id " +
						"WHERE sco.id_rifornimento in ( :rifids );" )
					.setParameter( "rifids", getRifornimentiId() );
		
		query.executeUpdate();
		
		query = getEntityManager().createQuery( 
				"DELETE FROM " + Scontrino.class.getName() + " sco " +
			 	"WHERE sco.rifornimento.id IN ( :rifids ) ")
				.setParameter( "rifids", getRifornimentiId() );
		
		return query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------------
	
	private int removeConsumi()
	{
		Query query = getEntityManager().createQuery( 
				"DELETE FROM " + Consumo.class.getName() + " con " +
			 	"WHERE con.rifornimento.id IN ( :rifids ) ")
				.setParameter( "rifids", getRifornimentiId() );
		
		query = getEntityManager().createQuery( 
				"DELETE FROM " + Consumo.class.getName() + " con " +
			 	"WHERE con.rifornimentoPrecedente.id IN ( :rifids ) ")
				.setParameter( "rifids", getRifornimentiId() );
		
		return query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------------
	
	private int removeAnomalie()
	{
		int tot = 0;
		Query query = getEntityManager().createQuery( 
				"DELETE FROM " + Anomalia.class.getName() + " ano " +
			 	"WHERE ano.rifornimento.id IN ( :rifids ) ")
				.setParameter( "rifids", getRifornimentiId() );
		
		tot = query.executeUpdate();
		
		query = getEntityManager().createQuery( 
				"DELETE FROM " + Anomalia.class.getName() + " ano " +
			 	"WHERE ano.rifornimentoPrecedente.id IN ( :rifids ) ")
				.setParameter( "rifids", getRifornimentiId() );
		
		tot += query.executeUpdate();
		
		return tot; 
	}
	
	//-------------------------------------------------------------------------------------
	
	private int removeRifornimenti()
	{
		Query query = getEntityManager().createQuery( 
				"DELETE FROM " + Rifornimento.class.getName() + " rif " +
				"WHERE rif.importInfo = :importInfo" )
				.setParameter("importInfo", getInfo() );
		return query.executeUpdate();
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	public boolean isRepeatable() 
	{
		return true;
	}
	
}
