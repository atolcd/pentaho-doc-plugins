package com.atolcd.pentaho.di.ui.trans.steps.pdfmerge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import com.atolcd.pentaho.di.trans.steps.pdfmerge.PdfMergeMeta;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class PdfMergeDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = PdfMergeMeta.class;

  private Label wlOutputField;

  private CCombo wOutputField;

  private FormData fdlOutputField;

  private FormData fdOutputField;

  private Label wlInputSeparator;

  private Text wInputSeparator;

  private FormData fdlInputSeparator;
  private FormData fdInputSeparator;
  private Label wlInputField;
  private CCombo wInputField;
  private FormData fdlInputField;
  private FormData fdInputField;
  private Label wlKeepBookmarks;
  private Button wKeepBookmarks;
  private FormData fdlKeepBookmarks;
  private FormData fdKeepBookmarks;
  private Label wlMetadata;
  private TableView wMetadata;
  private FormData fdlMetadata;
  private FormData fdMetadata;
  private ColumnInfo[] metadataColumnInfo;
  private boolean gotPreviousFields = false;

  private PdfMergeMeta input;

  public PdfMergeDialog(Shell parent, Object in, TransMeta tr, String sname) {
    super(parent, (BaseStepMeta) in, tr, sname);
    input = ((PdfMergeMeta) in);
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell(parent, 3312);
    props.setLook(shell);
    setShellImage(shell, input);

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        input.setChanged();
      }
    };
    backupChanged = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = 5;
    formLayout.marginHeight = 5;

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "PdfMerge.Shell.Title", new String[0]));

    int middle = props.getMiddlePct();
    int margin = 4;

    wlStepname = new Label(shell, 131072);
    wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName", new String[0]));
    props.setLook(wlStepname);
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment(0, 0);
    fdlStepname.right = new FormAttachment(middle, -margin);
    fdlStepname.top = new FormAttachment(0, margin);
    wlStepname.setLayoutData(fdlStepname);
    wStepname = new Text(shell, 18436);
    wStepname.setText(stepname);
    props.setLook(wStepname);
    wStepname.addModifyListener(lsMod);
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment(middle, 0);
    fdStepname.top = new FormAttachment(0, margin);
    fdStepname.right = new FormAttachment(100, 0);
    wStepname.setLayoutData(fdStepname);

    wlInputField = new Label(shell, 131072);
    wlInputField.setText(BaseMessages.getString(PKG, "PdfMerge.InputFieldName.Label", new String[0]));
    props.setLook(wlInputField);
    fdlInputField = new FormData();
    fdlInputField.left = new FormAttachment(0, 0);
    fdlInputField.top = new FormAttachment(wStepname, margin);
    fdlInputField.right = new FormAttachment(middle, -margin);
    wlInputField.setLayoutData(fdlInputField);

    wInputField = new CCombo(shell, 2056);
    wInputField.setToolTipText(BaseMessages.getString(PKG, "PdfMerge.InputFieldName.ToolTip", new String[0]));
    wInputField.setEditable(true);
    props.setLook(wInputField);
    wInputField.addModifyListener(lsMod);
    fdInputField = new FormData();
    fdInputField.left = new FormAttachment(middle, 0);
    fdInputField.right = new FormAttachment(100, 0);
    fdInputField.top = new FormAttachment(wStepname, margin);
    wInputField.setLayoutData(fdInputField);
    wInputField.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
      }

      public void focusGained(FocusEvent e) {
        PdfMergeDialog.this.loadFields();
      }

    });
    wlInputSeparator = new Label(shell, 131072);
    wlInputSeparator.setText(BaseMessages.getString(PKG, "PdfMerge.InputSeparator.Label", new String[0]));
    props.setLook(wlInputSeparator);
    fdlInputSeparator = new FormData();
    fdlInputSeparator.left = new FormAttachment(0, 0);
    fdlInputSeparator.top = new FormAttachment(wInputField, margin);
    fdlInputSeparator.right = new FormAttachment(middle, -margin);
    wlInputSeparator.setLayoutData(fdlInputSeparator);

    wInputSeparator = new Text(shell, 2056);
    wInputSeparator.setToolTipText(BaseMessages.getString(PKG, "PdfMerge.InputSeparator.ToolTip", new String[0]));
    wInputSeparator.setEditable(true);
    props.setLook(wInputSeparator);
    wInputSeparator.addModifyListener(lsMod);
    fdInputSeparator = new FormData();
    fdInputSeparator.left = new FormAttachment(middle, 0);
    fdInputSeparator.right = new FormAttachment(100, 0);
    fdInputSeparator.top = new FormAttachment(wInputField, margin);
    wInputSeparator.setLayoutData(fdInputSeparator);

    wlOutputField = new Label(shell, 131072);
    wlOutputField.setText(BaseMessages.getString(PKG, "PdfMerge.OutputFieldName.Label", new String[0]));
    props.setLook(wlOutputField);
    fdlOutputField = new FormData();
    fdlOutputField.left = new FormAttachment(0, 0);
    fdlOutputField.top = new FormAttachment(wInputSeparator, margin);
    fdlOutputField.right = new FormAttachment(middle, -margin);
    wlOutputField.setLayoutData(fdlOutputField);

    wOutputField = new CCombo(shell, 2056);
    wOutputField.setToolTipText(BaseMessages.getString(PKG, "PdfMerge.OutputFieldName.ToolTip", new String[0]));
    wOutputField.setEditable(true);
    props.setLook(wOutputField);
    wOutputField.addModifyListener(lsMod);
    fdOutputField = new FormData();
    fdOutputField.left = new FormAttachment(middle, 0);
    fdOutputField.right = new FormAttachment(100, 0);
    fdOutputField.top = new FormAttachment(wInputSeparator, margin);
    wOutputField.setLayoutData(fdOutputField);
    wOutputField.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
      }

      public void focusGained(FocusEvent e) {
        PdfMergeDialog.this.loadFields();
      }

    });
    wlKeepBookmarks = new Label(shell, 131072);
    wlKeepBookmarks.setText(BaseMessages.getString(PKG, "PdfMerge.KeepBookmarks.Label", new String[0]));
    props.setLook(wlKeepBookmarks);
    fdlKeepBookmarks = new FormData();
    fdlKeepBookmarks.left = new FormAttachment(0, 0);
    fdlKeepBookmarks.top = new FormAttachment(wOutputField, margin);
    fdlKeepBookmarks.right = new FormAttachment(middle, -margin);
    wlKeepBookmarks.setLayoutData(fdlKeepBookmarks);

    wKeepBookmarks = new Button(shell, 32);
    wKeepBookmarks.setToolTipText(BaseMessages.getString(PKG, "PdfMerge.KeepBookmarks.ToolTip", new String[0]));
    props.setLook(wKeepBookmarks);

    fdKeepBookmarks = new FormData();
    fdKeepBookmarks.left = new FormAttachment(middle, 0);
    fdKeepBookmarks.top = new FormAttachment(wOutputField, margin);
    fdKeepBookmarks.right = new FormAttachment(100, 0);
    wKeepBookmarks.setLayoutData(fdKeepBookmarks);
    wKeepBookmarks.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent arg0) {
        input.setChanged();

      }

    });
    wOK = new Button(shell, 8);
    wOK.setText(BaseMessages.getString(PKG, "System.Button.OK", new String[0]));
    wCancel = new Button(shell, 8);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel", new String[0]));
    setButtonPositions(new Button[] { wOK, wCancel }, margin, null);

    wlMetadata = new Label(shell, 0);
    wlMetadata.setText(BaseMessages.getString(PKG, "PdfMerge.Metadata.Label", new String[0]));
    props.setLook(wlMetadata);
    fdlMetadata = new FormData();
    fdlMetadata.left = new FormAttachment(0, 0);
    fdlMetadata.top = new FormAttachment(wKeepBookmarks, margin);
    wlMetadata.setLayoutData(fdlMetadata);

    metadataColumnInfo = getMetadataColumnsInfos();
    wMetadata = new TableView(transMeta, shell, 67586, metadataColumnInfo, 0, lsMod, props);
    wMetadata.optWidth(true);
    fdMetadata = new FormData();
    fdMetadata.left = new FormAttachment(0, 0);
    fdMetadata.top = new FormAttachment(wlMetadata, margin);
    fdMetadata.right = new FormAttachment(100, 0);
    fdMetadata.bottom = new FormAttachment(wOK, -2 * margin);
    fdMetadata.height = 75;
    wMetadata.setLayoutData(fdMetadata);

    lsOK = new Listener() {
      public void handleEvent(Event e) {
        PdfMergeDialog.this.ok();
      }
    };
    lsCancel = new Listener() {
      public void handleEvent(Event e) {
        PdfMergeDialog.this.cancel();
      }
    };
    wOK.addListener(13, lsOK);
    wCancel.addListener(13, lsCancel);

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        PdfMergeDialog.this.ok();
      }
    };
    wStepname.addSelectionListener(lsDef);

    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        PdfMergeDialog.this.cancel();
      }
    });
    loadFields();
    loadData();
    input.setChanged(changed);
    setSize();

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return stepname;
  }

  public void loadData() {
    if (input.getFileInputFieldName() != null) {
      wInputField.setText(input.getFileInputFieldName());
    }

    if (input.getFileOutputFieldName() != null) {
      wOutputField.setText(input.getFileOutputFieldName());
    }

    if (input.getFileInputSeparator() != null) {
      wInputSeparator.setText(input.getFileInputSeparator());
    }

    wKeepBookmarks.setSelection(input.isKeepBookmarks());

    Table metadataTable = wMetadata.table;
    if (input.getMetadata().size() > 0) {
      metadataTable.removeAll();
    }

    Iterator<String> metadataIt = input.getMetadata().keySet().iterator();
    int j = 0;
    while (metadataIt.hasNext()) {
      String key = (String) metadataIt.next();
      TableItem tableItem = new TableItem(metadataTable, 0);
      tableItem.setText(0, "" + (j + 1));
      tableItem.setText(1, key);
      tableItem.setText(2, (String) input.getMetadata().get(key));
    }

    wMetadata.setRowNums();
    wStepname.selectAll();
  }

  private void loadFields() {
    if (!gotPreviousFields) {
      try {
        String inputField = wInputField.getText();
        String outputField = wOutputField.getText();

        wInputField.removeAll();
        wOutputField.removeAll();

        RowMetaInterface r = transMeta.getPrevStepFields(stepname);
        if (r != null) {

          TreeSet<String> textFieldsTree = new TreeSet<String>();
          String[] fieldNames = r.getFieldNames();
          String[] fieldNamesAndTypes = r.getFieldNamesAndTypes(0);

          for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNamesAndTypes[i].toLowerCase().contains("string")) {
              textFieldsTree.add(fieldNames[i]);
            }
          }

          String[] textFields = (String[]) textFieldsTree.toArray(new String[0]);

          wInputField.setItems(textFields);
          wOutputField.setItems(textFields);
          metadataColumnInfo[0].setComboValues(new String[] { "Title", "Subject", "Keywords", "Creator", "Author" });
          metadataColumnInfo[1].setComboValues(textFields);
        }

        if (inputField != null) {
          wInputField.setText(inputField);
        }

        if (outputField != null) {
          wOutputField.setText(outputField);
        }

      } catch (KettleException ke) {
        new ErrorDialog(shell,
            BaseMessages.getString(PKG, "ChangeFileEncodingDialog.FailedToGetFields.DialogTitle", new String[0]),
            BaseMessages.getString(PKG, "ChangeFileEncodingDialog.FailedToGetFields.DialogMessage", new String[0]), ke);
      }

      gotPreviousFields = true;
    }
  }

  private void cancel() {
    stepname = null;
    input.setChanged(backupChanged);
    dispose();
  }

  private void ok() {
    stepname = wStepname.getText();
    input.setFileInputFieldName(wInputField.getText());
    input.setFileOutputFieldName(wOutputField.getText());
    input.setFileInputSeparator(wInputSeparator.getText());
    input.setKeepBookmarks(wKeepBookmarks.getSelection());

    int nrMetadata = wMetadata.nrNonEmpty();
    HashMap<String, String> metadata = new HashMap<String, String>();
    for (int i = 0; i < nrMetadata; i++) {
      TableItem tableItem = wMetadata.getNonEmpty(i);
      metadata.put(tableItem.getText(1), tableItem.getText(2));
    }

    input.setMetadata(metadata);

    dispose();
  }

  private ColumnInfo[] getMetadataColumnsInfos() {
    ColumnInfo keyColumnInfo = new ColumnInfo(
        BaseMessages.getString(PKG, "PdfMerge.Metadata.KeyColumn.Label", new String[0]), 2);
    keyColumnInfo.setReadOnly(false);
    keyColumnInfo.setToolTip(BaseMessages.getString(PKG, "PdfMerge.Metadata.KeyColumn.ToolTip", new String[0]));

    ColumnInfo fieldValueColumnInfo = new ColumnInfo(
        BaseMessages.getString(PKG, "PdfMerge.Metadata.FieldValueColumn.Label", new String[0]), 2);
    fieldValueColumnInfo.setReadOnly(true);
    fieldValueColumnInfo
        .setToolTip(BaseMessages.getString(PKG, "PdfMerge.Metadata.FieldValueColumn.ToolTip", new String[0]));

    return new ColumnInfo[] { keyColumnInfo, fieldValueColumnInfo };
  }
}
