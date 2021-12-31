package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.*;


/**
 * Abstract datastore class
 */
public interface DataStore {

	/**
	 * Initialize storage
	 *
	 * @throws Exception if datastore cannot be initialized
	 */
	void initialize() throws Exception;

	boolean isInitialized();


	/**
	 * Get data store type
	 *
	 * @return the datastore type
	 */
	DataStoreType getType();


	/**
	 * Close datastore connection
	 */
	void close();


	/**
	 * Sync datastore to disk if supported
	 */
	void sync();


	/**
	 * Delete datastore
	 */
	@SuppressWarnings("UnusedReturnValue")
	boolean delete();


	/**
	 * Create new data store of given type and convert old data store.<br>
	 * Two parameter version used when a datastore instance already exists
	 *
	 * @param plugin reference to plugin main class
	 * @return a new datastore instance of the given type
	 */
	static DataStore connect(final JavaPlugin plugin) {

		// get data store type from config
		DataStoreType dataStoreType = DataStoreType.match(plugin.getConfig().getString("storage-type"));

		// get new data store of specified type
		DataStore newDataStore = dataStoreType.connect(plugin);

		// initialize new data store
		try {
			newDataStore.initialize();
		}
		catch (Exception e) {
			plugin.getLogger().severe("Could not initialize " + newDataStore + " datastore!");
			plugin.getLogger().severe(e.getLocalizedMessage());
			if (plugin.getConfig().getBoolean("debug")) {
				e.printStackTrace();
			}
		}

		// convert any existing data stores to new type
		DataStoreType.convertAll(plugin, newDataStore);

		// return initialized data store
		return newDataStore;
	}


	/**
	 * Reload data store if configured type has changed
	 */
	static void reload(PluginMain plugin) {

		// get current datastore type
		DataStoreType currentType = plugin.dataStore.getType();

		// get configured datastore type
		DataStoreType newType = DataStoreType.match(plugin.getConfig().getString("storage-type"));

		// if current datastore type does not match configured datastore type, create new datastore
		if (!currentType.equals(newType)) {

			// create new datastore
			plugin.dataStore = connect(plugin);
		}
	}


	/**
	 * get all graveyard records
	 *
	 * @return List of all graveyard objects in alphabetical order
	 */
	Collection<Graveyard> selectAllGraveyards();


	/**
	 * Get record
	 *
	 * @param displayName the name of the Graveyard to be retrieved
	 * @return Graveyard object or null if no matching record
	 */
	Graveyard selectGraveyard(final String displayName);


	/**
	 * Get undiscovered graveyards for player
	 *
	 * @param player the player for whom to retrieve undiscovered Graveyards
	 * @return HashSet of Graveyard objects that are undiscovered for player
	 */
	Collection<Graveyard> selectUndiscoveredGraveyards(final Player player);


	/**
	 * Get undiscovered graveyard keys for player
	 *
	 * @param player the player for whom to retrieve undiscovered Graveyard keys
	 * @return HashSet of Graveyard search keys that are undiscovered for player
	 */
	Collection<String> selectUndiscoveredKeys(final Player player);


	/**
	 * Gets closest graveyard to player's current location
	 *
	 * @param player the player for whom to retrieve the nearest Graveyard
	 * @return Graveyard object
	 */
	Graveyard selectNearestGraveyard(final Player player);


	/**
	 * Get records that prefix match string
	 *
	 * @param match the prefix to match
	 * @return String collection of names with matching prefix
	 */
	List<String> selectMatchingGraveyardNames(final String match);


	/**
	 * Insert discovery record
	 *
	 * @param record the discovery record to be inserted
	 */
	void insertDiscovery(Discovery record);


	/**
	 * Insert discovery records
	 *
	 * @param insertSet set of records to be inserted
	 * @return number of records successfully inserted
	 */
	int insertDiscoveries(Collection<Discovery> insertSet);


	/**
	 * Insert a collection of records
	 *
	 * @param graveyards a collection of graveyard records
	 * @return int - the number of records successfully inserted
	 */
	int insertGraveyards(final Collection<Graveyard> graveyards);


	/**
	 * Update record
	 *
	 * @param graveyard the Graveyard to update in the datastore
	 */
	void updateGraveyard(final Graveyard graveyard);


	/**
	 * Delete record
	 *
	 * @param displayName display name or search key of record to be deleted
	 * @return Deleted graveyard record
	 */
	Graveyard deleteGraveyard(final String displayName);


	/**
	 * Delete discovery record
	 *
	 * @param displayName display name or search key of record to be deleted
	 * @param playerUUID the player unique id
	 * @return boolean - {@code true} if deletion was successful, {@code false} if not
	 */
	boolean deleteDiscovery(final String displayName, final UUID playerUUID);


	/**
	 * select graveyard keys that player has discovered
	 * @param playerUid the player uid to query
	 * @return Collection of String - graveyard keys
	 */
	Collection<String> selectDiscoveredKeys(final UUID playerUid);


	/**
	 * Select players who have discovered any graveyards
	 * @return Collection of String - player names with discovered graveyards
	 */
	Collection<String> selectPlayersWithDiscoveries();

}
