package com.winterhaven_mc.savagegraveyards.tasks;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.events.DiscoveryEvent;
import com.winterhaven_mc.savagegraveyards.messages.Macro;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.storage.Discovery;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.savagegraveyards.messages.MessageId;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;


/**
 * Repeating task that checks if any players are
 * within discovery distance of undiscovered graveyard locations
 */
public class DiscoveryTask extends BukkitRunnable {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public DiscoveryTask(final PluginMain plugin) {
		this.plugin = plugin;
	}


	@Override
	public void run() {

		// iterate through online players
		for (Player player : ImmutableList.copyOf(plugin.getServer().getOnlinePlayers())) {

			// get player location
			Location playerLocation = player.getLocation();

			// iterate through player's undiscovered graveyards
			for (Graveyard graveyard : plugin.dataStore.selectUndiscoveredGraveyards(player)) {

				// get graveyard location
				Location graveyardLocation = graveyard.getLocation();

				// if graveyard location is null, skip to next graveyard
				if (graveyardLocation == null) {
					continue;
				}

				// check that player has graveyard.discover permission
				if (player.hasPermission("graveyard.discover")) {

					// check if player is in graveyard group
					if (graveyard.getGroup() == null
							|| graveyard.getGroup().isEmpty()
							|| player.hasPermission("group." + graveyard.getGroup())) {

						// get graveyard discovery range, or config default if null or negative
						int discoveryRange = graveyard.getDiscoveryRange();
						if (discoveryRange < 0) {
							discoveryRange = plugin.getConfig().getInt("discovery-range");
						}

						// check if player is within discovery range of graveyard
						if (graveyardLocation.distanceSquared(playerLocation) < Math.pow(discoveryRange, 2)) {

							// create discovery record
							Discovery record = new Discovery(graveyard.getSearchKey(), player.getUniqueId());

							// set graveyard as discovered for player
							plugin.dataStore.insertDiscovery(record);

							// send discovery message to player
							if (graveyard.getDiscoveryMessage() != null
									&& !graveyard.getDiscoveryMessage().isEmpty()) {
								player.sendMessage(ChatColor
										.translateAlternateColorCodes('&', graveyard.getDiscoveryMessage()));
							}
							else {
								Message.create(player, MessageId.DEFAULT_DISCOVERY)
										.setMacro(Macro.GRAVEYARD, graveyard)
										.setMacro(Macro.LOCATION, graveyardLocation)
										.send();
							}

							// call discovery event
							DiscoveryEvent event = new DiscoveryEvent(player, graveyard);
							plugin.getServer().getPluginManager().callEvent(event);

							// play discovery sound
							plugin.soundConfig.playSound(player, SoundId.ACTION_DISCOVERY);
						}
					}
				}
			}
		}
	}

}
