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
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * Loads information from the config and menu configs into local lists.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */

public class ConfigLoader {
	
	//Enabled And Loaded Warps
	public static HashMap<String, ServerTP> servers;
	public static HashMap<String, WarpTP> warps;
	
	public ConfigLoader() {
		init();
	}
	
	/** Initializes all of the components of the Config Loader class*/
	public static void init() {
		resetSendGeneratorDelay();
		
		servers = new HashMap<String, ServerTP>();
		warps = new HashMap<String, WarpTP>();
		
		writeParticleGeneratorHelpFile();
		loadServers();
		loadWarps();
	}
	
	/**
	 * Resets the SendGenerator's Delay variable to the specified value in the config.
	 */
	private static void resetSendGeneratorDelay() {
		if (Main.config.contains("general")) {
			if (Main.config.getConfigurationSection("general").contains("teleportDelay")) {
				//Configuration Check
				
				SendParticleGenerator.delay = Main.config.getInt("general.teleportDelay");
				
				
			}else{
				Bukkit.getServer().getLogger().severe("Couldn't load SendParticleGeneartors because there is no \"teleportDelay\" section in config.");
				SendParticleGenerator.delay = -1;
			}
		}else{
			Bukkit.getServer().getLogger().severe("Couldn't load SendParticleGeneartors because there is no \"general\" section in config.");
			SendParticleGenerator.delay = -1;
		}
	}
	
