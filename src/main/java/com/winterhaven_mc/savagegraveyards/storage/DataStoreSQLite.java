package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.*;

import static com.winterhaven_mc.savagegraveyards.storage.Queries.getQuery;


/**
 * Concrete SQLite datastore class
 */
class DataStoreSQLite extends DataStore {

	// reference to main class
	private final PluginMain plugin;

	// database connection object
	private Connection connection;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	DataStoreSQLite(final PluginMain plugin) {

		// reference to main class
		this.plugin = plugin;

		// set datastore type
		this.type = DataStoreType.SQLITE;

		// set datastore filename
		this.filename = "graveyards.db";
	}


	@Override
	void initialize() throws SQLException, ClassNotFoundException {

		// if data store is already initialized, do nothing and return
		if (this.isInitialized()) {
			plugin.getLogger().info(this.toString() + " datastore already initialized.");
			return;
		}

		// register the driver
		final String jdbcDriverName = "org.sqlite.JDBC";

		Class.forName(jdbcDriverName);

		// create database url
		String dataFilePath = plugin.getDataFolder() + File.separator + filename;
		String jdbc = "jdbc:sqlite";
		String dbUrl = jdbc + ":" + dataFilePath;

		// create a database connection
		connection = DriverManager.getConnection(dbUrl);
		Statement statement = connection.createStatement();

		// enable foreign keys
		statement.executeUpdate(getQuery("EnableForeignKeys"));

		// execute table creation statements
		statement.executeUpdate(getQuery("CreateGraveyardsTable"));
		statement.executeUpdate(getQuery("CreateDiscoveredTable"));

		// set initialized true
		setInitialized(true);
		if (plugin.debug) {
			plugin.getLogger().info(this.toString() + " datastore initialized.");
		}

	}


	@Override
	public List<Graveyard> selectAllGraveyards() {

		// create empty list for return
		List<Graveyard> returnList = new ArrayList<>();

		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(getQuery("SelectAllGraveyards"));

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				int primaryKey = rs.getInt("id");
				String searchKey = rs.getString("searchkey");
				String displayName = rs.getString("displayname");
				boolean enabled = rs.getBoolean("enabled");
				boolean hidden = rs.getBoolean("hidden");
				int discoveryRange = rs.getInt("discoveryrange");
				String discoveryMessage = rs.getString("discoverymessage");
				String respawnMessage = rs.getString("respawnmessage");
				String groupName = rs.getString("groupname");
				int safetyRange = rs.getInt("safetyrange");
				int safetyTime = rs.getInt("safetytime");
				String worldName = rs.getString("worldname");
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				World world;

				try {
					world = plugin.getServer().getWorld(worldName);
				}
				catch (Exception e) {
					plugin.getLogger().warning("Stored record has invalid world: "
							+ worldName + ". Skipping record.");
					continue;
				}

				Location location = new Location(world, x, y, z, yaw, pitch);

				Graveyard graveyard = new Graveyard.Builder()
						.primaryKey(primaryKey)
						.displayName(displayName)
						.searchKey(searchKey)
						.enabled(enabled)
						.hidden(hidden)
						.discoveryRange(discoveryRange)
						.discoveryMessage(discoveryMessage)
						.respawnMessage(respawnMessage)
						.group(groupName)
						.safetyRange(safetyRange)
						.safetyTime(safetyTime)
						.location(location)
						.build();

				returnList.add(graveyard);
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while trying to "
					+ "fetch all Graveyard records from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}

		// return results as unmodifiable list
		return Collections.unmodifiableList(returnList);
	}


