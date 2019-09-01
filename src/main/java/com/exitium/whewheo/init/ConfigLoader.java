package com.exitium.whewheo.init;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.particles.send.ValidSendGenerators;
import com.exitium.whewheo.teleportobjects.WarpTP;
import com.exitium.whewheo.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Loads information from the config and menu configs into local lists.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */

public class ConfigLoader {

	// Plugin's Config File Reference.
	private FileConfiguration config;
	private File configFile;

	// Plugin's Menu File Reference.
	// (Used to store valid servers and warps)
	private FileConfiguration menuConfig;
	private  File menuFile;

	private FileConfiguration sentPlayersConfig;
	private File sentPlayersFile;

	// Enabled And Loaded Warps
	private HashMap<String, WarpTP> warps;
	private String prefix = ChatColor.AQUA + "[" + ChatColor.BLUE + "Whewheo" + ChatColor.AQUA + "]" + ChatColor.WHITE;

	private Main main;

	public ConfigLoader(Main main) {
		this.main = main;
		init();
	}

	/** Initializes all of the components of the Config Loader class */
	public void init() {
		initiateConfigFiles();

		loadPrefix();
		resetSendGeneratorDelay();

		warps = new HashMap<String, WarpTP>();

		writeParticleGeneratorHelpFile();
		loadWarps();
	}

	private void loadPrefix() {
		if (!config.contains("general")) {
			Bukkit.getServer().getLogger().severe("Couldn't find \"general\" in config");
			return;
		}

		if (!config.getConfigurationSection("general").contains("prefix")) {
			Bukkit.getServer().getLogger().severe("Couldn't find \"prefix\" in general");
			return;
		}
		
		prefix = ChatColor.translateAlternateColorCodes('&', config.getString("general.prefix")) + " ";
	}

	/** Initiates the config variables and copies the file if no exist. */
	private void initiateConfigFiles() {
		// Initiate and copy config file.
		configFile = new File(main.getDataFolder(), "config.yml");
		config = getConfig();

		main.getConfig().options().copyDefaults(true);
		main.saveConfig();

		// Initiate and copy menu file.

		menuFile = new File(main.getDataFolder(), "menu.yml");

		if (!menuFile.exists()) {
			main.saveResource("menu.yml", false);
		}

		menuConfig = YamlConfiguration.loadConfiguration(menuFile);

		menuConfig.options().copyDefaults(true);
		saveMenuConfig();

		Bukkit.getServer().getLogger().info("BungeeCord Folder: " + (config.getString("general.bungeecordFolder")));
		sentPlayersFile = new File(config.getString("general.bungeecordFolder"), "sentplayers.yml");
		Bukkit.getServer().getLogger().info("Sent Players File Path: " + sentPlayersFile.getPath());

		sentPlayersConfig = YamlConfiguration.loadConfiguration(sentPlayersFile);
	}

