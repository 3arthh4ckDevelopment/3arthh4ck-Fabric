package me.earth.earthhack.impl.modules.misc.extratab;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.gui.MixinPlayerListHud;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

public class ExtraTab extends Module
{
    private static final ModuleCache<Media> MEDIA =
        Caches.getModule(Media.class);

    protected final Setting<Integer> size =
            register(new NumberSetting<>("Size", 250, 0, 500));

    public ExtraTab()
    {
        super("ExtraTab", Category.Misc);
        register(new BooleanSetting("Download-Threads", false));
        register(new BooleanSetting("Ping", false));
        register(new BooleanSetting("Bars", true));
        this.setData(new ExtraTabData(this));
    }

    /**
     * {@link MixinPlayerListHud}
     *
     * @param defaultSize if off, the default size to return.
     * @return size of the player tab list.
     */
    public int getSize(int defaultSize)
    {
        return this.isEnabled() ? size.getValue() : defaultSize;
    }

    /**
     * {@link MixinPlayerListHud}
     *
     * @param info the player info.
     * @return name to display on the tab list.
     */
    public String getName(PlayerListEntry info)
    {
        String name = info.getDisplayName() != null
                ? info.getDisplayName().getString()
                : Team.decorateName(
                    info.getScoreboardTeam(),
                    Text.of(info.getProfile().getName()))
                        .getString();

        String finalName = name;
        name = MEDIA.returnIfPresent(m -> m.convert(finalName), name);

        if (this.isEnabled())
        {
            if (Managers.FRIENDS.contains(finalName))
            {
                return TextColor.AQUA + name;
            }
            else if (Managers.ENEMIES.contains(finalName))
            {
                return TextColor.RED + name;
            }
        }

        return name;
    }

}
