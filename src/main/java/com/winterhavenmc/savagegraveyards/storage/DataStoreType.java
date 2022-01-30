/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.savagegraveyards.storage;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


/**
 * Enumeration of supported datastore types with constructor and other convenience methods
 */
enum DataStoreType {

	SQLITE("SQLite", "graveyards.db") {

		@Override
		public DataStore connect(final JavaPlugin plugin) {

			// create new sqlite datastore object
			return new DataStoreSQLite(plugin);
		}

		@Override
		boolean storageObjectExists(final JavaPlugin plugin) {
			// get path name to data store file
			File dataStoreFile = new File(plugin.getDataFolder() + File.separator + this.getStorageName());
			return dataStoreFile.exists();
		}
	};

	// data store type display name
	private final String displayName;

	// data store object name
	private final String storageName;

	// default DataStoreType
	private final static DataStoreType defaultType = DataStoreType.SQLITE;


	/**
	 * Class constructor
	 *
	 * @param displayName formatted name of datastore type
	 */
	DataStoreType(final String displayName, final String storageName) {
		this.displayName = displayName;
		this.storageName = storageName;
	}


	/**
	 * Get new instance of DataStore of configured type
	 *
	 * @return new instance of DataStore
	 */
	abstract DataStore connect(final JavaPlugin plugin);


	/**
	 * Getter for storage object name.
	 *
	 * @return the name of the backing store object for a data store type
	 */
	String getStorageName() {
		return storageName;
	}


	/**
	 * Test if datastore backing object (file, database) exists
	 *
	 * @param plugin reference to plugin main class
	 * @return true if backing object exists, false if not
	 */
	abstract boolean storageObjectExists(final JavaPlugin plugin);


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
	 * @return A DataStoreType whose name matched the passed string, else the default DataStoreType if no match
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
	 * convert old data store to new data store
	 *
	 * @param oldDataStore the old datastore to convert from
	 * @param newDataStore the new datastore to convert to
	 */
	private static void convert(final JavaPlugin plugin, final DataStore oldDataStore, final DataStore newDataStore) {

		// if datastores are same type, do not convert
		if (oldDataStore.getType().equals(newDataStore.getType())) {
			return;
		}

		// if old datastore exists, attempt to read all records
		if (oldDataStore.getType().storageObjectExists(plugin)) {

			plugin.getLogger().info("Converting existing " + oldDataStore + " datastore to "
					+ newDataStore + " datastore...");

			// initialize old datastore if necessary
			if (!oldDataStore.isInitialized()) {
				try {
					oldDataStore.initialize();
				}
				catch (Exception e) {
					plugin.getLogger().warning("Could not initialize "
							+ oldDataStore + " datastore for conversion.");
					plugin.getLogger().warning(e.getLocalizedMessage());
					return;
				}
			}

			// get count of records inserted in new datastore from old datastore
			int count = newDataStore.insertGraveyards(oldDataStore.selectAllGraveyards());

			// log record count message
			plugin.getLogger().info(count + " records converted to " + newDataStore + " datastore.");

			// flush new datastore to disk if applicable
			newDataStore.sync();

			// close old datastore
			oldDataStore.close();

			// delete old datastore
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
		Collection<DataStoreType> dataStoreTypes = new HashSet<>(Arrays.asList(DataStoreType.values()));

		// remove newDataStore type from list of types to convert
		dataStoreTypes.remove(newDataStore.getType());

		for (DataStoreType type : dataStoreTypes) {
			if (type.storageObjectExists(plugin)) {
				convert(plugin, type.connect(plugin), newDataStore);
			}
		}
	}

}
