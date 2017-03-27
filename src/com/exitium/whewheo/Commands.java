package com.exitium.whewheo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

/**
 * CommandExecutor class for all Whewheo commands.
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
					
					Main.saveMenuConfig();
					sender.sendMessage("Successfully reloaded config");
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
						
						Main.menuConfig.set("warps." + nextIndex + ".Name", warpName);
						Main.menuConfig.set("warps." + nextIndex + ".Location", ConfigLoader.serializeLocation(player.getLocation()));
						Main.menuConfig.set("warps." + nextIndex + ".Enabled", false);
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
							if (Main.menuConfig.getConfigurationSection("warps." + key).contains("Name")) {
								if (Main.menuConfig.getString("warps." + key + ".Name").equals(warpName)) {
									if (Main.menuConfig.getConfigurationSection("warps." + key).contains("Enabled")) {
										if (Main.menuConfig.getBoolean("warps." + key + ".Enabled")) {
											sender.sendMessage("This warp is already enabled!");
											return true;
										}else{
											
											if (ConfigLoader.hasRequirments("warps." + key)) {
												ConfigLoader.addWarp("warps." + key);
												Main.menuConfig.set("warps." + key + ".Enabled", true);
												Main.saveMenuConfig();
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
				}
			}
		}
		//TODO: Send Help Message
		return true;
	}
	
	public void notPlayer(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You must be a player to perform this commands.");
	}
}