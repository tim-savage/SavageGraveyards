package com.winterhaven_mc.savagegraveyards.storage;


/**
 * Abstract datastore class
 */
public abstract class DataStoreAbstract implements DataStore {

	// datastore initialized state
	private boolean initialized;

	// datastore type
	DataStoreType type;

	// datastore filename
	String filename;


	/**
	 * Get datastore filename or equivalent
	 *
	 * @return datastore filename
	 */
	String getFilename() {
		return this.filename;
	}


	/**
	 * Get datastore type
	 *
	 * @return Enum value of DataStoreType
	 */
	public DataStoreType getType() {
		return this.type;
	}


	/**
	 * Get datastore name, formatted for display
	 *
	 * @return String containing datastore name
	 */
	public String getDisplayName() {
		return this.getType().toString();
	}


	/**
	 * Get datastore initialized field
	 *
	 * @return true if datastore is initialized, false if it is not
	 */
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

}
