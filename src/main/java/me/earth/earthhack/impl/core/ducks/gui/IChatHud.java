package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface IChatHud {
    boolean replace(MutableText component,
                    int id,
                    boolean wrap,
                    boolean returnFirst);

    int getScrollPos();

    void setScrollPos(int pos);

    boolean getScrolled();

    void setScrolled(boolean scrolled);

    void invokeSetChatLine(Text chatComponent,
                           int chatLineId,
                           int updateCounter,
                           boolean displayOnly);

    void invokeClearChat(boolean sent);
}
