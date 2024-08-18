package me.earth.earthhack.impl.commands;

import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Arrays;

import static me.earth.earthhack.impl.util.thread.EnchantmentUtil.addEnchantment;

public class Thirty2kCommand extends AbstractStackCommand
{
    public Thirty2kCommand()
    {
        super(new String[][]{{"32k"},
                {"sword", "bow", "pick", "helmet", "chest", "legs", "boots", "skeleton"}}, "32k");
        CommandDescriptions.register(this, "Gives you a 32k sword.");
    }

    @Override
    protected ItemStack getStack(String[] args)
    {
        if (Arrays.stream(args).anyMatch("bow"::equalsIgnoreCase))
        {
            ItemStack s = new ItemStack(Items.BOW);
            s.setCustomName(Text.of("3\u00B2arthB0w"));
            s.setCount(64);

            addEnchantment(s, Enchantments.POWER, 255);
            addEnchantment(s, Enchantments.PUNCH, 255);
            addEnchantment(s, Enchantments.FLAME, 255);
            addEnchantment(s, Enchantments.INFINITY, 1);

            addEnchantment(s, Enchantments.UNBREAKING, 255);
            addEnchantment(s, Enchantments.MENDING, 1);
            addEnchantment(s, Enchantments.VANISHING_CURSE, 1);
            return s;
        }

        if (Arrays.stream(args).anyMatch("skeleton"::equalsIgnoreCase))
        {
            return getSkeleton();
        }

        if (Arrays.stream(args).anyMatch("magma"::equalsIgnoreCase))
        {
            return getSlime(args, false);
        }

        if (Arrays.stream(args).anyMatch("slime"::equalsIgnoreCase)) {
            return getSlime(args, true);
        }

        if (Arrays.stream(args).anyMatch("pick"::equalsIgnoreCase))
        {
            ItemStack s = new ItemStack(Items.NETHERITE_SWORD);
            s.setCustomName(Text.of("3\u00B2arth P1ck"));
            s.setCount(64);

            addEnchantment(s, Enchantments.EFFICIENCY, 255);
            if (Arrays.stream(args).anyMatch("-fortune"::equalsIgnoreCase))
            {
                addEnchantment(s, Enchantments.FORTUNE, 255);
            }
            else if (Arrays.stream(args).anyMatch("-silk"::equalsIgnoreCase))
            {
                addEnchantment(s, Enchantments.SILK_TOUCH, 1);
            }
            addEnchantment(s, Enchantments.UNBREAKING, 255);
            addEnchantment(s, Enchantments.MENDING, 1);
            addEnchantment(s, Enchantments.VANISHING_CURSE, 1);
            return s;
        }

        boolean helmet = Arrays.stream(args).anyMatch("helmet"::equalsIgnoreCase);
        boolean chest = Arrays.stream(args).anyMatch("chest"::equalsIgnoreCase);
        boolean legs = Arrays.stream(args).anyMatch("legs"::equalsIgnoreCase);
        boolean boots = Arrays.stream(args).anyMatch("boots"::equalsIgnoreCase);

        ItemStack s = null;
        if (helmet) {
            s = new ItemStack(Items.NETHERITE_HELMET);
            s.setCustomName(Text.of("3\u00B2arth H3lmet"));
            s.setCount(64);
        }

        if (chest) {
            s = new ItemStack(Items.NETHERITE_CHESTPLATE);
            s.setCustomName(Text.of("3\u00B2arth Ch3stPl4te"));
            s.setCount(64);
        }

        if (legs) {
            s = new ItemStack(Items.NETHERITE_LEGGINGS);
            s.setCustomName(Text.of("3\u00B2arth L3ggings"));
            s.setCount(64);
            addEnchantment(s, Enchantments.BLAST_PROTECTION, 255);
        }

        if (boots) {
            s = new ItemStack(Items.NETHERITE_BOOTS);
            s.setCustomName(Text.of("3\u00B2arth Bo0ts"));
            s.setCount(64);
        }

        if (helmet || chest || legs || boots) {
            String dura = CommandUtil.getArgument("--dura", args);
            if (dura != null) {
                try {
                    s.setDamage(Integer.parseInt(dura));
                } catch (NumberFormatException e) {
                    ChatUtil.sendMessage(TextColor.RED + e.getMessage());
                }
            }

            addEnchantment(s, Enchantments.PROTECTION, 255);
            addEnchantment(s, Enchantments.THORNS, 255);
            addEnchantment(s, Enchantments.UNBREAKING, 255);
            addEnchantment(s, Enchantments.MENDING, 1);
            addEnchantment(s, Enchantments.VANISHING_CURSE, 1);
            return s;
        }

        return get32kSword();
    }

