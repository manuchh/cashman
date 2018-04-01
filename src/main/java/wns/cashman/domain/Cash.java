package wns.cashman.domain;

import java.util.List;

import wns.cashman.model.BankNote;

public class Cash {

	List<BankNote> money;

	public Cash(List<BankNote> money) {
		this.money = money;
	}

	public List<BankNote> getMoney() {
		return money;
	}

	public void setMoney(List<BankNote> money) {
		this.money = money;
	}
	
}