	/**
	 * Loops through all of the keys under "servers" and attempts to load them into the plugin.
	 */
	private static void loadServers() {
		if (Main.menuConfig.contains("servers")) {
			
			for (String key : Main.menuConfig.getConfigurationSection("servers").getKeys(false)) {
				if (serverHasRequirements("servers." + key)) {
					
					ConfigurationSection section = Main.menuConfig.getConfigurationSection("servers." + key);
					
					String name = ConfigLoader.getColoredTextFromMenu("servers." + key + ".name");
					String serverName = section.getString("server");
					int slot = section.getInt("slot");
					String material = section.getString("material");
					String enchantment = section.getString("enchantment");
					int quantity = section.getInt("quantity");
					List<String> lore = section.getStringList("lore");
					
					boolean enableCommands = section.getBoolean("enableCommands");
					List<String> commands = section.getStringList("commands");
					
					int id = 0;
					
					try {
						id = Integer.parseInt(key);
					}catch(NumberFormatException e) {
						Bukkit.getServer().getLogger().severe("Invalid id: " + key + ". Skipping..");
						continue;
					}
					
					Enchantment realEnchantment = null;
					
					if (enchantment.equals("null")) {
						realEnchantment = null;
					}else if (Enchantment.getByName(enchantment) == null) {
						Bukkit.getServer().getLogger().severe("No enchantment by the name of: " + enchantment + "!");
						continue;
					}else{
						realEnchantment = Enchantment.getByName(enchantment);
					}
					
					String sendGeneratorName = section.getString("sendEffect");
					ValidSendGenerators validSendGenerator = null;
					try {
						validSendGenerator = ValidSendGenerators.valueOf(sendGeneratorName.toUpperCase());
					}catch (Exception e) {
						Bukkit.getLogger().severe("Couldn't load server " + key + ". Invalid Send Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
						continue;
					}
					
					String receiveGeneratorName = section.getString("receiveEffect");
					ValidReceiveGenerators validReceiveGenerator = null;
					
					try {
						validReceiveGenerator = ValidReceiveGenerators.valueOf(receiveGeneratorName.toUpperCase());
					}catch (Exception e) {
						Bukkit.getLogger().severe("Couldn't load server " + key + ". Invalid Receive Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
						continue;
					}
					
					ServerTP server = new ServerTP(id, name, serverName, slot, material, realEnchantment, quantity, lore, enableCommands, commands, validSendGenerator, validReceiveGenerator);
					
					servers.put(name, server);
					
				}else{
					Bukkit.getServer().getLogger().severe("Couldn't load server " + key + ". Doesn't contain all required fields. Skipping...");
				}
			}
			
		}
	}
	
	/**
	 * Loops through all of the keys under "warps" and attempts to load them into the plugin.
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
	 * @param key warpName to load form
	 */
	public static void loadWarp(String key) {

		if (Main.menuConfig.getConfigurationSection("warps." + key).contains("enabled")) {
			if (Main.menuConfig.getBoolean("warps." + key + ".enabled")) {
				
				if (warpHasRequirements("warps." + key)) {
					
					ConfigurationSection section = Main.menuConfig.getConfigurationSection("warps." + key);
					
					if (deserializeLocation(section.getString("location")) == null) {
						Bukkit.getServer().getLogger().severe("No location set for warp: " + key);
						return;
					}
					
					int id = 0;
					try {
						id = Integer.parseInt(key);
					}catch (NumberFormatException e) {
						Bukkit.getServer().getLogger().severe("Invalid Id! Not an integer! Current ID: " + key);
						return;
					}
					
					String path = "warps." + key + ".name";
					
					
					String name = ConfigLoader.getColoredTextFromMenu(path);
					Location location = deserializeLocation(section.getString("location"));
					int slot = section.getInt("slot");
					String material = section.getString("material");
					String enchantment = section.getString("enchantment");
					int quantity = section.getInt("quantity");
					List<String> lore = section.getStringList("lore");
					boolean enableCommands = section.getBoolean("enableCommands");
					List<String> commands = section.getStringList("commands");
					
					Enchantment realEnchantment;
					
					if (enchantment.equals("null")) {
						realEnchantment = null;
					}else if (Enchantment.getByName(enchantment) == null) {
						Bukkit.getServer().getLogger().severe("No enchantment by the name of: " + enchantment + "!");
						return;
					}else{
						realEnchantment = Enchantment.getByName(enchantment);
					}
					
					String sendGeneratorName = section.getString("sendEffect");
					ValidSendGenerators validSendGenerator = null;
					try {
						validSendGenerator = ValidSendGenerators.valueOf(sendGeneratorName);
					}catch (Exception e) {
						Bukkit.getLogger().severe("Couldn't load warp " + key + ". Invalid Send Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
						return;
					}
					
					String receiveGeneratorName = section.getString("receiveEffect");
					ValidReceiveGenerators validReceiveGenerator = null;
					
					try {
						validReceiveGenerator = ValidReceiveGenerators.valueOf(receiveGeneratorName);
					}catch (Exception e) {
						Bukkit.getLogger().severe("Couldn't load warp " + key + ". Invalid Receive Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
						return;
					}
					
					
					WarpTP warp = new WarpTP(id, name, location, slot, material, realEnchantment, quantity, lore, enableCommands, commands, validSendGenerator, validReceiveGenerator);
					
					warps.put(name, warp);
					
					Bukkit.getLogger().info("Warp Location Loaded! ID: " + id);
				}else{
					Bukkit.getServer().getLogger().severe("Couldn't load enabled warp " + key + ". Doesn't contain all required fields. Skipping...");
				}
			}
		}
	
	}
	
	/**
	 * Gets the next available server Id
	 * @return next available server id in an int form.
	 */
	public static int getNextServerId() {
		if (Main.menuConfig.contains("servers")) {
			int nextId = 1;
			while (Main.menuConfig.getConfigurationSection("servers").contains(nextId + "")) {
				nextId++;
			}
			return nextId;
		}else{
			return 1;
		}
	}
	
	/**
	 * Gets the next available warp Id
	 * @return next available warp id in an int form.
	 */
	public static int getNextWarpId() {
		if (Main.menuConfig.contains("warps")) {
			int nextId = 1;
			while (Main.menuConfig.getConfigurationSection("warps").contains(nextId + "")) {
				nextId++;
			}
			return nextId;
		}else{
			return 1;
		}
	}
	
	/** 
	 * Serializes a location into a string for easy storage in a config file.
	 * 
	 * @param l The location to serialize
	 * @return The string version of the location
	 * */
	public static String serializeLocation(Location l) {
		String worldName = l.getWorld().getName();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z= l.getBlockZ();
		
		
		return worldName + ":" + x + ":" + y + ":" + z;
	}
	
	/** 
	 * Deserializes a string into a location.
	 * 
	 * @param l The location to serialize
	 * @return The string version of the location
	 * */
	public static Location deserializeLocation(String s) {
		String[] splitter = s.split(":");
		if (splitter.length == 4) { // worldname:x:y:z  <- 4 segments.
			String worldName = splitter[0];
			int x = 0;
			int y = 0;
			int z = 0;
			try {
				//Attempt to parse given strings to integers.
				x = Integer.parseInt(splitter[1]);
				y = Integer.parseInt(splitter[2]);
				z = Integer.parseInt(splitter[3]);
			}catch(Exception e) {
				Bukkit.getServer().getLogger().severe("Error while trying to deserializeLocation! Invalid Integer. String to deserialize: " + s);
				return null;
			}
			
			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				Bukkit.getServer().getLogger().severe("Couldn't find world: " + worldName + "!");
			}else{
				return new Location(world, x, y, z);
			}
			
		}else{
			Bukkit.getServer().getLogger().severe("Error while trying to deserializeLocation! Not enough information to deserialize. String to deserialize: " + s);
		}
		
		return null;
	}
	
	/**
	 * Determine if any of the loaded warps match the specified name.
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
		}else{
			return false;
		}
	}
	
	/** @param path The path to the number configuration: Ex: "warps.1"*/
	public static boolean warpHasRequirements(String path) {
		ConfigurationSection s = Main.menuConfig.getConfigurationSection(path);
		
		if (s.contains("name") &&
				s.contains("location") &&
				s.contains("enabled") &&
				s.contains("slot") &&
				s.contains("material") &&
				s.contains("enchantment") &&
				s.contains("quantity") &&
				s.contains("lore") &&
				s.contains("enableCommands") &&
				s.contains("commands") &&
				s.contains("sendEffect") &&
				s.contains("receiveEffect")) {
			return true;
		}else{
			return false;
		}
	}
	
	/** @param path The path to the number configuration: Ex: "warps.1"*/
	public static boolean serverHasRequirements(String path) {
		ConfigurationSection s = Main.menuConfig.getConfigurationSection(path);
		
		if (s.contains("name") &&
				s.contains("server") &&
				s.contains("slot") &&
				s.contains("material") &&
				s.contains("enchantment") &&
				s.contains("quantity") &&
				s.contains("lore") &&
				s.contains("enableCommands") &&
				s.contains("commands") &&
				s.contains("sendEffect") &&
				s.contains("receiveEffect")) {
			return true;
		}else{
			return false;
		}
	}
	
	/** @param path The path to the number configuration: Ex: "warps.1"*/
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
			}else{
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
			}else{
				return Main.menuConfig.getString(path);
			}
		}
		return null;
	}
	
