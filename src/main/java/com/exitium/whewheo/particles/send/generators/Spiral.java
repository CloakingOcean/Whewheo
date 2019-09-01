package com.exitium.whewheo.particles.send.generators;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.send.SendParticleGenerator;
import com.exitium.whewheo.particles.util.ParticleEffect;
import com.exitium.whewheo.teleportobjects.WarpTP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * This is a Send Generator class Description: Displays a white thick spiral
 * starting from 1 block above the player's head, coming from a small circle of
 * clouds.
 * 
 * @author Cloaking_Ocean date Mar 27, 2017
 * @version 1.0
 */
public class Spiral extends SendParticleGenerator {

	private double radius = 1;

	private ConfigLoader configLoader;
	private Main main;
	private ServerSelectionHandler serverSel;

	public Spiral(Player player, WarpTP warp, ConfigLoader configLoader, Main main, ServerSelectionHandler serverSel) {
		super(player, 1, warp, configLoader, main, serverSel);

		Bukkit.getServer().getLogger().info("A spiral is loading up");
	}

	@Override
	public void run() {
		if (Bukkit.getServer().getPlayer(player.getUniqueId()) != null) {
			if (serverSel.containsTeleportingPlayer(player.getUniqueId().toString())) {

				Location loc = player.getLocation();

				timeInRadians = timeInRadians + Math.PI / 8;

				for (int i = 0; i < 10; i++) {
					double x = (radius - (.09) * i) * Math.cos(timeInRadians + (Math.PI / 8) / 2 * i);
					double y = i * (Math.PI / 8) / 1.50;
					double z = (radius - (.09) * i) * Math.sin(timeInRadians + (Math.PI / 8) / 2 * i);
					loc.add(x, y, z);

					ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1, loc, 100);

					loc.subtract(x, y, z);
				}

				double distance = 0.2;

				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX(), loc.getY() + 2.7, loc.getZ()), 100);

				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() + distance, loc.getY() + 2.7, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() - distance, loc.getY() + 2.7, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX(), loc.getY() + 2.7, loc.getZ() + distance), 100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX(), loc.getY() + 2.7, loc.getZ() - distance), 100);

				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() + distance, loc.getY() + 2.7, loc.getZ() + distance),
						100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() + distance, loc.getY() + 2.7, loc.getZ() - distance),
						100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() - distance, loc.getY() + 2.7, loc.getZ() + distance),
						100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() - distance, loc.getY() + 2.7, loc.getZ() - distance),
						100);

				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX(), loc.getY() + 3, loc.getZ()), 100);

				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() + 0.1, loc.getY() + 3, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX() - 0.1, loc.getY() + 3, loc.getZ()), 100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX(), loc.getY() + 3, loc.getZ() + 0.1), 100);
				ParticleEffect.CLOUD.display(new Vector(0, 0, 0), 1,
						new Location(loc.getWorld(), loc.getX(), loc.getY() + 3, loc.getZ() - 0.1), 100);
				checkTeleporation();
			} else {
				cancel();
			}

			secondsPassed += (1.0 / 20.0);
		} else {
			serverSel.removeTeleportingPlayer(player.getUniqueId().toString());
			cancel();
		}
	}
}