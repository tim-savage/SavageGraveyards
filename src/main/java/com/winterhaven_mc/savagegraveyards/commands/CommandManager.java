package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.storage.DataStoreFactory;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.savagegraveyards.util.MessageId;
import com.winterhaven_mc.savagegraveyards.util.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Implements command executor for {@code SavageGraveyards} commands.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class CommandManager implements CommandExecutor, TabCompleter {

	// reference to main class
	private final PluginMain plugin;

	private final static ChatColor helpColor = ChatColor.YELLOW;
	private final static ChatColor usageColor = ChatColor.GOLD;

	// list of possible subcommands
	private final static List<String> SUBCOMMANDS =
			Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
					"closest","create","delete","list","reload",
					"set","show","status","teleport","help")));

	// list of possible attributes
	private final static List<String> ATTRIBUTES =
			Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
					"enabled","hidden","location","name","safetytime",
					"discoveryrange","discoverymessage","respawnmessage")));


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

		// return list of valid matching death spawn names
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("teleport")
					|| args[0].equalsIgnoreCase("tp") 
					|| args[0].equalsIgnoreCase("set")
					|| args[0].equalsIgnoreCase("show")
					|| args[0].equalsIgnoreCase("delete")) {
				returnList = plugin.dataStore.selectMatchingGraveyardNames(args[1]);
			}
		}
		
		// return list of valid matching attributes
		else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
			
			for (String attribute : ATTRIBUTES) {
				if (sender.hasPermission("graveyard.set." + attribute)
						&& attribute.startsWith(args[2])) {
					returnList.add(attribute);
				}
			}
		}
		return returnList;
	}

	
	/**
	 * Command Executor for SavageGraveyards
	 * 
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
			displayUsage(sender,"all");
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
			return reloadCommand(sender,args);
		}

		//create command
		if (subcommand.equalsIgnoreCase("create")) {
			return createCommand(sender,args);
		}
		
		// delete command
		if (subcommand.equalsIgnoreCase("delete")) {
			return deleteCommand(sender,args);
		}
		
		// list command
		if (subcommand.equalsIgnoreCase("list")) {
			return listCommand(sender,args);
		}
		
		// set command
		if (subcommand.equalsIgnoreCase("set")) {
			return setCommand(sender,args);
		}
		
		// show command
		if (subcommand.equalsIgnoreCase("show")) {
			return showCommand(sender,args);
		}
		
		// teleport command
		if (subcommand.equalsIgnoreCase("teleport") || subcommand.equalsIgnoreCase("tp")) {
			return teleportCommand(sender,args);
		}
		
		// help command
		if (subcommand.equalsIgnoreCase("help")) {
			return helpCommand(sender,args);
		}
		
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_INVALID_COMMAND);
		plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
		displayUsage(sender,"help");
		return true;
	}


	/**
	 * Display plugin settings
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean statusCommand (final CommandSender sender) {
		
		// if command sender does not have permission to view status, output error message and return true
		if (!sender.hasPermission("graveyard.status")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_STATUS);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
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

		sender.sendMessage(ChatColor.GREEN + "Discovery check interval: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-interval") + " ticks");

		sender.sendMessage(ChatColor.GREEN + "Default safety time: "
				+ ChatColor.RESET + plugin.getConfig().getInt("safety-time") + " seconds");

		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());

		return true;
	}
	
	
	/**
	 * Reload plugin settings
	 * @param sender the command sender
	 * @param args the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean reloadCommand(final CommandSender sender, final String args[]) {
		
		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("graveyard.reload")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_RELOAD);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String subcommand = args[0];
		
		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.length > maxArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER);
			displayUsage(sender, subcommand);
			return true;
		}
		
		// reload main configuration
		plugin.reloadConfig();

		// reload enabled worlds
		plugin.worldManager.reload();
		
		// reload messages
		plugin.messageManager.reload();

		// reload datastore
		DataStoreFactory.reload();
		
		// set debug field
		plugin.debug = plugin.getConfig().getBoolean("debug");
		
		// send reloaded message
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_RELOAD);
		return true;
	}
	
	
	private boolean setCommand(final CommandSender sender, final String args[]) {
		
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
		String subcommand = arguments.get(0);
		
		// remove subcommand from ArrayList
		arguments.remove(0);

		int minArgs = 3;
		
		// check min arguments
		if (args.length < minArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}
		
		// get deathspawn name
		String graveyardName = arguments.get(0);
		
		// remove name from arguments ArrayList
		arguments.remove(0);
		
		// get deathspawn key
		String key = Graveyard.deriveKey(graveyardName);
		
		if (plugin.debug) {
			plugin.getLogger().info("Entered key: " + key);
		}
		
		Graveyard graveyard = plugin.dataStore.selectGraveyard(key);
		
		if (graveyard == null) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// get attribute name
		String attribute = arguments.get(0);
		
		// remove attribute from arguments ArrayList
		arguments.remove(0);
		
		// get value by joining remaining arguments
		String value = join(arguments);
		
		if (attribute.equalsIgnoreCase("location")) {
			return setLocation(sender,graveyard);
		}
		
		if (attribute.equalsIgnoreCase("name")) {
			return setName(sender,graveyard,value);
		}
		
		if (attribute.equalsIgnoreCase("enabled")) {
			return setEnabled(sender,graveyard,value);
		}
		
		if (attribute.equalsIgnoreCase("hidden")) {
			return setHidden(sender,graveyard,value);
		}
		
		if (attribute.equalsIgnoreCase("discoveryrange")) {
			return setDiscoveryRange(sender,graveyard,value);
		}
		
		if (attribute.equalsIgnoreCase("discoverymessage")) {
			return setDiscoveryMessage(sender,graveyard,value);
		}

		if (attribute.equalsIgnoreCase("respawnmessage")) {
			return setRespawnMessage(sender,graveyard,value);
		}
		
		if (attribute.equalsIgnoreCase("group")) {
			return setGroup(sender,graveyard,value);
		}
		
		if (attribute.equalsIgnoreCase("safetytime")) {
			return setSafetyTime(sender,graveyard,value);
		}
		
		// no matching attribute, send error message
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_INVALID_ATTRIBUTE);
		plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
		return true;
	}
	
	
	private boolean setLocation(final CommandSender sender, final Graveyard graveyard) {
		
		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}
		
		Player player = (Player) sender;
		
		// check player permission
		if (!player.hasPermission("graveyard.set.location")) {
			plugin.messageManager.sendPlayerMessage(player, MessageId.PERMISSION_DENIED_SET_LOCATION,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		graveyard.setLocation(player.getLocation());
		
		plugin.dataStore.updateGraveyard(graveyard);
		
		plugin.messageManager.sendPlayerMessage(player, MessageId.COMMAND_SUCCESS_SET_LOCATION,graveyard.getDisplayName());
		plugin.messageManager.sendPlayerSound(player,SoundId.COMMAND_SUCCESS_SET);
		return true;	
	}
	
	
	private boolean setName(final CommandSender sender, final Graveyard graveyard, final String newName) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.name")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_NAME,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// get original name
		final String originalName = graveyard.getDisplayName();

		// set new graveyard displayName
		graveyard.setDisplayName(newName);
		
		// save record with new name to data store
		plugin.dataStore.updateGraveyard(graveyard);
		
		// send success message
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_NAME,
				originalName,newName.replace('_', ' '));
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}

	
	private boolean setEnabled(final CommandSender sender, final Graveyard graveyard, String value) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.enabled")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_ENABLED,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// if value is null or empty, set to true
		if (value == null || value.isEmpty()) {
			value = "true";
		}
		
		if (value.equalsIgnoreCase("default")) {
			value = plugin.getConfig().getString("default-enabled");
			graveyard.setHidden(plugin.getConfig().getBoolean("default-enabled"));
		}
		else if (value.equalsIgnoreCase("true") 
				|| value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("y")) {
			graveyard.setEnabled(true);
		}
		else if (value.equalsIgnoreCase("false") 
				|| value.equalsIgnoreCase("no")
				|| value.equalsIgnoreCase("n")) {
			graveyard.setEnabled(false);
		}
		else {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_BOOLEAN);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// update record in data store
		plugin.dataStore.updateGraveyard(graveyard);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_ENABLED,
				graveyard.getDisplayName(),value);
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	private boolean setHidden(final CommandSender sender, final Graveyard graveyard, String value) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.hidden")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_HIDDEN,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// if value is null or empty, set to true
		if (value == null || value.isEmpty()) {
			value = "true";
		}
		
		if (value.equalsIgnoreCase("default")) {
			value = plugin.getConfig().getString("default-hidden");
			graveyard.setHidden(plugin.getConfig().getBoolean("default-hidden"));
		}
		else if (value.equalsIgnoreCase("true") 
				|| value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("y")) {
			graveyard.setHidden(true);
		}
		else if (value.equalsIgnoreCase("false") 
				|| value.equalsIgnoreCase("no")
				|| value.equalsIgnoreCase("n")) {
			graveyard.setHidden(false);
		}
		else {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_BOOLEAN);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// update record in data store
		plugin.dataStore.updateGraveyard(graveyard);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_HIDDEN,
				graveyard.getDisplayName(),value);
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	private boolean setDiscoveryRange(final CommandSender sender, final Graveyard graveyard, String value) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.discoveryrange")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_DISCOVERYRANGE,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
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
				plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
				plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
				return true;					
			}
		}
		else {
			// try to parse entered range as integer
			try {
				discoveryRange = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_INTEGER);
				plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}
		
		// set new range
		if (discoveryRange > 0) {
			graveyard.setDiscoveryRange(discoveryRange);
			plugin.dataStore.updateGraveyard(graveyard);
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_DISCOVERYRANGE,
					graveyard.getDisplayName(),String.valueOf(discoveryRange));
			plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		}
		return true;
	}
	
	
	private boolean setDiscoveryMessage(final CommandSender sender, final Graveyard graveyard, String discoveryMessage) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.discoverymessage")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_DISCOVERYMESSAGE,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// if message is 'default', set message to empty string
		if (discoveryMessage.equalsIgnoreCase("default")) {
			discoveryMessage = "";
		}
		
		// set new message
		graveyard.setDiscoveryMessage(discoveryMessage);
		
		// update record in data store
		plugin.dataStore.updateGraveyard(graveyard);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_DISCOVERYMESSAGE,graveyard.getDisplayName());
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	private boolean setRespawnMessage(final CommandSender sender, final Graveyard graveyard, String respawnMessage) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.respawnmessage")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_RESPAWNMESSAGE,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// if message is 'default', set message to empty string
		if (respawnMessage.equalsIgnoreCase("default")) {
			respawnMessage = "";
		}
		
		// set new message
		graveyard.setRespawnMessage(respawnMessage);
		
		// update record in data store
		plugin.dataStore.updateGraveyard(graveyard);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_RESPAWNMESSAGE,graveyard.getDisplayName());
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	private boolean setGroup(final CommandSender sender, final Graveyard graveyard, String group) {
		
		// check sender permission
		if (!sender.hasPermission("graveyard.set.group")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_GROUP,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// set new group
		graveyard.setGroupName(group);
		
		// update record in data store
		plugin.dataStore.updateGraveyard(graveyard);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_GROUP,group);
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	private boolean setSafetyTime(final CommandSender sender, final Graveyard graveyard, String value) {

		// check sender permission
		if (!sender.hasPermission("graveyard.set.safetytime")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SET_SAFETYTIME,graveyard.getDisplayName());
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		//noinspection UnusedAssignment
		int safetyTime = 0;

		if (value.equalsIgnoreCase("default")) {
			safetyTime = -1;
		}
		else {
			// try to parse entered safety time as integer
			try {
				safetyTime = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_SET_INVALID_INTEGER);
				plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// set new range
		graveyard.setSafetyTime(safetyTime);
		plugin.dataStore.updateGraveyard(graveyard);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_SET_SAFETYTIME,
				graveyard.getDisplayName(),String.valueOf(safetyTime));
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	private boolean createCommand(final CommandSender sender, final String args[]) {
		
		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}
		
		// check for permission
		if (!sender.hasPermission("graveyard.create")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_CREATE);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.get(0);
		
		// remove subcommand from ArrayList
		arguments.remove(0);

		int minArgs = 2;
		int maxArgs = 2;

		// check min arguments
		if (args.length < minArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}
		// check max arguments
		if (args.length > maxArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CREATE_ARGS_COUNT_OVER);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		Player player = (Player) sender;
		Location location = player.getLocation();
		
		// set displayName to passed arguments
		String displayName = join(arguments);
		
		// check if death spawn exists and if so if player has overwrite permission
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// check if death spawn already exists
		if (graveyard != null) {
				plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CREATE_EXISTS,displayName);
				plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
				return true;
		}
		
		// create death spawn object
		graveyard = new Graveyard();
		graveyard.setSearchKey(Graveyard.deriveKey(displayName));
		graveyard.setDisplayName(displayName);
		graveyard.setLocation(location);
		graveyard.setEnabled(plugin.getConfig().getBoolean("default-enabled"));
		graveyard.setHidden(plugin.getConfig().getBoolean("default-hidden"));

		// store death spawn object
		plugin.dataStore.insertGraveyard(graveyard);

		// send success message to player
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_CREATE,displayName);
		
		// play sound effect
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_SET);
		return true;
	}
	
	
	/**
	 * Remove named destination
	 * @param sender the command sender
	 * @param args the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean deleteCommand(final CommandSender sender, final String args[]) {

		// check for permission
		if (!sender.hasPermission("graveyard.delete")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_DELETE);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.get(0);
		
		// remove subcommand from ArrayList
		arguments.remove(0);

		int minArgs = 2;

		// check min arguments
		if (args.length < minArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			displayUsage(sender, subcommand);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get death spawn name to delete from arguments
		String displayName = arguments.get(0);
		
		// remove death spawn record from storage
		Graveyard result = plugin.dataStore.deleteGraveyard(displayName);
		
		// if result is null, send not found error message
		if (result == null) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD,displayName);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}
		
		// send success message to player
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_DELETE,displayName);
		
		// play sound effect
		plugin.messageManager.sendPlayerSound(sender,SoundId.COMMAND_SUCCESS_DELETE);
		return true;
	}

	
	/**
	 * Display a single deathspawn's settings
	 * @param sender the command sender
	 * @param args the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean showCommand(final CommandSender sender, final String args[]) {
		
		// if command sender does not have permission to show death spawns, output error message and return true
		if (!sender.hasPermission("graveyard.show")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_SHOW);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.get(0);
		
		// remove subcommand from ArrayList
		arguments.remove(0);

		// argument limits
		int minArgs = 2;
		
		// if too few arguments, display error and usage messages and return
		if (args.length < minArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}
		
		// get display name from remaining arguments joined with spaces
		String displayName = join(arguments);
		
		// get deathspawn key from display name
		String key = Graveyard.deriveKey(displayName);
		
		// retrieve deathspawn from data store
		Graveyard graveyard = plugin.dataStore.selectGraveyard(key);
		
		// if retrieved deathspawn is null, display error and usage messages and return
		if (graveyard == null) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD, displayName);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// display deathspawn display name
		sender.sendMessage(ChatColor.DARK_AQUA + "Name: "
				+ ChatColor.RESET + graveyard.getDisplayName());
		
		// display deathspawn 'enabled' setting
		sender.sendMessage(ChatColor.DARK_AQUA + "Enabled: " 
				+ ChatColor.RESET + graveyard.isEnabled());
		
		// display deathspawn 'hidden' setting
		sender.sendMessage(ChatColor.DARK_AQUA + "Hidden: " 
				+ ChatColor.RESET + graveyard.isHidden());
		
		// get configured default discovery range
		int discoveryRange = plugin.getConfig().getInt("discovery-range");

		// if deathspawn discovery range is set, display it instead of default
		if (graveyard.getDiscoveryRange() >= 0) {
			discoveryRange = graveyard.getDiscoveryRange();
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "Discovery Range: " 
				+ ChatColor.RESET + discoveryRange);
		
		// get custom discovery message and display if not null or empty
		if (graveyard.getDiscoveryMessage() != null && !graveyard.getDiscoveryMessage().isEmpty()) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Custom Discovery MessageId: "
					+ ChatColor.RESET + graveyard.getDiscoveryMessage());
		}
		
		// get custom respawn message and display if not null or empty
		if (graveyard.getRespawnMessage() != null && !graveyard.getRespawnMessage().isEmpty()) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Custom Respawn MessageId: "
					+ ChatColor.RESET + graveyard.getRespawnMessage());
		}
		
		// get configured default safety time
		int safetyTime = plugin.getConfig().getInt("safety-time");

		// if deathspawn safety time is set, display it instead of default
		if (graveyard.getSafetyTime() >= 0) {
			safetyTime = graveyard.getSafetyTime();
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
				+ ChatColor.RESET + safetyTime);

		// get death spawn group; if null or empty, set to ALL
		String group = graveyard.getGroup();
		if (group == null || group.isEmpty()) {
			group = "ALL";
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "Group: " 
				+ ChatColor.RESET + group);
		
		// display deathspawn location
		Location location = graveyard.getLocation();
		String locationString = ChatColor.DARK_AQUA + "Location: "
				+ ChatColor.RESET+ "[" 
				+ ChatColor.AQUA + location.getWorld().getName() 
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
	 * @param sender the command sender
	 * @param args the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean listCommand(final CommandSender sender, final String args[]) {
		
		// if command sender does not have permission to list death spawns, output error message and return true
		if (!sender.hasPermission("graveyard.list")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_LIST);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String subcommand = args[0];

		// argument limits
		int maxArgs = 2;

		if (args.length > maxArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}
		
		int page = 1;
		
		if (args.length == 2) {
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
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
		
		// get undiscovered ids for player
		List<Integer> undiscoveredIds = new ArrayList<>();
		if (sender instanceof Player) {
			undiscoveredIds.addAll(plugin.dataStore.getUndiscoveredKeys((Player) sender));
		}
		
		// create empty list of records
		List<Graveyard> displayRecords = new ArrayList<>();
		
		for (Graveyard graveyard : allRecords) {
			
			// if graveyard is not enabled and sender does not have override permission, do not add to display list
			if (!graveyard.isEnabled() && !sender.hasPermission("graveyard.list.disabled")) {
				if (plugin.debug) {
					plugin.getLogger().info(graveyard.getDisplayName() + " is disabled and player does not have graveyard.list.disabled permission.");
				}
				continue;
			}
			
			// if graveyard is undiscovered and sender does not have override permission, do not add to display list
			if (graveyard.isHidden()
					&& undiscoveredIds.contains(graveyard.getKey())
					&& !sender.hasPermission("graveyard.list.hidden")) {
				if (plugin.debug) {
					plugin.getLogger().info(graveyard.getDisplayName() + " is undiscovered and player does not have graveyard.list.hidden permission.");
				}
				continue;
			}
			
			// if graveyard has group set and sender does not have group permission, do not add to display list
			String group = graveyard.getGroup();
			if (group != null && !group.isEmpty() && !sender.hasPermission("group." + graveyard.getGroup())) {
				if (plugin.debug) {
					plugin.getLogger().info(graveyard.getDisplayName() + " is in group that player does not have permission.");
				}
				continue;
			}
			
			// add graveyard to display list
			displayRecords.add(graveyard);
			
		}
		
		// if display list is empty, output list empty message and return
		if (displayRecords.isEmpty()) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.LIST_EMPTY);
			return true;
		}
		
		// get page count 
		int pageCount = ((displayRecords.size() - 1) / itemsPerPage) + 1;
		if (page > pageCount) {
			page = pageCount;
		}
		int startIndex = ((page - 1) * itemsPerPage);
		int endIndex = Math.min((page*itemsPerPage),displayRecords.size());
		
		List<Graveyard> displayRange = displayRecords.subList(startIndex, endIndex);

		// display list header
		plugin.messageManager.sendPlayerMessage(sender, MessageId.LIST_HEADER,page,pageCount);
		
		for (Graveyard graveyard : displayRange) {
			
			// display disabled list item
			if (!graveyard.isEnabled()) {
				plugin.messageManager.sendPlayerMessage(sender, MessageId.LIST_ITEM_DISABLED,graveyard.getDisplayName());
				continue;
			}
			
			// display undiscovered list item
			if (graveyard.isHidden() && undiscoveredIds.contains(graveyard.getKey())) {
				plugin.messageManager.sendPlayerMessage(sender, MessageId.LIST_ITEM_UNDISCOVERED,graveyard.getDisplayName());
				continue;
			}
			
			// display normal list item
			plugin.messageManager.sendPlayerMessage(sender, MessageId.LIST_ITEM,graveyard.getDisplayName());
		}
		
		// display list footer
		plugin.messageManager.sendPlayerMessage(sender, MessageId.LIST_FOOTER,page,pageCount);
		return true;
	}
	
	
	/**
	 * Display closest graveyard that is known to player and otherwise allowed
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean closestCommand(final CommandSender sender) {
	
		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.closest")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_CLOSEST);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}

		Player player = (Player) sender;

		Graveyard closest = plugin.dataStore.selectNearestGraveyard(player);
		
		if (closest == null) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CLOSEST_NO_MATCH);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_CLOSEST,closest.getDisplayName());
		return true;
	}
	
	
	/**
	 * Teleport player to death spawn location
	 * @param sender the command sender
	 * @param args the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean teleportCommand(final CommandSender sender, final String args[]) {
		
		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_CONSOLE);
			return true;
		}
		
		// check for permission
		if (!sender.hasPermission("graveyard.teleport")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_TELEPORT);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// convert args list to ArrayList so we can remove elements as we parse them
		List<String> arguments = new ArrayList<>(Arrays.asList(args));

		// get subcommand from arguments ArrayList
		String subcommand = arguments.get(0);
		
		// remove subcommand from ArrayList
		arguments.remove(0);

		// argument limits
		int minArgs = 2;
		
		if (args.length < minArgs) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}
		
		Player player = (Player) sender;
		
		String displayName = join(arguments);
		String key = Graveyard.deriveKey(displayName);
		
		Graveyard graveyard = plugin.dataStore.selectGraveyard(key);
		
		if (graveyard == null) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_FAIL_NO_RECORD, displayName);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// teleport player to death spawn location
		Location destination = graveyard.getLocation();
		player.teleport(destination, TeleportCause.COMMAND);
		plugin.messageManager.sendPlayerMessage(sender, MessageId.COMMAND_SUCCESS_TELEPORT, displayName);
		return true;
	}
	
	/**
	 * Display help message for commands
	 * @param sender the command sender
	 * @param args the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean helpCommand(final CommandSender sender, final String args[]) {

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.help")) {
			plugin.messageManager.sendPlayerMessage(sender, MessageId.PERMISSION_DENIED_HELP);
			plugin.messageManager.sendPlayerSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String command = "help";
		
		if (args.length > 1) {
			command = args[1]; 
		}
		
		String helpMessage = "That is not a valid command.";
		
		if (command.equalsIgnoreCase("create")) {
			helpMessage = "Creates a graveyard at current player location.";
		}
		if (command.equalsIgnoreCase("delete")) {
			helpMessage = "Removes a graveyard location.";
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
		sender.sendMessage(helpColor + helpMessage);
		displayUsage(sender,command);
		return true;
	}


	/**
	 * Display command usage
	 * @param sender the command sender
	 * @param command the command for which to display usage
	 */
	private void displayUsage(final CommandSender sender, String command) {
	
		if (command.isEmpty() || command.equalsIgnoreCase("help")) {
			command = "all";
		}
		if ((command.equalsIgnoreCase("status")	
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.status")) {
			sender.sendMessage(usageColor + "/graveyard status");
		}
		if ((command.equalsIgnoreCase("reload") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.reload")) {
			sender.sendMessage(usageColor + "/graveyard reload");
		}
		if ((command.equalsIgnoreCase("create") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.create")) {
			sender.sendMessage(usageColor + "/graveyard create <name>");
		}
		if ((command.equalsIgnoreCase("delete") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.delete")) {
			sender.sendMessage(usageColor + "/graveyard delete <name>");
		}
		if ((command.equalsIgnoreCase("help") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.help")) {
			sender.sendMessage(usageColor + "/graveyard help [command]");
		}
		if ((command.equalsIgnoreCase("list") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.list")) {
			sender.sendMessage(usageColor + "/graveyard list [page]");
		}
		if ((command.equalsIgnoreCase("set") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.set")) {
			sender.sendMessage(usageColor + "/graveyard set <name> <attribute> <value>");
		}
		if ((command.equalsIgnoreCase("show") 
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.show")) {
			sender.sendMessage(usageColor + "/graveyard show <name>");
		}
		if ((command.equalsIgnoreCase("teleport") 
				|| command.equalsIgnoreCase("tp")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.teleport")) {
			sender.sendMessage(usageColor + "/graveyard teleport <name>");
		}
	}


	/**
	 * Join list of strings into one string with spaces
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
