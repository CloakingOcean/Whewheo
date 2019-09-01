package com.exitium.whewheo.util;

import com.exitium.whewheo.Main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public final class Util {

    private static String superPerm = "whewheo.";
	public static Permission createWarp = new Permission(superPerm + "createwarp");
	public static Permission enableWarp = new Permission(superPerm + "enablewarp");
	public static Permission reload = new Permission(superPerm + "reload");
	public static Permission open = new Permission(superPerm + "open");

    private Util() {}

	/** Teleports a player to the center of a target location. */
	public static void centeredTP(Player player, Location loc) {

		// By default, a teleport sends a player to the 0.0, 0.0 corner of a block, so
		// we add half a block (0.5) on each side to center it.
		loc = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getY(), loc.getBlockZ() + 0.5);

		// Maintains where the player is looking.
		loc.setPitch(player.getLocation().getPitch());
		loc.setYaw(player.getLocation().getYaw());

		// Teleports the player
		player.teleport(loc);
	}

    /**
	 * Serializes a location into a string for easy storage in a config file.
	 * 
	 * @param l The location to serialize
	 * @return The string version of the location
	 */
	public static String serializeLocation(Location l) {
		String worldName = l.getWorld().getName();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();

		return worldName + ":" + x + ":" + y + ":" + z;
	}

	/**
	 * Deserializes a string into a location.
	 * 
	 * @param l The location to serialize
	 * @return The string version of the location
	 */
	public static Location deserializeLocation(String s) {
		String[] splitter = s.split(":");
		if (splitter.length == 4) { // worldname:x:y:z <- 4 segments.
			String worldName = splitter[0];
			int x = 0;
			int y = 0;
			int z = 0;
			try {
				// Attempt to parse given strings to integers.
				x = Integer.parseInt(splitter[1]);
				y = Integer.parseInt(splitter[2]);
				z = Integer.parseInt(splitter[3]);
			} catch (Exception e) {
				Bukkit.getServer().getLogger().severe(
						"Error while trying to deserializeLocation! Invalid Integer. String to deserialize: " + s);
				return null;
			}

			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				Bukkit.getServer().getLogger().severe("Couldn't find world: " + worldName + "!");
			} else {
				return new Location(world, x, y, z);
			}

		} else {
			Bukkit.getServer().getLogger().severe(
					"Error while trying to deserializeLocation! Not enough information to deserialize. String to deserialize: "
							+ s);
		}

		return null;
	}
}