package com.exitium.whewheo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.exitium.whewheo.commands.Commands;
import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.receive.ReceiveParticleGenerator;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.particles.receive.generators.Emerald;
import com.exitium.whewheo.particles.receive.generators.FireExplosion;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.particles.send.ValidSendGenerators;
import com.exitium.whewheo.particles.send.generators.NetherPortal;
import com.exitium.whewheo.particles.send.generators.Spiral;
import com.exitium.whewheo.teleportobjects.WarpTP;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Main class for Whewheo. Extends JavaPlugin.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */
public class Main extends JavaPlugin implements PluginMessageListener {

	// Plugin's Config File Reference.
	public static FileConfiguration config;
	public static File configFile;

	// Plugin's Menu File Reference.
	// (Used to store valid servers and warps)
	public static FileConfiguration menuConfig;
	public static File menuFile;

	private static FileConfiguration sentPlayersConfig;
	private static File sentPlayersFile;

	// Server name returned by Bungeecord's Proxy.
	public static String serverName;

	// Public static instance of the Main class for easier access.
	public static Main instance;

	/*
	 * Permissions
	 */
	private static String superPerm = "whewheo.";
	public static Permission createWarp = new Permission(superPerm + "createwarp");
	public static Permission enableWarp = new Permission(superPerm + "enablewarp");
	public static Permission reload = new Permission(superPerm + "reload");
	public static Permission open = new Permission(superPerm + "open");

	public static String prefix = "";

	private ConfigLoader configLoader;

