package com.exitium.whewheo.util;

public class Util {

    /** Sends Bungeecord a request to get the playercount of a specific server */
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