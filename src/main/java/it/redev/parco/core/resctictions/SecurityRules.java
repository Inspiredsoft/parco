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

package it.redev.parco.core.resctictions;

import it.redev.parco.core.resctictions.annotation.*;
import it.redev.parco.core.security.LoggedUser;

import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;
import org.jboss.solder.logging.Logger;

public class SecurityRules
{
	@Inject
	Logger log;
	
    public @Secures @Private boolean authenticatedCheck(Identity identity)
    {
    	boolean perm = 	identity != null && 
    					identity.getUser() != null && 
    					identity.isLoggedIn();
    	
    	log.debugv( "Private permission for user {0} is {1}", identity.getUser(), perm );
    	
    	return perm;
    }

    
    //----------------------------------------------------------------------------------------------------
    // SYSTEM
    //----------------------------------------------------------------------------------------------------
    
    public @Secures @UserManage boolean amanageUser(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.USERGROUP.USER");
    	
    	log.debugv( "UserManage permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @GroupManage boolean manageGroup(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.USERGROUP.GROUP");
    	
    	log.debugv( "GroupManage permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @PermissionManage boolean managePermission(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.USERGROUP.PERMISSION");
    	
    	log.debugv( "PermissionManage permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @JobManage boolean manageJob(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.JOB") || loggedUser.hasPerm("MODEL.JOB.LIST");
    	
    	log.debugv( "JobManage permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @MailManage boolean mail(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.MAIL");
    	
    	log.debugv( "Mail permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @ConfManage boolean manageConf(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.CONFIG");
    	
    	log.debugv( "ConfManager permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Info boolean info(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.INFO");
    	
    	log.debugv( "Info permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Import boolean importFile(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.SYSTEM.IMPORT");
    	
    	log.debugv( "Import permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Anomalie boolean anomalie(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.RIFORNIMENTI.ANOMALIE");
    	
    	log.debugv( "Anomalie permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Assicurazioni boolean assicurazioni(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.MEZZI.ASSICURAZIONE");
    	
    	log.debugv( "Assicurazioni permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Carte boolean carte(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.RIFORNIMENTI.CARTE");
    	
    	log.debugv( "Carte permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Mezzi boolean mezzi(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.MEZZI.MEZZO");
    	
    	log.debugv( "Mezzi permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @TipiMezzo boolean tipiMezzo(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.MEZZI.TIPOMEZZO");
    	
    	log.debugv( "TipiMezzo permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Dotazioni boolean dotazioni(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("ACTION.MEZZO.DOTAZIONE.EDIT") ||
    					loggedUser.hasPerm("MODEL.MEZZO.DOTAZIONI.VIEW");
    	
    	log.debugv( "Dotazioni permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Province boolean province(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.PROVINCE");
    	
    	log.debugv( "Province permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Aree boolean aree(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.AREE");
    	
    	log.debugv( "Aree permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Postazioni boolean postazioni(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.POSTAZIONI");
    	
    	log.debugv( "Postazioni permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Persone boolean persone(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.PERSONE");
    	
    	log.debugv( "Persone permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @ScadenzeReport boolean scadenze(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.SCADENZE");
    	
    	log.debugv( "Scadenze report permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @MezziReport boolean mezzirep(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.MEZZI");
    	
    	log.debugv( "Mezzi report permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @PostazioniReport boolean posrep(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.ANAGRAFICA.POSTAZIONI");
    	
    	log.debugv( "Postazioni report permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Carburante boolean carburanti(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.RIFORNIMENTI.CARBURANTI");
    	
    	log.debugv( "Carburanti permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures @Rifornimento boolean rifornimenti(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.RIFORNIMENTI.RIFORNIMENTI");
    	
    	log.debugv( "Rifornimenti permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures  @Scontrini boolean scontrini(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.RIFORNIMENTI.SCONTRINI");
    	
    	log.debugv( "Scontrini permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures  @Pincard boolean pincard(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.RIFORNIMENTI.PIN");
    	
    	log.debugv( "Pincard permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
    public @Secures  @Soccorsi boolean soccorsi(LoggedUser loggedUser)
    {
    	boolean perm = loggedUser.hasPerm("MENU.MEZZI.SOCCORSI");
    	
    	log.debugv( "Soccorsi permission for user {0} is {1}", loggedUser.getUser(), perm );
    	
    	return perm;
    }
    
   
    
    
    
    
    
    
    
    
}
