package com.winterhaven_mc.savagegraveyards.storage;

import java.util.Objects;
import java.util.UUID;


/**
 * Class that defines a value object representing
 * a graveyard discovery for an individual player
 */
public class Discovery {

	private final String searchKey;
	private final UUID playerUid;


	/**
	 * Class constructor
	 *
	 * @param searchKey graveyard search key
	 * @param playerUid player uuid
	 */
	public Discovery(String searchKey, UUID playerUid) {
		this.searchKey = searchKey;
		this.playerUid = playerUid;
	}


	/**
	 * Getter for search key
	 *
	 * @return String - search key
	 */
	public String getSearchKey() {
		return searchKey;
	}


	/**
	 * Getter for player uuid
	 *
	 * @return UUID - player uuid
	 */
	public UUID getPlayerUid() {
		return playerUid;
	}


	@Override
	public String toString() {
		return "Discovery{" +
				"searchKey='" + searchKey + '\'' +
				", playerUid=" + playerUid +
				'}';
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Discovery discovery = (Discovery) o;
		return Objects.equals(searchKey, discovery.searchKey) &&
				Objects.equals(playerUid, discovery.playerUid);
	}


	@Override
	public int hashCode() {
		return Objects.hash(searchKey, playerUid);
	}
}
