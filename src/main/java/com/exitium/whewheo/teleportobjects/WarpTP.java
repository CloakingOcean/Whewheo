package com.exitium.whewheo.teleportobjects;

import java.util.ArrayList;
import java.util.List;

import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.particles.send.ValidSendGenerators;
import com.exitium.whewheo.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

/**
 * Wrapper class for warp teleport information.
 * 
 * @author Cloaking_Ocean date Mar 26, 2017
 * @version 1.0
 */
public class WarpTP {

	private int id;
	private String name;
	private String serverName;
	private Location location;
	private int slot;
	private String material;
	private Enchantment enchantment;
	private int quantity;
	private List<String> lore;
	private boolean enableCommands;
	private List<String> commands;
	private ValidSendGenerators send;
	private ValidReceiveGenerators receive;

	public WarpTP() {

	}

	public boolean load(ConfigurationSection section, String key) {
		if (!section.getBoolean("enabled")) {
			Bukkit.getServer().getLogger().info("Skipping warp " + key
					+ " because it has not been enabled. Please configure the warp to use it.");
			return false;
		}

		this.slot = section.getInt("slot");
		if (slot == -1) {
			Bukkit.getServer().getLogger().info("Skipping warp " + key
					+ " because it's slot has not been updated in the menu.yml. Please update it to use the warp.");
			return false;
		}

		this.id = retrieveId(key);
		this.name = ConfigLoader.getColoredTextFromMenu("warps." + key + ".name");
		loadLocation(section, key);
		this.material = section.getString("material");
		this.enchantment = parseEnchantment(section);
		this.quantity = section.getInt("quantity");
		this.lore = section.getStringList("lore");

		this.enableCommands = section.getBoolean("enableCommands");
		this.commands = section.getStringList("commands");

		this.send = parseSendGenerator(section, key);
		if (this.send == null) {
			return false;
		}

		this.receive = parseReceiveGenerator(section, key);
		if (this.receive == null) {
			return false;
		}

		return true;
	}

	public void updatePlaceholders(Player player) {
		ArrayList<String> previouslyRequested = new ArrayList<String>();
		ArrayList<String> lore = new ArrayList<String>();

		for (String s : this.lore) {
			// s = s.replace("%player%", player.getName());

			if (s.contains("%count%")) {
				if (!previouslyRequested.contains(this.serverName)) {
					Util.requestPlayerCount(this.serverName, player);
					previouslyRequested.add(this.serverName);
				}
			}

			lore.add(s);
		}

		this.lore = lore;
	}

	private void loadLocation(ConfigurationSection section, String key) {
		String locationText = section.getString("location");

		String[] splitter = locationText.split(":");

		/*
			* Possible Values:
			* 
			* location: <server>:<world>:<x>:<y>:<z> args.length = 5
			*
			* location: <world>:<x>:<y>:<z> args.length = 4
			* 
			* location: <server>:<world> args.length = 2
			* 
			* location: <server> args.length = 1
			*/

		if (splitter.length == 5 || splitter.length == 4) {

			int index = 0;
			if (splitter.length == 5) {
				// Send to server with specific location
				this.serverName = splitter[index];
				index++;
			}
			
			// Send to local warp

			String worldName = splitter[index+1];

			int x, y, z;

			try {
				x = Integer.parseInt(splitter[index+2]);
				y = Integer.parseInt(splitter[index+3]);
				z = Integer.parseInt(splitter[index+4]);
			} catch (NumberFormatException e) {
				Bukkit.getServer().getLogger().severe(
						"Couldn't load warp: " + key + ". Invalid location coordinates. Invalid Integers.");
				return;
			}

			World world = Bukkit.getServer().getWorld(worldName);
			if (world == null) {
				Bukkit.getServer().getLogger()
						.severe("Couldn't load warp: " + key + ". Couldn't find world : " + worldName
								+ "! Please edit location configuration for warp " + key + "!");
				return;
			}
			
			this.location = new Location(world, x, y, z);

		} else if (splitter.length == 2) {
			// Send to server at default spawn location of world
			this.serverName = splitter[0];

			String worldName = splitter[1];

			World world = Bukkit.getServer().getWorld(worldName);
			if (world == null) {
				Bukkit.getServer().getLogger()
						.severe("Couldn't load warp: " + key + ". Couldn't find world : " + worldName
								+ "! Please edit location configuration for warp " + key + "!");
				return;
			}
			
			this.location = world.getSpawnLocation();

		} else if (splitter.length == 1) {
			// Send to server with no extra teleportation
			this.serverName = splitter[0];
		}
	}

	private int retrieveId(String key) {
		try {
			return Integer.parseInt(key);
		} catch (NumberFormatException e) {
			Bukkit.getServer().getLogger().severe("Invalid id: " + key + ". Skipping..");
			return -1;
		}
	}

	private Enchantment parseEnchantment(ConfigurationSection section) {
		String enchantment = section.getString("enchantment");
		Enchantment realEnchantment = Enchantment.getByName(enchantment);
		if (realEnchantment == null) {
			Bukkit.getServer().getLogger().severe("No enchantment by the name of: " + enchantment + "!");
			
		}

		return realEnchantment;
	}

	private ValidSendGenerators parseSendGenerator(ConfigurationSection section, String key) {
		String sendGeneratorName = section.getString("sendEffect");
		ValidSendGenerators validSendGenerator = null;
		try {
			return ValidSendGenerators.valueOf(sendGeneratorName.toUpperCase());
		} catch (Exception e) {
			Bukkit.getLogger().severe("Couldn't load warp " + key
					+ ". Invalid Send Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
			return null;
		}
	}

	private ValidReceiveGenerators parseReceiveGenerator(ConfigurationSection section, String key) {
		String receiveGeneratorName = section.getString("receiveEffect");
		ValidReceiveGenerators validReceiveGenerator = null;

		try {
			return validReceiveGenerator = ValidReceiveGenerators.valueOf(receiveGeneratorName.toUpperCase());
		} catch (Exception e) {
			Bukkit.getLogger().severe("Couldn't load warp " + key +
					". Invalid Receive Effect Specified. Please review \"generatorhelp.txt\" in plugin folder.");
			return null;
		}
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @return the lore
	 */
	public List<String> getLore() {
		return lore;
	}

	/**
	 * @return the enableCommands
	 */
	public boolean isEnableCommands() {
		return enableCommands;
	}

	/**
	 * @return the commands
	 */
	public List<String> getCommands() {
		return commands;
	}

	/**
	 * @return the enchantment
	 */
	public Enchantment getEnchantment() {
		return enchantment;
	}

	/**
	 * @return the send
	 */
	public ValidSendGenerators getSend() {
		return send;
	}

	/**
	 * @return the receive
	 */
	public ValidReceiveGenerators getReceive() {
		return receive;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}
}