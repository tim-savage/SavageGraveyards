package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.messages.MessageId;
import org.bukkit.command.CommandSender;

import java.util.List;


public interface Subcommand {

	boolean onCommand(CommandSender sender, List<String> argsList);

	String getName();

	void setName(final String name);

	List<String> getAliases();

	void setAliases(List<String> aliases);

	void addAlias(String alias);

	String getUsage();

	void setUsage(String usageString);

	void displayUsage(CommandSender sender);

	MessageId getDescription();

	void setDescription(MessageId messageId);

}
