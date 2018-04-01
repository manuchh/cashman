package wns.cashman.exception;

public class InvalidAmountException extends Exception {

	private static final long serialVersionUID = 3618563547246689866L;

	public InvalidAmountException() {
		super();
	}

	public InvalidAmountException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public InvalidAmountException(String message) {
		super(message);
	}

	public InvalidAmountException(Throwable throwable) {
		super(throwable);
	}

}
