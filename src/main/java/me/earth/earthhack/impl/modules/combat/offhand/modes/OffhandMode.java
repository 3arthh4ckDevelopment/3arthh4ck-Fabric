package me.earth.earthhack.impl.modules.combat.offhand.modes;

import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

/**
 * OffhandMode for the {@link Offhand}.
 */
public class OffhandMode
{
    /** Offhand Mode for {@link Items#TOTEM_OF_UNDYING}. */
    public static final OffhandMode TOTEM =
            new OffhandMode(Items.TOTEM_OF_UNDYING, "Totem");
    /** Offhand Mode for {@link Items#GOLDEN_APPLE}. */
    public static final OffhandMode GAPPLE =
            new OffhandMode(Items.GOLDEN_APPLE, "Gapple");
    /** Offhand Mode for {@link Items#END_CRYSTAL}. */
    public static final OffhandMode CRYSTAL =
            new OffhandMode(Items.END_CRYSTAL, "Crystal");
    public static final OffhandMode BED =
            new OffhandMode(Items.WHITE_BED, "Bed"); // ain't no way u can't use a generic item "Items.BED"
    /** Offhand Mode for {@link Blocks#OBSIDIAN}. */
    public static final OffhandMode OBSIDIAN =
            new OffhandMode(Blocks.OBSIDIAN, "Obsidian");

    private final String name;
    private final Item item;

    public OffhandMode(Block block, String name)
    {
        this(Item.fromBlock(block), name);
    }

    public OffhandMode(Item item, String name)
    {
        this.item = item;
        this.name = name;
    }

    public Item getItem()
    {
        return item;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return item == null ? 0 : item.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof OffhandMode)
        {
            return ((OffhandMode) o).item == this.item;
        }

        return false;
    }

}
