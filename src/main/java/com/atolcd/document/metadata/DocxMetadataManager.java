package com.atolcd.document.metadata;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.atolcd.document.metadata.CustomProperty.PropertyType;

import org.apache.poi.ooxml.POIXMLProperties.CoreProperties;
import org.apache.poi.ooxml.POIXMLProperties.CustomProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;

public class DocxMetadataManager implements MetadataManager {

	public Metadata parse(InputStream inputStream) throws Exception {

		Metadata metadata = new Metadata();

		XWPFDocument document = new XWPFDocument(inputStream);
		XWPFWordExtractor word = new XWPFWordExtractor(document);

		// Core properties
		CoreProperties coreProperties = word.getCoreProperties();
		if (coreProperties.getTitle() != null) {
			metadata.setTitle(coreProperties.getTitle());

		}

		if (coreProperties.getSubject() != null) {
			metadata.setSubject(coreProperties.getSubject());

		}

		if (coreProperties.getDescription() != null) {
			metadata.setComment(coreProperties.getDescription());

		}

		if (coreProperties.getCreator() != null) {
			metadata.setAuthor(coreProperties.getCreator());

		}
		if (coreProperties.getKeywords() != null) {

			for (String keyword : coreProperties.getKeywords().split(",")) {
				metadata.addKeyword(keyword.trim());
			}

		}

		// Custom properties
		for (CTProperty ctProperty : word.getCustomProperties().getUnderlyingProperties().getPropertyList()) {

			String key = ctProperty.getName();

			// Lpwstr
			if (ctProperty.isSetLpwstr()) {
				metadata.addCustomProperty(key, ctProperty.getLpwstr());

				// Lpstr
			} else if (ctProperty.isSetLpstr()) {
				metadata.addCustomProperty(key, ctProperty.getLpstr());

			} else if (ctProperty.isSetDate()) {
				metadata.addCustomProperty(key, ctProperty.getDate().getTime());

			} else if (ctProperty.isSetFiletime()) {
				metadata.addCustomProperty(key, ctProperty.getFiletime().getTime());

				// Bool
			} else if (ctProperty.isSetBool()) {
				metadata.addCustomProperty(key, ctProperty.getBool());

				// Integers
			} else if (ctProperty.isSetI1()) {
				metadata.addCustomProperty(key, ctProperty.getI1());

			} else if (ctProperty.isSetI2()) {
				metadata.addCustomProperty(key, ctProperty.getI2());

			} else if (ctProperty.isSetI4()) {
				metadata.addCustomProperty(key, ctProperty.getI4());

			} else if (ctProperty.isSetI8()) {
				metadata.addCustomProperty(key, ctProperty.getI8());

			} else if (ctProperty.isSetInt()) {
				metadata.addCustomProperty(key, ctProperty.getInt());

				// Unsigned Integer
			} else if (ctProperty.isSetUi1()) {
				metadata.addCustomProperty(key, ctProperty.getUi1());

			} else if (ctProperty.isSetUi2()) {
				metadata.addCustomProperty(key, ctProperty.getUi2());

			} else if (ctProperty.isSetUi4()) {
				metadata.addCustomProperty(key, ctProperty.getUi4());

			} else if (ctProperty.isSetUi8()) {
				metadata.addCustomProperty(key, ctProperty.getUi8().longValue());

			} else if (ctProperty.isSetUint()) {
				metadata.addCustomProperty(key, ctProperty.getUint());

				// Reals
			} else if (ctProperty.isSetR4()) {
				metadata.addCustomProperty(key, ctProperty.getR4());

			} else if (ctProperty.isSetR8()) {
				metadata.addCustomProperty(key, ctProperty.getR8());

			} else if (ctProperty.isSetDecimal()) {
				metadata.addCustomProperty(key, ctProperty.getDecimal().doubleValue());
			}

		}

		word.close();

		return metadata;
	}

