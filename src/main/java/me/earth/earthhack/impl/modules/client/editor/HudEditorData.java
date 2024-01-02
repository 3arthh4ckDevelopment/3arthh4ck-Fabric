package me.earth.earthhack.impl.modules.client.editor;

import me.earth.earthhack.api.module.data.DefaultData;

final class HudEditorData extends DefaultData<HudEditor>
{
    public HudEditorData(HudEditor module) {
        super(module);
        register(module.show, "Shows the hud");
        register(module.colorMode, "The hud color mode");
        register(module.color, "The hud text color");
        register(module.shadow, "The text shadow");
        register(module.testShadow, "Test shadow [DEV SETTING], it should work the same as normal shadow, maybe some issues...");
        register(module.bracketsColor, "The custom brackets color");
        register(module.insideText, "The text inside the brackets color");
        register(module.brackets, "The custom brackets:\n " +
                "use the : to separate the beginning bracket from the ending one.\n" +
                "Example:\n [:] --> [text will be here]\n (:) --> (text will be here)");


    }
}
