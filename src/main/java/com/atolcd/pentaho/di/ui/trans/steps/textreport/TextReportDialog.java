package com.atolcd.pentaho.di.ui.trans.steps.textreport;

import java.util.TreeSet;

import com.atolcd.pentaho.di.trans.steps.textreport.TextReportMeta;

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
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class TextReportDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = TextReportMeta.class;

	private Label wlTextEngineField;
	private CCombo wTextEngineField;
	private FormData fdlTextEngineField, fdTextEngineField;

	private Label wlTextTplField;
	private CCombo wTextTplField;
	private FormData fdlTextTplField, fdTextTplField;

	private Label wlXmlDataField;
	private CCombo wXmlDataField;
	private FormData fdlXmlDataField, fdXmlDataField;

	private Label wlXmlDataIsFile;
	private Button wXmlDataIsFile;
	private FormData fdlXmlDataIsFile, fdXmlDataIsFile;

	private Label wlTextOutputField;
	private CCombo wTextOutputField;
	private FormData fdlTextOutputField, fdTextOutputField;

	private TextReportMeta input;

	public TextReportDialog(Shell parent, Object in, TransMeta tr, String sname) {

		super(parent, (BaseStepMeta) in, tr, sname);
		input = (TextReportMeta) in;

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
		shell.setText(BaseMessages.getString(PKG, "TextReport.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Nom du step
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

		// Template engine choice
		wlTextEngineField = new Label(shell, SWT.RIGHT);
		wlTextEngineField.setText(BaseMessages.getString(PKG, "TextReport.TextEngineFieldName.Label"));
		props.setLook(wlTextEngineField);
		fdlTextEngineField = new FormData();
		fdlTextEngineField.left = new FormAttachment(0, 0);
		fdlTextEngineField.top = new FormAttachment(wStepname, margin);
		fdlTextEngineField.right = new FormAttachment(middle, -margin);
		wlTextEngineField.setLayoutData(fdlTextEngineField);

		wTextEngineField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wTextEngineField.setToolTipText(BaseMessages.getString(PKG, "TextReport.TextEngineFieldName.ToolTip"));
		wTextEngineField.setEditable(false);
		props.setLook(wTextEngineField);
		wTextEngineField.add("JODReport");
		wTextEngineField.add("xDocReport");
		wTextEngineField.select(0);
		wTextEngineField.addModifyListener(lsMod);
		fdTextEngineField = new FormData();
		fdTextEngineField.left = new FormAttachment(middle, 0);
		fdTextEngineField.right = new FormAttachment(100, 0);
		fdTextEngineField.top = new FormAttachment(wStepname, margin);
		wTextEngineField.setLayoutData(fdTextEngineField);

		// Modéle de document
		wlTextTplField = new Label(shell, SWT.RIGHT);
		wlTextTplField.setText(BaseMessages.getString(PKG, "TextReport.TextTplFieldName.Label"));
		props.setLook(wlTextTplField);
		fdlTextTplField = new FormData();
		fdlTextTplField.left = new FormAttachment(0, 0);
		fdlTextTplField.top = new FormAttachment(wTextEngineField, margin);
		fdlTextTplField.right = new FormAttachment(middle, -margin);
		wlTextTplField.setLayoutData(fdlTextTplField);

		wTextTplField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wTextTplField.setToolTipText(BaseMessages.getString(PKG, "TextReport.TextTplFieldName.ToolTip"));
		wTextTplField.setEditable(false);
		props.setLook(wTextTplField);
		wTextTplField.addModifyListener(lsMod);
		fdTextTplField = new FormData();
		fdTextTplField.left = new FormAttachment(middle, 0);
		fdTextTplField.right = new FormAttachment(100, 0);
		fdTextTplField.top = new FormAttachment(wTextEngineField, margin);
		wTextTplField.setLayoutData(fdTextTplField);

		// Source de donnée XML
		wlXmlDataField = new Label(shell, SWT.RIGHT);
		wlXmlDataField.setText(BaseMessages.getString(PKG, "TextReport.XmlDataFieldName.Label"));
		props.setLook(wlXmlDataField);
		fdlXmlDataField = new FormData();
		fdlXmlDataField.left = new FormAttachment(0, 0);
		fdlXmlDataField.top = new FormAttachment(wTextTplField, margin);
		fdlXmlDataField.right = new FormAttachment(middle, -margin);
		wlXmlDataField.setLayoutData(fdlXmlDataField);

		wXmlDataField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wXmlDataField.setToolTipText(BaseMessages.getString(PKG, "TextReport.XmlDataFieldName.ToolTip"));
		wXmlDataField.setEditable(false);
		props.setLook(wXmlDataField);
		wXmlDataField.addModifyListener(lsMod);
		fdXmlDataField = new FormData();
		fdXmlDataField.left = new FormAttachment(middle, 0);
		fdXmlDataField.right = new FormAttachment(100, 0);
		fdXmlDataField.top = new FormAttachment(wTextTplField, margin);
		wXmlDataField.setLayoutData(fdXmlDataField);

		// Source de donnée = fichier ?
		wlXmlDataIsFile = new Label(shell, SWT.RIGHT);
		wlXmlDataIsFile.setText(BaseMessages.getString(PKG, "TextReport.XmlDataIsFile.Label"));
		props.setLook(wlXmlDataIsFile);
		fdlXmlDataIsFile = new FormData();
		fdlXmlDataIsFile.left = new FormAttachment(0, 0);
		fdlXmlDataIsFile.top = new FormAttachment(wXmlDataField, margin);
		fdlXmlDataIsFile.right = new FormAttachment(middle, -margin);
		wlXmlDataIsFile.setLayoutData(fdlXmlDataIsFile);

		wXmlDataIsFile = new Button(shell, SWT.CHECK);
		wXmlDataIsFile.setToolTipText(BaseMessages.getString(PKG, "TextReport.XmlDataIsFile.ToolTip"));
		props.setLook(wXmlDataIsFile);
		fdXmlDataIsFile = new FormData();
		fdXmlDataIsFile.left = new FormAttachment(middle, 0);
		fdXmlDataIsFile.right = new FormAttachment(100, 0);
		fdXmlDataIsFile.top = new FormAttachment(wXmlDataField, margin);
		wXmlDataIsFile.setLayoutData(fdXmlDataIsFile);

		// Rapport
		wlTextOutputField = new Label(shell, SWT.RIGHT);
		wlTextOutputField.setText(BaseMessages.getString(PKG, "TextReport.TextOutputFieldName.Label"));
		props.setLook(wlTextOutputField);
		fdlTextOutputField = new FormData();
		fdlTextOutputField.left = new FormAttachment(0, 0);
		fdlTextOutputField.top = new FormAttachment(wXmlDataIsFile, margin);
		fdlTextOutputField.right = new FormAttachment(middle, -margin);
		wlTextOutputField.setLayoutData(fdlTextOutputField);

		wTextOutputField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wTextOutputField.setToolTipText(BaseMessages.getString(PKG, "TextReport.TextOutputFieldName.ToolTip"));
		wTextOutputField.setEditable(false);
		props.setLook(wTextOutputField);
		wTextOutputField.addModifyListener(lsMod);
		fdTextOutputField = new FormData();
		fdTextOutputField.left = new FormAttachment(middle, 0);
		fdTextOutputField.right = new FormAttachment(100, 0);
		fdTextOutputField.top = new FormAttachment(wXmlDataIsFile, margin);
		wTextOutputField.setLayoutData(fdTextOutputField);

		// Boutons Ok et Annuler
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		setButtonPositions(new Button[] { wOK, wCancel }, margin, wTextOutputField);
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

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
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

	// Charge les données dans le formulaire
	public void loadData() {
		if (input.getTextEngineFieldName() != null) {
			wTextEngineField.setText(input.getTextEngineFieldName());
		}

		wTextTplField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getTextTplFieldName() != null) {
			wTextTplField.setText(input.getTextTplFieldName());
		}

		wXmlDataField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getXmlDataFieldName() != null) {
			wXmlDataField.setText(input.getXmlDataFieldName());
		}

		wXmlDataIsFile.setSelection(input.isXmlDataIsFile());

		wTextOutputField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getTextOutputFieldName() != null) {
			wTextOutputField.setText(input.getTextOutputFieldName());
		}

		wStepname.selectAll();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(backupChanged);
		dispose();
	}

	private void ok() {

		stepname = wStepname.getText();
		input.setTextEngineFieldName(wTextEngineField.getText());
		input.setTextTplFieldName(wTextTplField.getText());
		input.setXmlDataFieldName(wXmlDataField.getText());
		input.setXmlDataIsFile(wXmlDataIsFile.getSelection());
		input.setTextOutputFieldName(wTextOutputField.getText());

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

			// Récupération des colonnes de l'étape précédente
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null) {

				// Filtrage par type de colonne texte
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

}
