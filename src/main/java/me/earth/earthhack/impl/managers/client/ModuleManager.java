package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.rpc.RPC;

import java.util.ArrayList;

public class ModuleManager extends IterationRegister<Module>
{
    public void init()
    {
        Earthhack.getLogger().info("Initializing Modules.");
        /* ----- CLIENT ----- */
        this.forceRegister(new Commands());
        this.forceRegister(new PingBypassModule());
        this.forceRegister(new RPC());

        Bus.EVENT_BUS.post(new PostInitEvent());
    }

    public void load()
    {
        Caches.setManager(this);
        for (Module module : getRegistered())
        {
            module.load();
        }
    }

    @Override
    public void unregister(Module module) throws CantUnregisterException
    {
        super.unregister(module);
        module.setRegistered(false);
        Bus.EVENT_BUS.unsubscribe(module);
    }

    protected void forceRegister(Module module)
    {
        registered.add(module);
        module.setRegistered(true);
        if (module instanceof Registrable)
        {
            ((Registrable) module).onRegister();
        }
    }

    public ArrayList<Module> getModulesFromCategory(Category moduleCategory) {
        final ArrayList<Module> iModules = new ArrayList<>();
        for (Module iModule : getRegistered()) {
            if (iModule.getCategory() == moduleCategory) iModules.add(iModule);
        }
        return iModules;
    }
}
