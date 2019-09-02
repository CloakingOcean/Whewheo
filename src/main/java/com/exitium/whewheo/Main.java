package com.exitium.whewheo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for Whewheo. Extends JavaPlugin.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */
public class Main extends JavaPlugin { 

	private ConfigLoader configLoader;
	private ServerSelectionHandler serverSel;

	/** Runs on server startup. */
	@Override
	public void onEnable() {

		// Instantiates a new Config Loader to store information from the config
		this.configLoader = new ConfigLoader(this);
		this.configLoader.init();

		// Register listeners for ServerSelectionHandler.
		Bukkit.getPluginManager().registerEvents(serverSel = new ServerSelectionHandler(this.configLoader.getWarps(), this), this);

		// Sets command "/ww"'s executor to Commands.
		getCommand("ww").setExecutor(new Commands(this.configLoader, this.serverSel));

		// Attempts to get the server name from the bungeecord proxy if a player is
		// online.
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) { // If a player is online

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer"); // Set channel to GetServer

			Bukkit.getServer().getOnlinePlayers().iterator().next().sendPluginMessage(this, "BungeeCord",
					out.toByteArray()); // Get an online player to send the Plugin Message through.
		}
	}

	/** Runs on server shut down. */
	@Override
	public void onDisable() {

	}

	/**
	 * Attempts to send the player to the target server. Prints a stack trace if
	 * unsuccessful.
	 */
	public void sendToSever(Player player, String targetServer) {

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
		player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
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

	public ConfigLoader getConfigLoader() {
		return this.configLoader;
	}

	public ServerSelectionHandler getServerSel() {
		return this.serverSel;
	}
}