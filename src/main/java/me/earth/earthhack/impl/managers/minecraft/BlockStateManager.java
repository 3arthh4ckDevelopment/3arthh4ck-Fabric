package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

// TODO: SPacketBlock/MultiBlockChange Listeners
//  in most modules can be replaced with this
public class BlockStateManager extends SubscriberImpl implements Globals
{
    private final Map<BlockPos, Queue<Consumer<BlockState>>> callbacks =
            new ConcurrentHashMap<>();

    public BlockStateManager()
    {
        this.listeners.add(
            new ReceiveListener<>(BlockUpdateS2CPacket.class, event ->
        {
            BlockUpdateS2CPacket packet = event.getPacket();
            process(packet.getPos(), packet.getState());
        }));
        this.listeners.add(
            new ReceiveListener<>(ExplosionS2CPacket.class, event ->
        {
            ExplosionS2CPacket packet = event.getPacket();
            for (BlockPos pos : packet.getAffectedBlocks())
            {
                process(pos, Blocks.AIR.getDefaultState());
            }
        }));
        this.listeners.add(new EventListener<>
            (WorldClientEvent.Load.class)
        {
            @Override
            public void invoke(WorldClientEvent.Load event)
            {
                callbacks.clear();
            }
        });
        this.listeners.add(new EventListener<>
            (WorldClientEvent.Unload.class)
        {
            @Override
            public void invoke(WorldClientEvent.Unload event)
            {
                callbacks.clear();
            }
        });
    }

    /**
     * The Callback will be invoked and removed when a
     * {@link SPacketBlockChange} or {@link SPacketMultiBlockChange}
     * or a {@link SPacketExplosion} packet is received,
     * which targets the given position.
     *
     * @param pos the position we want to detect state changes at.
     * @param callback called when the BlockState at the pos is changed.
     */
    public void addCallback(BlockPos pos, Consumer<BlockState> callback)
    {
        callbacks.computeIfAbsent(pos.toImmutable(), v -> new ConcurrentLinkedQueue<>())
                 .add(callback);
    }

    /** Processes a BlockState change and calls all Callbacks. */
    private void process(BlockPos pos, BlockState state)
    {
        Queue<Consumer<BlockState>> cbs = callbacks.remove(pos);
        if (cbs != null)
        {
            CollectionUtil.emptyQueue(cbs, c -> c.accept(state));
        }
    }

}
