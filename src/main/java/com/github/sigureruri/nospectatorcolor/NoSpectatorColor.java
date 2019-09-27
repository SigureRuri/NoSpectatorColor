package com.github.sigureruri.nospectatorcolor;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class NoSpectatorColor extends JavaPlugin {

    public static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PlayerInfoPacketListener(this));
    }

    @Override
    public void onDisable() {
    }
}
