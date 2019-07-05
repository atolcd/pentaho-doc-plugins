package com.atolcd.pentaho.di.trans.steps.texttopdf;

import java.util.List;
import java.util.Map;

import com.atolcd.pentaho.di.ui.trans.steps.texttopdf.TextToPdfDialog;

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

@Step(id = "TextToPdf", image = "com/atolcd/pentaho/di/ui/trans/steps/images/TextToPdf.svg", name = "TextToPdf.Shell.Name", description = "TextToPdf.Shell.Description", categoryDescription = "TextToPdf.Shell.CategoryDescription", i18nPackageName = "com.atolcd.pentaho.di.trans.steps.texttopdf")
public class TextToPdfMeta extends BaseStepMeta implements StepMetaInterface {

	private String textInputFileFieldName;
	private String pdfOutputFileFieldName;
	private boolean copyMetadata;

	public TextToPdfMeta() {
		super();
	}

	public String getTextInputFileFieldName() {
		return textInputFileFieldName;
	}

	public void setTextInputFileFieldName(String textInputFileFieldName) {
		this.textInputFileFieldName = textInputFileFieldName;
	}

	public String getPdfOutputFileFieldName() {
		return pdfOutputFileFieldName;
	}

	public void setPdfOutputFileFieldName(String pdfOutputFileFieldName) {
		this.pdfOutputFileFieldName = pdfOutputFileFieldName;
	}

	public boolean isCopyMetadata() {
		return copyMetadata;
	}

	public void setCopyMetadata(boolean copyMetadata) {
		this.copyMetadata = copyMetadata;
	}

	public void getFields(RowMetaInterface rowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {

	}

	public Object clone() {

		TextToPdfMeta retval = (TextToPdfMeta) super.clone();
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
		return new TextToPdfDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new TextToPdf(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	public StepDataInterface getStepData() {
		return new TextToPdfData();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleXMLException {

		try {

			textInputFileFieldName = XMLHandler.getTagValue(stepnode, "textInputFileFieldName");
			pdfOutputFileFieldName = XMLHandler.getTagValue(stepnode, "pdfOutputFileFieldName");
			copyMetadata = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "copyMetadata"));

		} catch (Exception e) {
			throw new KettleXMLException("Unable to read step info from XML node", e);
		}

	}

	public String getXML() {

		StringBuffer retval = new StringBuffer();
		retval.append("    " + XMLHandler.addTagValue("textInputFileFieldName", textInputFileFieldName));
		retval.append("    " + XMLHandler.addTagValue("pdfOutputFileFieldName", pdfOutputFileFieldName));
		retval.append("    " + XMLHandler.addTagValue("copyMetadata", copyMetadata));

		return retval.toString();

	}

	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {

		try {

			textInputFileFieldName = rep.getStepAttributeString(id_step, "textInputFileFieldName");
			pdfOutputFileFieldName = rep.getStepAttributeString(id_step, "pdfOutputFileFieldName");
			copyMetadata = rep.getStepAttributeBoolean(id_step, "copyMetadata");

		} catch (Exception e) {

			throw new KettleXMLException("Unable to read step info from repository", e);
		}
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {

		try {

			rep.saveStepAttribute(id_transformation, id_step, "textInputFileFieldName", textInputFileFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "pdfOutputFileFieldName", pdfOutputFileFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "copyMetadata", copyMetadata);

		} catch (Exception e) {

			throw new KettleXMLException("Unable to write step info in repository", e);

		}
	}

}
