package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class Nametag implements Globals
{
    private static final ModuleCache<Media> MEDIA =
            Caches.getModule(Media.class);

    private final Nametags module;
    public final PlayerEntity player;
    // public final StackRenderer mainHand;
    // public final List<StackRenderer> stacks;
    public final String nameString;
    public final int nameColor;
    public final int nameWidth;
    public int maxEnchHeight;
    public boolean renderDura;
    public static boolean isRendering;

    public Nametag(Nametags module, PlayerEntity player)
    {
        this.module = module;
        this.player = player;
        //this.stacks = new ArrayList<>(6);

        ItemStack mainStack = player.getMainHandStack();
        if (mainStack.isEmpty())
        {
            // mainHand = null;
        }
        else
        {
            boolean damageable = mainStack.isDamageable()
                                    && module.durability.getValue();
            if (damageable)
            {
                renderDura = true;
            }

            // mainHand = new StackRenderer(mainStack, damageable);
            // calcEnchHeight(mainHand);
        }

        for (int i = 3; i > -1; i--) {
            addStack(player.getInventory().armor.get(i));
        }

        addStack(player.getOffHandStack());

        this.nameColor = getColor(player);
        this.nameString = getName(player);
        this.nameWidth = Managers.TEXT.getStringWidth(nameString);

        // for (StackRenderer sr : stacks)
        // {
        //     calcEnchHeight(sr);
        // }
    }

    // private void calcEnchHeight(StackRenderer sr)
    // {
    //     int enchHeight = EnchantmentHelper.get(sr.getStack())
    //             .size();
    //     if (module.armor.getValue() && enchHeight > maxEnchHeight)
    //     {
    //         maxEnchHeight = enchHeight;
    //     }
    // }

    private void addStack(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            boolean damageable = stack.isDamageable()
                                    && module.durability.getValue();
            if (damageable)
            {
                renderDura = true;
            }

            // stacks.add(new StackRenderer(stack, damageable));
        }
    }

    private String getName(PlayerEntity player)
    {
        String name = player.getDisplayName().getString().trim();

        String s;
        if (module.media.getValue())
        {
            s = MEDIA.returnIfPresent(m -> m.convert(name), name);
        }
        else
        {
            s = name;
        }

        StringBuilder builder = new StringBuilder(s);
        boolean offset = builder.toString().replaceAll("§.", "").length() > 0;
        if (module.id.getValue())
        {
            builder.append(offset ? " " : "")
                   .append("ID: ")
                   .append(player.getId());
            offset = true;
        }

        if (module.gameMode.getValue())
        {
            builder.append((offset ? " " : ""))
                   .append("[")
                   .append(player.isCreative()
                             ? "C"
                             : (player.isSpectator() ? "I" : "S"))
                   .append("]");
            offset = true;
        }

        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection != null)
        {
            PlayerListEntry playerInfo =
                    connection.getPlayerListEntry(player.getUuid());
            //noinspection ConstantConditions
            if (module.ping.getValue() && playerInfo != null)
            {
                builder.append((offset ? " " : ""))
                       .append(playerInfo.getLatency())
                       .append("ms");
                offset = true;
            }
        }

        if (module.health.getValue())
        {
            double health = Math.ceil(EntityUtil.getHealth(player));
            String healthColor;

            if (health > 18.0)
            {
                healthColor = TextColor.GREEN;
            }
            else if (health > 16.0)
            {
                healthColor = TextColor.DARK_GREEN;
            }
            else if (health > 12.0)
            {
                healthColor = TextColor.YELLOW;
            }
            else if (health > 8.0)
            {
                healthColor = TextColor.GOLD;
            }
            else if (health > 5.0)
            {
                healthColor = TextColor.RED;
            }
            else
            {
                healthColor = TextColor.DARK_RED;
            }

            builder.append((offset ? " " : ""))
                   .append(healthColor)
                   .append(health > 0.0 ? (int) health : "0");
        }

        if (module.pops.getValue())
        {
            int pops = Managers.COMBAT.getPops(player);
            if (pops != 0)
            {
                builder.append(TextColor.WHITE)
                       .append(" -")
                       .append(pops);
            }
        }

        if (module.motion.getValue())
        {
            builder.append(" ")
                .append(TextColor.GRAY)
                .append("x: ")
                .append(TextColor.WHITE)
                .append(MathUtil.round(player.getVelocity().getX() * 20, 2))
                .append(TextColor.GRAY)
                .append(", y: ")
                .append(TextColor.WHITE)
                .append(MathUtil.round(player.getVelocity().getY() * 20, 2))
                .append(TextColor.GRAY)
                .append(", z: ")
                .append(TextColor.WHITE)
                .append(MathUtil.round(player.getVelocity().getZ() * 20, 2));
        }

        if (module.motionKpH.getValue())
        {
            double kpH = Math.sqrt(
                player.getVelocity().getX() * player.getVelocity().getX()
                    + player.getVelocity().getZ() * player.getVelocity().getZ()) * 20 * 3.6;

            builder.append(" ")
                   .append(TextColor.WHITE)
                   .append(MathUtil.round(kpH, 2))
                   .append(TextColor.GRAY)
                   .append(" km/h");
        }

        return builder.toString();
    }

    private int getColor(PlayerEntity player)
    {
        if (Managers.FRIENDS.contains(player))
        {
            return 0xff66ffff;
        }

        if (module.burrow.getValue())
        {
            BlockPos pos = PositionUtil.getPosition(player);
            BlockState state = mc.world.getBlockState(pos);
            if (!state.isReplaceable()
                    && state.getCollisionShape(mc.world, pos).getBoundingBox().offset(pos).maxY
                            > player.getY())
            {
                return 0xff670067;
            }
        }

        if (module.phase.getValue() && PhaseUtil.isPhasing(
            player, module.pushMode.getValue()))
        {
            return 0xff670067;
        }

        if (Managers.ENEMIES.contains(player))
        {
            return 0xffff0000;
        }

        if (player.isInvisible())
        {
            return 0xffff2500;
        }

        //noinspection ConstantConditions
        if (mc.getNetworkHandler() != null
              && mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null)
        {
            return 0xffef0147;
        }

        if (player.isSneaking() && module.sneak.getValue())
        {
            return 0xffff9900;
        }

        return 0xffffffff;
    }

}
