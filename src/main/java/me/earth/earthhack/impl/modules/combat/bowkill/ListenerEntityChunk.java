package me.earth.earthhack.impl.modules.combat.bowkill;

import com.mojang.text2speech.Narrator;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.EntityChunkEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.Formatting;

final class ListenerEntityChunk extends ModuleListener<BowKiller, EntityChunkEvent> {
    private final Narrator narrator = Narrator.getNarrator();

    public ListenerEntityChunk(BowKiller module) {
        super(module, EntityChunkEvent.class);
    }

    @Override
    public void invoke(EntityChunkEvent event) {
        if (!module.oppSpotted.getValue())
            return;

        if (event.getStage() == Stage.PRE && event.getEntity() != null) {
            if (module.isValid(event.getEntity())) {
                ChatUtil.sendMessage(Formatting.RED + "Opp detected I repeat opp detected!");
                if (!module.hasEntity(event.getEntity().getUuidAsString())) {
                    narrator.clear();
                    narrator.say("Ah pu  Detected!", true);
                    module.entityDataArrayList.add(new BowKiller.EntityData(event.getEntity().getUuidAsString(),
                            System.currentTimeMillis()));
                }
            }
        }
    }
}
