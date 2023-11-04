package me.earth.earthhack.impl;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link me.earth.earthhack.impl.core.mixins.MixinMinecraftClient}
 */

public class Earthhack implements ModInitializer, ClientModInitializer {

    private static final Logger LOGGER = LogManager.getLogger("3arthh4ck");
    public static final String NAME = "3arthh4ck";
    public static final String MOD_ID = "earthhack";
    public static final String VERSION = "2.0.0-fabric";

    @Override
    public void onInitialize() {
        LOGGER.info("\n\nInitializing 3arthh4ck-fabric.");
        Managers.load();
        LOGGER.info("Prefix is " + Commands.getPrefix());
        LOGGER.info("\n3arthh4ck initialized.\n");
    }

    @Override
    public void onInitializeClient() {

    }

    public static Logger getLogger()
    {
        return LOGGER;
    }
}
