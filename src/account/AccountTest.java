package account;

public class AccountTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Account account = new Account("test", "sample");
		account.accountSet();
		account.accountGet();

	}

}
