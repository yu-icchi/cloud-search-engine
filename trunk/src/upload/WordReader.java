//---------------------------------------------------------
//WordReaderクラス
//---------------------------------------------------------
package upload;

import java.io.FileInputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class WordReader {

	/**
	 * Word(.doc)からテキストを抽出する
	 *
	 * @param filePath
	 */
	static void extractDoc(String filePath) {

		try {
			FileInputStream fileStream = new FileInputStream(filePath);
			HWPFDocument doc = new HWPFDocument(fileStream);

			Range range = doc.getRange();

			for (int i = 0; i < range.numSections(); i++) {
				Section section = range.getSection(i);

				for (int j = 0; j < section.numParagraphs(); j++) {
					Paragraph paragraph = section.getParagraph(j);

					for (int k = 0; k < paragraph.numCharacterRuns(); k++) {
						CharacterRun ran = paragraph.getCharacterRun(k);
						String line = ran.text();
						System.out.println(line);
					}
				}
			}
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Word(.docx)からテキストを抽出する(未完成)
	 *
	 * @param filePath
	 */
	static void extractDocx(String filePath) {

		try {
			FileInputStream fileStream = new FileInputStream(filePath);

			//XWPFがDOCX拡張子に対応している
			XWPFDocument doc = new XWPFDocument(fileStream);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String file = "demo/sample.doc";
		extractDoc(file);
	}

}
