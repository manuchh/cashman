package wns.cashman.service;

import java.util.List;

import wns.cashman.domain.Cash;
import wns.cashman.enums.Denomination;
import wns.cashman.exception.InvalidAmountException;
import wns.cashman.model.BankNote;

/**
 * Service interface to support cash dispense functionality
 *
 * @author Sunil Chopra
 */
public interface CashDispenseService {
	
	public Boolean initialize(Cash p);
	
	public List<BankNote> withdraw(Integer amount) throws InvalidAmountException;
	
	public int getNumberOfAvailableBankNotes(Denomination bankNoteType);
	
	public int addBankNotes(Denomination bankNoteType, Integer number);

}
