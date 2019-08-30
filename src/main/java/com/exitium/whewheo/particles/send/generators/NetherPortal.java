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
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * This is a Send Generator class
 * Description: Surrounds the Player in a box resembling a nether portal.
 * 
 * @author Cloaking_Ocean
 * date Mar 27, 2017
 * @version 1.0
 */
public class NetherPortal extends SendParticleGenerator{
	
	private int height = 2;
	private int width = 2;
	final private double space = .4;
	
	//Remember to go up from the players location because the player's location refers to the bottom of their feet.
	
	public NetherPortal(Player player, WarpTP warp) {
		super(player, 1, warp);
	}
	
	@Override
	public void run() {
		
		if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null ) {
			if (ServerSelectionHandler.teleportingPlayers.contains(player.getUniqueId().toString())) {
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
				
				checkTeleporation();
			}else{
				cancel();
			}
			
			secondsPassed += ((double)1)/((double)20);
		}else{
			ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
			cancel();
		}
	}
}
