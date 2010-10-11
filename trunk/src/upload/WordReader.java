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

public class WordReader {

	/**
	 * Wordからテキストを抽出する
	 *
	 * @param filePath
	 */
	static void extractText(String filePath) {

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

	public static void main(String[] args) {
		String file = "demo/sample.doc";
		extractText(file);
	}

}
