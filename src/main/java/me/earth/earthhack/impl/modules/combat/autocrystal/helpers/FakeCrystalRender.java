package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.decoration.EndCrystalEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FakeCrystalRender implements Globals
{
    private final List<EndCrystalEntity> crystals = new ArrayList<>();
    private final Setting<Integer> simulate;

    public FakeCrystalRender(Setting<Integer> simulate)
    {
        this.simulate = simulate;
    }

    public void addFakeCrystal(EndCrystalEntity crystal)
    {
        crystal.setShowBottom(false);
        mc.execute(() -> // TODO: why what?
        {
            if (mc.world != null)
            {
                for (EndCrystalEntity entity : mc.world.getEntitiesByClass(
                                                EndCrystalEntity.class,
                                                crystal.getBoundingBox(),
                                                e -> true))
                {
                    crystal.yaw = entity.yaw;
                    break;
                }
            }

            crystals.add(crystal);
        });
    }

    public void onSpawn(EndCrystalEntity crystal)
    {
        Iterator<EndCrystalEntity> itr = crystals.iterator();
        while (itr.hasNext())
        {
            EndCrystalEntity fake = itr.next();
            if (fake.getBoundingBox()
                    .intersects(crystal.getBoundingBox()))
            {
                crystal.yaw = fake.yaw;
                itr.remove();
            }
        }
    }

    public void tick()
    {
        if (simulate.getValue() == 0)
        {
            crystals.clear();
            return;
        }

        Iterator<EndCrystalEntity> itr = crystals.iterator();
        while (itr.hasNext())
        {
            EndCrystalEntity crystal = itr.next();
            crystal.tick();
            if (++crystal.endCrystalAge >= simulate.getValue())
            {
                crystal.kill();
                itr.remove();
            }
        }
    }

    public void render(float partialTicks)
    {
        EntityRenderDispatcher manager = mc.getEntityRenderDispatcher();
        for (EndCrystalEntity crystal : crystals)
        {
            // manager.render(crystal, crystal.getX(), crystal.getY(), crystal.getZ(), crystal.getYaw(), partialTicks, matrices, vertices, false);
        }
    }

    public void clear()
    {
        crystals.clear();
    }

}
