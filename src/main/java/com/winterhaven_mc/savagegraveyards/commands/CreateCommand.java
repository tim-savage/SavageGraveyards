package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.*;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


public class CreateCommand implements Subcommand {

	private final PluginMain plugin;
	private final CommandSender sender;
	private final List<String> args;

	final static String usageString = "/graveyard create <graveyard name>";


	CreateCommand(final PluginMain plugin, final CommandSender sender, final List<String> args) {
		this.plugin = Objects.requireNonNull(plugin);
		this.sender = Objects.requireNonNull(sender);
		this.args = Objects.requireNonNull(args);
	}


	@Override
	public boolean execute() {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.create")) {
			Message.create(sender, PERMISSION_DENIED_CREATE).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		int minArgs = 1;

		// check min arguments
		if (args.size() < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			HelpCommand.displayUsage(sender, "create");
			return false;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get player location
		Location location = player.getLocation();

		// set displayName to passed arguments
		String displayName = String.join(" ", args);

		// attempt to retrieve existing graveyard from datastore
		Graveyard existingGraveyard = plugin.dataStore.selectGraveyard(displayName);

		// if graveyard does not exist, insert new graveyard in data store and return
		if (existingGraveyard == null) {

			// create new graveyard object with passed display name and player location
			Graveyard newGraveyard = new Graveyard.Builder()
					.displayName(displayName)
					.location(location)
					.build();

			// insert graveyard in data store
			Collection<Graveyard> insertSet = new HashSet<>(1);
			insertSet.add(newGraveyard);
			plugin.dataStore.insertGraveyards(insertSet);

			// send success message
			Message.create(sender, COMMAND_SUCCESS_CREATE)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(LOCATION, location)
					.send();

			// play sound effect
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
			return true;
		}

		// if player has overwrite permission, update record with new graveyard and return
		if (player.hasPermission("graveyard.overwrite")) {

			// create new graveyard object with passed display name and player location and existing primary key
			Graveyard newGraveyard = new Graveyard.Builder()
					.primaryKey(existingGraveyard.getPrimaryKey())
					.displayName(displayName)
					.location(location)
					.build();

			// update graveyard in data store
			plugin.dataStore.updateGraveyard(newGraveyard);

			// send success message
			Message.create(sender, COMMAND_SUCCESS_CREATE)
					.setMacro(GRAVEYARD, newGraveyard)
					.setMacro(LOCATION, newGraveyard.getLocation())
					.send();

			// play sound effect
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
			return true;
		}

		// send graveyard exists error message
		Message.create(sender, COMMAND_FAIL_CREATE_EXISTS)
				.setMacro(GRAVEYARD, existingGraveyard)
				.send();

		// play sound effect
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);

		return true;
	}

}
