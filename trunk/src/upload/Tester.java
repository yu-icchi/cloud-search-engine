package upload;

public class Tester {

	public static void main(String[] args) {

		//TextFileReader readerText = new TextFileReader();
		//System.out.println(readerText.extractText("demo/kumofs.txt"));

		//WordReader readerDoc = new WordReader();
		//System.out.println(readerDoc.extractDoc("demo/sample.doc"));

		//PDFReader readerPDF = new PDFReader();
		//System.out.println(readerPDF.extractPDF("demo/a2-mapion.pdf"));

		PowerPointReader readerPPT = new PowerPointReader();
		System.out.println(readerPPT.extractPPT("demo/sample.ppt"));
	}
}