	/** Runs on server startup. */
	@Override
	public void onEnable() {
		// Sets the public static instance of Main to this instance.
		instance = this;

		// Initiates the config variables and copies the files if none exist.
		initiateConfigFiles();

		if (Main.config.contains("general")) {
			if (Main.config.getConfigurationSection("general").contains("prefix")) {
				prefix = ChatColor.translateAlternateColorCodes('&', Main.config.getString("general.prefix")) + " ";
			} else {
				Bukkit.getServer().getLogger().severe("Couldn't find \"prefix\" in general");
			}
		} else {
			Bukkit.getServer().getLogger().severe("Couldn't find \"general\" in config");
		}

		// Register Outgoing and Incoming Plugin Channel for BungeeCord to request the
		// server name and player count.
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		// Instantiates a new Config Loader to store information from the config
		this.configLoader = new ConfigLoader();

		// Register listeners for ServerSelectionHandler.
		Bukkit.getPluginManager().registerEvents(new ServerSelectionHandler(this.configLoader.getWarps()), this);

		// Sets command "/ww"'s executor to Commands.
		getCommand("ww").setExecutor(new Commands());

		// Attempts to get the server name from the bungeecord proxy if a player is
		// online.
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) { // If a player is online

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer"); // Set channel to GetServer

			Bukkit.getServer().getOnlinePlayers().iterator().next().sendPluginMessage(Main.instance, "BungeeCord",
					out.toByteArray()); // Get an online player to send the Plugin Message through.
		}
	}

	/** Initiates the config variables and copies the file if no exist. */
	public void initiateConfigFiles() {
		// Initiate and copy config file.
		configFile = new File(getDataFolder(), "config.yml");
		config = getConfig();

		getConfig().options().copyDefaults(true);
		saveConfig();

		// Initiate and copy menu file.

		menuFile = new File(getDataFolder(), "menu.yml");

		if (!menuFile.exists()) {
			saveResource("menu.yml", false);
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
	public static void saveConfigFile() {
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
	public static void saveMenuConfig() {
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
	public static void saveSentPlayersConfig() {
		try {
			sentPlayersConfig.save(sentPlayersFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Error trying to save xp config. Contact Developer.");
			e.printStackTrace();
		}
	}

	/** Runs on server shut down. */
	@Override
	public void onDisable() {

	}

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
	 * Attempts to send the player to the target server. Prints a stack trace if
	 * unsuccessful.
	 */
	public static void sendToSever(Player player, String targetServer) {

		// Prepare Output Stream
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		// Attempts to send the request to Bungeecord's proxy.
		try {
			out.writeUTF("Connect");
			out.writeUTF(targetServer);
		} catch (Exception e) {
			// If unsuccessful, prints a stack trace.
			e.printStackTrace();
		}

		// Sends the targetPlayer the plugin message.
		player.sendPluginMessage(instance, "BungeeCord", b.toByteArray());
	}

	/** Receives a message from Bungeecord and handles it appropriately */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("GetServer")) {
			String servername = in.readUTF();

			serverName = servername;
			int playerCount = Bukkit.getServer().getOnlinePlayers().size();

			for (int i = 0; i < ServerSelectionHandler.warps.getContents().length; i++) {
				ItemStack item = ServerSelectionHandler.warps.getItem(i);
				if (item != null) {
					if (ServerSelectionHandler.warpItems.containsKey(item) == true) {

						WarpTP warp = ServerSelectionHandler.warpItems.get(item);

						if (serverName.equals(warp.getServerName())) {

							// Add Player Number
							ItemMeta meta = item.getItemMeta();

							ArrayList<String> lore = new ArrayList<String>();

							if (meta.hasLore()) {

								for (String l : meta.getLore()) {
									String toAdd = l;

									if (toAdd.contains("&"))
										toAdd = ChatColor.translateAlternateColorCodes('&', toAdd);
									if (toAdd.contains("%count%"))
										toAdd = toAdd.replace("%count%", playerCount + "");

									lore.add(toAdd);
								}

								meta.setLore(lore);

								item.setItemMeta(meta);
								ServerSelectionHandler.warps.setItem(i, item);
								ServerSelectionHandler.warpItems.put(item, warp);
							}
						}
					}
				}
			}

		} else if (subchannel.equals("PlayerCount")) {
			int playerCount = 0;
			String serverName;
			try {
				serverName = in.readUTF(); // Name of server, as given in the arguments
			} catch (Exception e) {
				Bukkit.getServer().getLogger().severe("Invalid Server Name!");
				playerCount = 0;
				return;
			}

			for (int i = 0; i < ServerSelectionHandler.warps.getContents().length; i++) {
				ItemStack item = ServerSelectionHandler.warps.getItem(i);
				if (item == null)
					continue;
				if (ServerSelectionHandler.warpItems.containsKey(item) == false)
					continue;

				WarpTP warp = ServerSelectionHandler.warpItems.get(item);

				if (serverName.equals(warp.getServerName())) {

					// Add Player Number
					ItemMeta meta = item.getItemMeta();

					ArrayList<String> lore = new ArrayList<String>();

					if (meta.hasLore()) {

						for (String l : meta.getLore()) {
							String toAdd = l;

							if (toAdd.contains("&"))
								toAdd = ChatColor.translateAlternateColorCodes('&', toAdd);
							if (toAdd.contains("%count%"))
								toAdd = toAdd.replace("%count%", playerCount + "");

							lore.add(toAdd);
						}

						meta.setLore(lore);

						item.setItemMeta(meta);

						ServerSelectionHandler.warps.setItem(i, item);
						ServerSelectionHandler.warpItems.put(item, warp);
					}
				}
			}
		}
	}

	/** Gets a specified message from the config */
	public static String msg(String message) {
		if (Main.config.contains("messages")) {
			if (Main.config.getConfigurationSection("messages").contains(message)) {
				return ChatColor.translateAlternateColorCodes('&', Main.config.getString("messages." + message));
			} else {
				Bukkit.getLogger().severe("Couldn't find \"" + message + "\" in messages");
			}
		} else {
			Bukkit.getLogger().severe("Couldn't find \"messages\" in config");
		}
		return "";
	}

	public static SendParticleGenerator getSendGeneratorFromEnum(ValidSendGenerators generator, Player player,
			WarpTP warp) {
		switch (generator) {
		case SPIRAL:
			return new Spiral(player, warp);
		case NETHER_PORTAL:
			return new NetherPortal(player, warp);
		default:
			Bukkit.getServer().getLogger()
					.severe("Couldn't determine matching ValidSendGenerators. Contact Developer!");
			return new Spiral(player, warp);
		}
	}

	public static ReceiveParticleGenerator getReceiveGeneratorFromEnum(ValidReceiveGenerators generator,
			Player player) {
		switch (generator) {
		case EMERALD:
			return new Emerald(player);
		case FIRE_EXPLOSION:
			return new FireExplosion(player);
		default:
			Bukkit.getServer().getLogger()
					.severe("Couldn't determine matching ValidReceiveGenerators. Contact Developer!");
			return new Emerald(player);
		}
	}

	/**
	 * @return the sentPlayersConfig
	 */
	public static FileConfiguration getSentPlayersConfig() {
		sentPlayersConfig = YamlConfiguration.loadConfiguration(sentPlayersFile);
		return sentPlayersConfig;
	}

	public ConfigLoader getConfigLoader() {
		return this.configLoader;
	}
}