package me.earth.earthhack.impl.gui.chat.components.values;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * A ValueComponent.
 *
 * These Components display the value of the given Setting.
 */
public class ValueComponent extends SuppliedComponent
{
    private final Setting<?> setting;

    public ValueComponent(Setting<?> setting)
    {
        super(() ->
        {
            if (setting.getValue() == null)
            {
                return "null";
            }

            if (setting instanceof StringSetting
                    && setting.getValue().toString().isEmpty())
            {
                return "<...>";
            }

            if (setting instanceof StringSetting
                && ((StringSetting) setting).isPassword())
            {
                return ((StringSetting) setting).censor();
            }

            return setting.getValue().toString();
        });

        this.setting = setting;
    }

    @Override
    public MutableText copy()
    {
        ValueComponent copy = new ValueComponent(setting);
        copy.setStyle(this.getStyle());

        for (Text sibling : this.getSiblings())
        {
            copy.append(sibling.copy());
        }

        return copy;
    }

}
