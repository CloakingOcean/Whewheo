package com.exitium.whewheo.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.particles.send.generators.NetherPortal;
import com.exitium.whewheo.particles.send.generators.Spiral;
import com.exitium.whewheo.teleportobjects.ServerTP;
import com.exitium.whewheo.teleportobjects.WarpTP;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;

/**
 * Handles all GUI interaction for the plugin. Also sets up ItemStacks and inventories.
 * 
 * @author Cloaking_Ocean
 * @date Mar 27, 2017
 * @version 1.0
 */
public class ServerSelectionHandler implements Listener {
	
	
	/*
	 * TODO: PERHAPS MOVE ALL UNRELATED LISTENRS TO A SEPARATE CLASS FOR ORGANIZATIONAL PURPOSES.
	 */

	private static ItemStack bluePanel;
	
	public static Inventory servers;
	//Items
		private static ItemStack warpSelector;
	
	private static Inventory warps;
	//Items
		private static ItemStack serverSelector;
	
	

		
		
	// Links the itemstack to the server name
	public static HashMap<ItemStack, ServerTP> serverItems;
	private static HashMap<ItemStack, WarpTP> warpItems;
	
	
	public static List<String> teleportingPlayers;
	
	
	
	
	
	public ServerSelectionHandler() {
		init();
	}
	
	/** Initializes the components for the ServerSelectionHandler class*/
	public static void init() {
		serverItems = new HashMap<ItemStack, ServerTP>();
		warpItems = new HashMap<ItemStack, WarpTP>();
		
		teleportingPlayers = new ArrayList<String>();
		
		setupItems();
		setupInventories();
	}
	
	/** Creates items from the loaded warps and servers objects*/
	public static void setupItems() {
		if (Main.config.contains("serverSelector")) {
			if (Main.config.getConfigurationSection("serverSelector").contains("name") 
					&& Main.config.getConfigurationSection("serverSelector").contains("material")
					&& Main.config.getConfigurationSection("serverSelector").contains("enchantment")
					&& Main.config.getConfigurationSection("serverSelector").contains("quantity")
					&& Main.config.getConfigurationSection("serverSelector").contains("lore")) {
				
				
				serverSelector = ConfigLoader.getItemStackFromId(Main.config.getString("serverSelector.material"), Main.config.getInt("serverSelector.quantity"));
				ItemMeta serverSelectorMeta = serverSelector.getItemMeta();
				serverSelectorMeta.setDisplayName(ConfigLoader.getColoredTextFromConfig("serverSelector.name"));
				if (!Main.config.getString("serverSelector.enchantment").equals("null"))
				serverSelectorMeta.addEnchant(Enchantment.getByName(Main.config.getString("serverSelector.enchantment")), 1, false);
//				serverSelectorMeta.setLore(Main.config.getStringList("serverSelector.lore"));
				
				List<String> lore = new ArrayList<String>();
				for (String l: Main.config.getStringList("serverSelector.lore")) {
					if (l.contains("&")) {
						lore.add(ChatColor.translateAlternateColorCodes('&', l));
					}else{
						lore.add(l);
					}
				}
				
				if (!lore.equals(Arrays.asList(""))) {
					serverSelectorMeta.setLore(lore);
				}
				
				serverSelector.setItemMeta(serverSelectorMeta);
				
				
			}else{
				Bukkit.getServer().getLogger().severe("Couldn't load Server Selector. Missing Requirments");
			}
		}else{
			Bukkit.getServer().getLogger().severe("Couldn't load Server Selector. No serverSelector section found");
		}
		
		if (Main.config.contains("warpSelector")) {
			if (Main.config.getConfigurationSection("warpSelector").contains("name") 
					&& Main.config.getConfigurationSection("warpSelector").contains("material")
					&& Main.config.getConfigurationSection("warpSelector").contains("enchantment")
					&& Main.config.getConfigurationSection("warpSelector").contains("quantity")
					&& Main.config.getConfigurationSection("warpSelector").contains("lore")) {
				
				warpSelector = ConfigLoader.getItemStackFromId(Main.config.getString("warpSelector.material"), Main.config.getInt("warpSelector.quantity"));
				ItemMeta warpSelectorMeta = warpSelector.getItemMeta();
				warpSelectorMeta.setDisplayName(ConfigLoader.getColoredTextFromConfig("warpSelector.name"));
				if (!Main.config.getString("warpSelector.enchantment").equals("null"))
				warpSelectorMeta.addEnchant(Enchantment.getByName(Main.config.getString("warpSelector.enchantment")), 1, false);
//				warpSelectorMeta.setLore(Main.config.getStringList("warpSelector.lore"));

				List<String> lore = new ArrayList<String>();
				for (String l: Main.config.getStringList("warpSelector.lore")) {
					if (l.contains("&")) {
						lore.add(ChatColor.translateAlternateColorCodes('&', l));
					}else{
						lore.add(l);
					}
				}
				
				if (!lore.equals(Arrays.asList(""))) {
					warpSelectorMeta.setLore(lore);
				}
				
				warpSelector.setItemMeta(warpSelectorMeta);
			}
		}else{
			Bukkit.getServer().getLogger().severe("Couldn't load Server Selector. No serverSelector section found");
		}
		
		bluePanel = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
		ItemMeta bluePanelMeta = bluePanel.getItemMeta();
		bluePanelMeta.setDisplayName(" ");
		bluePanel.setItemMeta(bluePanelMeta);
	}
	
