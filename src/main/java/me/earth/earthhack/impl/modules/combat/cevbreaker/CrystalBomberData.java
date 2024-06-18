package me.earth.earthhack.impl.modules.combat.cevbreaker;

import me.earth.earthhack.api.module.data.DefaultData;

public class CrystalBomberData extends DefaultData<CrystalBomber> {
    public CrystalBomberData(CrystalBomber module){
        super(module);
        register(module.mode, "CrystalBomber mode." +
                "\n- Normal : Completes a complete cycle of placing crystal, mining," +
                ", breaking and placing obby and repeat." +
                "\n- Instant : Tries to instantly break obsidian and placing a crystal and repeating." +
                " Likely doesn't work on newer servers.");
        register(module.range, "Select a maximum range at which CrystalBomber" +
                " can place/break etc.");
        register(module.toggleAt, "Not implemented yet.");
        register(module.enemyRange, "Players within this range will be " +
                "considered enemies, and attacked.");
        register(module.delay, "Delay between redoing a cycle after finishing the first one.");
        register(module.cooldown,"For servers that require you to wait a little" +
                " before starting to attack after switching hand. Check AutoCrystal - Cooldown.");
        register(module.rotate, "Whether or not to rotate.");
        register(module.reCheckCrystal, "Performs extra checks to see if a crystal already exists");
        register(module.airCheck, "Checks whether or not the block above the target is " +
                "already air, and whether or not to explode a crystal");
        register(module.smartSneak, "Sends sneak packets depending on your situation.");
        register(module.bypass, "Alternate method to switching.");
    }
}
