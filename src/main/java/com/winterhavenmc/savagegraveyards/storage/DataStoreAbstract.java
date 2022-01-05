package com.winterhavenmc.savagegraveyards.storage;


/**
 * Abstract datastore class
 */
public abstract class DataStoreAbstract implements DataStore {

	// datastore initialized state
	private boolean initialized;

	// datastore type
	DataStoreType type;


	/**
	 * Get datastore initialized field
	 *
	 * @return true if datastore is initialized, false if it is not
	 */
	@Override
	public boolean isInitialized() {
		return this.initialized;
	}


	/**
	 * Set initialized field
	 *
	 * @param initialized the initialized value of the datastore
	 */
	void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}


	/**
	 * Get datastore type
	 *
	 * @return Enum value of DataStoreType
	 */
	@Override
	public DataStoreType getType() {
		return this.type;
	}


	/**
	 * Override toString method to return the datastore type name
	 *
	 * @return the name of this datastore instance
	 */
	@Override
	public String toString() {
		return this.type.toString();
	}

}
