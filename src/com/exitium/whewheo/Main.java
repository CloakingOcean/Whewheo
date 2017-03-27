package com.exitium.whewheo;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

/**
 * Main class for Whewheo. Extends JavaPlugin.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */
public class Main extends JavaPlugin{

	//Plugin's Config File Reference.
	public static FileConfiguration config;
	public static File configFile;
	
	//Plugin's Menu File Reference.
	//(Used to store valid servers and warps)
	public static FileConfiguration menuConfig;
	public static File menuFile;
	
	/** Runs on server startup*/
	@Override
	public void onEnable() {
		//Initiates the config variables and copies the file if no exist.
		initiateConfigFiles();
		
		//Instantiates a new Config Loader to store information from the config
		ConfigLoader loader = new ConfigLoader();
		
		//Sets command "/ww"'s executor to Commands.
		getCommand("ww").setExecutor(new Commands());
	}
	
	/** Initiates the config variables and copies the file if no exist.*/
	public void initiateConfigFiles() {
		//Initiate and copy config file.
		configFile = new File(getDataFolder(), "config.yml");
		config = getConfig();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		//Initiate and copy menu file.
		menuFile = new File(getDataFolder(), "menu.yml");
		menuConfig = YamlConfiguration.loadConfiguration(menuFile);
		saveMenuConfig();
	}
	
	/** Attempts to save the menu configuration to the menu file. If unsuccessful, it prints an error message.*/
	public static void saveMenuConfig() {
		try {
			menuConfig.save(menuFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Error trying to save xp config. Contact Developer.");
			e.printStackTrace();
		}
	}
	
	/** Runs on server shut down.*/
	@Override
	public void onDisable() {
		
	}
}