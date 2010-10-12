//---------------------------------------------------------
//PowerPointReaderクラス
//---------------------------------------------------------
package upload;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.usermodel.SlideShow;

public class PowerPointReader {

	/**
	 * Powerpoint(.ppt)からテキストを抽出する(未完成:一部の文字しか読み取れない)
	 *
	 * @param filePath
	 */
	static void extractPPT(String filePath) {
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
							System.out.println(text);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String file = "demo/sample.ppt";
		extractPPT(file);
	}
}
