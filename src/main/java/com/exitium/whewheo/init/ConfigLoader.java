package com.exitium.whewheo.init;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.particles.send.ValidSendGenerators;
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * Loads information from the config and menu configs into local lists.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */

public class ConfigLoader {

	// Enabled And Loaded Warps
	public static HashMap<String, WarpTP> warps;

	public ConfigLoader() {
		init();
	}

	/** Initializes all of the components of the Config Loader class */
	public static void init() {
		resetSendGeneratorDelay();

		warps = new HashMap<String, WarpTP>();

		writeParticleGeneratorHelpFile();
		loadWarps();
	}

	/**
	 * Resets the SendGenerator's Delay variable to the specified value in the
	 * config.
	 */
	private static void resetSendGeneratorDelay() {
		if (Main.config.contains("general")) {
			if (Main.config.getConfigurationSection("general").contains("teleportDelay")) {
				// Configuration Check

				SendParticleGenerator.delay = Main.config.getInt("general.teleportDelay");

			} else {
				Bukkit.getServer().getLogger().severe(
						"Couldn't load SendParticleGeneartors because there is no \"teleportDelay\" section in config.");
				SendParticleGenerator.delay = -1;
			}
		} else {
			Bukkit.getServer().getLogger()
					.severe("Couldn't load SendParticleGeneartors because there is no \"general\" section in config.");
			SendParticleGenerator.delay = -1;
		}
	}

	/**
	 * Loops through all of the keys under "warps" and attempts to load them into
	 * the plugin.
	 */
	private static void loadWarps() {
		if (Main.menuConfig.contains("warps")) {
			for (String key : Main.menuConfig.getConfigurationSection("warps").getKeys(false)) {
				loadWarp(key);
			}
		}
	}

