package me.earth.earthhack.impl.modules.client.management;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.module.util.PluginsCategory;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.network.ClientPlayerEntity;

import java.awt.*;
import java.util.Map;

/**
 * { TODO: me.earth.earthhack.impl.core.mixins.util.MixinScreenShotHelper }
 */
public class Management extends Module {
    protected final Setting<Boolean> clear =
            register(new BooleanSetting("ClearPops", false));
    protected final Setting<Boolean> logout =
            register(new BooleanSetting("LogoutPops", false));
    protected final Setting<Boolean> friend =
            register(new BooleanSetting("SelfFriend", true));
    public final Setting<Boolean> pluginSection =
            register(new BooleanSetting("PluginSection", true));
    protected final Setting<Boolean> soundRemove =
            register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Integer> deathTime =
            register(new NumberSetting<>("DeathTime", 250, 0, 1000));
    protected final Setting<Integer> time =
            register(new NumberSetting<>("Time", 0, 0, 24000));
    protected final Setting<Boolean> aspectRatio =
            register(new BooleanSetting("ChangeAspectRatio", false));
    protected final Setting<Integer> aspectRatioWidth =
            register(new NumberSetting<>("AspectRatioWidth", 834, 0, 834));
    protected final Setting<Integer> aspectRatioHeight =
            register(new NumberSetting<>("AspectRatioHeight", 834, 0, 834));
    protected final Setting<Boolean> pooledScreenShots =
            register(new BooleanSetting("Pooled-Screenshots", false));
    protected final Setting<Boolean> pauseOnLeftFocus =
            register(new BooleanSetting("PauseOnLeftFocus",
                    true));
    public final Setting<Boolean> eventDebug =
            register(new BooleanSetting("DebugEvents",
                    true));
    protected final Setting<Boolean> customFogColor =
            register(new BooleanSetting("CustomFogColor", false));
    protected final Setting<Color> fogColor =
            register(new ColorSetting("FogColor", new Color(255, 255, 255, 255)));
    /*
    protected final Setting<Boolean> resourceDebug =
            register(new BooleanSetting("ResourceDebug", false)); // TODO:

     */
    protected final Setting<Integer> unfocusedFps =
            register(new NumberSetting<>("UnfocusedFps", 30, 0, 300));

    protected final Setting<CooldownBypass> globalCooldownBypass =
            register(new EnumSetting<>("Global-CD-Bypass", CooldownBypass.None));


    protected final Setting<CooldownBypass> manualCooldownBypass =
            register(new EnumSetting<>("Manual-CD-Bypass", CooldownBypass.None));

    public final Setting<Boolean> icon =
            register(new BooleanSetting("GameIcon", true));
    public final Setting<Boolean> toast =
            register(new BooleanSetting("Toast", false));
    public final Setting<String> toastText =
            register(new StringSetting("ToastText", "| Looking up at Phobos"));

    protected GameProfile lastProfile;
    protected ClientPlayerEntity player;
    int fps;
    boolean displayFlag = true;


    public Management()
    {
        super("Management", Category.Client);
        Bus.EVENT_BUS.register(new ListenerLogout(this));
        Bus.EVENT_BUS.register(new ListenerGameLoop(this));
        Bus.EVENT_BUS.register(new ListenerAspectRatio(this));
        Bus.EVENT_BUS.register(new ListenerTick(this));
        Bus.EVENT_BUS.register(new ListenerSwitch(this));
        register(new NumberSetting<>("PB-Position-Range", 5.0, 0.0, 10_000.0));
        register(new BooleanSetting("MotionService", true))
            .setComplexity(Complexity.Expert);
        register(new NumberSetting<>("EntityTracker-Updates", 2.0, 0.01, 1000.0))
            .setComplexity(Complexity.Expert);
        // TODO: kinda ugly that this is here
        register(new BooleanSetting("PB-SetPos", true))
            .setComplexity(Complexity.Expert);
        register(new BooleanSetting("PB-FixChunks", false))
            .setComplexity(Complexity.Expert);
        register(new BooleanSetting("IgnoreForgeRegistries", false));

        this.setData(new ManagementData(this));
        this.clear.addObserver(event ->
        {
            event.setValue(false);
            ChatUtil.sendMessage("Clearing TotemPops...", getName());
            Managers.COMBAT.reset();
        });
        this.pauseOnLeftFocus.addObserver(e ->
            mc.options.pauseOnLostFocus = e.getValue());

        // long handle = MinecraftClient.getInstance().getWindow().getHandle();
//
        // if (unfocusedFps.getValue() != 0) {
        //     this.listeners.add(new LambdaListener<>(Render2DEvent.class, e -> {
        //         if (GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_FOCUSED) != GLFW.GLFW_TRUE) {
        //             if (displayFlag) {
        //                 fps = mc.options.getMaxFps().getValue();
        //                 displayFlag = false;
        //             }
        //             mc.options.getMaxFps().setValue(unfocusedFps.getValue());
        //         }
        //         else if (!displayFlag) {
        //             mc.options.getMaxFps().setValue(fps);
        //             displayFlag = true;
        //         }
        //     }));
        // }
    }

    @Override
    protected void onLoad()
    {
        if (friend.getValue())
        {
            lastProfile = mc.getSession().getProfile();
            Managers.FRIENDS.add(lastProfile.getName(), lastProfile.getId());
        }
        pluginsUpdate(pluginSection.getValue());
    }

    private void pluginsUpdate(boolean isSeparated) {
        for (Map.Entry<Module, Category> entry : PluginsCategory.getInstance().getPluginsModuleList().entrySet())
            entry.getKey().setCategory(isSeparated ? PluginsCategory.getInstance().getCategory() : entry.getValue());
    }

    public boolean isUsingCustomFogColor()
    {
        return customFogColor.getValue();
    }

    public Color getCustomFogColor()
    {
        return fogColor.getValue();
    }

}
