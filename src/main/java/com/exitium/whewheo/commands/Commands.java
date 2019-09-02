package com.exitium.whewheo.commands;

import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandExecutor class for all Whewheo commands.
 * 
 * /ww | Default command for Whewheo. Shows the help screen. /ww create
 * <warpName> | Creates a new warp with the players current location. /ww enable
 * <warpName> | Enables a disabled warp if all of the requirements are met. /ww
 * reload | Reloads data from the config and menu files. Also resets items and
 * inventories.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */

public class Commands implements CommandExecutor {

	private ConfigLoader configLoader;
	private ServerSelectionHandler serverSel;

	public Commands(ConfigLoader configLoader, ServerSelectionHandler serverSel) {
		this.configLoader = configLoader;
		this.serverSel = serverSel;
	}

	/** Method that handles the /ww command */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("ww")) {
			// If the command starts with /ww

			if (args.length == 0) {
				// If there are no other arguments "/ww"

				sendHelpMessage(sender);
				return true;
			} else if (args.length == 1) {
				// If there is one argument "/ww <arg[0]>"

				if (args[0].equalsIgnoreCase("create")) {
					// If command is "/ww create"

					sender.sendMessage(ChatColor.RED + "Usage: /ww create <warp>");
					return true;
				} else if (args[0].equalsIgnoreCase("reload")) {
					// Reload All Information
					this.configLoader.reload();

					serverSel.init();
					sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("reloadedConfig"));
					return true;
				}

			} else if (args.length == 2) {
				// If there are two arguments "/ww <arg[0]> <arg[1]>"

				if (args[0].equalsIgnoreCase("create")) {
					// If arg[0] is create "/ww create <arg[1]>"

					if (sender instanceof Player) {
						// If sender is a player

						Player player = (Player) sender;

						String warpName = args[1];
						// Check to see if warp already exists

						if (this.configLoader.getWarps().containsKey(warpName)) {

							sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("warpAlreadyExists"));
							return true;
						}

						if (this.configLoader.getMenuConfig().contains("warps")) {
							for (String key : this.configLoader.getMenuConfig().getConfigurationSection("warps").getKeys(false)) {
								String name = this.configLoader.getMenuConfig().getString("warps." + key + ".name");

								if (name.equals(warpName)) {
									sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("warpAlreadyExists"));
									return true;
								}
							}
						}

						this.configLoader.saveDefaultWarp(warpName, player);

						player.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("createdWarp"));
						return true;
					} else {
						// Sender is not a player

						notPlayer(sender);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("enable")) {
					// If args[0] is enable "/ww enable <arg[1]>"
					String warpName = args[1];

					if (this.configLoader.containsWarpName(warpName)) { // If the warp name is already loaded into the plugin
						sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("warpAlreadyEnabled"));
						return true;
					} // Otherwise, create the new warp

					if (this.configLoader.getMenuConfig().contains("warps")) {
						for (String key : this.configLoader.getMenuConfig().getConfigurationSection("warps").getKeys(false)) {
							// Iterating though all warp keys in config

							if (this.configLoader.getMenuConfig().getConfigurationSection("warps." + key).contains("name")) {
								// Configuration Check

								if (this.configLoader.getMenuConfig().getString("warps." + key + ".name").equals(warpName)) {
									// Determine Warp

									if (this.configLoader.getMenuConfig().getConfigurationSection("warps." + key).contains("enabled")) {
										// Configuration Check

										if (this.configLoader.getMenuConfig().getBoolean("warps." + key + ".enabled")) { // Is enabled
											sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("warpAlreadyEnabled"));
											return true;
										} else { // Is disabled

											if (this.configLoader.warpHasRequirements("warps." + key)) { // Check to see if
																									// it should be
																									// enabled
												this.configLoader.reload();

												// Add it to current loaded warps
												this.configLoader.addWarp(key);

												// Set enabled to true in config
												this.configLoader.getMenuConfig().set("warps." + key + ".enabled", true);
												this.configLoader.saveMenuConfig();
													
												this.serverSel.setupItems();
												this.serverSel.setupInventories();

												sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("enabledWarp"));
											}

										}
									}
								}
							}
						}

						return true;
					} else { // Couldn't find any warps
						sender.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("noWarpsSaved"));
						return true;
					}
				}
			}
		}

		sendHelpMessage(sender);
		return true;
	}

	/**
	 * Sends the plugin's Help Message to sender
	 * 
	 * @param sender The sender to send the message to
	 */
	public void sendHelpMessage(CommandSender sender) {
		// * /ww | Default command for Whewheo. Shows the help screen.
		// * /ww create <warpName> | Creates a new warp with the players current
		// location.
		// * /ww enable <warpName> | Enables a disabled warp if all of the requirements
		// are met.
		// * /ww reload | Reloads data from the config and menu files. Also resets items
		// and inventories.

		sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.BLUE + "Whewheo Commands" + ChatColor.AQUA + "]");

		sender.sendMessage(ChatColor.GREEN + "/ww" + ChatColor.WHITE + " | " + ChatColor.YELLOW
				+ "Default command for Whewheo. Shows the help screen.");

		if (sender.hasPermission(Util.createWarp))
			sender.sendMessage(ChatColor.GREEN + "/ww create <warpName>" + ChatColor.WHITE + " | " + ChatColor.YELLOW
					+ "Creates a new warp with the players current location.");

		if (sender.hasPermission(Util.enableWarp))
			sender.sendMessage(ChatColor.GREEN + "/ww enable <warpName>" + ChatColor.WHITE + " | " + ChatColor.YELLOW
					+ "Enables a disabled warp if all of the requirements are met.");

		if (sender.hasPermission(Util.reload))
			sender.sendMessage(ChatColor.GREEN + "/ww reload" + ChatColor.WHITE + " | " + ChatColor.YELLOW
					+ "Reloads data from the config and menu files. Also resets items and inventories.");
	}

	public void notPlayer(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You must be a player to perform this commands.");
	}
}