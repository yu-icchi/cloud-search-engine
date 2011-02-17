//---------------------------------------------------------
//PowerPointReaderクラス
//---------------------------------------------------------
package lse.crawler;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.solr.common.SolrInputDocument;

public class PowerPointReader {

	/**
	 * Powerpoint(.ppt)からテキストを抽出する(未完成:一部の文字しか読み取れない)
	 *
	 * @param filePath
	 * @return
	 */
	public String extractPPT(String filePath) {

		String line = "";

		try {
			//読み込み
			SlideShow slideShow = new SlideShow(new HSLFSlideShow(filePath));
			Slide[] slides = slideShow.getSlides();

			for (int i = 0; i < slides.length; i++) {

				Shape[] shapes = slides[i].getShapes();

				for (int j = 0; j < shapes.length; j++) {

					if (shapes[j] instanceof TextBox) {
						TextBox shape = (TextBox) shapes[j];
						String text = shape.getText();
						if (text != null) {
							line += text.trim();
						}
					}
				}
			}
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
	public SolrInputDocument extractPPT(String filePath, SolrInputDocument document) {
		try {
			//読み込み
			SlideShow slideShow = new SlideShow(new HSLFSlideShow(filePath));
			Slide[] slides = slideShow.getSlides();

			for (int i = 0; i < slides.length; i++) {

				Shape[] shapes = slides[i].getShapes();

				for (int j = 0; j < shapes.length; j++) {

					if (shapes[j] instanceof TextBox) {
						TextBox shape = (TextBox) shapes[j];
						String text = shape.getText();
						if (text != null) {
							if (!text.trim().isEmpty()) {
								document.addField("text", text.trim());
							}
						}
					}
				}
			}
			return document;
		} catch (Exception e) {
			e.printStackTrace();
			return document;
		}
	}

}