    private ItemStack get32kSword() {
        ItemStack s = new ItemStack(Items.NETHERITE_SWORD);
        s.setCustomName(Text.of("3\u00B2arthbl4de"));
        s.setCount(64);

        addEnchantment(s, Enchantments.SHARPNESS, 255);
        addEnchantment(s, Enchantments.KNOCKBACK, 10);
        addEnchantment(s, Enchantments.FIRE_ASPECT, 255);
        addEnchantment(s, Enchantments.LOOTING, 10);
        addEnchantment(s, Enchantments.SWEEPING_EDGE, 3);
        addEnchantment(s, Enchantments.UNBREAKING, 255);
        addEnchantment(s, Enchantments.MENDING, 1);
        addEnchantment(s, Enchantments.VANISHING_CURSE, 1);
        return s;
    }

    private ItemStack getSkeleton() {
        SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, mc.world);
        ItemStack s;
        s = new ItemStack(Items.NETHERITE_HELMET);
        s.setCustomName(Text.of("3\u00B2arth H3lmet"));
        basicArmorEnchants(s);
        skeleton.equipStack(EquipmentSlot.HEAD, s);

        s = new ItemStack(Items.NETHERITE_CHESTPLATE);
        s.setCustomName(Text.of("3\u00B2arth Ch3stPl4te"));
        basicArmorEnchants(s);
        skeleton.equipStack(EquipmentSlot.CHEST, s);

        s = new ItemStack(Items.NETHERITE_LEGGINGS);
        s.setCustomName(Text.of("3\u00B2arth L3ggings"));
        addEnchantment(s, Enchantments.BLAST_PROTECTION, 255);
        basicArmorEnchants(s);
        skeleton.equipStack(EquipmentSlot.LEGS, s);


        s = new ItemStack(Items.NETHERITE_BOOTS);
        s.setCustomName(Text.of("3\u00B2arth Bo0ts"));
        basicArmorEnchants(s);
        skeleton.equipStack(EquipmentSlot.FEET, s);

        s = get32kSword();
        skeleton.equipStack(EquipmentSlot.MAINHAND, s);

        s = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
        skeleton.equipStack(EquipmentSlot.OFFHAND, s);

        s = new ItemStack(Items.SKELETON_SPAWN_EGG);
        NbtCompound nbtTagCompound = new NbtCompound();
        NbtCompound entityTag = new NbtCompound();
        skeleton.writeCustomDataToNbt(entityTag);
        entityTag.putString("id", "minecraft:skeleton");
        nbtTagCompound.put("EntityTag", entityTag);
        nbtTagCompound.putString("id", "minecraft:skeleton_spawn_egg");
        s.setNbt(nbtTagCompound);
        s.setCount(64);
        return s;
    }

    private ItemStack getSlime(String[] args, boolean isSlime) {
        ItemStack s;
        if (isSlime) {
            s = new ItemStack(Items.SLIME_SPAWN_EGG);
        } else {
            s = new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG);
        }
        NbtCompound nbtSize = new NbtCompound();
        nbtSize.putInt("Size", CommandUtil.getInt("--size", 10, args));

        NbtCompound nbtTagCompound = new NbtCompound();
        nbtTagCompound.put("EntityTag", nbtSize);
        s.setNbt(nbtTagCompound);
        s.setCount(64);
        return s;
    }

    private void basicArmorEnchants(ItemStack s) {
        s.addEnchantment(Enchantments.PROTECTION, 255);
        s.addEnchantment(Enchantments.THORNS, 255);
        s.addEnchantment(Enchantments.UNBREAKING, 255);
        s.addEnchantment(Enchantments.MENDING, 1);
        s.addEnchantment(Enchantments.VANISHING_CURSE, 1);
    }

}
