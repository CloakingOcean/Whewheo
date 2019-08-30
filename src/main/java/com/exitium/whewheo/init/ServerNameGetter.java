package com.exitium.whewheo.init;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Meant to wait until a player joins to get the Server Name From Bungee.
 * 
 * @author Cloaking_Ocean
 * date Mar 29, 2017
 * @version 1.0
 */
public class ServerNameGetter implements Runnable{
	

	//Player Reference
	private Player player;
	
	//Thread id of this current thread
	private int threadId;
	private boolean completed = false;
	
	public ServerNameGetter(Player player) {
		threadId = 0;
		this.player = player;
	}
	
	/** Continually checks for the player until they have actually joinwd the server. Once they have actually joined, it sends a BungeeRequest to get the server's name*/
	@Override
	public void run() {
		if (threadId != 0) {
			if (Main.serverName == null) {
				Bukkit.getServer().getLogger().severe("ServerName == null");
				if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null) {
					Bukkit.getServer().getLogger().info("Player is online now at this point!");
					
//					ByteArrayDataOutput out = ByteStreams.newDataOutput();
//					
//					try {
//						out.writeUTF("GetServer");
//					}catch(Exception e) {
//						Bukkit.getServer().getLogger().severe("Error occured while attempting get server name.");
//						e.printStackTrace();
//					}
//					
//					player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
//					
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("GetServer"); //Set channel to GetServer
					
					player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());  //Get an online player to send the Plugin Message through.
					
					Bukkit.getServer().getLogger().info("Sending request!");
					
					
//					if (Main.receivedPlayers.containsKey(player.getUniqueId().toString())) {
//						out = ByteStreams.newDataOutput();
//						
//						Bukkit.getServer().getLogger().info("Sending Received Player Message");
//						
//						try {
//							out.writeUTF("Forward"); // So BungeeCord knows to forward it
//							out.writeUTF("ALL");
//							out.writeUTF("WhewheoReceived"); // The channel name to check if this your data
//
//							ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
//							DataOutputStream msgout = new DataOutputStream(msgbytes);
//							msgout.writeUTF(player.getUniqueId().toString()); // You can do anything you want with msgout
//
//							out.writeShort(msgbytes.toByteArray().length);
//							out.write(msgbytes.toByteArray());
//							
//						}catch (Exception e) {
//							Bukkit.getServer().getLogger().severe("Error occured while attempting to send player target location information.");
//							e.printStackTrace();
//						}
//						
//						player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
//					}else{
//						Bukkit.getServer().getLogger().info("Received Players doesn't contain player");
//						Bukkit.getServer().getLogger().info("Player UUID: " + player.getUniqueId().toString());
//						Bukkit.getServer().getLogger().info("Received Players: ");
//						for (String s : Main.receivedPlayers.keySet()) {
//							Bukkit.getServer().getLogger().info("  " + s);
//						}
//						Bukkit.getServer().getLogger().info("Is Empty: " + Main.receivedPlayers.isEmpty());
//					}
					completed = true;
					Bukkit.getServer().getLogger().info("Completed is now true!");
					
				}
			}else{
				Bukkit.getServer().getScheduler().cancelTask(threadId);
			}
		}
		
		if (completed) {
			Bukkit.getServer().getLogger().info("Cancelling Thread!");
			Bukkit.getServer().getScheduler().cancelTask(threadId);
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
