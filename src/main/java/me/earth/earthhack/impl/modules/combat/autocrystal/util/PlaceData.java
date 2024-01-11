package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PlaceData
{
    private final Map<PlayerEntity, ForceData> force         = new HashMap<>();
    private final Map<PlayerEntity, List<PositionData>> corr = new HashMap<>();
    private final Map<BlockPos, PositionData> obby           = new HashMap<>();
    private final Map<BlockPos, PositionData> liquidObby     = new HashMap<>();

    private final List<PositionData> liquid    = new ArrayList<>();
    private final Set<PositionData> data       = new TreeSet<>();
    private final Set<AntiTotemData> antiTotem = new TreeSet<>();
    private final Set<PositionData> shieldData = new TreeSet<>();
    private final Set<PositionData> raytraceData = new TreeSet<>();

    private PlayerEntity shieldPlayer;
    private float highestSelfDamage;
    private final float minDamage;
    private PlayerEntity target;

    public PlaceData(float minDamage)
    {
        this.minDamage = minDamage;
    }

    public void setTarget(PlayerEntity target)
    {
        this.target = target;
    }

    public PlayerEntity getShieldPlayer()
    {
        if (shieldPlayer == null)
        {
            shieldPlayer = new ShieldPlayer(MinecraftClient.getInstance().world);
        }

        return shieldPlayer;
    }

    public void addAntiTotem(AntiTotemData data)
    {
        this.antiTotem.add(data);
    }

    public void addCorrespondingData(PlayerEntity player, PositionData data)
    {
        List<PositionData> list =
                corr.computeIfAbsent(player, v -> new ArrayList<>());

        list.add(data);
    }

    public void confirmHighDamageForce(PlayerEntity player)
    {
        ForceData data = force.computeIfAbsent(player, v -> new ForceData());
        data.setPossibleHighDamage(true);
    }

    public void confirmPossibleAntiTotem(PlayerEntity player)
    {
        ForceData data = force.computeIfAbsent(player, v -> new ForceData());
        data.setPossibleAntiTotem(true);
    }

    public void addForceData(PlayerEntity player, ForcePosition forceIn)
    {
        ForceData data = force.computeIfAbsent(player, v -> new ForceData());
        data.getForceData().add(forceIn);
    }

    public void addAllCorrespondingData()
    {
        for (AntiTotemData antiTotemData : antiTotem)
        {
            for (PlayerEntity player : antiTotemData.getAntiTotems())
            {
                List<PositionData> corresponding = corr.get(player);
                if (corresponding != null)
                {
                    corresponding.forEach(antiTotemData::addCorrespondingData);
                }
            }
        }
    }

    public float getMinDamage()
    {
        return minDamage;
    }

    public PlayerEntity getTarget()
    {
        return target;
    }

    public Set<AntiTotemData> getAntiTotem()
    {
        return antiTotem;
    }

    public Set<PositionData> getData()
    {
        return data;
    }

    public Map<BlockPos, PositionData> getAllObbyData()
    {
        return obby;
    }

    public Map<PlayerEntity, ForceData> getForceData()
    {
        return force;
    }

    public List<PositionData> getLiquid()
    {
        return liquid;
    }

    public Map<BlockPos, PositionData> getLiquidObby()
    {
        return liquidObby;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PlaceData:\n");
        for (PositionData data : data)
        {
            builder.append("Position: ").append(data.getPos()).append("\n");
        }

        return builder.toString();
    }

    public float getHighestSelfDamage() {
        return highestSelfDamage;
    }

    public void setHighestSelfDamage(float highestSelfDamage) {
        this.highestSelfDamage = highestSelfDamage;
    }

    public Set<PositionData> getShieldData()
    {
        return shieldData;
    }

    public Set<PositionData> getRaytraceData() {
        return raytraceData;
    }

}
