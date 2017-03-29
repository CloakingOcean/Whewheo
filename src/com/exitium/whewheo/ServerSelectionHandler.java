package com.exitium.whewheo;

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
	

	private static ItemStack bluePanel;
	
	private static Inventory servers;
	//Items
		private static ItemStack warpSelector;
	
	private static Inventory warps;
	//Items
		private static ItemStack serverSelector;
	
	

		
		
	// Links the itemstack to the server name
	private static HashMap<ItemStack, ServerTP> serverItems;
	private static HashMap<ItemStack, WarpTP> warpItems;
	
	
	public static List<String> teleportingPlayers;
	
	
	
	
	
	public ServerSelectionHandler() {
		init();
	}
	
	public static void init() {
		serverItems = new HashMap<ItemStack, ServerTP>();
		warpItems = new HashMap<ItemStack, WarpTP>();
		
		teleportingPlayers = new ArrayList<String>();
		
		setupItems();
		setupInventories();
	}
	
	
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
				
				serverSelectorMeta.setLore(lore);
				
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
				warpSelectorMeta.setLore(lore);
				
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
					
//					serverItemMeta.setLore(server.getLore());
					List<String> lore = new ArrayList<String>();
					for (String l: server.getLore()) {
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
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Main.serverName == null) {
			Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.instance, "BungeeCord");
			Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Main.instance, "BungeeCord", Main.instance);
			
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer");
			
			event.getPlayer().sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
		}
		
		
		
		
		if (!event.getPlayer().getInventory().contains(serverSelector)) {
			if (event.getPlayer().getInventory().firstEmpty() != -1) {
				event.getPlayer().getInventory().addItem(serverSelector);
			}else{
				event.getPlayer().sendMessage("Your inventory is too full to recieve the server selector");
			}
		}
	}
	
	@EventHandler
	public void onPlayerInventoryInteract(InventoryClickEvent event) {
//		if (event.getCurrentItem().equals(serverSelector)) {
//			event.setCancelled(true);
//			((Player) event.getWhoClicked()).openInventory(servers);
//		}
		
		Inventory inventory = event.getInventory();
		if (inventory.equals(servers) || inventory.equals(warps)) {
			Player player = (Player) event.getWhoClicked();
			
			
			event.setCancelled(true);
			if (event.getCurrentItem().equals(warpSelector)) {
				((Player) event.getWhoClicked()).openInventory(warps);
			}else if (event.getCurrentItem().equals(serverSelector)) {
				((Player) event.getWhoClicked()).openInventory(servers);
			}else if (serverItems.containsValue(event.getCurrentItem())) {
				Bukkit.broadcastMessage("Found Server Item");
				
				
				
				new ParticleGenerator(player, serverItems.get(event.getCurrentItem()));
				
			}else if (warpItems.containsValue(event.getCurrentItem())) {
				WarpTP warp = warpItems.get(event.getCurrentItem());
						
						//Add Effects Later
						
						//Add player to a cooldown waiting map. If they move again, take them away from the map.
						
						
						if (teleportingPlayers.contains(player.getUniqueId().toString())) {
							teleportingPlayers.remove(player.getUniqueId().toString());
						}
						
						
						teleportingPlayers.add(player.getUniqueId().toString());
						
						
						//TODO: Handle Delays to make it work right
						ParticleGenerator pg = new ParticleGenerator(player, warp);
						int threadId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.instance, pg, (long) 0, (long) 20L);
						pg.setThreadId(threadId);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (teleportingPlayers.contains(event.getPlayer().getUniqueId().toString())) {
			if (!(event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ())) {
				Bukkit.broadcastMessage("teleporting players contains!!");
				event.getPlayer().sendMessage("Teleporation cancelled.");
				teleportingPlayers.remove(event.getPlayer().getUniqueId().toString());
			}
		}
	}
	
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
								Bukkit.broadcastMessage("Opening Server Selector Inventory");
								
								event.getPlayer().openInventory(servers);
							}
						}
						
						if (Main.config.getBoolean("serverSelector.leftClick")) {
							if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
								event.setCancelled(true);
								Bukkit.broadcastMessage("Opening Server Selector Inventory");
								
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
	
	@EventHandler
	public void onPlayerThrowItemEvent(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(serverSelector)) { 
		  event.setCancelled(true);
		  Bukkit.broadcastMessage("Cancelled");
		}
	}
}