package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;


/**
 * Create command implementation<br>
 * Creates new graveyard at player location with given name
 */
final class CreateCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	CreateCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("create");
		this.setUsage("/graveyard create <graveyard name>");
		this.setDescription(MessageId.COMMAND_HELP_CREATE);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.create")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_CREATE).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		int minArgs = 1;

		// check min arguments
		if (args.size() < minArgs) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
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
			plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_CREATE)
					.setMacro(Macro.GRAVEYARD, newGraveyard)
					.setMacro(Macro.LOCATION, location)
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
			plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_CREATE)
					.setMacro(Macro.GRAVEYARD, newGraveyard)
					.setMacro(Macro.LOCATION, newGraveyard.getLocation())
					.send();

			// play sound effect
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_SET);
			return true;
		}

		// send graveyard exists error message
		plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_CREATE_EXISTS)
				.setMacro(Macro.GRAVEYARD, existingGraveyard)
				.send();

		// play sound effect
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);

		return true;
	}

}
