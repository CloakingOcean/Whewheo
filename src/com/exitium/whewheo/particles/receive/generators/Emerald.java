package com.exitium.whewheo.particles.receive.generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.exitium.whewheo.particles.ParticleEffect;
import com.exitium.whewheo.particles.receive.ReceiveParticleGenerator;

/**
 * Receiving Class. Run after a player has been successfully teleported. 
 * [Only implemented with warps currently because I don't know of a way to send specific information to another server when a player is teleported]
 * 
 * @author Cloaking_Ocean
 * @date Apr 1, 2017
 * @version 1.0
 */
public class Emerald extends ReceiveParticleGenerator{
	
	private double startyLevel = 3;
	private double yLevel = 3;
	private double radius = 1;
	
	public Emerald(Player player) {
		super(player, 1);
	}
	
	@Override
	public void run() {
		double delay = .5;
		if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null ) {
				
				Location loc = player.getLocation();
				
				int count = 1;
				
				double tempT = timeInRadians;
				
				if (secondsPassed < delay/2) {
					radius += .1;
				}else{
					radius -= .1;
				}
				
				for (int i = 0; i < 16; i++) { //Create 16 particles
					timeInRadians = timeInRadians + Math.PI/8;
					
					//Create a circle
					double x = (radius)*Math.cos(timeInRadians);
					double y = yLevel;
					double z = (radius)*Math.sin(timeInRadians);
					loc.add(x, y, z);
					
					
					
					ParticleEffect.VILLAGER_HAPPY.display(new Vector(0, 0, 0), 1, loc, 100);
					
//					ParticleEffect.SPELL_WITCH.display(new Vector(0,0,0), 1, loc, 100);
//					ParticleEffect.SPELL_WITCH.display(new Vector(0, 0, 0), 1, loc, 100);
					
					

					
//					loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1);
					
					
					
					loc.subtract(x, y, z);
				}
				
				yLevel -= startyLevel/((20)/2);
				
				if (secondsPassed >= delay) {
					cancel();
				}
			
			secondsPassed += (1.0/20.0);
		}else{
			cancel();
		}
	}
}