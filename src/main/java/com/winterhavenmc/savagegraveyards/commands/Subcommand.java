package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;


public interface Subcommand {

	boolean onCommand(CommandSender sender, List<String> argsList);

	List<String> onTabComplete(final CommandSender sender, final Command command,
							   final String alias, final String[] args);

	String getName();

	void setName(final String name);

	List<String> getAliases();

	@SuppressWarnings("unused")
	void setAliases(List<String> aliases);

	void addAlias(String alias);

	@SuppressWarnings("unused")
	String getUsage();

	void setUsage(String usageString);

	void displayUsage(CommandSender sender);

	MessageId getDescription();

	void setDescription(MessageId messageId);

}
