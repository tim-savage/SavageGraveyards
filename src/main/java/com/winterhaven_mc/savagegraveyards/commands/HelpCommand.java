package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


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

		if (args.size() == 0) {
			displayUsage(sender,"all");
			return true;
		}

		// set default command
		String commandName = args.get(0);

		switch (commandName.toLowerCase()) {
			case "create":
				Message.create(sender, COMMAND_HELP_CREATE).send();
				break;

			case "closest":
			case "nearest":
				Message.create(sender, COMMAND_HELP_CLOSEST).send();
				break;

			case "delete":
				Message.create(sender, COMMAND_HELP_DELETE).send();
				break;

			case "forget":
				Message.create(sender, COMMAND_HELP_FORGET).send();
				break;

			case "help":
				Message.create(sender, COMMAND_HELP_HELP).send();
				break;

			case "list":
				Message.create(sender, COMMAND_HELP_LIST).send();
				break;

			case "reload":
				Message.create(sender, COMMAND_HELP_RELOAD).send();
				break;

			case "set":
				Message.create(sender, COMMAND_HELP_SET).send();
				break;

			case "show":
				Message.create(sender, COMMAND_HELP_SHOW).send();
				break;

			case "status":
				Message.create(sender, COMMAND_HELP_STATUS).send();
				break;

			case "teleport":
			case "tp":
				Message.create(sender, COMMAND_HELP_TELEPORT).send();
				break;

			default:
				Message.create(sender, COMMAND_HELP_INVALID).send();
				commandName = "all";
		}

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

		String command = passedCommand.trim();

		if (command.isEmpty()) {
			command = "all";
		}
		if ((command.equalsIgnoreCase("status")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.status")) {
			sender.sendMessage(USAGE_COLOR + StatusCommand.usageString);
		}

		if ((command.equalsIgnoreCase("reload")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.reload")) {
			sender.sendMessage(USAGE_COLOR + ReloadCommand.usageString);
		}

		if ((command.equalsIgnoreCase("create")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.create")) {
			sender.sendMessage(USAGE_COLOR + CreateCommand.usageString);
		}

		if ((command.equalsIgnoreCase("closest")
				|| command.equalsIgnoreCase("nearest")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.closest")) {
			sender.sendMessage(USAGE_COLOR + ClosestCommand.usageString);
		}

		if ((command.equalsIgnoreCase("delete")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.delete")) {
			sender.sendMessage(USAGE_COLOR + DeleteCommand.usageString);
		}

		if ((command.equalsIgnoreCase("forget")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.forget")) {
			sender.sendMessage(USAGE_COLOR + ForgetCommand.usageString);
		}

		if ((command.equalsIgnoreCase("help")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.help")) {
			sender.sendMessage(USAGE_COLOR + HelpCommand.usageString);
		}

		if ((command.equalsIgnoreCase("list")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.list")) {
			sender.sendMessage(USAGE_COLOR + ListCommand.usageString);
		}

		if ((command.equalsIgnoreCase("set")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.set")) {
			sender.sendMessage(USAGE_COLOR + SetCommand.usageString);
		}

		if ((command.equalsIgnoreCase("show")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.show")) {
			sender.sendMessage(USAGE_COLOR + ShowCommand.usageString);
		}

		if ((command.equalsIgnoreCase("teleport")
				|| command.equalsIgnoreCase("tp")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("graveyard.teleport")) {
			sender.sendMessage(USAGE_COLOR + TeleportCommand.usageString);
		}
	}

}