	/**
	 * Loads a single warp
	 * 
	 * @param key warpName to load form
	 */
	public static void loadWarp(String key) {
		if (warpHasRequirements("warps." + key)) {

			ConfigurationSection section = Main.menuConfig.getConfigurationSection("warps." + key);

			boolean enabled = section.getBoolean("enabled");

			if (enabled) {

				int slot = section.getInt("slot");
				if (slot != -1) {

					String name = ConfigLoader.getColoredTextFromMenu("warps." + key + ".name");

					String locationText = section.getString("location");

					String[] splitter = locationText.split(":");

					/*
					 * Possible Values:
					 * 
					 * location: <server>:<world>:<x>:<y>:<z> args.length = 5
					 * 
					 * location: <server>:<world> args.length = 2
					 * 
					 * location: <server> args.length = 1
					 */

					String serverName = "";
					Location location = null;

					if (splitter.length == 5) {
						// Send to server with specific location
						serverName = splitter[0];

						String worldName = splitter[1];

						int x, y, z;

						try {
							x = Integer.parseInt(splitter[2]);
							y = Integer.parseInt(splitter[3]);
							z = Integer.parseInt(splitter[4]);
						} catch (NumberFormatException e) {
							Bukkit.getServer().getLogger().severe(
									"Couldn't load warp: " + key + ". Invalid location coordinates. Invalid Integers.");
							return;
						}

						World world = Bukkit.getServer().getWorld(worldName);
						if (world == null) {
							Bukkit.getServer().getLogger()
									.severe("Couldn't load warp: " + key + ". Couldn't find world : " + worldName
											+ "! Please edit location configuration for warp " + key + "!");
							return;
						} else {
							location = new Location(world, x, y, z);
						}

					} else if (splitter.length == 2) {
						// Send to server at default spawn location of world
						serverName = splitter[0];

						String worldName = splitter[1];

						World world = Bukkit.getServer().getWorld(worldName);
						if (world == null) {
							Bukkit.getServer().getLogger()
									.severe("Couldn't load warp: " + key + ". Couldn't find world : " + worldName
											+ "! Please edit location configuration for warp " + key + "!");
							return;
						} else {
							location = world.getSpawnLocation();
						}

					} else if (splitter.length == 1) {
						// Send to server with no extra teleportation
						serverName = splitter[0];
					}
					String material = section.getString("material");
					String enchantment = section.getString("enchantment");
					int quantity = section.getInt("quantity");
					List<String> lore = section.getStringList("lore");

					boolean enableCommands = section.getBoolean("enableCommands");
					List<String> commands = section.getStringList("commands");

					int id = 0;

					try {
						id = Integer.parseInt(key);
					} catch (NumberFormatException e) {
						Bukkit.getServer().getLogger().severe("Invalid id: " + key + ". Skipping..");
						return;
					}

					Enchantment realEnchantment = null;

					if (enchantment.equals("null")) {
						realEnchantment = null;
					} else if (Enchantment.getByName(enchantment) == null) {
						Bukkit.getServer().getLogger().severe("No enchantment by the name of: " + enchantment + "!");
						return;
					} else {
						realEnchantment = Enchantment.getByName(enchantment);
					}

					String sendGeneratorName = section.getString("sendEffect");
					ValidSendGenerators validSendGenerator = null;
					try {
						validSendGenerator = ValidSendGenerators.valueOf(sendGeneratorName.toUpperCase());
					} catch (Exception e) {
						Bukkit.getLogger().severe("Couldn't load warp " + key
								+ ". Invalid Send Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
						return;
					}

					String receiveGeneratorName = section.getString("receiveEffect");
					ValidReceiveGenerators validReceiveGenerator = null;

					try {
						validReceiveGenerator = ValidReceiveGenerators.valueOf(receiveGeneratorName.toUpperCase());
					} catch (Exception e) {
						Bukkit.getLogger().severe("Couldn't load warp " + key
								+ ". Invalid Receive Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
						return;
					}

					WarpTP warp = new WarpTP(id, name, serverName, location, slot, material, realEnchantment, quantity,
							lore, enableCommands, commands, validSendGenerator, validReceiveGenerator);

					warps.put(name, warp);

				} else {
					Bukkit.getServer().getLogger().info("Skipping warp " + key
							+ " because it's slot has not been updated in the menu.yml. Please update it to use the warp.");
				}

			} else {
				Bukkit.getServer().getLogger().info("Skipping warp " + key + " because it's not enabled");
			}
		} else {
			Bukkit.getServer().getLogger()
					.severe("Couldn't load warp " + key + ". Doesn't contain all required fields. Skipping...");
		}
	}

	/**
	 * Gets the next available warp Id
	 * 
	 * @return next available warp id in an int form.
	 */
	public static int getNextWarpId() {
		if (Main.menuConfig.contains("warps")) {
			int nextId = 1;
			while (Main.menuConfig.getConfigurationSection("warps").contains(nextId + "")) {
				nextId++;
			}
			return nextId;
		} else {
			return 1;
		}
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

	/**
	 * Determine if any of the loaded warps match the specified name.
	 * 
	 * @param warpName Name of target warp
	 * @return Whether or not a warp with that name is enabled.
	 */
	public static boolean containsWarpName(String warpName) {
		if (warps != null) {
			for (WarpTP w : warps.values()) {
				if (w.getName().equals(warpName)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/** @param path The path to the number configuration: Ex: "warps.1" */
	public static boolean warpHasRequirements(String path) {
		ConfigurationSection s = Main.menuConfig.getConfigurationSection(path);

		if (s.contains("name") && s.contains("location") && s.contains("enabled") && s.contains("slot")
				&& s.contains("material") && s.contains("enchantment") && s.contains("quantity") && s.contains("lore")
				&& s.contains("enableCommands") && s.contains("commands") && s.contains("sendEffect")
				&& s.contains("receiveEffect")) {
			return true;
		} else {
			return false;
		}
	}

	/** @param path The path to the number configuration: Ex: "warps.1" */
	public static void addWarp(String warpName) {
		loadWarp(warpName);
	}

	/**
	 * Returns a string translated with color codes for the specified option
	 * 
	 * @param path Configuration Path for wanted Option
	 * @return Specified Option with ChatColors
	 */
	public static String getColoredTextFromConfig(String path) {
		if (path != null) {
			if (Main.config.getString(path).contains("&")) {
				return ChatColor.translateAlternateColorCodes('&', Main.config.getString(path));
			} else {
				return Main.config.getString(path);
			}
		}
		return null;
	}

	/**
	 * Returns a string translated with color codes for the specified option
	 * 
	 * @param path Configuration Path for wanted Option
	 * @return Specified Option with ChatColors
	 */
	public static String getColoredTextFromMenu(String path) {
		if (path != null) {
			if (Main.menuConfig.getString(path).contains("&")) {
				return ChatColor.translateAlternateColorCodes('&', Main.menuConfig.getString(path));
			} else {
				return Main.menuConfig.getString(path);
			}
		}
		return null;
	}

	/**
	 * Gets an item stack from a material and quantity. Id is optional
	 * 
	 * @param materialAndId Material and optionally id
	 * @param quantity      The amount of items in the stack
	 * @return An ItemStack from the given material, id, and quantity
	 */
	public static ItemStack getItemStackFromId(String materialAndId, int quantity) {
		int materialId = 0;
		int data = 0;
		if (materialAndId.contains(":")) {
			String[] splitter = materialAndId.split(":");
			try {
				materialId = Integer.parseInt(splitter[0]);
				data = Integer.parseInt(splitter[1]);
			} catch (NumberFormatException e) {
				return null;
			}

			@SuppressWarnings("deprecation")
			ItemStack item = new ItemStack(materialId, quantity, (short) data);

			return item;
		} else {
			try {
				materialId = Integer.parseInt(materialAndId);
			} catch (NumberFormatException e) {
				return null;
			}

			@SuppressWarnings("deprecation")
			ItemStack item = new ItemStack(materialId, quantity);

			return item;
		}

	}

	/**
	 * Generates a TextFile in the Plugin's Directory to help users know the valid
	 * generator names
	 */
	public static void writeParticleGeneratorHelpFile() {
		try {
			File dataFolder = Main.instance.getDataFolder();
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}

			File helpFile = new File(Main.instance.getDataFolder(), "generatorhelp.txt");
			if (!helpFile.exists()) {
				helpFile.createNewFile();
			}

			FileWriter fw = new FileWriter(helpFile);

			String toWrite = "[VALID GENERATOR NAMES]\n"
					+ "*Please note that the names aren't case-sensitive, but they must have all punctuation if there is any.\n"
					+ "\n" + "Valid Send Generator Types:\n" + "\n";

			for (ValidSendGenerators v : ValidSendGenerators.values()) {
				toWrite += "    \"" + v.name() + "\": " + v.getDescription() + "\n" + "\n";
			}

			toWrite += "Valid Receive Generator Types:\n" + "\n";

			for (ValidReceiveGenerators v : ValidReceiveGenerators.values()) {
				toWrite += "    \"" + v.name() + "\": " + v.getDescription() + "\n" + "\n";
			}

			fw.write(toWrite);

			fw.flush();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}