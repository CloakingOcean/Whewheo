package com.exitium.whewheo.teleportobjects;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;

import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.particles.send.ValidSendGenerators;

/**
 * Wrapper class for warp teleport information.
 * 
 * @author Cloaking_Ocean
 * date Mar 26, 2017
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
	
	
	public WarpTP(int id, String name, String serverName, Location location, int slot, String material, Enchantment enchantment, int quantity, List<String> lore, boolean enableCommands, List<String> commands, ValidSendGenerators send, ValidReceiveGenerators receive) {
		this.id = id;
		this.name = name;
		this.setServerName(serverName);
		this.location = location;
		this.slot = slot;
		this.material = material;
		this.enchantment = enchantment;
		this.quantity = quantity;
		this.lore = lore;
		this.enableCommands = enableCommands;
		this.commands = commands;
		this.setSend(send);
		this.setReceive(receive);
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @param slot the slot to set
	 */
	public void setSlot(int slot) {
		this.slot = slot;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the lore
	 */
	public List<String> getLore() {
		return lore;
	}

	/**
	 * @param lore the lore to set
	 */
	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	/**
	 * @return the enableCommands
	 */
	public boolean isEnableCommands() {
		return enableCommands;
	}

	/**
	 * @param enableCommands the enableCommands to set
	 */
	public void setEnableCommands(boolean enableCommands) {
		this.enableCommands = enableCommands;
	}

	/**
	 * @return the commands
	 */
	public List<String> getCommands() {
		return commands;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	/**
	 * @return the enchantment
	 */
	public Enchantment getEnchantment() {
		return enchantment;
	}

	/**
	 * @param enchantment the enchantment to set
	 */
	public void setEnchantment(Enchantment enchantment) {
		this.enchantment = enchantment;
	}

	/**
	 * @return the send
	 */
	public ValidSendGenerators getSend() {
		return send;
	}

	/**
	 * @param send the send to set
	 */
	public void setSend(ValidSendGenerators send) {
		this.send = send;
	}

	/**
	 * @return the receive
	 */
	public ValidReceiveGenerators getReceive() {
		return receive;
	}

	/**
	 * @param receive the receive to set
	 */
	public void setReceive(ValidReceiveGenerators receive) {
		this.receive = receive;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}