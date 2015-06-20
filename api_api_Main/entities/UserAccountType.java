package entities;

import java.io.Serializable;
import java.util.HashMap;

public enum UserAccountType
implements Serializable {
	Unknown(0),

	Free(1),

	PaidIndividual(2),

	PaidBusiness(3),

	LimitedFree(6),

	LimitedBusiness(7),

	PaidPersonal(13),

	TrialBusiness(14),

	Reseller(15),

	PendingBusiness(16);

	private int intValue;
	private static HashMap < Integer, UserAccountType > mappings;

	private static HashMap < Integer, UserAccountType > getMappings() {
		if (mappings == null) {
			synchronized(UserAccountType.class) {
				if (mappings == null) {
					mappings = new HashMap();
				}
			}
		}
		return mappings;
	}

	private UserAccountType(int value) {
		this.intValue = value;
		getMappings().put(Integer.valueOf(value), this);
	}

	public int getValue() {
		return this.intValue;
	}

	public static UserAccountType forValue(int value) {
		return (UserAccountType) getMappings().get(Integer.valueOf(value));
	}
}