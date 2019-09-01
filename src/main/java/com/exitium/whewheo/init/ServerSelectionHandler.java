package com.exitium.whewheo.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.receive.ReceiveParticleGenerator;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.teleportobjects.WarpTP;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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

/**
 * Handles all GUI interaction for the plugin. Also sets up ItemStacks and
 * inventories.
 * 
 * @author Cloaking_Ocean date Mar 27, 2017
 * @version 1.0
 */
public class ServerSelectionHandler implements Listener {

	/*
	 * TODO: PERHAPS MOVE ALL UNRELATED LISTENRS TO A SEPARATE CLASS FOR
	 * ORGANIZATIONAL PURPOSES.
	 */

	public static Inventory warps;
	// Items
	private static ItemStack warpSelector;

	// Links the itemstack to the server name
	public static HashMap<ItemStack, WarpTP> warpItems;

	public static List<String> teleportingPlayers;

	public ServerSelectionHandler(HashMap<String, WarpTP> warps) {
		init();
	}

	/** Initializes the components for the ServerSelectionHandler class */
	public static void init() {
		warpItems = new HashMap<ItemStack, WarpTP>();

		teleportingPlayers = new ArrayList<String>();

		setupItems();
		setupInventories();
	}

	/** Creates items from the loaded warps and servers objects */
	public static void setupItems() {
		if (Main.config.contains("warpSelector")) {
			if (Main.config.getConfigurationSection("warpSelector").contains("name")
					&& Main.config.getConfigurationSection("warpSelector").contains("material")
					&& Main.config.getConfigurationSection("warpSelector").contains("enchantment")
					&& Main.config.getConfigurationSection("warpSelector").contains("quantity")
					&& Main.config.getConfigurationSection("warpSelector").contains("lore")) {

				warpSelector = ConfigLoader.getItemStackFromId(Main.config.getString("warpSelector.material"),
						Main.config.getInt("warpSelector.quantity"));
				ItemMeta warpSelectorMeta = warpSelector.getItemMeta();
				warpSelectorMeta.setDisplayName(ConfigLoader.getColoredTextFromConfig("warpSelector.name"));
				if (!Main.config.getString("warpSelector.enchantment").equals("null"))
					warpSelectorMeta.addEnchant(
							Enchantment.getByName(Main.config.getString("warpSelector.enchantment")), 1, false);

				List<String> lore = new ArrayList<String>();
				for (String l : Main.config.getStringList("warpSelector.lore")) {
					if (l.contains("&")) {
						lore.add(ChatColor.translateAlternateColorCodes('&', l));
					} else {
						lore.add(l);
					}
				}

				if (!lore.equals(Arrays.asList(""))) {
					warpSelectorMeta.setLore(lore);
				}

				warpSelector.setItemMeta(warpSelectorMeta);

			} else {
				Bukkit.getServer().getLogger().severe("Couldn't load Warp Selector. Missing Requirments");
			}
		} else {
			Bukkit.getServer().getLogger().severe("Couldn't load Warp Selector. No serverSelector section found");
		}
	}

