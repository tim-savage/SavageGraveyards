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

import java.util.stream.LongStream;

import static com.winterhavenmc.savagegraveyards.util.BukkitTime.*;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitTimeTest {

	@Test
	void toMillis() {
		// check that millis field holds correct value for milliseconds in a millisecond (1)
		Assertions.assertEquals(1, MILLISECONDS.getMillis());

		// iterate over range testing each time unit conversion to milliseconds
		for (long duration : LongStream.range(-1000, 1000).toArray()) {
			Assertions.assertEquals(duration, MILLISECONDS.toMillis(duration), "MILLISECONDS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 50, TICKS.toMillis(duration), "TICKS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1000, SECONDS.toMillis(duration), "SECONDS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 60000, MINUTES.toMillis(duration), "MINUTES to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 3600000, HOURS.toMillis(duration), "HOURS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 86400000, DAYS.toMillis(duration), "DAYS to millis failed with duration " + duration + ".");
		}

		// iterate through all time units and perform tests
		for (BukkitTime timeUnit : BukkitTime.values()) {

			// test all conversions with duration of zero
			Assertions.assertEquals(0, timeUnit.toMillis(0), "zero " + timeUnit + " to milliseconds failed.");

			// test overflow with min values
			Assertions.assertEquals(Long.MIN_VALUE, DAYS.toMillis(Long.MIN_VALUE + 1), "MIN + 1 " + timeUnit + " to milliseconds failed overflow test.");

			// skip overflow test for milliseconds to milliseconds, because it cannot overflow, even with max values
			if (timeUnit == MILLISECONDS) continue;

			// test overflow with max values
			Assertions.assertEquals(Long.MAX_VALUE, timeUnit.toMillis(Long.MAX_VALUE - 1), "MAX - 1 " + timeUnit + " to milliseconds failed overflow test.");
		}
	}

	@Test
	void toTicks() {
		// check that millis field holds correct value for milliseconds in a tick (50)
		Assertions.assertEquals(50, TICKS.getMillis());

		// iterate over range testing each time unit conversion to ticks
		for (long duration : LongStream.range(-1000, 1000).toArray()) {
			Assertions.assertEquals(duration / 50, MILLISECONDS.toTicks(duration), "MILLISECONDS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration, TICKS.toTicks(duration), "TICKS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 20, SECONDS.toTicks(duration), "SECONDS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1200, MINUTES.toTicks(duration), "MINUTES to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 72000, HOURS.toTicks(duration), "HOURS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1728000, DAYS.toTicks(duration), "DAYS to ticks failed with duration " + duration + ".");
		}

		// iterate through all time units and perform tests
		for (BukkitTime timeUnit : BukkitTime.values()) {

			// test all conversions with duration of zero
			Assertions.assertEquals(0, timeUnit.toTicks(0), "zero " + timeUnit + " to ticks failed.");

			// test overflow with max values
			Assertions.assertEquals(Long.MAX_VALUE, timeUnit.toTicks(Long.MAX_VALUE - 1), "MAX - 1 " + timeUnit + " to ticks failed overflow test.");

			// test overflow with min values
			Assertions.assertEquals(Long.MIN_VALUE, DAYS.toTicks(Long.MIN_VALUE + 1), "MIN + 1 " + timeUnit + " to ticks failed overflow test.");
		}
	}

	@Test
	void toSeconds() {
		// check that millis field holds correct value for milliseconds in a second (1000)
		Assertions.assertEquals(1000, SECONDS.getMillis());

		// iterate over range testing each time unit conversion to seconds
		for (long duration : LongStream.range(-1000, 1000).toArray()) {
			Assertions.assertEquals(duration / 1000, MILLISECONDS.toSeconds(duration), "MILLISECONDS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 20, TICKS.toSeconds(duration), "TICKS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration, SECONDS.toSeconds(duration), "SECONDS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 60, MINUTES.toSeconds(duration), "MINUTES to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 3600, HOURS.toSeconds(duration), "HOURS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 86400, DAYS.toSeconds(duration), "DAYS to seconds failed with duration " + duration + ".");
		}

		// iterate through all time units and perform tests
		for (BukkitTime timeUnit : BukkitTime.values()) {

			// test all conversions with duration of zero
			Assertions.assertEquals(0, timeUnit.toSeconds(0), "zero " + timeUnit + " to seconds failed.");

			// test overflow with max values
			Assertions.assertEquals(Long.MAX_VALUE, timeUnit.toSeconds(Long.MAX_VALUE - 1), "MAX - 1 " + timeUnit + " to seconds failed overflow test.");

			// test overflow with min values
			Assertions.assertEquals(Long.MIN_VALUE, timeUnit.toSeconds(Long.MIN_VALUE + 1), "MIN + 1 " + timeUnit + " to seconds failed overflow test.");
		}
	}

	@Test
	void toMinutes() {
		// check that millis field holds correct value for milliseconds in a minute (60000)
		Assertions.assertEquals(60 * 1000, MINUTES.getMillis());

		// iterate over range testing each time unit conversion to minutes
		for (long duration : LongStream.range(-1000, 1000).toArray()) {
			Assertions.assertEquals(duration / 6000, MILLISECONDS.toMinutes(duration), "MILLISECONDS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 1200, TICKS.toMinutes(duration), "TICKS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 60, SECONDS.toMinutes(duration), "SECONDS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration, MINUTES.toMinutes(duration), "MINUTES to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 60, HOURS.toMinutes(duration), "HOURS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1440, DAYS.toMinutes(duration), "DAYS to minutes failed with duration " + duration + ".");
		}

		// iterate through all time units and perform tests
		for (BukkitTime timeUnit : BukkitTime.values()) {

			// test all conversions with duration of zero
			Assertions.assertEquals(0, timeUnit.toMinutes(0), "zero " + timeUnit + " to minutes failed.");

			// test overflow with max values
			Assertions.assertEquals(Long.MAX_VALUE, timeUnit.toMinutes(Long.MAX_VALUE - 1), "MAX - 1 " + timeUnit + " to minutes failed overflow test.");

			// test overflow with min values
			Assertions.assertEquals(Long.MIN_VALUE, timeUnit.toMinutes(Long.MIN_VALUE + 1), "MIN + 1 " + timeUnit + " to minutes failed overflow test.");
		}
	}

	@Test
	void toHours() {
		// check that millis field holds correct value for milliseconds in an hour (3600000)
		Assertions.assertEquals(60 * 60 * 1000, HOURS.getMillis());

		// iterate over range testing each time unit conversion to hours
		for (long duration : LongStream.range(-1000, 1000).toArray()) {
			Assertions.assertEquals(duration / 3600000, MILLISECONDS.toHours(duration), "MILLISECONDS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 72000, TICKS.toHours(duration), "TICKS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 3600, SECONDS.toHours(duration), "SECONDS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 60, MINUTES.toHours(duration), "MINUTES to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration, HOURS.toHours(duration), "HOURS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 24, DAYS.toHours(duration), "DAYS to hours failed with duration " + duration + ".");
		}

		// iterate through all time units and perform tests
		for (BukkitTime timeUnit : BukkitTime.values()) {

			// test all conversions with duration of zero
			Assertions.assertEquals(0, timeUnit.toHours(0), "zero " + timeUnit + " to hours failed.");

			// test overflow with max values
			Assertions.assertEquals(Long.MAX_VALUE, timeUnit.toHours(Long.MAX_VALUE - 1), "MAX - 1 " + timeUnit + " to hours failed overflow test.");

			// test overflow with min values
			Assertions.assertEquals(Long.MIN_VALUE, timeUnit.toHours(Long.MIN_VALUE + 1), "MIN + 1 " + timeUnit + " to hours failed overflow test.");
		}
	}

	@Test
	void toDays() {
		// check that millis field holds correct value for milliseconds in a day (86400000)
		Assertions.assertEquals(24 * 60 * 60 * 1000, DAYS.getMillis());

		// iterate over range testing each time unit conversion to days
		for (long duration : LongStream.range(-1000, 1000).toArray()) {
			Assertions.assertEquals(duration / 86400000, MILLISECONDS.toDays(duration), "MILLISECONDS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 1728000, TICKS.toDays(duration), "TICKS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 86400, SECONDS.toDays(duration), "SECONDS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 1440, MINUTES.toDays(duration), "MINUTES to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 24, HOURS.toDays(duration), "HOURS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration, DAYS.toDays(duration), "DAYS to days failed with duration " + duration + ".");
		}

		// iterate through all time units and perform tests
		for (BukkitTime timeUnit : BukkitTime.values()) {

			// test all conversions with duration of zero
			Assertions.assertEquals(0, timeUnit.toDays(0), "zero " + timeUnit + " to days failed.");

			// test overflow with max values
			Assertions.assertEquals(Long.MAX_VALUE, timeUnit.toDays(Long.MAX_VALUE - 1), "MAX - 1 " + timeUnit + " to days failed overflow test.");

			// test overflow with min values
			Assertions.assertEquals(Long.MIN_VALUE, timeUnit.toDays(Long.MIN_VALUE + 1), "MIN + 1 " + timeUnit + " to days failed overflow test.");
		}
	}

	@Test
	void convert() {
		Assertions.assertEquals(1, MILLISECONDS.convert(1000, SECONDS), "convert millis to seconds failed.");
		Assertions.assertEquals(1000, SECONDS.convert(1, MILLISECONDS), "convert seconds to millis failed.");

		Assertions.assertEquals(1, MINUTES.convert(60, HOURS), "convert minutes to hours failed.");
		Assertions.assertEquals(60, HOURS.convert(1, MINUTES), "convert hours to minutes failed.");

		Assertions.assertEquals(1, HOURS.convert(24, DAYS), "convert hours to days failed.");
		Assertions.assertEquals(24, DAYS.convert(1, HOURS), "convert days to hours failed.");

		Assertions.assertEquals(1, TICKS.convert(20, SECONDS), "convert ticks to seconds failed.");
		Assertions.assertEquals(20, SECONDS.convert(1, TICKS), "convert seconds to ticks failed.");
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
