package com.exitium.whewheo.init;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Meant to wait until a player joins to get the Server Name From Bungee.
 * 
 * @author Cloaking_Ocean
 * @date Mar 29, 2017
 * @version 1.0
 */
public class ServerNameGetter implements Runnable{
	

	//Player Reference
	private Player player;
	
	//Thread id of this current thread
	private int threadId;
	
	
	public ServerNameGetter(Player player) {
		threadId = 0;
		this.player = player;
	}
	
	/** Continually checks for the player until they have actually joind the server. Once they have actually joined, it sends a BungeeRequest to get the server's name*/
	@Override
	public void run() {
		Bukkit.getServer().broadcastMessage("Running Thread");
		if (threadId != 0) {
			if (Main.serverName == null) {
				if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null) {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					
					try {
						out.writeUTF("GetServer");
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
	
					
				}
			}else{
				Bukkit.getServer().getScheduler().cancelTask(threadId);
			}
		}
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
