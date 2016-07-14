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

package it.redev.parco.mail;

import java.io.Serializable;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.Session;

import org.jboss.seam.mail.core.EmailMessage;
import org.jboss.seam.mail.core.MailTransporter;
import org.jboss.seam.mail.util.MailUtility;
import org.jboss.solder.core.ExtensionManaged;

@SuppressWarnings("serial")
public class Trasporter implements Serializable, MailTransporter
{

	@Inject
	@ExtensionManaged
	private Instance<Session> session;
	
	@Override
	public EmailMessage send(EmailMessage msg) 
	{
		Session ses = session.get();
		ses.getProperties().put("mail.smtp.connectiontimeout", 15000);
	    ses.getProperties().put("mail.smtp.timeout", 15000);
		
	    MailUtility.send(msg, ses);
	    
	    return msg;
	}

}
