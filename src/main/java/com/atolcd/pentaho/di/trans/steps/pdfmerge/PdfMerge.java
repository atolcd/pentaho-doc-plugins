package com.atolcd.pentaho.di.trans.steps.pdfmerge;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.SimpleBookmark;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class PdfMerge extends BaseStep implements StepInterface {
  private PdfMergeData data;
  private PdfMergeMeta meta;

  public PdfMerge(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
    super(s, stepDataInterface, c, t, dis);
  }

  public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
    meta = ((PdfMergeMeta) smi);
    data = ((PdfMergeData) sdi);

    Object[] r = getRow();
    if (r == null) {
      setOutputDone();
      return false;
    }

    if (first) {
      first = false;

      data.outputRowMeta = getInputRowMeta().clone();
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

      data.inputFieldPosition = Integer.valueOf(getInputRowMeta().indexOfValue(meta.getFileInputFieldName()));
      data.outputFieldPosition = Integer.valueOf(getInputRowMeta().indexOfValue(meta.getFileOutputFieldName()));

      logBasic("Initialized successfully");
    }

    try {
      List<SimpleBookmark> boorkmarks = new ArrayList<SimpleBookmark>();
      int shiftPage = 0;

      String outputFilename = checkFilename(data.outputRowMeta.getString(r, data.outputFieldPosition.intValue())).getFile();
      logBasic("Writing " + outputFilename);
      PdfCopyFields pdfCopyFields = new PdfCopyFields(new FileOutputStream(outputFilename));
      String[] inputFilenames = data.outputRowMeta.getString(r, data.inputFieldPosition.intValue())
          .split(meta.getFileInputSeparator());
      for (int i = 0; i < inputFilenames.length; i++) {
        String inputFilename = checkFilename(inputFilenames[i].trim()).getFile();
        PdfReader pdfReader = new PdfReader(inputFilename);

        if ((meta.isKeepBookmarks()) && (SimpleBookmark.getBookmark(pdfReader) != null)) {
          List<SimpleBookmark> tmpBoorkmarks = new ArrayList<SimpleBookmark>();
          for (Object bookmark : SimpleBookmark.getBookmark(pdfReader)) {
            tmpBoorkmarks.add((SimpleBookmark) bookmark);
          }
          SimpleBookmark.shiftPageNumbers(tmpBoorkmarks, shiftPage, null);
          boorkmarks.addAll(tmpBoorkmarks);
        }

        pdfCopyFields.addDocument(pdfReader);

        shiftPage += pdfReader.getNumberOfPages();
      }

      if (meta.isKeepBookmarks()) {
        pdfCopyFields.getWriter().setOutlines(boorkmarks);
      }

      HashMap<String, String> metadata = meta.getMetadata();
      Iterator<String> metadataIt = metadata.keySet().iterator();
      while (metadataIt.hasNext()) {
        String key = (String) metadataIt.next();
        Integer valuePosition = Integer.valueOf(getInputRowMeta().indexOfValue((String) metadata.get(key)));
        String value = data.outputRowMeta.getString(r, valuePosition.intValue());
        pdfCopyFields.getWriter().getInfo().put(new PdfName(key), new PdfString(value));
      }

      pdfCopyFields.close();
    } catch (FileNotFoundException e) {
      throw new KettleException("", e);
    } catch (DocumentException e) {
      throw new KettleException("", e);
    } catch (IOException e) {
      throw new KettleException("", e);
    }

    putRow(data.outputRowMeta, r);

    if (checkFeedback(getLinesRead())) {
      logBasic("Linenr " + getLinesRead());
    }

    return true;
  }

  public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
    meta = ((PdfMergeMeta) smi);
    data = ((PdfMergeData) sdi);

    return super.init(smi, sdi);
  }

  public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
    meta = ((PdfMergeMeta) smi);
    data = ((PdfMergeData) sdi);

    super.dispose(smi, sdi);
  }

  public void run() {
    logBasic("Starting to run...");
    try {
      do {
        if (!processRow(meta, data))
          break;
      } while (!isStopped());
    } catch (Exception e) {
      logError("Unexpected error : " + e.toString());
      logError(org.pentaho.di.core.Const.getStackTracker(e));
      setErrors(1L);
      stopAll();
    } finally {
      dispose(meta, data);
      logBasic("Finished, processing " + getLinesRead() + " rows");
      markStop();
    }
  }

  private URL checkFilename(String filename) throws KettleException {
    try {
      return KettleVFS.getFileObject(filename).getURL();
    } catch (KettleFileException e) {
      throw new KettleException("", e);
    } catch (Exception e) {
      throw new KettleException("", e);
    }
  }
}