	/** Loads the created warps and servers items into the appropriate inventories*/
	public static void setupInventories() {
		
		if (Main.config.contains("serverMenu")) {
			if (Main.config.getConfigurationSection("serverMenu").contains("size") && Main.config.getConfigurationSection("serverMenu").contains("name")) {
				int size = Main.config.getInt("serverMenu.size");
				if (size >= 54) {
					servers = Bukkit.createInventory(null, 54, ConfigLoader.getColoredTextFromConfig("serverMenu.name"));
				}else{
					servers = Bukkit.createInventory(null, size + 9, ConfigLoader.getColoredTextFromConfig("serverMenu.name"));
				}
				
				
				
				
				for (ServerTP server : ConfigLoader.servers.values()) {
					
					
					ItemStack serverItem = ConfigLoader.getItemStackFromId(server.getMaterial(), server.getQuantity());
					ItemMeta serverItemMeta = serverItem.getItemMeta();
					if (server.getName().contains("&")) {
						serverItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', server.getName()));
					}else{
						serverItemMeta.setDisplayName(server.getName());
					}
					
					//TODO: Implement Place Holders: %count%
					
//					serverItemMeta.setLore(server.getLore());
					List<String> lore = new ArrayList<String>();
					for (String l : server.getLore()) {
						if (l.contains("&")) {
							lore.add(ChatColor.translateAlternateColorCodes('&', l));
						}else{
							lore.add(l);
						}
					}
					
					if (!lore.equals(Arrays.asList(""))) {
						serverItemMeta.setLore(lore);
					}
					
					if (server.getEnchantment() != null)
					serverItemMeta.addEnchant(server.getEnchantment(), 1, false);
					serverItem.setItemMeta(serverItemMeta);
					
					
					
					serverItems.put(serverItem, server);
					
					
					
					
					
					if (servers.getItem(server.getSlot()) != null) {
						Bukkit.getServer().getLogger().severe("Overriding server itemstacks. Slot: " + server.getSlot() + ". Previous: " + servers.getItem(server.getSlot()).getType().name() + ". Now: " + serverItem.getType().name());
					}
					
					servers.setItem(server.getSlot(), serverItem);
					
				}
				
				
				
				
				
				
				
				int lastIndex = (servers.getSize()-1);
				
				servers.setItem(lastIndex, warpSelector);
				for (int i = 1; i < 9; i++) {
					servers.setItem(lastIndex-i, bluePanel);
				}
				
			}else{
				Bukkit.getServer().getLogger().severe("Couldn't load Server Menu. No size or name found");
			}
		}else{
			Bukkit.getServer().getLogger().severe("Couldn't load Server Menu. No serverMenu found");
		}
		
		//Use to be Warps Inventory
		if (Main.config.contains("warpMenu")) {
			if (Main.config.getConfigurationSection("warpMenu").contains("size") && Main.config.getConfigurationSection("warpMenu").contains("name")) {
				warps = Bukkit.createInventory(null, Main.config.getInt("warpMenu.size"), ConfigLoader.getColoredTextFromConfig("warpMenu.name"));
				
				for (WarpTP warp : ConfigLoader.warps.values()) {
					
					/*
					 * Has been disabled because of the assumption that
					 * each server has a config.yml specific to the warps needed.
					 */
//					if (warp.getServer().equals(Main.serverName)) { 
						
						ItemStack warpItem = ConfigLoader.getItemStackFromId(warp.getMaterial(), warp.getQuantity());
						ItemMeta warpItemMeta = warpItem.getItemMeta();
						if (warp.getName().contains("&")) {
							warpItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', warp.getName()));
						}else{
							warpItemMeta.setDisplayName(warp.getName());
						}
						
	//					warpItemMeta.setLore(warp.getLore());
						List<String> lore = new ArrayList<String>();
						for (String l: warp.getLore()) {
							if (l.contains("&")) {
								lore.add(ChatColor.translateAlternateColorCodes('&', l));
							}else{
								lore.add(l);
							}
						}
						if (!lore.equals(Arrays.asList(""))) {
							warpItemMeta.setLore(lore);
						}
						
						if (warp.getEnchantment() != null)
						warpItemMeta.addEnchant(warp.getEnchantment(), 1, false);
						warpItem.setItemMeta(warpItemMeta);
						
						
						warpItems.put(warpItem, warp);
						
						
						if (warps.getItem(warp.getSlot()) != null) {
							Bukkit.getServer().getLogger().severe("Overriding warp itemstacks. Slot: " + warp.getSlot() + ". Previous: " + warps.getItem(warp.getSlot()).getType().name() + ". Now: " + warpItem.getType().name());
						}
						
						warps.setItem(warp.getSlot(), warpItem);
						
//					}else{
//						//Debugging Message
//						Bukkit.getServer().getLogger().severe("Couldn't load warp configured for server: " + warp.getServer());
//						Bukkit.getServer().getLogger().severe("Current Server: " + Main.serverName);
//					}
				}
				
				
				
				
				
				
				
				int lastIndex = (warps.getSize()-1);
				
				warps.setItem(lastIndex-8, serverSelector);
				for (int i = 0; i < 8; i++) {
					warps.setItem(lastIndex-i, bluePanel);
				}
				
				
				
				
				
				
			}else{
				Bukkit.getServer().getLogger().severe("Couldn't load Warp Menu. No size or name found");
			}
		}else {
			Bukkit.getServer().getLogger().severe("Couldn't load Warp Menu. No warpMenu found");
		}
	}
	
