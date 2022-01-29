package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;


abstract class SubcommandAbstract implements Subcommand {

	protected String name;
	protected Collection<String> aliases = new HashSet<>();
	protected String usageString;
	protected MessageId description;
	protected int minArgs;
	protected int maxArgs;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<String> getAliases() {
		return aliases;
	}

	@Override
	public String getUsage() {
		return usageString;
	}

	@Override
	public void displayUsage(final CommandSender sender) {
		sender.sendMessage(usageString);
	}

	@Override
	public MessageId getDescription() {
		return description;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		return Collections.emptyList();
	}

}
