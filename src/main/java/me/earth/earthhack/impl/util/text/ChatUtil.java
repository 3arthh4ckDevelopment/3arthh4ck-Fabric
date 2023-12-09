package me.earth.earthhack.impl.util.text;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Random;
import java.util.function.Consumer;

public class ChatUtil implements Globals {
    private static final Random RND = new Random();

    public static void sendMessage(String message)
    {
        sendMessage(Text.of(
                message == null ? "null" : message));
    }

    public static void sendMessage(Text messageContent) {
        if (mc.world == null)
            return;
        MutableText message = Text.empty();
        message.append(messageContent);
        
        mc.inGameHud.getChatHud().addMessage(message);
    }

    public static void deleteMessage(int id) {
        // TODO: mc.inGameHud.getChatHud().removeMessage();
    }

    public static void applyIfPresent(Consumer<IChatHud> consumer)
    {
        ChatHud chat = getChatGui();
        if (chat != null)
        {
            consumer.accept((IChatHud) chat);
        }
    }

    public static ChatHud getChatGui() {
        /* Unnecessary?? */
        return mc.inGameHud != null
                ? mc.inGameHud.getChatHud()
                : null;
    }

    public static void sendMessageScheduled(String message) {
        mc.execute(() -> sendMessage(message));
    }

    public static String generateRandomHexSuffix(int places)
    {
        return "[" + Integer.toHexString((RND.nextInt() + 11)
                * RND.nextInt()).substring(0, places) + "]";
    }
}
