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

import it.redev.parco.model.User;
import it.redev.parco.model.UserContact;
import it.redev.parco.model.core.MailParameter;

import java.util.HashMap;
import java.util.List;

public class SimpleMail extends AbstractMail
{
	private Integer userId;
	private String subject;
	private String text;
	
	//-----------------------------------------------------------
	
	public SimpleMail(){}
	
	public SimpleMail( Integer userId, String subject, String text )
	{
		this.userId   = userId;
		this.subject = subject;
		this.text	 = text;
	}
	
	//-----------------------------------------------------------
	
	@Override
	protected void init() 
	{
		setSubject( subject );
		setTextContent( text );
		addRecipient( new UserContact( getEntityManager().find( User.class, userId ) ) );
	}
	
	//-----------------------------------------------------------
	
	@Override
	protected List<MailParameter> getState() 
	{
		List<MailParameter> params = super.getState();
		
		params.add( new MailParameter( "userId", 	"User id", this.userId ) );
		params.add( new MailParameter( "subject", 	"Subject", this.subject ) );
		params.add( new MailParameter( "text", 		"Text", this.text ) );
		
		return params;
	}
	
	@Override
	protected void setState(HashMap<String, String> params) 
	{
		super.setState( params );
		this.userId   = Integer.parseInt( params.get( "userId" ) );
		this.subject = params.get( "subject" );
		this.text 	 = params.get( "text" );
	}
}
