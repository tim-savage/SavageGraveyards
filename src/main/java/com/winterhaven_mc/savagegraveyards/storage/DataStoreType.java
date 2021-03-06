package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import org.bukkit.plugin.java.JavaPlugin;


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
	private final static PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);

	// DataStoreType display name
	private final String displayName;

	// default DataStoreType
	private final static DataStoreType defaultType = DataStoreType.SQLITE;


	/**
	 * Get new instance of DataStore of configured type
	 * @return new instance of DataStore
	 */
	abstract DataStore create();


	/**
	 * Class constructor
	 *
	 * @param displayName formatted name of datastore type
	 */
	DataStoreType(final String displayName) {
		this.displayName = displayName;
	}


	/**
	 * Get display name of DataStoreType
	 *
	 * @return String - display name of DataStoreType
	 */
	@Override
	public String toString() {
		return displayName;
	}


	/**
	 * Attempt to match a DataStoreType by name
	 *
	 * @param name the name to attempt to match to a DataStoreType
	 * @return A DataStoreType whose name matched the passed string,
	 * or the default DataStoreType if no match
	 */
	public static DataStoreType match(final String name) {
		for (DataStoreType type : DataStoreType.values()) {
			if (type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}
		// no match; return default type
		return defaultType;
	}


	/**
	 * Get the default DataStoreType
	 *
	 * @return DataStoreType - the default DataStoreType
	 */
	public static DataStoreType getDefaultType() {
		return defaultType;
	}

}
