package com.exitium.whewheo.particles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * General Particle Generator class that all Particle Generators must extend.
 * 
 * @author Cloaking_Ocean
 * date Apr 1, 2017
 * 
 * @version 1.0
 */
public class ParticleGenerator extends BukkitRunnable{
	
	protected Player player;
	protected int tickDelay;
	
	protected double timeInRadians = 0;
	protected double secondsPassed;
	
	public ParticleGenerator(Player player, int tickDelay) {
		this.player = player;
		this.setTickDelay(tickDelay);
	}
	
	@Override
	public void run() {
		Bukkit.getServer().getLogger().severe("This ParticleGenorator doesn't have a specific animation.");
	}

	/**
	 * @return the tickDelay
	 */
	public int getTickDelay() {
		return tickDelay;
	}

	/**
	 * @param tickDelay the tickDelay to set
	 */
	public void setTickDelay(int tickDelay) {
		this.tickDelay = tickDelay;
	}
	
	protected Vector rotateAroundAxisX(Vector v, double angle) {
        angle = Math.toRadians(angle);
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    protected Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    protected Vector rotateAroundAxisZ(Vector v, double angle) {
        angle = Math.toRadians(angle);
        double x, y, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }
}