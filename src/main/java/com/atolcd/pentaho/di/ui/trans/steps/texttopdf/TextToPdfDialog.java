package com.atolcd.pentaho.di.ui.trans.steps.texttopdf;

import java.util.TreeSet;

import com.atolcd.pentaho.di.trans.steps.texttopdf.TextToPdfMeta;

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

public class TextToPdfDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = TextToPdfMeta.class;

	private Label wlTextInputFileField;
	private CCombo wTextInputFileField;
	private FormData fdlTextInputFileField, fdTextInputFileField;

	private Label wlPdfOutputFileField;
	private CCombo wPdfOutputFileField;
	private FormData fdlPdfOutputFileField, fdPdfOutputFileField;

	private Label wlCopyMetadata;
	private Button wCopyMetadata;
	private FormData fdlCopyMetadata, fdCopyMetadata;

	private TextToPdfMeta input;

	public TextToPdfDialog(Shell parent, Object in, TransMeta tr, String sname) {

		super(parent, (BaseStepMeta) in, tr, sname);
		input = (TextToPdfMeta) in;

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
		shell.setText(BaseMessages.getString(PKG, "TextToPdf.Shell.Title"));

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

		// Document texte
		wlTextInputFileField = new Label(shell, SWT.RIGHT);
		wlTextInputFileField.setText(BaseMessages.getString(PKG, "TextToPdf.TextInputFileFieldName.Label"));
		props.setLook(wlTextInputFileField);
		fdlTextInputFileField = new FormData();
		fdlTextInputFileField.left = new FormAttachment(0, 0);
		fdlTextInputFileField.top = new FormAttachment(wStepname, margin);
		fdlTextInputFileField.right = new FormAttachment(middle, -margin);
		wlTextInputFileField.setLayoutData(fdlTextInputFileField);

		wTextInputFileField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wTextInputFileField.setToolTipText(BaseMessages.getString(PKG, "TextToPdf.TextInputFileFieldName.ToolTip"));
		wTextInputFileField.setEditable(false);
		props.setLook(wTextInputFileField);
		wTextInputFileField.addModifyListener(lsMod);
		fdTextInputFileField = new FormData();
		fdTextInputFileField.left = new FormAttachment(middle, 0);
		fdTextInputFileField.right = new FormAttachment(100, 0);
		fdTextInputFileField.top = new FormAttachment(wStepname, margin);
		wTextInputFileField.setLayoutData(fdTextInputFileField);

		// Document PDF
		wlPdfOutputFileField = new Label(shell, SWT.RIGHT);
		wlPdfOutputFileField.setText(BaseMessages.getString(PKG, "TextToPdf.PdfOutputFileFieldName.Label"));
		props.setLook(wlPdfOutputFileField);
		fdlPdfOutputFileField = new FormData();
		fdlPdfOutputFileField.left = new FormAttachment(0, 0);
		fdlPdfOutputFileField.top = new FormAttachment(wTextInputFileField, margin);
		fdlPdfOutputFileField.right = new FormAttachment(middle, -margin);
		wlPdfOutputFileField.setLayoutData(fdlPdfOutputFileField);

		wPdfOutputFileField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wPdfOutputFileField.setToolTipText(BaseMessages.getString(PKG, "TextToPdf.PdfOutputFileFieldName.ToolTip"));
		wPdfOutputFileField.setEditable(false);
		props.setLook(wPdfOutputFileField);
		wPdfOutputFileField.addModifyListener(lsMod);
		fdPdfOutputFileField = new FormData();
		fdPdfOutputFileField.left = new FormAttachment(middle, 0);
		fdPdfOutputFileField.right = new FormAttachment(100, 0);
		fdPdfOutputFileField.top = new FormAttachment(wTextInputFileField, margin);
		wPdfOutputFileField.setLayoutData(fdPdfOutputFileField);

		// Copier les métadonnées
		wlCopyMetadata = new Label(shell, SWT.RIGHT);
		wlCopyMetadata.setText(BaseMessages.getString(PKG, "TextToPdf.CopyMetadata.Label"));
		props.setLook(wlCopyMetadata);
		fdlCopyMetadata = new FormData();
		fdlCopyMetadata.left = new FormAttachment(0, 0);
		fdlCopyMetadata.top = new FormAttachment(wPdfOutputFileField, margin);
		fdlCopyMetadata.right = new FormAttachment(middle, -margin);
		wlCopyMetadata.setLayoutData(fdlCopyMetadata);

		wCopyMetadata = new Button(shell, SWT.CHECK);
		wCopyMetadata.setToolTipText(BaseMessages.getString(PKG, "TextToPdf.CopyMetadata.ToolTip"));
		props.setLook(wCopyMetadata);
		fdCopyMetadata = new FormData();
		fdCopyMetadata.left = new FormAttachment(middle, 0);
		fdCopyMetadata.right = new FormAttachment(100, 0);
		fdCopyMetadata.top = new FormAttachment(wPdfOutputFileField, margin);
		wCopyMetadata.setLayoutData(fdCopyMetadata);

		// Boutons Ok et Annuler
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		setButtonPositions(new Button[] { wOK, wCancel }, margin, wCopyMetadata);
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

		wTextInputFileField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getTextInputFileFieldName() != null) {
			wTextInputFileField.setText(input.getTextInputFileFieldName());
		}

		wPdfOutputFileField.setItems(getFieldsFromType(ValueMetaInterface.TYPE_STRING));
		if (input.getPdfOutputFileFieldName() != null) {
			wPdfOutputFileField.setText(input.getPdfOutputFileFieldName());
		}

		wCopyMetadata.setSelection(input.isCopyMetadata());

		wStepname.selectAll();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(backupChanged);
		dispose();
	}

	private void ok() {

		stepname = wStepname.getText();
		input.setTextInputFileFieldName(wTextInputFileField.getText());
		input.setPdfOutputFileFieldName(wPdfOutputFileField.getText());
		input.setCopyMetadata(wCopyMetadata.getSelection());

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
