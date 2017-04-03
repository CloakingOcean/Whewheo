package com.exitium.whewheo.particles.receive.generators;

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
 * @date Apr 2, 2017
 * @version 1.0
 */
public class FireExplosion extends ReceiveParticleGenerator{

	double delay = 1;
	
	public FireExplosion(Player player) {
		super(player, 1);
	}
	
	double radius = 1.5;
	@Override
	public void run() {
		
		Location loc = player.getLocation();
		
		for (int i = 0; i < 16; i ++) {
			timeInRadians = timeInRadians + Math.PI/8;
			double x = (radius)*Math.cos(timeInRadians);
			double y = 0;
			double z = (radius)*Math.sin(timeInRadians);
			Vector v = new Vector(x, 0, z);
	        v = rotateAroundAxisZ(v, 40);
			
			loc.add(v);
			loc.add(0, 1, 0);
			ParticleEffect.FLAME.display(new Vector(0, 0, 0), 1, loc, 100);
			loc.subtract(0, 1, 0);
			loc.subtract(v);
		}
		
		timeInRadians = 0;
		
		for (int i = 0; i < 16; i ++) {
			timeInRadians = timeInRadians + Math.PI/8;
			double x = (radius)*Math.cos(timeInRadians);
			double y = 0;
			double z = (radius)*Math.sin(timeInRadians);
			Vector v = new Vector(x, 0, z);
	        v = rotateAroundAxisZ(v, -40);
			
			loc.add(v);
			loc.add(0, 1, 0);
			ParticleEffect.FLAME.display(new Vector(0, 0, 0), 1, loc, 100);
			loc.subtract(0, 1, 0);
			loc.subtract(v);
		}
		
		
		timeInRadians = 0;
		
		for (int i = 0; i < 16; i ++) {
			timeInRadians = timeInRadians + Math.PI/8;
			double x = (radius)*Math.cos(timeInRadians);
			double y = 0;
			double z = (radius)*Math.sin(timeInRadians);
			Vector v = new Vector(x, 0, z);
	        v = rotateAroundAxisZ(v, 90);
			
			loc.add(v);
			loc.add(0, 1, 0);
			ParticleEffect.FLAME.display(new Vector(0, 0, 0), 1, loc, 100);
			loc.subtract(0, 1, 0);
			loc.subtract(v);
		}
		
		
		
		radius += .2;
		
		if (secondsPassed > delay ) {
			cancel();
		}
		
		secondsPassed += 1.0/20.0;
	}
}
