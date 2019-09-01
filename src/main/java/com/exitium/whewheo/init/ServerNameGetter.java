package com.exitium.whewheo.init;

import com.exitium.whewheo.Main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Meant to wait until a player joins to get the Server Name From Bungee.
 * 
 * @author Cloaking_Ocean date Mar 29, 2017
 * @version 1.0
 */
public class ServerNameGetter implements Runnable {

	// Player Reference
	private Player player;

	// Thread id of this current thread
	private int threadId;

	private Main main;

	public ServerNameGetter(Player player, Main main) {
		threadId = 0;
		this.player = player;

		this.main = main;
	}

	/**
	 * Continually checks for the player until they have actually joinwd the server.
	 * Once they have actually joined, it sends a BungeeRequest to get the server's
	 * name
	 */
	@Override
	public void run() {
		if (threadId == 0) {
			return;
		}

		if (main.getServerName() != null) {
			Bukkit.getServer().getScheduler().cancelTask(threadId);
			return;
		}

		Bukkit.getServer().getLogger().severe("ServerName has not been defined yet.");
		if (Bukkit.getServer().getPlayer(player.getUniqueId()) == null) {
			return;
		}

		Bukkit.getServer().getLogger().info("Player is online now at this point!");

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer"); // Set channel to GetServer

		player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
		Bukkit.getServer().getLogger().info("Sending request!");
		Bukkit.getServer().getLogger().info("Cancelling Thread!");
		Bukkit.getServer().getScheduler().cancelTask(threadId);
	}

	/**
	 * @return the threadId
	 */
	public int getThreadId() {
		return threadId;
	}

	/**
	 * @param threadId the threadId to set
	 */
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
}
