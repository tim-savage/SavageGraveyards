package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.*;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Set command implementation<br>
 * changes graveyard settings
 */
public class SetCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;

	private final static int CONFIG_DEFAULT = -1;

	// list of possible attributes
	private final static List<String> ATTRIBUTES =
			Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
					"enabled", "hidden", "location", "name", "safetytime",
					"discoveryrange", "discoverymessage", "respawnmessage")));


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	SetCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("set");
		this.setUsage("/graveyard set <graveyard> <attribute> <value>");
		this.setDescription(COMMAND_HELP_SET);
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		List<String> returnList = new ArrayList<>();

		if (args.length == 2) {
			// return list of valid matching graveyard names
			returnList = plugin.dataStore.selectMatchingGraveyardNames(args[1]);
		}

		else if (args.length == 3) {

			for (String attribute : ATTRIBUTES) {
				if (sender.hasPermission("graveyard.set." + attribute)
						&& attribute.startsWith(args[2])) {
					returnList.add(attribute);
				}
			}
		}

		return returnList;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		int minArgs = 3;

		// check min arguments
		if (args.size() < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send(plugin.languageHandler);
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get graveyard name from arguments ArrayList
		String displayName = args.remove(0);

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
		String attribute = args.remove(0);

		// get value by joining remaining arguments
		String value = String.join(" ", args).trim();

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
		Message.create(sender, COMMAND_FAIL_INVALID_ATTRIBUTE).send(plugin.languageHandler);
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
			Message.create(sender, COMMAND_FAIL_CONSOLE).send(plugin.languageHandler);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// check player permission
		if (!player.hasPermission("graveyard.set.location")) {
			Message.create(sender, PERMISSION_DENIED_SET_LOCATION).send(plugin.languageHandler);
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
				.send(plugin.languageHandler);

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
			Message.create(sender, PERMISSION_DENIED_SET_NAME).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get new name from passed string trimmed
		String newName = passedString.trim();

		// if new name is blank, send invalid name message
		if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', newName)).isEmpty()) {
			Message.create(sender, COMMAND_FAIL_SET_INVALID_NAME).send(plugin.languageHandler);
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
				.send(plugin.languageHandler);

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
			Message.create(sender, PERMISSION_DENIED_SET_ENABLED).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get value from passed string trimmed
		String value = passedString;
		boolean enabled;

		// if value is empty, set to true
		if (value.isEmpty()) {
			value = "true";
		}

		// if value is "default", set to configured default setting
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
			Message.create(sender, COMMAND_FAIL_SET_INVALID_BOOLEAN).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set value to string representation of enabled boolean
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
				.send(plugin.languageHandler);

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
			Message.create(sender, PERMISSION_DENIED_SET_HIDDEN).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get value from passed string
		String value = passedString;
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
			Message.create(sender, COMMAND_FAIL_SET_INVALID_BOOLEAN).send(plugin.languageHandler);
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
				.send(plugin.languageHandler);

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
			Message.create(sender, PERMISSION_DENIED_SET_DISCOVERYRANGE).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// declare discovery range to be set
		int discoveryRange = CONFIG_DEFAULT;

		// if no distance given, or string "default",
		// set to CONFIG_DEFAULT to use configured default value
		if (passedString.isEmpty() || passedString.equalsIgnoreCase("default")) {
			//noinspection ConstantConditions
			discoveryRange = CONFIG_DEFAULT;
		}

		// if value is string "player", attempt to use player distance
		else if (passedString.equalsIgnoreCase("player")
				|| passedString.equalsIgnoreCase("current")) {

			// if sender is player, use player's current distance
			if (sender instanceof Player && graveyard.getLocation() != null) {
				Player player = (Player) sender;
				// check that player is in same world as graveyard
				if (player.getWorld().getUID().equals(graveyard.getWorldUid())) {
					discoveryRange = (int) player.getLocation().distance(graveyard.getLocation());
				}
			}
		}
		else {
			// try to parse entered range as integer
			try {
				discoveryRange = Integer.parseInt(passedString);
			}
			catch (NumberFormatException e) {
				Message.create(sender, COMMAND_FAIL_SET_INVALID_INTEGER).send(plugin.languageHandler);
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
					.setMacro(VALUE, plugin.getConfig().getInt("discovery-range"))
					.send(plugin.languageHandler);
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_DISCOVERYRANGE)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(VALUE, String.valueOf(discoveryRange))
					.send(plugin.languageHandler);
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
			Message.create(sender, PERMISSION_DENIED_SET_DISCOVERYMESSAGE).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get discovery message from passed string
		String discoveryMessage = passedString;

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
					.send(plugin.languageHandler);
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_DISCOVERYMESSAGE)
					.setMacro(GRAVEYARD, newGraveyard)
					.send(plugin.languageHandler);
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
			Message.create(sender, PERMISSION_DENIED_SET_RESPAWNMESSAGE).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get respawn message from passed string
		String respawnMessage = passedString;

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
					.send(plugin.languageHandler);
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_RESPAWNMESSAGE)
					.setMacro(GRAVEYARD, newGraveyard)
					.send(plugin.languageHandler);
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
			Message.create(sender, PERMISSION_DENIED_SET_GROUP).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// create new graveyard object from existing graveyard with new group
		Graveyard newGraveyard = new Graveyard.Builder(graveyard).group(passedString).build();

		// update graveyard record in datastore
		plugin.dataStore.updateGraveyard(newGraveyard);

		// send success message
		Message.create(sender, COMMAND_SUCCESS_SET_GROUP)
				.setMacro(GRAVEYARD, newGraveyard)
				.setMacro(VALUE, passedString)
				.send(plugin.languageHandler);

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
			Message.create(sender, PERMISSION_DENIED_SET_SAFETYTIME).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// declare safety time to be set
		int safetyTime;

		// if passed string is "default" or empty, set safety time to negative to use configured default
		if (passedString.equalsIgnoreCase("default") || passedString.isEmpty()) {
			safetyTime = CONFIG_DEFAULT;
		}
		else {
			// try to parse entered safety time as integer
			try {
				safetyTime = Integer.parseInt(passedString);
			}
			catch (NumberFormatException e) {
				Message.create(sender, COMMAND_FAIL_SET_INVALID_INTEGER).send(plugin.languageHandler);
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
		if (safetyTime == CONFIG_DEFAULT) {
			Message.create(sender, COMMAND_SUCCESS_SET_SAFETYTIME_DEFAULT)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(DURATION, TimeUnit.SECONDS.toMillis(plugin.getConfig().getInt("safety-time")))
					.send(plugin.languageHandler);
		}
		else {
			Message.create(sender, COMMAND_SUCCESS_SET_SAFETYTIME)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(DURATION, TimeUnit.SECONDS.toMillis(safetyTime))
					.send(plugin.languageHandler);
		}

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
		return true;
	}

}
