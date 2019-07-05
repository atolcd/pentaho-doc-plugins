package com.atolcd.pentaho.di.trans.steps.pdfmerge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atolcd.pentaho.di.ui.trans.steps.pdfmerge.PdfMergeDialog;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

@Step(id = "PdfMerge", image = "com/atolcd/pentaho/di/ui/trans/steps/images/PdfMerge.svg", name = "PdfMerge.Shell.Name", description = "PdfMerge.Shell.Description", categoryDescription = "PdfMerge.Shell.CategoryDescription", i18nPackageName = "com.atolcd.pentaho.di.trans.steps.pdfmerge")
public class PdfMergeMeta extends org.pentaho.di.trans.step.BaseStepMeta implements StepMetaInterface {
  private String fileOutputFieldName;
  private String fileInputFieldName;
  private String fileInputSeparator;
  private HashMap<String, String> metadata;
  private boolean keepBookmarks;

  public PdfMergeMeta() {
  }

  public String getFileOutputFieldName() {
    return fileOutputFieldName;
  }

  public void setFileOutputFieldName(String fileOutputFieldName) {
    this.fileOutputFieldName = fileOutputFieldName;
  }

  public String getFileInputFieldName() {
    return fileInputFieldName;
  }

  public void setFileInputFieldName(String fileInputFieldName) {
    this.fileInputFieldName = fileInputFieldName;
  }

  public String getFileInputSeparator() {
    return fileInputSeparator;
  }

  public void setFileInputSeparator(String fileInputSeparator) {
    this.fileInputSeparator = fileInputSeparator;
  }

  public HashMap<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(HashMap<String, String> metadata) {
    this.metadata = metadata;
  }

  public boolean isKeepBookmarks() {
    return keepBookmarks;
  }

  public void setKeepBookmarks(boolean keepBookmarks) {
    this.keepBookmarks = keepBookmarks;
  }

  public String getXML() {
    StringBuffer retval = new StringBuffer();
    retval.append("    " + XMLHandler.addTagValue("fileOutputFieldName", fileOutputFieldName));
    retval.append("    " + XMLHandler.addTagValue("fileInputFieldName", fileInputFieldName));
    retval.append("    " + XMLHandler.addTagValue("fileInputSeparator", fileInputSeparator));
    retval.append("    " + XMLHandler.addTagValue("keepBookmarks", keepBookmarks));

    retval.append("    <properties>").append(Const.CR);
    Iterator<String> metadataIt = metadata.keySet().iterator();
    while (metadataIt.hasNext()) {
      String key = (String) metadataIt.next();
      retval.append("      <property>").append(Const.CR);
      retval.append("        ").append(XMLHandler.addTagValue("pdfKey", key));
      retval.append("        ").append(XMLHandler.addTagValue("valueFieldName", (String) metadata.get(key)));
      retval.append("      </property>").append(Const.CR);
    }
    retval.append("    </properties>").append(Const.CR);

    return retval.toString();
  }

  public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep,
      org.pentaho.di.core.variables.VariableSpace space) {
  }

  public Object clone() {
    PdfMergeMeta retval = (PdfMergeMeta) super.clone();

    metadata = new HashMap<String, String>();

    Iterator<String> metadataIt = metadata.keySet().iterator();
    while (metadataIt.hasNext()) {
      String key = (String) metadataIt.next();
      metadata.put(key, (String) metadata.get(key));
    }

    return retval;
  }

  public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
      throws KettleXMLException {
    try {
      fileOutputFieldName = XMLHandler.getTagValue(stepnode, "fileOutputFieldName");
      fileInputFieldName = XMLHandler.getTagValue(stepnode, "fileInputFieldName");
      fileInputSeparator = XMLHandler.getTagValue(stepnode, "fileInputSeparator");
      keepBookmarks = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "keepBookmarks"));

      metadata = new HashMap<String, String>();
      Node propertiesNode = XMLHandler.getSubNode(stepnode, "properties");
      for (int i = 0; i < XMLHandler.countNodes(propertiesNode, "property"); i++) {
        Node fnode = XMLHandler.getSubNodeByNr(propertiesNode, "property", i);
        metadata.put(XMLHandler.getTagValue(fnode, "pdfKey"), XMLHandler.getTagValue(fnode, "valueFieldName"));
      }
    } catch (Exception e) {
      throw new KettleXMLException("Unable to read step info from XML node", e);
    }
  }

  public void setDefault() {
    fileInputSeparator = ";";
    keepBookmarks = false;
    metadata = new HashMap<String, String>();
  }

  public void check(List<org.pentaho.di.core.CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta,
      RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
    if (input.length > 0) {
      CheckResult cr = new CheckResult(1, "Step is receiving info from other steps.", stepMeta);
      remarks.add(cr);
    } else {
      CheckResult cr = new CheckResult(4, "No input received from other steps.", stepMeta);
      remarks.add(cr);
    }
  }

  public org.pentaho.di.trans.step.StepDialogInterface getDialog(org.eclipse.swt.widgets.Shell shell,
      StepMetaInterface meta, TransMeta transMeta, String name) {
    return new PdfMergeDialog(shell, meta, transMeta, name);
  }

  public org.pentaho.di.trans.step.StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface,
      int cnr, TransMeta transMeta, org.pentaho.di.trans.Trans disp) {
    return new PdfMerge(stepMeta, stepDataInterface, cnr, transMeta, disp);
  }

  public StepDataInterface getStepData() {
    return new PdfMergeData();
  }

  public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
      throws org.pentaho.di.core.exception.KettleException {
    try {
      fileOutputFieldName = rep.getStepAttributeString(id_step, "fileOutputFieldName");
      fileInputFieldName = rep.getStepAttributeString(id_step, "fileInputFieldName");
      fileInputSeparator = rep.getStepAttributeString(id_step, "fileInputSeparator");
      keepBookmarks = rep.getStepAttributeBoolean(id_step, "keepBookmarks");

      metadata = new HashMap<String, String>();
      int nrKeys = rep.countNrStepAttributes(id_step, "keyName");
      for (int i = 0; i < nrKeys; i++) {
        metadata.put(rep.getStepAttributeString(id_step, i, "pdfKey"),
            rep.getStepAttributeString(id_step, i, "valueFieldName"));
      }

    } catch (Exception e) {
      throw new KettleXMLException("Unable to read step info from repository", e);
    }
  }

  public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step)
      throws org.pentaho.di.core.exception.KettleException {
    try {
      rep.saveStepAttribute(id_transformation, id_step, "fileOutputFieldName", fileOutputFieldName);
      rep.saveStepAttribute(id_transformation, id_step, "fileInputFieldName", fileInputFieldName);
      rep.saveStepAttribute(id_transformation, id_step, "fileInputSeparator", fileInputSeparator);
      rep.saveStepAttribute(id_transformation, id_step, "keepBookmarks", keepBookmarks);

      Iterator<String> metadataIt = metadata.keySet().iterator();
      int j = 0;
      while (metadataIt.hasNext()) {
        String key = (String) metadataIt.next();
        rep.saveStepAttribute(id_transformation, id_step, j, "pdfKey", key);
        rep.saveStepAttribute(id_transformation, id_step, j, "valueFieldName", (String) metadata.get(key));
        j++;
      }
    } catch (Exception e) {
      throw new KettleXMLException("Unable to write step info in repository", e);
    }
  }
}