	public void write(Metadata metadata, InputStream inputStream, OutputStream outputStream,
			boolean eraseCorePropertyWithNull, boolean cleanCustomProperties) throws Exception {

		XWPFDocument document = new XWPFDocument(inputStream);
		XWPFWordExtractor word = new XWPFWordExtractor(document);

		// Core properties

		CoreProperties coreProperties = word.getCoreProperties();

		/*
		 * coreProperties.setCategory(null); coreProperties.setContentStatus(null);
		 * coreProperties.setContentType(null); coreProperties.setCreated((String)null);
		 * coreProperties.setCreator(null); coreProperties.setDescription(null);
		 * coreProperties.setIdentifier(null); coreProperties.setKeywords(null);
		 * coreProperties.setLastPrinted((String)null);
		 * coreProperties.setModified((String)null); coreProperties.setRevision(null);
		 * coreProperties.setSubjectProperty(null); coreProperties.setTitle(null);
		 */

		// coreProperties.se

		/*
		 * ExtendedProperties extProperties = word.getExtendedProperties();
		 * extProperties.getUnderlyingProperties().setCompany("Ma compagnie");
		 * extProperties.getUnderlyingProperties().setManager("Manager");
		 * extProperties.getUnderlyingProperties().set
		 */

		if (metadata.getTitle() != null || eraseCorePropertyWithNull) {
			coreProperties.setTitle(metadata.getTitle());
		}

		if (metadata.getSubject() != null || eraseCorePropertyWithNull) {
			coreProperties.setSubjectProperty(metadata.getSubject());
		}

		if (metadata.getComment() != null || eraseCorePropertyWithNull) {
			coreProperties.setDescription(metadata.getComment());
		}

		if (metadata.getAuthor() != null || eraseCorePropertyWithNull) {
			coreProperties.setCreator(metadata.getAuthor());
		}

		// coreProperties.setMo

		if ((metadata.getKeywords() != null && !metadata.getKeywords().isEmpty()) || eraseCorePropertyWithNull) {

			StringBuffer stringBuffer = new StringBuffer();
			Iterator<String> stringIt = metadata.getKeywords().iterator();
			while (stringIt.hasNext()) {
				stringBuffer.append(stringIt.next());
				if (stringIt.hasNext()) {
					stringBuffer.append(",");
				}

			}

			coreProperties.setKeywords(stringBuffer.toString());
		}

		// Custom properties
		CustomProperties docxCustomProperties = word.getCustomProperties();

		// Custom properties - clean
		if (cleanCustomProperties) {
			docxCustomProperties.getUnderlyingProperties().getPropertyList().clear();
		}

		// Custom properties - add
		for (CustomProperty customProperty : metadata.getCustomProperties()) {

			String key = customProperty.getName();
			Object value = customProperty.getValue();

			// boolean
			if (customProperty.getType().equals(PropertyType.BOOLEAN)) {

				if (!docxCustomProperties.contains(key)) {
					docxCustomProperties.addProperty(key, (Boolean) value);
				} else {
					docxCustomProperties.getProperty(key).setBool((Boolean) value);
				}

				// date : specific
			} else if (customProperty.getType().equals(PropertyType.DATE)) {

				CTProperty newDateProperty = null;

				if (!docxCustomProperties.contains(key)) {

					newDateProperty = docxCustomProperties.getUnderlyingProperties().addNewProperty();

					// Properties count + 1 -> propid
					int propid = 1;
					for (CTProperty p : docxCustomProperties.getUnderlyingProperties().getPropertyList()) {
						if (p.getPid() > propid)
							propid = p.getPid();
					}
					propid++;

					newDateProperty.setPid(propid);
					newDateProperty.setFmtid("{D5CDD505-2E9C-101B-9397-08002B2CF9AE}");
					newDateProperty.setName(key);
				} else {

					newDateProperty = docxCustomProperties.getProperty(key);
				}

				Calendar calendar = Calendar.getInstance();
				calendar.setTime((Date) value);
				newDateProperty.setFiletime(calendar);

				// double
			} else if (customProperty.getType().equals(PropertyType.DOUBLE)) {

				if (!docxCustomProperties.contains(key)) {
					docxCustomProperties.addProperty(key, (Double) value);
				} else {
					docxCustomProperties.getProperty(key).setR8((Double) value);
				}

				// integer
			} else if (customProperty.getType().equals(PropertyType.LONG)) {

				if (!docxCustomProperties.contains(key)) {
					docxCustomProperties.addProperty(key, ((Long) value).intValue());
				} else {
					docxCustomProperties.getProperty(key).setI4(((Long) value).intValue());
				}

				// string
			} else {

				if (!docxCustomProperties.contains(key)) {
					docxCustomProperties.addProperty(key, (String) value);
				} else {
					docxCustomProperties.getProperty(key).setLpwstr((String) value);
				}

			}

		}

		word.close();
		document.write(outputStream);

	}

}
