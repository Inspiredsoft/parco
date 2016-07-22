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

import it.redev.parco.model.User;
import it.redev.parco.model.asset.Asset;
import it.redev.parco.model.asset.DotazioneMezzo;
import it.redev.parco.model.asset.GenereAsset;
import it.redev.parco.model.mezzi.CompagniaAssicurazione;
import it.redev.parco.model.mezzi.Mezzo;
import it.redev.parco.model.mezzi.Polizza;
import it.redev.parco.model.mezzi.TipoMezzo;
import it.redev.parco.model.oc.Area;
import it.redev.parco.model.oc.Persona;
import it.redev.parco.model.oc.Postazione;
import it.redev.parco.model.oc.Provincia;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

public class ImportService
{
	private EntityManager em;
	private User user;
	
	private HashMap<String, TipoMezzo> 				tipiMezzo 	= new HashMap<String, TipoMezzo>();
	private HashMap<String, CompagniaAssicurazione> compagnie 	= new HashMap<String, CompagniaAssicurazione>();
	private HashMap<String, Polizza> 				polizze 	= new HashMap<String, Polizza>();
	private HashMap<String, Postazione>				postazioni	= new HashMap<String, Postazione>(); 
	private HashMap<String, Area>					aree		= new HashMap<String, Area>(); 
	private HashMap<String, Provincia>				province	= new HashMap<String, Provincia>(); 
	private HashMap<String, Persona>				persone		= new HashMap<String, Persona>(); 
	private HashMap<String, DotazioneMezzo>			dotazioni   = new HashMap<String, DotazioneMezzo>();
	private HashMap<String, GenereAsset>			genereDotazioneMezzo   = new HashMap<String, GenereAsset>();
	
	//---------------------------------------------------------------------------
	
	public ImportService(EntityManager em, User user)
	{
		super();
		this.em = em;
		this.user = user;
	}
	
	//---------------------------------------------------------------------------

	public HashMap<String, TipoMezzo> getTipiMezzo() {
		return tipiMezzo;
	}

	public void setTipiMezzo(HashMap<String, TipoMezzo> tipiMezzo) {
		this.tipiMezzo = tipiMezzo;
	}

	public HashMap<String, CompagniaAssicurazione> getCompagnie() {
		return compagnie;
	}

	public void setCompagnie(HashMap<String, CompagniaAssicurazione> compagnie) {
		this.compagnie = compagnie;
	}

	public HashMap<String, Polizza> getPolizze() {
		return polizze;
	}

	public void setPolizze(HashMap<String, Polizza> polizze) {
		this.polizze = polizze;
	}

	public HashMap<String, Postazione> getPostazioni() {
		return postazioni;
	}

	public void setPostazioni(HashMap<String, Postazione> postazioni) {
		this.postazioni = postazioni;
	}
	
	public HashMap<String, Area> getAree() {
		return aree;
	}
	
	public void setAree(HashMap<String, Area> aree) {
		this.aree = aree;
	}
	
	public HashMap<String, Provincia> getProvince() {
		return province;
	}
	
	public void setProvince(HashMap<String, Provincia> province) {
		this.province = province;
	}
	
	public HashMap<String, Persona> getPersone() {
		return persone;
	}
	
	public void setPersone(HashMap<String, Persona> persone) {
		this.persone = persone;
	}

	public HashMap<String, DotazioneMezzo> getDotazioni() {
		return dotazioni;
	}
	
	public void setDotazioni(HashMap<String, DotazioneMezzo> dotazioni) {
		this.dotazioni = dotazioni;
	}
	
	//---------------------------------------------------------------------------
	
	
	@SuppressWarnings("unchecked")
	protected GenereAsset findGenereDotazioneMezzo( String  descrizione ) {
		GenereAsset genere = genereDotazioneMezzo.get( descrizione );
		if ( genere == null ) {
			List<GenereAsset> lista = em.createQuery( 
					"FROM GenereAsset ga " +
					"WHERE ga.tipo = :tipo " + 
					"AND   ga.descrizione = :descrizione ")
					.setParameter( "tipo", Asset.Tipo.MEZZO )
					.setParameter( "descrizione", descrizione )
					.getResultList();
			if ( lista.size() > 0 )
			{
				genere = lista.get( 0 );
				genereDotazioneMezzo.put( descrizione, genere );
			}
		}
		return genere;
	}
	
	//---------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected DotazioneMezzo findDotazioneMezzo( String matricola, GenereAsset genere, Mezzo mezzo )
	{
		String key = mezzo.getId() + "." + matricola;
		DotazioneMezzo dotazione = dotazioni.get( key );
		if ( dotazione == null )
		{
			List<DotazioneMezzo> lista = em.createQuery( 
					"FROM DotazioneMezzo d " +
					"WHERE d.matricola = :matricola " + 
					"AND   d.mezzo = :mezzo " + 
					"AND   d.tipo = :tipo " +
					"AND   d.genere = :genere")
					.setParameter( "matricola", matricola )
					.setParameter( "mezzo", mezzo )
					.setParameter( "tipo", Asset.Tipo.MEZZO )
					.setParameter( "genere", genere )
					.getResultList();
			if ( lista.size() > 0 )
			{
				dotazione = lista.get( 0 );
			}
			else
			{
				dotazione = new DotazioneMezzo();
				dotazione.setMatricola( matricola );
				dotazione.setMezzo( mezzo );
				dotazione.setGenere( genere );
				dotazione.setTipo( Asset.Tipo.MEZZO );
				dotazione.cambiaStato( DotazioneMezzo.Stato.ATTIVO, new Date(), user, "Inserito da job di importazione" );
			}
			dotazioni.put( key, dotazione );
		}
		return dotazione;
	}
	
