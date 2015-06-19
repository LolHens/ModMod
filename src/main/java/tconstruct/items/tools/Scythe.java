//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.items.tools;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.Weapon;
import tconstruct.modifiers.tools.ModCareful;
import tconstruct.modifiers.tools.ModRange;
import tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Scythe extends Weapon {
    static Material[] materials;

    public Scythe() {
        super(4);
        this.setUnlocalizedName("InfiTool.Scythe");
    }

    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    public Item getHeadItem() {
        return TinkerTools.scytheBlade;
    }

    public Item getHandleItem() {
        return TinkerTools.toughRod;
    }

    public Item getAccessoryItem() {
        return TinkerTools.toughBinding;
    }

    public Item getExtraItem() {
        return TinkerTools.toughRod;
    }

    public int getPartAmount() {
        return 4;
    }

    public String getIconSuffix(int partType) {
        switch (partType) {
            case 0:
                return "_scythe_head";
            case 1:
                return "_scythe_head_broken";
            case 2:
                return "_scythe_handle";
            case 3:
                return "_scythe_binding";
            case 4:
                return "_scythe_accessory";
            default:
                return "";
        }
    }

    public float getDurabilityModifier() {
        return 3.0F;
    }

    public float getRepairCost() {
        return 4.0F;
    }

    public String getEffectSuffix() {
        return "_scythe_effect";
    }

    public String getDefaultFolder() {
        return "scythe";
    }

    public int durabilityTypeAccessory() {
        return 1;
    }

    public int durabilityTypeExtra() {
        return 1;
    }

    public float getDamageModifier() {
        return 0.75F;
    }

    public String[] getTraits() {
        return new String[]{"weapon", "melee", "harvest"};
    }

    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (!stack.hasTagCompound()) return false;

        World world = player.worldObj;
        Block blockB = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        boolean butter = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0;
        int fortune = EnchantmentHelper.getFortuneModifier(player);

        int radius = ModCareful.isAOE(stack, player) ? 1 + ModRange.getRangeIncrease(stack) : 0;

        for (int xPos = x - radius; xPos <= x + radius; ++xPos) {
            for (int yPos = y - radius; yPos <= y + radius; ++yPos) {
                for (int zPos = z - radius; zPos <= z + radius; ++zPos) {
                    if (!tags.getBoolean("Broken")) {
                        boolean cancelHarvest = false;
                        Iterator localBlock = TConstructRegistry.activeModifiers.iterator();

                        while (localBlock.hasNext()) {
                            ActiveToolMod localMeta = (ActiveToolMod) localBlock.next();
                            if (localMeta.beforeBlockBreak(this, stack, xPos, yPos, zPos, player)) {
                                cancelHarvest = true;
                            }
                        }

                        if (!cancelHarvest) {
                            Block var32 = world.getBlock(xPos, yPos, zPos);
                            int var33 = world.getBlockMetadata(xPos, yPos, zPos);
                            float localHardness = var32 == null ? 3.4028235E38F : var32.getBlockHardness(world, xPos, yPos, zPos);
                            if (var32 != null) {
                                for (int iter = 0; iter < materials.length; ++iter) {
                                    if (materials[iter] == var32.getMaterial()) {
                                        if (player.capabilities.isCreativeMode) {
                                            world.setBlockToAir(xPos, yPos, zPos);
                                        } else if (butter && var32 instanceof IShearable && ((IShearable) var32).isShearable(stack, player.worldObj, xPos, yPos, zPos)) {
                                            ArrayList var34 = ((IShearable) var32).onSheared(stack, player.worldObj, xPos, yPos, zPos, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
                                            Random var35 = new Random();
                                            if (!world.isRemote) {
                                                Iterator var36 = var34.iterator();

                                                while (var36.hasNext()) {
                                                    ItemStack dropStack = (ItemStack) var36.next();
                                                    float f = 0.7F;
                                                    double d = (double) (var35.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                                    double d1 = (double) (var35.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                                    double d2 = (double) (var35.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                                    EntityItem entityitem = new EntityItem(player.worldObj, (double) xPos + d, (double) yPos + d1, (double) zPos + d2, dropStack);
                                                    entityitem.delayBeforeCanPickup = 10;
                                                    player.worldObj.spawnEntityInWorld(entityitem);
                                                }
                                            }

                                            if (localHardness > 0.0F) {
                                                this.onBlockDestroyed(stack, world, var32, xPos, yPos, zPos, player);
                                            }

                                            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(var32)], 1);
                                            world.setBlockToAir(xPos, yPos, zPos);
                                        } else {
                                            int exp = var32.getExpDrop(world, var33, fortune);
                                            var32.onBlockHarvested(world, xPos, yPos, zPos, var33, player);
                                            if (var32.removedByPlayer(world, player, xPos, yPos, zPos, true)) {
                                                var32.onBlockDestroyedByPlayer(world, xPos, yPos, zPos, var33);
                                                var32.harvestBlock(world, player, xPos, yPos, zPos, var33);
                                                if (!butter) {
                                                    var32.dropXpOnBlockBreak(world, xPos, yPos, zPos, exp);
                                                }
                                            }

                                            if (world.isRemote) {
                                                INetHandler handler = FMLClientHandler.instance().getClientPlayHandler();
                                                if (handler != null && handler instanceof NetHandlerPlayClient) {
                                                    NetHandlerPlayClient handlerClient = (NetHandlerPlayClient) handler;
                                                    handlerClient.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
                                                    handlerClient.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
                                                }
                                            }

                                            if (localHardness > 0.0F) {
                                                this.onBlockDestroyed(stack, world, var32, xPos, yPos, zPos, player);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!world.isRemote) {
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(blockB) + (meta << 12));
        }

        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
        Iterator i$ = list.iterator();

        while (i$.hasNext()) {
            Object o = i$.next();
            AbilityHelper.onLeftClickEntity(stack, player, (Entity) o, this);
        }

        return true;
    }

    static {
        materials = new Material[]{Material.web, Material.cactus, Material.plants, Material.leaves, Material.vine, Material.gourd};
    }
}
