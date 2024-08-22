package me.earth.earthhack.impl.util.minecraft.blocks.states;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@link WorldAccess} that delegates all its methods
 * to <tt>mc.world</tt> except
 * {@link BlockStateHelper#getBlockState(BlockPos)}.
 * For the reason why read the documentation of the
 * {@link BlockStateHelper#addBlockState(BlockPos, BlockState)}
 * method.
 */
@SuppressWarnings("unused")
public class BlockStateHelper implements Globals, IBlockStateHelper
{
    private final Map<BlockPos, BlockState> states;
    private final Supplier<WorldAccess> world;

    public BlockStateHelper()
    {
        this(new HashMap<>());
    }

    public BlockStateHelper(Supplier<WorldAccess> world)
    {
        this(new HashMap<>(), world);
    }

    public BlockStateHelper(Map<BlockPos, BlockState> stateMap)
    {
        this(stateMap, () -> mc.world);
    }

    public BlockStateHelper(Map<BlockPos, BlockState> stateMap,
                            Supplier<WorldAccess> world)
    {
        this.states = stateMap;
        this.world = world;
    }

    /**
     * Returns an IBlockState set by
     * {@link BlockStateHelper#addBlockState(BlockPos, BlockState)},
     * or if none was found the IBlockState from
     * {@link World#getBlockState(BlockPos)}.
     *
     * @param pos the position to get the BlockState for.
     * @return the BlockState at that Position.
     */
    @Override
    public BlockState getBlockState(BlockPos pos)
    {
        BlockState state = states.get(pos);
        if (state == null)
        {
            return world.get().getBlockState(pos);
        }

        return state;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }

    /**
     * This Method is not ThreadSafe, unless you use the second
     * constructor with a Concurrent map. If you want to use
     * this method on another Thread you are better off
     * instantiating your own BlockStateManager.
     * <p></p>
     * Some modules used to set a BlockState in the world
     * to some other BlockState, do a calculation and then
     * set it back. That doesn't go well with Multithreading
     * and other stuff, so this Manager was created.
     * <p></p>
     * Use this Method to add the given BlockState.
     * The BlockState will not be added if a BlockState
     * is already added for that position.
     * You can then use {@link BlockUtil#getFacing(BlockPos)}
     * for example.
     *
     * @param pos the position to change the BlockState at.
     * @param state the state that will be at that position.
     */
    @Override
    public void addBlockState(BlockPos pos, BlockState state)
    {
        states.putIfAbsent(pos.toImmutable(), state);
    }

    /**
     * Removes the custom IBlockState at the given pos.
     *
     * @param pos the pos to remove.
     */
    @Override
    public void delete(BlockPos pos)
    {
        states.remove(pos);
    }

    /**
     *  Clears all BlockStates set by
     * {@link BlockStateHelper#addBlockState(BlockPos, BlockState)}.
     */
    @Override
    public void clearAllStates()
    {
        states.clear();
    }

    public Map<BlockPos, BlockState> getStates(){
        return states;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos)
    {
        return world.get().getBlockEntity(pos);
    }

    @Override
    public int getLightLevel(BlockPos pos, int lightValue)
    {
        return world.get().getLightLevel(pos, lightValue);
    }

    @Override
    public boolean isAir(BlockPos pos)
    {
        return this.getBlockState(pos).isAir();
    }

    @Nullable
    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        return null;
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return 0;
    }

    @Override
    public int getAmbientDarkness() {
        return 0;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return null;
    }

    @Override
    public RegistryEntry<Biome> getBiome(BlockPos pos)
    {
        return world.get().getBiome(pos);
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return null;
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public DimensionType getDimension() {
        return null;
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return null;
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return null;
    }

    /*
    @Override
    public int getStrongPower(BlockPos pos, Direction direction)
    {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }
     */

    public boolean isSideSolid(BlockPos pos, Direction side, boolean _default)
    {
        if (!mc.world.isValid(pos))
        {
            return _default;
        }

        Chunk chunk = mc.world.getChunk(pos);
        //noinspection ConstantConditions
        if (chunk == null || chunk.getStatus() == ChunkStatus.EMPTY)
        {
            return _default;
        }

        return this.getBlockState(pos).isSideSolid(mc.world, pos, side, SideShapeType.FULL);
    }

    public ClientWorld getClientWorld() {
        return mc.world;
    }

    @Override
    public long getTickOrder() {
        return 0;
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return null;
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return null;
    }

    @Override
    public WorldProperties getLevelProperties() {
        return null;
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return null;
    }

    @Override
    public ChunkManager getChunkManager() {
        return null;
    }

    @Override
    public Random getRandom() {
        return null;
    }

    @Override
    public void playSound(@Nullable PlayerEntity except, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {

    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {

    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {

    }

    @Override
    public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter) {

    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return null;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate) {
        return null;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return null;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        return false;
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        return false;
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return false;
    }

    @Override
    public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
        return false;
    }
}
