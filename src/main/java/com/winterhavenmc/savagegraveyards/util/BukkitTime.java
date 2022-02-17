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

package com.winterhavenmc.savagegraveyards.util;

public enum BukkitTime {

	DAYS(86400000),
	HOURS(3600000),
	MINUTES(60000),
	SECONDS(1000),
	TICKS(50),
	MILLISECONDS(1);

	private final long millis;

	BukkitTime(final long millis) {
		this.millis = millis;
	}

	public long toMillis(final long duration) {
		return duration * millis;
	}

	public long toSeconds(final long duration) {
		return duration * millis;
	}

	public long toMinutes(final long duration) {
		return duration * millis;
	}

	public long toHours(final long duration) {
		return duration * millis;
	}

	public long toDays(final long duration) { return duration * millis; }

	public long toTicks(final long duration) {
		return duration * millis / 50;
	}

}
