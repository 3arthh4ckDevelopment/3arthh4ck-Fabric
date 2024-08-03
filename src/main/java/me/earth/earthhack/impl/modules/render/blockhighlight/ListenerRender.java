package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

final class ListenerRender extends ModuleListener<BlockHighlight, Render3DEvent> {
    private static final ModuleCache<Speedmine> SPEED_MINE =
            Caches.getModule(Speedmine.class);

    public ListenerRender(BlockHighlight module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK && mc.crosshairTarget instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (mc.world.getWorldBorder().contains(pos) && (!SPEED_MINE.isEnabled() || !pos.equals(SPEED_MINE.get().getPos()))) {
                BlockState state = mc.world.getBlockState(pos);
                if (!state.isAir()) {
                    VoxelShape voxelShape = state.getOutlineShape(mc.world, pos);
                    if (!voxelShape.isEmpty()) {
                        Box bb = voxelShape.getBoundingBox();
                        renderBlockHilight(event, pos, bb);
                    }
                }
            }
        }
    }

    private void renderBlockHilight(Render3DEvent event, BlockPos pos, Box bb) {
        if (!bb.equals(module.currentBB)) {
            module.slideBB = module.currentBB;
            module.currentBB = bb;
            module.slideTimer.reset();
        }

        double factor;
        Box slide;
        if (module.slide.getValue()
                && (slide = module.slideBB) != null
                && (factor = module.slideTimer.getTime() / Math.max(1.0, module.slideTime.getValue())) < 1.0) {
            Box renderBB = new Box(
                    slide.minX + (bb.minX - slide.minX) * factor,
                    slide.minY + (bb.minY - slide.minY) * factor,
                    slide.minZ + (bb.minZ - slide.minZ) * factor,
                    slide.maxX + (bb.maxX - slide.maxX) * factor,
                    slide.maxY + (bb.maxY - slide.maxY) * factor,
                    slide.maxZ + (bb.maxZ - slide.maxZ) * factor
            );

            module.renderInterpAxis(event.getStack(), Interpolation.interpolateAxis(pos, renderBB).expand(0.002));
        } else {
            module.renderInterpAxis(event.getStack(), Interpolation.interpolateAxis(pos, bb).expand(0.002));
        }
    }

}
