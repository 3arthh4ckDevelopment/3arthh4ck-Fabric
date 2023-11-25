package me.earth.earthhack.impl.modules.client.autoconfig;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.setting.DefaultComponent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RemovingStringComponent
        extends DefaultComponent<String, RemovingString>
{
    public RemovingStringComponent(RemovingString setting)
    {
        super(setting);
        if (setting.getContainer() instanceof Module)
        {
            Module module = (Module) setting.getContainer();
            HoverEvent event = new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Text.empty().append("Removes this Setting"));

            this.append(Text.empty().append(
                    TextColor.RED + " Remove ")
                    .setStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withClickEvent(
                            new SmartClickEvent(ClickEvent.Action.RUN_COMMAND)
                            {
                                @Override
                                public String getValue()
                                {
                                    return Commands.getPrefix()
                                            + "hiddensetting "
                                            + module.getName()
                                            + " \""
                                            + setting.getName()
                                            + "\" remove";
                                }
                            })
                        .withHoverEvent(event))
            );
        }
    }

    @Override
    public String getText()
    {
        return setting.getName()
                + TextColor.GRAY
                + " : "
                + TextColor.GOLD;
    }

}
