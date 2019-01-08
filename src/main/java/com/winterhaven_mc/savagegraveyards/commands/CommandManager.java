package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.storage.DataStore;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.savagegraveyards.messages.MessageId;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.*;


/**
 * Implements command executor for {@code SavageGraveyards} commands.
 *
 * @author Tim Savage
 * @version 1.0
 */
public class CommandManager implements CommandExecutor, TabCompleter {

	// reference to main class
	private final PluginMain plugin;

	private final static ChatColor HELP_COLOR = ChatColor.YELLOW;
	private final static ChatColor USAGE_COLOR = ChatColor.GOLD;

	// list of possible subcommands
	private final static List<String> SUBCOMMANDS =
			Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
					"closest", "create", "delete", "forget", "list", "reload",
					"set", "show", "status", "teleport", "help")));

	// list of possible attributes
	private final static List<String> ATTRIBUTES =
			Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
					"enabled", "hidden", "location", "name", "safetytime",
					"discoveryrange", "discoverymessage", "respawnmessage")));


	/**
	 * constructor method for {@code CommandManager} class
	 *
	 * @param plugin reference to main class
	 */
	public CommandManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// register this class as command executor
		plugin.getCommand("graveyard").setExecutor(this);

		// register this class as tab completer
		plugin.getCommand("graveyard").setTabCompleter(this);
	}


	/**
	 * Tab completer for SavageGraveyards commands
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		List<String> returnList = new ArrayList<>();

		// return list of valid matching subcommands
		if (args.length == 1) {

			for (String subcommand : SUBCOMMANDS) {
				if (sender.hasPermission("graveyard." + subcommand)
						&& subcommand.startsWith(args[0].toLowerCase())) {
					returnList.add(subcommand);
				}
			}
		}

		// return list of valid matching graveyard names
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("teleport")
					|| args[0].equalsIgnoreCase("tp")
					|| args[0].equalsIgnoreCase("set")
					|| args[0].equalsIgnoreCase("show")
					|| args[0].equalsIgnoreCase("delete")
					|| args[0].equalsIgnoreCase("forget")) {
				returnList = plugin.dataStore.selectMatchingGraveyardNames(args[1]);
			}
		}

		// return list of valid matching attributes
		else if (args.length == 3) {

			if (args[0].equalsIgnoreCase("set")) {
				for (String attribute : ATTRIBUTES) {
					if (sender.hasPermission("graveyard.set." + attribute)
							&& attribute.startsWith(args[2])) {
						returnList.add(attribute);
					}
				}
			}
			else if (args[0].equalsIgnoreCase("forget")) {
				// select playerUUIDs for graveyard from Discovered table
				List<UUID> playerUUIDs = plugin.dataStore.selectPlayersDiscovered(args[1]);

				// iterate over list of playerUUIDs and add player names to return list that match prefix
				for (UUID playerUUID : playerUUIDs) {

					// get player name from UUID
					String playerName = plugin.getServer().getOfflinePlayer(playerUUID).getName();

					// if player name begins with arg[2] (ignoring case), add player name to return list
					if (playerName.toLowerCase().startsWith(args[2].toLowerCase())) {
						returnList.add(playerName);
					}
				}
			}
		}

		return returnList;
	}


	/**
	 * Command Executor for SavageGraveyards
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
							 final String label, final String[] args) {

		String subcommand;

		// get subcommand
		if (args.length > 0) {
			subcommand = args[0];
		}

		// if no arguments, display usage for all commands
		else {
			displayUsage(sender, "all");
			return true;
		}

		// closest command
		if (subcommand.equalsIgnoreCase("closest")) {
			return closestCommand(sender);
		}

		// status command
		if (subcommand.equalsIgnoreCase("status")) {
			return statusCommand(sender);
		}

		// reload command
		if (subcommand.equalsIgnoreCase("reload")) {
			return reloadCommand(sender, args);
		}

		//create command
		if (subcommand.equalsIgnoreCase("create")) {
			return createCommand(sender, args);
		}

		// delete command
		if (subcommand.equalsIgnoreCase("delete")) {
			return deleteCommand(sender, args);
		}

		// list command
		if (subcommand.equalsIgnoreCase("list")) {
			return listCommand(sender, args);
		}

		// set command
		if (subcommand.equalsIgnoreCase("set")) {
			return setCommand(sender, args);
		}

		// show command
		if (subcommand.equalsIgnoreCase("show")) {
			return showCommand(sender, args);
		}

		// teleport command
		if (subcommand.equalsIgnoreCase("teleport") || subcommand.equalsIgnoreCase("tp")) {
			return teleportCommand(sender, args);
		}

		// forget command
		if (subcommand.equalsIgnoreCase("forget")) {
			return forgetCommand(sender, args);
		}

		// help command
		if (subcommand.equalsIgnoreCase("help")) {
			return helpCommand(sender, args);
		}

		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_INVALID_COMMAND);
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		displayUsage(sender, "help");
		return true;
	}


	/**
	 * Display plugin settings
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean statusCommand(final CommandSender sender) {

		// if command sender does not have permission to view status, output error message and return true
		if (!sender.hasPermission("graveyard.status")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_STATUS);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// output config settings
		String versionString = plugin.getDescription().getVersion();

		sender.sendMessage(ChatColor.DARK_AQUA
				+ "[" + plugin.getName() + "] " + ChatColor.AQUA + "Version: " + ChatColor.RESET + versionString);

		if (plugin.debug) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}

		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));

		sender.sendMessage(ChatColor.GREEN + "Storage type: "
				+ ChatColor.RESET + plugin.dataStore.toString());

		sender.sendMessage(ChatColor.GREEN + "Default discovery range: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-range") + " blocks");

		sender.sendMessage(ChatColor.GREEN + "Default safety time: "
				+ ChatColor.RESET + plugin.getConfig().getInt("safety-time") + " seconds");

		sender.sendMessage(ChatColor.GREEN + "Discovery check interval: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-interval") + " ticks");

		sender.sendMessage(ChatColor.GREEN + "List items page size: "
				+ ChatColor.RESET + plugin.getConfig().getInt("list-page-size") + " items");

		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());

		return true;
	}


	/**
	 * Reload plugin settings
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean reloadCommand(final CommandSender sender, final String[] args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("graveyard.reload")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_RELOAD);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String subcommand = args[0];

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.length > maxArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER);
			displayUsage(sender, subcommand);
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// reload enabled worlds
		plugin.worldManager.reload();

		// reload messages
		plugin.messageManager.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// reload datastore
		DataStore.reload();

		// set debug field
		plugin.debug = plugin.getConfig().getBoolean("debug");

		// send reloaded message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_RELOAD);
		return true;
	}


	/**
	 * Set new value for graveyard attribute
	 *
	 * @param sender the player issuing the command
	 * @param args   the command arguments
	 * @return always return {@code true} to suppress bukkit usage message
	 */
	private boolean setCommand(final CommandSender sender, final String[] args) {

		// Example usage:
		// graveyard set <graveyard> displayname <new_name>
		// graveyard set <graveyard> location
		// graveyard set <graveyard> enabled <true|false>
		// graveyard set <graveyard> hidden <true|false>
		// graveyard set <graveyard> discoverymessage <message>
		// graveyard set <graveyard> respawnmessage <message>
		// graveyard set <graveyard> safetytime <seconds>
		// graveyard set <graveyard> group <group>

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		int minArgs = 3;

		// check min arguments
		if (args.length < minArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// get graveyard name from arguments ArrayList
		String displayName = arguments.remove(0);

		// fetch graveyard from datastore
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if graveyard not found in datastore, send failure message and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send failure message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD, dummyGraveyard);

			// play failure sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get attribute name and remove from arguments ArrayList
		String attribute = arguments.remove(0);

		// get value by joining remaining arguments
		String value = join(arguments);

		if (attribute.equalsIgnoreCase("location")) {
			return setLocation(sender, graveyard);
		}

		if (attribute.equalsIgnoreCase("name")) {
			return setName(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("enabled")) {
			return setEnabled(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("hidden")) {
			return setHidden(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("discoveryrange")) {
			return setDiscoveryRange(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("discoverymessage")) {
			return setDiscoveryMessage(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("respawnmessage")) {
			return setRespawnMessage(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("group")) {
			return setGroup(sender, graveyard, value);
		}

		if (attribute.equalsIgnoreCase("safetytime")) {
			return setSafetyTime(sender, graveyard, value);
		}

		// no matching attribute, send error message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_INVALID_ATTRIBUTE);
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return true;
	}


	/**
	 * Set new location for existing graveyard
	 *
	 * @param sender    the player that issued the command
	 * @param graveyard the existing graveyard to be updated
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setLocation(final CommandSender sender, final Graveyard graveyard) {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// check player permission
		if (!player.hasPermission("graveyard.set.location")) {
			plugin.messageManager.sendMessage(player, MessageId.PERMISSION_DENIED_SET_LOCATION);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// create new graveyard object from existing graveyard with new location
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.location(player.getLocation())
				.build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(player, MessageId.COMMAND_SUCCESS_SET_LOCATION, newGraveyard);

		// play success sound
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new display name for existing graveyard
	 *
	 * @param sender    the player that issued the command
	 * @param graveyard the existing graveyard to be updated
	 * @param newName   the new display name for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setName(final CommandSender sender, final Graveyard graveyard, final String newName) {

		// check sender permission
		if (!sender.hasPermission("graveyard.set.name")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_NAME);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get original name
		final String originalName = graveyard.getDisplayName();

		// create new graveyard object with from existing graveyard with new displayName
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.displayName(newName)
				.build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_SET_NAME, newGraveyard, originalName);

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new enabled setting for existing graveyard
	 *
	 * @param sender      the player that issued the command
	 * @param graveyard   the existing graveyard to be updated
	 * @param passedValue the new enabled setting for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setEnabled(final CommandSender sender, final Graveyard graveyard, final String passedValue) {

		String value = passedValue;
		boolean enabled;

		// check sender permission
		if (!sender.hasPermission("graveyard.set.enabled")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_ENABLED);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// if value is null or empty, set to true
		if (value == null || value.isEmpty()) {
			value = "true";
		}

		if (value.equalsIgnoreCase("default")) {
			value = plugin.getConfig().getString("default-enabled");
			enabled = plugin.getConfig().getBoolean("default-enabled");
		}
		else if (value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("y")) {
			enabled = true;
		}
		else if (value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("no")
				|| value.equalsIgnoreCase("n")) {
			enabled = false;
		}
		else {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_BOOLEAN);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// create new graveyard object from existing graveyard with new enabled setting
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.enabled(enabled)
				.build();

		// update record in data store
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_SET_ENABLED, newGraveyard, value);

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new hidden setting for existing graveyard
	 *
	 * @param sender      the player that issued the command
	 * @param graveyard   the existing graveyard to be updated
	 * @param passedValue the new hidden setting for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setHidden(final CommandSender sender, final Graveyard graveyard, final String passedValue) {

		String value = passedValue;
		boolean hidden;

		// check sender permission
		if (!sender.hasPermission("graveyard.set.hidden")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_HIDDEN);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// if value is null or empty, set to true
		if (value == null || value.isEmpty()) {
			value = "true";
		}

		if (value.equalsIgnoreCase("default")) {
			value = plugin.getConfig().getString("default-hidden");
			hidden = plugin.getConfig().getBoolean("default-hidden");
		}
		else if (value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("y")) {
			hidden = true;
		}
		else if (value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("no")
				|| value.equalsIgnoreCase("n")) {
			hidden = false;
		}
		else {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_BOOLEAN);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// create new graveyard object from existing graveyard with new hidden setting
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.hidden(hidden)
				.build();

		// update record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_SET_HIDDEN, newGraveyard, value);

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new discovery range for existing graveyard
	 *
	 * @param sender    the player that issued the command
	 * @param graveyard the existing graveyard to be updated
	 * @param value     the new hidden setting for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setDiscoveryRange(final CommandSender sender, final Graveyard graveyard, final String value) {

		// check sender permission
		if (!sender.hasPermission("graveyard.set.discoveryrange")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_DISCOVERYRANGE);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		//noinspection UnusedAssignment
		int discoveryRange = 0;

		// if no distance given and sender is player, use player's current distance
		if (value.isEmpty()) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				discoveryRange = (int) player.getLocation().distance(graveyard.getLocation());
			}
			else {
				plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}
		else {
			// try to parse entered range as integer
			try {
				discoveryRange = Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_INTEGER);
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// set new range
		if (discoveryRange > 0) {

			// create new graveyard object from existing graveyard with new discovery range
			Graveyard newGraveyard = new Graveyard.Builder(graveyard)
					.discoveryRange(discoveryRange)
					.build();

			// update graveyard in datastore
			plugin.dataStore.updateGraveyard(newGraveyard);

			// send success message
			plugin.messageManager.sendMessage(sender,
					MessageId.COMMAND_SUCCESS_SET_DISCOVERYRANGE,
					newGraveyard,
					String.valueOf(discoveryRange));

			// play success sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		}
		return true;
	}


	/**
	 * Set new discovery message for existing graveyard
	 *
	 * @param sender                 the player that issued the command
	 * @param graveyard              the existing graveyard to be updated
	 * @param passedDiscoveryMessage the new discovery message for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setDiscoveryMessage(final CommandSender sender,
										final Graveyard graveyard,
										final String passedDiscoveryMessage) {

		String discoveryMessage = passedDiscoveryMessage;

		// check sender permission
		if (!sender.hasPermission("graveyard.set.discoverymessage")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_DISCOVERYMESSAGE);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// if message is 'default', set message to empty string
		if (discoveryMessage.equalsIgnoreCase("default")) {
			discoveryMessage = "";
		}

		// create new graveyard object from existing graveyard with new discovery message
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.discoveryMessage(discoveryMessage)
				.build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_SET_DISCOVERYMESSAGE, newGraveyard);

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new respawn message for existing graveyard
	 *
	 * @param sender               the player that issued the command
	 * @param graveyard            the existing graveyard to be updated
	 * @param passedRespawnMessage the new respawn message for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setRespawnMessage(final CommandSender sender,
									  final Graveyard graveyard,
									  final String passedRespawnMessage) {

		String respawnMessage = passedRespawnMessage;

		// check sender permission
		if (!sender.hasPermission("graveyard.set.respawnmessage")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_RESPAWNMESSAGE);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// if message is 'default', set message to empty string
		if (respawnMessage.equalsIgnoreCase("default")) {
			respawnMessage = "";
		}

		// create new graveyard object with new respawn message
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.respawnMessage(respawnMessage)
				.build();

		// update record in data store
		plugin.dataStore.updateGraveyard(newGraveyard);
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_SET_RESPAWNMESSAGE, newGraveyard);
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new group for existing graveyard
	 *
	 * @param sender    the player that issued the command
	 * @param graveyard the existing graveyard to be updated
	 * @param group     the new group for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setGroup(final CommandSender sender,
							 final Graveyard graveyard,
							 final String group) {

		// check sender permission
		if (!sender.hasPermission("graveyard.set.group")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_GROUP);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// create new graveyard object from existing graveyard with new group
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.group(group)
				.build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_SET_GROUP, newGraveyard);

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new safety time for existing graveyard
	 *
	 * @param sender    the player that issued the command
	 * @param graveyard the existing graveyard to be updated
	 * @param value     the new safety time for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean setSafetyTime(final CommandSender sender,
								  final Graveyard graveyard,
								  final String value) {

		// check sender permission
		if (!sender.hasPermission("graveyard.set.safetytime")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SET_SAFETYTIME);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		int safetyTime;

		if (value.equalsIgnoreCase("default")) {
			safetyTime = -1;
		}
		else {
			// try to parse entered safety time as integer
			try {
				safetyTime = Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_INTEGER);
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// create new graveyard object with from existing graveyard with new safety time
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.safetyTime(safetyTime)
				.build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		plugin.messageManager.sendMessage(sender,
				MessageId.COMMAND_SUCCESS_SET_SAFETYTIME,
				newGraveyard,
				value);

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Create new graveyard
	 *
	 * @param sender the player that issued the command
	 * @param args   passed command arguments
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 */
	private boolean createCommand(final CommandSender sender, final String[] args) {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.create")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_CREATE);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		int minArgs = 1;

		// check min arguments
		if (arguments.size() < minArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get player location
		Location location = player.getLocation();

		// set displayName to passed arguments
		String displayName = join(arguments);

		// attempt to retrieve existing graveyard from datastore
		Graveyard existingGraveyard = plugin.dataStore.selectGraveyard(displayName);

		// if graveyard does not exist, insert new graveyard in data store and return
		if (existingGraveyard == null) {

			// create new graveyard object with passed display name and player location
			Graveyard newGraveyard = new Graveyard.Builder()
					.displayName(displayName)
					.location(location)
					.build();

			// insert graveyard in data store
			plugin.dataStore.insertGraveyard(newGraveyard);

			// send success message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_CREATE, newGraveyard);

			// play sound effect
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
			return true;
		}

		// if player has overwrite permission, update record with new graveyard and return
		if (player.hasPermission("graveyard.overwrite")) {

			// create new graveyard object with passed display name and player location and existing primary key
			Graveyard newGraveyard = new Graveyard.Builder()
					.primaryKey(existingGraveyard.getPrimaryKey())
					.displayName(displayName)
					.location(location)
					.build();

			// update graveyard in data store
			plugin.dataStore.updateGraveyard(newGraveyard);

			// send success message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_CREATE, newGraveyard);

			// play sound effect
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
			return true;
		}

		// send graveyard exists error message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CREATE_EXISTS, existingGraveyard);

		// play sound effect
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return true;
	}


	/**
	 * Remove graveyard
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean deleteCommand(final CommandSender sender, final String[] args) {

		// check for permission
		if (!sender.hasPermission("graveyard.delete")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_DELETE);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		int minArgs = 2;

		// check min arguments
		if (args.length < minArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			displayUsage(sender, subcommand);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set displayName to passed arguments
		String displayName = join(arguments);

		// delete graveyard record from storage
		Graveyard graveyard = plugin.dataStore.deleteGraveyard(displayName);

		// if graveyard is null, send not found error message
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD, dummyGraveyard);

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message to player
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_DELETE, graveyard);

		// play sound effect
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_DELETE);
		return true;
	}


	/**
	 * Display a single graveyard's settings
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean showCommand(final CommandSender sender, final String[] args) {

		// if command sender does not have permission to show graveyards, output error message and return true
		if (!sender.hasPermission("graveyard.show")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_SHOW);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int minArgs = 2;

		// if too few arguments, display error and usage messages and return
		if (args.length < minArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// get display name from remaining arguments joined with spaces
		String displayName = join(arguments);

		// retrieve graveyard from data store
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if retrieved graveyard is null, display error and usage messages and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard notGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD, notGraveyard);

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// display graveyard display name
		sender.sendMessage(ChatColor.DARK_AQUA + "Name: "
				+ ChatColor.RESET + graveyard.getDisplayName());

		// display graveyard 'enabled' setting
		sender.sendMessage(ChatColor.DARK_AQUA + "Enabled: "
				+ ChatColor.RESET + graveyard.isEnabled());

		// display graveyard 'hidden' setting
		sender.sendMessage(ChatColor.DARK_AQUA + "Hidden: "
				+ ChatColor.RESET + graveyard.isHidden());

		// if graveyard discovery range is set to non-negative value, display it; else display configured default
		if (graveyard.getDiscoveryRange() >= 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Discovery Range: "
					+ ChatColor.RESET + graveyard.getDiscoveryRange() + " blocks");
		}
		else {
			sender.sendMessage(ChatColor.DARK_AQUA + "Discovery Range: "
					+ ChatColor.RESET + plugin.getConfig().getInt("discovery-range") + " blocks (default)");
		}

		// get custom discovery message and display if not null or empty
		if (graveyard.getDiscoveryMessage() != null && !graveyard.getDiscoveryMessage().isEmpty()) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Custom Discovery Message: "
					+ ChatColor.RESET + graveyard.getDiscoveryMessage());
		}

		// get custom respawn message and display if not null or empty
		if (graveyard.getRespawnMessage() != null && !graveyard.getRespawnMessage().isEmpty()) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Custom Respawn Message: "
					+ ChatColor.RESET + graveyard.getRespawnMessage());
		}

		// if graveyard safety time is set to non-negative value, display it; else display configured default
		if (graveyard.getSafetyTime() >= 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
					+ ChatColor.RESET + graveyard.getSafetyTime() + " seconds");
		}
		else {
			sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
					+ ChatColor.RESET + plugin.getConfig().getInt("safety-time") + " seconds (default)");
		}

		// get graveyard group; if null or empty, set to ALL
		String group = graveyard.getGroup();
		if (group == null || group.isEmpty()) {
			group = "ALL";
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "Group: "
				+ ChatColor.RESET + group);

		// display graveyard location
		Location location = graveyard.getLocation();
		String locationString = ChatColor.DARK_AQUA + "Location: "
				+ ChatColor.RESET + "["
				+ ChatColor.AQUA + plugin.messageManager.getWorldName(location)
				+ ChatColor.RESET + "] "
				+ ChatColor.RESET + "X: " + ChatColor.AQUA + location.getBlockX() + " "
				+ ChatColor.RESET + "Y: " + ChatColor.AQUA + location.getBlockY() + " "
				+ ChatColor.RESET + "Z: " + ChatColor.AQUA + location.getBlockZ() + " "
				+ ChatColor.RESET + "P: " + ChatColor.GOLD + String.format("%.2f", location.getPitch()) + " "
				+ ChatColor.RESET + "Y: " + ChatColor.GOLD + String.format("%.2f", location.getYaw());
		sender.sendMessage(locationString);
		return true;
	}


	/**
	 * List Graveyard names
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean listCommand(final CommandSender sender, final String[] args) {

		// if command sender does not have permission to list graveyards, output error message and return true
		if (!sender.hasPermission("graveyard.list")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_LIST);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String subcommand = args[0];

		// argument limits
		int maxArgs = 2;

		if (args.length > maxArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// set default page
		int page = 1;

		// if argument exists, try to parse as integer page number
		if (args.length == 2) {
			try {
				page = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e) {
				// second argument not a page number, let default of 1 stand
			}
		}
		page = Math.max(1, page);

		int itemsPerPage = plugin.getConfig().getInt("list-page-size");

		// get all records from datastore
		final List<Graveyard> allRecords = plugin.dataStore.selectAllGraveyards();

		if (plugin.debug) {
			plugin.getLogger().info("Total records fetched from db: " + allRecords.size());
		}

		// get undiscovered searchKeys for player
		List<String> undiscoveredIds = new ArrayList<>();
		if (sender instanceof Player) {
			undiscoveredIds.addAll(plugin.dataStore.getUndiscoveredKeys((Player) sender));
		}

		// create empty list of records
		List<Graveyard> displayRecords = new ArrayList<>();

		for (Graveyard graveyard : allRecords) {

			// if graveyard is not enabled and sender does not have override permission, do not add to display list
			if (!graveyard.isEnabled() && !sender.hasPermission("graveyard.list.disabled")) {
				if (plugin.debug) {
					plugin.getLogger().info(graveyard.getDisplayName()
							+ " is disabled and player does not have graveyard.list.disabled permission.");
				}
				continue;
			}

			// if graveyard is undiscovered and sender does not have override permission, do not add to display list
			if (graveyard.isHidden()
					&& undiscoveredIds.contains(graveyard.getSearchKey())
					&& !sender.hasPermission("graveyard.list.hidden")) {
				if (plugin.debug) {
					plugin.getLogger().info(graveyard.getDisplayName()
							+ " is undiscovered and player does not have graveyard.list.hidden permission.");
				}
				continue;
			}

			// if graveyard has group set and sender does not have group permission, do not add to display list
			String group = graveyard.getGroup();
			if (group != null && !group.isEmpty() && !sender.hasPermission("group." + graveyard.getGroup())) {
				if (plugin.debug) {
					plugin.getLogger().info(graveyard.getDisplayName()
							+ " is in group that player does not have permission.");
				}
				continue;
			}

			// add graveyard to display list
			displayRecords.add(graveyard);
		}

		// if display list is empty, output list empty message and return
		if (displayRecords.isEmpty()) {
			plugin.messageManager.sendMessage(sender, MessageId.LIST_EMPTY);
			return true;
		}

		// get page count
		int pageCount = ((displayRecords.size() - 1) / itemsPerPage) + 1;
		if (page > pageCount) {
			page = pageCount;
		}
		int startIndex = ((page - 1) * itemsPerPage);
		int endIndex = Math.min((page * itemsPerPage), displayRecords.size());

		List<Graveyard> displayRange = displayRecords.subList(startIndex, endIndex);

		int itemNumber = startIndex;

		// display list header
		plugin.messageManager.listAnnotation(sender, MessageId.LIST_HEADER, page, pageCount);

		for (Graveyard graveyard : displayRange) {

			// increment item number
			itemNumber++;

			// display disabled list item
			if (!graveyard.isEnabled()) {
				plugin.messageManager.listItem(sender, MessageId.LIST_ITEM_DISABLED, graveyard, itemNumber);
				continue;
			}

			// display undiscovered list item
			if (graveyard.isHidden() && undiscoveredIds.contains(graveyard.getSearchKey())) {
				plugin.messageManager.listItem(sender, MessageId.LIST_ITEM_UNDISCOVERED, graveyard, itemNumber);
				continue;
			}

			// display normal list item
			plugin.messageManager.listItem(sender, MessageId.LIST_ITEM, graveyard, itemNumber);
		}

		// display list footer
		plugin.messageManager.listAnnotation(sender, MessageId.LIST_FOOTER, page, pageCount);
		return true;
	}


	/**
	 * Display closest graveyard that is known to player and otherwise allowed
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean closestCommand(final CommandSender sender) {

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.closest")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_CLOSEST);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get nearest graveyard
		Graveyard graveyard = plugin.dataStore.selectNearestGraveyard(player);

		// if no graveyard returned from datastore, send failure message and return
		if (graveyard == null) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CLOSEST_NO_MATCH);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message
		plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_CLOSEST, graveyard);
		return true;
	}


	/**
	 * Teleport player to graveyard location
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean teleportCommand(final CommandSender sender, final String[] args) {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.teleport")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_TELEPORT);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int minArgs = 2;

		if (args.length < minArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get display name from remaining arguments
		String displayName = join(arguments);

		// get graveyard from datastore
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if graveyard does not exist, send message and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard notGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD, notGraveyard);

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// teleport player to graveyard location
		Location destination = graveyard.getLocation();

		// play teleport departure sound
		plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_DEPARTURE);
		if (player.teleport(destination, TeleportCause.PLUGIN)) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_TELEPORT, graveyard);
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_ARRIVAL);
		}
		else {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_TELEPORT, graveyard);
		}
		return true;
	}


	/**
	 * Remove graveyard discovery record for player
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean forgetCommand(final CommandSender sender, final String[] args) {

		// check for permission
		if (!sender.hasPermission("graveyard.forget")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_FORGET);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int minArgs = 3;

		// check for minimum arguments
		if (args.length < minArgs) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// get graveyard search key
		String searchKey = arguments.remove(0);

		// get graveyard (for messages)
		Graveyard graveyard = plugin.dataStore.selectGraveyard(searchKey);

		// if no matching graveyard found, send message and return
		if (graveyard == null) {

			// create dummy graveyard for message
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(searchKey).build();

			// send graveyard not found message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_FORGET_INVALID_GRAVEYARD, dummyGraveyard);

			// play command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get player name
		String playerName = arguments.remove(0);

		// get offline player from passed player name
		@SuppressWarnings("deprecation")
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);

		// if player not found, send message and return
		if (player == null) {
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_FORGET_INVALID_PLAYER);
			return true;
		}

		// delete discovery record
		if (plugin.dataStore.deleteDiscovery(searchKey, player.getUniqueId())) {

			// send success message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_SUCCESS_FORGET, graveyard, player);

			// play success sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_FORGET);
		}
		else {
			// send failure message
			plugin.messageManager.sendMessage(sender, MessageId.COMMAND_FAIL_FORGET, graveyard, player);

			// send command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		}
		return true;
	}


	/**
	 * Display help message for commands
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean helpCommand(final CommandSender sender, final String[] args) {

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.help")) {
			plugin.messageManager.sendMessage(sender, MessageId.PERMISSION_DENIED_HELP);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set default command
		String command = "help";

		// get passed command
		if (args.length > 1) {
			command = args[1];
		}

		// set failure message
		String helpMessage = "That is not a valid command.";

		if (command.equalsIgnoreCase("create")) {
			helpMessage = "Creates a graveyard at current player location.";
		}
		if (command.equalsIgnoreCase("delete")) {
			helpMessage = "Removes a graveyard location.";
		}
		if (command.equalsIgnoreCase("forget")) {
			helpMessage = "Remove graveyard from player's memory.";
		}
		if (command.equalsIgnoreCase("help")) {
			helpMessage = "Displays help for graveyard commands.";
		}
		if (command.equalsIgnoreCase("list")) {
			helpMessage = "Displays a list of all graveyard locations.";
		}
		if (command.equalsIgnoreCase("reload")) {
			helpMessage = "Reloads the configuration without needing to restart the server.";
		}
		if (command.equalsIgnoreCase("set")) {
			helpMessage = "Update a graveyard attribute with a new value.";
		}
		if (command.equalsIgnoreCase("show")) {
			helpMessage = "Display a graveyard's settings.";
		}
		if (command.equalsIgnoreCase("status")) {
			helpMessage = "Displays current configuration settings.";
		}
		if (command.equalsIgnoreCase("teleport")) {
			helpMessage = "Teleport player to graveyard location.";
		}
		sender.sendMessage(HELP_COLOR + helpMessage);
		displayUsage(sender, command);
		return true;
	}


	/**
	 * Display command usage
	 *
	 * @param sender        the command sender
	 * @param passedCommand the command for which to display usage
	 */
	private void displayUsage(final CommandSender sender, final String passedCommand) {

		String command = passedCommand;

		if (command.isEmpty() || command.equalsIgnoreCase("help")) {
			command = "all";
		}
		if ((command.equalsIgnoreCase("status")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.status")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard status");
		}
		if ((command.equalsIgnoreCase("reload")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.reload")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard reload");
		}
		if ((command.equalsIgnoreCase("create")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.create")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard create <graveyard>");
		}
		if ((command.equalsIgnoreCase("delete")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.delete")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard delete <graveyard>");
		}
		if ((command.equalsIgnoreCase("forget")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.forget")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard forget <graveyard> <player>");
		}
		if ((command.equalsIgnoreCase("help")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.help")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard help [command]");
		}
		if ((command.equalsIgnoreCase("list")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.list")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard list [page]");
		}
		if ((command.equalsIgnoreCase("set")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.set")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard set <graveyard> <attribute> <value>");
		}
		if ((command.equalsIgnoreCase("show")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.show")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard show <graveyard>");
		}
		if ((command.equalsIgnoreCase("teleport")
				|| command.equalsIgnoreCase("tp")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.teleport")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard teleport <name>");
		}
	}


	/**
	 * Join list of strings into one string with spaces
	 *
	 * @param stringList List of String to join with spaces
	 * @return the joined String
	 */
	private String join(final List<String> stringList) {

		StringBuilder returnString = new StringBuilder();

		for (String string : stringList) {
			returnString.append(" ").append(string);
		}
		return returnString.toString().trim();
	}

}
