package com.winterhaven_mc.savagegraveyards.storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


/**
 * Graveyard object
 */
public final class Graveyard {

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
	private final long safetyTime;
	private final String worldName;
	private final UUID worldUid;
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;


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
		worldName = builder.worldName;
		worldUid = builder.worldUid;
		x = builder.x;
		y = builder.y;
		z = builder.z;
		yaw = builder.yaw;
		pitch = builder.pitch;
	}


	/**
	 * Builder class
	 */
	public final static class Builder {

		private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(this.getClass());

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
		private long safetyTime = CONFIG_DEFAULT;
		private String worldName = "";
		private UUID worldUid = null;
		private double x = 0;
		private double y = 0;
		private double z = 0;
		private float yaw = 0;
		private float pitch = 0;

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
			this.worldName = graveyard.getWorldName();
			this.worldUid = graveyard.getWorldUid();
			this.x = graveyard.getX();
			this.y = graveyard.getY();
			this.z = graveyard.getZ();
			this.yaw = graveyard.getYaw();
			this.pitch = graveyard.getPitch();
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
		 * @param value location value to assign to builder location fields
		 * @return this Builder object
		 */
		public final Builder location(final Location value) {

			// if passed location is null, set worldUid null and let other values default to zero
			if (value == null || value.getWorld() == null) {
				worldUid = null;
			}
			else {
				worldUid = value.getWorld().getUID();
				x = value.getX();
				y = value.getY();
				z = value.getZ();
				yaw = value.getYaw();
				pitch = value.getPitch();
			}
			return this;
		}


		/**
		 * Set worldName field
		 * @param value String value to assign to builder worldName field
		 * @return this builder object
		 */
		public final Builder worldName(final String value) {
			worldName = value;
			return this;
		}


		/**
		 * Set worldUid field
		 *
		 * @param value UUID value to assign to builder worldUid field
		 * @return this builder object
		 */
		public final Builder worldUid(final UUID value) {
			worldUid = value;
			return this;
		}


		/**
		 * Set x field
		 * @param value double value to assign to builder x field
		 * @return this builder object
		 */
		public final Builder x(final double value) {
			x = value;
			return this;
		}


		/**
		 * Set y field
		 * @param value double value to assign to builder y field
		 * @return this builder object
		 */
		public final Builder y(final double value) {
			y = value;
			return this;
		}


		/**
		 * Set z field
		 * @param value double value to assign to builder z field
		 * @return this builder object
		 */
		public final Builder z(final double value) {
			z = value;
			return this;
		}


		/**
		 * Set yaw field
		 * @param value float value to assign to builder yaw field
		 * @return this builder object
		 */
		public final Builder yaw(final float value) {
			yaw = value;
			return this;
		}


		/**
		 * Set pitch field
		 * @param value float value to assign to builder pitch field
		 * @return this builder object
		 */
		public final Builder pitch(final float value) {
			pitch = value;
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
	 * Override toString method to return graveyard display name
	 * @return String - graveyard display name
	 */
	@Override
	public String toString() {
		return displayName;
	}


	/**
	 * Getter for primary key
	 *
	 * @return int - primary key
	 */
	public final int getPrimaryKey() {
		return primaryKey;
	}


	/**
	 * Getter for display name
	 *
	 * @return String - display name
	 */
	public final String getDisplayName() {
		return displayName;
	}


	/**
	 * Getter for search key
	 *
	 * @return String - search key
	 */
	public final String getSearchKey() {
		return searchKey;
	}


	/**
	 * Getter for enabled
	 *
	 * @return boolean - enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}


	/**
	 * Getter for hidden
	 *
	 * @return boolean - hidden
	 */
	public final boolean isHidden() {
		return hidden;
	}


	/**
	 * Getter for discovery range
	 *
	 * @return int - discovery range
	 */
	public final int getDiscoveryRange() {
		return discoveryRange;
	}


	/**
	 * Getter for group
	 *
	 * @return String - group
	 */
	public final String getGroup() {
		return group;
	}


	/**
	 * Getter for discovery message
	 *
	 * @return String - discovery message
	 */
	public final String getDiscoveryMessage() {
		return discoveryMessage;
	}


	/**
	 * Getter for respawn message
	 *
	 * @return String - respawn message
	 */
	public final String getRespawnMessage() {
		return respawnMessage;
	}


	/**
	 * Getter for location; returns new location object constructed from components.
	 * If worldUid is null, or if world referenced by worldUid is invalid,
	 * perhaps because it has been unloaded, the returned location will be null.
	 *
	 * @return Location - location
	 */
	public final Location getLocation() {

		// if worldUid is null, return null
		if (worldUid == null) {
			return null;
		}

		// get world by uid
		World world = Bukkit.getServer().getWorld(worldUid);

		// if world is null, return null
		if (world == null) {
			return null;
		}

		// return new location
		return new Location(world, x, y, z, yaw, pitch);
	}


	/**
	 * Getter for worldName
	 * @return String - worldName
	 */
	public final String getWorldName() {
		return worldName;
	}


	/**
	 * Getter for worldUid
	 * @return UUID - worldUid
	 */
	public final UUID getWorldUid() {
		return worldUid;
	}


	/**
	 * Getter for x
	 * @return double - x
	 */
	public final double getX() {
		return x;
	}


	/**
	 * Getter for y
	 * @return double - y
	 */
	public final double getY() {
		return y;
	}


	/**
	 * Getter for z
	 * @return double - z
	 */
	public final double getZ() {
		return z;
	}


	/**
	 * Getter for yaw
	 * @return float - yaw
	 */
	public final float getYaw() {
		return yaw;
	}


	/**
	 * Getter for pitch
	 * @return float - pitch
	 */
	public float getPitch() {
		return pitch;
	}


	/**
	 * Getter for safety range (currently unused)
	 *
	 * @return int - safety range
	 */
	final int getSafetyRange() {
		return safetyRange;
	}


	/**
	 * Getter for safety time
	 *
	 * @return int - safety time
	 */
	public final long getSafetyTime() {
		return safetyTime;
	}


	/**
	 * Static method to create search key from graveyard display name;
	 * strips color codes and replaces spaces with underscores;
	 * preserves case
	 *
	 * @param displayName the graveyard display name
	 * @return String - a search key derived from graveyard display name
	 */
	public static String createSearchKey(String displayName) {
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		return ChatColor.stripColor(displayName.replace(' ', '_'));
	}

}
