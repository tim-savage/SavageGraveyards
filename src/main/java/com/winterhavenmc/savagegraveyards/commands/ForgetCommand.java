package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

import static com.winterhavenmc.savagegraveyards.messages.Macro.GRAVEYARD;
import static com.winterhavenmc.savagegraveyards.messages.Macro.TARGET_PLAYER;


/**
 * Forget command implementation<br>
 * Removes graveyard discovery record for player
 */
final class ForgetCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ForgetCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("forget");
		setUsage("/graveyard forget <player> <graveyard name>");
		setDescription(MessageId.COMMAND_HELP_FORGET);
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		List<String> returnList = new ArrayList<>();

		if (args.length == 2) {

			// get collection of players with discoveries
			Collection<String> playerNames = plugin.dataStore.selectPlayersWithDiscoveries();

			// add matching player names to return list
			for (String playerName : playerNames) {
				if (playerName != null && playerName.toLowerCase().startsWith(args[1].toLowerCase())) {
					returnList.add(playerName);
				}
			}
		}

		else if (args.length == 3) {

			// get uid for player name in args[1]
			String playerName = args[1];

			// get all offline players
			List<OfflinePlayer> offlinePlayers = new ArrayList<>(Arrays.asList(
					plugin.getServer().getOfflinePlayers()));

			UUID playerUid = null;

			// iterate over offline players trying to match name
			for (OfflinePlayer offlinePlayer : offlinePlayers) {
				if (playerName.equalsIgnoreCase(offlinePlayer.getName())) {
					playerUid = offlinePlayer.getUniqueId();
					break;
				}
			}

			// if playerUid is null, return empty list
			if (playerUid == null) {
				return Collections.emptyList();
			}

			// get graveyard keys discovered by player
			Collection<String> graveyardKeys =
					plugin.dataStore.selectDiscoveredKeys(playerUid);

			// iterate over graveyards
			for (String graveyardKey : graveyardKeys) {
				if (graveyardKey.startsWith(args[2])) {
					returnList.add(graveyardKey);
				}
			}
		}

		return returnList;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// check for permission
		if (!sender.hasPermission("graveyard.forget")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_FORGET).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int minArgs = 2;

		// check for minimum arguments
		if (args.size() < minArgs) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
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
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_FORGET_INVALID_PLAYER).send();
			return true;
		}

		// get graveyard search key
		String searchKey = String.join("_", args);

		// get graveyard (for messages)
		Graveyard graveyard = plugin.dataStore.selectGraveyard(searchKey);

		// if no matching graveyard found, send message and return
		if (graveyard == null) {

			// create dummy graveyard for message
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(searchKey).build();

			// send graveyard not found message
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_FORGET_INVALID_GRAVEYARD)
					.setMacro(GRAVEYARD, dummyGraveyard)
					.send();

			// play command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// delete discovery record
		if (plugin.dataStore.deleteDiscovery(searchKey, player.getUniqueId())) {

			// send success message
			plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_FORGET)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(TARGET_PLAYER, player)
					.send();

			// play success sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_FORGET);
		}
		else {
			// send failure message
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_FORGET)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(TARGET_PLAYER, player)
					.send();

			// send command fail sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		}

		return true;
	}
}
