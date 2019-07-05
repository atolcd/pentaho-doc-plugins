package com.atolcd.document.metadata;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

import com.atolcd.document.metadata.CustomProperty.PropertyType;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class PdfMetadataManager implements MetadataManager {

	public enum PdfPropertyKey {
		Subject, Author, Keywords, Title, Creator,
	}

	public Metadata parse(InputStream inputStream) throws Exception {

		Metadata metadata = new Metadata();

		PdfReader pdfReader = new PdfReader(inputStream);
		@SuppressWarnings("unchecked")
		HashMap<String, String> pdfMetadatas = pdfReader.getInfo();

		for (String key : pdfMetadatas.keySet()) {

			if (key.equalsIgnoreCase(PdfPropertyKey.Title.toString())) {
				metadata.setTitle(pdfMetadatas.get(PdfPropertyKey.Title.toString()));

			} else if (key.equalsIgnoreCase(PdfPropertyKey.Subject.toString())) {
				metadata.setSubject(pdfMetadatas.get(PdfPropertyKey.Subject.toString()));

			} else if (key.equalsIgnoreCase(PdfPropertyKey.Author.toString())) {
				metadata.setAuthor(pdfMetadatas.get(PdfPropertyKey.Author.toString()));

			} else if (key.equalsIgnoreCase(PdfPropertyKey.Creator.toString())) {
				metadata.setCreator(pdfMetadatas.get(PdfPropertyKey.Creator.toString()));

			} else if (key.equalsIgnoreCase(PdfPropertyKey.Keywords.toString())) {

				for (String keyword : pdfMetadatas.get(PdfPropertyKey.Keywords.toString()).split(";")) {
					metadata.addKeyword(keyword.trim());
				}

			} else if (key.equalsIgnoreCase("COMMENT")) {
				metadata.setComment(pdfMetadatas.get(key));

			} else {
				metadata.addCustomProperty(key, pdfMetadatas.get(key));
			}

		}

		pdfReader.close();

		return metadata;

	}

	public void write(Metadata metadata, InputStream inputStream, OutputStream outputStream,
			boolean eraseCorePropertyWithNull, boolean cleanCustomProperties) throws Exception {

		PdfReader pdfReader = new PdfReader(inputStream);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);

		@SuppressWarnings("unchecked")
		HashMap<String, String> pdfMetadatas = pdfReader.getInfo();

		// Core properties
		if (metadata.getTitle() != null || eraseCorePropertyWithNull) {
			pdfMetadatas.put(PdfPropertyKey.Title.toString(), metadata.getTitle());
		}

		if (metadata.getSubject() != null || eraseCorePropertyWithNull) {
			pdfMetadatas.put(PdfPropertyKey.Subject.toString(), metadata.getSubject());
		}

		if (metadata.getAuthor() != null || eraseCorePropertyWithNull) {
			pdfMetadatas.put(PdfPropertyKey.Author.toString(), metadata.getAuthor());
		}

		if (metadata.getCreator() != null || eraseCorePropertyWithNull) {
			pdfMetadatas.put(PdfPropertyKey.Creator.toString(), metadata.getCreator());
		}

		if ((metadata.getKeywords() != null && !metadata.getKeywords().isEmpty()) || eraseCorePropertyWithNull) {

			StringBuffer stringBuffer = new StringBuffer();
			Iterator<String> stringIt = metadata.getKeywords().iterator();
			while (stringIt.hasNext()) {
				stringBuffer.append(stringIt.next());
				if (stringIt.hasNext()) {
					stringBuffer.append(";");
				}

			}

			pdfMetadatas.put(PdfPropertyKey.Keywords.toString(), stringBuffer.toString());
		}

		// Custom properties - clean
		if (cleanCustomProperties) {

			HashMap<String, String> pdfCoreMetadatas = new HashMap<String, String>();

			// Remove property if not core property
			for (String key : pdfMetadatas.keySet()) {

				if (key.equalsIgnoreCase(PdfPropertyKey.Author.toString())
						|| key.equalsIgnoreCase(PdfPropertyKey.Creator.toString())
						|| key.equalsIgnoreCase(PdfPropertyKey.Keywords.toString())
						|| key.equalsIgnoreCase(PdfPropertyKey.Subject.toString())
						|| key.equalsIgnoreCase(PdfPropertyKey.Title.toString())) {

					pdfCoreMetadatas.put(key, pdfMetadatas.get(key));

				}

			}

			pdfMetadatas.clear();
			pdfMetadatas.putAll(pdfCoreMetadatas);

		}

		// Custom properties - add
		for (CustomProperty customProperty : metadata.getCustomProperties()) {

			String key = customProperty.getName();
			Object value = (Object) customProperty.getValue();

			// boolean
			if (customProperty.getType().equals(PropertyType.BOOLEAN)) {
				pdfMetadatas.put(key, value.toString());

				// date
			} else if (customProperty.getType().equals(PropertyType.DATE)) {
				pdfMetadatas.put(key, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(value));

				// float
			} else if (customProperty.getType().equals(PropertyType.DOUBLE)
					|| customProperty.getType().equals(PropertyType.LONG)) {
				pdfMetadatas.put(key, value.toString());

				// string
			} else {
				pdfMetadatas.put(key, value.toString());

			}

		}

		pdfStamper.setMoreInfo(pdfMetadatas);

		pdfStamper.close();
		pdfReader.close();

	}

}
