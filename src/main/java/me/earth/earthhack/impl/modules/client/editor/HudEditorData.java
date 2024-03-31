package me.earth.earthhack.impl.modules.client.editor;

import me.earth.earthhack.api.module.data.DefaultData;

final class HudEditorData extends DefaultData<HudEditor>
{
    public HudEditorData(HudEditor module) {
        super(module);
        register(module.show, "If you want to render the HUD.");
        register(module.colorMode, "If you want a Rainbow on the HUD.");
        register(module.color, "The color of the HUD text.");
        register(module.shadow, "If you want to modify the Minecraft shadow.");
        register(module.bracketsColor, "The custom brackets color");
        register(module.insideText, "The color of the Text setting inside the shadow.");
        register(module.brackets, """
                The custom brackets:
                 Use the ':' sign to separate the beginning bracket from the ending one.
                Example:
                 [:] -> [Strafe]
                 (:) -> (Strafe)""");
    }
}
