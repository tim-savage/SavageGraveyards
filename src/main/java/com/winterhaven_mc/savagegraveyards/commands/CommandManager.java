package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.storage.DataStore;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;

import com.winterhaven_mc.util.LanguageManager;
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

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;
import static com.winterhaven_mc.savagegraveyards.messages.Macro.*;


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

	// constant value for integer attributes to use configured default
	private final static int CONFIG_DEFAULT = -1;

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
	@SuppressWarnings("ConstantConditions")
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
				Collection<UUID> playerUUIDs = plugin.dataStore.selectPlayersDiscovered(args[1]);

				// iterate over list of playerUUIDs and add player names to return list that match prefix
				for (UUID playerUUID : playerUUIDs) {

					// get player name from UUID
					String playerName = plugin.getServer().getOfflinePlayer(playerUUID).getName();

					// if player name begins with arg[2] (ignoring case), add player name to return list
					if (playerName != null && playerName.toLowerCase().startsWith(args[2].toLowerCase())) {
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

		// handle subcommands
		switch (subcommand.toLowerCase()) {

			case "closest":
			case "nearest":
				return closestCommand(sender);

			case "status":
				return statusCommand(sender);

			case "reload":
				return reloadCommand(sender, args);

			case"create":
				return createCommand(sender, args);

			case "delete":
				return deleteCommand(sender, args);

			case "list":
				return listCommand(sender, args);

			case "set":
				return setCommand(sender, args);

			case "show":
				return showCommand(sender, args);

			case "teleport":
			case "tp":
				return teleportCommand(sender, args);

			case "forget":
				return forgetCommand(sender, args);

			case "help":
				return helpCommand(sender, args);
		}

		Message.create(sender, COMMAND_FAIL_INVALID_COMMAND).send();
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		displayUsage(sender, "help");
		return true;
	}


	/**
	 * Display plugin settings
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean statusCommand(final CommandSender sender) {

		// check for null parameter
		Objects.requireNonNull(sender);

		// if command sender does not have permission to view status, output error message and return true
		if (!sender.hasPermission("graveyard.status")) {
			Message.create(sender, PERMISSION_DENIED_STATUS).send();
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
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean reloadCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("graveyard.reload")) {
			Message.create(sender, PERMISSION_DENIED_RELOAD).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String subcommand = args[0];

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.length > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender, subcommand);
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// reload enabled worlds
		plugin.worldManager.reload();

		// reload messages
		LanguageManager.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// reload datastore
		DataStore.reload();

		// set debug field
		plugin.debug = plugin.getConfig().getBoolean("debug");

		// send reloaded message
		Message.create(sender, COMMAND_SUCCESS_RELOAD).send();
		return true;
	}


	/**
	 * Set new value for graveyard attribute
	 *
	 * @param sender the player issuing the command
	 * @param args   the command arguments
	 * @return always return {@code true} to suppress bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setCommand(final CommandSender sender, final String[] args) {

		// Example usage:
		// graveyard set <graveyard> displayname <new_name>
		// graveyard set <graveyard> location
		// graveyard set <graveyard> enabled <true|false|default>
		// graveyard set <graveyard> hidden <true|false|default>
		// graveyard set <graveyard> discoverymessage <message>
		// graveyard set <graveyard> respawnmessage <message>
		// graveyard set <graveyard> safetytime <seconds>
		// graveyard set <graveyard> group <group>

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// convert args to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		int minArgs = 3;

		// check min arguments
		if (args.length < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
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

			// send command fail message
			Message.create(sender, COMMAND_FAIL_NO_RECORD).setMacro(GRAVEYARD, dummyGraveyard);

			// play command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get attribute name and remove from arguments ArrayList
		String attribute = arguments.remove(0);

		// get value by joining remaining arguments
		String value = String.join(" ", arguments);

		switch (attribute.toLowerCase()) {
			case "location":
				return setLocation(sender, graveyard);

			case "name":
				return setName(sender, graveyard, value);

			case "enabled":
				return setEnabled(sender, graveyard, value);

			case "hidden":
				return setHidden(sender, graveyard, value);

			case "discoveryrange":
				return setDiscoveryRange(sender, graveyard, value);

			case "discoverymessage":
				return setDiscoveryMessage(sender, graveyard, value);

			case "respawnmessage":
				return setRespawnMessage(sender, graveyard, value);

			case "group":
				return setGroup(sender, graveyard, value);

			case "safetytime":
				return setSafetyTime(sender, graveyard, value);
		}

		// no matching attribute, send error message
		Message.create(sender, COMMAND_FAIL_INVALID_ATTRIBUTE).send();
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return true;
	}


	/**
	 * Set new location for existing graveyard
	 *
	 * @param sender    the player that issued the command
	 * @param graveyard the existing graveyard to be updated
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setLocation(final CommandSender sender, final Graveyard graveyard) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// check player permission
		if (!player.hasPermission("graveyard.set.location")) {
			Message.create(sender, PERMISSION_DENIED_SET_LOCATION).send();
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
		Message.create(player, COMMAND_SUCCESS_SET_LOCATION)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(LOCATION, newGraveyard.getLocation())
				.send();

		// play success sound
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new display name for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new display name for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setName(final CommandSender sender,
							final Graveyard graveyard,
							final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.name")) {
			Message.create(sender, PERMISSION_DENIED_SET_NAME).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get new name from passed string trimmed
		String newName = passedString.trim();

		// if new name is blank, send invalid name message
		if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', newName)).isEmpty()) {
			Message.create(sender, COMMAND_FAIL_SET_INVALID_NAME).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get original name
		final String oldName = graveyard.getDisplayName();

		// create new graveyard object from existing graveyard with new name
		Graveyard newGraveyard = new Graveyard.Builder(graveyard).displayName(newName).build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		Message.create(sender, COMMAND_SUCCESS_SET_NAME)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(VALUE, oldName)
				.send();

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new enabled setting for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new enabled setting for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setEnabled(final CommandSender sender,
							   final Graveyard graveyard,
							   final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.enabled")) {
			Message.create(sender, PERMISSION_DENIED_SET_ENABLED).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get value from passed string trimmed
		String value = passedString.trim();
		boolean enabled;

		// if value is empty, set to true
		if (value.isEmpty()) {
			value = "true";
		}

		if (value.equalsIgnoreCase("default")) {
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
			Message.create(sender, COMMAND_FAIL_SET_INVALID_BOOLEAN).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set value to string representation of boolean
		value = String.valueOf(enabled);

		// create new graveyard object from existing graveyard with new enabled setting
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.enabled(enabled)
				.build();

		// update record in data store
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		Message.create(sender, COMMAND_SUCCESS_SET_ENABLED)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(VALUE, value)
				.send();

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new hidden setting for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new hidden setting for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setHidden(final CommandSender sender,
							  final Graveyard graveyard,
							  final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.hidden")) {
			Message.create(sender, PERMISSION_DENIED_SET_HIDDEN).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get value from passed string trimmed
		String value = passedString.trim();
		boolean hidden;

		// if value is empty, set to true
		if (value.isEmpty()) {
			value = "true";
		}

		if (value.equalsIgnoreCase("default")) {
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
			Message.create(sender, COMMAND_FAIL_SET_INVALID_BOOLEAN).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set value to string representation of boolean
		value = String.valueOf(hidden);

		// create new graveyard object from existing graveyard with new hidden setting
		Graveyard newGraveyard = new Graveyard.Builder(graveyard).hidden(hidden).build();

		// update record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		Message.create(sender, COMMAND_SUCCESS_SET_HIDDEN)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(VALUE, value)
				.send();

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new discovery range for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new hidden setting for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setDiscoveryRange(final CommandSender sender,
									  final Graveyard graveyard,
									  final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.discoveryrange")) {
			Message.create(sender, PERMISSION_DENIED_SET_DISCOVERYRANGE).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get value from passed string trimmed
		String value = passedString.trim();

		// declare discovery range to be set
		int discoveryRange;

		// if passed string is "default", set discovery range to negative to use configured default
		if (value.equalsIgnoreCase("default")) {
			discoveryRange = CONFIG_DEFAULT;
		}

		// if no distance given...
		else if (value.isEmpty()) {

			// if sender is player, use player's current distance
			if (sender instanceof Player && graveyard.getLocation() != null) {
				Player player = (Player) sender;
				discoveryRange = (int) player.getLocation().distance(graveyard.getLocation());
			}

			// if command sender is not in game player, set negative discovery range to use configured default
			else {
				discoveryRange = CONFIG_DEFAULT;
			}
		}
		else {
			// try to parse entered range as integer
			try {
				discoveryRange = Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				Message.create(sender, COMMAND_FAIL_SET_INVALID_INTEGER).send();
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// create new graveyard object from existing graveyard with new discovery range
		Graveyard newGraveyard = new Graveyard.Builder(graveyard)
				.discoveryRange(discoveryRange)
				.build();

		// update graveyard in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		if (discoveryRange < 0) {
			Message.create(sender, COMMAND_SUCCESS_SET_DISCOVERYRANGE_DEFAULT)
					.setMacro(GRAVEYARD, newGraveyard)
					.send();
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_DISCOVERYRANGE)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(VALUE, String.valueOf(discoveryRange))
					.send();
		}

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new discovery message for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new discovery message for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setDiscoveryMessage(final CommandSender sender,
										final Graveyard graveyard,
										final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.discoverymessage")) {
			Message.create(sender, PERMISSION_DENIED_SET_DISCOVERYMESSAGE).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get discovery message from passed string trimmed
		String discoveryMessage = passedString.trim();

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
		if (discoveryMessage.isEmpty()) {
			Message.create(sender, COMMAND_SUCCESS_SET_DISCOVERYMESSAGE_DEFAULT)
					.setMacro(GRAVEYARD, newGraveyard)
					.send();
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_DISCOVERYMESSAGE)
					.setMacro(GRAVEYARD, newGraveyard)
					.send();
		}

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new respawn message for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new respawn message for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setRespawnMessage(final CommandSender sender,
									  final Graveyard graveyard,
									  final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.respawnmessage")) {
			Message.create(sender, PERMISSION_DENIED_SET_RESPAWNMESSAGE).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get respawn message from passed string trimmed
		String respawnMessage = passedString.trim();

		// if message is 'default', set message to empty string
		if (respawnMessage.equalsIgnoreCase("default")) {
			respawnMessage = "";
		}

		// create new graveyard object with new respawn message
		Graveyard newGraveyard = new Graveyard.Builder(graveyard).respawnMessage(respawnMessage).build();

		// update record in data store
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		if (respawnMessage.isEmpty()) {
			Message.create(sender, COMMAND_SUCCESS_SET_RESPAWNMESSAGE_DEFAULT)
					.setMacro(GRAVEYARD, newGraveyard)
					.send();
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_RESPAWNMESSAGE)
					.setMacro(GRAVEYARD, newGraveyard)
					.send();
		}

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new group for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new group for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setGroup(final CommandSender sender,
							 final Graveyard graveyard,
							 final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.group")) {
			Message.create(sender, PERMISSION_DENIED_SET_GROUP).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get group name from passed string trimmed
		String group = passedString.trim();

		// create new graveyard object from existing graveyard with new group
		Graveyard newGraveyard = new Graveyard.Builder(graveyard).group(group).build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		Message.create(sender, COMMAND_SUCCESS_SET_GROUP)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(VALUE, group)
				.send();

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}


	/**
	 * Set new safety time for existing graveyard
	 *
	 * @param sender       the player that issued the command
	 * @param graveyard    the existing graveyard to be updated
	 * @param passedString the new safety time for the graveyard
	 * @return always returns {@code true} to suppress display of bukkit command usage
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean setSafetyTime(final CommandSender sender,
								  final Graveyard graveyard,
								  final String passedString) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(passedString);

		// check sender permission
		if (!sender.hasPermission("graveyard.set.safetytime")) {
			Message.create(sender, PERMISSION_DENIED_SET_SAFETYTIME).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get value from passed string trimmed
		String value = passedString.trim();

		// declare safety time to be set
		int safetyTime;

		// if passed string is "default" or empty, set safety time to negative to use configured default
		if (value.equalsIgnoreCase("default") || value.isEmpty()) {
			safetyTime = CONFIG_DEFAULT;
		}
		else {
			// try to parse entered safety time as integer
			try {
				safetyTime = Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				Message.create(sender, COMMAND_FAIL_SET_INVALID_INTEGER).send();
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
		Message.create(sender, COMMAND_SUCCESS_SET_SAFETYTIME)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(VALUE, value)
				.send();

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
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean createCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.create")) {
			Message.create(sender, PERMISSION_DENIED_CREATE).send();
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
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get player location
		Location location = player.getLocation();

		// set displayName to passed arguments
		String displayName = String.join(" ", arguments);

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
			Collection<Graveyard> insertSet = new HashSet<>(1);
			insertSet.add(newGraveyard);
			plugin.dataStore.insertGraveyards(insertSet);

			// send success message
			Message.create(sender, COMMAND_SUCCESS_CREATE)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(LOCATION, location)
					.send();

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
			Message.create(sender, COMMAND_SUCCESS_CREATE)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(LOCATION, newGraveyard.getLocation())
					.send();

			// play sound effect
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
			return true;
		}

		// send graveyard exists error message
		Message.create(sender, COMMAND_FAIL_CREATE_EXISTS)
				.setMacro(GRAVEYARD, existingGraveyard)
				.send();

		// play sound effect
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return true;
	}


	/**
	 * Delete graveyard
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean deleteCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// check for permission
		if (!sender.hasPermission("graveyard.delete")) {
			Message.create(sender, PERMISSION_DENIED_DELETE).send();
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
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender, subcommand);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set displayName to passed arguments
		String displayName = String.join(" ", arguments);

		// delete graveyard record from storage
		Graveyard graveyard = plugin.dataStore.deleteGraveyard(displayName);

		// if graveyard is null, send not found error message
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			Message.create(sender, COMMAND_FAIL_NO_RECORD).setMacro(GRAVEYARD, dummyGraveyard).send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message to player
		Message.create(sender, COMMAND_SUCCESS_DELETE)
				.setMacro(GRAVEYARD, graveyard)
				.send();

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
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean showCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if command sender does not have permission to show graveyards, output error message and return true
		if (!sender.hasPermission("graveyard.show")) {
			Message.create(sender, PERMISSION_DENIED_SHOW).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int minArgs = 1;

		// if too few arguments, display error and usage messages and return
		if (arguments.size() < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// get display name from remaining arguments joined with spaces
		String displayName = String.join(" ", arguments);

		// retrieve graveyard from data store
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if retrieved graveyard is null, display error and usage messages and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			Message.create(sender, COMMAND_FAIL_NO_RECORD).setMacro(GRAVEYARD, dummyGraveyard).send();

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
		if (graveyard.getSafetyTime() >= 0L) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
					+ ChatColor.RESET + graveyard.getSafetyTime() + " seconds");
		}
		else {
			sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
					+ ChatColor.RESET + plugin.getConfig().getLong("safety-time") + " seconds (default)");
		}

		// get graveyard group; if null or empty, set to ALL
		String group = graveyard.getGroup();
		if (group == null || group.isEmpty()) {
			group = "ALL";
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "Group: "
				+ ChatColor.RESET + group);

		// if world is invalid, set color to gray
		ChatColor worldColor = ChatColor.AQUA;
		if (graveyard.getLocation() == null) {
			worldColor = ChatColor.GRAY;
		}

		// display graveyard location
		String locationString = ChatColor.DARK_AQUA + "Location: "
				+ ChatColor.RESET + "["
				+ worldColor + graveyard.getWorldName()
				+ ChatColor.RESET + "] "
				+ ChatColor.RESET + "X: " + ChatColor.AQUA + Math.round(graveyard.getX()) + " "
				+ ChatColor.RESET + "Y: " + ChatColor.AQUA + Math.round(graveyard.getY()) + " "
				+ ChatColor.RESET + "Z: " + ChatColor.AQUA + Math.round(graveyard.getZ()) + " "
				+ ChatColor.RESET + "P: " + ChatColor.GOLD + String.format("%.2f", graveyard.getPitch()) + " "
				+ ChatColor.RESET + "Y: " + ChatColor.GOLD + String.format("%.2f", graveyard.getYaw());
		sender.sendMessage(locationString);
		return true;
	}


	/**
	 * List Graveyards
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean listCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if command sender does not have permission to list graveyards, output error message and return true
		if (!sender.hasPermission("graveyard.list")) {
			Message.create(sender, PERMISSION_DENIED_LIST).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int maxArgs = 1;

		if (arguments.size() > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
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
		final Collection<Graveyard> allRecords = plugin.dataStore.selectAllGraveyards();

		if (plugin.debug) {
			plugin.getLogger().info("Records fetched from datastore: " + allRecords.size());
		}

		// get undiscovered searchKeys for player
		List<String> undiscoveredKeys = new ArrayList<>();
		if (sender instanceof Player) {
			undiscoveredKeys.addAll(plugin.dataStore.selectUndiscoveredKeys((Player) sender));
		}

		// create empty list of records
		List<Graveyard> displayRecords = new ArrayList<>();

		for (Graveyard graveyard : allRecords) {

			// if graveyard has invalid location and sender has list disabled permission, add to display list
			if (graveyard.getLocation() == null) {
				if (sender.hasPermission("graveyard.list.disabled")) {
					displayRecords.add(graveyard);
				}
				continue;
			}

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
					&& undiscoveredKeys.contains(graveyard.getSearchKey())
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
			Message.create(sender, LIST_EMPTY).send();
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
		Message.create(sender, LIST_HEADER).setMacro(PAGE_NUMBER, page).setMacro(PAGE_TOTAL, pageCount).send();

		for (Graveyard graveyard : displayRange) {

			// increment item number
			itemNumber++;

			// display invalid world list item
			if (graveyard.getLocation() == null) {
				Message.create(sender, LIST_ITEM_INVALID_WORLD)
						.setMacro(GRAVEYARD, graveyard)
						.setMacro(ITEM_NUMBER, itemNumber)
						.setMacro(INVALID_WORLD, graveyard.getWorldName())
						.send();
				continue;
			}

			// display disabled list item
			if (!graveyard.isEnabled()) {

				Message.create(sender, LIST_ITEM_DISABLED)
						.setMacro(GRAVEYARD, graveyard)
						.setMacro(ITEM_NUMBER, itemNumber)
						.setMacro(LOCATION, graveyard.getLocation())
						.send();
				continue;
			}

			// display undiscovered list item
			if (graveyard.isHidden() && undiscoveredKeys.contains(graveyard.getSearchKey())) {
				Message.create(sender, LIST_ITEM_UNDISCOVERED)
						.setMacro(GRAVEYARD, graveyard)
						.setMacro(ITEM_NUMBER, itemNumber)
						.setMacro(LOCATION, graveyard.getLocation())
						.send();
				continue;
			}

			// display normal list item
			Message.create(sender, LIST_ITEM)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(ITEM_NUMBER, itemNumber)
					.setMacro(LOCATION, graveyard.getLocation())
					.send();
		}

		// display list footer
		Message.create(sender, LIST_FOOTER).setMacro(PAGE_NUMBER, page).setMacro(PAGE_TOTAL, pageCount).send();
		return true;
	}


	/**
	 * Display closest graveyard that is known to player and otherwise allowed
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean closestCommand(final CommandSender sender) {

		// check for null parameters
		Objects.requireNonNull(sender);

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.closest")) {
			Message.create(sender, PERMISSION_DENIED_CLOSEST).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get nearest graveyard
		Graveyard graveyard = plugin.dataStore.selectNearestGraveyard(player);

		// if no graveyard returned from datastore, send failure message and return
		if (graveyard == null || graveyard.getLocation() == null) {
			Message.create(sender, COMMAND_FAIL_CLOSEST_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message
		Message.create(sender, COMMAND_SUCCESS_CLOSEST)
				.setMacro(GRAVEYARD, graveyard)
				.setMacro(LOCATION, graveyard.getLocation())
				.send();
		return true;
	}


	/**
	 * Teleport player to graveyard location
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean teleportCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.teleport")) {
			Message.create(sender, PERMISSION_DENIED_TELEPORT).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int minArgs = 1;

		if (arguments.size() < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get display name from remaining arguments
		String displayName = String.join(" ", arguments);

		// get graveyard from datastore
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if graveyard does not exist, send message and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			Message.create(sender, COMMAND_FAIL_NO_RECORD)
					.setMacro(GRAVEYARD, dummyGraveyard)
					.send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get graveyard location
		Location destination = graveyard.getLocation();

		// if destination is null, send fail message and return
		if (destination == null) {

			// send message
			Message.create(sender, COMMAND_FAIL_TELEPORT_WORLD_INVALID)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(INVALID_WORLD, graveyard.getWorldName())
					.send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// play teleport departure sound
		plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_DEPARTURE);
		if (player.teleport(destination, TeleportCause.PLUGIN)) {
			Message.create(sender, COMMAND_SUCCESS_TELEPORT)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(LOCATION, graveyard.getLocation())
					.send();
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_ARRIVAL);
		}
		else {
			// send message
			Message.create(sender, COMMAND_FAIL_TELEPORT).setMacro(GRAVEYARD, graveyard).send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		}
		return true;
	}


	/**
	 * Remove graveyard discovery record for player
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean forgetCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// check for permission
		if (!sender.hasPermission("graveyard.forget")) {
			Message.create(sender, PERMISSION_DENIED_FORGET).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.remove(0);

		// argument limits
		int minArgs = 2;

		// check for minimum arguments
		if (arguments.size() < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
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
			Message.create(sender, COMMAND_FAIL_FORGET_INVALID_GRAVEYARD)
					.setMacro(GRAVEYARD, dummyGraveyard)
					.send();

			// play command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get player name
		String playerName = arguments.remove(0);

		// get list of offline players
		OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();

		OfflinePlayer player = null;

		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (playerName.equals(offlinePlayer.getName())) {
				player = offlinePlayer;
			}
		}

		// if player not found, send message and return
		if (player == null) {
			Message.create(sender, COMMAND_FAIL_FORGET_INVALID_PLAYER).send();
			return true;
		}

		// delete discovery record
		if (plugin.dataStore.deleteDiscovery(searchKey, player.getUniqueId())) {

			// send success message
			Message.create(sender, COMMAND_SUCCESS_FORGET)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(TARGET_PLAYER, player)
					.send();

			// play success sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_FORGET);
		}
		else {
			// send failure message
			Message.create(sender, COMMAND_FAIL_FORGET)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(TARGET_PLAYER, player)
					.send();

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
	 * @throws NullPointerException if any parameter is null
	 */
	private boolean helpCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.help")) {
			Message.create(sender, PERMISSION_DENIED_HELP).send();
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
		if (command.equalsIgnoreCase("closest")
				|| command.equalsIgnoreCase("nearest")) {
			helpMessage = "Display the nearest graveyard to player's current location.";
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
		if (command.equalsIgnoreCase("teleport")
				|| command.equalsIgnoreCase("tp")) {
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
	 * @throws NullPointerException if any parameter is null
	 */
	private void displayUsage(final CommandSender sender, final String passedCommand) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(passedCommand);

		String command = passedCommand.trim();

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
		if ((command.equalsIgnoreCase("closest")
				|| command.equalsIgnoreCase("nearest")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.closest")) {
			sender.sendMessage(USAGE_COLOR + "/graveyard closest");
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

}
