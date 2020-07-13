package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.messages.MessageId;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Help command implementation<br>
 * displays help and usage messages for plugin commands
 */
public class HelpCommand implements Subcommand {

	private final PluginMain plugin;
	private final CommandSender sender;
	private final List<String> args;

	final static String usageString = "/graveyard help [command]";

	private final static ChatColor USAGE_COLOR = ChatColor.GOLD;


	HelpCommand(final PluginMain plugin, final CommandSender sender, final List<String> args) {
		this.plugin = Objects.requireNonNull(plugin);
		this.sender = Objects.requireNonNull(sender);
		this.args = Objects.requireNonNull(args);
	}


	@Override
	public boolean execute() {

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("graveyard.help")) {
			Message.create(sender, PERMISSION_DENIED_HELP).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// if no arguments, display usage for all commands
		if (args.size() == 0) {
			displayUsage(sender,"all");
			return true;
		}

		// set default command
		String commandName = args.get(0);
		MessageId messageId;

		// switch on command name to select MessageId
		switch (commandName.toLowerCase()) {
			case "create":
				messageId = COMMAND_HELP_CREATE;
				break;

			case "closest":
			case "nearest":
				messageId = COMMAND_HELP_CLOSEST;
				break;

			case "delete":
				messageId = COMMAND_HELP_DELETE;
				break;

			case "forget":
				messageId = COMMAND_HELP_FORGET;
				break;

			case "help":
				messageId = COMMAND_HELP_HELP;
				break;

			case "list":
				messageId = COMMAND_HELP_LIST;
				break;

			case "reload":
				messageId = COMMAND_HELP_RELOAD;
				break;

			case "set":
				messageId = COMMAND_HELP_SET;
				break;

			case "show":
				messageId = COMMAND_HELP_SHOW;
				break;

			case "status":
				messageId = COMMAND_HELP_STATUS;
				break;

			case "teleport":
			case "tp":
				messageId = COMMAND_HELP_TELEPORT;
				break;

			default:
				messageId = COMMAND_HELP_INVALID;
				commandName = "all";
				break;
		}

		// display help message and command usage
		Message.create(sender, messageId).send();
		displayUsage(sender, commandName);
		return true;
	}


	/**
	 * Display command usage
	 *
	 * @param sender        the command sender
	 * @param passedCommand the command for which to display usage
	 * @throws NullPointerException if any parameter is null
	 */
	static void displayUsage(final CommandSender sender, final String passedCommand) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(passedCommand);

		String commandName = passedCommand;

		if (commandName.isEmpty()) {
			commandName = "all";
		}

		if ((commandName.equalsIgnoreCase("status")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.status")) {
			sender.sendMessage(USAGE_COLOR + StatusCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("reload")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.reload")) {
			sender.sendMessage(USAGE_COLOR + ReloadCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("create")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.create")) {
			sender.sendMessage(USAGE_COLOR + CreateCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("closest")
				|| commandName.equalsIgnoreCase("nearest")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.closest")) {
			sender.sendMessage(USAGE_COLOR + ClosestCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("delete")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.delete")) {
			sender.sendMessage(USAGE_COLOR + DeleteCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("forget")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.forget")) {
			sender.sendMessage(USAGE_COLOR + ForgetCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("help")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.help")) {
			sender.sendMessage(USAGE_COLOR + HelpCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("list")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.list")) {
			sender.sendMessage(USAGE_COLOR + ListCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("set")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.set")) {
			sender.sendMessage(USAGE_COLOR + SetCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("show")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.show")) {
			sender.sendMessage(USAGE_COLOR + ShowCommand.usageString);
		}

		if ((commandName.equalsIgnoreCase("teleport")
				|| commandName.equalsIgnoreCase("tp")
				|| commandName.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.teleport")) {
			sender.sendMessage(USAGE_COLOR + TeleportCommand.usageString);
		}
	}

}
