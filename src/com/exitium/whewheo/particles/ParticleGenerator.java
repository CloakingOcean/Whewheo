package com.exitium.whewheo.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.ParticleEffect.ParticleColor;
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * This class generates particles for a teleporting player
 * Should be functionally running at 20 ticks per second.
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
	
	private double secondsPassed;
	
	private double t = 0;
	final private double r = 5;

	
	public ParticleGenerator(Player player, WarpTP warp) {
		secondsPassed = 0;
		threadId = 0;
		t = 0;
		this.player = player;
		this.warp = warp;
	}
	
	public ParticleGenerator(Player player, ServerTP server) {
		secondsPassed = 0;
		threadId = 0;
		t = 0;
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
					
					Location loc = player.getLocation();
					
					int count = 50;
					
					t = t + Math.PI/8;
					
					double x = r*Math.cos(t);
					double y = t;
					double z = r*Math.sin(t);
					loc.add(x, y, z);
					ParticleEffect.FLAME.display(new ParticleColor() {
						
						@Override
						public float getValueZ() {
							// TODO Auto-generated method stub
							return (float) loc.getX();
						}
						
						@Override
						public float getValueY() {
							// TODO Auto-generated method stub
							return (float) loc.getY();
						}
						
						@Override
						public float getValueX() {
							// TODO Auto-generated method stub
							return (float) loc.getZ();
						}
					}, loc, 100); // Should work
					
					
					
					
					
					
					
//					player.getLocation().getWorld().spawnParticle(Particle.FLAME, player.getLocation(), count);
					
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, 1.0, 0.0, 0.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, -1.0, 0.0, 0.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, 1.0, 0.0, 1.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, -1.0, 0.0, -1.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, 1.0, 0.0, -1.0);
//					player.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, x, y, z, count, -1.0, 0.0, 1.0);
					
					
					
					
					
					
					if (secondsPassed < delay) {
						if ((secondsPassed - (int) secondsPassed) == 0) {
							player.sendMessage("Teleportion will commence in " + (delay-secondsPassed));
						}
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
				
				secondsPassed += (1.0/20.0);
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