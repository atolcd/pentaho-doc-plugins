package com.atolcd.pentaho.di.trans.steps.texttopdf;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class TextToPdfData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;

	public int textInputFileFieldPosition;
	public int pdfOutputFileFieldPosition;

	public TextToPdfData() {
		super();
	}
}
