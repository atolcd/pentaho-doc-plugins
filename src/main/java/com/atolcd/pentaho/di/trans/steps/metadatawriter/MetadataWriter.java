package com.atolcd.pentaho.di.trans.steps.metadatawriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
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

public class MetadataWriter extends BaseStep implements StepInterface {

	private MetadataWriterData data;
	private MetadataWriterMeta meta;

	public MetadataWriter(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		meta = (MetadataWriterMeta) smi;
		data = (MetadataWriterData) sdi;
		return super.init(smi, sdi);

	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (MetadataWriterMeta) smi;
		data = (MetadataWriterData) sdi;
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

		meta = (MetadataWriterMeta) smi;
		data = (MetadataWriterData) sdi;

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
			data.inputFileFieldPosition = getInputRowMeta().indexOfValue(meta.getInputFileFieldName());
			data.outputFileFieldPosition = getInputRowMeta().indexOfValue(meta.getOutputFileFieldName());

			logBasic("Initialized successfully");

		}

		try {

			// Create new metadata from values
			Metadata outputMetadata = new Metadata();
			HashMap<String, String> metadataKeysAndFields = meta.getMetadata();
			Iterator<String> metadataKeysAndFieldsIt = metadataKeysAndFields.keySet().iterator();

			while (metadataKeysAndFieldsIt.hasNext()) {

				String key = metadataKeysAndFieldsIt.next();
				Integer valuePosition = getInputRowMeta().indexOfValue(metadataKeysAndFields.get(key));

				if (key.equalsIgnoreCase("title")) {
					outputMetadata.setTitle(data.outputRowMeta.getString(r, valuePosition));

				} else if (key.equalsIgnoreCase("subject")) {
					outputMetadata.setSubject(data.outputRowMeta.getString(r, valuePosition));

				} else if (key.equalsIgnoreCase("comment")) {
					outputMetadata.setComment(data.outputRowMeta.getString(r, valuePosition));

				} else if (key.equalsIgnoreCase("author")) {
					outputMetadata.setAuthor(data.outputRowMeta.getString(r, valuePosition));

				} else if (key.equalsIgnoreCase("creator")) {
					outputMetadata.setCreator(data.outputRowMeta.getString(r, valuePosition));

				} else if (key.equalsIgnoreCase("keywords")) {

					String keywords[] = data.outputRowMeta.getString(r, valuePosition).split(",");

					for (int i = 0; i < keywords.length; i++) {
						outputMetadata.addKeyword(keywords[i].trim());
					}

				} else {

					int pdiType = getInputRowMeta().getValueMeta(valuePosition).getType();

					if (pdiType == ValueMetaInterface.TYPE_STRING) {
						outputMetadata.addCustomProperty(key, data.outputRowMeta.getString(r, valuePosition));

					} else if (pdiType == ValueMetaInterface.TYPE_INTEGER) {
						outputMetadata.addCustomProperty(key, data.outputRowMeta.getInteger(r, valuePosition));

					} else if (pdiType == ValueMetaInterface.TYPE_NUMBER) {
						outputMetadata.addCustomProperty(key, data.outputRowMeta.getNumber(r, valuePosition));

					} else if (pdiType == ValueMetaInterface.TYPE_BIGNUMBER) {
						outputMetadata.addCustomProperty(key,
								data.outputRowMeta.getBigNumber(r, valuePosition).doubleValue());

					} else if (pdiType == ValueMetaInterface.TYPE_DATE) {
						outputMetadata.addCustomProperty(key, data.outputRowMeta.getDate(r, valuePosition));

					} else if (pdiType == ValueMetaInterface.TYPE_BOOLEAN) {
						outputMetadata.addCustomProperty(key, data.outputRowMeta.getBoolean(r, valuePosition));

					} else {
						// Not implemented
					}

				}
			}

			// Write metadata
			String inputFilename = data.outputRowMeta.getString(r, data.inputFileFieldPosition);
			String outputFilename = data.outputRowMeta.getString(r, data.outputFileFieldPosition);
			InputStream inputStream = KettleVFS.getInputStream(inputFilename);
			OutputStream outputStream = KettleVFS.getOutputStream(outputFilename, false);

			if (inputFilename.toLowerCase().endsWith(".docx")) {
				new DocxMetadataManager().write(outputMetadata, inputStream, outputStream, true,
						meta.isCleanAllCustomMetadata());
			} else if (inputFilename.toLowerCase().endsWith(".odt")) {
				new OdtMetadataManager().write(outputMetadata, inputStream, outputStream, true,
						meta.isCleanAllCustomMetadata());

			} else if (inputFilename.toLowerCase().endsWith(".pdf")) {
				new PdfMetadataManager().write(outputMetadata, inputStream, outputStream, true,
						meta.isCleanAllCustomMetadata());

			} else {
				// Nothing
			}

			inputStream.close();
			outputStream.close();

		} catch (Exception e) {
			throw new KettleException(e);
		}

		// Put row
		putRow(data.outputRowMeta, r);

		if (checkFeedback(getLinesRead())) {
			logBasic("Linenr " + getLinesRead());
		}

		return true;
	}
}
