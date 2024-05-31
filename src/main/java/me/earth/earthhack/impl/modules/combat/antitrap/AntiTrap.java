package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;

import java.util.*;

// TODO: INSTANT!!!!
public class AntiTrap extends ObbyListenerModule<ListenerAntiTrap>
{
    private static final ModuleCache<Offhand> OFFHAND =
        Caches.getModule(Offhand.class);

    protected final Setting<AntiTrapMode> mode =
        registerBefore(new EnumSetting<>("Mode", AntiTrapMode.Crystal), blocks);
    protected final Setting<Boolean> offhand =
        register(new BooleanSetting("Offhand", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> waitForMine =
            register(new BooleanSetting("WaitForMine", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Integer> timeOut =
        register(new NumberSetting<>("TimeOut", 400, 0, 1000))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> mineTime =
            register(new NumberSetting<>("MineTime", 400, 0, 2000))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> empty   =
        register(new BooleanSetting("Empty", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> swing   =
        register(new BooleanSetting("Swing", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Float> mineRange =
        register(new NumberSetting<>("MineRange", 1.0f, 1.0f, 4.0f))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> highFill =
        register(new BooleanSetting("HighFill", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> highFacePlace =
        register(new BooleanSetting("HighFacePlace", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> autoOff =
        register(new BooleanSetting("Auto-Off", true));
    protected final Setting<Boolean> requireOffhand =
        register(new BooleanSetting("RequireOffhand", false));

    /** Blocks that have been placed and await a SPacketBlockChange */
    protected final Map<BlockPos, Long> placed = new HashMap<>();
    /** Positions that have been confirmed by a SPacketBlockChange */
    protected final Set<BlockPos> confirmed = new HashSet<>();
    /** Positions that have been hit and can be placed on */
    protected final Set<BlockPos> hit = new HashSet<>();

    /** Manages the {@link AntiTrap#timeOut}. */
    protected final StopWatch interval = new StopWatch();
    protected BlockHitResult result;
    protected OffhandMode previous;
    protected BlockPos startPos;
    protected BlockPos pos;

    public AntiTrap()
    {
        super("AntiTrap", Category.Combat);
        this.listeners.add(new ListenerBreakingProgress(this));
        this.setData(new AntiTrapData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        previous = null;
        placed.clear();
        confirmed.clear();
        if (super.checkNull() && interval.passed(timeOut.getValue()))
        {
            interval.reset();
            result = null;
            pos    = null;
            startPos = PositionUtil.getPosition();
        }
        else
        {
            this.disable();
        }
    }

    @Override
    protected void onDisable()
    {
        if (offhand.getValue() && previous != null)
        {
            OFFHAND.computeIfPresent(o -> o.setMode(previous));
        }
    }

    @Override
    protected ListenerAntiTrap createListener()
    {
        return new ListenerAntiTrap(this);
    }

    @Override
    public boolean execute()
    {
        if (offhand.getValue()
            && this.listener.mode != AntiTrapMode.Crystal
            && OFFHAND.isEnabled())
        {
            OffhandMode previous = this.previous;
            if (packets.isEmpty()
                && previous != null
                && OFFHAND.get().getMode() == OffhandMode.OBSIDIAN)
            {
                OFFHAND.get().setMode(previous);
                this.previous = null;
            }
            else if (!packets.isEmpty()
                && OFFHAND.get().getMode() != OffhandMode.OBSIDIAN
                && OFFHAND.get().isSafe())
            {
                this.previous = OFFHAND.get().getMode();
                OFFHAND.get().setMode(OffhandMode.OBSIDIAN);
                OFFHAND.get().doOffhand();
            }

            if (requireOffhand.getValue()
                && !InventoryUtil.isHolding(Blocks.OBSIDIAN))
            {
                return false;
            }
        }

        return super.execute();
    }

    @Override
    public boolean placeBlock(BlockPos pos)
    {
        boolean hasPlaced = super.placeBlock(pos);
        if (hasPlaced)
        {
            placed.put(pos, System.currentTimeMillis());
        }

        return hasPlaced;
    }

    protected List<BlockPos> getCrystalPositions()
    {
        List<BlockPos> result = new ArrayList<>();
        BlockPos playerPos = PositionUtil.getPosition();
        if (!mc.world.getEntitiesByClass(EndCrystalEntity.class,
                    new Box(playerPos.toCenterPos(), playerPos.up().add(1, 2, 1).toCenterPos()), e -> true)
               .isEmpty())
        {
            this.disable();
            return result;
        }

        for (Vec3i vec : AntiTrapMode.Crystal.getOffsets())
        {
            BlockPos pos = playerPos.add(vec);
            if (BlockUtil.canPlaceCrystal(pos, false, false))
            {
                result.add(pos);
            }
        }

        return result;
    }

}
