package wns.cashman.controller;

public class CashDispenseCommand {

	private String noteTypeOfCheckBal;
	private String number;
	private String amount;
	private String noteTypeOfLoadCash;
	private String submitAction;

	public String getNoteTypeOfCheckBal() {
		return noteTypeOfCheckBal;
	}

	public void setNoteTypeOfCheckBal(String noteTypeOfCheckBal) {
		this.noteTypeOfCheckBal = noteTypeOfCheckBal;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getNoteTypeOfLoadCash() {
		return noteTypeOfLoadCash;
	}

	public void setNoteTypeOfLoadCash(String noteTypeOfLoadCash) {
		this.noteTypeOfLoadCash = noteTypeOfLoadCash;
	}

	public String getSubmitAction() {
		return submitAction;
	}

	public void setSubmitAction(String submitAction) {
		this.submitAction = submitAction;
	}

}