	//---------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	protected TipoMezzo findTipoMezzo( String descrizione )
	{
		descrizione = descrizione.toUpperCase();
		TipoMezzo tipo = tipiMezzo.get( descrizione );
		if ( tipo == null )
		{
			List<TipoMezzo> lista = em.createQuery( 
					"FROM TipoMezzo t " +
					"WHERE t.descrizione = :descrizione ")
					.setParameter( "descrizione", descrizione )
					.getResultList();
			if ( lista.size() > 0 )
			{
				tipo = lista.get( 0 );
			}
			else
			{
				tipo = new TipoMezzo();
				tipo.setDescrizione( descrizione );
			}
			tipiMezzo.put( descrizione, tipo );
		}
		return tipo;
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected CompagniaAssicurazione findCompagnia( String nome )
	{
		nome = nome.toUpperCase();
		CompagniaAssicurazione comp = compagnie.get( nome );
		if ( comp == null )
		{
			List<CompagniaAssicurazione> lista = em.createQuery( 
					"FROM CompagniaAssicurazione c " +
					"WHERE c.nome = :nome ")
					.setParameter( "nome", nome )
					.getResultList();
			if ( lista.size() > 0 )
			{
				comp = lista.get( 0 );
			}
			else
			{
				comp = new CompagniaAssicurazione();
				comp.setNome( nome );
			}
			compagnie.put( nome, comp );
		}
		return comp;
	}
	
	//-------------------------------------------------------------------------------------
	
	protected void removePolizza( Polizza polizza )
	{
		String key = polizza.getCompagnia().getNome() + ":" + polizza.getNumero();
		polizze.remove( key );
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Polizza findPolizza( String numero, CompagniaAssicurazione comp )
	{
		String key = comp.getNome() + ":" + numero;
		Polizza polizza = polizze.get( key );
		if ( polizza == null )
		{
			List<Polizza> lista = em.createQuery( 
					"FROM Polizza p " +
					"WHERE p.numero = :numero " +
					"AND   p.compagnia.nome = :compagnia ")
					.setParameter( "numero", numero )
					.setParameter( "compagnia", comp.getNome() )
					.getResultList();
			if ( lista.size() > 0 )
			{
				polizza = lista.get( 0 );
			}
			else
			{
				polizza = new Polizza();
				polizza.setNumero( numero );
				polizza.setCompagnia( comp );
			}
			polizze.put( key, polizza );
		}
		return polizza;
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Postazione findPostazione( String nome, Area area )
	{
		nome = nome.toUpperCase();
		Postazione pos = postazioni.get( nome );
		if ( pos == null )
		{
			List<Postazione> lista = em.createQuery( 
					"FROM Postazione p " +
					"WHERE p.nome = :nome " +
					"AND   p.area.nome = :area ")
					.setParameter( "nome", nome )
					.setParameter( "area", area.getNome() )
					.getResultList();
			if ( lista.size() > 0 )
			{
				pos = lista.get( 0 );
			}
			else
			{
				pos = new Postazione();
				pos.setNome( nome );
				pos.setArea( area );
			}
			postazioni.put( nome, pos );
		}
		return pos;
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Postazione findPostazione( String nome )
	{
		nome = nome.toUpperCase();
		Postazione pos = postazioni.get( nome );
		if ( pos == null )
		{
			List<Postazione> lista = em.createQuery( 
					"FROM Postazione p " +
					"WHERE p.nome = :nome ")
					.setParameter( "nome", nome )
					.getResultList();
			if ( lista.size() == 1 )
			{
				pos = lista.get( 0 );
				postazioni.put( nome, pos );
			}
		}
		return pos;
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Area findArea( String nome, Provincia provincia )
	{
		nome = nome.toUpperCase();
		Area area = aree.get( nome );
		if ( area == null )
		{
			List<Area> lista = em.createQuery( 
					"FROM Area a " +
					"WHERE a.nome = :nome " +
					"AND   a.provincia.sigla = :provincia")
					.setParameter( "nome", nome )
					.setParameter( "provincia", provincia.getSigla() )
					.getResultList();
			if ( lista.size() > 0 )
			{
				area = lista.get( 0 );
			}
			else
			{
				area = new Area();
				area.setNome( nome );
				area.setProvincia( provincia );
			}
			aree.put( nome, area );
		}
		return area;
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Provincia findProvincia( String sigla )
	{
		sigla = sigla.toUpperCase();
		Provincia prov = province.get( sigla );
		if ( prov == null )
		{
			List<Provincia> lista = em.createQuery( 
					"FROM Provincia p " +
					"WHERE p.sigla = :sigla ")
					.setParameter( "sigla", sigla )
					.getResultList();
			if ( lista.size() > 0 )
			{
				prov = lista.get( 0 );
			}
			else
			{
				prov = new Provincia();
				prov.setSigla(sigla);
			}
			province.put( sigla, prov );
		}
		return prov;
	}
	
	//-------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	protected Persona findPersona( String matricola )
	{
		matricola = matricola.toUpperCase();
		Persona persona = persone.get( matricola );
		if ( persona == null )
		{
			List<Persona> lista = em.createQuery( 
					"FROM Persona p " +
					"WHERE p.matricola = :matricola " +
					"AND   p.removed = false")
					.setParameter( "matricola", matricola )
					.getResultList();
			if ( lista.size() > 0 )
			{
				persona = lista.get( 0 );
			}
			else
			{
				persona = new Persona();
				persona.setMatricola( matricola );
			}
			persone.put( matricola, persona );
		}
		return persona;
	}
}
