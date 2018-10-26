package com.winterhaven_mc.savagegraveyards.listeners;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.savagegraveyards.messages.MessageId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;


/**
 * Implements player event listener for {@code SavageGraveyards}
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class PlayerEventListener implements Listener {

	// reference to main class
	private final PluginMain plugin;
	
	// set entity target cancel reasons
	private final static Set<TargetReason> cancelReasons =
			Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
					TargetReason.CLOSEST_PLAYER,
					TargetReason.RANDOM_TARGET)));


	/**
	 * constructor method for {@code PlayerEventListener} class
	 * @param	plugin		A reference to this plugin's main class
	 */
	public PlayerEventListener(final PluginMain plugin) {
		
		// reference to main
		this.plugin = plugin;
		
		// register events in this class
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	@EventHandler(priority=EventPriority.LOWEST)
	void onPlayerRespawnLOWEST(final PlayerRespawnEvent event) {
		if (plugin.getConfig().getString("respawn-priority").equalsIgnoreCase("LOWEST")) {
			if (plugin.debug) {
				plugin.getLogger().info("PlayerRespawnEvent responding at LOWEST priority.");
			}
			onPlayerRespawnHandler(event);
		}
	}
	
	
	@EventHandler(priority=EventPriority.LOW)
	void onPlayerRespawnLOW(final PlayerRespawnEvent event) {
		if (plugin.getConfig().getString("respawn-priority").equalsIgnoreCase("LOW")) {
			if (plugin.debug) {
				plugin.getLogger().info("PlayerRespawnEvent responding at LOW priority.");
			}
			onPlayerRespawnHandler(event);
		}
	}
	
	
	@EventHandler(priority=EventPriority.NORMAL)
	void onPlayerRespawnNORMAL(final PlayerRespawnEvent event) {
		if (plugin.getConfig().getString("respawn-priority").equalsIgnoreCase("NORMAL")) {
			if (plugin.debug) {
				plugin.getLogger().info("PlayerRespawnEvent responding at NORMAL priority.");
			}
			onPlayerRespawnHandler(event);
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGH)
	void onPlayerRespawnHIGH(final PlayerRespawnEvent event) {
		if (plugin.getConfig().getString("respawn-priority").equalsIgnoreCase("HIGH")) {
			if (plugin.debug) {
				plugin.getLogger().info("PlayerRespawnEvent responding at HIGH priority.");
			}
			onPlayerRespawnHandler(event);
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	void onPlayerRespawnHIGHEST(final PlayerRespawnEvent event) {
		if (plugin.getConfig().getString("respawn-priority").equalsIgnoreCase("HIGHEST")) {
			if (plugin.debug) {
				plugin.getLogger().info("PlayerRespawnEvent responding at HIGHEST priority.");
			}
			onPlayerRespawnHandler(event);
		}
	}
	
	
	private void onPlayerRespawnHandler(final PlayerRespawnEvent event) {
		
		// get event player
		Player player = event.getPlayer();
		
		// get config default safety time duration
		Integer duration = plugin.getConfig().getInt("safety-time");
		
		// check that player world is enabled
		if (!plugin.worldManager.isEnabled(player.getWorld())) {
			return;
		}

		// check that player has deathspawn.respawn permission
		if (!player.hasPermission("graveyard.respawn")) {
			return;
		}
		
		// get closest valid death spawn for player
		Graveyard closest = plugin.dataStore.selectNearestGraveyard(player);
		
		// if closest death spawn is not null, set respawn location
		if (closest != null) {
			event.setRespawnLocation(closest.getLocation());
			
			// if death spawn has custom respawn message, send custom message to player
			if (closest.getRespawnMessage() != null && !closest.getRespawnMessage().isEmpty()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', closest.getRespawnMessage()));
			}
			// else send default respawn message
			else {
				plugin.messageManager.sendMessage(player, MessageId.DEFAULT_RESPAWN, closest);
			}
			
			// if death spawn safety time is not null, use to set duration
			if (closest.getSafetyTime() != null) {
				duration = closest.getSafetyTime();
			}
			
			// if safety time is negative, get configured default
			if (duration < 0) {
				duration = plugin.getConfig().getInt("safety-time");
			}
			
			// put player in safety cooldown map
			plugin.safetyManager.putPlayer(player, duration);
		}
	}

	
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


//	@EventHandler
//	void onPlayerLogout(final PlayerQuitEvent event) {
//		plugin.messageManager.removePlayerCooldown(event.getPlayer());
//	}

}
