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

package it.redev.parco.ext;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Initialized;

public class BeanStartupHelper
{
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void onStartup(@Observes @Initialized WebApplication webApplication, BeanManager beanManager)
    {

    	for( Bean bean : beanManager.getBeans(Startup.class) )
    	{
    		CreationalContext context = beanManager.createCreationalContext(bean);
    		Startup st = (Startup) beanManager.getReference(bean, Startup.class, context);
    		st.init();
    	}
    }
}
