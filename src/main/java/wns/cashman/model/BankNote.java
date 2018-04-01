package wns.cashman.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import wns.cashman.enums.Denomination;

/**
 * Simple JavaBean domain object representing a Bank note.
 *
 * @author Sunil Chopra
 */
@Entity
public class BankNote {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	Denomination type;

	int number;

	public BankNote(Denomination type, int numberOfAvailableNotes) {
		this.type = type;
		this.number = numberOfAvailableNotes;
	}

	public BankNote() {
	}

	public Denomination getType() {
		return type;
	}

	public void setType(Denomination type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return this.getNumber() + " notes of " + this.getType().name() + " AUD";  
	}
}