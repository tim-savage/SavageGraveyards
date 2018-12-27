package com.winterhaven_mc.savagegraveyards.events;

import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


@SuppressWarnings("unused")
public final class DiscoveryEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Graveyard graveyard;


	public DiscoveryEvent(final Player player, final Graveyard graveyard) {
		this.player = player;
		this.graveyard = graveyard;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}


	/**
	 * Get player that triggered graveyard discovery
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}


	/**
	 * Get graveyard discovered by player
	 * @return graveyard
	 */
	public Graveyard getGraveyard() {
		return this.graveyard;
	}

}
