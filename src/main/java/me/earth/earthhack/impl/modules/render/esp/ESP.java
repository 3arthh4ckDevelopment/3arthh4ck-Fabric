package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import me.earth.earthhack.impl.util.minecraft.PushMode;
import net.minecraft.block.entity.*;

import java.awt.*;

public class ESP extends Module {

    protected final Setting<EspMode> mode =
            register(new EnumSetting<>("Mode", EspMode.Outline));
    protected final Setting<Float> lineWidth =
            register(new NumberSetting<>("LineWidth", 3.0f, 2.0f, 5.0f));
    protected final Setting<Integer> range =
            register(new NumberSetting<>("Range", 200, 1, 200));
    private final Setting<Page> pages =
            register(new EnumSetting<>("Page", Page.Entity));

    // Player
    protected final Setting<Boolean> players =
            register(new BooleanSetting("Players", true));
    protected final Setting<Color> playersColor =
            register(new ColorSetting("TargetColor", new Color(248, 254, 0, 255)));
    protected final Setting<Color> friendColor =
            register(new ColorSetting("FriendColor", new Color(13, 255, 0, 255)));
    protected final Setting<Color> targetColor =
            register(new ColorSetting("TargetColor", new Color(255, 0, 0, 102)));
    protected final Setting<Color> invisibleColor =
            register(new ColorSetting("InvisibleColor", new Color(180, 180, 255, 102)));
    protected final Setting<Color> phaseColor =
            register(new ColorSetting("PhaseColor", new Color(255, 171, 0, 102)));
    protected final Setting<PushMode> pushMode =
            register(new EnumSetting<>("PhasePushDetect", PushMode.None))
                    .setComplexity(Complexity.Dev);

    // Entity
    protected final Setting<Boolean> monsters =
            register(new BooleanSetting("Monsters", false));
    protected final Setting<Color> monstersColor =
            register(new ColorSetting("Color", new Color(255, 0, 0, 102)));
    protected final Setting<Boolean> animals =
            register(new BooleanSetting("Animals", false));
    protected final Setting<Color> animalsColor =
            register(new ColorSetting("AnimalsColor", new Color(0, 255, 0, 102)));
    protected final Setting<Boolean> vehicles =
            register(new BooleanSetting("Vehicles", false));
    protected final Setting<Color> vehiclesColor =
            register(new ColorSetting("VehiclesColor", new Color(255, 255, 0, 102)));
    protected final Setting<Boolean> misc =
            register(new BooleanSetting("Other", false));
    protected final Setting<Color> miscColor =
            register(new ColorSetting("OtherColor", new Color(255, 0, 255, 102)));

    // TileEntity
    protected final Setting<Boolean> storage =
            register(new BooleanSetting("Storage", false));
    protected final Setting<Float> storageRange =
            register(new NumberSetting<>("Storage-Range", 100.0f, 0.0f, 200.0f));

    // Items
    protected final Setting<Boolean> items =
            register(new BooleanSetting("Items", false));
    protected final Setting<Color> itemsColor =
            register(new ColorSetting("ItemsColor", new Color(0, 169, 0, 102)));
//    protected final Setting<Boolean> nameTag =
//            register(new BooleanSetting("NameTag", false));
//    protected final Setting<Float> scale =
//            register(new NumberSetting<>("Scale", 0.5f, 0.1f, 1.00f));

    public ESP() {
        super("ESP", Category.Render);
        this.setData(new ESPData(this));
        this.listeners.add(new ListenerRender(this));

        new PageBuilder<>(this, pages)
                .addPage(p -> p == Page.Player, players, pushMode)
                .addPage(p -> p == Page.Entity, monsters, miscColor)
                .addPage(p -> p == Page.Storage, storage, storageRange)
                .addPage(p -> p == Page.Items, items, itemsColor)
                .register(Visibilities.VISIBILITY_MANAGER);
    }

    protected Color colorTileEntityInside(BlockEntity tileEntity) {
        if (tileEntity instanceof ChestBlockEntity) {
            if ((tileEntity).getType() == BlockEntityType.TRAPPED_CHEST) {
                return new Color(250, 54, 0, 60);
            } else {
                return new Color(234, 183, 88, 60);
            }
        } else if (tileEntity instanceof EnderChestBlockEntity) {
            return new Color(174, 0, 255, 60);
        } else if (tileEntity instanceof ShulkerBoxBlockEntity) {
            return new Color(81, 140, 255, 60);
        }
        return new Color(0, 0, 0, 0);
    }

    protected Color colorTileEntity(BlockEntity tileEntity) {
        if (tileEntity instanceof ChestBlockEntity) {
            if ((tileEntity).getType() == BlockEntityType.TRAPPED_CHEST) {
                return new Color(250, 54, 0, 255);
            } else {
                return new Color(234, 183, 88, 255);
            }
        } else if (tileEntity instanceof EnderChestBlockEntity) {
            return new Color(174, 0, 255, 255);
        } else if (tileEntity instanceof ShulkerBoxBlockEntity) {
            return new Color(81, 140, 255, 255);
        }
        return new Color(0, 0, 0, 0);
    }

    enum Page {
        Player,
        Entity,
        Storage,
        Items
    }

}
