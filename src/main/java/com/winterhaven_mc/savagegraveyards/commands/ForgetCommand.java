package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.GRAVEYARD;
import static com.winterhaven_mc.savagegraveyards.messages.Macro.TARGET_PLAYER;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Forget command implementation<br>
 * Removes graveyard discovery record for player
 */
public class ForgetCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ForgetCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		setUsage("/graveyard forget <player> <graveyard name>");
		setDescription(COMMAND_HELP_FORGET);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// check for permission
		if (!sender.hasPermission("graveyard.forget")) {
			Message.create(sender, PERMISSION_DENIED_FORGET).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int minArgs = 2;

		// check for minimum arguments
		if (args.size() < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get player name
		String playerName = args.remove(0);

		// get list of offline players
		OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();

		OfflinePlayer player = null;

		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (playerName.equals(offlinePlayer.getName())) {
				player = offlinePlayer;
			}
		}

		// if player not found, send message and return
		if (player == null) {
			Message.create(sender, COMMAND_FAIL_FORGET_INVALID_PLAYER).send();
			return true;
		}

		// get graveyard search key
//		String searchKey = args.remove(0);
		String searchKey = String.join("_", args);

		// get graveyard (for messages)
		Graveyard graveyard = plugin.dataStore.selectGraveyard(searchKey);

		// if no matching graveyard found, send message and return
		if (graveyard == null) {

			// create dummy graveyard for message
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(searchKey).build();

			// send graveyard not found message
			Message.create(sender, COMMAND_FAIL_FORGET_INVALID_GRAVEYARD)
					.setMacro(GRAVEYARD, dummyGraveyard)
					.send();

			// play command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

//		// get player name
//		String playerName = args.remove(0);
//
//		// get list of offline players
//		OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();
//
//		OfflinePlayer player = null;
//
//		for (OfflinePlayer offlinePlayer : offlinePlayers) {
//			if (playerName.equals(offlinePlayer.getName())) {
//				player = offlinePlayer;
//			}
//		}
//
//		// if player not found, send message and return
//		if (player == null) {
//			Message.create(sender, COMMAND_FAIL_FORGET_INVALID_PLAYER).send();
//			return true;
//		}

		// delete discovery record
		if (plugin.dataStore.deleteDiscovery(searchKey, player.getUniqueId())) {

			// send success message
			Message.create(sender, COMMAND_SUCCESS_FORGET)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(TARGET_PLAYER, player)
					.send();

			// play success sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_FORGET);
		}
		else {
			// send failure message
			Message.create(sender, COMMAND_FAIL_FORGET)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(TARGET_PLAYER, player)
					.send();

			// send command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		}

		return true;
	}
}
