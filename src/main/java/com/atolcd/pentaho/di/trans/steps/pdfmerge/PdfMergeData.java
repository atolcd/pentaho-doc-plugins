package com.atolcd.pentaho.di.trans.steps.pdfmerge;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class PdfMergeData extends BaseStepData implements StepDataInterface {
  public RowMetaInterface outputRowMeta;

  public Integer inputFieldPosition;
  public Integer outputFieldPosition;

  public PdfMergeData() {
  }
}
