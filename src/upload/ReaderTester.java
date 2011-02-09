package upload;

public class ReaderTester {

	public static void main(String[] args) {

		//TextFileReader readerText = new TextFileReader();
		//System.out.println(readerText.extractText("demo/data.txt"));

		//WordReader readerDoc = new WordReader();
		//System.out.println(readerDoc.extractDoc("demo/sample.doc"));

		PDFReader readerPDF = new PDFReader();
		readerPDF.extractPDF("demo/DPSWS1992.pdf");

		//PowerPointReader readerPPT = new PowerPointReader();
		//System.out.println(readerPPT.extractPPT("demo/sample.ppt"));
	}
}
