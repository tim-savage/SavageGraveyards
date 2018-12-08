package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.*;


/**
 * Concrete SQLite datastore class
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
class DataStoreSQLite extends DataStore {

	// reference to main class
	private final PluginMain plugin;

	// database connection object
	private Connection connection;


	/**
	 * Class constructor
	 * @param plugin reference to main class
	 */
	DataStoreSQLite (PluginMain plugin) {

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

		// execute table creation statements
		statement.executeUpdate(Queries.getQuery("CreateGraveyardsTable"));
		statement.executeUpdate(Queries.getQuery("CreateDiscoveredTable"));

		// set initialized true
		setInitialized(true);
		if (plugin.debug) {
			plugin.getLogger().info(this.toString() + " datastore initialized.");
		}

	}

	
	public List<Graveyard> selectAllGraveyards() {
	
		// create empty list for return
		List<Graveyard> returnList = new ArrayList<>();
	
		try {
			PreparedStatement preparedStatement = 
					connection.prepareStatement(Queries.getQuery("SelectAllGraveyards"));
	
			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();
	
			while (rs.next()) {
	
				Integer id = rs.getInt("id");				
				String searchKey = rs.getString("searchkey");
				String displayName = rs.getString("displayname");
				Boolean enabled = rs.getBoolean("enabled");
				Boolean hidden = rs.getBoolean("hidden");
				Integer discoveryRange = rs.getInt("discoveryrange");
				String discoveryMessage = rs.getString("discoverymessage");
				String respawnMessage = rs.getString("respawnmessage");
				String groupName = rs.getString("groupname");
				Integer safetyRange = rs.getInt("safetyrange");
				Integer safetyTime = rs.getInt("safetytime");
				String worldName = rs.getString("worldname");
				Double x = rs.getDouble("x");
				Double y = rs.getDouble("y");
				Double z = rs.getDouble("z");
				Float yaw = rs.getFloat("yaw");
				Float pitch = rs.getFloat("pitch");
	
				World world;
	
				try {
					world = plugin.getServer().getWorld(worldName);
				} catch (Exception e) {
					plugin.getLogger().warning("Stored record has unloaded world: " 
							+ worldName + ". Skipping record.");
					continue;
				}
	
				Location location = new Location(world,x,y,z,yaw,pitch);
				
				Graveyard graveyard = new Graveyard();
				graveyard.setKey(id);
				graveyard.setSearchKey(searchKey);
				graveyard.setDisplayName(displayName);
				graveyard.setEnabled(enabled);
				graveyard.setHidden(hidden);
				graveyard.setDiscoveryRange(discoveryRange);
				graveyard.setDiscoveryMessage(discoveryMessage);
				graveyard.setRespawnMessage(respawnMessage);
				graveyard.setGroupName(groupName);
				graveyard.setSafetyRange(safetyRange);
				graveyard.setSafetyTime(safetyTime);
				graveyard.setLocation(location);
	
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
	public Graveyard selectGraveyard(final String name) {

		// derive search key in case display name was passed
		String derivedKey = Graveyard.deriveKey(name);
		
		// if key is null or empty, return null record
		if (derivedKey == null || derivedKey.isEmpty()) {
			return null;
		}

		Graveyard graveyard = null;
		World world;

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(Queries.getQuery("SelectGraveyard"));

			preparedStatement.setString(1, derivedKey.toLowerCase());

			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();

			// only zero or one record can match the unique key
			if (rs.next()) {

				// get stored id
				Integer id = rs.getInt("id");
				
				// get stored searchKey
				String searchKey = rs.getString("searchkey");
				
				// get stored displayName
				String displayName = rs.getString("displayname");
				
				// get stored enabled
				Boolean enabled = rs.getBoolean("enabled");
				
				// get stored hidden
				Boolean hidden = rs.getBoolean("hidden");
				
				// get stored discovery distance
				Integer discoveryRange = rs.getInt("discoveryrange");
				
				// get stored discovery message
				String discoveryMessage = rs.getString("discoverymessage");

				// get stored respawn message
				String respawnMessage = rs.getString("respawnmessage");

				// get stored group
				String groupName = rs.getString("groupname");
				
				// get stored safety range
				Integer safetyRange = rs.getInt("safetyrange");
				
				// get stored safety time
				Integer safetyTime = rs.getInt("safetytime");
				
				// get stored world and coordinates
				String worldName = rs.getString("worldname");
				Double x = rs.getDouble("x");
				Double y = rs.getDouble("y");
				Double z = rs.getDouble("z");
				Float yaw = rs.getFloat("yaw");
				Float pitch = rs.getFloat("pitch");

				if (plugin.getServer().getWorld(worldName) == null) {
					plugin.getLogger().warning("Stored Graveyard world not found!");
					return null;
				}
				world = plugin.getServer().getWorld(worldName);
				Location location = new Location(world,x,y,z,yaw,pitch);
				
				graveyard = new Graveyard();
				graveyard.setKey(id);
				graveyard.setSearchKey(searchKey);
				graveyard.setDisplayName(displayName);
				graveyard.setEnabled(enabled);
				graveyard.setHidden(hidden);
				graveyard.setDiscoveryRange(discoveryRange);
				graveyard.setDiscoveryMessage(discoveryMessage);
				graveyard.setRespawnMessage(respawnMessage);
				graveyard.setGroupName(groupName);
				graveyard.setSafetyRange(safetyRange);
				graveyard.setSafetyTime(safetyTime);
				graveyard.setLocation(location);
			}
		}
		catch (SQLException e) {

			// output simple error message
			plugin.getLogger().warning("An error occured while fetching a Graveyard record from the SQLite database.");
			plugin.getLogger().warning(e.getLocalizedMessage());

			// if debugging is enabled, output stack trace
			if (plugin.debug) {
				e.getStackTrace();
			}
			return null;
		}
		return graveyard;
	}
	
	
	public Graveyard selectNearestGraveyard(Player player) {
		
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
					connection.prepareStatement(Queries.getQuery("SelectNearestGraveyards"));
	
			preparedStatement.setString(1, playerWorldName);
			preparedStatement.setString(2, uuidString);
	
			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();
	
			while (rs.next()) {
			
				Integer key = rs.getInt("id");				
				String searchKey = rs.getString("searchkey");
				String displayName = rs.getString("displayname");
				Boolean enabled = rs.getBoolean("enabled");
				Boolean hidden = rs.getBoolean("hidden");
				Integer discoveryRange = rs.getInt("discoveryrange");
				String discoveryMessage = rs.getString("discoverymessage");
				String respawnMessage = rs.getString("respawnmessage");
				String groupName = rs.getString("groupname");
				Integer safetyRange = rs.getInt("safetyrange");
				Integer safetyTime = rs.getInt("safetytime");
				String worldName = rs.getString("worldname");
				Double x = rs.getDouble("x");
				Double y = rs.getDouble("y");
				Double z = rs.getDouble("z");
				Float yaw = rs.getFloat("yaw");
				Float pitch = rs.getFloat("pitch");
	
				World world;
	
				try {
					world = plugin.getServer().getWorld(worldName);
				} catch (Exception e) {
					plugin.getLogger().warning("Stored record has unloaded world: " 
							+ worldName + ". Skipping record.");
					continue;
				}
	
				Location location = new Location(world,x,y,z,yaw,pitch);
				
				Graveyard graveyard = new Graveyard();
				graveyard.setKey(key);
				graveyard.setSearchKey(searchKey);
				graveyard.setDisplayName(displayName);
				graveyard.setEnabled(enabled);
				graveyard.setHidden(hidden);
				graveyard.setDiscoveryRange(discoveryRange);
				graveyard.setDiscoveryMessage(discoveryMessage);
				graveyard.setRespawnMessage(respawnMessage);
				graveyard.setGroupName(groupName);
				graveyard.setSafetyRange(safetyRange);
				graveyard.setSafetyTime(safetyTime);
				graveyard.setLocation(location);
				
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
					connection.prepareStatement(Queries.getQuery("SelectMatchingGraveyardNames"));

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
					connection.prepareStatement(Queries.getQuery("SelectUndiscoveredGraveyards"));
			
			preparedStatement.setString(1, player.getWorld().getName());
			preparedStatement.setString(2, player.getUniqueId().toString());
	
			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();
	
			while (rs.next()) {
				
				Integer key = rs.getInt("id");
				String searchKey = rs.getString("searchkey");
				String displayName = rs.getString("displayname");
				Boolean enabled = rs.getBoolean("enabled");
				Boolean hidden = rs.getBoolean("hidden");
				Integer discoveryRange = rs.getInt("discoveryrange");
				String discoveryMessage = rs.getString("discoverymessage");
				String respawnMessage = rs.getString("respawnmessage");
				String groupName = rs.getString("groupname");
				Integer safetyRange = rs.getInt("safetyrange");
				Integer safetyTime = rs.getInt("safetytime");
				String worldName = rs.getString("worldname");
				Double x = rs.getDouble("x");
				Double y = rs.getDouble("y");
				Double z = rs.getDouble("z");
				Float yaw = rs.getFloat("yaw");
				Float pitch = rs.getFloat("pitch");
	
				World world;
	
				try {
					world = plugin.getServer().getWorld(worldName);
				} catch (Exception e) {
					plugin.getLogger().warning("Stored record has unloaded world: " 
							+ worldName + ". Skipping record.");
					continue;
				}
	
				Location location = new Location(world,x,y,z,yaw,pitch);				
				
				Graveyard graveyard = new Graveyard();
				graveyard.setKey(key);
				graveyard.setSearchKey(searchKey);
				graveyard.setDisplayName(displayName);
				graveyard.setEnabled(enabled);
				graveyard.setHidden(hidden);
				graveyard.setDiscoveryRange(discoveryRange);
				graveyard.setDiscoveryMessage(discoveryMessage);
				graveyard.setRespawnMessage(respawnMessage);
				graveyard.setGroupName(groupName);
				graveyard.setSafetyRange(safetyRange);
				graveyard.setSafetyTime(safetyTime);
				graveyard.setLocation(location);

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
	public Set<Integer> getUndiscoveredKeys(final Player player) {
		
		// if player is null, return empty set
		if (player == null) {
			return Collections.emptySet();
		}

		// create empty set for return
		Set<Integer> returnSet = new HashSet<>();
		
		try {
			PreparedStatement preparedStatement = 
					connection.prepareStatement(Queries.getQuery("SelectUndiscoveredGraveyardKeys"));
			
			preparedStatement.setString(1, player.getWorld().getName());
			preparedStatement.setString(2, player.getUniqueId().toString());
	
			// execute sql query
			ResultSet rs = preparedStatement.executeQuery();
	
			while (rs.next()) {
				returnSet.add(rs.getInt("id"));
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
	public void insertDiscovery(final Player player, final String name) {

		final String uuidString = player.getUniqueId().toString();

		Graveyard graveyard = selectGraveyard(name);
		
		final Integer primaryKey = graveyard.getKey();

		if (primaryKey == null) {
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {

				try {

					// synchronize on connection
					synchronized(connection) {

						PreparedStatement preparedStatement = 
								connection.prepareStatement(Queries.getQuery("InsertDiscovered"));

						preparedStatement.setInt(1, primaryKey);
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
	
		// if destination is null do nothing and return
		if (graveyard == null) {
			return;
		}
	
		// get display name
		final String displayName = graveyard.getDisplayName();
		
		// get key
		final String searchKey = Graveyard.deriveKey(displayName);
		
		// get enabled
		final Boolean enabled = graveyard.isEnabled();
		
		// get hidden
		final Boolean hidden = graveyard.isHidden();
		
		// get discovery range
		final Integer discoveryRange = graveyard.getDiscoveryRange();
		
		// get discovery message
		final String discoveryMessage = graveyard.getDiscoveryMessage();
	
		// get respawn message
		final String respawnMessage = graveyard.getRespawnMessage();
	
		// get group
		final String groupName = graveyard.getGroup();
		
		// get safety range
		final Integer safetyRange = graveyard.getSafetyRange();
		
		// get safety time
		final Integer safetyTime = graveyard.getSafetyTime();
		
		// get location
		final Location location = graveyard.getLocation();
	
		// get world name
		String testWorldName;
	
		// test that world in destination location is valid
		try {
			testWorldName = location.getWorld().getName();
		} catch (Exception e) {
			plugin.getLogger().warning("An error occured while inserting"
					+ " a record in the " + this.toString() + " datastore. World invalid!");
			return;
		}
		final String worldName = testWorldName;
	
		new BukkitRunnable() {
			@Override
			public void run() {

				try {
					
					// synchronize on connection
					synchronized(connection) {
						
						// create prepared statement
						PreparedStatement preparedStatement = 
								connection.prepareStatement(Queries.getQuery("InsertGraveyard"));

						preparedStatement.setString(1, searchKey);
						preparedStatement.setString(2, displayName);
						preparedStatement.setBoolean(3, enabled);
						preparedStatement.setBoolean(4, hidden);
						preparedStatement.setInt(5, discoveryRange);
						preparedStatement.setString(6, discoveryMessage);
						preparedStatement.setString(7, respawnMessage);
						preparedStatement.setString(8, groupName);					
						preparedStatement.setInt(9, safetyRange);
						preparedStatement.setInt(10, safetyTime);
						preparedStatement.setString(11, worldName);
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
					plugin.getLogger().warning("An error occured while inserting a Graveyard record "
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


	//	@Override
	public void updateGraveyard(final Graveyard graveyard) {
	
		// if destination is null do nothing and return
		if (graveyard == null) {
			return;
		}
	
		// get key
		final Integer key = graveyard.getKey();
		
		// get display name
		final String displayName = graveyard.getDisplayName();
		
		// get search key
		final String searchKey = Graveyard.deriveKey(displayName);
		
		// get enabled
		final Boolean enabled = graveyard.isEnabled();
		
		// get hidden
		final Boolean hidden = graveyard.isHidden();
		
		// get discovery range
		final Integer discoveryRange = graveyard.getDiscoveryRange();
		
		// get discovery message
		final String discoveryMessage = graveyard.getDiscoveryMessage();
	
		// get respawn message
		final String respawnMessage = graveyard.getRespawnMessage();
	
		// get group
		final String groupName = graveyard.getGroup();
		
		// get safety range
		final Integer safetyRange = graveyard.getSafetyRange();
		
		// get safety time
		final Integer safetyTime = graveyard.getSafetyTime();
		
		// get location
		final Location location = graveyard.getLocation();
	
		// get world name
		String testWorldName;
	
		// test that world in destination location is valid
		try {
			testWorldName = location.getWorld().getName();
		} catch (Exception e) {
			plugin.getLogger().warning("An error occured while inserting"
					+ " a record in the " + this.toString() + " datastore. World invalid!");
			return;
		}
		final String worldName = testWorldName;
	
		new BukkitRunnable() {
			@Override
			public void run() {

				try {
					// synchronize on connection
					synchronized(connection) {

						// create prepared statement
						PreparedStatement preparedStatement = 
								connection.prepareStatement(Queries.getQuery("UpdateGraveyard"));

						preparedStatement.setString(1, searchKey);
						preparedStatement.setString(2, displayName);
						preparedStatement.setBoolean(3, enabled);
						preparedStatement.setBoolean(4, hidden);
						preparedStatement.setInt(5, discoveryRange);
						preparedStatement.setString(6, discoveryMessage);
						preparedStatement.setString(7, respawnMessage);
						preparedStatement.setString(8, groupName);
						preparedStatement.setInt(9, safetyRange);
						preparedStatement.setInt(10, safetyTime);
						preparedStatement.setString(11, worldName);
						preparedStatement.setDouble(12, location.getX());
						preparedStatement.setDouble(13, location.getY());
						preparedStatement.setDouble(14, location.getZ());
						preparedStatement.setFloat(15, location.getYaw());
						preparedStatement.setFloat(16, location.getPitch());
						preparedStatement.setInt(17, key);

						// execute prepared statement
						preparedStatement.executeUpdate();
					}
				}
				catch (Exception e) {
	
					// output simple error message
					plugin.getLogger().warning("An error occured while updating a Graveyard record "
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
	public Graveyard deleteGraveyard(final String name) {
		
		// derive key in case display name was passed
		String derivedKey = Graveyard.deriveKey(name);
		
		// if key is null or empty, return null record
		if (derivedKey == null || derivedKey.isEmpty()) {
			return null;
		}
	
		// get destination record to be deleted, for return
		final Graveyard graveyard = this.selectGraveyard(derivedKey);
		
		// get primary key of record to be deleted
		final Integer primaryKey = graveyard.getKey();
		
		new BukkitRunnable() {
			@Override
			public void run() {

				int rowsAffected = 0;

				try {
					
					// synchronize on connection
					synchronized(connection) {
						
						// create prepared statement
						PreparedStatement preparedStatement = 
								connection.prepareStatement(Queries.getQuery("DeleteGraveyard"));

						preparedStatement.setInt(1, primaryKey);

						// execute prepared statement
						rowsAffected = preparedStatement.executeUpdate();
					}
					
					// output debugging information
					if (plugin.debug) {
						plugin.getLogger().info(rowsAffected + " death spawns deleted.");
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

				if (rowsAffected > 0) {
					try {

						// synchronize on connection
						synchronized(connection) {

							// create prepared statement
							PreparedStatement preparedStatement = 
									connection.prepareStatement(Queries.getQuery("DeleteDiscoveries"));

							preparedStatement.setInt(1, primaryKey);

							// execute prepared statement
							rowsAffected = preparedStatement.executeUpdate();
						}
						
						// output debugging information
						if (plugin.debug) {
							plugin.getLogger().info(rowsAffected + " discoveries deleted.");
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
			}
		}.runTaskAsynchronously(plugin);

		return graveyard;
	}


	@Override
	public void close() {

		try {
			connection.close();
			plugin.getLogger().info("SQLite datastore connection closed.");
		}
		catch (Exception e) {

			// output simple error message
			plugin.getLogger().warning("An error occured while closing the SQLite datastore.");
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
