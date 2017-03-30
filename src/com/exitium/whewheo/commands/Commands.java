package com.exitium.whewheo.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;

import net.md_5.bungee.api.ChatColor;

/**
 * CommandExecutor class for all Whewheo commands.
 * 
 * /ww                      |   Default command for Whewheo. Shows the help screen.
 * /ww create <warpName>    |   Creates a new warp with the players current location.
 * /ww enable <warpName>    |   Enables a disabled warp if all of the requirements are met.
 * /ww reload               |   Reloads data from the config and menu files. Also resets items and inventories.
 * /ww listwarps            |   Lists the warps currently enabled and loaded.
 * /ww listservers          |   Lists the servers currently loaded.
 * /ww teleport/tp <warp>   |   Teleports the player to a specific warp.
 * /ww send <serverName>    |   Sends the player to a specific server.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */

public class Commands implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("ww")) { 
			//If the command starts with /ww
			
			if (args.length == 0) { 
				//If there are no other arguments "/ww"
				
				//TODO: Send Help Message
				return true;
			}else if (args.length == 1){
				//If there is one argument "/ww <arg[0]>"
				
				if (args[0].equalsIgnoreCase("create")) {
					//If command is "/ww create"
					
					sender.sendMessage(ChatColor.RED + "Usage: /ww create <warp>");
					return true;
				}else if(args[0].equalsIgnoreCase("reload")) {
					Main.config = YamlConfiguration.loadConfiguration(Main.configFile);
					Main.menuConfig = YamlConfiguration.loadConfiguration(Main.menuFile);
					Main.saveConfigFile();
					Main.saveMenuConfig();
					
					ConfigLoader.init();
					ServerSelectionHandler.init();
					sender.sendMessage("Successfully reloaded config");
				}else if(args[0].equalsIgnoreCase("listwarps")) {
					if (ConfigLoader.warps != null) {
						for (WarpTP warp : ConfigLoader.warps.values()) {
							sender.sendMessage(warp.getId() + ": " + warp.getName() + " at " + warp.getLocation().getWorld().getName() + ":" + warp.getLocation().getX() + ":" + warp.getLocation().getY() + ":" + warp.getLocation().getZ());
						}
					}else{
						sender.sendMessage("No warps are set yet");
						return true;
					}
				}else if(args[0].equalsIgnoreCase("listservers")) {
					if (ConfigLoader.servers != null) {
						for (ServerTP server : ConfigLoader.servers.values()) {
							sender.sendMessage(server.getId() +  ": " + server.getName() + ".");
						}
					}
				}
						
			}else if (args.length == 2) {
				//If there are two arguments "/ww <arg[0]> <arg[1]>"
				
				if (args[0].equalsIgnoreCase("create")) {
					//If arg[0] is create "/ww create <arg[1]>"
					
					if (sender instanceof Player) { 
						//If sender is a player
						
						Player player = (Player) sender;
						
						
						String warpName = args[1];
						//Check to see if warp already exists
						
						int nextIndex = ConfigLoader.getNextWarpId();
						
						Main.menuConfig.set("warps." + nextIndex + ".name", warpName);
						Main.menuConfig.set("warps." + nextIndex + ".location", ConfigLoader.serializeLocation(player.getLocation()));
						Main.menuConfig.set("warps." + nextIndex + ".enabled", false);
						
						//Create Place Holders
						Main.menuConfig.set("warps." + nextIndex + ".slot", 0);
						Main.menuConfig.set("warps." + nextIndex + ".material", "341:0");
						Main.menuConfig.set("warps." + nextIndex + ".enchantment", "null");
						Main.menuConfig.set("warps." + nextIndex + ".quantity", 1);
						Main.menuConfig.set("warps." + nextIndex + ".lore", Arrays.asList(""));
						Main.menuConfig.set("warps." + nextIndex + ".enableCommands", true);
						Main.menuConfig.set("warps." + nextIndex + ".commands", Arrays.asList(""));
						Main.saveMenuConfig();
						
						player.sendMessage("You have successfully added a new warp.");
						player.sendMessage("Please fill out the rest of the information in the menu.yml file.");
						player.sendMessage("And then use /ww enable <warp> to enable a warp.");
						
					}else{
						//Sender is not a player
						
						notPlayer(sender);
						return true;
					}
				}else if (args[0].equalsIgnoreCase("enable")) {
					String warpName = args[1];
					
					if (ConfigLoader.containsWarpName(warpName)) {
						sender.sendMessage("This warp is already enabled!");
						return true;
					}
					
					if (Main.menuConfig.contains("warps")) {
						for (String key : Main.menuConfig.getConfigurationSection("warps").getKeys(false)) {
							if (Main.menuConfig.getConfigurationSection("warps." + key).contains("name")) {
								if (Main.menuConfig.getString("warps." + key + ".name").equals(warpName)) {
									if (Main.menuConfig.getConfigurationSection("warps." + key).contains("enabled")) {
										if (Main.menuConfig.getBoolean("warps." + key + ".enabled")) {
											sender.sendMessage("This warp is already enabled!");
											return true;
										}else{
											
											if (ConfigLoader.warpHasRequirements("warps." + key)) {
												ConfigLoader.addWarp("warps." + key);
												Main.menuConfig.set("warps." + key + ".enabled", true);
												Main.saveMenuConfig();
												
												sender.sendMessage("Enabled Warp");
											}
											
										}
									}
								}
							}
						}
					}else{
						sender.sendMessage("No warps have been saved yet");
						return true;
					}
				}else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
					String warpName = args[1];
					
					if (ConfigLoader.warps.containsKey(warpName)) {
						
						if (sender instanceof Player) {
							Player player = (Player) sender;
							
							Main.centeredTP(player, ConfigLoader.warps.get(warpName).getLocation());
						}else{
							notPlayer(sender);
							return true;
						}
						
					}else{
						sender.sendMessage("No warp by the name of: " + warpName);
						return true;
					}
				}else if (args[0].equalsIgnoreCase("send")) {
					String serverName = args[1];
					
					if (sender instanceof Player) {
						Player player = (Player) sender;
						Main.sendToSever(player, serverName);
					}else{
						sender.sendMessage("You must be a player to perform this command.");
					}
				}
			}
		}
		//TODO: Send Help Message
		return true;
	}
	
	public void sendHelpMessage(CommandSender sender) {
//		 * /ww                      |   Default command for Whewheo. Shows the help screen.
//		 * /ww create <warpName>    |   Creates a new warp with the players current location.
//		 * /ww enable <warpName>    |   Enables a disabled warp if all of the requirements are met.
//		 * /ww reload               |   Reloads data from the config and menu files. Also resets items and inventories.
//		 * /ww listwarps            |   Lists the warps currently enabled and loaded.
//		 * /ww listservers          |   Lists the servers currently loaded.
//		 * /ww teleport/tp <warp>   |   Teleports the player to a specific warp.
//		 * /ww send <serverName>    |   Sends the player to a specific server.
		
		
	}
	
	public void notPlayer(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You must be a player to perform this commands.");
	}
}