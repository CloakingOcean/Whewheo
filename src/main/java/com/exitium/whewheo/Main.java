package com.exitium.whewheo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Main class for Whewheo. Extends JavaPlugin.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */
public class Main extends JavaPlugin implements PluginMessageListener { 

	// Server name returned by Bungeecord's Proxy.
	private String serverName;

	// Public static instance of the Main class for easier access.
	public static Main instance;

	private ConfigLoader configLoader;
	private ServerSelectionHandler serverSel;

	/** Runs on server startup. */
	@Override
	public void onEnable() {

		// Instantiates a new Config Loader to store information from the config
		this.configLoader = new ConfigLoader(this);

		// Register Outgoing and Incoming Plugin Channel for BungeeCord to request the
		// server name and player count.
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		// Register listeners for ServerSelectionHandler.
		Bukkit.getPluginManager().registerEvents(serverSel = new ServerSelectionHandler(this.configLoader.getWarps(), this), this);

		// Sets command "/ww"'s executor to Commands.
		getCommand("ww").setExecutor(new Commands(this.configLoader, this.serverSel));

		// Attempts to get the server name from the bungeecord proxy if a player is
		// online.
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) { // If a player is online

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer"); // Set channel to GetServer

			Bukkit.getServer().getOnlinePlayers().iterator().next().sendPluginMessage(Main.instance, "BungeeCord",
					out.toByteArray()); // Get an online player to send the Plugin Message through.
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

			Inventory warpInventory = this.serverSel.getWarpsInventory();
			HashMap<ItemStack, WarpTP> warpItems = this.serverSel.getWarpItems();

			for (int i = 0; i < warpInventory.getContents().length; i++) {
				ItemStack item = warpInventory.getItem(i);
				if (item != null) {
					if (warpItems.containsKey(item) == true) {

						WarpTP warp = warpItems.get(item);

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
								this.serverSel.setWarpItem(i, item);
								this.serverSel.addWarpItem(item, warp);
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

			Inventory warpsInventory = this.serverSel.getWarpsInventory();
			HashMap<ItemStack, WarpTP> warpItems = this.serverSel.getWarpItems();

			for (int i = 0; i < warpsInventory.getContents().length; i++) {
				ItemStack item = warpsInventory.getItem(i);
				if (item == null)
					continue;
				if (warpItems.containsKey(item) == false)
					continue;

				WarpTP warp = warpItems.get(item);

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

						this.serverSel.setWarpItem(i, item);
						this.serverSel.addWarpItem(item, warp);
					}
				}
			}
		}
	}

	public SendParticleGenerator getSendGeneratorFromEnum(ValidSendGenerators generator, Player player,
			WarpTP warp) {
		switch (generator) {
		case SPIRAL:
			return new Spiral(player, warp, this);
		case NETHER_PORTAL:
			return new NetherPortal(player, warp, this);
		default:
			Bukkit.getServer().getLogger()
					.severe("Couldn't determine matching ValidSendGenerators. Contact Developer!");
			return new Spiral(player, warp, this);
		}
	}

	public ReceiveParticleGenerator getReceiveGeneratorFromEnum(ValidReceiveGenerators generator,
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

	public String getServerName() {
		return this.serverName;
	}

	public ConfigLoader getConfigLoader() {
		return this.configLoader;
	}

	public ServerSelectionHandler getServerSel() {
		return this.serverSel;
	}
}