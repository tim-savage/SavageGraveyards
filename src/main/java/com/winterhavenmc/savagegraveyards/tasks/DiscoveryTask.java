/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.savagegraveyards.tasks;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.events.DiscoveryEvent;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.storage.Discovery;
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;


/**
 * Repeating task that checks if any players are
 * within discovery distance of undiscovered graveyard locations
 */
public final class DiscoveryTask extends BukkitRunnable {

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

			// if player does not have discover permission, skip to next player
			if (!player.hasPermission("graveyard.discover")) {
				continue;
			}

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

						// send player message
						plugin.messageBuilder.compose(player, MessageId.DEFAULT_DISCOVERY)
								.setAltMessage(graveyard.getDiscoveryMessage())
								.setMacro(Macro.GRAVEYARD, graveyard)
								.setMacro(Macro.LOCATION, graveyardLocation)
								.send();

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
