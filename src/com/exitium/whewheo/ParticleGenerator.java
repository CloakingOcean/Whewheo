package com.exitium.whewheo;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

/**
 * This class generates particles for a teleporting player
 * 
 * @author Cloaking_Ocean
 * @date Mar 27, 2017
 * @version 1.0
 */
public class ParticleGenerator implements Runnable{
	
	private Player player;
	private WarpTP warp;
	
	private ServerTP server;
	
	private int threadId;
	
	private int secondsPassed;
	
	public ParticleGenerator(Player player, WarpTP warp) {
		secondsPassed = 0;
		threadId = 0;
		this.player = player;
		this.warp = warp;
	}
	
	public ParticleGenerator(Player player, ServerTP server) {
		secondsPassed = 0;
		threadId = 0;
		this.player = player;
		
		this.server = server;
	}
	
	@Override
	public void run() {
		Bukkit.getServer().broadcastMessage("Running Thread");
		if (threadId != 0) {
			Bukkit.getServer().broadcastMessage("Thread Id Set!");
			int delay = Main.config.getInt("general.teleportDelay");
			if (ServerSelectionHandler.teleportingPlayers.contains(player.getUniqueId().toString())) {
				
				player.getLocation().getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 10);
				
				if (secondsPassed < delay) {
					player.sendMessage("Teleportion will commence in " + (delay-secondsPassed));
				}else{
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
						@Override
						public void run() {
							if (warp != null) {
								Main.centeredTP(player, warp.getLocation());
								ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
							}else if (server != null) {
								Main.sendToSever(player, server.getName());
								ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
							}
						}
					});
				}
			}else{
				Bukkit.getScheduler().cancelTask(threadId);
			}
			
			secondsPassed++;
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