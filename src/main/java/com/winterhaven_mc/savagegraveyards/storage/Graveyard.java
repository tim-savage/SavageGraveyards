package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Graveyard object
 */
public final class Graveyard {

	private static PluginMain plugin = PluginMain.instance;

	private final int primaryKey;
	private final String searchKey;
	private final String displayName;
	private final boolean enabled;
	private final boolean hidden;
	private final int discoveryRange;
	private final String discoveryMessage;
	private final String respawnMessage;
	private final String group;
	private final int safetyRange;
	private final int safetyTime;
	private final Location location;


	/**
	 * Private class constructor to prevent instantiation
	 */
	@SuppressWarnings("unused")
	private Graveyard() {
		throw new AssertionError();
	}


	/**
	 * Private class constructor used with static builder
	 * @param builder builder object
	 */
	private Graveyard(final Builder builder) {
		primaryKey = builder.primaryKey;
		displayName = builder.displayName;
		searchKey = builder.searchKey;
		enabled = builder.enabled;
		hidden = builder.hidden;
		discoveryRange = builder.discoveryRange;
		discoveryMessage = builder.discoveryMessage;
		respawnMessage = builder.respawnMessage;
		group = builder.group;
		safetyRange = builder.safetyRange;
		safetyTime = builder.safetyTime;
		location = builder.location;
	}


	public final static class Builder {

		private int primaryKey;
		private String displayName;
		private String searchKey;
		private boolean enabled = plugin.getConfig().getBoolean("default-enabled");
		private boolean hidden = plugin.getConfig().getBoolean("default-hidden");
		private int discoveryRange = -1;
		private String discoveryMessage = "";
		private String respawnMessage = "";
		private String group = "";
		private int safetyRange = -1;
		private int safetyTime = -1;
		private Location location;


		public Builder() { }


		public Builder(Graveyard graveyard) {
			this.primaryKey = graveyard.getPrimaryKey();
			this.displayName = graveyard.getDisplayName();
			this.searchKey = graveyard.getSearchKey();
			this.enabled = graveyard.isEnabled();
			this.hidden = graveyard.isHidden();
			this.discoveryRange = graveyard.getDiscoveryRange();
			this.respawnMessage = graveyard.getRespawnMessage();
			this.group = graveyard.getGroup();
			this.safetyRange = graveyard.getSafetyRange();
			this.safetyTime = graveyard.getSafetyTime();
			this.location = graveyard.getLocation();
		}


		final Builder primaryKey(final int val) {
			primaryKey = val;
			return this;
		}

		public final Builder displayName(final String val) {
			displayName = val;
			searchKey = createSearchKey(val);
			return this;
		}

		final Builder searchKey(final String val) {
			searchKey = val;
			return this;
		}

		public final Builder enabled(final boolean val) {
			enabled = val;
			return this;
		}

		public final Builder hidden(final boolean val) {
			hidden = val;
			return this;
		}

		public final Builder discoveryRange(final int val) {
			discoveryRange = val;
			return this;
		}

		public final Builder discoveryMessage(final String val) {
			discoveryMessage = val;
			return this;
		}

		public final Builder respawnMessage(final String val) {
			respawnMessage = val;
			return this;
		}

		public final Builder group(final String val) {
			group = val;
			return this;
		}

		final Builder safetyRange(final int val) {
			safetyRange = val;
			return this;
		}

		public final Builder safetyTime(final int val) {
			safetyTime = val;
			return this;
		}

		public final Builder location(final Location val) {

			// create defensive copy of passed location
			location = new Location(val.getWorld(),
					val.getX(),
					val.getY(),
					val.getZ(),
					val.getYaw(),
					val.getPitch());
			return this;
		}

		public final Graveyard build() {
			return new Graveyard(this);
		}
	}


	/**
	 * Getter for primary key
	 * @return int - primary key
	 */
	final int getPrimaryKey() {
		return this.primaryKey;
	}


	/**
	 * Getter for display name
	 * @return String - display name
	 */
	public final String getDisplayName() {
		return this.displayName;
	}


	/**
	 * Getter for search key
	 * @return String - search key
	 */
	public final String getSearchKey() {
		return this.searchKey;
	}


	/**
	 * Getter for enabled
	 * @return boolean - enabled
	 */
	public final boolean isEnabled() {
		return this.enabled;
	}


	/**
	 * Getter for hidden
	 * @return boolean - hidden
	 */
	public final boolean isHidden() {
		return this.hidden;
	}


	/**
	 * Getter for discovery range
	 * @return int - discovery range
	 */
	public final int getDiscoveryRange() {
		return this.discoveryRange;
	}


	/**
	 * Getter for group
	 * @return String - group
	 */
	public final String getGroup() {
		return this.group;
	}


	/**
	 * Getter for discovery message
	 * @return String - discovery message
	 */
	public final String getDiscoveryMessage() {
		return this.discoveryMessage;
	}


	/**
	 * Getter for respawn message
	 * @return String - respawn message
	 */
	public final String getRespawnMessage() {
		return this.respawnMessage;
	}


	/**
	 * Getter for location
	 * @return Location - location
	 */
	public final Location getLocation() {

		// return defensive copy of location
		return new Location(this.location.getWorld(),
				this.location.getX(),
				this.location.getY(),
				this.location.getZ(),
				this.location.getYaw(),
				this.location.getPitch());
	}


	/**
	 * Getter for safety range (currently unused)
	 * @return int - safety range
	 */
	final int getSafetyRange() {
		return this.safetyRange;
	}


	/**
	 * Getter for safety time
	 * @return int safety time
	 */
	public final int getSafetyTime() {
		return this.safetyTime;
	}


	/**
	 * Static method to create search key from graveyard display name;
	 * strips color codes and replaces spaces with underscores;
	 * preserves case
	 * @param displayName the graveyard display name
	 * @return String - a search key derived from graveyard display name
	 */
	static String createSearchKey(String displayName) {
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		return ChatColor.stripColor(displayName.replace(' ', '_'));
	}

}
