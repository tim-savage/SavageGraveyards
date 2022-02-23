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

import com.winterhavenmc.savagegraveyards.PluginMain;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;

public class MetricsHandler {


	public MetricsHandler(PluginMain plugin) {

		Metrics metrics = new Metrics(plugin, 13924);

		// get total number of graveyards as single line chart
		metrics.addCustomChart(new SingleLineChart("total_graveyards", () -> plugin.dataStore.selectGraveyardCount()));

		// total number of graveyards as pie chart
		metrics.addCustomChart(new SimplePie("graveyard_count", () -> String.valueOf(plugin.dataStore.selectGraveyardCount())));

	}

}
