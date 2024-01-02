package me.earth.earthhack.impl.hud.text.arraylist;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.modules.PbModule;

import java.util.*;

final class ListenerPostKey extends EventListener<KeyboardEvent.Post> implements Globals
{
    private final Set<Module> addedModules = new HashSet<>();
    private final HudArrayList module;

    public ListenerPostKey(HudArrayList module)
    {
        super(KeyboardEvent.Post.class);
        this.module = module;
    }

    @Override
    public void invoke(KeyboardEvent.Post event)
    {
        if (mc.player == null || mc.world == null) {
            return;
        }

        module.modules.clear();
        addedModules.clear();

        if (module.moduleRender.getValue() != null) {
            for (Module mod : Managers.MODULES.getRegistered()) {
                if (mod.isEnabled() && mod.isHidden() != Hidden.Hidden) {
                    Map.Entry<String, Module> entry = new AbstractMap.SimpleEntry<>(module.getHudName(mod), mod);
                    addedModules.add(mod);
                    module.modules.add(entry);
                }
            }

            /*
            for (Module mod : PingBypass.MODULES.getRegistered()) {
                if (mod.isEnabled() && mod.isHidden() != Hidden.Hidden
                        && !(addedModules.contains(mod) || mod instanceof PbModule && addedModules.contains(((PbModule) mod).getModule())))
                {
                    Map.Entry<String, Module> entry = new AbstractMap.SimpleEntry<>(module.getHudName(mod), mod);
                    addedModules.add(mod);
                    module.modules.add(entry);
                }
            }
            //TODO: pb
             */

            if (module.moduleRender.getValue() == Modules.Length)
                module.modules.sort(Comparator.comparing(entry -> Managers.TEXT.getStringWidth(entry.getKey()) *  -1));
            else
                module.modules.sort(Map.Entry.comparingByKey());
        }
    }

}
