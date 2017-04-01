package com.exitium.whewheo.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ServerSelectionHandler;
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
	final private double r = 1;

	
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
					
					int count = 1;
					
					t = t + Math.PI/8;
					
					
					for (int i = 0; i < 10; i++) {
						double x = (r - (.09) * i)*Math.cos(t + (Math.PI/8)/2 * i);
						double y = i * (Math.PI/8)/1.50;
						double z = (r - (.09) * i)*Math.sin(t + (Math.PI/8)/2 * i);
						loc.add(x, y, z);
						
						
						
//						ParticleEffect.FLAME.display(0.0f, 0.0f, 0.0f, (float) speed, 1, loc, player);
						
						ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, loc, 100);
						
						//Spigot API
//						loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.getX(), loc.getY(), loc.getZ(), count);
						
						//Particle API
						
						
//						ParticleEffect.NAME.display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players);
						
						
						
//						int speed = 100;
//						PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, false, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0, 0, 0, speed, 1, null);
//						((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//						
						
						
						
						
						loc.subtract(x, y, z);
					}
					
					
					
					
					
//					loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.getX(), loc.getY() + 2.5, loc.getZ(), count*20);
					
//					PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
//							EnumParticle.VILLAGER_HAPPY,
//							true,
//							
//							(float) loc.getX(), //float
//							(float) (loc.getY() + 2.5), //float 
//							(float) loc.getZ(), //float
//							
//							0, //x offset
//							0, //y offset
//							0, //z offset
//							
//							1, //Speed
//							10*20,  //Number of Particles
//							null
//					);
//					
//					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
					
					
//					Example 1:
//					
//					for (int i = 0; i < 15; i++) {
//						
//						if (i%2 == 0) {
//							double x = r*Math.cos(t + (Math.PI/8)*i);
//							double y = t;
//							double z = r*Math.sin(t + (Math.PI/8)*i);
//							loc.add(x, y, z);
//							
//							loc.getWorld().spawnParticle(Particle.HEART, loc.getX(), loc.getY(), loc.getZ(), count);
//							
//							loc.subtract(x, y, z);
//						}
//					}
					
					
					
					
					
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