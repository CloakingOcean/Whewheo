package com.exitium.whewheo.particles.send;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.receive.ReceiveParticleGenerator;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.teleportobjects.WarpTP;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * General Send Particle Generator class that all Send Particle Generators must extend.
 * 
 * @author Cloaking_Ocean
 * date Apr 1, 2017
 * @version 1.0
 */
public class SendParticleGenerator extends ParticleGenerator{
	
	protected WarpTP warp;
	
	public static double delay;
	protected int currentSecond = -1;
	
	public SendParticleGenerator(Player player, int tickDelay, WarpTP warp) {
		super(player, tickDelay);
		
		this.warp = warp;
		
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

	protected void handleLocationDetails(Player player) {
		if (warp.getLocation() == null) {
			//Not specified. No need to teleport
			
			sendPlayerToServer(player, warp.getReceive());
		}else{
			sendPlayerToServer(player, warp.getLocation(), warp.getReceive());
		}
	}
	
	protected void checkTeleporation() {
		if (secondsPassed < delay) {
			if (currentSecond < (int) secondsPassed) {
				currentSecond = (int) secondsPassed;
				player.sendMessage(Main.prefix + Main.msg("teleportationWillCommenceIn").replace("%time%", ((int)(delay - currentSecond)) + ""));
			}
			currentSecond = (int) secondsPassed;
		}else{
			if (warp.getServerName().equals(Main.serverName)) {
				//
				Main.centeredTP(player, warp.getLocation());
				player.closeInventory();
//						new PurpleSphere(player).runTaskTimer(Main.instance, 0, 1);
				
				
//						
//						ParticleGenerator generator = ;
				
				//Switch to determine generator for specific item;
				
				ParticleGenerator generator = getGenerator(player, warp);
				
				generator.runTaskTimer(Main.instance, 0, generator.getTickDelay());
				
//						ParticleEffect.SPELL_WITCH.display(new Vector(0, 0, 0), 1, player.getLocation().add(0, 2, 0), 100);
				ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
			}else{
				ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
				handleLocationDetails(player);
			}
			cancel();
			
		}
	}
	
	protected void sendPlayerToServer(Player player, ValidReceiveGenerators generator) {
		ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
		
		addPlayerInformation(player, generator);
		
		Main.sendToSever(Bukkit.getPlayer(player.getUniqueId()), warp.getServerName());
	}
	
	protected void sendPlayerToServer(Player player, Location loc, ValidReceiveGenerators generator) {
		ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
		
		addPlayerInformation(player, loc, generator);
		
		Main.sendToSever(Bukkit.getPlayer(player.getUniqueId()), warp.getServerName());
	}
	
	private void addPlayerInformation(Player player, Location loc, ValidReceiveGenerators generator) {
		if (Main.getSentPlayersConfig().contains(player.getUniqueId().toString())) {
			Bukkit.getServer().getLogger().severe("Left over information inside the sentplayers.yml!");
		}
		
		FileConfiguration fc = Main.getSentPlayersConfig();
		fc.set(player.getUniqueId().toString(), loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + generator.name());
		Main.saveSentPlayersConfig();
	}
	
	private void addPlayerInformation(Player player, ValidReceiveGenerators generator) {
		if (Main.getSentPlayersConfig().contains(player.getUniqueId().toString())) {
			Bukkit.getServer().getLogger().severe("Left over information inside the sentplayers.yml!");
		}
		
		FileConfiguration fc = Main.getSentPlayersConfig();
		fc.set(player.getUniqueId().toString(), generator.name());
		Main.saveSentPlayersConfig();
		
//		" + loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + generator.name()); // You can do anything you want with msgout
	}
	
//	private void sendPlayerLocationInformation(Player player, Location loc, ValidReceiveGenerators generator) {
//		ByteArrayDataOutput out = ByteStreams.newDataOutput();
//		
//		try {
//			out.writeUTF("Forward"); // So BungeeCord knows to forward it
//			out.writeUTF("ALL");
//			out.writeUTF("Whewheo"); // The channel name to check if this your data
//
//			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
//			DataOutputStream msgout = new DataOutputStream(msgbytes);
//			msgout.writeUTF(player.getUniqueId().toString() + ":" + loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + generator.name()); // You can do anything you want with msgout
//
//			out.writeShort(msgbytes.toByteArray().length);
//			out.write(msgbytes.toByteArray());
//			
//		}catch (Exception e) {
//			Bukkit.getServer().getLogger().severe("Error occured while attempting to send player target location information.");
//			e.printStackTrace();
//		}
//		
//		player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
//	}
	
//	private void sendPlayerLocationInformation(Player player, ValidReceiveGenerators generator) {
//		ByteArrayDataOutput out = ByteStreams.newDataOutput();
//		
//		try {
//			out.writeUTF("Forward"); // So BungeeCord knows to forward it
//			out.writeUTF("ALL");
//			out.writeUTF("Whewheo"); // The channel name to check if this your data
//
//			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
//			DataOutputStream msgout = new DataOutputStream(msgbytes);
//			msgout.writeUTF(player.getUniqueId().toString() + ":" + generator.name()); // You can do anything you want with msgout
//
//			out.writeShort(msgbytes.toByteArray().length);
//			out.write(msgbytes.toByteArray());
//			
//		}catch (Exception e) {
//			Bukkit.getServer().getLogger().severe("Error occured while attempting to send player target location information.");
//			e.printStackTrace();
//		}
//		
//		player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
//	}
//	
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
	
	public ReceiveParticleGenerator getGenerator(Player player, WarpTP warp) {
		Bukkit.getServer().getLogger().severe("Starting Switch Statement");
		
		return Main.getReceiveGeneratorFromEnum(warp.getReceive(), player);
	}
}
