package com.winterhavenmc.savagegraveyards.events;

import com.winterhavenmc.savagegraveyards.storage.Graveyard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;


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
	@Nonnull
	public HandlerList getHandlers() {
		return handlers;
	}


	/**
	 * Get player that triggered graveyard discovery
	 *
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}


	/**
	 * Get graveyard discovered by player
	 *
	 * @return graveyard
	 */
	public Graveyard getGraveyard() {
		return this.graveyard;
	}

}