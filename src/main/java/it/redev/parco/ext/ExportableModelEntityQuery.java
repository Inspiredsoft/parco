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

import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.ExportType;
import it.inspired.exporter.annotation.Unexportable;
import it.redev.parco.core.MessagesManager;
import it.redev.parco.model.Identifiable;
import it.inspired.utils.BeanUtils;
import it.inspired.utils.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.solder.logging.Logger;

@SuppressWarnings("serial")
public class ExportableModelEntityQuery<T> extends LazyModelEntityQuery<T> 
{
	@Inject
	Logger log;
	
	@Inject
	MessagesManager message;
	
	private int first 	= 0;
	private int pageSize= 10;
	private Workbook workbook = null;
	
	private String dateFormat = "dd/MM/yyyy HH:mm";
	
	private List<Header> headers = new ArrayList<Header>();
	List<Object> exported = new ArrayList<Object>();
	
	//--------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	private class Header
	{
		public Class clazz;
		public List<String> properties = new ArrayList<String>();
		
		public Header( Class clazz )
		{
			this.clazz = clazz;
		}
		
		public boolean isFor( Class clazz )
		{
			return this.clazz.equals( clazz );
		}
	}
	
	//--------------------------------------------------------------------------------------
	
	private List<T> loadNext() {
		List<T> nextList = getDataModel().load( first, pageSize, null, null, null);
		first += pageSize;
		return nextList;
	}
	
	//--------------------------------------------------------------------------------------
	
	private boolean isExportable( PropertyDescriptor property )
	{
		return !getExcludedProperties().contains( property.getName() );
	}
	
	//--------------------------------------------------------------------------------------
	
	private boolean isExportable( Method method )
	{
		ExpoElement anno = method.getAnnotation( ExpoElement.class );
		if ( anno != null )
		{
			return !anno.value().equals( ExportType.IGNORE );
		}
		return true;
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Check if has Unexportable annotation
	 * @param obj
	 * @return
	 */
	private boolean isUnexportable(Object obj) 
	{
		if ( obj != null )
		{
			Unexportable anno = obj.getClass().getAnnotation( Unexportable.class );
			return ( anno != null );
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------
	
	private boolean isUnexportable(Method method) 
	{
		Unexportable anno = method.getAnnotation( Unexportable.class );
		return ( anno != null );
	}
	
	
	//--------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	private boolean isPrimitive( PropertyDescriptor property )
	{
		Class type = property.getPropertyType();
		return type.isPrimitive() || isPrimitive( type );
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isPrimitive( Class type )
	{
		return ( type == Integer.class || type == Double.class ||  type == String.class || 
  				 type == Date.class || type == Boolean.class || type == BigDecimal.class ||
  				 type == Timestamp.class);
	}
	
	//--------------------------------------------------------------------------------------
	
	private ExportType getExportType( Object obj )
	{
		ExpoElement anno = obj.getClass().getAnnotation( ExpoElement.class );
		return ( anno != null ? anno.value() : null );
	}
	
	private ExportType getExportType( Method method )
	{
		ExpoElement anno = method.getAnnotation( ExpoElement.class );
		return ( anno != null ? anno.value() : null );
	}
	
	//--------------------------------------------------------------------------------------
	
	private List<ExpoProperty> getExportProperty( Object obj )
	{
		ExpoElement e = obj.getClass().getAnnotation( ExpoElement.class );
		if ( e != null )
		{
			return Arrays.asList( e.property() );
		}
		
		ExpoProperty ep = obj.getClass().getAnnotation( ExpoProperty.class );
		if ( ep != null )
		{
			Arrays.asList( ep );
		}
		return null;
	}
	
	private List<ExpoProperty> getExportProperty( Method method )
	{
		ExpoElement e = method.getAnnotation( ExpoElement.class );
		if ( e != null )
		{
			return Arrays.asList( e.property() );
		}
		
		ExpoProperty ep = method.getAnnotation( ExpoProperty.class );
		if ( ep != null )
		{
			return Arrays.asList( ep );
		}
 		return null;
	}
	
	//--------------------------------------------------------------------------------------
	
	private List<String> getProperty( Object obj )
	{
		List<String> props = new ArrayList<String>();
		List<ExpoProperty> eps = getExportProperty( obj );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( !StringUtils.isEmpty( ep.value() ) )
				{
					props.add( ep.value()  );
				}
			}
		}
		return ( props.isEmpty() ? null : props );
	}
	
	private List<String> getProperty( Method method )
	{
		List<String> props = new ArrayList<String>();
		List<ExpoProperty> eps = getExportProperty( method );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( !StringUtils.isEmpty( ep.value() ) )
				{
					props.add( ep.value()  );
				}
			}
		}
		return  ( props.isEmpty() ? null : props );
	}
	
