package wns.cashman.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import wns.cashman.enums.Denomination;
import wns.cashman.exception.InvalidAmountException;
import wns.cashman.model.BankNote;
import wns.cashman.service.CashDispenseService;
import wns.cashman.validation.CashDispenseValidator;

/**
 * Controller to support UI operations for checking balance of notes,
 * withdrawing cash and loading more money
 *
 * @author Sunil Chopra
 */
@Controller
@RequestMapping("/cash")
public class CashDispenseController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(CashDispenseController.class);

	CashDispenseService cashDispenseService;
	
	@Autowired
    private CashDispenseValidator validator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

	@Autowired
	public CashDispenseController(CashDispenseService cashDispenseService) {
		this.cashDispenseService = cashDispenseService;
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST, params = "action=checkBalance")
	public String checkBalance(@ModelAttribute CashDispenseCommand command, BindingResult errors, Model model) {
		String message = "Number of notes available for " + Denomination.fromValue(Integer.valueOf(command.getNoteTypeOfCheckBal())).name() + " AUD is ";
		int number = cashDispenseService.getNumberOfAvailableBankNotes(Denomination.fromValue(Integer.valueOf(command.getNoteTypeOfCheckBal())));
		message += number;
		model.addAttribute("formAction", "checkBalance");
		model.addAttribute("message", message);
		model.addAttribute("command", new CashDispenseCommand());
		return "home";
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST, params = "action=withdraw")
	public String withdraw(@ModelAttribute("command") @Valid CashDispenseCommand command, BindingResult bindingResult, Model model) {
		String message = "Thanks for using our service, your bank notes include: ";
		List<BankNote> bankNotes = null;
		String notes = "";
		
		if(bindingResult.hasErrors()) {
			log.info("Validation errors while submitting form.");
			model.addAttribute("formAction", "withdraw");
			model.addAttribute("command", command);
			return "home";
        }
		
		try {
			bankNotes = cashDispenseService.withdraw(Integer.valueOf(command.getAmount()));
			if(bankNotes != null) {
				for(BankNote note : bankNotes) {
					if(note.getNumber() > 0) {
						if(StringUtils.isEmpty(notes))
							notes += note.toString();
						else
							notes += " and " + note.toString();
					}
				}
			}
		} catch (InvalidAmountException ex) {
			model.addAttribute("message", ex.getMessage());
			model.addAttribute("formAction", "withdraw");
			model.addAttribute("command", command);
			return "home";
		}
		model.addAttribute("message", message + notes);
		model.addAttribute("formAction", "withdraw");
		model.addAttribute("command", new CashDispenseCommand());
		return "home";
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST, params = "action=loadCash")
	public String loadCash(@ModelAttribute("command") @Valid CashDispenseCommand command, BindingResult bindingResult, Model model) {
		
		if(bindingResult.hasErrors()) {
			log.info("Validation errors while submitting form.");
			model.addAttribute("formAction", "loadCash");
			model.addAttribute("command", command);
			return "home";
        }
		
		String message = "Bank Notes successfully loaded. Total number of bank notes available now for " + Denomination.fromValue(Integer.valueOf(command.getNoteTypeOfLoadCash())).name() + " AUD is ";
		int number = cashDispenseService.addBankNotes(Denomination.fromValue(Integer.valueOf(command.getNoteTypeOfLoadCash())), Integer.valueOf(command.getNumber()));
		message += number;
		model.addAttribute("formAction", "loadCash");
		model.addAttribute("message", message);
		model.addAttribute("command", new CashDispenseCommand());
		return "home";
	}

}
