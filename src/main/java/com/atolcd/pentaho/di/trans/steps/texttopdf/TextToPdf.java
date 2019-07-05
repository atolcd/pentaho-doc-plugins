package com.atolcd.pentaho.di.trans.steps.texttopdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.io.Util;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.atolcd.document.metadata.DocxMetadataManager;
import com.atolcd.document.metadata.Metadata;
import com.atolcd.document.metadata.OdtMetadataManager;
import com.atolcd.document.metadata.PdfMetadataManager;

import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.XDocConverterException;
import fr.opensagres.xdocreport.core.document.DocumentKind;

public class TextToPdf extends BaseStep implements StepInterface {

	private TextToPdfData data;
	private TextToPdfMeta meta;

	public TextToPdf(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		meta = (TextToPdfMeta) smi;
		data = (TextToPdfData) sdi;
		return super.init(smi, sdi);

	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (TextToPdfMeta) smi;
		data = (TextToPdfData) sdi;
		super.dispose(smi, sdi);
	}

	public void run() {

		logBasic("Starting to run...");
		try {
			while (processRow(meta, data) && !isStopped())
				;
		} catch (Exception e) {

			logError("Unexpected error : " + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		} finally {
			dispose(meta, data);
			logBasic("Finished, processing " + getLinesRead() + " rows");
			markStop();
		}

	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		meta = (TextToPdfMeta) smi;
		data = (TextToPdfData) sdi;

		Object[] r = getRow();

		if (r == null) {

			setOutputDone();
			return false;

		}

		if (first) {

			first = false;
			data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, null, repository, metaStore);

			// Fields positions
			data.textInputFileFieldPosition = getInputRowMeta().indexOfValue(meta.getTextInputFileFieldName());
			data.pdfOutputFileFieldPosition = getInputRowMeta().indexOfValue(meta.getPdfOutputFileFieldName());

			logBasic("Initialized successfully");

		}

		// Conversion en PDF
		try {

			String textInputFilename = data.outputRowMeta.getString(r, data.textInputFileFieldPosition);
			String pdfOutputFilename = data.outputRowMeta.getString(r, data.pdfOutputFileFieldPosition);

			if (!KettleVFS.fileExists(textInputFilename)) {
				throw new KettleException(textInputFilename + " not found");
			}

			InputStream textInputStream = KettleVFS.getInputStream(textInputFilename);

			// Récupération des métadonnées et des options de pdf en fonction du format
			// odt/docx
			Metadata metadata = null;
			Options options = null;

			if (textInputFilename.toLowerCase().endsWith(".docx")) {

				if (meta.isCopyMetadata()) {
					metadata = new DocxMetadataManager().parse(textInputStream);
				}

				options = Options.getFrom(DocumentKind.DOCX).to(ConverterTypeTo.PDF);

			} else if (data.outputRowMeta.getString(r, data.textInputFileFieldPosition).toLowerCase().endsWith(".odt")) {

				if (meta.isCopyMetadata()) {
					metadata = new OdtMetadataManager().parse(textInputStream);
				}

				options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);

			} else {
				throw new KettleException("Only ODT and DOCX files are supported");
			}

			// Conversion de base
			IConverter converter = ConverterRegistry.getRegistry().getConverter(options);
			ByteArrayOutputStream pdfTmpOutputStream = new ByteArrayOutputStream();
			converter.convert(textInputStream, pdfTmpOutputStream, options);

			// Ecriture des métadonnées si nécessaire
			ByteArrayInputStream pdfTmpInputStream = new ByteArrayInputStream(pdfTmpOutputStream.toByteArray());
			OutputStream pdfOutputStream = KettleVFS.getOutputStream(pdfOutputFilename, false);
			if (meta.isCopyMetadata()) {
				new PdfMetadataManager().write(metadata, pdfTmpInputStream, pdfOutputStream, true, true);
			} else {
				Util.copyStream(pdfTmpInputStream, pdfOutputStream);
			}

			textInputStream.close();
			pdfTmpInputStream.close();
			pdfOutputStream.close();

		} catch (XDocConverterException e) {
			throw new KettleException(e);
		} catch (IOException e) {
			throw new KettleException(e);
		} catch (Exception e) {
			throw new KettleException(e);
		}

		// Transmission de la ligne
		putRow(data.outputRowMeta, r);

		if (checkFeedback(getLinesRead())) {
			logBasic("Linenr " + getLinesRead());
		}

		return true;

	}

}
