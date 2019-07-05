package com.atolcd.pentaho.di.trans.steps.textreport;

import java.util.List;
import java.util.Map;

import com.atolcd.pentaho.di.ui.trans.steps.textreport.TextReportDialog;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(id = "TextReport", image = "com/atolcd/pentaho/di/ui/trans/steps/images/TextReport.svg", name = "TextReport.Shell.Name", description = "TextReport.Shell.Description", categoryDescription = "TextReport.Shell.CategoryDescription", i18nPackageName = "com.atolcd.pentaho.di.trans.steps.textreport")
public class TextReportMeta extends BaseStepMeta implements StepMetaInterface {

	private String textEngineFieldName;
	private String textTplFieldName;
	private String xmlDataFieldName;
	private boolean xmlDataIsFile;
	private String textOutputFieldName;

	public TextReportMeta() {
		super();
	}

	public String getTextEngineFieldName() {
		return textEngineFieldName;
	}

	public void setTextEngineFieldName(String textEngineFieldName) {
		this.textEngineFieldName = textEngineFieldName;
	}

	public String getTextTplFieldName() {
		return textTplFieldName;
	}

	public void setTextTplFieldName(String textTplFieldName) {
		this.textTplFieldName = textTplFieldName;
	}

	public String getXmlDataFieldName() {
		return xmlDataFieldName;
	}

	public void setXmlDataFieldName(String xmlDataFieldName) {
		this.xmlDataFieldName = xmlDataFieldName;
	}

	public boolean isXmlDataIsFile() {
		return xmlDataIsFile;
	}

	public void setXmlDataIsFile(boolean xmlDataIsFile) {
		this.xmlDataIsFile = xmlDataIsFile;
	}

	public String getTextOutputFieldName() {
		return textOutputFieldName;
	}

	public void setTextOutputFieldName(String textOutputFieldName) {
		this.textOutputFieldName = textOutputFieldName;
	}

	public void getFields(RowMetaInterface rowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {

	}

	public Object clone() {

		TextReportMeta retval = (TextReportMeta) super.clone();
		return retval;

	}

	public void setDefault() {

	}

	public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev,
			String input[], String output[], RowMetaInterface info) {

		CheckResult cr;

		if (input.length > 0) {

			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "Step is receiving info from other steps.", stepMeta);
			remarks.add(cr);

		} else {

			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "No input received from other steps.", stepMeta);
			remarks.add(cr);

		}

	}

	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
		return new TextReportDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new TextReport(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	public StepDataInterface getStepData() {
		return new TextReportData();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleXMLException {

		try {

			textEngineFieldName = XMLHandler.getTagValue(stepnode, "textEngineFieldName");
			textTplFieldName = XMLHandler.getTagValue(stepnode, "textTplFieldName");
			xmlDataFieldName = XMLHandler.getTagValue(stepnode, "xmlDataFieldName");
			xmlDataIsFile = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "xmlDataIsFile"));
			textOutputFieldName = XMLHandler.getTagValue(stepnode, "textOutputFieldName");

		} catch (Exception e) {
			throw new KettleXMLException("Unable to read step info from XML node", e);
		}

	}

	public String getXML() {

		StringBuffer retval = new StringBuffer();
		retval.append("    " + XMLHandler.addTagValue("textEngineFieldName", textEngineFieldName));
		retval.append("    " + XMLHandler.addTagValue("textTplFieldName", textTplFieldName));
		retval.append("    " + XMLHandler.addTagValue("xmlDataFieldName", xmlDataFieldName));
		retval.append("    " + XMLHandler.addTagValue("xmlDataIsFile", xmlDataIsFile));
		retval.append("    " + XMLHandler.addTagValue("textOutputFieldName", textOutputFieldName));

		return retval.toString();

	}

	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {

		try {

			textEngineFieldName = rep.getStepAttributeString(id_step, "textEngineFieldName");
			textTplFieldName = rep.getStepAttributeString(id_step, "textTplFieldName");
			xmlDataFieldName = rep.getStepAttributeString(id_step, "xmlDataFieldName");
			xmlDataIsFile = rep.getStepAttributeBoolean(id_step, "xmlDataIsFile");
			textOutputFieldName = rep.getStepAttributeString(id_step, "textOutputFieldName");

		} catch (Exception e) {

			throw new KettleXMLException("Unable to read step info from repository", e);
		}
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {

		try {

			rep.saveStepAttribute(id_transformation, id_step, "textEngineFieldName", textEngineFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "textTplFieldName", textTplFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "xmlDataFieldName", xmlDataFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "xmlDataIsFile", xmlDataIsFile);
			rep.saveStepAttribute(id_transformation, id_step, "textOutputFieldName", textOutputFieldName);

		} catch (Exception e) {

			throw new KettleXMLException("Unable to write step info in repository", e);

		}
	}

}
