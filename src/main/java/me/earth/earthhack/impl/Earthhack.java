package me.earth.earthhack.impl;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.commands.gui.EarthhackButton;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link me.earth.earthhack.impl.core.mixins.MixinMinecraftClient}
 */
public class Earthhack implements ModInitializer, ClientModInitializer, Globals {

    private static final Logger LOGGER = LogManager.getLogger("3arthh4ck");
    public static final String NAME = "3arthh4ck";
    public static final String MOD_ID = "earthhack";
    public static final String VERSION = "2.0.0-fabric";

    @Override
    public void onInitialize() {
        LOGGER.info("\n\n ------------------ Initializing 3arthh4ck-fabric. ------------------ \n");
        Managers.load();
        LOGGER.info("Prefix is " + Commands.getPrefix());
        LOGGER.info("\n\n ------------------ 3arthh4ck-fabric initialized. ------------------ \n");
    }

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) -> {
            EarthhackButton temp;
            if (screen instanceof TitleScreen)
            {
                int bx = w / 2 + 104;
                int by = h / 4 + 48 + 72 + (-24);
                try {
                    bx = (int) screen.width / 2 + 250;
                    by = (int) screen.height / 2 - (screen.height / 3);
                } catch (Throwable t) {
                    bx = w / 2 + 104;
                    by = h / 4 + 48 + 72 + (-24);
                }
                temp = new EarthhackButton(400, bx, by, action -> mc.setScreen(new CommandGui(screen, 400)));
                temp.setTooltip(Tooltip.of(Text.of("In-Game Account Switcher")));
                Screens.getButtons(screen).add(temp);
            }
        });
    }

    public static Logger getLogger()
    {
        return LOGGER;
    }

    public static boolean isRunning()
    {
        return ((IMinecraftClient) mc).isEarthhackRunning();
    }
}
