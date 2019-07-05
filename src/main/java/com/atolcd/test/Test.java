package com.atolcd.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.atolcd.document.pdf.PdfUtil.PdfExtractPatternException;
import com.lowagie.text.DocumentException;

import org.apache.commons.net.io.Util;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.vfs.KettleVFS;

import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.XDocConverterException;
import fr.opensagres.xdocreport.core.document.DocumentKind;

public class Test {

	public static void main(String[] args) throws IOException, PdfExtractPatternException, DocumentException,
			UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			KettleFileException, XDocConverterException {

		/*
		 * PdfUtil pdfUtil = new PdfUtil(); pdfUtil.mergeFiles(new
		 * String[]{"D:/fichier_de test.pdf","D:/fichier_de_test_scan.pdf"},
		 * "d:/mergeFiles.pdf", true); pdfUtil.extractPages("D:/fichier_de test.pdf",
		 * "d:/extractPages.pdf", "2,5 -3,7,1");
		 * pdfUtil.attachFiles("D:/fichier_de test.pdf","D:/attachFiles.pdf",new
		 * String[]{"D:/modele_charte_antenniste_new2.pdf","D:/139_code.png"});
		 * //pdfUtil.addSignature("D:/fichier_de test.pdf", "D:/addSignature.pdf",
		 * "D:/certificat.pfx","remocraKeyStorePassword"); //139_code.png
		 */

		// InputStream textInputStream = KettleVFS.getInputStream("D:/out__dsd.odt");
		InputStream textInputStream = KettleVFS.getInputStream("D:/modele_charte_antenniste_new2.docx");

		// Options options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);
		Options options = Options.getFrom(DocumentKind.DOCX).to(ConverterTypeTo.PDF);

		IConverter converter = ConverterRegistry.getRegistry().getConverter(options);
		ByteArrayOutputStream pdfTmpOutputStream = new ByteArrayOutputStream();
		converter.convert(textInputStream, pdfTmpOutputStream, options);

		// Ecriture des métadonnées si nécessaire
		ByteArrayInputStream pdfTmpInputStream = new ByteArrayInputStream(pdfTmpOutputStream.toByteArray());
		OutputStream pdfOutputStream = KettleVFS.getOutputStream("D:/out__dsd.pdf", false);
		Util.copyStream(pdfTmpInputStream, pdfOutputStream);

		// Conversion de base en PDF
		/*
		 * OutputStream pdfTmpOutputStream =
		 * KettleVFS.getOutputStream("D:/out__dsnnnnnnd.pdf", false); IConverter
		 * converter = ConverterRegistry.getRegistry().getConverter(options);
		 * converter.convert(textInputFileInputStream, pdfTmpOutputStream, options);
		 * textInputFileInputStream.close(); pdfTmpOutputStream.close();
		 */

	}

}
