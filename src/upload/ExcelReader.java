//---------------------------------------------------------
//ExcelReaderクラス
//---------------------------------------------------------
package upload;

import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelReader {

	/**
	 * Excelからテキストを抽出する (未完成)
	 *
	 * @param filePath
	 */
	static void extractText(String filePath) {
		try {
			//Excelを読み込む
			POIFSFileSystem file = new POIFSFileSystem(new FileInputStream(filePath));
			HSSFWorkbook wb = new HSSFWorkbook(file);

			//シートを読み込む
			HSSFSheet sheet = wb.getSheet("Sheet1");



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}
}