	@Override
	public Graveyard selectGraveyard(final String displayName) {

		// derive search key from displayName
		String searchKey = Graveyard.createSearchKey(displayName);

		// if key is null or empty, return null record
		if (searchKey == null || searchKey.isEmpty()) {
			return null;
		}

		Graveyard graveyard = null;
		World world;

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(getQuery("SelectGraveyard"));

			preparedStatement.setString(1, searchKey);

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			// only zero or one record can match the unique search key
			if (rs.next()) {

				// get stored world and coordinates
				String worldName = rs.getString("worldname");
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				if (plugin.getServer().getWorld(worldName) == null) {
					plugin.getLogger().warning("Stored Graveyard world not found!");
					return null;
				}
				world = plugin.getServer().getWorld(worldName);
				Location location = new Location(world, x, y, z, yaw, pitch);

				graveyard = new Graveyard.Builder()
						.primaryKey(rs.getInt("id"))
						.displayName(rs.getString("displayName"))
						.searchKey(rs.getString("searchKey"))
						.enabled(rs.getBoolean("enabled"))
						.hidden(rs.getBoolean("hidden"))
						.discoveryRange(rs.getInt("discoveryRange"))
						.discoveryMessage(rs.getString("discoveryMessage"))
						.respawnMessage(rs.getString("respawnMessage"))
						.group(rs.getString("groupName"))
						.safetyRange(rs.getInt("safetyRange"))
						.safetyTime(rs.getInt("safetyTime"))
						.location(location)
						.build();
			}
		}
		catch (SQLException e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while fetching a Graveyard record from the SQLite database.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
			return null;
		}
		return graveyard;
	}


	@Override
	public Graveyard selectNearestGraveyard(final Player player) {

		// if player is null, return null record
		if (player == null) {
			return null;
		}

		Location playerLocation = player.getLocation();
		String uuidString = player.getUniqueId().toString();
		String playerWorldName = player.getWorld().getName();

		Graveyard closest = null;

		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(getQuery("SelectNearestGraveyards"));

			preparedStatement.setString(1, playerWorldName);
			preparedStatement.setString(2, uuidString);

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				int primaryKey = rs.getInt("id");
				String searchKey = rs.getString("searchkey");
				String displayName = rs.getString("displayname");
				boolean enabled = rs.getBoolean("enabled");
				boolean hidden = rs.getBoolean("hidden");
				int discoveryRange = rs.getInt("discoveryrange");
				String discoveryMessage = rs.getString("discoverymessage");
				String respawnMessage = rs.getString("respawnmessage");
				String groupName = rs.getString("groupname");
				int safetyRange = rs.getInt("safetyrange");
				int safetyTime = rs.getInt("safetytime");
				String worldName = rs.getString("worldname");
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				World world;

				try {
					world = plugin.getServer().getWorld(worldName);
				}
				catch (Exception e) {
					plugin.getLogger().warning("Stored record has unloaded world: "
							+ worldName + ". Skipping record.");
					continue;
				}

				Location location = new Location(world, x, y, z, yaw, pitch);

				Graveyard graveyard = new Graveyard.Builder()
						.primaryKey(primaryKey)
						.displayName(displayName)
						.searchKey(searchKey)
						.enabled(enabled)
						.hidden(hidden)
						.discoveryRange(discoveryRange)
						.discoveryMessage(discoveryMessage)
						.respawnMessage(respawnMessage)
						.group(groupName)
						.safetyRange(safetyRange)
						.safetyTime(safetyTime)
						.location(location)
						.build();

				if (graveyard.getLocation() == null) {
					continue;
				}

				if (groupName == null || groupName.isEmpty() || player.hasPermission("group." + groupName)) {
					if (closest == null
							|| graveyard.getLocation().distanceSquared(playerLocation)
							< closest.getLocation().distanceSquared(playerLocation)) {
						closest = graveyard;
					}
				}
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while trying to "
					+ "fetch the closest Graveyard from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}

		// return closest result
		return closest;
	}


	@Override
	public List<String> selectMatchingGraveyardNames(final String match) {

		// create empty return list
		List<String> returnList = new ArrayList<>();

		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(getQuery("SelectMatchingGraveyardNames"));

			preparedStatement.setString(1, match.toLowerCase() + "%");

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				returnList.add(rs.getString("searchkey"));
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while trying to "
					+ "fetch matching Graveyard records from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}

		// return unmodifiable list of search key strings
		return Collections.unmodifiableList(returnList);
	}


	@Override
	public Set<Graveyard> getUndiscovered(final Player player) {

		// create empty set of Graveyard for return
		Set<Graveyard> returnSet = new HashSet<>();

		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(getQuery("SelectUndiscoveredGraveyards"));

			preparedStatement.setString(1, player.getWorld().getName());
			preparedStatement.setString(2, player.getUniqueId().toString());

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				int primaryKey = rs.getInt("id");
				String searchKey = rs.getString("searchkey");
				String displayName = rs.getString("displayname");
				boolean enabled = rs.getBoolean("enabled");
				boolean hidden = rs.getBoolean("hidden");
				int discoveryRange = rs.getInt("discoveryrange");
				String discoveryMessage = rs.getString("discoverymessage");
				String respawnMessage = rs.getString("respawnmessage");
				String groupName = rs.getString("groupname");
				int safetyRange = rs.getInt("safetyrange");
				int safetyTime = rs.getInt("safetytime");
				String worldName = rs.getString("worldname");
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				World world;

				try {
					world = plugin.getServer().getWorld(worldName);
				}
				catch (Exception e) {
					plugin.getLogger().warning("Stored record has unloaded world: "
							+ worldName + ". Skipping record.");
					continue;
				}

				Location location = new Location(world, x, y, z, yaw, pitch);

				Graveyard graveyard = new Graveyard.Builder()
						.primaryKey(primaryKey)
						.displayName(displayName)
						.searchKey(searchKey)
						.enabled(enabled)
						.hidden(hidden)
						.discoveryRange(discoveryRange)
						.discoveryMessage(discoveryMessage)
						.respawnMessage(respawnMessage)
						.group(groupName)
						.safetyRange(safetyRange)
						.safetyTime(safetyTime)
						.location(location)
						.build();

				returnSet.add(graveyard);
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while trying to "
					+ "fetch undiscovered Graveyard records from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}

		// return results
		return Collections.unmodifiableSet(returnSet);
	}


	@Override
	public Set<String> getUndiscoveredKeys(final Player player) {

		// if player is null, return empty set
		if (player == null) {
			return Collections.emptySet();
		}

		// create empty set for return
		Set<String> returnSet = new HashSet<>();

		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(getQuery("SelectUndiscoveredGraveyardKeys"));

			preparedStatement.setString(1, player.getWorld().getName());
			preparedStatement.setString(2, player.getUniqueId().toString());

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				returnSet.add(rs.getString("searchkey"));
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while trying to "
					+ "fetch undiscovered Graveyard ids from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}

		// return results
		return returnSet;
	}


	@Override
	public void insertDiscovery(final Player player, final String displayName) {

		final String uuidString = player.getUniqueId().toString();

		new BukkitRunnable() {
			@Override
			public void run() {

				try {

					// synchronize on connection
					synchronized (this) {

						PreparedStatement preparedStatement =
								connection.prepareStatement(getQuery("InsertDiscovered"));

						preparedStatement.setString(1, Graveyard.createSearchKey(displayName));
						preparedStatement.setString(2, uuidString);

						// execute prepared statement
						preparedStatement.executeUpdate();
					}
				}
				catch (Exception e) {

					// output simple error message
					plugin.getLogger().warning("An error occurred while trying to "
							+ "insert a record into the discovered table in the SQLite datastore.");
					plugin.getLogger().warning(e.getLocalizedMessage());

					// if debugging is enabled, output stack trace
					if (plugin.debug) {
						e.getStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(plugin);

	}


	@Override
	public void insertGraveyard(final Graveyard graveyard) {

		// if graveyard or graveyard location is null, do nothing and return
		if (graveyard == null || graveyard.getLocation() == null || graveyard.getLocation().getWorld() == null) {
			if (plugin.debug) {
				plugin.getLogger().warning("Could not insert graveyard in data store "
						+ "because location is not valid!");
			}
			return;
		}

		// get location
		final Location location = graveyard.getLocation();

		new BukkitRunnable() {
			@Override
			public void run() {

				try {

					// synchronize on connection
					synchronized (this) {

						// create prepared statement
						PreparedStatement preparedStatement =
								connection.prepareStatement(getQuery("InsertGraveyard"));

						preparedStatement.setString(1, graveyard.getSearchKey());
						preparedStatement.setString(2, graveyard.getDisplayName());
						preparedStatement.setBoolean(3, graveyard.isEnabled());
						preparedStatement.setBoolean(4, graveyard.isHidden());
						preparedStatement.setInt(5, graveyard.getDiscoveryRange());
						preparedStatement.setString(6, graveyard.getDiscoveryMessage());
						preparedStatement.setString(7, graveyard.getRespawnMessage());
						preparedStatement.setString(8, graveyard.getGroup());
						preparedStatement.setInt(9, graveyard.getSafetyRange());
						preparedStatement.setInt(10, graveyard.getSafetyTime());
						preparedStatement.setString(11, location.getWorld().getName());
						preparedStatement.setDouble(12, location.getX());
						preparedStatement.setDouble(13, location.getY());
						preparedStatement.setDouble(14, location.getZ());
						preparedStatement.setFloat(15, location.getYaw());
						preparedStatement.setFloat(16, location.getPitch());

						// execute prepared statement
						preparedStatement.executeUpdate();
					}
				}
				catch (Exception e) {

					// output simple error message
					plugin.getLogger().warning("An error occurred while inserting a Graveyard record "
							+ "into the SQLite datastore.");
					plugin.getLogger().warning(e.getLocalizedMessage());

					// if debugging is enabled, output stack trace
					if (plugin.debug) {
						e.getStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(plugin);
	}


	@Override
	public void updateGraveyard(final Graveyard graveyard) {

		// if graveyard or graveyard location is null do nothing and return
		if (graveyard == null || graveyard.getLocation() == null || graveyard.getLocation().getWorld() == null) {
			return;
		}

		// get location
		final Location location = graveyard.getLocation();

		new BukkitRunnable() {
			@Override
			public void run() {

				try {
					// synchronize on connection
					synchronized (this) {

						// create prepared statement
						PreparedStatement preparedStatement =
								connection.prepareStatement(getQuery("UpdateGraveyard"));

						preparedStatement.setString(1, graveyard.getSearchKey());
						preparedStatement.setString(2, graveyard.getDisplayName());
						preparedStatement.setBoolean(3, graveyard.isEnabled());
						preparedStatement.setBoolean(4, graveyard.isHidden());
						preparedStatement.setInt(5, graveyard.getDiscoveryRange());
						preparedStatement.setString(6, graveyard.getDiscoveryMessage());
						preparedStatement.setString(7, graveyard.getRespawnMessage());
						preparedStatement.setString(8, graveyard.getGroup());
						preparedStatement.setInt(9, graveyard.getSafetyRange());
						preparedStatement.setInt(10, graveyard.getSafetyTime());
						preparedStatement.setString(11, location.getWorld().getName());
						preparedStatement.setDouble(12, location.getX());
						preparedStatement.setDouble(13, location.getY());
						preparedStatement.setDouble(14, location.getZ());
						preparedStatement.setFloat(15, location.getYaw());
						preparedStatement.setFloat(16, location.getPitch());
						preparedStatement.setInt(17, graveyard.getPrimaryKey());

						// execute prepared statement
						preparedStatement.executeUpdate();
					}
				}
				catch (Exception e) {

					// output simple error message
					plugin.getLogger().warning("An error occurred while updating a Graveyard record "
							+ "into the SQLite datastore.");
					plugin.getLogger().warning(e.getLocalizedMessage());

					// if debugging is enabled, output stack trace
					if (plugin.debug) {
						e.getStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(plugin);
	}


	@Override
	public Graveyard deleteGraveyard(final String displayName) {

		// get destination record to be deleted, for return
		final Graveyard graveyard = this.selectGraveyard(displayName);

		new BukkitRunnable() {
			@Override
			public void run() {

				int rowsAffected;

				try {

					// synchronize on connection
					synchronized (this) {

						// create prepared statement
						PreparedStatement preparedStatement =
								connection.prepareStatement(getQuery("DeleteGraveyard"));

						preparedStatement.setString(1, Graveyard.createSearchKey(displayName));

						// execute prepared statement
						rowsAffected = preparedStatement.executeUpdate();
					}

					// output debugging information
					if (plugin.debug) {
						plugin.getLogger().info(rowsAffected + " graveyards deleted.");
					}
				}
				catch (Exception e) {

					// output simple error message
					plugin.getLogger().warning("An error occurred while attempting to "
							+ "delete a Graveyard record from the SQLite datastore.");
					plugin.getLogger().warning(e.getLocalizedMessage());

					// if debugging is enabled, output stack trace
					if (plugin.debug) {
						e.getStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(plugin);

		return graveyard;
	}


	@Override
	public List<UUID> selectPlayersDiscovered(final String displayName) {

		// get search key from passed display name
		String searchKey = Graveyard.createSearchKey(displayName);

		// if search key is null or empty, return empty list
		if (searchKey == null || displayName.isEmpty()) {
			return Collections.emptyList();
		}

		// create empty list for return
		List<UUID> resultList = new ArrayList<>();

		try {
			PreparedStatement preparedStatement =
					connection.prepareStatement(getQuery("SelectPlayersDiscovered"));

			preparedStatement.setString(1, searchKey);

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				try {
					resultList.add(UUID.fromString(rs.getString("PlayerUuid")));
				}
				catch (IllegalArgumentException e) {
					plugin.getLogger().warning("Invalid Player UUID in datastore!");
				}
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while trying to "
					+ "fetch player discovery records from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}

		// return results
		return resultList;
	}


	@Override
	public boolean deleteDiscovery(final String displayName, final UUID playerUUID) {

		int rowsAffected;
		boolean result = true;

		try {

			// synchronize on connection
			synchronized (this) {

				// create prepared statement
				PreparedStatement preparedStatement =
						connection.prepareStatement(getQuery("DeleteDiscovery"));

				preparedStatement.setString(1, playerUUID.toString());
				preparedStatement.setString(2, Graveyard.createSearchKey(displayName));

				// execute prepared statement
				rowsAffected = preparedStatement.executeUpdate();
			}

			if (rowsAffected < 1) {
				result = false;
			}

			// output debugging information
			if (plugin.debug) {
				plugin.getLogger().info(rowsAffected + " discoveries deleted.");
			}
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while attempting to "
					+ "delete a Discovery record from the SQLite datastore.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}
		return result;
	}


	@Override
	public void close() {

		try {
			connection.close();
			plugin.getLogger().info("SQLite datastore connection closed.");
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occurred while closing the SQLite datastore.");
			plugin.getLogger().warning(e.getMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
		}
		setInitialized(false);
	}


	@Override
	void sync() {

		// no action necessary for this storage type

	}


	@Override
	boolean delete() {

		// get path name to data store file
		File dataStoreFile = new File(plugin.getDataFolder() + File.separator + this.getFilename());
		boolean result = false;
		if (dataStoreFile.exists()) {
			result = dataStoreFile.delete();
		}
		return result;
	}


	@Override
	boolean exists() {

		// get path name to data store file
		File dataStoreFile = new File(plugin.getDataFolder() + File.separator + this.getFilename());
		return dataStoreFile.exists();
	}

}
