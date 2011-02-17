//---------------------------------------------------------
//PDFReaderクラス
//
//UploadServerでPDFの内容を登録する時に使用する
//---------------------------------------------------------
package lse.crawler;

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
		FileInputStream pdfStream = null;
		PDDocument pdf = null;
		int COUNT = 200;
		try {
			pdfStream = new FileInputStream(filePath);
			PDFParser parser = new PDFParser(pdfStream);
			parser.parse();
			pdf = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			out = new ByteArrayOutputStream();
			stripper.writeText(pdf, new BufferedWriter(new OutputStreamWriter(out)));
			int beginIndex = 0;
			int endIndex = 0;
			System.out.println(out.toString().length());
			for (int i = 0; i < out.toString().length(); i++) {
				beginIndex = COUNT * i;
				endIndex = beginIndex + COUNT;
				if (out.toString().length() < endIndex) {
					endIndex = out.toString().length();
					System.out.println(beginIndex + " : " + endIndex);
					System.out.println(out.toString().substring(beginIndex, endIndex).trim());
					break;
				}
				System.out.println(beginIndex + " : " + endIndex);
				System.out.println(out.toString().substring(beginIndex, endIndex).trim());
			}
			//System.out.println(stripper.getText(pdf));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
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
		//文字の読み込み数
		int COUNT = 200;
		try {
			pdfStream = new FileInputStream(filePath);
			PDFParser parser = new PDFParser(pdfStream);
			parser.parse();
			pdf = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			out = new ByteArrayOutputStream();
			stripper.writeText(pdf, new BufferedWriter(new OutputStreamWriter(out)));
			int beginIndex = 0;
			int endIndex = 0;
			//一行ごと書き込み
			//System.out.println(out.toString().length());
			for (int i = 0; i < out.toString().length(); i++) {
				beginIndex = COUNT * i;
				endIndex = beginIndex + COUNT;
				if (out.toString().length() < endIndex) {
					endIndex = out.toString().length();
					//System.out.println(beginIndex + " : " + endIndex);
					//System.out.println(out.toString().substring(beginIndex, endIndex).trim());
					document.addField("text", out.toString().substring(beginIndex, endIndex).trim());
					//終了
					break;
				}
				//System.out.println(beginIndex + " : " + endIndex);
				//System.out.println(out.toString().substring(beginIndex, endIndex).trim());
				document.addField("text", out.toString().substring(beginIndex, endIndex).trim());
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//クローズ処理
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
