package com.winterhaven_mc.savagegraveyards.util;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Cancel mob targeting of players for configured period after respawn
 */
public class SafetyManager {

	private final PluginMain plugin;
	private HashMap<UUID,Integer> safetyCooldownMap = new HashMap<>();
	

	/**
	 * Class constructor
	 * @param plugin reference to main class
	 */
	public SafetyManager(final PluginMain plugin) {
		this.plugin = plugin;
	}

	
	/**
	 * Insert player uuid into safety cooldown map
	 * @param player the player whose uuid will be used as key in the safety cooldown map
	 * @param duration in seconds
	 */
	public void putPlayer(final Player player, int duration) {

		// if duration is negative, use configured default
		if (duration < 0) {
			duration = plugin.getConfig().getInt("safety-time");
		}
		
		// if duration is less than one, do nothing and return
		if (duration < 1) {
			return;
		}
		
		safetyCooldownMap.put(player.getUniqueId(), duration);
		
		plugin.messageManager.sendPlayerMessage(player, MessageId.SAFETY_COOLDOWN_START);

		// create task to remove player from map after duration
		new BukkitRunnable() {
			@Override
			public void run() {
				removePlayer(player);
				plugin.messageManager.sendPlayerMessage(player, MessageId.SAFETY_COOLDOWN_END);
			}
		}.runTaskLater(plugin, duration * 20);
	}

	
	/**
	 * Remove player from safety cooldown map
	 * @param player the player to be removed from the safety cooldown map
	 */
	private void removePlayer(final Player player) {
		safetyCooldownMap.remove(player.getUniqueId());
	}
	
	
	/**
	 * Check if player is in safety cooldown map
	 * @param player the player to test if in the safety cooldown map
	 * @return {@code true} if the player is in the safety cooldown map, {@code false} if they are not
	 */
	public boolean isPlayerProtected(final Player player) {

		return safetyCooldownMap.containsKey(player.getUniqueId());
	}
	
	
	/**
	 * Get player safety cooldown duration
	 * @param player the player for whom to retrieve duration from the safety cooldown map
	 * @return the duration of the player's safety cooldown, or zero if they player is not in the safety cooldown map
	 */
	int getDuration(final Player player) {
		
		int duration = 0;
		if (isPlayerProtected(player)) {
			duration = safetyCooldownMap.get(player.getUniqueId());
		}
		return duration;
	}
}
