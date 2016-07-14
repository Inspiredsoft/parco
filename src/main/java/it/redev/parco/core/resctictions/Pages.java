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

import org.jboss.seam.faces.event.PhaseIdType;
import org.jboss.seam.faces.rewrite.FacesRedirect;
import org.jboss.seam.faces.security.AccessDeniedView;
import org.jboss.seam.faces.security.LoginView;
import org.jboss.seam.faces.security.RestrictAtPhase;
import org.jboss.seam.faces.view.config.ViewConfig;
import org.jboss.seam.faces.view.config.ViewPattern;


@ViewConfig
public interface Pages 
{
	static enum AllPages 
	{	
		@FacesRedirect
        @ViewPattern("/public/*")
        @LoginView("/public/login.xhtml")
        @AccessDeniedView("/public/login.xhtml")
        PUBLIC,
        
		
        @ViewPattern("/private/*")
        @LoginView("/public/login.xhtml")
        @AccessDeniedView("/public/accessdeny.xhtml")
        @Private
        PRIVATE,
		
		//------------------------------------------------------------
        // SYSTEM
        //------------------------------------------------------------
		        
        @ViewPattern("/private/user/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@UserManage
        USERGROUP,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/group/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@GroupManage
        GROUP,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/permission/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@PermissionManage
        PERMISSION,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/job/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@JobManage
        JOB,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/conf/config.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@ConfManage
        CONFIG,
        
        @ViewPattern("/private/conf/info.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Info
        INFO,
        
        @ViewPattern("/private/conf/manage.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Manage
        MANAGE,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mail/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@MailManage
        MAIL,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/import/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Import
        IMPORT,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/anomalie/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Anomalie
        ANOMALIE,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/assicurazioni/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Assicurazioni
        ASSICURAZIONE,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/carte/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Carte
        CARTE,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/mezzo*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Mezzi
        MEZZI,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/tipoMezzo*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@TipiMezzo
        TIPIMEZZO,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/dotazione*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Dotazioni
        DOTAZIONI,
        
        @ViewPattern("/private/mezzi/consegnaDotazione.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Dotazioni
        CONSDOTAZIONI,
        
        @ViewPattern("/private/mezzi/riconsegnaDotazione.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Dotazioni
        RICONSDOTAZIONI,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/oc/provincia*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Dotazioni
        PROVINCE,
        
        @ViewPattern("/private/mezzi/oc/area*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Aree
        AREE,
        
        @ViewPattern("/private/mezzi/oc/postazione*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Postazioni
        POSTAZIONI,
        
        @ViewPattern("/private/mezzi/oc/persona*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Persone
        PERSONE,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/report/mezziReport.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@MezziReport
        MEZZIREPORT,
        
        @ViewPattern("/private/mezzi/report/scadenzeReport.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@ScadenzeReport
        SCADENZEREPORT,
        
        @ViewPattern("/private/mezzi/report/postazioniReport.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@PostazioniReport
        POSTAZIONIREPORT,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/rifornimenti/carburanteList.xhtml")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Carburante
        CARBURANTI,
        
        @ViewPattern("/private/mezzi/rifornimenti/rifornimento*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Rifornimento
        RIFORNIMENTI,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/scontrini/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Scontrini
        SCONTRINI,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/mezzi/soccorsi/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Soccorsi
        SOCCORSI,
        
        //-------------------------------------------------------
        
        @ViewPattern("/private/pin/*")
		@RestrictAtPhase(PhaseIdType.RESTORE_VIEW)
		@Pincard
        PINCARD,
        
    }
}