	/** Loads the created warps items into the appropriate inventories */
	public static void setupInventories() {

		if (Main.config.contains("warpMenu")) {
			if (Main.config.getConfigurationSection("warpMenu").contains("size")
					&& Main.config.getConfigurationSection("warpMenu").contains("name")) {
				int size = Main.config.getInt("warpMenu.size");
				if (size > 54)
					size = 54;
				warps = Bukkit.createInventory(null, size, ConfigLoader.getColoredTextFromConfig("warpMenu.name"));

				for (WarpTP warp : ConfigLoader.warps.values()) {

					ItemStack warpItem = ConfigLoader.getItemStackFromId(warp.getMaterial(), warp.getQuantity());
					ItemMeta warpItemMeta = warpItem.getItemMeta();
					if (warp.getName().contains("&")) {
						warpItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', warp.getName()));
					} else {
						warpItemMeta.setDisplayName(warp.getName());
					}

					// TODO: Implement Place Holders: %count%

					// warpItemMeta.setLore(warp.getLore());
					List<String> lore = new ArrayList<String>();
					for (String l : warp.getLore()) {
						if (l.contains("&")) {
							lore.add(ChatColor.translateAlternateColorCodes('&', l));
						} else {
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
						Bukkit.getServer().getLogger()
								.severe("Overriding warp itemstacks. Slot: " + warp.getSlot() + ". Previous: "
										+ warps.getItem(warp.getSlot()).getType().name() + ". Now: "
										+ warpItem.getType().name());
					}

					warps.setItem(warp.getSlot(), warpItem);

				}
			} else {
				Bukkit.getServer().getLogger().severe("Couldn't load Warp Menu. No size or name found");
			}
		} else {
			Bukkit.getServer().getLogger().severe("Couldn't load Warp Menu. No warpMenu found");
		}
	}

	/**
	 * Runs when a player joins the server Attempts to give the player a
	 * serverSelector. If the desired slot has an item and there is no empty space
	 * to put the item, drop the item in the desired slot, and give the player the
	 * serverselector
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		receivedPlayersIf: if (Main.getSentPlayersConfig().contains(event.getPlayer().getUniqueId().toString())) {
			// Sent from a Server with this plugin
			Bukkit.getServer().getLogger().info("A ReceivedPlayer joined. Removing him from the list");

			FileConfiguration fc = Main.getSentPlayersConfig();
			String message = fc.getString(event.getPlayer().getUniqueId().toString());

			if (message.contains(":")) {

				String[] splitter = message.split(":");

				int x = 0, y = 0, z = 0;

				String worldName = splitter[0];
				try {
					x = Integer.parseInt(splitter[1]);
					y = Integer.parseInt(splitter[2]);
					z = Integer.parseInt(splitter[3]);
				} catch (NumberFormatException e) {
					Bukkit.getServer().getLogger()
							.severe("Invalid information saved in sentplayers.yml. Contact Developer");
				}

				String generatorName = splitter[4];

				World targetWorld = Bukkit.getWorld(worldName);
				if (targetWorld == null) {
					break receivedPlayersIf;
				}

				Location loc = new Location(targetWorld, x, y, z);

				Main.centeredTP(event.getPlayer(), loc);

				ReceiveParticleGenerator g = Main
						.getReceiveGeneratorFromEnum(ValidReceiveGenerators.valueOf(generatorName), event.getPlayer());

				g.runTaskTimer(Main.instance, 0, g.getTickDelay());

			} else {
				String generatorName = message;

				ReceiveParticleGenerator g = Main
						.getReceiveGeneratorFromEnum(ValidReceiveGenerators.valueOf(generatorName), event.getPlayer());

				g.runTaskTimer(Main.instance, 0, g.getTickDelay());

			}
			fc.set(event.getPlayer().getUniqueId().toString(), null);
			Main.saveSentPlayersConfig();
		}

		if (Main.serverName == null) {

			ServerNameGetter sng = new ServerNameGetter(event.getPlayer());
			int threadId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.instance, sng, (long) 40L,
					(long) 1L);
			sng.setThreadId(threadId);
		}

		if (warpSelector != null) {

			if (Main.config.contains("warpSelector")) {

				if (Main.config.getConfigurationSection("warpSelector").contains("itemOnJoin")
						&& Main.config.getConfigurationSection("warpSelector").contains("slot")) {

					if (Main.config.getBoolean("warpSelector.itemOnJoin")) {
						// Config Checks

						if (!event.getPlayer().getInventory().contains(warpSelector)) { // If the player doesn't have
																						// the serverSelector Item
																						// already

							Inventory pInv = event.getPlayer().getInventory();
							int slot = Main.config.getInt("warpSelector.slot");
							ItemStack itemInSlot = pInv.getItem(slot);

							if (itemInSlot != null) {
								if (pInv.firstEmpty() == -1) {
									// //Player's Inventory is Full. Drop the other item.
									// pInv.setItem(slot, serverSelector);
									// event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(),
									// itemInSlot);

									// Perhaps instead just ignore people with full inventories, They probably
									// should already have a compass. This will probably only happen
									// if someone /clear 's their inventory, and if so they're probably admin and
									// know how to get the compass back.
								} else {
									// Player's Inventory is not Full. Put the item in their inventory in the open
									// slot and put the server selector in the appropriate slot.

									pInv.setItem(pInv.firstEmpty(), itemInSlot);

									pInv.setItem(slot, warpSelector);
								}

							} else {
								pInv.setItem(slot, warpSelector);
							}
						}
					}
				} else {
					Bukkit.getServer().getLogger()
							.severe("Couldn't find itemOnJoin or slot options for warpSelector in config!");
				}
			} else {
				Bukkit.getServer().getLogger().severe("Couldn't find warpSelector");
			}

		} else {
			Bukkit.getServer().getLogger()
					.severe("There was an error initializing the warp selector item. Couldn't give to a player");
		}
	}

	/**
	 * Runs when a player clicks in an inventory Handles all interaction inside of a
	 * custom inventory
	 */
	@EventHandler
	public void onPlayerInventoryInteract(InventoryClickEvent event) {

		Inventory inventory = event.getInventory();
		if (inventory.equals(warps)) {
			Player player = (Player) event.getWhoClicked();

			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				if (warpSelector != null) {

					if (event.getCurrentItem().equals(warpSelector)) {
						((Player) event.getWhoClicked()).openInventory(warps);
					} else if (warpItems.containsKey(event.getCurrentItem())) {

						WarpTP warp = warpItems.get(event.getCurrentItem());

						if (warp.getCommands() != null) {
							if (warp.getCommands().isEmpty() == false) {
								for (String command : warp.getCommands()) {
									if (command.contains("%player%")) {
										command = command.replace("%player%", player.getName());
									}

									if (command.startsWith("p:")) {
										command = command.substring(2);
										Bukkit.getServer().dispatchCommand(player, command);
									} else if (command.startsWith("s:")) {
										command = command.substring(2);
										Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
												command);
									} else {
										Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
												command);
									}
								}
							}
						}

						teleportingPlayers.add(player.getUniqueId().toString());

						player.closeInventory();

						ParticleGenerator generator = Main.getSendGeneratorFromEnum(warp.getSend(), player, warp);

						generator.runTaskTimer(Main.instance, 0, generator.getTickDelay());
					}
				} else {
					Bukkit.getServer().getLogger().severe("WarpSelector was not properly initiated.");
				}
			}
		} else {
			if (event.getCurrentItem().equals(warpSelector)) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Runs when a player moves Cancels teleportations when a player moves.
	 */
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (teleportingPlayers.contains(event.getPlayer().getUniqueId().toString())) {
			if (!(event.getTo().getBlockX() == event.getFrom().getBlockX()
					&& event.getTo().getBlockZ() == event.getFrom().getBlockZ()
					&& event.getTo().getBlockY() == event.getFrom().getBlockY())) {
				event.getPlayer().sendMessage(Main.prefix + Main.msg("teleportationCancelled"));
				teleportingPlayers.remove(event.getPlayer().getUniqueId().toString());
			}
		}
	}

	/**
	 * Runs when a player interacts with anything. Handles the serverSelection Item.
	 */
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null) {
			if (warpSelector != null) {
				if (event.getItem().equals(warpSelector)) {

					Action action = event.getAction();

					if (Main.config.contains("warpSelector")) {
						if (Main.config.getConfigurationSection("warpSelector").contains("rightClick")
								&& Main.config.getConfigurationSection("warpSelector").contains("leftClick")) {

							if (Main.config.getBoolean("warpSelector.rightClick")) {
								if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
									event.setCancelled(true);

									updatePlaceHolders(event.getPlayer());

									event.getPlayer().openInventory(warps);
								}
							}

							if (Main.config.getBoolean("warpSelector.leftClick")) {
								if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
									event.setCancelled(true);
									updatePlaceHolders(event.getPlayer());

									event.getPlayer().openInventory(warps);
								}
							}

						} else {
							Bukkit.getServer().getLogger()
									.severe("Couldn't find right or left click option in config for warpSelector");
						}

					} else {
						Bukkit.getServer().getLogger().severe("Couldn't find warpSelector in config!");
					}

				}
			} else {
				Bukkit.getServer().getLogger()
						.severe("There was an error initializing the warp selector item. Run Interact Event");
			}
		}
	}

	/**
	 * Runs when the player drops an item. Prevents the serverSelector from being
	 * dropped.
	 */
	@EventHandler
	public void onPlayerThrowItemEvent(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(warpSelector)) {
			event.setCancelled(true);
		}
	}

	/**
	 * Updates the Lore of items and also requests the player count of a specified
	 * server.
	 */
	public static void updatePlaceHolders(Player player) {

		//TODO: LOOP THROUGH WAPRS AND RUN COMMAND UPDATE PLACE HOLDERS

	}
}