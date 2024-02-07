package me.earth.earthhack.impl.util.text;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.function.Consumer;

public class ChatUtil implements Globals {
    private static final Random RND = new Random();

    public static void sendMessage(String message, String identifier)
    {
        if (mc.inGameHud != null)
            sendMessage(((IChatHud) mc.inGameHud.getChatHud()), Text.of(message == null ? "null" : message), identifier);
    }

    public static void sendMessage(IChatHud chat, Text message, @Nullable String identifier) {
        var signature = identifier != null ? new MessageSignatureData(forgeSignature(identifier)) : null;
        deleteMessage(signature);
        chat.earthhack$invokeAddMessage(message, signature, mc.inGameHud.getTicks(), MessageIndicator.system(), false);
    }

    public static void deleteMessage(@Nullable MessageSignatureData signature) {
        ((IChatHud) mc.inGameHud.getChatHud()).earthhack$remove(signature, true);
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

    public static void sendMessageScheduled(String message, String identifier) {
        mc.execute(() -> sendMessage(message, identifier));
    }

    public static String generateRandomHexSuffix(int places)
    {
        return "[" + Integer.toHexString((RND.nextInt() + 11)
                * RND.nextInt()).substring(0, places) + "]";
    }

    private static byte[] forgeSignature(String identifier) {
        byte[] bytes = new byte[256];
        byte[] identifierBytes = identifier.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(identifierBytes, 0, bytes, 0, Math.min(bytes.length, identifierBytes.length));
        return bytes;
    }
}
