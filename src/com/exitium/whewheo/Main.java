package com.exitium.whewheo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;

/**
 * Main class for Whewheo. Extends JavaPlugin.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */
public class Main extends JavaPlugin implements PluginMessageListener{

	//Plugin's Config File Reference.
	public static FileConfiguration config;
	public static File configFile;
	
	//Plugin's Menu File Reference.
	//(Used to store valid servers and warps)
	public static FileConfiguration menuConfig;
	public static File menuFile;
	
	public static String serverName;
	
	public static Main instance;
	
	/** Runs on server startup*/
	@Override
	public void onEnable() {
		instance = this;
		
		//Initiates the config variables and copies the file if no exist.
		initiateConfigFiles();
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		//Instantiates a new Config Loader to store information from the config
		ConfigLoader loader = new ConfigLoader();
		
		Bukkit.getPluginManager().registerEvents(new ServerSelectionHandler(), this);
		
		//Sets command "/ww"'s executor to Commands.
		getCommand("ww").setExecutor(new Commands());
		
		

		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.instance, "BungeeCord");
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Main.instance, "BungeeCord", Main.instance);
		
		
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
			
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer");
			
			Bukkit.getServer().getOnlinePlayers().iterator().next().sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
		}
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

	/** Attempts to save the config. If unsuccessful, it prints an error message.*/
	public static void saveConfigFile() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Error trying to save config. Contact Developer.");
			e.printStackTrace();
		}
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
	
	public static void centeredTP(Player player, Location loc) {
		Location reference = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		reference.setPitch(player.getLocation().getPitch());
		reference.setYaw(player.getLocation().getYaw());
		player.teleport(reference.add(0.5, 0.0, 0.5));
	}
	
	public static void sendToSever(Player player, String targetServer) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(targetServer);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		player.sendPluginMessage(instance, "BungeeCord", b.toByteArray());
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.messaging.PluginMessageListener#onPluginMessageReceived(java.lang.String, org.bukkit.entity.Player, byte[])
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		Bukkit.broadcastMessage("RECIEVED INFORMATION!");
		// TODO Auto-generated method stub
		if (!channel.equals("BungeeCord")) {
			return;
		}
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("GetServer")) {
			Bukkit.getServer().getLogger().info("Got Name");
			String servername = in.readUTF();
			
			serverName = servername;
		} else if (subchannel.equals("PlayerCount")) {
			Bukkit.getServer().broadcastMessage("Got Player Count");
			
			String serverName = in.readUTF(); // Name of server, as given in the arguments
			int playerCount = in.readInt();
	
			Bukkit.broadcastMessage("ServerName: " + serverName);
			Bukkit.broadcastMessage("PlayerCount: " + playerCount);
			
			ServerTP server = ServerSelectionHandler.getServerFromName(serverName);
			
			ItemStack serverItem = ServerSelectionHandler.getServerItemFromName(serverName);
//			
			ItemMeta serverItemMeta = serverItem.getItemMeta();
			if (server.getName().contains("&")) {
				serverItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', server.getName()));
			}else{
				serverItemMeta.setDisplayName(server.getName());
			}
			
//			TODO: Implement Place Holders: %count%
			
			serverItemMeta.setLore(server.getLore());
			List<String> lore = new ArrayList<String>();
			for (String l : serverItemMeta.getLore()) {
				if (l.contains("&")) {
					lore.add(ChatColor.translateAlternateColorCodes('&', l).replace("%count%", playerCount + ""));
					Bukkit.broadcastMessage("Changed to" + ChatColor.translateAlternateColorCodes('&', l).replace("%count%", playerCount + ""));
				}else{
					lore.add(l.replace("%count%", playerCount + ""));
					Bukkit.broadcastMessage("Changed to "  + l.replace("%count%", playerCount + ""));
				}
			}
//			
//			serverItemMeta.setLore(lore);
//			
//			
//			
//			
			if (!lore.equals(Arrays.asList(""))) {
				serverItemMeta.setLore(lore);
			}
//			
			if (server.getEnchantment() != null)
			serverItemMeta.addEnchant(server.getEnchantment(), 1, false);
			serverItem.setItemMeta(serverItemMeta);
			
			
			
			ServerSelectionHandler.serverItems.put(serverItem, server);
			
			ServerSelectionHandler.servers.setItem(server.getSlot(), serverItem);
			
		}
	}
}