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

package it.redev.parco.core;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named
@ApplicationScoped
public class InfoManager implements Serializable
{
	
	private final String version = "1.9"; 
	
	private String hostAddress;
	private String hostName;
	
	@PostConstruct
	public void init()
	{
		try 
		{
			hostAddress	= InetAddress.getLocalHost().getHostAddress();
			hostName	= InetAddress.getLocalHost().getHostName();
		} 
		catch (UnknownHostException e) 
		{
			hostAddress	= "Host address unknow";
			hostName	= "Host name unknow";
		}
	}
	
	//------------------------------------------------------------------
	
	public String getVersion()
	{
		return version;
	}
	
	public String getJavaVersion() 
	{
		return System.getProperty("java.version");
	}
	
	//------------------------------------------------------------------
	
	public String getHostAddress() {
		return hostAddress;
	}
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	//------------------------------------------------------------------
	
	public List<NetworkInterface> getInterfaces() 	
	{ 
		List<NetworkInterface> nics = new ArrayList<NetworkInterface>();
		try 
		{
			Enumeration<NetworkInterface> iter = NetworkInterface.getNetworkInterfaces();
			
			while ( iter.hasMoreElements() )
			{
				NetworkInterface nic =  iter.nextElement();
				if ( nic.getInterfaceAddresses().size() >0 )
				{
					nics.add( nic );
				}
			}
			
		}
		catch (SocketException e1) 
		{
			e1.printStackTrace();
		}
		return nics;
	}
}
