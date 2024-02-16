package me.earth.earthhack.impl.modules.render.breakesp;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.DistanceUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreakESP extends BlockESPModule {

    protected final Setting<Integer> radius =
            register(new NumberSetting<>("Radius", 5, 1, 100));
    protected final Setting<Boolean> chatPos =
            register(new BooleanSetting("ChatPosition", false));

    List<BreakESPBlock> blocks = new ArrayList<>();
    Random random = new Random();

    public BreakESP() {
        super("BreakESP", Category.Render);
        this.listeners.add(new ListenerBlockBreakAnimation(this));

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, event -> {
            if (!blocks.isEmpty()) {
                for (int i = 0; i < blocks.size(); i++) {
                    try {
                        PlayerEntity enemyPlayer = findEnemy(blocks.get(i).entityID);
                        if (System.currentTimeMillis() - blocks.get(i).time > 4000
                                || Math.sqrt(DistanceUtil.distanceSq(blocks.get(i).blockPos.getX(), blocks.get(i).blockPos.getY(), blocks.get(i).blockPos.getZ(), enemyPlayer.getX(), enemyPlayer.getY(), enemyPlayer.getZ())) > 6.5)
                            blocks.remove(blocks.get(i));
                        else
                            renderPos(event.getStack(), blocks.get(i).blockPos);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }

    PlayerEntity findEnemy(int id) {
        for (PlayerEntity player : Managers.ENTITIES.getPlayers())
            if (player.getId() == id)
                return player;
        return mc.player;
    }
}

class BreakESPBlock {
    public BreakESPBlock(BlockPos blockPos, int entityID, long time) {
        this.blockPos = blockPos;
        this.entityID = entityID;
        this.time = time;
    }

    BlockPos blockPos;
    int entityID;
    long time;
}
