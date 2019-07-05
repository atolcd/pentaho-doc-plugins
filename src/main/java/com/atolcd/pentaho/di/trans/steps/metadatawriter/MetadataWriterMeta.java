package com.atolcd.pentaho.di.trans.steps.metadatawriter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atolcd.pentaho.di.ui.trans.steps.metadatawriter.MetadataWriterDialog;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
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

@Step(id = "MetadataWriter", image = "com/atolcd/pentaho/di/ui/trans/steps/images/MetadataWriter.svg", name = "MetadataWriter.Shell.Name", description = "MetadataWriter.Shell.Description", categoryDescription = "MetadataWriter.Shell.CategoryDescription", i18nPackageName = "com.atolcd.pentaho.di.trans.steps.metadatawriter")
public class MetadataWriterMeta extends BaseStepMeta implements StepMetaInterface {

	private String inputFileFieldName;
	private String outputFileFieldName;
	private LinkedHashMap<String, String> metadata;
	private boolean cleanAllCustomMetadata;

	public MetadataWriterMeta() {
		super();
	}

	public String getInputFileFieldName() {
		return inputFileFieldName;
	}

	public void setInputFileFieldName(String inputFileFieldName) {
		this.inputFileFieldName = inputFileFieldName;
	}

	public String getOutputFileFieldName() {
		return outputFileFieldName;
	}

	public void setOutputFileFieldName(String outputFileFieldName) {
		this.outputFileFieldName = outputFileFieldName;
	}

	public LinkedHashMap<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(LinkedHashMap<String, String> metadata) {
		this.metadata = metadata;
	}

	public boolean isCleanAllCustomMetadata() {
		return cleanAllCustomMetadata;
	}

	public void setCleanAllCustomMetadata(boolean cleanAllCustomMetadata) {
		this.cleanAllCustomMetadata = cleanAllCustomMetadata;
	}

	public void getFields(RowMetaInterface rowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {

	}

	public Object clone() {

		MetadataWriterMeta retval = (MetadataWriterMeta) super.clone();

		retval.metadata = new LinkedHashMap<String, String>();

		Iterator<String> metadataIt = metadata.keySet().iterator();
		while (metadataIt.hasNext()) {

			String key = metadataIt.next();
			retval.metadata.put(key, metadata.get(key));
		}

		return retval;

	}

	public void setDefault() {

		cleanAllCustomMetadata = false;
		metadata = new LinkedHashMap<String, String>();

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
		return new MetadataWriterDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new MetadataWriter(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	public StepDataInterface getStepData() {
		return new MetadataWriterData();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleXMLException {

		try {

			inputFileFieldName = XMLHandler.getTagValue(stepnode, "inputFileFieldName");
			outputFileFieldName = XMLHandler.getTagValue(stepnode, "outputFileFieldName");
			cleanAllCustomMetadata = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "cleanAllCustomMetadata"));

			// Custom metadata
			metadata = new LinkedHashMap<String, String>();
			Node propertiesNode = XMLHandler.getSubNode(stepnode, "properties");
			for (int i = 0; i < XMLHandler.countNodes(propertiesNode, "property"); i++) {
				Node fnode = XMLHandler.getSubNodeByNr(propertiesNode, "property", i);
				metadata.put(XMLHandler.getTagValue(fnode, "key"), XMLHandler.getTagValue(fnode, "valueFieldName"));
			}

		} catch (Exception e) {
			throw new KettleXMLException("Unable to read step info from XML node", e);
		}

	}

	public String getXML() {

		StringBuffer retval = new StringBuffer();
		retval.append("    " + XMLHandler.addTagValue("inputFileFieldName", inputFileFieldName));
		retval.append("    " + XMLHandler.addTagValue("outputFileFieldName", outputFileFieldName));
		retval.append("    " + XMLHandler.addTagValue("cleanAllCustomMetadata", cleanAllCustomMetadata));

		// Custom metadata
		retval.append("    <properties>").append(Const.CR);
		Iterator<String> metadataIt = metadata.keySet().iterator();
		while (metadataIt.hasNext()) {

			String key = metadataIt.next();
			retval.append("      <property>").append(Const.CR);
			retval.append("        ").append(XMLHandler.addTagValue("key", key));
			retval.append("        ").append(XMLHandler.addTagValue("valueFieldName", metadata.get(key)));
			retval.append("      </property>").append(Const.CR);
		}
		retval.append("    </properties>").append(Const.CR);

		return retval.toString();

	}

	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {

		try {

			inputFileFieldName = rep.getStepAttributeString(id_step, "inputFileFieldName");
			outputFileFieldName = rep.getStepAttributeString(id_step, "outputFileFieldName");
			cleanAllCustomMetadata = rep.getStepAttributeBoolean(id_step, "cleanAllCustomMetadata");

			// Custom metadata
			metadata = new LinkedHashMap<String, String>();
			int nrKeys = rep.countNrStepAttributes(id_step, "keyName");
			for (int i = 0; i < nrKeys; i++) {

				metadata.put(rep.getStepAttributeString(id_step, i, "key"),
						rep.getStepAttributeString(id_step, i, "valueFieldName"));

			}

		} catch (Exception e) {

			throw new KettleXMLException("Unable to read step info from repository", e);
		}
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {

		try {

			rep.saveStepAttribute(id_transformation, id_step, "inputFileFieldName", inputFileFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "outputFileFieldName", outputFileFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "cleanAllCustomMetadata", cleanAllCustomMetadata);

			// Custom metadata
			Iterator<String> metadataIt = metadata.keySet().iterator();
			int j = 0;
			while (metadataIt.hasNext()) {

				String key = metadataIt.next();
				rep.saveStepAttribute(id_transformation, id_step, j, "key", key);
				rep.saveStepAttribute(id_transformation, id_step, j, "valueFieldName", metadata.get(key));
				j++;
			}

		} catch (Exception e) {

			throw new KettleXMLException("Unable to write step info in repository", e);

		}
	}

}
