package analysis;

import java.util.ArrayList;

import net.java.sen.StringTagger;
import net.java.sen.Token;

public class SenAnalyzerExtract {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	private ArrayList<String> stringArray = new ArrayList<String>();

	//-----------------------------------------------------
	//コンストラクタ
	//-----------------------------------------------------

	/**
	 * コンストラクタ
	 */
	public SenAnalyzerExtract() {
		System.setProperty("sen.home", "lib/sen");
	}

	/**
	 *
	 * @param array
	 */
	public SenAnalyzerExtract(ArrayList<String> array) {
		System.setProperty("sen.home", "lib/sen");
		this.setStringArray(array);
	}

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------

	public void setStringArray(ArrayList<String> stringArray) {
		this.stringArray = stringArray;
	}

	public ArrayList<String> getStringArray() {
		return stringArray;
	}

	//-----------------------------------------------------
	//
	//-----------------------------------------------------

	public ArrayList<String> extract() throws Exception {

		ArrayList<String> array = new ArrayList<String>();

		StringTagger tagger = StringTagger.getInstance();

		for (String str : this.stringArray) {
			Token[] token = tagger.analyze(str);
			for (int i = 0; i < token.length; i++) {
				array.add(token[i].getBasicString());
			}
		}

		return array;
	}

	/**
	 *
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String qbssExtract(String value) throws Exception {

		ArrayList<String> list = new ArrayList<String>();

		StringTagger tagger = StringTagger.getInstance();

		Token[] token = tagger.analyze(value);
		for (int i = 0; i < token.length; i++) {
			list.add(token[i].getBasicString());
		}

		String result = "(";

		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				result += list.get(i) + " AND ";
			} else {
				result += list.get(i) + ")";
			}
		}

		return result;
	}

}
