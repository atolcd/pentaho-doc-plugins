package com.atolcd.pentaho.di.trans.steps.metadatawriter;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class MetadataWriterData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;

	public int inputFileFieldPosition;
	public int outputFileFieldPosition;

	public MetadataWriterData() {
		super();
	}

}
