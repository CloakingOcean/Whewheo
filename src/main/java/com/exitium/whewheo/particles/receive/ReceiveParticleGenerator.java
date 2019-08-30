package com.exitium.whewheo.particles.receive;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.exitium.whewheo.particles.ParticleGenerator;

/**
 * General Receive Particle Generator class that all Receive Particle Generators
 * must extend.
 * 
 * @author Cloaking_Ocean date Apr 1, 2017
 * @version 1.0
 */
public class ReceiveParticleGenerator extends ParticleGenerator {

	public ReceiveParticleGenerator(Player player, int tickDelay) {
		super(player, tickDelay);
	}

	@Override
	public void run() {
		Bukkit.getServer().getLogger().severe("This ReceiveParticleGenorator doesn't have a specific animation.");
	}
}
