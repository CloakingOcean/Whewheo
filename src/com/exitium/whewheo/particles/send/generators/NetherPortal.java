package com.exitium.whewheo.particles.send.generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.ParticleEffect;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * This is a Send Generator class
 * Description: Surrounds the Player in a box resembling a nether portal.
 * 
 * @author Cloaking_Ocean
 * @date Mar 27, 2017
 * @version 1.0
 */
public class NetherPortal extends SendParticleGenerator{
	
	private int height = 2;
	private int width = 2;
	final private double space = .4;
	
	private int currentSecond = -1;
	
	//Remember to go up from the players location because the player's location refers to the bottom of their feet.
	
	public NetherPortal(Player player, ServerTP server) {
		super(player, 1, server);
	}
	
	public NetherPortal(Player player, WarpTP warp) {
		super(player, 1, warp);
	}
	
	@Override
	public void run() {
		
		Location loc = player.getLocation();
		for (double r = (width/2) * -1; r < (width/2); r += space) {
			for (double c = (height/2) * -1; c < (height/2); c += space) {
		        double x = r;
		        double y = 0;
		        double z = c;
		        Vector v = new Vector(x, 0, z);
		        v = rotateAroundAxisY(v, 50 * secondsPassed);
		        loc.add(v.getX(), v.getY(), v.getZ());
		        ParticleEffect.SMOKE_NORMAL
		        .display(0, 0, 0, 0, 1, loc, 100D);
				loc.subtract(v.getX(), v.getY(), v.getZ());
			}
		}
		
		loc.add(0, height, 0);
		for (double r = (width/2) * -1; r < (width/2); r += space) {
			for (double c = (height/2) * -1; c < (height/2); c += space) {
		        double x = r;
		        double y = 0;
		        double z = c;
		        Vector v = new Vector(x, 0, z);
		        v = rotateAroundAxisY(v, 50 * secondsPassed);
		        loc.add(v.getX(), v.getY(), v.getZ());
		        ParticleEffect.SMOKE_NORMAL
		        .display(0, 0, 0, 0, 1, loc, 100D);
				loc.subtract(v.getX(), v.getY(), v.getZ());
			}
		}
		loc.subtract(0, height, 0);
		
		for (double y = 0; y < height; y += space) {
			for (double r = (width/2) * -1; r < (width/2); r += space) {
				for (double c = (height/2) * -1; c < (height/2); c += space) {
			        double x = r;
			        double z = c;
			        Vector v = new Vector(x, y, z);
			        v = rotateAroundAxisY(v, 50 * secondsPassed);
			        loc.add(v.getX(), v.getY(), v.getZ());
			        
			        if (x >= (width/2)- space || x <= ((width/2) * -1) + space  || 
			        	z >= (height/2)- space || z <= ((height/2) * -1) + space) {
			        	ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0, 1, loc, 100D);
			        }else{
				        ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, loc, 100D);
			        }
					loc.subtract(v.getX(), v.getY(), v.getZ());
				}
			}
		}
		//if (t > Math.PI * 8) {
		//    this.cancel();
		//}
		
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
//					new PurpleSphere(player).runTaskTimer(Main.instance, 0, 1);
				
				
//					
//					ParticleGenerator generator = ;
				
				//Switch to determine generator for specific item;
				
				ParticleGenerator generator = getGenerator(player, warp);
				
				generator.runTaskTimer(Main.instance, 0, generator.getTickDelay());
				
//					ParticleEffect.SPELL_WITCH.display(new Vector(0, 0, 0), 1, player.getLocation().add(0, 2, 0), 100);
				ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
				
			}
			if (server != null) {
				//Add check for if player is online
				
				ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
				Main.sendToSever(Bukkit.getPlayer(player.getUniqueId()), server.getServer());
			}
			cancel();
			
		}
		
		secondsPassed += ((double)1)/((double)20);
	}
}
