package com.wns.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import wns.cashman.domain.Cash;
import wns.cashman.enums.Denomination;
import wns.cashman.exception.InvalidAmountException;
import wns.cashman.model.BankNote;
import wns.cashman.repository.NoteRepository;
import wns.cashman.service.CashDispenseService;
import wns.cashman.service.CashDispenseServiceImpl;

public class CashDispenseServiceTest {
	
    private CashDispenseService cashDispenseService;
    private NoteRepository noteRepositoryMock;
    
    Cash cash = null;
    List<BankNote> bankNotes = null;

    @Before
    public void setUp() {
    	noteRepositoryMock = Mockito.mock(NoteRepository.class);
    	cashDispenseService = new CashDispenseServiceImpl(noteRepositoryMock);
    	
    	bankNotes = new ArrayList<BankNote>(2);
		BankNote fifty = new BankNote(Denomination.FIFTY, 10);
		BankNote twenty = new BankNote(Denomination.TWENTY, 10);
		bankNotes.add(fifty);
		bankNotes.add(twenty);
		cash = new Cash(bankNotes);
    }

    @Test
    public void testInitializeSuccessfuly() throws Exception {
    	given(noteRepositoryMock.countByType(Denomination.FIFTY)).willReturn(0);
    	given(noteRepositoryMock.countByType(Denomination.TWENTY)).willReturn(0);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        boolean successFlag = cashDispenseService.initialize(cash);
        assertEquals(true, successFlag);
    }
    
    @Test
    public void testAlreadyIinitialized() throws Exception {
    	given(noteRepositoryMock.countByType(Denomination.FIFTY)).willReturn(10);
    	given(noteRepositoryMock.countByType(Denomination.TWENTY)).willReturn(10);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        boolean successFlag = cashDispenseService.initialize(cash);
        assertEquals(false, successFlag);
    }
    
    @Test
    public void testWithdraw() throws Exception {
    	given(noteRepositoryMock.findAll()).willReturn(bankNotes);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        List<BankNote> bankNotes = cashDispenseService.withdraw(120);
        assertNotNull(bankNotes);
    }
    
    @Test(expected = InvalidAmountException.class)
    public void testWithdrawInvalidAmount() throws Exception {
    	given(noteRepositoryMock.findAll()).willReturn(bankNotes);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        List<BankNote> bankNotes = cashDispenseService.withdraw(0);
    }
    
    @Test(expected = InvalidAmountException.class)
    public void testWithdrawAmountMoreThanAvailable() throws Exception {
    	given(noteRepositoryMock.findAll()).willReturn(bankNotes);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        List<BankNote> bankNotes = cashDispenseService.withdraw(1000);
    }
    
    @Test(expected = InvalidAmountException.class)
    public void testWithdrawLessNotesFor200() throws Exception {
    	bankNotes = new ArrayList<BankNote>(2);
		BankNote fifty = new BankNote(Denomination.FIFTY, 2);
		BankNote twenty = new BankNote(Denomination.TWENTY, 7);
		bankNotes.add(fifty);
		bankNotes.add(twenty);
    	given(noteRepositoryMock.findAll()).willReturn(bankNotes);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        List<BankNote> bankNotes = cashDispenseService.withdraw(200);
    }
    
    @Test(expected = InvalidAmountException.class)
    public void testWithdrawNoPossibleCombination() throws Exception {
    	given(noteRepositoryMock.findAll()).willReturn(bankNotes);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        List<BankNote> bankNotes = cashDispenseService.withdraw(30);
    }
    
    @Test
    public void testEmailSendingWhenBalanceIsLow() throws Exception {
    	bankNotes = new ArrayList<BankNote>(2);
		BankNote fifty = new BankNote(Denomination.FIFTY, 2);
		BankNote twenty = new BankNote(Denomination.TWENTY, 2);
		bankNotes.add(fifty);
		bankNotes.add(twenty);
    	given(noteRepositoryMock.findAll()).willReturn(bankNotes);
        doAnswer(returnsFirstArg()).when(noteRepositoryMock).save(any(BankNote.class));
        List<BankNote> bankNotes = cashDispenseService.withdraw(100);
    }

    @Test
    public void testcheckBalance() throws Exception {
    	given(noteRepositoryMock.findByType(Denomination.FIFTY)).willReturn(new BankNote(Denomination.FIFTY, 10));
        int number = cashDispenseService.getNumberOfAvailableBankNotes(Denomination.FIFTY);
        assertEquals(10, number);
    }
    
    @Test
    public void testLoadCash() throws Exception {
    	given(noteRepositoryMock.findByType(Denomination.FIFTY)).willReturn(new BankNote(Denomination.FIFTY, 10));
        int number = cashDispenseService.addBankNotes(Denomination.FIFTY, 10);
        assertEquals(20, number);
    }
}