	/** Runs when a player joins the server
	 * 	Attempts to give the player a serverSelector. If the desired slot has an item
	 *  and there is no empty space to put the item, drop the item in the desired slot, and give the player the serverselector
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if (Main.serverName == null) {

			ServerNameGetter sng = new ServerNameGetter(event.getPlayer());
			int threadId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.instance, sng, (long) 0, (long) 1L);
			sng.setThreadId(threadId);
		}
		
		
		
		if (Main.config.contains("serverSelector")) {
			
			if (Main.config.getConfigurationSection("serverSelector").contains("itemOnJoin") && 
				Main.config.getConfigurationSection("serverSelector").contains("slot")) {
				
				if (Main.config.getBoolean("serverSelector.itemOnJoin")) {
					//Config Checks
					
					
					if (!event.getPlayer().getInventory().contains(serverSelector)) { //If the player doesn't have the serverSelector Item already
						
						
						Inventory pInv = event.getPlayer().getInventory();
						int slot = Main.config.getInt("serverSelector.slot");
						ItemStack itemInSlot = pInv.getItem(slot);
						
						
						if (itemInSlot != null) {
							if (pInv.firstEmpty() == -1) {
//								//Player's Inventory is Full. Drop the other item.
//								pInv.setItem(slot, serverSelector);
//								event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), itemInSlot);
								
								
								
								//Perhaps instead just ignore people with full inventories, They probably should already have a compass. This will probably only happen 
								//if someone /clear 's their inventory, and if so they're probably admin and know how to get the compass back.
							}else{
								//Player's Inventory is not Full. Put the item in their inventory in the open slot and put the server selector in the appropriate slot.

								
								pInv.setItem(pInv.firstEmpty(), itemInSlot);
								
								pInv.setItem(slot, serverSelector);
							}
							
							
						}else{
							pInv.setItem(slot, serverSelector);
						}
					}
				}
			}else{
				Bukkit.getServer().getLogger().severe("Couldn't find itemOnJoin or slot options for serverSelector in config!");
			}
		}else{
			Bukkit.getServer().getLogger().severe("Couldn't find serverSelector");
		}
	}
	
	/** Runs when a player clicks in an inventory
	 *  Handles all interaction inside of a custom inventory
	 */
	@EventHandler
	public void onPlayerInventoryInteract(InventoryClickEvent event) {
		
		
		Inventory inventory = event.getInventory();
		if (inventory.equals(servers) || inventory.equals(warps)) {
			Player player = (Player) event.getWhoClicked();
			
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().equals(warpSelector)) {
					((Player) event.getWhoClicked()).openInventory(warps);
				}else if (event.getCurrentItem().equals(serverSelector)) {
					((Player) event.getWhoClicked()).openInventory(servers);
				}else if (serverItems.containsKey(event.getCurrentItem())) {
					
					ServerTP server = serverItems.get(event.getCurrentItem());
					
					
					if (!server.getServer().equals(Main.serverName)) {
						
							if (server.getCommands() != null) {
								if (server.getCommands().isEmpty() == false) {
									for (String command : server.getCommands()) {
										if (command.contains("%player")) {
											command = command.replace("%player%", player.getName());
										}
										
										if (command.startsWith("p:")) {
											command = command.substring(2);
											Bukkit.getServer().dispatchCommand(player, command);
										}else if (command.startsWith("s:")) {
											command = command.substring(2);
											Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
										}else{
											Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
										}
									}
								}
							}
						
						
						teleportingPlayers.add(player.getUniqueId().toString());
						
						player.closeInventory();
						
//						new Spiral(player, server).runTaskTimer(Main.instance, 0, 1);
						ParticleGenerator generator = getGenerator(player, server);
						
						generator.runTaskTimer(Main.instance, 0, generator.getTickDelay());
						
					}else{
						player.sendMessage(Main.prefix + Main.msg("alreadyConnected"));
					}
				}else if (warpItems.containsKey(event.getCurrentItem())) {
					WarpTP warp = warpItems.get(event.getCurrentItem());
					if (warp.getCommands() != null) {
						if (warp.getCommands().isEmpty() == false) {
							for (String command : warp.getCommands()) {
								if (!command.equals("")) {
									
									
									if (command.contains("%player")) {
										command = command.replace("%player%", player.getName());
									}
									
									if (command.startsWith("p:")) {
										command = command.substring(2);
										Bukkit.getServer().dispatchCommand(player, command);
									}else if (command.startsWith("s:")) {
										command = command.substring(2);
										Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
									}else{
										Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
									}
								}
							}
						}
					}
					
					teleportingPlayers.add(player.getUniqueId().toString());
					
					player.closeInventory();
					
					
					//TODO: Handle Delays to make it work right
					ParticleGenerator generator = getGenerator(player, warp);
					
					generator.runTaskTimer(Main.instance, 0, generator.getTickDelay());
				}
			}
		}else{
			if (event.getCurrentItem().equals(serverSelector)) {
				event.setCancelled(true);
			}
		}
	}
	
	/** Runs when a player moves
	 *  Cancels teleportations when a player moves.
	 */
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (teleportingPlayers.contains(event.getPlayer().getUniqueId().toString())) {
			if (!(event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ() && event.getTo().getBlockY() == event.getFrom().getBlockY())) {
				event.getPlayer().sendMessage(Main.prefix + Main.msg("teleportationCancelled"));
				teleportingPlayers.remove(event.getPlayer().getUniqueId().toString());
			}
		}
	}
	
	/** Runs when a player interacts with anything.
	 *  Handles the serverSelection Item.
	 */
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null) {
			if (event.getItem().equals(serverSelector)) {
				

				
				Action action = event.getAction();
				
				
				if(Main.config.contains("serverSelector")) {
					if (Main.config.getConfigurationSection("serverSelector").contains("rightClick") &&
							Main.config.getConfigurationSection("serverSelector").contains("leftClick")) {
						
						if (Main.config.getBoolean("serverSelector.rightClick")) {
							if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
								event.setCancelled(true);
								
								updatePlaceHolders(event.getPlayer());
								
								event.getPlayer().openInventory(servers);
							}
						}
						
						if (Main.config.getBoolean("serverSelector.leftClick")) {
							if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
								event.setCancelled(true);								
								updatePlaceHolders(event.getPlayer());
								
								event.getPlayer().openInventory(servers);
							}
						}
						
						
						
					}else{
						Bukkit.getServer().getLogger().severe("Couldn't find right or left click option in config for serverSelector");
					}
					
				}else{
					Bukkit.getServer().getLogger().severe("Couldn't find serverSelector in config!");
				}

			}
		}
	}
	
	/** Runs when the player drops an item.
	 *  Prevents the serverSelector from being dropped.
	 */
	@EventHandler
	public void onPlayerThrowItemEvent(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(serverSelector)) { 
		  event.setCancelled(true);
		}
	}

	/** Updates the Lore of items and also requests the player count of a specified server.*/
	public static void updatePlaceHolders(Player player) {
		for (ServerTP server : ConfigLoader.servers.values()) {
			
			boolean containsPlaceHolders = false;
			
			for (String s : server.getLore()) {
				if (s.contains("%count%") || s.contains("%player%")) {
					containsPlaceHolders = true;
				}
			}
			
			if (containsPlaceHolders) {
				List<String> temporaryLore = new ArrayList<String>();
				
				for (String lore : server.getLore()) {
					temporaryLore.add(lore.replace("%player%", player.getName()));
				}
				
				server.setLore(temporaryLore);
			}
			
			requestPlayerCount(server.getServer(), player);
		}
		
	}
	
	/** Sends Bungeecord a request to get the playercount of a specific server*/
	public static void requestPlayerCount(String serverName, Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		try {
			out.writeUTF("PlayerCount");
			out.writeUTF(serverName);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
		
		
	}
	
	/** Gets a loaded server item from the name of the server*/
	public static ItemStack getServerItemFromName(String name) {
		for (ItemStack item : serverItems.keySet()) {
			if (serverItems.get(item).getServer().equals(name)) {
				return item;
			}
		}
		return null;
	}
	
	/** Gets the ServerTP object from the name of the server*/
	public static ServerTP getServerFromName(String name) {
		for (ServerTP server : serverItems.values()) {
			if (server.getServer().equals(name)) {
				return server;
			}
		}
		return null;
	}
	
	/** Gets the Send generator for a server*/
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
	
	/** Gets the Receive generator for a warp*/
	public SendParticleGenerator getGenerator(Player player, WarpTP warp) {
		
		switch(warp.getSend()) {
			case SPIRAL:
				return new Spiral(player, warp);
			case NETHER_PORTAL:
				return new NetherPortal(player, warp);
			default:
				Bukkit.getServer().getLogger().severe("Couldn't determine matching ValidSendGenerators. Contact Developer!");
				return new Spiral(player, warp);
		}
	}
}