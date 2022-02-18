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

@SuppressWarnings("unused")
public enum BukkitTime {

	MILLISECONDS(1),
	TICKS(50),
	SECONDS(1000),
	MINUTES(60000),
	HOURS(3600000),
	DAYS(86400000);

	private final long millis;

	BukkitTime(final long millis) {
		this.millis = millis;
	}

	public long toMillis(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) return Long.MIN_VALUE;
		if (duration > Long.MAX_VALUE / this.millis) return Long.MAX_VALUE;
		return duration * this.millis / MILLISECONDS.millis;
	}

	public long toTicks(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) return Long.MIN_VALUE;
		if (duration > Long.MAX_VALUE / this.millis) return Long.MAX_VALUE;
		return duration * this.millis / TICKS.millis;
	}

	public long toSeconds(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) return Long.MIN_VALUE;
		if (duration > Long.MAX_VALUE / this.millis) return Long.MAX_VALUE;
		return duration * this.millis / SECONDS.millis;
	}

	public long toMinutes(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) return Long.MIN_VALUE;
		if (duration > Long.MAX_VALUE / this.millis) return Long.MAX_VALUE;
		return duration * this.millis / MINUTES.millis;
	}

	public long toHours(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) return Long.MIN_VALUE;
		if (duration > Long.MAX_VALUE / this.millis) return Long.MAX_VALUE;
		return duration * this.millis / HOURS.millis;
	}

	public long toDays(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) return Long.MIN_VALUE;
		if (duration > Long.MAX_VALUE / this.millis) return Long.MAX_VALUE;
		return duration * this.millis / DAYS.millis;
	}

	public long convert(long duration, BukkitTime unit) { return (duration * this.millis / unit.millis); }

	/**
	 * For testing
	 *
	 * @return returns the number of milliseconds equal to each time unit
	 */
	long getMillis() {
		return this.millis;
	}

}
