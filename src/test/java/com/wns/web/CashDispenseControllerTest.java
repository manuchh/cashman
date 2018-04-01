package com.wns.web;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import wns.cashman.CashManApplication;
import wns.cashman.controller.CashDispenseCommand;
import wns.cashman.enums.Denomination;
import wns.cashman.exception.InvalidAmountException;
import wns.cashman.model.BankNote;
import wns.cashman.service.CashDispenseService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={CashManApplication.class})
public class CashDispenseControllerTest {
	
	MockMvc mockMvc;
	
	@Autowired 
	ObjectMapper objectMapper;
	
	@Autowired
    private WebApplicationContext webApplicationContext;

	@MockBean
	CashDispenseService cashDispenseServiceMock;
	
	CashDispenseCommand command = null;
	Denomination noteType = null;
	List<BankNote> bankNotes = null;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		noteType = Denomination.FIFTY;
		
		command = new CashDispenseCommand();
		command.setNoteTypeOfCheckBal("50");
		command.setNoteTypeOfLoadCash("50");
		command.setAmount("100");
		
		bankNotes = new ArrayList<BankNote>(2);
		BankNote fifty = new BankNote(Denomination.FIFTY, 10);
		BankNote twenty = new BankNote(Denomination.TWENTY, 10);
		bankNotes.add(fifty);
		bankNotes.add(twenty);
	}

    @Test
    public void testcheckBalance() throws Exception {
    	given(cashDispenseServiceMock.getNumberOfAvailableBankNotes(noteType)).willReturn(10);
    	mockMvc.perform(post("/cash/submit?action=checkBalance")
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"noteTypeOfCheckBal", "50",
        			"number", "10",
        			"amount", "100",
        			"noteTypeOfLoadCash", "50",
        			"submitAction", "checkBalance"
      			)))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testWithdraw() throws Exception {
    	command.setSubmitAction("withdraw");
    	given(cashDispenseServiceMock.withdraw(100)).willReturn(bankNotes);
    	mockMvc.perform(post("/cash/submit?action=withdraw")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"amount", "100",
        			"submitAction", "withdraw"
      			)))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testWithdrawWithValidationErrorNegativeAmount() throws Exception {
    	command.setSubmitAction("withdraw");
    	given(cashDispenseServiceMock.withdraw(100)).willReturn(bankNotes);
    	mockMvc.perform(post("/cash/submit?action=withdraw")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"amount", "-10",
        			"submitAction", "withdraw"
      			)))
    		.andExpect(content().string(Matchers.containsString("Amount should be positive numeric value")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testWithdrawWithValidationErrorBlankAmount() throws Exception {
    	command.setSubmitAction("withdraw");
    	given(cashDispenseServiceMock.withdraw(100)).willReturn(bankNotes);
    	mockMvc.perform(post("/cash/submit?action=withdraw")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"amount", "",
        			"submitAction", "withdraw"
      			)))
    		.andExpect(content().string(Matchers.containsString("Please provide the amount to withdraw")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testWithdrawWithValidationErrorInvalidNumber() throws Exception {
    	command.setSubmitAction("withdraw");
    	given(cashDispenseServiceMock.withdraw(100)).willReturn(bankNotes);
    	mockMvc.perform(post("/cash/submit?action=withdraw")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"amount", "abcd",
        			"submitAction", "withdraw"
      			)))
    		.andExpect(content().string(Matchers.containsString("Amount should be positive numeric value")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testWithdrawWithServiceException() throws Exception {
    	command.setSubmitAction("withdraw");
    	given(cashDispenseServiceMock.withdraw(100)).willThrow(new InvalidAmountException("Invalid amount"));
    	mockMvc.perform(post("/cash/submit?action=withdraw")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"amount", "100",
        			"submitAction", "withdraw"
      			)))
    		.andExpect(content().string(Matchers.containsString("Invalid amount")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testLoadCash() throws Exception {
    	command.setSubmitAction("loadCash");
    	given(cashDispenseServiceMock.addBankNotes(noteType, 10)).willReturn(20);
    	mockMvc.perform(post("/cash/submit?action=loadCash")
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"number", "10",
        			"noteTypeOfLoadCash", "50",
        			"submitAction", "loadCash"
      			)))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testLoadCashWithValidationErrorNegativeValue() throws Exception {
    	command.setSubmitAction("loadCash");
    	given(cashDispenseServiceMock.addBankNotes(noteType, 10)).willReturn(20);
    	mockMvc.perform(post("/cash/submit?action=loadCash")
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"number", "-10",
        			"noteTypeOfLoadCash", "50",
        			"submitAction", "loadCash"
      			)))
    		.andExpect(content().string(Matchers.containsString("Number should be positive numeric value")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testLoadCashWithValidationErrorBlankValue() throws Exception {
    	command.setSubmitAction("loadCash");
    	given(cashDispenseServiceMock.addBankNotes(noteType, 10)).willReturn(20);
    	mockMvc.perform(post("/cash/submit?action=loadCash")
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"number", "",
        			"noteTypeOfLoadCash", "50",
        			"submitAction", "loadCash"
      			)))
    		.andExpect(content().string(Matchers.containsString("Please provide the number of notes to be loaded")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
    
    @Test
    public void testLoadCashWithValidationErrorInvalidNumber() throws Exception {
    	command.setSubmitAction("loadCash");
    	given(cashDispenseServiceMock.addBankNotes(noteType, 10)).willReturn(20);
    	mockMvc.perform(post("/cash/submit?action=loadCash")
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"number", "abcd",
        			"noteTypeOfLoadCash", "50",
        			"submitAction", "loadCash"
      			)))
    		.andExpect(content().string(Matchers.containsString("Number should be positive numeric value")))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andDo(print());
    }
	private String buildUrlEncodedFormEntity(String... params) {
	   if ( (params.length%2) > 0) {
	      throw new IllegalArgumentException("Need to give an even number of parameters");
	   }
	   StringBuilder result = new StringBuilder();
	   for (int i = 0; i < params.length; i += 2) {
	      if (i > 0) result.append('&');
	      result.append(URLEncoder.encode(params[i])).append('=').append(URLEncoder.encode(params[i+1]));
	   }
	   return result.toString();
    }

}
