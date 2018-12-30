package com.winterhaven_mc.savagegraveyards.storage;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Abstract datastore class
 */
public abstract class DataStore {

	private boolean initialized;
	DataStoreType type;
	String filename;


	/**
	 * Initialize storage
	 *
	 * @throws Exception if datastore cannot be initialized
	 */
	abstract void initialize() throws Exception;


	/**
	 * get all graveyard records
	 *
	 * @return List of all graveyard objects in alphabetical order
	 */
	public abstract List<Graveyard> selectAllGraveyards();


	/**
	 * Get record
	 *
	 * @param displayName the name of the Graveyard to be retrieved
	 * @return Graveyard object or null if no matching record
	 */
	public abstract Graveyard selectGraveyard(final String displayName);


	/**
	 * Get undiscovered graveyards for player
	 *
	 * @param player the player for whom to retrieve undiscovered Graveyards
	 * @return HashSet of Graveyard objects that are undiscovered for player
	 */
	public abstract Set<Graveyard> getUndiscovered(final Player player);


	/**
	 * Get undiscovered graveyard keys for player
	 *
	 * @param player the player for whom to retrieve undiscovered Graveyard keys
	 * @return HashSet of Graveyard search keys that are undiscovered for player
	 */
	public abstract Set<String> getUndiscoveredKeys(final Player player);


	/**
	 * Gets closest graveyard to player's current location
	 *
	 * @param player the player for whom to retrieve the nearest Graveyard
	 * @return Graveyard object
	 */
	public abstract Graveyard selectNearestGraveyard(final Player player);


	/**
	 * Set graveyard to discovered for player
	 *
	 * @param player      the player for whom to set a Graveyard as discovered
	 * @param displayName display name or search key of the Graveyard to set as discovered
	 */
	public abstract void insertDiscovery(final Player player, final String displayName);


	/**
	 * Insert new record
	 *
	 * @param graveyard the Graveyard object to insert into the datastore
	 */
	public abstract void insertGraveyard(final Graveyard graveyard);


	/**
	 * Update record
	 *
	 * @param graveyard the Graveyard to update in the datastore
	 */
	public abstract void updateGraveyard(final Graveyard graveyard);


	/**
	 * Delete record
	 *
	 * @param displayName display name or search key of record to be deleted
	 * @return Deleted graveyard record
	 */
	public abstract Graveyard deleteGraveyard(final String displayName);


	/**
	 * Delete discovery record
	 *
	 * @param displayName display name or search key of record to be deleted
	 */
	public abstract boolean deleteDiscovery(final String displayName, final UUID playerUUID);


	/**
	 * Select player UUIDs that have discovered a graveyard
	 *
	 * @param displayName the display name or search key of the graveyard
	 * @return List of UUID - player UUIDs that have discovered the graveyard
	 */
	public abstract List<UUID> selectPlayersDiscovered(final String displayName);


	/**
	 * Close datastore connection
	 */
	public abstract void close();


	/**
	 * Sync datastore to disk if supported
	 */
	abstract void sync();


	/**
	 * Delete datastore
	 */
	@SuppressWarnings("UnusedReturnValue")
	abstract boolean delete();


	/**
	 * Check that datastore exists
	 *
	 * @return true if datastore exists, false if it does not
	 */
	abstract boolean exists();


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
	DataStoreType getType() {
		return this.type;
	}


	/**
	 * Get datastore name
	 *
	 * @return String containing datastore name
	 */
	@Override
	public String toString() {
		return this.getType().toString();
	}


	/**
	 * Get datastore initialized field
	 *
	 * @return true if datastore is initialized, false if it is not
	 */
	boolean isInitialized() {
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
	 * Get records that prefix match string
	 *
	 * @param match the prefix to match
	 * @return String list of names with matching prefix
	 */
	public abstract List<String> selectMatchingGraveyardNames(final String match);

}
