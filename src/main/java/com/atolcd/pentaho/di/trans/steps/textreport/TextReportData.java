package com.atolcd.pentaho.di.trans.steps.textreport;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class TextReportData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;

	public int textTplFieldPosition;
	public int xmlDataFieldPosition;
	public int textOutputFieldPosition;

	public String engine;

	public TextReportData() {
		super();
	}
}
