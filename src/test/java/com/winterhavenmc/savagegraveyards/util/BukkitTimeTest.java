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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.winterhavenmc.savagegraveyards.util.BukkitTime.*;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitTimeTest {

	@Test
	void convert() {
		Assertions.assertEquals(1, MILLISECONDS.convert(1000, SECONDS), "convert millis to seconds failed");
		Assertions.assertEquals(1000, SECONDS.convert(1, MILLISECONDS), "convert seconds to millis failed");

		Assertions.assertEquals(1, MINUTES.convert(60, HOURS), "convert minutes to hours failed");
		Assertions.assertEquals(60, HOURS.convert(1, MINUTES), "convert hours to minutes failed");

		Assertions.assertEquals(1, HOURS.convert(24, DAYS), "convert hours to days failed");
		Assertions.assertEquals(24, DAYS.convert(1, HOURS), "convert days to hours failed");

		Assertions.assertEquals(1, TICKS.convert(20, SECONDS), "convert ticks to seconds failed");
		Assertions.assertEquals(20, SECONDS.convert(1, TICKS), "convert seconds to ticks failed");
	}

	@Test
	void toMillis() {
		Assertions.assertEquals(1, MILLISECONDS.getMillis());
		Assertions.assertEquals(86400000, DAYS.toMillis(1), "DAYS to millis failed.");
		Assertions.assertEquals(3600000, HOURS.toMillis(1), "HOURS to millis failed.");
		Assertions.assertEquals(60000, MINUTES.toMillis(1),"MINUTES to millis failed.");
		Assertions.assertEquals(1000, SECONDS.toMillis(1), "SECONDS to millis failed.");
		Assertions.assertEquals(50, TICKS.toMillis(1), "TICKS to millis failed.");
		Assertions.assertEquals(1, MILLISECONDS.toMillis(1), "MILLISECONDS to millis failed.");

		Assertions.assertEquals(Long.MAX_VALUE, DAYS.toMillis(Long.MAX_VALUE - 1), "MAX_VALUE exceeded.");
		Assertions.assertEquals(Long.MIN_VALUE, DAYS.toMillis(Long.MIN_VALUE + 1), "MIN_VALUE exceeded.");
	}

	@Test
	void toTicks() {
		Assertions.assertEquals(50, TICKS.getMillis());
		Assertions.assertEquals(1728000, DAYS.toTicks(1), "DAYS to seconds failed.");
		Assertions.assertEquals(72000, HOURS.toTicks(1), "HOURS to seconds failed.");
		Assertions.assertEquals(1200, MINUTES.toTicks(1), "MINUTES to seconds failed.");
		Assertions.assertEquals(20, SECONDS.toTicks(1), "SECONDS to seconds failed.");
		Assertions.assertEquals(1, TICKS.toTicks(1), "TICKS to seconds failed.");
		Assertions.assertEquals(1, MILLISECONDS.toTicks(50), "MILLISECONDS to seconds failed.");

		Assertions.assertEquals(Long.MAX_VALUE, DAYS.toTicks(Long.MAX_VALUE - 1), "MAX_VALUE exceeded.");
		Assertions.assertEquals(Long.MIN_VALUE, DAYS.toTicks(Long.MIN_VALUE + 1), "MIN_VALUE exceeded.");
	}

	@Test
	void toSeconds() {
		Assertions.assertEquals(1000, SECONDS.getMillis());
		Assertions.assertEquals(86400, DAYS.toSeconds(1), "DAYS to seconds failed.");
		Assertions.assertEquals(3600, HOURS.toSeconds(1), "HOURS to seconds failed.");
		Assertions.assertEquals(60, MINUTES.toSeconds(1), "MINUTES to seconds failed.");
		Assertions.assertEquals(1, SECONDS.toSeconds(1), "SECONDS to seconds failed.");
		Assertions.assertEquals(1, TICKS.toSeconds(20), "TICKS to seconds failed.");
		Assertions.assertEquals(1, MILLISECONDS.toSeconds(1000), "MILLISECONDS to seconds failed.");

		Assertions.assertEquals(Long.MAX_VALUE, DAYS.toSeconds(Long.MAX_VALUE - 1), "MAX_VALUE exceeded.");
		Assertions.assertEquals(Long.MIN_VALUE, DAYS.toSeconds(Long.MIN_VALUE + 1), "MIN_VALUE exceeded.");
	}

	@Test
	void toMinutes() {
		Assertions.assertEquals(60000, MINUTES.getMillis());
		Assertions.assertEquals(1440, DAYS.toMinutes(1), "DAYS to seconds failed.");
		Assertions.assertEquals(60, HOURS.toMinutes(1), "HOURS to seconds failed.");
		Assertions.assertEquals(1, MINUTES.toMinutes(1), "MINUTES to seconds failed.");
		Assertions.assertEquals(1, SECONDS.toMinutes(60), "SECONDS to seconds failed.");
		Assertions.assertEquals(1, TICKS.toMinutes(1200), "TICKS to seconds failed.");
		Assertions.assertEquals(1, MILLISECONDS.toMinutes(60000), "MILLISECONDS to seconds failed.");

		Assertions.assertEquals(Long.MAX_VALUE, DAYS.toMinutes(Long.MAX_VALUE - 1), "MAX_VALUE exceeded.");
		Assertions.assertEquals(Long.MIN_VALUE, DAYS.toMinutes(Long.MIN_VALUE + 1), "MIN_VALUE exceeded.");
	}

	@Test
	void toHours() {
		Assertions.assertEquals(3600000, HOURS.getMillis());
		Assertions.assertEquals(24, DAYS.toHours(1), "DAYS to seconds failed.");
		Assertions.assertEquals(1, HOURS.toHours(1), "HOURS to seconds failed.");
		Assertions.assertEquals(1, MINUTES.toHours(60), "MINUTES to seconds failed.");
		Assertions.assertEquals(1, SECONDS.toHours(3600), "SECONDS to seconds failed.");
		Assertions.assertEquals(1, TICKS.toHours(72000), "TICKS to seconds failed.");
		Assertions.assertEquals(1, MILLISECONDS.toHours(3600000), "MILLISECONDS to seconds failed.");

		Assertions.assertEquals(Long.MAX_VALUE, DAYS.toTicks(Long.MAX_VALUE - 1), "MAX_VALUE exceeded.");
		Assertions.assertEquals(Long.MIN_VALUE, DAYS.toTicks(Long.MIN_VALUE + 1), "MIN_VALUE exceeded.");
	}

	@Test
	void toDays() {
		Assertions.assertEquals(86400000, DAYS.getMillis());
		Assertions.assertEquals(1, DAYS.toDays(1), "DAYS to seconds failed.");
		Assertions.assertEquals(1, HOURS.toDays(24), "HOURS to seconds failed.");
		Assertions.assertEquals(1, MINUTES.toDays(1440), "MINUTES to seconds failed.");
		Assertions.assertEquals(1, SECONDS.toDays(86400), "SECONDS to seconds failed.");
		Assertions.assertEquals(1, TICKS.toDays(1728000), "TICKS to seconds failed.");
		Assertions.assertEquals(1, MILLISECONDS.toDays(86400000), "MILLISECONDS to seconds failed.");

		Assertions.assertEquals(Long.MAX_VALUE, DAYS.toTicks(Long.MAX_VALUE - 1), "MAX_VALUE exceeded.");
		Assertions.assertEquals(Long.MIN_VALUE, DAYS.toTicks(Long.MIN_VALUE + 1), "MIN_VALUE exceeded.");
	}

	@Test
	void values() {
		BukkitTime[] bukkitTimes = { MILLISECONDS, TICKS, SECONDS, MINUTES, HOURS, DAYS };
		Assertions.assertArrayEquals(BukkitTime.values(), bukkitTimes, "BukkitTime values did not match.");
	}

	@Test
	void valueOf() {
		// test all valid member names
		Assertions.assertEquals(MILLISECONDS, BukkitTime.valueOf("MILLISECONDS"));
		Assertions.assertEquals(TICKS, BukkitTime.valueOf("TICKS"));
		Assertions.assertEquals(SECONDS, BukkitTime.valueOf("SECONDS"));
		Assertions.assertEquals(MINUTES, BukkitTime.valueOf("MINUTES"));
		Assertions.assertEquals(HOURS, BukkitTime.valueOf("HOURS"));
		Assertions.assertEquals(DAYS, BukkitTime.valueOf("DAYS"));

		// test invalid member name
		Exception exception = assertThrowsExactly(IllegalArgumentException.class, () -> BukkitTime.valueOf("invalid"));

		String expectedMessage = "No enum constant";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.startsWith(expectedMessage));
	}

}
