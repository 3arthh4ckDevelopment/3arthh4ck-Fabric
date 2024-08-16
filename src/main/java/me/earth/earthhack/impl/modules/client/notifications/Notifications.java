package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.client.PostModulesLoadingEvent;
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
    protected final Setting<Boolean> totems =
            register(new BooleanSetting("TotemPops", true));
    protected final Setting<TextColor> totemColor =
            register(new EnumSetting<>("Totem-Color", TextColor.None));
    protected final Setting<TextColor> totemAmountColor =
            register(new EnumSetting<>("Amount-Color", TextColor.None));
    protected final Setting<TextColor> totemPlayerColor =
            register(new EnumSetting<>("Player-Color", TextColor.None));
    protected final Setting<NotificationType> typeNotification   =
            register(new EnumSetting<>("Type", NotificationType.Chat));

    protected final Setting<Boolean> modules =
            register(new BooleanSetting("ModuleSorting", true));
    protected final Setting<Boolean> configure =
            register(new BooleanSetting("Show-ModuleSorting", true));
    protected final Setting<Category.CategoryEnum> categories =
            register(new EnumSetting<>("Categories", Category.CategoryEnum.Combat));

    protected final Map<Module, Setting<Boolean>> announceMap = new HashMap<>();
    protected final StopWatch timer = new StopWatch();

    public Notifications()
    {
        super("Notifications", Category.Client);
        this.listeners.add(new ListenerTotems(this));
        this.listeners.add(new ListenerDeath(this));
        this.setData(new NotificationData(this));

        Bus.EVENT_BUS.register(
            new EventListener<>(PostModulesLoadingEvent.class) {
                @Override
                public void invoke(PostModulesLoadingEvent event)
                {
                    createSettings();
                }
            }
        );
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
                        + player.getName().getString()
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
                        + player.getName().getString()
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

    public void sendNotification(String message, String name, int senderID, boolean scheduled) {
        if (typeNotification.getValue() == NotificationType.Chat) {
            if (scheduled) {
                mc.execute(() -> Managers.CHAT.sendDeleteMessage(message, name, senderID));
            } else {
                Managers.CHAT.sendDeleteMessage(message, name, senderID);
            }
        }
    }
}
