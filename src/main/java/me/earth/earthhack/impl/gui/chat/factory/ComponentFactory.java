package me.earth.earthhack.impl.gui.chat.factory;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.setting.*;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ComponentFactory
{
    private static final Map<Class<? extends Setting<?>>,
            IComponentFactory<?, ?>> FACTORIES = new HashMap<>();

    static
    {
        register(ColorSetting.class, ColorComponent::new);
        register(BindSetting.class, BindComponent::new);
        register(BooleanSetting.class, BooleanComponent::new);
        register(StringSetting.class, StringComponent::new);

        // FACTORIES.put((Class<? extends Setting<?>>) EnumSetting.class, EnumComponent.FACTORY); //TODO: fix this asap
        // FACTORIES.put((Class<? extends Setting<?>>) NumberSetting.class, NumberComponent.FACTORY);
    }

    public static <E, T extends Setting<E>> IComponentFactory<?, ?>
                register(Class<T> clazz, IComponentFactory<E, T> factory)
    {
        return FACTORIES.put(clazz, factory);
    }

    public static <T, S extends Setting<T>> SettingComponent<T, S>
                    create(S setting)
    {
        IComponentFactory<T, S> factory =
                (IComponentFactory<T, S>) FACTORIES.get(setting.getClass());
        if (factory == null)
        {
            return new DefaultComponent<>(setting);
        }

        return factory.create(setting);
    }

    /**
     * Creates a Hover Event with the ModuleData Description
     * for the given setting if possible.
     *
     * @param setting the setting to get a HoverEvent for.
     * @return a HoverEvent for the given Setting.
     */
    public static HoverEvent getHoverEvent(Setting<?> setting)
    {
        if (setting == null)
        {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.empty().append("null"));
        }

        ModuleData<?> data = null;
        if (setting.getContainer() instanceof Module)
        {
            data = ((Module) setting.getContainer()).getData();
        }

        String description = "A Setting. ("
                + setting.getInitial().getClass().getSimpleName()
                + ")";

        if (data != null)
        {
            String dataDescription = data.settingDescriptions().get(setting);
            if (dataDescription != null)
            {
                description = dataDescription;
            }
        }

        return ChatComponentUtil.setOffset(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Text.empty().append(description)));
    }

}
