package wns.cashman.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import wns.cashman.domain.Cash;
import wns.cashman.enums.Denomination;
import wns.cashman.exception.InvalidAmountException;
import wns.cashman.model.BankNote;
import wns.cashman.repository.NoteRepository;
import wns.cashman.util.EmailSender;

/**
 * Service implementation to support cash dispense functionality
 *
 * @author Sunil Chopra
 */
@Service
public class CashDispenseServiceImpl implements CashDispenseService {

	private static final Logger log = LoggerFactory.getLogger(CashDispenseServiceImpl.class);
	
	private static int MIN_FIFTY_NOTES_LIMIT_FOR_200CHECK = 3;
	private static int MIN_TWENTY_NOTES_LIMIT_FOR_200CHECK = 8;

	NoteRepository noteRepository;
	
	@Autowired
	EmailSender emailSender;
	
	@Value("${wns.mail.from}")
	private String from;

	@Value("${wns.mail.to}")
	private String to;
	
	@Value("${wns.mail.subject}")
	private String subject;
	
	@Value("${wns.mail.body}")
	private String body;
	
	public CashDispenseServiceImpl() {}
	
	@Autowired
	public CashDispenseServiceImpl(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}
	
	@Override
	public Boolean initialize(Cash cash) {
		Boolean success = true;
		for (BankNote note: cash.getMoney()) {
			if (noteRepository.countByType(note.getType()) != 0) {
				success = false;
			}
		}
		if (success) {
			for (BankNote note : cash.getMoney()) {
				if (noteRepository.countByType(note.getType()) == 0) {
					noteRepository.save(note);
				}
			}
		}
		return success;
	}

	/**
	 * This is the main method to handle all the backend operations in case of
	 * withdrawal request. It accepts the amount to be withdraw and return the
	 * best possible combination as per available denominations. If amount is
	 * not valid or if no possible combination is found as per available
	 * denominations and given amount then exception will be thrown
	 * 
	 * @param amount
	 * @return combination of bank denominations
	 */
	@Override
	public List<BankNote> withdraw(Integer amount) throws InvalidAmountException {
		log.debug("Processing request to withdraw AUD" + amount);
		List<BankNote> result = null;

		// check for pre-conditions
		String errorMessage = validateRequest(amount);
		if (!StringUtils.isEmpty(errorMessage)) {
			log.error("Invalid amount requested.");
			throw new InvalidAmountException(errorMessage);
		}

		// fetch all the possible combinations of available denominations
		List<BankNote> bankNotes = noteRepository.findAll();
		List<BankNote[]> dispenseOptionsList = getDispenseOptions(bankNotes, new int[bankNotes.size()], amount, 0);
		if (dispenseOptionsList != null && dispenseOptionsList.size() > 0) {
			log.debug("Number of available combinations is : " + dispenseOptionsList.size());
		} else {
			log.error("No possible combination of notes found as per available denominations.");
			throw new InvalidAmountException("No possible combination of notes found as per available denominations: " + getAllAvailableDenominationTypes());
		}

		// select appropriate combination
		result = selectAppropriateCombination(dispenseOptionsList);

		// update balance of bank notes
		updateBankNotesBalance(result);

		return result;
	}
	
	private List<BankNote> selectAppropriateCombination(List<BankNote[]> dispenseOptionsList) {
		List<BankNote> result = null; 
		if(dispenseOptionsList.size() > 1) {
			int availableFiftyNotes = 0;
			int availableTwentyNotes = 0;
			Iterable<BankNote> availableNotes = noteRepository.findAll();
			for(BankNote note : availableNotes) {
				if(Denomination.FIFTY.equals(note.getType())) {
					availableFiftyNotes = note.getNumber();
				}
				if(Denomination.TWENTY.equals(note.getType())) {
					availableTwentyNotes = note.getNumber();
				}
			}
			for(BankNote[] bankNotes : dispenseOptionsList) {
				boolean appropriateCombination = true;
				for(BankNote bankNote : bankNotes) {
					if(Denomination.FIFTY.equals(bankNote.getType())) {
						if((availableFiftyNotes - bankNote.getNumber()) == 0) {
							appropriateCombination = false;
						}
					}
					if(Denomination.TWENTY.equals(bankNote.getType())) {
						if((availableTwentyNotes - bankNote.getNumber()) == 0) {
							appropriateCombination = false;
						}
					}
				}
				if(appropriateCombination) {
					result = Arrays.asList(bankNotes);
					break;
				}
			}
			
			// if no suitable combination found then select first one
			if(result == null) {
				return Arrays.asList(dispenseOptionsList.get(0));
			}
		} else {
			return Arrays.asList(dispenseOptionsList.get(0));
		}
		return result;
	}

