package com.exitium.whewheo;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads information from the config and menu configs into local lists.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */
public class ConfigLoader {
	
	public static HashMap<String, ServerTP> servers;
	public static HashMap<String, WarpTP> warps;
	
	public ConfigLoader() {
//		servers = new HashMap<String, ServerTP>();
		warps = new HashMap<String, WarpTP>();
		
//		loadServer();
		loadWarps();
	}
	
	public void loadServer() {
		
	}
	
	public static void loadWarps() {
		//MAKE SURE TO CHECK IF THE WARP IS ENABLED
		//ALSO TO MAKE SURE IT HAS ALL THE REQUIREMENTS ANYWAYS
		
		Bukkit.broadcastMessage("Loading Warps");
		
		if (Main.menuConfig.contains("warps")) {
			Bukkit.broadcastMessage("Iterating Through Warps");
			for (String key : Main.menuConfig.getConfigurationSection("warps").getKeys(false)) {
				Bukkit.broadcastMessage("Current Key: " + key);
				if (Main.menuConfig.getConfigurationSection("warps." + key).contains("enabled")) {
					Bukkit.broadcastMessage("Enabled");
					if (Main.menuConfig.getBoolean("warps." + key + ".enabled")) {
						
						if (hasRequirements("warps." + key)) {
							
							ConfigurationSection section = Main.menuConfig.getConfigurationSection("warps." + key);
							
							if (deserializeLocation(section.getString("location")) == null) {
								Bukkit.getServer().getLogger().severe("No location set for warp: " + key);
								continue;
							}
							
							int id = 0;
							try {
								id = Integer.parseInt(key);
							}catch (NumberFormatException e) {
								Bukkit.getServer().getLogger().severe("Invalid Id! Not an integer! Current ID: " + key);
								continue;
							}
							
							String name = section.getString("name");
							String server = section.getString("server");
							Location location = deserializeLocation(section.getString("location"));
							int slot = section.getInt("slot");
							String material = section.getString("material");
							String enchantment = section.getString("enchantment");
							int quantity = section.getInt("quantity");
							List<String> lore = section.getStringList("lore");
							boolean enableCommands = section.getBoolean("enableCommands");
							List<String> commands = section.getStringList("commands");
							
							WarpTP warp = new WarpTP(id, name, server, location, slot, material, quantity, lore, enableCommands, commands);
							
							warps.put(name, warp);
							
							Bukkit.getLogger().info("Warp Location Loaded! ID: " + id);
							Bukkit.broadcastMessage("Warp Location Loaded! ID: " + id);
						}else{
							Bukkit.getServer().getLogger().severe("Couldn't load enabled warp " + key + ". Doesn't contain all required fields. Skipping...");
						}
					}
				}
			}
		}
	}
	
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
	public static boolean hasRequirements(String path) {
		ConfigurationSection s = Main.menuConfig.getConfigurationSection(path);
		
		if (s.contains("name") &&
				s.contains("server") &&
				s.contains("location") &&
				s.contains("enabled") &&
				s.contains("slot") &&
				s.contains("material") &&
				s.contains("enchantment") &&
				s.contains("quantity") &&
				s.contains("lore") &&
				s.contains("enableCommands") &&
				s.contains("commands")) {
			return true;
		}else{
			return false;
		}
	}
	
	/** @param path The path to the number configuration: Ex: "warps.1"*/
	public static void addWarp(String path) {
		
	}
}