	//--------------------------------------------------------------------------------------
	
	private String getPrefixKey( Object obj, String property )
	{
		List<ExpoProperty> eps = getExportProperty( obj );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( StringUtils.isEmpty( ep.value() ) || ep.value().equals( property ) )
				{
					return ( StringUtils.isEmpty( ep.prefixKey() ) ? null : ep.prefixKey() );
				}
			}
		}
		return null;
	}
	
	private String getPrefixKey( Method method, String property )
	{
		List<ExpoProperty> eps = getExportProperty( method );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( StringUtils.isEmpty( ep.value() ) || property.equals( ep.value() ) )
				{
					return ( StringUtils.isEmpty( ep.prefixKey() ) ? null : ep.prefixKey() );
				}
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------------------------
	
	private void addHeader( Integer col, BeanInfo info,  String property )
	{
		Header header = null;
		for ( Header head : headers )
		{
			if ( head.isFor( info.getBeanDescriptor().getBeanClass() ) )
			{
				header = head;
				break;
			}
		}
		if ( header == null )
		{
			header = new Header( info.getBeanDescriptor().getBeanClass() );
			headers.add( header );
		}
		// Cerco la proprieta', se non c'e' la aggiungo altrimenti esco
		for ( String pd : header.properties )
		{
			if ( pd.equals( property ) )
			{
				return;
			}
		}
		header.properties.add( property );
	}
	//--------------------------------------------------------------------------------------
	
	private void exportHeaders( Sheet sheet )
	{
		Row row0 = sheet.createRow( 0 );
		Row row1 = sheet.createRow( 1 );
		
		int coll = 0;
		for ( Header  header: headers )
		{
			Cell cell0 = row0.createCell( coll );
			cell0.setCellValue( StringUtils.capitalizeMethodName( header.clazz.getSimpleName() ) );
			
			CellRangeAddress region = new CellRangeAddress(0, 0, coll, coll + header.properties.size() - 1 );
			sheet.addMergedRegion( region );
		
			CellStyle style = workbook.createCellStyle();
			style.setAlignment( CellStyle.ALIGN_CENTER );
			cell0.setCellStyle( style );
			
			for ( String pd : header.properties )
			{
				Cell cell1 = row1.createCell( coll );
				cell1.setCellValue( StringUtils.capitalizeMethodName( pd ) );
				coll++;
			}
		}
	}
	
	//--------------------------------------------------------------------------------------
	
	private void setCell( Cell cell, Object obj )
	{
		if ( obj == null )
		{
			cell.setCellValue("");
		}
		else if ( obj instanceof Date )
		{
			cell.setCellValue( (Date) obj );
			CellStyle style = workbook.createCellStyle();
			CreationHelper helper = workbook.getCreationHelper();
			style.setDataFormat( helper.createDataFormat().getFormat( dateFormat ) );
			cell.setCellStyle( style );
		}
		else if ( obj instanceof Boolean )
		{
			cell.setCellValue( (Boolean) obj );
		}
		else if ( obj instanceof Integer )
		{
			cell.setCellValue( Double.parseDouble( obj.toString() ) );
			DataFormat format = workbook.createDataFormat();
			CellStyle style = workbook.createCellStyle();
			style.setDataFormat( format.getFormat("0") );
			cell.setCellStyle( style );
		}
		else if ( obj instanceof Double )
		{
			cell.setCellValue( Double.parseDouble( obj.toString() ) );
			DataFormat format = workbook.createDataFormat();
			CellStyle style = workbook.createCellStyle();
			style.setDataFormat( format.getFormat("0.00") );
			cell.setCellStyle( style );
		}
		else if ( obj instanceof BigDecimal )
		{
			cell.setCellValue( Double.parseDouble( obj.toString() ) );
			DataFormat format = workbook.createDataFormat();
			CellStyle style = workbook.createCellStyle();
			style.setDataFormat( format.getFormat("#,##0.0000") );
			cell.setCellStyle( style );
		}
		else
		{
			CreationHelper helper = workbook.getCreationHelper();
			cell.setCellValue( helper.createRichTextString( obj.toString() ) );
		}
	}
	
	//--------------------------------------------------------------------------------------
	
	 private static Object deproxy(Object proxy) 
	 {
		 if ( proxy instanceof HibernateProxy ) 
		 {
			 return ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer()
					 .getImplementation();
		 }
		 else 
		 {
			 return proxy;
		 }
	 }
	
	//--------------------------------------------------------------------------------------

	private int export( Row row, int startColl, Object obj ) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		obj = deproxy( obj );
		if ( obj == null || isUnexportable( obj ) )
		{
			//log.debug( "Object " + obj.getClass().getSimpleName() + " is NULL or Unxportable annotation" );
			return startColl;
		}
		
		ExportType oet = getExportType( obj );
		if ( oet == null || oet.equals( ExportType.IGNORE ) )
		{
			//log.debug( "Object " + obj.getClass().getName() + " has no Exportable annotation or ExportType is IGNORE" );
			return startColl;
		}
		
		BeanInfo info = null;
		if ( oet.equals( ExportType.SUPERCLASS ) )
		{
			//log.debug( "Object " + obj.getClass().getSimpleName() + " has ExportType.SUPERCLASS annotation" );
			if ( obj.getClass().getSuperclass() == null )
			{
				return startColl;
			}
			info = Introspector.getBeanInfo( obj.getClass().getSuperclass() );
		}
		else // anno.value().equals( ExportType.CLASS ) )
		{
			info = Introspector.getBeanInfo( obj.getClass() );
		}
		
		log.debug( "Exporting object " + obj.getClass().getName() );
		
		exported.add( obj );
		
		List<String> oprops = getProperty( obj );
		
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		
		List<Object> expQueue = new ArrayList<Object>();
		
		/*
		 * Se oggetto con id questo e' posto all'inizio
		 */
		if( obj instanceof Identifiable )
		{
			addHeader( startColl, info, "id" );
			Cell cell = row.createCell( startColl );
			setCell( cell, ((Identifiable) obj).getId() );
			startColl++;
		}
		
		
		for (PropertyDescriptor pd : pds)
		{  
			// Se l'annotazione della classe indica la propieta' da esportare 
			// i ciclo salta fino a quando non viene trovata
			if ( pd.getName().equals("id") || ( oprops != null && !oprops.contains( pd.getName() ) ) )
			{
				continue;
			}
			
			Method getter = pd.getReadMethod();
			
			if ( isUnexportable( getter ) )
			{
				// Proprieta' non esportabile
				log.debug( "Property " + pd.getName() + " has Unxportable annotation" );
				continue;
			}
			
            if ( isExportable( pd ) && isExportable( getter ) )
            {
            	if ( isPrimitive( pd ) )
            	{
            		log.debug( "Exporting property " + pd.getName() + " of type " + pd.getPropertyType().getSimpleName() );
	     
	            	addHeader( startColl, info, pd.getName() );
	            	
	            	String prefix = getPrefixKey( obj, pd.getName() );	
	            	if ( prefix == null )
	            	{
	            		prefix = getPrefixKey( getter, pd.getName() );
	            	}
	            	
	            	Object ovalue = getter.invoke(obj);
	            	
        			Cell cell = row.createCell( startColl );
        			if ( prefix == null || ovalue == null )
        			{
        				setCell( cell, ovalue );
        			}
        			else
        			{
        				setCell( cell, message.getMessage( prefix + ovalue ) );
        			}
        			startColl++;
        		}	     
            	else
            	{
            		// Oggetto non primitivo, verifico se e' indicata la proprieta' da esportare
            		List<String> mprops = getProperty( getter );
            		if ( mprops == null )
            		{
            			// Se la propieta' non e' indicata si esporta tutto l'oggetto come entita' a se
            			Object value = getter.invoke(obj);
            			expQueue.add( value );
            		}
            		else
            		{
            			// Viene restituita la sola proprieta' indicata
            			Object value = getter.invoke(obj);
            			for ( String mprop : mprops )
            			{
	            			Object pvalue = BeanUtils.getProperty( value, mprop );
	            			if ( pvalue == null || isPrimitive( pvalue.getClass() ) )
	                    	{    				
	            				log.debug( "Exporting property " + mprop + " of type " + info.getBeanDescriptor().getBeanClass().getSimpleName() );
	            			     
	        	            	addHeader( startColl, info, mprop );
	        	            	
	        	            	String prefix = getPrefixKey( getter, mprop );
	                			Cell cell = row.createCell( startColl );
	                			if ( prefix == null )
	                			{
	                				setCell( cell, pvalue );
	                			}
	                			else
	                			{
	                				setCell( cell, message.getMessage( prefix + pvalue ) );
	                			}
	                			startColl++;
	                    	}
	            			else
	            			{
	            				expQueue.add( pvalue );
	            			}
            			}
            		}
            	}
        	}
        	else
        	{
        		log.debug( "Exluded property " + pd.getName() + " of type " + pd.getPropertyType().getSimpleName() );
        	}
		}
		if ( !expQueue.isEmpty() )
		{
			for ( Object value : expQueue )
			{
    			if ( !exported.contains( value ) )
    			{
    				startColl = export( row, startColl, value );
    			}
			}
		}
		return startColl;
	}
	
	//--------------------------------------------------------------------------------------
	
	private Workbook export() throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		first 	= 0;
		pageSize= 10;
		workbook = new HSSFWorkbook(); //XSSFWorkbook(); //HSSFWorkbook();
		Sheet sheet = workbook.createSheet( "export" );
		List<T> list = loadNext();
		int nrow = 1; // 2 righe sono lasciate per l'header
		if ( list != null && !list.isEmpty() )
		{
			while ( list != null && !list.isEmpty() )
			{
				for ( T obj : list )
				{
					nrow++;
					Row row = sheet.createRow( nrow );
					exported.clear();
					export( row, 0, obj );
				}
				list = loadNext();
			}
			exportHeaders( sheet );
		}
		return workbook;
	}
	
	//--------------------------------------------------------------------------------------
	// Protected methods 
	//--------------------------------------------------------------------------------------
	
	protected String getFileName() {
		return "export.xls";
	}
	
	protected List<String> getExcludedProperties() {
		return Arrays.asList( new String[]{"removed","version","removalDate"} );
	}
	
	//--------------------------------------------------------------------------------------
	
	public void download()
	{
		refresh();
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType("application/vnd.ms-excel"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + getFileName() + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

	    // Now you can write the InputStream of the file to the above OutputStream the usual way.
	    // ...
	    try 
	    {
	    	Workbook xls = export();
			OutputStream output = ec.getResponseOutputStream();
			xls.write( output );
		} 
	    catch (IOException e) {
			e.printStackTrace();
		}
	   
	    catch (IllegalArgumentException e) {
			log.error( StringUtils.getStackTrace( e ) );
		} catch (IntrospectionException e) {
			log.error( StringUtils.getStackTrace( e ) );
		} catch (IllegalAccessException e) {
			log.error( StringUtils.getStackTrace( e ) );
		} catch (InvocationTargetException e) {
			log.error( StringUtils.getStackTrace( e ) );
		}

	    fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
	}

}
