package me.earth.earthhack.impl;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.network.NetworkState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link me.earth.earthhack.impl.core.mixins.MixinMinecraftClient}
 */
public class Earthhack implements ClientModInitializer {

    private static final Logger LOGGER = LogManager.getLogger("3arthh4ck");
    public static final String NAME = "3arthh4ck";
    public static final String VERSION = "1.0.1";
    public static long startMS;

    @Override
    public void onInitializeClient() {
        //Core CORE = new Core();
        startMS = System.currentTimeMillis();
        LOGGER.info("\n\n ------------------ Initializing 3arthh4ck-fabric. ------------------ \n");
        Managers.load();
        LOGGER.info("Prefix is " + Commands.getPrefix());
        LOGGER.info("\n\n ------------------ 3arthh4ck-fabric initialized. ------------------ \n");

        NetworkState networkState = NetworkState.PLAY;


    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
