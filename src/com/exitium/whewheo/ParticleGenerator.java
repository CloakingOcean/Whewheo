package com.exitium.whewheo;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
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
			if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null ) {
				if (ServerSelectionHandler.teleportingPlayers.contains(player.getUniqueId().toString())) {
					
					
					double x = player.getLocation().getX();
					double y = player.getLocation().getY() + 2;
					double z = player.getLocation().getZ();
					
					int count = 50;
					
					player.getLocation().getWorld().spawnParticle(Particle.FLAME, player.getLocation(), count);
					
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, 1.0, 0.0, 0.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, -1.0, 0.0, 0.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, 1.0, 0.0, 1.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, -1.0, 0.0, -1.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, 1.0, 0.0, -1.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, -1.0, 0.0, 1.0);
					
					
					
					
					
					
					if (secondsPassed < delay) {
						player.sendMessage("Teleportion will commence in " + (delay-secondsPassed));
					}else{
						
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
							@Override
							public void run() {
								if (warp != null) {
									Main.centeredTP(player, warp.getLocation());
									ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
								}
								if (server != null) {
									Bukkit.broadcastMessage("Server Name: " + server.getName());
									
									//Add check for if player is online
									
									ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
									Main.sendToSever(Bukkit.getPlayer(player.getUniqueId()), server.getServer());
								}
							}
						});
					}
				}else{
					Bukkit.getServer().broadcastMessage("Cancelled task because is no longer a teleporting Player");
					Bukkit.getScheduler().cancelTask(threadId);
				}
				
				secondsPassed++;
			}else{
				ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
				Bukkit.getScheduler().cancelTask(threadId);
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