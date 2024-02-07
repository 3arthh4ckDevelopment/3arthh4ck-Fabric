package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface IChatHud {
    boolean replace(MutableText component,
                    int id,
                    boolean wrap,
                    boolean returnFirst);

    void earthhack$invokeAddMessage(Text text, @Nullable MessageSignatureData sig, int time,
                                    @Nullable MessageIndicator indicator, boolean refresh);
    void earthhack$remove(@Nullable MessageSignatureData signature, boolean all);

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
