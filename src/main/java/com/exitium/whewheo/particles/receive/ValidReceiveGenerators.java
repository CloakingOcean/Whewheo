package com.exitium.whewheo.particles.receive;

/**
 * A list of valid Receive Generators
 * 
 * @author Cloaking_Ocean date Apr 1, 2017
 * @version 1.0
 */
public enum ValidReceiveGenerators {
	EMERALD("Displays an emerald by starting from the top (1 block above the player's head) and creating circles with varying sizes."),

	FIRE_EXPLOSION("Displays three fire rings that explode outward.");

	private String description;

	ValidReceiveGenerators(String description) {
		this.setDescription(description);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
