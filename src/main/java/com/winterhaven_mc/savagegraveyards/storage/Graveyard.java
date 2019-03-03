package com.winterhaven_mc.savagegraveyards.storage;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import org.bukkit.ChatColor;
import org.bukkit.Location;


/**
 * Graveyard object
 */
public final class Graveyard {

	// static reference to plugin main class
	private static PluginMain plugin = PluginMain.instance;

	// constant value for integer attributes to use configured default
	private final static int CONFIG_DEFAULT = -1;

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
	 * Private class constructor for use with static builder
	 *
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


	/**
	 * Builder class
	 */
	public final static class Builder {

		private int primaryKey;
		private String displayName;
		private String searchKey;
		private boolean enabled = plugin.getConfig().getBoolean("default-enabled");
		private boolean hidden = plugin.getConfig().getBoolean("default-hidden");
		private int discoveryRange = CONFIG_DEFAULT;
		private String discoveryMessage = "";
		private String respawnMessage = "";
		private String group = "";
		private int safetyRange = CONFIG_DEFAULT;
		private int safetyTime = CONFIG_DEFAULT;
		private Location location;


		/**
		 * Builder class constructor
		 */
		public Builder() { }


		/**
		 * Builder class constructor
		 *
		 * @param graveyard existing graveyard object from which all values are copied
		 */
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


		/**
		 * set primary key
		 *
		 * @param value int value to assign to builder primary key field
		 * @return this Builder object
		 */
		public final Builder primaryKey(final int value) {
			primaryKey = value;
			return this;
		}


		/**
		 * set display name field
		 *
		 * @param value string value to assign to builder display name field
		 * @return this Builder object
		 */
		public final Builder displayName(final String value) {
			displayName = value;
			searchKey = createSearchKey(value);
			return this;
		}


		/**
		 * set search key field
		 *
		 * @param value string value to assign to builder search key field
		 * @return this Builder object
		 */
		final Builder searchKey(final String value) {
			searchKey = value;
			return this;
		}


		/**
		 * set enabled field
		 *
		 * @param value boolean value to assign to builder enabled field
		 * @return this Builder object
		 */
		public final Builder enabled(final boolean value) {
			enabled = value;
			return this;
		}


		/**
		 * set hidden field
		 *
		 * @param value boolean value to assign to builder hidden field
		 * @return this Builder object
		 */
		public final Builder hidden(final boolean value) {
			hidden = value;
			return this;
		}


		/**
		 * set discovery range field
		 *
		 * @param value int value to assign to builder discovery range field
		 * @return this Builder object
		 */
		public final Builder discoveryRange(final int value) {
			discoveryRange = value;
			return this;
		}


		/**
		 * set discovery message field
		 *
		 * @param value string value to assign to builder discover message field
		 * @return this Builder object
		 */
		public final Builder discoveryMessage(final String value) {
			discoveryMessage = value;
			return this;
		}


		/**
		 * set respawn message field
		 *
		 * @param value string value to assign to builder respawn message field
		 * @return this Builder object
		 */
		public final Builder respawnMessage(final String value) {
			respawnMessage = value;
			return this;
		}


		/**
		 * set group field
		 *
		 * @param value string value to assign to builder group field
		 * @return this Builder object
		 */
		public final Builder group(final String value) {
			group = value;
			return this;
		}


		/**
		 * set safety range field (currently unused)
		 *
		 * @param value int value to assign to builder safety range field
		 * @return this Builder object
		 */
		final Builder safetyRange(final int value) {
			safetyRange = value;
			return this;
		}


		/**
		 * set safety time field
		 *
		 * @param value int value to assign to builder safety time field
		 * @return this Builder object
		 */
		public final Builder safetyTime(final int value) {
			safetyTime = value;
			return this;
		}


		/**
		 * set location field
		 *
		 * @param value location value to assign to builder location field
		 * @return this Builder object
		 */
		public final Builder location(final Location value) {

			// create defensive copy of passed location
			location = new Location(value.getWorld(),
					value.getX(),
					value.getY(),
					value.getZ(),
					value.getYaw(),
					value.getPitch());
			return this;
		}

		/**
		 * build Graveyard object from builder fields
		 *
		 * @return new Graveyard object
		 */
		public final Graveyard build() {
			return new Graveyard(this);
		}
	}


	/**
	 * Getter for primary key
	 *
	 * @return int - primary key
	 */
	public final int getPrimaryKey() {
		return this.primaryKey;
	}


	/**
	 * Getter for display name
	 *
	 * @return String - display name
	 */
	public final String getDisplayName() {
		return this.displayName;
	}


	/**
	 * Getter for search key
	 *
	 * @return String - search key
	 */
	public final String getSearchKey() {
		return this.searchKey;
	}


	/**
	 * Getter for enabled
	 *
	 * @return boolean - enabled
	 */
	public final boolean isEnabled() {
		return this.enabled;
	}


	/**
	 * Getter for hidden
	 *
	 * @return boolean - hidden
	 */
	public final boolean isHidden() {
		return this.hidden;
	}


	/**
	 * Getter for discovery range
	 *
	 * @return int - discovery range
	 */
	public final int getDiscoveryRange() {
		return this.discoveryRange;
	}


	/**
	 * Getter for group
	 *
	 * @return String - group
	 */
	public final String getGroup() {
		return this.group;
	}


	/**
	 * Getter for discovery message
	 *
	 * @return String - discovery message
	 */
	public final String getDiscoveryMessage() {
		return this.discoveryMessage;
	}


	/**
	 * Getter for respawn message
	 *
	 * @return String - respawn message
	 */
	public final String getRespawnMessage() {
		return this.respawnMessage;
	}


	/**
	 * Getter for location
	 *
	 * @return Location - location
	 */
	public final Location getLocation() {

		// if location is null, return null
		if (this.location == null) {
			return null;
		}

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
	 *
	 * @return int - safety range
	 */
	final int getSafetyRange() {
		return this.safetyRange;
	}


	/**
	 * Getter for safety time
	 *
	 * @return int - safety time
	 */
	public final int getSafetyTime() {
		return this.safetyTime;
	}


	/**
	 * Static method to create search key from graveyard display name;
	 * strips color codes and replaces spaces with underscores;
	 * preserves case
	 *
	 * @param displayName the graveyard display name
	 * @return String - a search key derived from graveyard display name
	 */
	static String createSearchKey(String displayName) {
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		return ChatColor.stripColor(displayName.replace(' ', '_'));
	}

}
