package com.atolcd.document.metadata;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.meta.OdfOfficeMeta;

import com.atolcd.document.metadata.CustomProperty.PropertyType;

public class OdtMetadataManager implements MetadataManager{
	
	public enum OdtPropertyType {
		STRING,
		FLOAT,
		BOOLEAN,
		TIME,
		DATE
	}

	public Metadata parse(InputStream inputStream) throws Exception {
		
		Metadata metadata = new Metadata();
		
		OdfDocument odtDocument = OdfTextDocument.loadDocument(inputStream);
		OdfOfficeMeta odfOfficeMeta = odtDocument.getOfficeMetadata();
		
		//Core properties
		if(odfOfficeMeta.getTitle() != null){
			metadata.setTitle(odfOfficeMeta.getTitle());
			
		}
		
		if(odfOfficeMeta.getSubject() != null){
			metadata.setSubject(odfOfficeMeta.getSubject());
			
		}
		
		if(odfOfficeMeta.getDescription() != null){
			metadata.setComment(odfOfficeMeta.getDescription());
			
		}
		
		if(odfOfficeMeta.getCreator() != null){
			metadata.setAuthor(odfOfficeMeta.getCreator());
			
		}
		
		if(odfOfficeMeta.getGenerator() != null){
			metadata.setCreator(odfOfficeMeta.getGenerator());
			
		}
		if(odfOfficeMeta.getKeywords() != null){

			for(String keyword : odfOfficeMeta.getKeywords()){
				metadata.addKeyword(keyword.trim());
			}

		}
		
		//Custom properties
		if(odfOfficeMeta.getUserDefinedDataNames()!= null
				&& odfOfficeMeta.getUserDefinedDataNames().size() > 0){

			for(String key : odfOfficeMeta.getUserDefinedDataNames()){
				
				OdtPropertyType odfType = OdtPropertyType.valueOf(odfOfficeMeta.getUserDefinedDataType(key).toUpperCase());
				String value = odfOfficeMeta.getUserDefinedDataValue(key);
				
				//BOOLEAN
				if (odfType.equals(OdtPropertyType.BOOLEAN)){
					metadata.addCustomProperty(key, Boolean.parseBoolean(value));
				
				//FLOAT
				}else if (odfType.equals(OdtPropertyType.FLOAT)){
					
					Double doubleValue =   Double.parseDouble(value);
					if ((doubleValue == Math.floor(doubleValue)) && !Double.isInfinite(doubleValue)) {
						
						metadata.addCustomProperty(key, doubleValue.longValue());
					    
					}else{
						metadata.addCustomProperty(key, doubleValue);
					}
				
				//STRING
				}else if (odfType.equals(OdtPropertyType.STRING)){
					metadata.addCustomProperty(key, value);
				
				//DATE
				}else if (odfType.equals(OdtPropertyType.DATE)){
					
					try{
						metadata.addCustomProperty(key, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(value));
					
					}catch(Exception e){
						metadata.addCustomProperty(key, new SimpleDateFormat("yyyy-MM-dd").parse(value));	
					}
				
				//TIME
				}else{
					metadata.addCustomProperty(key, value);
				}
				
			}

		}

		odtDocument.close();
		
		return metadata;
		
	}

	public void write(Metadata metadata, InputStream inputStream, OutputStream outputStream, boolean eraseCorePropertyWithNull, boolean cleanCustomProperties) throws Exception{
		
		OdfDocument odtDocument = OdfTextDocument.loadDocument(inputStream);
		OdfOfficeMeta odfOfficeMeta = odtDocument.getOfficeMetadata();
		
		//Core properties
		if(metadata.getTitle() != null || eraseCorePropertyWithNull){
			odfOfficeMeta.setTitle(metadata.getTitle());

		}
		
		if(metadata.getSubject() != null || eraseCorePropertyWithNull){
			odfOfficeMeta.setSubject(metadata.getSubject());
		}
		
		if(metadata.getComment() != null || eraseCorePropertyWithNull){
			odfOfficeMeta.setDescription(metadata.getComment());
		}
		
		if(metadata.getAuthor() != null || eraseCorePropertyWithNull){
			odfOfficeMeta.setInitialCreator(metadata.getAuthor());
		}
		
		if(metadata.getCreator() != null || eraseCorePropertyWithNull){
			odfOfficeMeta.setGenerator(metadata.getCreator());
		}
		
		if((metadata.getKeywords() != null && !metadata.getKeywords().isEmpty()) || eraseCorePropertyWithNull){
			odfOfficeMeta.setKeywords(metadata.getKeywords());
		}
		
		//Custom properties - clean
		if (cleanCustomProperties && odfOfficeMeta.getUserDefinedDataNames() != null){
			
			for(String propertyName : odfOfficeMeta.getUserDefinedDataNames()){
				odfOfficeMeta.removeUserDefinedDataByName(propertyName);
			}
			
		}
		
		//Custom properties - add
		for(CustomProperty customProperty : metadata.getCustomProperties()){

			String key = customProperty.getName();
			Object value = (Object)customProperty.getValue();
			String odtType;
	
			//boolean
			if(customProperty.getType().equals(PropertyType.BOOLEAN)){
				odtType = OdtPropertyType.BOOLEAN.toString();
			
			//date
			}else if (customProperty.getType().equals(PropertyType.DATE)){
				odtType = OdtPropertyType.DATE.toString();
			
			//float
			}else if (customProperty.getType().equals(PropertyType.DOUBLE)
					|| customProperty.getType().equals(PropertyType.LONG)
					){
				odtType = OdtPropertyType.FLOAT.toString();
				
			//string
			}else{
				odtType = OdtPropertyType.STRING.toString();
					
			}
			
			//Not null value
			if(value != null){
				
				if(customProperty.getType().equals(PropertyType.DATE)){
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTime((Date) value);
					
					//Date + Time
					if(calendar.get(Calendar.HOUR) > 0
						|| calendar.get(Calendar.MINUTE) > 0
						|| calendar.get(Calendar.SECOND) > 0){
						

						if(calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000) == calendar.get(Calendar.HOUR)
								&& calendar.get(Calendar.MINUTE) == 0
								&& calendar.get(Calendar.SECOND) == 0){
							
							odfOfficeMeta.setUserDefinedData(key, odtType.toLowerCase(), new SimpleDateFormat("yyyy-MM-dd").format(value));
						
						}else{
							odfOfficeMeta.setUserDefinedData(key, odtType.toLowerCase(), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(value));
						}
						
						
					
					//Date
					}else{
						odfOfficeMeta.setUserDefinedData(key, odtType.toLowerCase(), new SimpleDateFormat("yyyy-MM-dd").format(value));
					}
				
				}else{
					odfOfficeMeta.setUserDefinedData(key, odtType.toLowerCase(), value.toString());
				}
			
			//Null value
			}else{
				
				odfOfficeMeta.setUserDefinedData(key,odtType.toLowerCase(), "");
			}

		}

		odtDocument.save(outputStream);
		odtDocument.close();

	}

}
