package com.exitium.whewheo.particles.send;

/**
 * 
 * 
 * @author Cloaking_Ocean
 * date Apr 1, 2017
 * @version 1.0
 */
public enum ValidSendGenerators {
	SPIRAL(
			"Displays a white thick spiral starting from 1 block above the player's head, coming from a small circle of clouds."
	),
	
	NETHER_PORTAL(
			"Surrounds the Player in a box resembling a nether portal."
	);
			
	
	private String description;
	ValidSendGenerators(String description) {
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
