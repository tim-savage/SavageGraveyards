package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.GRAVEYARD;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Delete command implementation<br>
 * Removes graveyard record from datastore
 */
public class DeleteCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	DeleteCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("delete");
		this.setDescription(COMMAND_HELP_DELETE);
		this.setUsage("/graveyard delete <graveyard name>");
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		if (args.length == 2) {
			// return list of valid matching graveyard names
			return plugin.dataStore.selectMatchingGraveyardNames(args[1]);
		}

		return Collections.emptyList();
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// check for permission
		if (!sender.hasPermission("graveyard.delete")) {
			plugin.messageBuilder.build(sender, PERMISSION_DENIED_DELETE).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		int minArgs = 1;

		// check min arguments
		if (args.size() < minArgs) {
			plugin.messageBuilder.build(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send(plugin.languageHandler);
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set displayName to passed arguments
		String displayName = String.join(" ", args);

		// delete graveyard record from storage
		Graveyard graveyard = plugin.dataStore.deleteGraveyard(displayName);

		// if graveyard is null, send not found error message
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			plugin.messageBuilder.build(sender, COMMAND_FAIL_NO_RECORD).setMacro(GRAVEYARD, dummyGraveyard).send(plugin.languageHandler);

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message to player
		plugin.messageBuilder.build(sender, COMMAND_SUCCESS_DELETE)
				.setMacro(GRAVEYARD, graveyard)
				.send(plugin.languageHandler);

		// play sound effect
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_DELETE);
		return true;
	}

}
