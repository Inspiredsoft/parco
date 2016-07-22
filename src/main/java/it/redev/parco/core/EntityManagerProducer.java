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

package it.redev.parco.core;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.solder.core.ExtensionManaged;

@SuppressWarnings("serial")
@ConversationScoped
public class EntityManagerProducer implements Serializable
{
     public static final String UNIT_NAME = "parcoPu";
 
     @PersistenceUnit(unitName = UNIT_NAME)
     private EntityManagerFactory entityManagerFactory;     
 
     @ExtensionManaged
     @Produces
     @ConversationScoped
     public EntityManager produce() 
     {
          EntityManager entityManager = entityManagerFactory.createEntityManager();
          // .. do somthing with entity manager
          return entityManager;
 
     }
     public void dispose(@Disposes EntityManager entityManager) 
     {
          if ( entityManager.isOpen() )
          {
               entityManager.close();
          }
     }
}
