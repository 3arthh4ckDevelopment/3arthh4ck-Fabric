package me.earth.earthhack.impl.hud.text.cps;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//TODO: simplify maybe
public class Cps extends HudElement {

    private final Setting<String> name =
            register(new StringSetting("CustomName", "Cps "));
    private final Setting<CountMode> mode =
            register(new EnumSetting<>("Mode", CountMode.Place));

    private final List<BlockPos> attack = new ArrayList<>();
    private final Map<BlockPos, Long> place = new ConcurrentHashMap<>();
    private final Map<Integer, BlockPos> ids = new ConcurrentHashMap<>();
    private final List<Integer> time = new ArrayList<>();

    protected void onRender(DrawContext context) {
        time.removeIf(i -> (int) System.currentTimeMillis() - i > 1000);
        HudRenderUtil.renderText(context, name.getValue() + TextColor.GRAY + time.size(), getX(), getY());
    }

    public Cps() {
        super("Cps", "Displays how many crystals are placed/destroyed every second.", HudCategory.Text, 130, 30);

        this.mode.addObserver(e -> {
            attack.clear();
            place.clear();
            ids.clear();
            time.clear();
        });

        this.listeners.add(new ReceiveListener<>(PlaySoundS2CPacket.class, e ->
        {
            if (mode.getValue() == CountMode.Break) {
                PlaySoundS2CPacket p = e.getPacket();
                if (p.getCategory() == SoundCategory.BLOCKS
                        && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    BlockPos pos = new BlockPos((int) p.getX(), (int) (p.getY() - 1), (int) p.getZ());
                    if (attack.contains(pos)) {
                        attack.remove(pos);
                        time.add((int) System.currentTimeMillis());
                    }
                }
            }
        }));

        this.listeners.add(new PostSendListener<>(PlayerInteractEntityC2SPacket.class, e ->
        {
            if (mode.getValue() == CountMode.Break) {
                Entity entity = e.getPacket().getEntity(mc.player.getCommandSource().getWorld());
                BlockPos pos;
                if (entity == null) {
                    pos = ids.get(entity.getId());
                } else {
                    pos = entity.getBlockPos().down();
                }

                if (pos != null) {
                    attack.add(pos);
                }
            }
        }));

        this.listeners.add(new ReceiveListener<>(EntitySpawnS2CPacket.class, e ->
        {
            if (e.getPacket().getEntityData() == 51) {
                BlockPos pos = new BlockPos((int) e.getPacket().getX(),
                        (int) (e.getPacket().getY() - 1),
                        (int) e.getPacket().getZ());

                if (mode.getValue() == CountMode.Place) {
                    Long l = place.remove(pos);
                    if (l != null) {
                        time.add((int) System.currentTimeMillis());
                    }
                } else {
                    ids.put(e.getPacket().getEntityId(), pos);
                }
            }
        }));

        this.listeners.add(new PostSendListener<>(
                PlayerInteractBlockC2SPacket.class, e ->
        {
            if (mode.getValue() == CountMode.Place) {
                if (mc.player.getStackInHand(e.getPacket().getHand()).getItem()
                        == Items.END_CRYSTAL
                        && !place.containsKey(e.getPacket().getBlockHitResult().getBlockPos())) {
                    place.put(e.getPacket().getBlockHitResult().getBlockPos(), System.currentTimeMillis());
                }
            }
        }));
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(name.getValue().trim() + "00");
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

    private enum CountMode {
        Place,
        Break
    }
}
