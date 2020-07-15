package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;
import static com.winterhaven_mc.savagegraveyards.sounds.SoundId.COMMAND_INVALID;


/**
 * Help command implementation<br>
 * displays help and usage messages for plugin commands
 */
public class HelpCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;
	private final SubcommandMap subcommandMap;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	HelpCommand(final PluginMain plugin, final SubcommandMap subcommandMap) {
		this.plugin = Objects.requireNonNull(plugin);
		this.subcommandMap = Objects.requireNonNull(subcommandMap);
		setUsage("/graveyard help [command]");
		setDescription(COMMAND_HELP_HELP);
	}


	@Override
	public boolean onCommand(CommandSender sender, List<String> args) {

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.help")) {
			Message.create(sender, PERMISSION_DENIED_HELP).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// if no arguments, display usage for all commands
		if (args.size() == 0) {
			sender.sendMessage(ChatColor.YELLOW + "Command usage:");
			displayUsageAll(sender);
			return true;
		}

		// get subcommand name
		String subcommandName = args.get(0);
		displayHelp(sender, subcommandName);
		return true;
	}


	/**
	 * Display help message and usage for a command
	 * @param sender the command sender
	 * @param commandName the name of the command for which to show help and usage
	 */
	void displayHelp(final CommandSender sender, final String commandName) {

		// get subcommand from map by name
		Subcommand subcommand = subcommandMap.getCommand(commandName);

		// if subcommand found in map, display help message and usage
		if (subcommand != null) {
			Message.create(sender, subcommand.getDescription()).send();
			subcommand.displayUsage(sender);
		}

		// else display invalid command help message and usage for all commands
		else {
			Message.create(sender, COMMAND_HELP_INVALID).send();
			plugin.soundConfig.playSound(sender, COMMAND_INVALID);
			displayUsageAll(sender);
		}
	}


	/**
	 * Display usage message for all commands
	 * @param sender the command sender
	 */
	void displayUsageAll(CommandSender sender) {

		for (String subcommandName : subcommandMap.getKeys()) {
			if (subcommandMap.getCommand(subcommandName) != null) {
				subcommandMap.getCommand(subcommandName).displayUsage(sender);
			}
		}
	}

}
