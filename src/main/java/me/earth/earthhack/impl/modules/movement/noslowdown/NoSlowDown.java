package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.block.MixinSlimeBlock;
import me.earth.earthhack.impl.core.mixins.block.MixinSoulSandBlock;
import me.earth.earthhack.impl.event.events.client.ClientInitEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MixinSoulSandBlock} for SoulSand.
 * {@link MixinSlimeBlock} for Slime.
 */
public class NoSlowDown extends Module
{
    protected final Setting<Boolean> guiMove  =
            register(new BooleanSetting("GuiMove", true));
    protected final Setting<Boolean> items    =
            register(new BooleanSetting("Items", true));
    protected final Setting<Boolean> legit    =
            register(new BooleanSetting("Legit", false));
    protected final Setting<Boolean> sprint   =
            register(new BooleanSetting("Sprint", true))
                    .setComplexity(Complexity.Dev);
    protected final Setting<Boolean> input    =
            register(new BooleanSetting("Input", true))
                    .setComplexity(Complexity.Dev);
    protected final Setting<Boolean> sneakPacket    =
            register(new BooleanSetting("SneakPacket", false));
    protected final Setting<Double> websY     =
            register(new NumberSetting<>("WebsVertical", 2.0, 1.0, 100.0));
    protected final Setting<Double> websXZ    =
            register(new NumberSetting<>("WebsHorizontal", 1.1, 1.0, 100.0));
    protected final Setting<Boolean> sneak    =
            register(new BooleanSetting("WebsSneak", false));
    protected final Setting<Boolean> useTimerWeb    =
            register(new BooleanSetting("UseTimerInWeb", false));
    protected final Setting<Double> timerSpeed     =
            register(new NumberSetting<>("Timer", 8.0, 0.1, 20.0));
    protected final Setting<Boolean> onGroundSpoof    =
            register(new BooleanSetting("OnGroundSpoof", false));
    protected final Setting<Boolean> superStrict =
            register(new BooleanSetting("SuperStrict", false));
    protected final Setting<Boolean> phobosGui =
            register(new BooleanSetting("PhobosGui", false));

    protected final List<Class<? extends Screen>> screens =
            new ArrayList<>();

    protected KeyBinding[] keys;
    protected boolean spoof = true;
    protected boolean usingTimer;

    public NoSlowDown()
    {
        super("NoSlowDown", Category.Movement);
        register(new BooleanSetting("SoulSand", true));
        register(new BooleanSetting("Slime", false));

        screens.add(OptionsScreen.class);
        screens.add(VideoOptionsScreen.class);
        screens.add(SoundOptionsScreen.class);
        screens.add(GenericContainerScreen.class);
        screens.add(GameMenuScreen.class);

        this.listeners.add(new ListenerSprint(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerPostKeys(this));
        this.listeners.add(new ListenerRightClickItem(this));
        this.listeners.add(new ListenerTryUseItem(this));
        this.listeners.add(new ListenerTryUseItemOnBlock(this));
        this.listeners.add(new LambdaListener<>(ClientInitEvent.class, e -> keys = new KeyBinding[]
                {
                        mc.options.forwardKey,
                        mc.options.backKey,
                        mc.options.leftKey,
                        mc.options.rightKey,
                        mc.options.jumpKey,
                        mc.options.sprintKey
                }));
        this.setData(new NoSlowDownData(this));
    }

    @Override
    protected void onDisable()
    {
        Managers.NCP.setStrict(false);
        this.usingTimer = false;
    }

    protected void updateKeyBinds()
    {
        if (guiMove.getValue())
        {
            if (screens
                    .stream()
                    .anyMatch(screen -> screen.isInstance(mc.currentScreen))
                || phobosGui.getValue() && mc.currentScreen instanceof Click)
            {
                for (KeyBinding key : keys)
                {
                    KeyBinding.setKeyPressed(key.getDefaultKey(),
                                               KeyBoardUtil.isKeyDown(key));
                }
            }
           // else if (mc.currentScreen == null)
           // {
           //     for (KeyBinding key : keys)
           //     {
           //         if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getDefaultKey().getCode()))
           //         {
           //             KeyBinding.setKeyPressed(key.getDefaultKey(), false);
           //         }
           //     }
           // }
        }
    }

    protected void onPacket(PlayerMoveC2SPacket packet) {
        /*if (onGroundSpoof.getValue()) {
            ((ICPacketPlayer) packet).setOnGround(false);
            ((ICPacketPlayer) packet).setY(packet.getY(mc.player.posY) + 0.1);
        }*/
    }

}