	/**
	 * Gets an item stack from a material and quantity. Id is optional
	 * @param materialAndId Material and optionally id
	 * @param quantity The amount of items in the stack
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
			}catch(NumberFormatException e) {
				return null;
			}
			
			@SuppressWarnings("deprecation")
			ItemStack item = new ItemStack(materialId, quantity, (short) data);
			
			return item;
		}else{
			try {
				materialId = Integer.parseInt(materialAndId);
			}catch (NumberFormatException e) {
				return null;
			}
			
			@SuppressWarnings("deprecation")
			ItemStack item = new ItemStack(materialId, quantity);
			
			return item;
		}
		
		
	}
	
	/**
	 * Generates a TextFile in the Plugin's Directory to help users know the valid generator names
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
			
			String toWrite = 
			"[VALID GENERATOR NAMES]\n"
			+ "*Please note that the names aren't case-sensitive, but they must have all punctuation if there is any.\n"
			+ "\n"
			+ "Valid Send Generator Types:\n"
			+ "\n";
			
			for (ValidSendGenerators v : ValidSendGenerators.values()) {
				toWrite += 
			  "    \"" + v.name() + "\": " + v.getDescription() + "\n"
			  		+ "\n";
			}
			
			toWrite+=
			  "Valid Receive Generator Types:\n"
			  + "\n";
			
			for (ValidReceiveGenerators v : ValidReceiveGenerators.values()) {
				toWrite += 
			  "    \"" + v.name() + "\": " + v.getDescription() + "\n"
				   + "\n";
			}
			
			fw.write(toWrite);
			
			
			fw.flush();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}