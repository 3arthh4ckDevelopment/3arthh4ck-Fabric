package me.earth.earthhack.impl.modules.misc.nointeract;

import me.earth.earthhack.api.module.data.DefaultData;

public class NoInteractData extends DefaultData<NoInteract> {
    public NoInteractData(NoInteract module){
        super(module);
        register(module.sneak, "Allows interactions if sneaking.");
        register(module.tileOnly, "Disables interactions with all " +
                "BlockEntities (e.g. Anvils, Beds, Chests etc.).");
    }

    @Override
    public String getDescription(){
        return "Removes interactions with blocks.";
    }
}
