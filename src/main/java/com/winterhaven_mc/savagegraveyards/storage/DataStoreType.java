package com.winterhaven_mc.savagegraveyards.storage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Enumeration of supported datastore types with constructor and other convenience methods
 */
enum DataStoreType {

	SQLITE("SQLite") {
		@Override
		public DataStore create(JavaPlugin plugin) {

			// create new sqlite datastore object
			return new DataStoreSQLite(plugin);
		}
	};

	// DataStoreType display name
	private final String displayName;

	// default DataStoreType
	private final static DataStoreType defaultType = DataStoreType.SQLITE;


	/**
	 * Get new instance of DataStore of configured type
	 * @return new instance of DataStore
	 */
	abstract DataStore create(JavaPlugin plugin);


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


	/**
	 * convert old data store to new data store
	 *
	 * @param oldDataStore the old datastore to convert from
	 * @param newDataStore the new datastore to convert to
	 */
	static void convert(final DataStore oldDataStore, final DataStore newDataStore) {

		// if datastores are same type, do not convert
		if (oldDataStore.getType().equals(newDataStore.getType())) {
			return;
		}

		// if old datastore file exists, attempt to read all records
		if (oldDataStore.exists()) {

			Bukkit.getLogger().info("Converting existing " + oldDataStore + " datastore to "
					+ newDataStore + " datastore...");

			// initialize old datastore if necessary
			if (!oldDataStore.isInitialized()) {
				try {
					oldDataStore.initialize();
				}
				catch (Exception e) {
					Bukkit.getLogger().warning("Could not initialize "
							+ oldDataStore + " datastore for conversion.");
					Bukkit.getLogger().warning(e.getLocalizedMessage());
					return;
				}
			}

			int count = newDataStore.insertGraveyards(oldDataStore.selectAllGraveyards());

			Bukkit.getLogger().info(count + " records converted to " + newDataStore + " datastore.");

			newDataStore.sync();

			oldDataStore.close();
			oldDataStore.delete();
		}
	}


	/**
	 * convert all existing data stores to new data store
	 *
	 * @param newDataStore the new datastore to convert all other existing datastores into
	 */
	static void convertAll(final JavaPlugin plugin, final DataStore newDataStore) {

		// get array list of all data store types
		ArrayList<DataStoreType> dataStores = new ArrayList<>(Arrays.asList(DataStoreType.values()));

		// remove newDataStore from list of types to convert
		dataStores.remove(newDataStore.getType());

		for (DataStoreType type : dataStores) {

			// create oldDataStore holder
			DataStore oldDataStore = null;

			if (type.equals(DataStoreType.SQLITE)) {
				oldDataStore = new DataStoreSQLite(plugin);
			}

			// add additional datastore types here as they become available

			if (oldDataStore != null) {
				convert(oldDataStore, newDataStore);
			}
		}
	}

}
