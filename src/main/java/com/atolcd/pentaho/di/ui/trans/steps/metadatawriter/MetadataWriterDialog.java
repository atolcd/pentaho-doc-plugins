package com.atolcd.pentaho.di.ui.trans.steps.metadatawriter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import com.atolcd.pentaho.di.trans.steps.metadatawriter.MetadataWriterMeta;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.ui.trans.step.TableItemInsertListener;

public class MetadataWriterDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = MetadataWriterMeta.class;

	private static List<String> coreMetadataKeys = Arrays.asList("author", "comment", "creator", "keywords", "subject",
			"title");

	private Label wlInputFileField;
	private CCombo wInputFileField;
	private FormData fdlInputFileField, fdInputFileField;

	private Label wlOutputFileField;
	private CCombo wOutputFileField;
	private FormData fdlOutputFileField, fdOutputFileField;

	private Label wlCleanAllCustomMetadata;
	private Button wCleanAllCustomMetadata;
	private FormData fdlCleanAllCustomMetadata, fdCleanAllCustomMetadata;

	private Label wlMetadata;
	private TableView wMetadata;
	private FormData fdlMetadata, fdMetadata;

	private ColumnInfo[] metadataColumnInfo;

	Button wLoadPreviousFieldsAsMeta;

	private MetadataWriterMeta input;

	public MetadataWriterDialog(Shell parent, Object in, TransMeta tr, String sname) {

		super(parent, (BaseStepMeta) in, tr, sname);
		input = (MetadataWriterMeta) in;

	}

	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		backupChanged = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "MetadataWriter.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Step name
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		// Input file
		wlInputFileField = new Label(shell, SWT.RIGHT);
		wlInputFileField.setText(BaseMessages.getString(PKG, "MetadataWriter.InputFileFieldName.Label"));
		props.setLook(wlInputFileField);
		fdlInputFileField = new FormData();
		fdlInputFileField.left = new FormAttachment(0, 0);
		fdlInputFileField.top = new FormAttachment(wStepname, margin);
		fdlInputFileField.right = new FormAttachment(middle, -margin);
		wlInputFileField.setLayoutData(fdlInputFileField);

		wInputFileField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wInputFileField.setToolTipText(BaseMessages.getString(PKG, "MetadataWriter.InputFileFieldName.ToolTip"));
		wInputFileField.setEditable(false);
		props.setLook(wInputFileField);
		wInputFileField.addModifyListener(lsMod);
		fdInputFileField = new FormData();
		fdInputFileField.left = new FormAttachment(middle, 0);
		fdInputFileField.right = new FormAttachment(100, 0);
		fdInputFileField.top = new FormAttachment(wStepname, margin);
		wInputFileField.setLayoutData(fdInputFileField);

		// Output file
		wlOutputFileField = new Label(shell, SWT.RIGHT);
		wlOutputFileField.setText(BaseMessages.getString(PKG, "MetadataWriter.OutputFileFieldName.Label"));
		props.setLook(wlOutputFileField);
		fdlOutputFileField = new FormData();
		fdlOutputFileField.left = new FormAttachment(0, 0);
		fdlOutputFileField.top = new FormAttachment(wInputFileField, margin);
		fdlOutputFileField.right = new FormAttachment(middle, -margin);
		wlOutputFileField.setLayoutData(fdlOutputFileField);

		wOutputFileField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wOutputFileField.setToolTipText(BaseMessages.getString(PKG, "MetadataWriter.OutputFileFieldName.ToolTip"));
		wOutputFileField.setEditable(false);
		props.setLook(wOutputFileField);
		wOutputFileField.addModifyListener(lsMod);
		fdOutputFileField = new FormData();
		fdOutputFileField.left = new FormAttachment(middle, 0);
		fdOutputFileField.right = new FormAttachment(100, 0);
		fdOutputFileField.top = new FormAttachment(wInputFileField, margin);
		wOutputFileField.setLayoutData(fdOutputFileField);

		// Clean all custom metadata
		wlCleanAllCustomMetadata = new Label(shell, SWT.RIGHT);
		wlCleanAllCustomMetadata.setText(BaseMessages.getString(PKG, "MetadataWriter.CleanAllCustomMetadata.Label"));
		props.setLook(wlCleanAllCustomMetadata);
		fdlCleanAllCustomMetadata = new FormData();
		fdlCleanAllCustomMetadata.left = new FormAttachment(0, 0);
		fdlCleanAllCustomMetadata.top = new FormAttachment(wOutputFileField, margin);
		fdlCleanAllCustomMetadata.right = new FormAttachment(middle, -margin);
		wlCleanAllCustomMetadata.setLayoutData(fdlCleanAllCustomMetadata);

		wCleanAllCustomMetadata = new Button(shell, SWT.CHECK);
		wCleanAllCustomMetadata
				.setToolTipText(BaseMessages.getString(PKG, "MetadataWriter.CleanAllCustomMetadata.ToolTip"));
		props.setLook(wCleanAllCustomMetadata);
		fdCleanAllCustomMetadata = new FormData();
		fdCleanAllCustomMetadata.left = new FormAttachment(middle, 0);
		fdCleanAllCustomMetadata.right = new FormAttachment(100, 0);
		fdCleanAllCustomMetadata.top = new FormAttachment(wOutputFileField, margin);
		wCleanAllCustomMetadata.setLayoutData(fdCleanAllCustomMetadata);

		// Ok, cancel button
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		wLoadPreviousFieldsAsMeta = new Button(shell, SWT.PUSH);
		wLoadPreviousFieldsAsMeta.setText(BaseMessages.getString(PKG, "MetadataWriter.LoadPreviousFieldsAsMeta.Label"));

		setButtonPositions(new Button[] { wOK, wCancel, wLoadPreviousFieldsAsMeta }, margin, null);

		// Metadata
		wlMetadata = new Label(shell, SWT.NONE);
		wlMetadata.setText(BaseMessages.getString(PKG, "MetadataWriter.Metadata.Label"));
		props.setLook(wlMetadata);
		fdlMetadata = new FormData();
		fdlMetadata.left = new FormAttachment(0, 0);
		fdlMetadata.top = new FormAttachment(wlCleanAllCustomMetadata, margin);
		wlMetadata.setLayoutData(fdlMetadata);

		metadataColumnInfo = getMetadataColumnsInfos();
		wMetadata = new TableView(transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, metadataColumnInfo, 0,
				lsMod, props);
		wMetadata.optWidth(true);
		fdMetadata = new FormData();
		fdMetadata.left = new FormAttachment(0, 0);
		fdMetadata.top = new FormAttachment(wlMetadata, margin);
		fdMetadata.right = new FormAttachment(100, 0);
		fdMetadata.bottom = new FormAttachment(wOK, -2 * margin);
		fdMetadata.height = 75;
		wMetadata.setLayoutData(fdMetadata);

		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};
		Listener lsLoadPreviousFields = new Listener() {
			public void handleEvent(Event e) {
				loadPreviousFieldsAsCustomProperties();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		wLoadPreviousFieldsAsMeta.addListener(SWT.Selection, lsLoadPreviousFields);

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		loadData();
		input.setChanged(changed);
		setSize();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return stepname;
	}

	// Load data
	public void loadData() {

		wInputFileField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getInputFileFieldName() != null) {
			wInputFileField.setText(input.getInputFileFieldName());
		}

		wOutputFileField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getOutputFileFieldName() != null) {
			wOutputFileField.setText(input.getOutputFileFieldName());
		}

		wCleanAllCustomMetadata.setSelection(input.isCleanAllCustomMetadata());

		metadataColumnInfo[0].setComboValues(coreMetadataKeys.toArray(new String[coreMetadataKeys.size()]));
		try {
			metadataColumnInfo[1].setComboValues(transMeta.getPrevStepFields(stepname).getFieldNames());
		} catch (KettleException ke) {
			new ErrorDialog(shell,
					BaseMessages.getString(PKG, "ChangeFileEncodingDialog.FailedToGetFields.DialogTitle"),
					BaseMessages.getString(PKG, "ChangeFileEncodingDialog.FailedToGetFields.DialogMessage"), ke);
		}

		Table metadataTable = wMetadata.table;
		if (input.getMetadata().size() > 0) {
			metadataTable.removeAll();
		}

		Iterator<String> metadataIt = input.getMetadata().keySet().iterator();
		int j = 0;
		while (metadataIt.hasNext()) {

			String key = metadataIt.next();
			TableItem tableItem = new TableItem(metadataTable, SWT.NONE);
			tableItem.setText(0, "" + (j + 1));
			tableItem.setText(1, (String) key);
			tableItem.setText(2, (String) input.getMetadata().get(key));

		}

		wMetadata.setRowNums();

		wStepname.selectAll();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(backupChanged);
		dispose();
	}

	private void ok() {

		stepname = wStepname.getText();
		input.setInputFileFieldName(wInputFileField.getText());
		input.setOutputFileFieldName(wOutputFileField.getText());
		input.setCleanAllCustomMetadata(wCleanAllCustomMetadata.getSelection());
		int nrMetadata = wMetadata.nrNonEmpty();
		LinkedHashMap<String, String> metadata = new LinkedHashMap<String, String>();
		for (int i = 0; i < nrMetadata; i++) {
			TableItem tableItem = wMetadata.getNonEmpty(i);
			metadata.put(tableItem.getText(1), tableItem.getText(2));
		}

		input.setMetadata(metadata);

		dispose();
	}

	/**
	 * Liste les colonnes d'un certain type pour un step donné
	 * 
	 * @param stepName
	 * @param type
	 * @return
	 */
	private String[] getFieldsFromType(int type) {

		String fieldNamesFromType[] = new String[] {};

		try {

			// Previous columns
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null) {

				// Data type filter
				TreeSet<String> fieldsTree = new TreeSet<String>();
				String[] fieldNames = r.getFieldNames();
				String[] fieldNamesAndTypes = r.getFieldNamesAndTypes(0);

				for (int i = 0; i < fieldNames.length; i++) {
					if (fieldNamesAndTypes[i].toLowerCase().contains(ValueMetaBase.getTypeDesc(type).toLowerCase())) {
						if (fieldNames[i] != null && !fieldNames[i].isEmpty()) {
							fieldsTree.add(fieldNames[i]);
						}
					}
				}

				fieldNamesFromType = fieldsTree.toArray(new String[] {});

			}

		} catch (KettleException ke) {
			new ErrorDialog(shell,
					BaseMessages.getString(PKG, "ChangeFileEncodingDialog.FailedToGetFields.DialogTitle"),
					BaseMessages.getString(PKG, "ChangeFileEncodingDialog.FailedToGetFields.DialogMessage"), ke);
		}

		return fieldNamesFromType;

	}

	private ColumnInfo[] getMetadataColumnsInfos() {

		// Metadata keys column
		ColumnInfo keyColumnInfo = new ColumnInfo(
				BaseMessages.getString(PKG, "MetadataWriter.Metadata.KeyColumn.Label"), ColumnInfo.COLUMN_TYPE_CCOMBO);
		keyColumnInfo.setReadOnly(false);
		keyColumnInfo.setToolTip(BaseMessages.getString(PKG, "MetadataWriter.Metadata.KeyColumn.ToolTip"));

		// Metadata values column
		ColumnInfo fieldValueColumnInfo = new ColumnInfo(
				BaseMessages.getString(PKG, "MetadataWriter.Metadata.FieldValueColumn.Label"),
				ColumnInfo.COLUMN_TYPE_CCOMBO);
		fieldValueColumnInfo.setReadOnly(true);
		fieldValueColumnInfo
				.setToolTip(BaseMessages.getString(PKG, "MetadataWriter.Metadata.FieldValueColumn.ToolTip"));

		return new ColumnInfo[] { keyColumnInfo, fieldValueColumnInfo };

	}

	private void loadPreviousFieldsAsCustomProperties() {

		try {
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null) {
				TableItemInsertListener tableInsertListener = new TableItemInsertListener() {
					public boolean tableItemInserted(TableItem tableItem, ValueMetaInterface v) {

						if ((wInputFileField.getText() != null && !wInputFileField.getText().isEmpty()
								&& !v.getName().equalsIgnoreCase(wInputFileField.getText())) && (

						wOutputFileField.getText() != null && !wOutputFileField.getText().isEmpty()
								&& !v.getName().equalsIgnoreCase(wOutputFileField.getText())

						)) {

							tableItem.setText(2, v.getName());
							return true;

						} else {

							return false;
						}

					}
				};

				BaseStepDialog.getFieldsFromPrevious(r, wMetadata, 1, new int[] { 1 }, new int[] {}, -1, -1,
						tableInsertListener);

			}

		} catch (KettleException ke) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Title"),
					BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"), ke);
		}

	}

}
