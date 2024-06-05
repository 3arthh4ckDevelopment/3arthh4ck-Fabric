package me.earth.earthhack.impl.modules.misc.settingspoof;

import net.minecraft.network.message.ChatVisibility;

public enum ChatVisibilityTranslator
{
    Full(ChatVisibility.FULL),
    System(ChatVisibility.SYSTEM),
    Hidden(ChatVisibility.HIDDEN);

    private final ChatVisibility visibility;

    ChatVisibilityTranslator(ChatVisibility visibility)
    {
        this.visibility = visibility;
    }

    public ChatVisibility getVisibility()
    {
        return visibility;
    }
}