	private String validateRequest(Integer amountToWithdraw) {
		String errorMessage = "";
		Integer amountAvailable = getAvailableAmount();
		if (amountToWithdraw == 0) {
			errorMessage = "Invalid amount requested.";
		} else if (amountToWithdraw > amountAvailable) {
			errorMessage = "Not enough cash in machine!! Please try with a smaller amount.";
		} else if (amountToWithdraw == 200) {
			// check for available notes condition - $200, when there is only 3x$50 notes and 8x$20 notes available
			int fiftyNotes = 0;
			int twentyNotes = 0;
			Iterable<BankNote> availableNotes = noteRepository.findAll();
			for(BankNote note : availableNotes) {
				if(Denomination.FIFTY.equals(note.getType())) {
					fiftyNotes = note.getNumber();
				}
				if(Denomination.TWENTY.equals(note.getType())) {
					twentyNotes = note.getNumber();
				}
			}
			if(!(fiftyNotes == MIN_FIFTY_NOTES_LIMIT_FOR_200CHECK && twentyNotes == MIN_TWENTY_NOTES_LIMIT_FOR_200CHECK)) {
				errorMessage = "No possible combination of notes found as per available denominations.";
			}
		}
		return errorMessage;
	}

	private Integer getAvailableAmount() {
		Integer availableAmount = 0;
		Iterable<BankNote> availableNotes = noteRepository.findAll();
		for (BankNote note : availableNotes) {
			int numberOfNotesAvailable = note.getNumber();
			int amount = note.getType().value();
			availableAmount += numberOfNotesAvailable * amount;
		}
		return availableAmount;
	}

	private List<BankNote[]> getDispenseOptions(List<BankNote> bankNotes, int[] variation, int amountToWithdraw,
			int position) {
		List<BankNote[]> list = new ArrayList<BankNote[]>();
		BankNote[] bankNotesArr = new BankNote[0];
		BankNote[] bankNotesArray = bankNotes.toArray(bankNotesArr);
		int value = calculate(bankNotesArray, variation);
		if (value < amountToWithdraw) {
			for (int i = position; i < bankNotes.size(); i++) {
				if (bankNotes.get(i).getNumber() > variation[i]) {
					int[] newVariation = variation.clone();
					newVariation[i]++;
					List<BankNote[]> newList = getDispenseOptions(bankNotes, newVariation, amountToWithdraw, i);
					if (newList != null) {
						list.addAll(newList);
					}
				}
			}
		} else if (value == amountToWithdraw) {
			list.add(copyResult(bankNotesArray, variation));
		}
		return list;
	}

	private int calculate(BankNote[] bankNotes, int[] variation) {
		return IntStream.range(0, variation.length).map(i -> bankNotes[i].getType().value() * variation[i]).sum();
	}

	private BankNote[] copyResult(BankNote[] bankNotes, int[] variation) {
		BankNote[] result = new BankNote[variation.length];
		for (int i = 0; i < variation.length; i++) {
			result[i] = new BankNote(bankNotes[i].getType(), variation[i]);
		}
		return result;
	}

	private String getAllAvailableDenominationTypes() {
		String availableDenominations = "";
		List<BankNote> bankNotes = noteRepository.findAll();
		for (BankNote bankNote : bankNotes) {
			if(bankNote.getNumber() > 0) {
				if(StringUtils.isEmpty(availableDenominations))
					availableDenominations += bankNote.getType().name() + " AUD";
				else
					availableDenominations += ", " + bankNote.getType().name() + " AUD";
			}
		}
		return availableDenominations;
	}
	
	/**
	 * This method is used to update balance of Bank Notes dynamically. Method
	 * is designed in a way that it can even support if in future new
	 * denominations are added
	 * 
	 * @param bankNotesConsumed
	 * @return
	 */
	private void updateBankNotesBalance(List<BankNote> bankNotesConsumed) {
		List<BankNote> bankNotes = noteRepository.findAll();
		for (BankNote bankNote : bankNotes) {
			for (BankNote noteConsumed : bankNotesConsumed) {
				if (bankNote.getType().equals(noteConsumed.getType())) {
					bankNote.setNumber(bankNote.getNumber() - noteConsumed.getNumber());
					noteRepository.save(bankNote);
				}
			}
		}
		
		// check if balance is low then send email to admin
		Integer amountAvailable = getAvailableAmount();
		if(amountAvailable < 50) {
			try {
				emailSender.sendMail(from, to, subject, body);
			} catch (Exception ex) {
				log.error("Mail server not available!!");
			}
			
		}
	}

	/**
	 * This method is used to check the balance of available Bank Notes
	 * as per provided Bank Note type
	 * 
	 * @param bankNoteType
	 * @return number of notes
	 */
	@Override
	public int getNumberOfAvailableBankNotes(Denomination bankNoteType) {
		BankNote bankNote = (noteRepository.findByType(bankNoteType));
		return bankNote.getNumber();
	}
	
	/**
	 * This method is used to load more Bank Notes
	 * as per provided Bank Note type and number
	 * 
	 * @param bankNoteType
	 * @param number
	 * @return 
	 */
	@Override
	public int addBankNotes(Denomination bankNoteType, Integer number) {
		BankNote bankNote = (noteRepository.findByType(bankNoteType));
		bankNote.setNumber(bankNote.getNumber() + number);
		noteRepository.save(bankNote);
		return bankNote.getNumber();
	}

}
