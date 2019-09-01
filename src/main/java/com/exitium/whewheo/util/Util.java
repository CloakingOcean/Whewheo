package com.exitium.whewheo.util;

import com.exitium.whewheo.Main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;

public final class Util {

    private Util() {}

    /***
     * Sends a plugin message through given player for the PlayerCount.
     * 
     * @param serverName
     * @param player
     */
    public static void requestPlayerCount(String serverName, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(serverName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());

    }
}