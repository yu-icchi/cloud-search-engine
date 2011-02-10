//---------------------------------------------------------
//PDFReaderクラス
//
//UploadServerでPDFの内容を登録する時に使用する
//---------------------------------------------------------
package upload;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.solr.common.SolrInputDocument;

public class PDFReader {

	/**
	 *PDFからテキストを抽出する
	 * @param filePath
	 * @return
	 */
	public String extractPDF(String filePath) {
		ByteArrayOutputStream out = null;
		try {
			FileInputStream pdfStream = new FileInputStream(filePath);
			PDFParser parser = new PDFParser(pdfStream);
			parser.parse();
			PDDocument pdf = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			out = new ByteArrayOutputStream();
			stripper.writeText(pdf, new BufferedWriter(new OutputStreamWriter(out)));
			System.out.println(stripper.getText(pdf));
			pdfStream.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	/**
	 *
	 * @param filePath
	 * @param document
	 * @return
	 */
	public SolrInputDocument extractPDF(String filePath, SolrInputDocument document) {
		ByteArrayOutputStream out = null;
		FileInputStream pdfStream = null;
		PDDocument pdf = null;
		try {
			pdfStream = new FileInputStream(filePath);
			PDFParser parser = new PDFParser(pdfStream);
			parser.parse();
			pdf = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			out = new ByteArrayOutputStream();
			stripper.writeText(pdf, new BufferedWriter(new OutputStreamWriter(out)));
			document.addField("text", out.toString());
			//document.addField("text", stripper.getText(pdf));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pdf.close();
				pdfStream.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}
}
