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

    int earthhack$getScrollPos();

    void earthhack$setScrollPos(int pos);

    boolean earthhack$getScrolled();

    void earthhack$setScrolled(boolean scrolled);

    void earthhack$invokeSetChatLine(Text chatComponent,
                                     int chatLineId,
                                     int updateCounter,
                                     boolean displayOnly);

    void earthhack$invokeClearChat(boolean sent);
}
