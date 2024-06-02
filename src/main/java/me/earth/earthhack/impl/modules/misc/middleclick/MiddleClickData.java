package me.earth.earthhack.impl.modules.misc.middleclick;

import me.earth.earthhack.api.module.data.DefaultData;

public class MiddleClickData extends DefaultData<MiddleClick> {

    public MiddleClickData(MiddleClick module)
    {
        super(module);
        register(module.keyBind, "The activation bind");
        register(module.pickBlock, "Vanilla pickblock feature on the bind");
        register(module.cancelPickBlock, "Cancel the vanilla bind for clickblock");
        register(module.entities, "Friends the entity you're looking at when you press the bind");
        register(module.air, "Throws an ender pearl if you're looking to air");
        register(module.bindMode, "Release bind/press bind setting");
    }

    @Override
    public int getColor()
    {
        return 0xff93FFA8;
    }

    @Override
    public String getDescription()
    {
        return "Middleclick on players to friend/unfriend them.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"MiddleClick", "MiddleClickFriend", "MiddleClickPearl"};
    }
}
