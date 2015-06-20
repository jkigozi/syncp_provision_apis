package entities;

import java.io.Serializable;

/**
 * User Account Type
 */
public enum UserAccountTypeCopyOf implements Serializable {
	// A type wasn't provided.
	Unknown(0),

	/**
	 * Free users have a limited storage for their own files. Shared files do
	 * not count against their quota if they're not the Virtual Folder owner.
	 */
	Free(1),

	/**
	 * An individual who is subscribed to Syncplicity.
	 */
	PaidIndividual(2),

	/**
	 * A user who is subscribed to Syncplicity as part of a business account.
	 */
	PaidBusiness(3),

	/**
	 * People who accepted an invite but have not yet provided their name and
	 * password.
	 */
	LimitedFree(6),

	/**
	 * Users who have been added to a company account but not yet provided their
	 * personal details
	 */
	LimitedBusiness(7),

	/**
	 * A user who is subscribed to Syncplicity as part of a personal account.
	 */
	PaidPersonal(13),

	/**
	 * A user who is subscribed to Syncplicity as part of a business trial
	 * account and hasn't previous subscription
	 */
	TrialBusiness(14),

	/**
	 * Reseller
	 */
	Reseller(15),

	/**
	 * A user who is suggested to be added to a company account, but not yet
	 * approved by a company administrator
	 */
	PendingBusiness(16);

	private int intValue;
	private static java.util.HashMap<Integer, UserAccountTypeCopyOf> mappings;

	private static java.util.HashMap<Integer, UserAccountTypeCopyOf> getMappings() {
		if (mappings == null) {
			synchronized (UserAccountTypeCopyOf.class) {
				if (mappings == null) {
					mappings = new java.util.HashMap<Integer, UserAccountTypeCopyOf>();
				}
			}
		}
		return mappings;
	}

	private UserAccountTypeCopyOf(int value) {
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static UserAccountTypeCopyOf forValue(int value) {
		return getMappings().get(value);
	}
}