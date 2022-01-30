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

package com.winterhavenmc.savagegraveyards.storage;

import java.util.Objects;
import java.util.UUID;


/**
 * Class that defines a value object representing
 * a graveyard discovery for an individual player
 */
public final class Discovery {

	private final String searchKey;
	private final UUID playerUid;


	/**
	 * Class constructor
	 *
	 * @param searchKey graveyard search key
	 * @param playerUid player uuid
	 */
	public Discovery(final String searchKey, final UUID playerUid) {
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
	public boolean equals(final Object o) {
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
