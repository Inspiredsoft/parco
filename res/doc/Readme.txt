-------------------------------------
----- CREAZIONE DATA SOURCE AS7 -----
-------------------------------------

- 	Instlallare MySql connector come modulo su AS7 creando sotto JBoss 
	la gerarchia di cartelle "modules\com\mysql\jdbc\main" 
	e copiando il jar ed il file module.xml
	
	OPPURE
	
- 	Instlallare Microsoft JDBC Driver 4.0 for SQL Server come modulo su AS7 
	creando sotto JBoss la gerarchia di cartelle "modules\com\microsoft\sqlserver\jdbc\main" 
	e copiando il jar ed il file module.xml
	
	QUINDI	
	
- 	Creare il datasource sulla console di JBoss o modificare il file standalone.xml 
	nella cartella "standalone\configuration" inserendo l'xml in datasource.xml 
	dentro la dichiarazione dei datasources e quello in driver.xml nella
	dichiaraziione dei drivers. 
	
-----------------------------------
---- COMPILAZIONE APPLICAZIONE ----
-----------------------------------

Lanciare da Forge il comando: 
1. mvn install compile deploy jboss-as:deploy
2. mvn install compile deploy jboss-as:redeploy

-- JBoss AS7 --
L: root
P: retis2012