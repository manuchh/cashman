package com.wns.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
import wns.cashman.domain.Cash;
import wns.cashman.domain.Money;
import wns.cashman.enums.Denomination;
import wns.cashman.model.BankNote;
import wns.cashman.service.CashDispenseService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={CashManApplication.class})
public class HomeControllerTest {

	private MockMvc mockMvc;
	
	@Autowired 
	private ObjectMapper objectMapper;
	
	@Autowired
    private WebApplicationContext webApplicationContext;

	@MockBean
	CashDispenseService cashDispenseServiceMock;
	
	Money money = null;
	Cash cash = null;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		money = new Money();
		money.setFifties(10);
		money.setTwenties(10);
		
		List<BankNote> bankNotes = new ArrayList<BankNote>(2);
		BankNote fifty = new BankNote(Denomination.FIFTY, money.getFifties());
		BankNote twenty = new BankNote(Denomination.TWENTY, money.getTwenties());
		bankNotes.add(fifty);
		bankNotes.add(twenty);
		cash = new Cash(bankNotes);
	}

	@Test
	public void testIndex() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andDo(print());
	}
	
	@Test
	public void testInitializationGet() throws Exception {
		this.mockMvc.perform(get("/initialize"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andDo(print());
	}
	
    @Test
    public void testMachineInitializedSuccessfully() throws Exception {
    	given(cashDispenseServiceMock.initialize(cash)).willReturn(true);
    	mockMvc.perform(post("/initialize")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.content(buildUrlEncodedFormEntity(
        			"twenties", "10",
        			"fifties", "10"
      			)))
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
