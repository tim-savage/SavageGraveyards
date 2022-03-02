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

package com.winterhavenmc.savagegraveyards.listeners;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.MessageId;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implements Listener for player events
 */
public final class PlayerEventListener implements Listener {

	// reference to main class
	private final PluginMain plugin;

	// player death respawn hash set, prevents setting respawn location to graveyards on non-death respawn events
	private final Set<UUID> deathTriggeredRespawn = ConcurrentHashMap.newKeySet();

	// unmodifiable set of entity target cancel reasons
	private final static Set<TargetReason> cancelReasons = Set.of(
			TargetReason.CLOSEST_PLAYER,
			TargetReason.RANDOM_TARGET );


	/**
	 * constructor method for {@code PlayerEventListener} class
	 *
	 * @param plugin A reference to this plugin's main class
	 */
	public PlayerEventListener(final PluginMain plugin) {

		// reference to main
		this.plugin = plugin;

		// register events in this class
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	/**
	 * Player death event handler
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event) {

		// put player uuid in deathTriggeredRespawn set
		deathTriggeredRespawn.add(event.getEntity().getUniqueId());
	}


	/**
	 * Player respawn event handler for LOWEST priority
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	void onPlayerRespawnLOWEST(final PlayerRespawnEvent event) {
		if ("LOWEST".equalsIgnoreCase(plugin.getConfig().getString("respawn-priority"))) {
			onPlayerRespawnHandler(event);
		}
	}


	/**
	 * Player respawn event handler for LOW priority
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler(priority = EventPriority.LOW)
	void onPlayerRespawnLOW(final PlayerRespawnEvent event) {
		if ("LOW".equalsIgnoreCase(plugin.getConfig().getString("respawn-priority"))) {
			onPlayerRespawnHandler(event);
		}
	}


	/**
	 * Player respawn event handler for NORMAL priority
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	void onPlayerRespawnNORMAL(final PlayerRespawnEvent event) {
		if ("NORMAL".equalsIgnoreCase(plugin.getConfig().getString("respawn-priority"))) {
			onPlayerRespawnHandler(event);
		}
	}


	/**
	 * Player respawn event handler for HIGH priority
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler(priority = EventPriority.HIGH)
	void onPlayerRespawnHIGH(final PlayerRespawnEvent event) {
		if ("HIGH".equalsIgnoreCase(plugin.getConfig().getString("respawn-priority"))) {
			onPlayerRespawnHandler(event);
		}
	}


	/**
	 * Player respawn event handler for HIGHEST priority
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	void onPlayerRespawnHIGHEST(final PlayerRespawnEvent event) {
		if ("HIGHEST".equalsIgnoreCase(plugin.getConfig().getString("respawn-priority"))) {
			onPlayerRespawnHandler(event);
		}
	}


	/**
	 * Player respawn handler, called by the registered event listener with configured priority
	 *
	 * @param event the player respawn event handled by this method
	 */
	private void onPlayerRespawnHandler(final PlayerRespawnEvent event) {

		// get event player
		Player player = event.getPlayer();

		// if deathTriggeredRespawn set does not contain user uuid, do nothing and return
		if (!deathTriggeredRespawn.contains(player.getUniqueId())) {
			return;
		}

		// remove player uuid from deathTriggeredRespawn set
		deathTriggeredRespawn.remove(player.getUniqueId());

		// check that player world is enabled
		if (!plugin.worldManager.isEnabled(player.getWorld())) {
			return;
		}

		// check that player has graveyard.respawn permission
		if (!player.hasPermission("graveyard.respawn")) {
			return;
		}

		// get nearest valid graveyard for player
		Graveyard graveyard = plugin.dataStore.selectNearestGraveyard(player);

		// if graveyard and graveyard location are not null, set respawn location
		if (graveyard != null && graveyard.getLocation() != null) {
			event.setRespawnLocation(graveyard.getLocation());

			// send player message
			plugin.messageBuilder.compose(player, MessageId.DEFAULT_RESPAWN)
					.setAltMessage(graveyard.getRespawnMessage())
					.setMacro(Macro.GRAVEYARD, graveyard)
					.setMacro(Macro.LOCATION, graveyard.getLocation())
					.send();

			// put player in safety cooldown map
			plugin.safetyManager.putPlayer(player, graveyard);
		}
	}


	/**
	 * Cancel mob targeting of a player for configured time period following death respawn
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onEntityTargetLivingEntity(final EntityTargetLivingEntityEvent event) {

		// check that target is a player
		if (event.getTarget() != null && event.getTarget() instanceof Player) {

			// get targeted player
			Player player = (Player) event.getTarget();

			// if player is in safety cooldown, cancel event
			if (plugin.safetyManager.isPlayerProtected(player)) {

				// get target reason
				EntityTargetEvent.TargetReason reason = event.getReason();

				// if reason is in cancelReasons list, cancel event
				if (cancelReasons.contains(reason)) {
					event.setCancelled(true);
				}
			}
		}
	}

}
