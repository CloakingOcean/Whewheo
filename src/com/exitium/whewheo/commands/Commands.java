package com.exitium.whewheo.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.init.ServerSelectionHandler;

import net.md_5.bungee.api.ChatColor;

/**
 * CommandExecutor class for all Whewheo commands.
 * 
 * /ww                      |   Default command for Whewheo. Shows the help screen.
 * /ww create <warpName>    |   Creates a new warp with the players current location.
 * /ww enable <warpName>    |   Enables a disabled warp if all of the requirements are met.
 * /ww reload               |   Reloads data from the config and menu files. Also resets items and inventories.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */

public class Commands implements CommandExecutor{

	/** Method that handles the /ww command*/
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("ww")) { 
			//If the command starts with /ww
			
			if (args.length == 0) { 
				//If there are no other arguments "/ww"
				
				sendHelpMessage(sender);
				return true;
			}else if (args.length == 1){
				//If there is one argument "/ww <arg[0]>"
				
				if (args[0].equalsIgnoreCase("create")) {
					//If command is "/ww create"
					
					sender.sendMessage(ChatColor.RED + "Usage: /ww create <warp>");
					return true;
				}else if(args[0].equalsIgnoreCase("reload")) {
					//Reload All Information
					
					Main.config = YamlConfiguration.loadConfiguration(Main.configFile);
					Main.menuConfig = YamlConfiguration.loadConfiguration(Main.menuFile);
					Main.saveConfigFile();
					Main.saveMenuConfig();
					
					ConfigLoader.init();
					ServerSelectionHandler.init();
					sender.sendMessage(Main.prefix  + Main.msg("reloadedConfig"));
					return true;
				}else if (args[0].equalsIgnoreCase("test")) {
					sender.sendMessage("Sending received Players.");
					for (String playerUUID : Main.receivedPlayers.keySet()) {
						sender.sendMessage("Player: " + playerUUID);
					}
				}
//				else if(args[0].equalsIgnoreCase("listwarps")) {
//					if (ConfigLoader.warps != null) {
//						for (WarpTP warp : ConfigLoader.warps.values()) {
//							sender.sendMessage(warp.getId() + ": " + warp.getName() + " at " + warp.getLocation().getWorld().getName() + ":" + warp.getLocation().getX() + ":" + warp.getLocation().getY() + ":" + warp.getLocation().getZ());
//						}
//					}else{
//						sender.sendMessage("No warps are set yet");
//						return true;
//					}
//				}else if(args[0].equalsIgnoreCase("listservers")) {
//					if (ConfigLoader.servers != null) {
//						for (ServerTP server : ConfigLoader.servers.values()) {
//							sender.sendMessage(server.getId() +  ": " + server.getName() + ".");
//						}
//					}
//				}
						
			}else if (args.length == 2) {
				//If there are two arguments "/ww <arg[0]> <arg[1]>"
				
				if (args[0].equalsIgnoreCase("create")) {
					//If arg[0] is create "/ww create <arg[1]>"
					
					if (sender instanceof Player) { 
						//If sender is a player
						
						Player player = (Player) sender;
						
						
						String warpName = args[1];
						//Check to see if warp already exists
						
						if (ConfigLoader.warps.containsKey(warpName)) {
							
							sender.sendMessage(Main.prefix + Main.msg("warpAlreadyExists"));
							return true;
						}
						
						if (Main.menuConfig.contains("warps")) {
							for (String key : Main.menuConfig.getConfigurationSection("warps").getKeys(false)) {
								String name = Main.menuConfig.getString("warps." + key + ".name");
								
								if (name.equals(warpName)) {
									sender.sendMessage(Main.prefix + Main.msg("warpAlreadyExists"));
									return true;
								}
							}
						}
						
						
						
						int nextIndex = ConfigLoader.getNextWarpId();
						
						Main.menuConfig.set("warps." + nextIndex + ".name", warpName);
						Main.menuConfig.set("warps." + nextIndex + ".location", Main.serverName + ":" + ConfigLoader.serializeLocation(player.getLocation()));
						Main.menuConfig.set("warps." + nextIndex + ".enabled", false);
						
						//Create Place Holders
						Main.menuConfig.set("warps." + nextIndex + ".slot", 0);
						Main.menuConfig.set("warps." + nextIndex + ".material", "341:0");
						Main.menuConfig.set("warps." + nextIndex + ".enchantment", "null");
						Main.menuConfig.set("warps." + nextIndex + ".quantity", 1);
						Main.menuConfig.set("warps." + nextIndex + ".lore", Arrays.asList(""));
						Main.menuConfig.set("warps." + nextIndex + ".enableCommands", true);
						Main.menuConfig.set("warps." + nextIndex + ".commands", Arrays.asList(""));
						Main.menuConfig.set("warps." + nextIndex + ".sendEffect", "SPIRAL");
						Main.menuConfig.set("warps." + nextIndex + ".receiveEffect", "EMERALD");
						Main.saveMenuConfig();
						
						player.sendMessage(Main.prefix + Main.msg("createdWarp"));
						return true;
					}else{
						//Sender is not a player
						
						notPlayer(sender);
						return true;
					}
				}else if (args[0].equalsIgnoreCase("enable")) {
					// If args[0] is enable "/ww enable <arg[1]>"
					String warpName = args[1];
					
					if (ConfigLoader.containsWarpName(warpName)) { //If the warp name is already loaded into the plugin
						sender.sendMessage(Main.prefix + Main.msg("warpAlreadyEnabled"));
						return true;
					} //Otherwise, create the new warp
					
					if (Main.menuConfig.contains("warps")) {
						for (String key : Main.menuConfig.getConfigurationSection("warps").getKeys(false)) {
							//Iterating though all warp keys in config
							
							if (Main.menuConfig.getConfigurationSection("warps." + key).contains("name")) {
								//Configuration Check
								
								if (Main.menuConfig.getString("warps." + key + ".name").equals(warpName)) {
									//Determine Warp
									
									if (Main.menuConfig.getConfigurationSection("warps." + key).contains("enabled")){
										//Configuration Check
										
										if (Main.menuConfig.getBoolean("warps." + key + ".enabled")) { //Is enabled
											sender.sendMessage(Main.prefix + Main.msg("warpAlreadyEnabled"));
											return true;
										}else{ //Is disabled
											
											if (ConfigLoader.warpHasRequirements("warps." + key)) { //Check to see if it should be enabled
												
												//Add it to current loaded warps
												ConfigLoader.addWarp(key);
												
												//Set enabled to true in config
												Main.menuConfig.set("warps." + key + ".enabled", true);
												Main.saveMenuConfig();
												
												sender.sendMessage(Main.prefix + Main.msg("enabledWarp"));
											}
											
										}
									}
								}
							}
						}
						
						return true;
					}else{ //Couldn't find any warps
						sender.sendMessage(Main.prefix + Main.msg("noWarpsSaved"));
						return true;
					}
				}
//				else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
//					String warpName = args[1];
//					
//					if (ConfigLoader.warps.containsKey(warpName)) {
//						
//						if (sender instanceof Player) {
//							Player player = (Player) sender;
//							
//							Main.centeredTP(player, ConfigLoader.warps.get(warpName).getLocation());
//						}else{
//							notPlayer(sender);
//							return true;
//						}
//						
//					}else{
//						sender.sendMessage("No warp by the name of: " + warpName);
//						return true;
//					}
//				}else if (args[0].equalsIgnoreCase("send")) {
//					String serverName = args[1];
//					
//					if (sender instanceof Player) {
//						Player player = (Player) sender;
//						Main.sendToSever(player, serverName);
//					}else{
//						sender.sendMessage("You must be a player to perform this command.");
//					}
//				}
			}
		}
		
		sendHelpMessage(sender);
		return true;
	}
	
	/** Sends the plugin's Help Message to sender
	 * 
	 *  @param sender The sender to send the message to
	 */
	public void sendHelpMessage(CommandSender sender) {
//		 * /ww                      |   Default command for Whewheo. Shows the help screen.
//		 * /ww create <warpName>    |   Creates a new warp with the players current location.
//		 * /ww enable <warpName>    |   Enables a disabled warp if all of the requirements are met.
//		 * /ww reload               |   Reloads data from the config and menu files. Also resets items and inventories.
		
		sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.BLUE + "Whewheo Commands" + ChatColor.AQUA + "]");
		
		sender.sendMessage(ChatColor.GREEN + "/ww" + ChatColor.WHITE + " | " + ChatColor.YELLOW + "Default command for Whewheo. Shows the help screen.");
		
		if (sender.hasPermission(Main.createWarp))
			sender.sendMessage(ChatColor.GREEN + "/ww create <warpName>" + ChatColor.WHITE + " | " + ChatColor.YELLOW + "Creates a new warp with the players current location.");
		
		if (sender.hasPermission(Main.enableWarp))
			sender.sendMessage(ChatColor.GREEN + "/ww enable <warpName>" + ChatColor.WHITE + " | " + ChatColor.YELLOW + "Enables a disabled warp if all of the requirements are met.");
		
		if (sender.hasPermission(Main.reload))
			sender.sendMessage(ChatColor.GREEN + "/ww reload" + ChatColor.WHITE + " | " + ChatColor.YELLOW + "Reloads data from the config and menu files. Also resets items and inventories.");
	}
	
	public void notPlayer(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You must be a player to perform this commands.");
	}
}