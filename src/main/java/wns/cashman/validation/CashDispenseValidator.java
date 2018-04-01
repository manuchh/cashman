package wns.cashman.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import wns.cashman.controller.CashDispenseCommand;

@Component
public class CashDispenseValidator implements Validator  {

	@Override
	public boolean supports(Class<?> clazz) {
		return CashDispenseCommand.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CashDispenseCommand command = (CashDispenseCommand) target;
		switch (command.getSubmitAction()) {
			case "withdraw": 
				if(StringUtils.isEmpty(command.getAmount())) {
					errors.rejectValue("amount", "invalid.amount", "Please provide the amount to withdraw");
				} else {
					try {
						Integer amount = Integer.valueOf(command.getAmount());
						if(amount <= 0) {
							errors.rejectValue("amount", "invalid.amount", "Amount should be positive numeric value without decimal");
						}
					} catch (Exception ex) {
						errors.rejectValue("amount", "invalid.amount", "Amount should be positive numeric value without decimal");
					}
				}
				break;
			case "loadCash": 
				if(StringUtils.isEmpty(command.getNumber())) {
					errors.rejectValue("number", "invalid.number", "Please provide the number of notes to be loaded");
				} else {
					try {
						Integer number = Integer.valueOf(command.getNumber());
						if(number <= 0) {
							errors.rejectValue("number", "invalid.number", "Number should be positive numeric value without decimal");
						}
					} catch (Exception ex) {
						errors.rejectValue("number", "invalid.number", "Number should be positive numeric value without decimal");
					}
				}
				break;
		}
	}

}