	/** Attempts to save the config. If unsuccessful, it prints an error message. */
	public void saveConfigFile() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Error trying to save config. Contact Developer.");
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to save the menu configuration to the menu file. If unsuccessful, it
	 * prints an error message.
	 */
	public void saveMenuConfig() {
		try {
			menuConfig.save(menuFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Error trying to save xp config. Contact Developer.");
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to save the sent players configuration to the menu file. If
	 * unsuccessful, it prints an error message.
	 */
	public void saveSentPlayersConfig() {
		try {
			sentPlayersConfig.save(sentPlayersFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Error trying to save xp config. Contact Developer.");
			e.printStackTrace();
		}
	}

	/**
	 * Resets the SendGenerator's Delay variable to the specified value in the
	 * config.
	 */
	private void resetSendGeneratorDelay() {
		if (config.contains("general")) {
			if (config.getConfigurationSection("general").contains("teleportDelay")) {
				// Configuration Check

				SendParticleGenerator.delay = config.getInt("general.teleportDelay");

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
	private void loadWarps() {
		if (menuConfig.contains("warps")) {
			for (String key : menuConfig.getConfigurationSection("warps").getKeys(false)) {
				loadWarp(key);
			}
		}
	}

	/**
	 * Loads a single warp
	 * 
	 * @param key warpName to load form
	 */
	public void loadWarp(String key) {


		if (!warpHasRequirements("warps." + key)) {
			Bukkit.getServer().getLogger()
					.severe("Couldn't load warp " + key + ". Doesn't contain all required fields. Skipping...");
		}
		
		ConfigurationSection section = menuConfig.getConfigurationSection("warps." + key);

		WarpTP warp = new WarpTP(this);

		if (warp.load(section, key)) {
			warps.put(warp.getName(), warp);
			Bukkit.getLogger().info("Successfully loaded warp at id " + key + " named " + warp.getName() + "!");
		} else {
			Bukkit.getLogger().severe("Was unable to load warp at id " + key + " named " + warp.getName() + "!");
		}
	}

	/**
	 * Gets the next available warp Id
	 * 
	 * @return next available warp id in an int form.
	 */
	private int getNextWarpId() {
		if (menuConfig.contains("warps")) {
			int nextId = 1;
			while (menuConfig.getConfigurationSection("warps").contains(nextId + "")) {
				nextId++;
			}
			return nextId;
		} else {
			return 1;
		}
	}

	public void saveDefaultWarp(String warpName, Player player) {
		int nextIndex = getNextWarpId();

		menuConfig.set("warps." + nextIndex + ".name", warpName);
		menuConfig.set("warps." + nextIndex + ".location",
				main.getServerName() + ":" + Util.serializeLocation(player.getLocation()));
		menuConfig.set("warps." + nextIndex + ".enabled", false);

		// Create Place Holders
		menuConfig.set("warps." + nextIndex + ".slot", -1);
		menuConfig.set("warps." + nextIndex + ".material", "341:0");
		menuConfig.set("warps." + nextIndex + ".enchantment", "null");
		menuConfig.set("warps." + nextIndex + ".quantity", 1);
		menuConfig.set("warps." + nextIndex + ".lore", Arrays.asList(""));
		menuConfig.set("warps." + nextIndex + ".enableCommands", true);
		menuConfig.set("warps." + nextIndex + ".commands", Arrays.asList(""));
		menuConfig.set("warps." + nextIndex + ".sendEffect", "SPIRAL");
		menuConfig.set("warps." + nextIndex + ".receiveEffect", "EMERALD");
		saveMenuConfig();
	}

	/**
	 * Determine if any of the loaded warps match the specified name.
	 * 
	 * @param warpName Name of target warp
	 * @return Whether or not a warp with that name is enabled.
	 */
	public boolean containsWarpName(String warpName) {
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
	public boolean warpHasRequirements(String path) {
		ConfigurationSection s = menuConfig.getConfigurationSection(path);

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
	public void addWarp(String warpName) {
		loadWarp(warpName);
	}

	/**
	 * Returns a string translated with color codes for the specified option
	 * 
	 * @param path Configuration Path for wanted Option
	 * @return Specified Option with ChatColors
	 */
	public String getColoredTextFromConfig(String path) {
		if (path == null) {
			return null;
		}

		if (!config.getString(path).contains("&")) {
			return config.getString(path);
		}
		
		return ChatColor.translateAlternateColorCodes('&', config.getString(path));
	}

	/**
	 * Returns a string translated with color codes for the specified option
	 * 
	 * @param path Configuration Path for wanted Option
	 * @return Specified Option with ChatColors
	 */
	public String getColoredTextFromMenu(String path) {
		if (path != null) {
			if (menuConfig.getString(path).contains("&")) {
				return ChatColor.translateAlternateColorCodes('&', menuConfig.getString(path));
			} else {
				return menuConfig.getString(path);
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

	/** Gets a specified message from the config */
	public String msg(String message) {
		if (!this.config.contains("messages")) {
			Bukkit.getLogger().severe("Couldn't find \"messages\" in config");
			return "";
		}
		
		if (!this.config.getConfigurationSection("messages").contains(message)) {
			Bukkit.getLogger().severe("Couldn't find \"" + message + "\" in messages");
		}


		return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages." + message));
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public void reload() {
		config = YamlConfiguration.loadConfiguration(configFile);
		menuConfig = YamlConfiguration.loadConfiguration(menuFile);
		saveConfigFile();
		saveMenuConfig();

		init();
	}

	public FileConfiguration getMenuConfig() {
		return this.menuConfig;
	}

	public FileConfiguration getSentPlayersConfig() {
		return this.sentPlayersConfig;
	}

	public HashMap<String, WarpTP> getWarps() {
		return this.warps;
	}

	public String getPrefix() {
		return this.prefix;
	}
}