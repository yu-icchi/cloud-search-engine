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
import org.apache.solr.common.SolrInputDocument;

public class WordReader {

	/**
	 * Word(.doc)からテキストを抽出する
	 *
	 * @param filePath
	 * @return
	 */
	public String extractDoc(String filePath) {

		String line = "";

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
						String tmp = ran.text();
						//trimメソッドでいらないモノを削除する
						line += tmp.trim();
					}
				}
			}
			fileStream.close();
			return line;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 *
	 * @param filePath
	 * @param document
	 * @return
	 */
	public SolrInputDocument extractDoc(String filePath, SolrInputDocument document) {
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
						String tmp = ran.text();
						//trimメソッドでいらないモノを削除する
						if (!tmp.trim().isEmpty()) {
							document.addField("text", tmp.trim());
						}
					}
				}
			}
			fileStream.close();

			return document;
		} catch (Exception e) {
			e.printStackTrace();
			return document;
		}
	}

}
