package com.atolcd.document.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.lang3.ArrayUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleBookmark;

public class PdfUtil {

	/**
	 * Merge PDF files in one file. File order = page order
	 * @param PDF input file names
	 * @param PDF output file name
	 * @param Keep PDF bookmarks and actualise
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public void mergeFiles(String[] inputPdfFilenames, String outputPdfFilename, boolean keepBookmarks) throws DocumentException, IOException{

		List<SimpleBookmark> boorkmarks = new ArrayList<SimpleBookmark>();
		int shiftPage = 0;

		FileOutputStream pdfFileOutputStream = new FileOutputStream(outputPdfFilename);
		PdfCopyFields pdfCopyFields = new PdfCopyFields(pdfFileOutputStream);
		for (String inputPdfFilename : inputPdfFilenames){

			PdfReader pdfReader = new PdfReader(inputPdfFilename.trim());

			//If keep bookmarks and has bookmarks
			if(keepBookmarks && SimpleBookmark.getBookmark(pdfReader)!=null){

				@SuppressWarnings("unchecked")
				List<SimpleBookmark> tmpBoorkmarks = SimpleBookmark.getBookmark(pdfReader);
				SimpleBookmark.shiftPageNumbers(tmpBoorkmarks, shiftPage, null);
				boorkmarks.addAll(tmpBoorkmarks);
			}

			pdfCopyFields.addDocument(pdfReader);
			shiftPage += pdfReader.getNumberOfPages();

		}

		if(!boorkmarks.isEmpty()){
			pdfCopyFields.getWriter().setOutlines(boorkmarks);
		}

		pdfCopyFields.close();
		pdfFileOutputStream.close();

	}
	
	/**
	 * Extract pages from PDF file into no one
	 * @param PDF input file name
	 * @param PDF output file names
	 * @param Extract pattern : 1,5-7,12-9,4 -> Extract pages in this order 1, and 5,6,7, and 12,11,10,9 and 4
	 * @throws IOException
	 * @throws PdfExtractPatternException
	 * @throws DocumentException
	 */
	public void extractPages(String inputPdfFilename, String outputPdfFilename, String extractPattern) throws IOException, PdfExtractPatternException, DocumentException {
		
		PdfReader pdfReader = new PdfReader(inputPdfFilename);

		Document document = new Document();
		FileOutputStream pdfFileOutputStream = new FileOutputStream(outputPdfFilename);
		PdfWriter pdfWriter = PdfWriter.getInstance(document, pdfFileOutputStream);
		document.open();

		PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
		PdfImportedPage importedPage;
		for(int pageNum : parseExtractPattern(pdfReader.getNumberOfPages(),extractPattern)){
			document.newPage();
			importedPage = pdfWriter.getImportedPage(pdfReader, pageNum);
			pdfContentByte.addTemplate(importedPage, 0, 0);
		}
		
		//Close document and outputStream.
		pdfFileOutputStream.flush();
		document.close();
		pdfReader.close();
		pdfFileOutputStream.close();

	}
	
	/**
	 * Retourn page number if is valid
	 * @param document page count
	 * @param page number
	 * @return
	 * @throws PdfExtractPatternException
	 */
	private int parsePageNumber(int pageCount, String pageNumber) throws PdfExtractPatternException{

		try{

			int page = Integer.parseInt(pageNumber.trim());
			if(page <=0 || page > pageCount){
				throw new PdfExtractPatternException();
			}

			return page;


		}catch(Exception e){
			throw new PdfExtractPatternException();
		}

	}
	
	
	/**
	 * Convert extract pattern to page number list
	 * @param pageCount
	 * @param extractPattern
	 * @return
	 * @throws PdfExtractPatternException
	 */
	private /*int[]*/Integer[] parseExtractPattern(int pageCount, String extractPattern) throws PdfExtractPatternException{

		List<Integer> pagesToExtract = new ArrayList<Integer>();
		for(String sequence: extractPattern.split(",")){

			//Specific page
			if(!sequence.contains("-")){

				pagesToExtract.add(parsePageNumber(pageCount, sequence));

				//Pages from - to
			}else{

				String[] pages = sequence.split("-");
				if(pages.length != 2){
					throw new PdfExtractPatternException();
				}

				int fromPage = parsePageNumber(pageCount, pages[0]);
				int toPage = parsePageNumber(pageCount, pages[1]);
				if(toPage < fromPage){

					while(fromPage >= toPage) {
						pagesToExtract.add(fromPage);
						fromPage--;
					}

				}else{

					while(fromPage <= toPage) {
						pagesToExtract.add(fromPage);
						fromPage++;
					}

				}
			}

		}

		//return ArrayUtils.toPrimitive(pagesToExtract.toArray(new Integer[pagesToExtract.size()]));
                return pagesToExtract.toArray(new Integer[pagesToExtract.size()]);
	}

	
	@SuppressWarnings("serial")
	public class PdfExtractPatternException extends Exception{ 

		public PdfExtractPatternException(){
			super("Invalid page selection sequence");
		}  

	}
	
	/**
	 * Attach files to pdf
	 * @param Input pdf file name
	 * @param Output pdf file name
	 * @param List of files to attach
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void attachFiles(String inputPdfFilename, String outputPdfFilename, String[] inputAttachmentFilenames) throws DocumentException, IOException{
		
		PdfReader pdfReader = new PdfReader(inputPdfFilename);
		FileOutputStream pdfFileOutputStream = new FileOutputStream(outputPdfFilename);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, pdfFileOutputStream);
		
		for (String inputAttachmentFilename : inputAttachmentFilenames){

			PdfFileSpecification attachment = PdfFileSpecification.fileEmbedded(
				pdfStamper.getWriter(),
				inputAttachmentFilename.trim(),
				inputAttachmentFilename.trim(),
				null
			);

			pdfStamper.addFileAttachment("", attachment);

		}
		
		pdfStamper.setViewerPreferences(PdfWriter.PageModeUseAttachments);
		
		pdfStamper.close();
		pdfReader.close();
		pdfFileOutputStream.close();

	}
		
	/*public void addSignature(String inputPdfFilename, String outputPdfFilename, String inputPfxFilename, String pfxPassword,) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException, UnrecoverableKeyException{
		
		//Certificat
		FileInputStream pfxInpuStream = new FileInputStream(inputPfxFilename);
		KeyStore keyStore = KeyStore.getInstance("pkcs12");
		keyStore.load(pfxInpuStream, pfxPassword.toCharArray());
		String alias = (String)keyStore.aliases().nextElement();
		pfxInpuStream.close();
		
		// Recupération de la clef privée et de la chaine de certificats
		PrivateKey certKey = (PrivateKey)keyStore.getKey(alias, pfxPassword.toCharArray());
		Certificate[] certChain = keyStore.getCertificateChain(alias);
		 
		// Lecture du document source
		PdfReader pdfReader = new PdfReader(inputPdfFilename);
		
		//Signature du document
		PdfStamper pdfStamper = PdfStamper.createSignature(pdfReader, null, '\0', new File(outputPdfFilename));
		PdfSignatureAppearance pdfSignatureAppearance = pdfStamper.getSignatureAppearance();
		//pdfSignatureAppearance.setCrypto(certKey, certChain, null, PdfSignatureAppearance.VERISIGN_SIGNED);
		switch (appearance) {
		
		
		
		}
			
		
		pdfSignatureAppearance.setCrypto(certKey, certChain, null, PdfSignatureAppearance.VERISIGN_SIGNED);
		pdfStamper.setFormFlattening(true);
		pdfStamper.close();
		pdfReader.close();
	}*/

}
