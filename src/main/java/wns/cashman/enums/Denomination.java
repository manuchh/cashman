package wns.cashman.enums;

/**
 * Enum to define available types of bank notes
 *
 * @author Sunil Chopra
 */
public enum Denomination {

	TWENTY(new Integer(20)), FIFTY(new Integer(50));

	private Integer value;

	Denomination(Integer amount) {
		value = amount;
	}

	public Integer value() {
		return value;
	}

	public static Denomination fromValue(Integer amount) {
		for (Denomination c : Denomination.values()) {
			if (c.value.equals(amount)) {
				return c;
			}
		}
		throw new IllegalArgumentException(String.valueOf(amount));
	}

}
