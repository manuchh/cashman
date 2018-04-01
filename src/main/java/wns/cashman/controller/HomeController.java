package wns.cashman.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import wns.cashman.domain.Cash;
import wns.cashman.domain.Money;
import wns.cashman.enums.Denomination;
import wns.cashman.model.BankNote;
import wns.cashman.service.CashDispenseService;

/**
 * Controller to support initialization of Cash Dispensing Machine
 *
 * @author Sunil Chopra
 */
@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	private static String MACHINE_INITIALIZED = "MACHINE_INITIALIZED";

	CashDispenseService cashDispenseService;
	
	public HomeController() {}

	@Autowired
	public HomeController(CashDispenseService cashDispenseService) {
		this.cashDispenseService = cashDispenseService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String load(Model model, HttpServletRequest request) {
		String message = "Machine has already been initialized";
		if("true".equals(request.getSession().getAttribute(MACHINE_INITIALIZED))) {
			model.addAttribute("initialized", true);
			model.addAttribute("message", message);
			model.addAttribute("command", new CashDispenseCommand());
			return "home";
		}
		model.addAttribute("initialized", false);
		model.addAttribute("money", new Money());
		model.addAttribute("message", "");
		return "index";
	}

	@RequestMapping(value = "/initialize", method = RequestMethod.POST)
	public String initialize(@ModelAttribute Money money, HttpServletRequest request, BindingResult errors, Model model) {
		String message = "Machine has already been initialized, new request ignored";
		List<BankNote> bankNotes = new ArrayList<BankNote>(2);
		BankNote fifty = new BankNote(Denomination.FIFTY, money.getFifties());
		BankNote twenty = new BankNote(Denomination.TWENTY, money.getTwenties());
		bankNotes.add(fifty);
		bankNotes.add(twenty);
		Cash cash = new Cash(bankNotes);
		Boolean success = cashDispenseService.initialize(cash);
		if (success) {
			message = "Machine has been initialized with " + fifty.getNumber() + " Fifty AUD notes and "
					+ twenty.getNumber() + " Twenty AUD notes";
			request.getSession().setAttribute("MACHINE_INITIALIZED", "true");
		}
		model.addAttribute("initialized", true);
		model.addAttribute("message", message);
		model.addAttribute("command", new CashDispenseCommand());
		return "home";
	}
	
	@RequestMapping(value = "/initialize", method = RequestMethod.GET)
	public String initializeGet(Model model, HttpServletRequest request) {
		String message = "Machine has already been initialized";
		if("true".equals(request.getSession().getAttribute(MACHINE_INITIALIZED))) {
			model.addAttribute("initialized", true);
			model.addAttribute("message", message);
			model.addAttribute("command", new CashDispenseCommand());
			return "home";
		}
		model.addAttribute("initialized", false);
		model.addAttribute("money", new Money());
		model.addAttribute("message", "");
		return "index";
	}

}
