package me.earth.earthhack.impl.modules.misc.antipackets;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.observable.Observer;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.gui.visibility.NumberPageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.misc.antipackets.util.Page;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AntiPackets extends Module
{
    private final Map<Class<? extends Packet<?>>, BooleanSetting> client;
    private final Map<Class<? extends Packet<?>>, BooleanSetting> server;
    private final Setting<Boolean> unknown;
    private int settings;

    public AntiPackets()
    {
        super("AntiPackets", Category.Misc);

        client = new HashMap<>();
        server = new HashMap<>();
        unknown = new BooleanSetting("Unknown", false);

        this.listeners.add(new ListenerCPacket(this));
        this.listeners.add(new ListenerSPacket(this));
        AntiPacketData data = new AntiPacketData(this);
        this.setData(data);


        for (Class<? extends Packet<?>> clazz : PacketUtil.getAllPackets())
        {
            // we skip inner classes, we get them anyway with outer name
            if (clazz.getName().contains("$"))
            {
                continue;
            }
            String simpleName = FabricLoader.getInstance().getMappingResolver().unmapClassName("intermediate", clazz.getName());
            boolean side = simpleName.endsWith("S2CPacket");
            getMap(side).put(clazz, new BooleanSetting(
                                        formatPacketName(simpleName),
                                        false));

            for (Class<?> inner : clazz.getDeclaredClasses())
            {
                if (inner.getSuperclass() == clazz)
                {
                    String sin = FabricLoader.getInstance().getMappingResolver().unmapClassName("intermediate", inner.getName());
                    BooleanSetting s = new BooleanSetting(
                                    formatPacketName(simpleName)
                                    + "-"
                                    + sin,
                                    false);

                    getMap(side).put((Class<? extends Packet<?>>) inner, s);
                }
            }
        }

        EnumSetting<Page> pageEnumSetting =
                register(new EnumSetting<>("Page", Page.CPackets));

        client.values().forEach(s ->
            Visibilities.VISIBILITY_MANAGER.registerVisibility(s, () ->
                    pageEnumSetting.getValue() == Page.CPackets));
        server.values().forEach(s ->
            Visibilities.VISIBILITY_MANAGER.registerVisibility(s, () ->
                    pageEnumSetting.getValue() == Page.SPackets));

        Iterable<BooleanSetting> sortedC = sorted(client.values());
        Iterable<BooleanSetting> sortedS = sorted(server.values());

        registerSettings(sortedC, data);
        registerSettings(sortedS, data);

        Setting<Integer> sPacketPages =
            NumberPageBuilder.autoPage(this, "S2CPackets", 8, sortedS)
                             .withConversion(Visibilities::andComposer)
                             .reapplyConversion()
                             .setPagePositionAfter("Page")
                             .register(Visibilities.VISIBILITY_MANAGER)
                             .registerPageSetting()
                             .getPageSetting();

        Setting<Integer> cPacketPages =
            NumberPageBuilder.autoPage(this, "C2SPackets", 8, sortedC)
                             .withConversion(Visibilities::andComposer)
                             .reapplyConversion()
                             .setPagePositionAfter("Page")
                             .register(Visibilities.VISIBILITY_MANAGER)
                             .registerPageSetting()
                             .getPageSetting();

        Visibilities.VISIBILITY_MANAGER.registerVisibility(sPacketPages, () ->
                pageEnumSetting.getValue() == Page.SPackets);
        Visibilities.VISIBILITY_MANAGER.registerVisibility(cPacketPages, () ->
                pageEnumSetting.getValue() == Page.CPackets);

        Function<Setting<Boolean>,Observer<SettingEvent<Boolean>>> f = s -> e ->
        {
            if (!e.getValue().equals(s.getValue()))
            {
                if (e.getValue())
                {
                    settings++;
                }
                else
                {
                    settings--;
                }
            }
        };

        register(unknown).addObserver(f.apply(unknown));
        client.values().forEach(s -> s.addObserver(f.apply(s)));
        server.values().forEach(s -> s.addObserver(f.apply(s)));
        data.register(unknown.getName(), "Cancels unknown packets.");
    }

    @Override
    public String getDisplayInfo()
    {
        return settings + "";
    }

    private Iterable<BooleanSetting> sorted(Collection<BooleanSetting> settings)
    {
        return settings.stream()
                       .sorted(Comparator.comparing(Setting::getName))
                       .collect(Collectors.toList());
    }

    private void registerSettings(Iterable<BooleanSetting> settings,
                                  AntiPacketData data)
    {
        for (BooleanSetting s : settings)
        {
            register(s);
            data.register(s.getName(), "Cancels " + s.getName() + " packets.");
        }
    }

    /**
     * Cancels the event if there's an active setting for the event's
     * packet.
     *
     * @param event the event to cancel.
     */
    protected void onPacket(PacketEvent<?> event, boolean receive)
    {
        BooleanSetting s = receive
                            ? server.get(event.getPacket().getClass())
                            : client.get(event.getPacket().getClass());
        if (s == null)
        {
            if (event.getPacket() instanceof ServerPlayPacketListener
                || event.getPacket() instanceof ClientPlayPacketListener) {
                return;
            }

            Earthhack.getLogger().info("Unknown packet: " + event.getPacket()
                                                                 .getClass()
                                                                 .getName());
            if (unknown.getValue())
            {
                event.setCancelled(true);
            }
        }
        else if (s.getValue())
        {
            event.setCancelled(true);
        }
    }

    private String formatPacketName(String name)
    {
        if (name.endsWith("S2CPacket") || name.endsWith("C2SPacket"))
        {
            return name.charAt(0) + name.substring(name.split("Packet")[0].length() - 3);
        }

        return name;
    }

    /**
     * @param side if the server map should be returned.
     * @return the server map if side == true otherwise the client map.
     */
    private Map<Class<? extends Packet<?>>, BooleanSetting> getMap(boolean side)
    {
        return side ? server : client;
    }

}
