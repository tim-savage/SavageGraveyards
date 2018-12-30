package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;

/**
 * Enumeration of supported datastore types with constructor and other convenience methods
 */
enum DataStoreType {

	SQLITE("SQLite") {
		@Override
		public DataStore create() {

			// create new sqlite datastore object
			return new DataStoreSQLite(plugin);
		}
	};

	// static reference to main class
	private final static PluginMain plugin = PluginMain.instance;

	// DataStoreType display name
	private String displayName;

	// default DataStoreType
	private final static DataStoreType defaultType = DataStoreType.SQLITE;

	abstract DataStore create();

	/**
	 * Class constructor
	 *
	 * @param displayName formatted name of datastore type
	 */
	DataStoreType(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public static DataStoreType match(final String name) {
		for (DataStoreType type : DataStoreType.values()) {
			if (type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}
		// no match; return default type
		return defaultType;
	}

	public static DataStoreType getDefaultType() {
		return defaultType;
	}

}
