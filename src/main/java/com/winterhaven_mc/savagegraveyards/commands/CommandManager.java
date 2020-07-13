package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Implements command executor for {@code SavageGraveyards} commands.
 */
public class CommandManager implements CommandExecutor, TabCompleter {

	// reference to main class
	private final PluginMain plugin;

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

		// convert args array to list
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		String subcommandString;

		// get subcommand, remove from front of list
		if (args.length > 0) {
			subcommandString = argsList.remove(0);
		}

		// if no arguments, display usage for all commands
		else {
			HelpCommand.displayUsage(sender, "all");
			return true;
		}

		Subcommand subcommand;

		// handle subcommands
		switch (subcommandString.toLowerCase()) {

			case "closest":
			case "nearest":
				subcommand = new ClosestCommand(plugin, sender);
				break;

			case "status":
				subcommand = new StatusCommand(plugin, sender);
				break;

			case "reload":
				subcommand = new ReloadCommand(plugin, sender);
				break;

			case"create":
				subcommand = new CreateCommand(plugin, sender, argsList);
				break;

			case "delete":
				subcommand = new DeleteCommand(plugin, sender, argsList);
				break;

			case "list":
				subcommand = new ListCommand(plugin, sender, argsList);
				break;

			case "set":
				subcommand = new SetCommand(plugin, sender, argsList);
				break;

			case "show":
				subcommand = new ShowCommand(plugin, sender, argsList);
				break;

			case "teleport":
			case "tp":
				subcommand = new TeleportCommand(plugin, sender, argsList);
				break;

			case "forget":
				subcommand = new ForgetCommand(plugin, sender, argsList);
				break;

			case "help":
				subcommand = new HelpCommand(plugin, sender, argsList);
				break;

			default:
				Message.create(sender, COMMAND_FAIL_INVALID_COMMAND).send();
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				HelpCommand.displayUsage(sender, "all");
				return true;
		}

		// execute subcommand
		return subcommand.execute();
	}

}
