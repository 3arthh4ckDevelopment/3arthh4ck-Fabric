package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class Notifications extends Module
{
    protected final Setting<Boolean> totems      =
            register(new BooleanSetting("TotemPops", true));
    protected final Setting<TextColor> totemColor =
            register(new EnumSetting<>("Totem-Color", TextColor.None));
    protected final Setting<TextColor> totemAmountColor =
            register(new EnumSetting<>("Amount-Color", TextColor.None));
    protected final Setting<TextColor> totemPlayerColor =
            register(new EnumSetting<>("Player-Color", TextColor.None));
    protected final Setting<NotificationType> typeNotification   =
            register(new EnumSetting<>("Type", NotificationType.Chat));
    protected final Setting<Double> duration =
            register(new NumberSetting<>("Duration", 1.0, 0.5, 3.0));
    // protected final Setting<Float> posX =
    //         register(new NumberSetting<>("PosX", 680.0f, 0.0f, (float) Render2DUtil.getScreenWidth()));
    // protected final Setting<Float> posY =
    //         register(new NumberSetting<>("posY", 380.0f, 0.0f, (float) Render2DUtil.getScreenHeight()));

    protected final Setting<Boolean> modules     =
            register(new BooleanSetting("Modules", true));
    protected final Setting<Boolean> configure   =
            register(new BooleanSetting("Show-Modules", true));
    protected final Setting<Category.CategoryEnum> categories =
            register(new EnumSetting<>("Categories", Category.CategoryEnum.Combat));

    protected final Map<Module, Setting<Boolean>> announceMap = new HashMap<>();
    protected final StopWatch timer = new StopWatch();

    boolean hudnotify = false;
    private float time = 1, border = 4, width = 130.0f;

    public Notifications()
    {
        super("Notifications", Category.Client);
        this.listeners.add(new ListenerTotems(this));
        this.listeners.add(new ListenerDeath(this));
        this.setData(new NotificationData(this));

        this.listeners.add(new LambdaListener<>(Render2DEvent.class, e -> {
            if (hudnotify) {
                time++;
                // if (typeNotification.getValue() == NotificationType.New) {
                //     // make an fade in/out animation?
                //     // and also handle multiples notifications?
                //     notificationHudNew();
                // } else {notificationHudOld();}
                if (timer.passed(duration.getValue() * 1000) || mc.world == null || mc.player == null) {
                    hudnotify = false;
                    timer.reset();
                }
            }
        }));

        Bus.EVENT_BUS.register(
            new EventListener<PostInitEvent>(PostInitEvent.class) {
                @Override
                public void invoke(PostInitEvent event)
                {
                    createSettings();
                }
            }
        );
    }

    @Override
    protected void onDisable() {
        hudnotify = false;
    }
    private void createSettings()
    {
        announceMap.clear();
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(categories, configure::getValue);

        for (Module module : Managers.MODULES.getRegistered())
        {
            Setting<Boolean> enabled = module.getSetting("Enabled",
                                                    BooleanSetting.class);
            if (enabled == null)
            {
                continue;
            }

            enabled.addObserver(event ->
            {
                if (isEnabled()
                        && !event.isCancelled()
                        && modules.getValue()
                        && announceMap.get(module).getValue())
                {
                    onToggleModule((Module) event.getSetting().getContainer(),
                                            event.getValue());
                }
            });

            String name = module.getName();
            if (this.getSetting(name) != null) {name = "Show" + name;}

            Setting<Boolean> setting =
                    register(new BooleanSetting(name, false));

            announceMap.put(module, setting);

            Visibilities.VISIBILITY_MANAGER.registerVisibility(setting,
                    () -> configure.getValue()
                        && categories.getValue().toValue() == module.getCategory());

            this.getData()
                .settingDescriptions()
                .put(setting, "Announce Toggling of " + name + "?");
        }
    }

    protected void onToggleModule(Module module, boolean enabled)
    {
        Setting<Boolean> setting = announceMap.get(module);
        if (setting != null && setting.getValue())
        {
            String message = TextColor.BOLD
                    + module.getDisplayName()
                    + (enabled ? TextColor.GREEN : TextColor.RED)
                    + (enabled ? " enabled" : " disabled");

            sendNotification(message, module.getName(), ChatIDs.MODULE, true);
        }
    }

    public void onPop(Entity player, int totemPops)
    {
        if (this.isEnabled()) {
            if (totems.getValue()) {
                String message = totemPlayerColor.getValue().getColor()
                        + player.getName()
                        + totemColor.getValue().getColor()
                        + " popped "
                        + totemAmountColor.getValue().getColor()
                        + totemPops
                        + totemColor.getValue().getColor()
                        + " totem"
                        + (totemPops == 1 ? "" : "s");

                sendNotification(message, player.getName().getString(), ChatIDs.TOTEM_POPS, false);
            }
        }
    }

    public void onDeath(Entity player, int totemPops)
    {
        if (this.isEnabled()) {
            if (totems.getValue()) {
                String message = totemPlayerColor.getValue().getColor()
                        + player.getName()
                        + totemColor.getValue().getColor()
                        + " died after popping "
                        + totemAmountColor.getValue().getColor()
                        + totemPops
                        + totemColor.getValue().getColor()
                        + " totem"
                        + (totemPops == 1 ? "" : "s");

                sendNotification(message, player.getName().getString(), ChatIDs.TOTEM_POPS, false);
            }
        }
    }

    private String messageEvent;

    public void sendNotification(String message, String name, int senderID, boolean scheduled) {
        if (typeNotification.getValue() == NotificationType.Chat && !scheduled) {
            Managers.CHAT.sendDeleteMessage(message, name, senderID);
        } else if (typeNotification.getValue() == NotificationType.Chat && scheduled) {
            mc.execute(() -> Managers.CHAT.sendDeleteMessage(message, name, senderID));
        } else if (typeNotification.getValue() != NotificationType.Chat) {
            System.out.println(message);
            messageEvent = message;
            time = 0;
            timer.reset();
            hudnotify = true;
        }
    }
    // TODO yo Ai_2473 fix
    /*
    public void notificationHudNew() {
        float messageWidth= Managers.TEXT.getStringWidth(messageEvent);
        float endX = (messageWidth < width ? posX.getValue() + width : posX.getValue() + messageWidth + 2.0f);
        Render2DUtil.roundedRect(posX.getValue() - 5, posY.getValue(), endX + 5.0f, posY.getValue() + 25.0f, border,0xaa454545);
        Managers.TEXT.drawString(messageEvent, posX.getValue() + ((endX - posX.getValue() + 5.0f) / 2 - messageWidth / 2), posY.getValue() + (25.0f / 2 - Managers.TEXT.getStringHeight() / 2), 0xffffffff);


        float progress;
        if (messageWidth < width) {
            progress = (float) ((time - border) / duration.getValue());
        } else {
            progress = (float) (((time + messageWidth / 4 + width / 2) / duration.getValue()));
        }
        Render2DUtil.progressBar((posX.getValue()) + progress, endX - border, posY.getValue() + 22.0f, border, 0xcc00ee00);

        //TODO: fix all of this
    }


    public void notificationHudOld() {
        // remember that this uses inverted coords!!
        Render2DUtil.drawRect(posX.getValue() - Managers.TEXT.getStringWidth(messageEvent) - 1.0f, posY.getValue() - Managers.TEXT.getStringHeight() - 1.0f, posX.getValue(), posY.getValue() - 1.0f,0xaa454545);
        Managers.TEXT.drawString(messageEvent, posX.getValue() - Managers.TEXT.getStringWidth(messageEvent), posY.getValue() - Managers.TEXT.getStringHeight(), 0xffffffff);
    }
    */
}
