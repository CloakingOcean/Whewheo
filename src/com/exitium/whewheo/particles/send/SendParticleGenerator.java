package com.exitium.whewheo.particles.send;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.receive.ReceiveParticleGenerator;
import com.exitium.whewheo.particles.receive.generators.Emerald;
import com.exitium.whewheo.particles.receive.generators.FireExplosion;
import com.exitium.whewheo.particles.send.generators.NetherPortal;
import com.exitium.whewheo.particles.send.generators.Spiral;
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;

/**
 * General Send Particle Generator class that all Send Particle Generators must extend.
 * 
 * @author Cloaking_Ocean
 * @date Apr 1, 2017
 * @version 1.0
 */
public class SendParticleGenerator extends ParticleGenerator{
	
	protected ServerTP server;
	protected WarpTP warp;
	
	protected boolean isServer;
	protected boolean isWarp;
	public static double delay;
	
	public SendParticleGenerator(Player player, int tickDelay, WarpTP warp) {
		super(player, tickDelay);
		
		this.warp = warp;
		
		isWarp = true;
		isServer = false;
		
		if (Main.config.contains("general")) {
			if (Main.config.getConfigurationSection("general").contains("teleportDelay")) {
				this.delay = Main.config.getInt("general.teleportDelay");;
			}else{
				Bukkit.getServer().getLogger().severe("Couldn't load SendParticleGeneartors because there is no \"teleportDelay\" section in config.");
				this.delay = -1;
			}
		}else{
			Bukkit.getServer().getLogger().severe("Couldn't load SendParticleGeneartors because there is no \"general\" section in config.");
			this.delay = -1;
		}
	}
	
	public SendParticleGenerator(Player player, int tickDelay, ServerTP server) {
		super(player, tickDelay);
		
		this.server = server;
		
		isWarp = false;
		isServer = true;
		this.delay = Main.config.getInt("general.teleportDelay");;
	}

	@Override
	public void run() {
		Bukkit.getServer().getLogger().severe("This SendParticleGenorator doesn't have a specific animation.");
	}
	
	public double getDelay() {
		return delay;
	}
	
	public void setDelay(double delay) {
		this.delay = delay;
	}
	
	public SendParticleGenerator getGenerator(Player player, ServerTP server) {
		
		switch(server.getSend()) {
			case SPIRAL:
				return new Spiral(player, server);
			case NETHER_PORTAL:
				return new NetherPortal(player, server);
			default:
				Bukkit.getServer().getLogger().severe("Couldn't determin matching ValidSendGenerators. Contact Developer!");
				return new Spiral(player, server);
		}
	}
	
	public ReceiveParticleGenerator getGenerator(Player player, WarpTP warp) {
		Bukkit.getServer().getLogger().severe("Starting Switch Statement");
		
		switch(warp.getReceive()) {
			case EMERALD:
				return new Emerald(player);
			case FIRE_EXPLOSION:
				return new FireExplosion(player);
			default:
				Bukkit.getServer().getLogger().severe("Couldn't determin matching ValidReceiveGenerators. Contact Developer!");
				return new Emerald(player);
		}
	}
}
