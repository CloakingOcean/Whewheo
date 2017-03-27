package com.exitium.whewheo;

import java.util.List;

/**
 * Wrapper class for server teleport information.
 * 
 * @author Cloaking_Ocean
 * @date Mar 26, 2017
 * @version 1.0
 */
public class ServerTP {

	private int id;
	private String name;
	private boolean groups;
	private String group;
	private int slot;
	private String material;
	private int quantity;
	private List<String> lore;
	private boolean enableCommands;
	private List<String> commands;
	
	public ServerTP(int id, String name, int slot, String material, int quantity, List<String> lore, boolean enableCommands, List<String> commands) {
		this.id = id;
		this.name = name;
		this.groups = false;
		this.group = "";
		this.material = material;
		this.quantity = quantity;
		this.lore = lore;
		this.enableCommands = enableCommands;
		this.commands = commands;
	}
	
	public ServerTP(int id, String name, boolean groups, String group, int slot, String material, int quantity, List<String> lore, boolean enableCommands, List<String> commands) {
		this.id = id;
		this.name = name;
		this.groups = groups;
		this.group = group;
		this.material = material;
		this.quantity = quantity;
		this.lore = lore;
		this.enableCommands = enableCommands;
		this.commands = commands;
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
	 * @return the groups
	 */
	public boolean isGroups() {
		return groups;
	}
	
	/**
	 * @param groups the groups to set
	 */
	public void setGroups(boolean groups) {
		this.groups = groups;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
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
	
}