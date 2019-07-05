package com.atolcd.pentaho.di.trans.steps.textreport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import freemarker.ext.dom.NodeModel;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;

public class TextReport extends BaseStep implements StepInterface {

	private TextReportData data;
	private TextReportMeta meta;

	public TextReport(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		meta = (TextReportMeta) smi;
		data = (TextReportData) sdi;
		return super.init(smi, sdi);

	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (TextReportMeta) smi;
		data = (TextReportData) sdi;
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

		meta = (TextReportMeta) smi;
		data = (TextReportData) sdi;

		Object[] r = getRow();

		if (r == null) {

			setOutputDone();
			return false;

		}

		if (first) {

			first = false;
			data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, null, repository, metaStore);

			// Selected engine
			data.engine = meta.getTextEngineFieldName();

			// Fields positions
			data.textTplFieldPosition = getInputRowMeta().indexOfValue(meta.getTextTplFieldName());
			data.xmlDataFieldPosition = getInputRowMeta().indexOfValue(meta.getXmlDataFieldName());
			data.textOutputFieldPosition = getInputRowMeta().indexOfValue(meta.getTextOutputFieldName());

			logBasic("Initialized successfully");

		}

		try {

			// Open Template
			InputStream textTplInputStream = KettleVFS
					.getInputStream(data.outputRowMeta.getString(r, data.textTplFieldPosition));

			// Load data XML
			InputSource xmlSource = null;
			if (meta.isXmlDataIsFile()) {
				xmlSource = new InputSource(
						KettleVFS.getInputStream(data.outputRowMeta.getString(r, data.xmlDataFieldPosition)));
			} else {
				xmlSource = new InputSource(new StringReader(data.outputRowMeta.getString(r, data.xmlDataFieldPosition)));
			}

			NodeModel nodeModel = NodeModel.parse(xmlSource);

			// Output file stream
			String outputFile = data.outputRowMeta.getString(r, data.textOutputFieldPosition);
			FileOutputStream textFileOutputStream = new FileOutputStream(outputFile);

			// Create result file with the proper engine
			if (data.engine.equals("JODReport")) {
				// Get and init template
				DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
				DocumentTemplate odtTemplate = null;

				odtTemplate = documentTemplateFactory.getTemplate(textTplInputStream);

				// Fill template with data and output file
				odtTemplate.createDocument(nodeModel, textFileOutputStream);
			} else if (data.engine.equals("xDocReport")) {
				IXDocReport textReport = XDocReportRegistry.getRegistry().loadReport(textTplInputStream,
						TemplateEngineKind.Freemarker);

				// Merge template + data
				IContext context = textReport.createContext();
				context.put(nodeModel.getNode().getFirstChild().getNodeName(), nodeModel.getChildNodes());
				textReport.process(context, textFileOutputStream);
				textFileOutputStream.close();
			} else {

			}
		} catch (FileNotFoundException e) {
			throw new KettleException(e);
		} catch (IOException e) {
			throw new KettleException(e);
		} catch (XDocReportException e) {
			throw new KettleException(e);
		} catch (SAXException e) {
			throw new KettleException(e);
		} catch (ParserConfigurationException e) {
			throw new KettleException(e);
		} catch (Exception e) {
			throw new KettleException("", e);
		}

		// Transmit row
		putRow(data.outputRowMeta, r);

		if (checkFeedback(getLinesRead())) {
			logBasic("Linenr " + getLinesRead());
		}

		return true;

	}

}
