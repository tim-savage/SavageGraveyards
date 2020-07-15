package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;
import static com.winterhaven_mc.savagegraveyards.sounds.SoundId.*;


/**
 * Implements command executor for SavageGraveyards commands.
 */
public class CommandManager implements CommandExecutor, TabCompleter {

	// reference to main class
	private final PluginMain plugin;

	// map of subcommands
	private final SubcommandMap subcommandMap = new SubcommandMap();

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

		// register subcommands in subcommand map
		subcommandMap.register("closest", new ClosestCommand(plugin));
		subcommandMap.register("create", new CreateCommand(plugin));
		subcommandMap.register("delete", new DeleteCommand(plugin));
		subcommandMap.register("forget", new ForgetCommand(plugin));
		subcommandMap.register("help", new HelpCommand(plugin, subcommandMap));
		subcommandMap.register("list", new ListCommand(plugin));
		subcommandMap.register("reload", new ReloadCommand(plugin));
		subcommandMap.register("set", new SetCommand(plugin));
		subcommandMap.register("show", new ShowCommand(plugin));
		subcommandMap.register("status", new StatusCommand(plugin));
		subcommandMap.register("teleport", new TeleportCommand(plugin));
	}


	/**
	 * Tab completer for SavageGraveyards commands
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		List<String> returnList = new ArrayList<>();

		// return list of subcommands for which sender has permission
		if (args.length == 1) {
			returnList = matchCommandsForPlayer(sender, args[0]);
		}

		else if (args.length == 2) {
			// return list of valid matching graveyard names
			if (args[0].equalsIgnoreCase("teleport")
					|| args[0].equalsIgnoreCase("tp")
					|| args[0].equalsIgnoreCase("set")
					|| args[0].equalsIgnoreCase("show")
					|| args[0].equalsIgnoreCase("delete")) {
				returnList = plugin.dataStore.selectMatchingGraveyardNames(args[1]);
			}

			// if forget command, return list of players that have discovered graveyards
			else if (args[0].equalsIgnoreCase("forget")) {

				// get collection of players with discoveries
				Collection<String> playerNames = plugin.dataStore.selectPlayersWithDiscoveries();

				// add matching player names to return list
				for (String playerName : playerNames) {
					if (playerName != null && playerName.toLowerCase().startsWith(args[1].toLowerCase())) {
						returnList.add(playerName);
					}
				}
			}

			// if help command, return list of subcommands for which sender has permission,
			// except help command itself
			else if (args[0].equalsIgnoreCase("help")) {
				for (String subcommand : SUBCOMMANDS) {
					if (sender.hasPermission("graveyard." + subcommand)
							&& subcommand.startsWith(args[1].toLowerCase())
							&& !subcommand.equalsIgnoreCase("help")) {
						returnList.add(subcommand);
					}
				}
			}
		}

		else if (args.length == 3) {

			// if set command, return list of attributes that player has permission to change
			if (args[0].equalsIgnoreCase("set")) {
				for (String attribute : ATTRIBUTES) {
					if (sender.hasPermission("graveyard.set." + attribute)
							&& attribute.startsWith(args[2])) {
						returnList.add(attribute);
					}
				}
			}

			// if forget command, return list of discovered graveyards for player
			else if (args[0].equalsIgnoreCase("forget")) {

				// get uid for player name in args[1]
				String playerName = args[1];

				// get all offline players
				List<OfflinePlayer> offlinePlayers = new ArrayList<>(Arrays.asList(
						plugin.getServer().getOfflinePlayers()));

				UUID playerUid = null;

				// iterate over offline players trying to match name
				for (OfflinePlayer offlinePlayer : offlinePlayers) {
					if (playerName.equalsIgnoreCase(offlinePlayer.getName())) {
						playerUid = offlinePlayer.getUniqueId();
						break;
					}
				}

				// if playerUid is null, return empty list
				if (playerUid == null) {
					return Collections.emptyList();
				}

				// get graveyard keys discovered by player
				Collection<String> graveyardKeys =
						plugin.dataStore.selectDiscoveredKeys(playerUid);

				// iterate over graveyards
				for (String graveyardKey : graveyardKeys) {
					if (graveyardKey.startsWith(args[2])) {
						returnList.add(graveyardKey);
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

		// convert args array to list
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		String subcommandName;

		// get subcommand, remove from front of list
		if (argsList.size() > 0) {
			subcommandName = argsList.remove(0);
		}

		// if no arguments, set command to help
		else {
			subcommandName = "help";
		}

		// get subcommand from map by name
		Subcommand subcommand = subcommandMap.getCommand(subcommandName);

		// if subcommand is null, get help command from map
		if (subcommand == null) {
			subcommand = subcommandMap.getCommand("help");
			Message.create(sender, COMMAND_FAIL_INVALID_COMMAND).send();
			plugin.soundConfig.playSound(sender, COMMAND_INVALID);
		}

		// execute subcommand
		 return subcommand.onCommand(sender, argsList);
	}


	private List<String> matchCommandsForPlayer(CommandSender sender, String matchString) {

		List<String> returnList = new ArrayList<>();

		for (String subcommand : SUBCOMMANDS) {
			if (sender.hasPermission("graveyard." + subcommand)
					&& subcommand.startsWith(matchString.toLowerCase())) {
				returnList.add(subcommand);
			}
		}
		return returnList;
	}

}
