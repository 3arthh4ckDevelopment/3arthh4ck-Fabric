package me.earth.earthhack.impl.modules.misc.nosoundlag;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SimpleSoundObserver;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.Set;

public class NoSoundLag extends Module
{
    protected static final Set<SoundEvent> ARMOR_SOUNDS = Sets.newHashSet
    (
        SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(),
        SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(),
        SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND.value(),
        SoundEvents.ITEM_ARMOR_EQUIP_IRON.value(),
        SoundEvents.ITEM_ARMOR_EQUIP_GOLD.value(),
        SoundEvents.ITEM_ARMOR_EQUIP_CHAIN.value(),
        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value()
    );

    protected static final Set<SoundEvent> WITHER_SOUNDS = Sets.newHashSet
    (
            SoundEvents.ENTITY_WITHER_AMBIENT,
            SoundEvents.ENTITY_WITHER_DEATH,
            SoundEvents.ENTITY_WITHER_BREAK_BLOCK,
            SoundEvents.ENTITY_WITHER_HURT,
            SoundEvents.ENTITY_WITHER_SPAWN,
            SoundEvents.ENTITY_WITHER_SHOOT
    );

    protected final Setting<Boolean> armor =
            register(new BooleanSetting("Armor", true));
    protected final Setting<Boolean> crystals =
            register(new BooleanSetting("Crystals", false));
    protected final Setting<Boolean> withers =
            register(new BooleanSetting("Withers", true));


    protected final SoundObserver observer =
            new SimpleSoundObserver(crystals::getValue);

    public NoSoundLag()
    {
        super("NoSoundLag", Category.Misc);
        this.listeners.add(new ListenerSound(this));
        this.setData(new NoSoundLagData(this));
    }

    @Override
    protected void onEnable()
    {
        Managers.SET_DEAD.addObserver(observer);
    }

    @Override
    protected void onDisable()
    {
        Managers.SET_DEAD.removeObserver(observer);
    }

}

