package me.earth.earthhack.impl.util.discord;

import me.earth.earthhack.impl.modules.client.rpc.RPC;

public class DiscordPresence {

    private final RPC module;
    private Thread thread;

    public DiscordPresence(RPC module){
        this.module = module;
    }


}
