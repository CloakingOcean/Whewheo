package com.exitium.whewheo.particles.send.generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.ParticleEffect;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.receive.generators.Emerald;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * This is a Send Generator class
 * Description: Displays a white thick spiral starting from 1 block above the player's head, coming from a small circle of clouds.
 * 
 * @author Cloaking_Ocean
 * @date Mar 27, 2017
 * @version 1.0
 */
public class Spiral extends SendParticleGenerator{
	
	private int currentSecond = -1;
	private double radius = 1;
	
	public Spiral(Player player, WarpTP warp) {
		super(player, 1, warp);
	}
	
	public Spiral(Player player, ServerTP server) {
		super(player, 1, server);
	}
	
	@Override
	public void run() {
		if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null ) {
			if (ServerSelectionHandler.teleportingPlayers.contains(player.getUniqueId().toString())) {
				
				Location loc = player.getLocation();
				
				int count = 1;
				
				timeInRadians = timeInRadians + Math.PI/8;
				
				
				for (int i = 0; i < 10; i++) {
					double x = (radius - (.09) * i)*Math.cos(timeInRadians + (Math.PI/8)/2 * i);
					double y = i * (Math.PI/8)/1.50;
					double z = (radius - (.09) * i)*Math.sin(timeInRadians + (Math.PI/8)/2 * i);
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
				
				double distance = 0.2;
				
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 2.7, loc.getZ()), 100);
				
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + distance, loc.getY()+ 2.7, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - distance, loc.getY()+ 2.7, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 2.7, loc.getZ() + distance), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 2.7, loc.getZ() - distance), 100);
				
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + distance, loc.getY()+ 2.7, loc.getZ() + distance), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + distance, loc.getY()+ 2.7, loc.getZ() - distance), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - distance, loc.getY()+ 2.7, loc.getZ() + distance), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - distance, loc.getY()+ 2.7, loc.getZ() - distance), 100);
				
				
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 3, loc.getZ()), 100);
				
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + 0.1, loc.getY()+ 3, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - 0.1, loc.getY()+ 3, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 3, loc.getZ() + 0.1), 100);
				ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 3, loc.getZ() - 0.1), 100);
				
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
					
					
					
					if (currentSecond < (int) secondsPassed) {
						currentSecond = (int) secondsPassed;
						player.sendMessage(Main.prefix + Main.msg("teleportationWillCommenceIn").replace("%time%", ((int)(delay - currentSecond)) + ""));
					}
					currentSecond = (int) secondsPassed;
				}else{
					if (warp != null) {
						Main.centeredTP(player, warp.getLocation());
						player.closeInventory();
//							new PurpleSphere(player).runTaskTimer(Main.instance, 0, 1);
						
						
//							
//							ParticleGenerator generator = ;
						
						//Switch to determine generator for specific item;
						
						ParticleGenerator generator = getGenerator(player, warp);
						
						generator.runTaskTimer(Main.instance, 0, generator.getTickDelay());
						
//							ParticleEffect.SPELL_WITCH.display(new Vector(0, 0, 0), 1, player.getLocation().add(0, 2, 0), 100);
						ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
						
					}
					if (server != null) {
						
						//Add check for if player is online
						
						ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
						Main.sendToSever(Bukkit.getPlayer(player.getUniqueId()), server.getServer());
					}
				}
			}else{
				cancel();
			}
			
			secondsPassed += (1.0/20.0);
		}else{
			ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
			cancel();
		}
	}
}