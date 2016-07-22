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

package it.redev.parco.utils;

import java.util.List;

import it.redev.parco.model.rifornimenti.Anomalia;
import it.redev.parco.model.rifornimenti.Rifornimento;

public class AnomaliaUtils 
{	
	public static Anomalia nuovaAnomalia( Rifornimento rif, Integer tipo, Integer stato )
	{
		Anomalia ano = new Anomalia();
		ano.setRifornimento( rif );
		ano.setTipo( tipo );
		ano.setStato( stato );
		return ano;
	}
	
	//------------------------------------------------------------------------------------------
	
	public static Anomalia nuovaAnomalia( Rifornimento rif, Rifornimento pred, Integer tipo, Integer stato )
	{
		Anomalia ano = new Anomalia();
		ano.setRifornimento( rif );
		ano.setRifornimentoPrecedente( pred );
		ano.setTipo( tipo );
		ano.setStato( stato );
		return ano;
	}
	
	//------------------------------------------------------------------------------------------
	
	public static void cambiaStato( List<Anomalia> anomalie, Integer stato )
	{
		for ( Anomalia ano : anomalie )
		{
			ano.setStato( stato );
		}
	}
}
