package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import net.minecraft.text.MutableText;

public abstract class ChatEvent extends Event
{
    protected final IChatHud gui;

    public ChatEvent(IChatHud gui)
    {
        this.gui = gui;
    }

    public abstract void invoke();

    public static class Clear extends ChatEvent
    {
        private boolean sent;

        public Clear(IChatHud gui, boolean sent)
        {
            super(gui);
        }

        @Override
        public void invoke()
        {
            gui.invokeClearChat(sent);
        }

        public boolean clearsSent()
        {
            return sent;
        }

        public void setSent(boolean sent)
        {
            this.sent = sent;
        }
    }

    public static class Log extends ChatEvent
    {
        public Log(IChatHud gui)
        {
            super(gui);
        }

        @Override
        public void invoke() { }
    }

    public static class Send extends ChatEvent
    {
        private MutableText chatComponent;
        private int chatLineId;
        private int updateCounter;
        private boolean displayOnly;

        public Send(IChatHud gui,
                    MutableText chatComponent,
                    int chatLineId,
                    int updateCounter,
                    boolean displayOnly)
        {
            super(gui);
            this.chatComponent = chatComponent;
            this.chatLineId    = chatLineId;
            this.updateCounter = updateCounter;
            this.displayOnly   = displayOnly;
        }

        @Override
        public void invoke()
        {
            gui.invokeSetChatLine(chatComponent,
                    chatLineId,
                    updateCounter,
                    displayOnly);
        }

        public MutableText getChatComponent()
        {
            return chatComponent;
        }

        public void setChatComponent(MutableText chatComponent)
        {
            this.chatComponent = chatComponent;
        }

        public int getChatLineId()
        {
            return chatLineId;
        }

        public void setChatLineId(int chatLineId)
        {
            this.chatLineId = chatLineId;
        }

        public int getUpdateCounter()
        {
            return updateCounter;
        }

        public void setUpdateCounter(int updateCounter)
        {
            this.updateCounter = updateCounter;
        }

        public boolean isDisplayOnly()
        {
            return displayOnly;
        }

        public void setDisplayOnly(boolean displayOnly)
        {
            this.displayOnly = displayOnly;
        }
    }

}
