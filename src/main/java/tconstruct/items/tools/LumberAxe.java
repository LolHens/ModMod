//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.items.tools;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AOEHarvestTool;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.modifiers.tools.ModSneakDetector;
import tconstruct.tools.TinkerTools;

import java.util.Iterator;

public class LumberAxe extends AOEHarvestTool {
    static Material[] materials;

    public LumberAxe() {
        super(0, 1, 1);
        this.setUnlocalizedName("InfiTool.LumberAxe");
    }

    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    protected String getHarvestType() {
        return "axe";
    }

    public float getRepairCost() {
        return 4.0F;
    }

    public float getDurabilityModifier() {
        return 2.5F;
    }

    public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player) {
        return block != null && block.getMaterial() == Material.leaves ? false : AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, super.random);
    }

    public float breakSpeedModifier() {
        return 0.4F;
    }

    public float stoneboundModifier() {
        return 216.0F;
    }

    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (stack.hasTagCompound() && ModSneakDetector.isAOE(stack, player)) {
            World world = player.worldObj;
            Block wood = world.getBlock(x, y, z);
            if (wood == null) {
                return super.onBlockStartBreak(stack, x, y, z, player);
            } else if ((wood.isWood(world, x, y, z) || wood.getMaterial() == Material.sponge) && this.detectTree(world, x, y, z, wood)) {
                NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
                int meta = world.getBlockMetadata(x, y, z);
                this.breakTree(world, x, y, z, stack, tags, wood, meta, player);
                return true;
            } else {
                return super.onBlockStartBreak(stack, x, y, z, player);
            }
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    private boolean detectTree(World world, int x, int y, int z, Block wood) {
        int height = y;
        boolean foundTop = false;

        do {
            ++height;
            Block numLeaves = world.getBlock(x, height, z);
            if (numLeaves != wood) {
                --height;
                foundTop = true;
            }
        } while (!foundTop);

        int var13 = 0;
        if (height - y < 50) {
            for (int xPos = x - 1; xPos <= x + 1; ++xPos) {
                for (int yPos = height - 1; yPos <= height + 1; ++yPos) {
                    for (int zPos = z - 1; zPos <= z + 1; ++zPos) {
                        Block leaves = world.getBlock(xPos, yPos, zPos);
                        if (leaves != null && leaves.isLeaves(world, xPos, yPos, zPos)) {
                            ++var13;
                        }
                    }
                }
            }
        }

        return var13 > 3;
    }

    private void breakTree(World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, Block bID, int meta, EntityPlayer player) {
        for (int xPos = x - 1; xPos <= x + 1; ++xPos) {
            for (int yPos = y; yPos <= y + 1; ++yPos) {
                for (int zPos = z - 1; zPos <= z + 1; ++zPos) {
                    if (!tags.getBoolean("Broken")) {
                        Block localBlock = world.getBlock(xPos, yPos, zPos);
                        if (bID == localBlock) {
                            int localMeta = world.getBlockMetadata(xPos, yPos, zPos);
                            int hlvl = localBlock.getHarvestLevel(localMeta);
                            float localHardness = localBlock == null ? 3.4028235E38F : localBlock.getBlockHardness(world, xPos, yPos, zPos);
                            if (hlvl <= tags.getInteger("HarvestLevel") && localHardness >= 0.0F) {
                                boolean cancelHarvest = false;
                                Iterator event = TConstructRegistry.activeModifiers.iterator();

                                while (event.hasNext()) {
                                    ActiveToolMod mod = (ActiveToolMod) event.next();
                                    if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player)) {
                                        cancelHarvest = true;
                                    }
                                }

                                BreakEvent var20 = new BreakEvent(x, y, z, world, localBlock, localMeta, player);
                                var20.setCanceled(cancelHarvest);
                                MinecraftForge.EVENT_BUS.post(var20);
                                cancelHarvest = var20.isCanceled();
                                if (cancelHarvest) {
                                    this.breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                                } else if (localBlock == bID && localMeta % 4 == meta % 4) {
                                    if (!player.capabilities.isCreativeMode) {
                                        localBlock.harvestBlock(world, player, x, y, z, localMeta);
                                        this.onBlockDestroyed(stack, world, localBlock, xPos, yPos, zPos, player);
                                    }

                                    world.setBlockToAir(xPos, yPos, zPos);
                                    if (!world.isRemote) {
                                        this.breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public Item getHeadItem() {
        return TinkerTools.broadAxeHead;
    }

    public Item getHandleItem() {
        return TinkerTools.toughRod;
    }

    public Item getAccessoryItem() {
        return TinkerTools.largePlate;
    }

    public Item getExtraItem() {
        return TinkerTools.toughBinding;
    }

    public int getPartAmount() {
        return 4;
    }

    public String getIconSuffix(int partType) {
        switch (partType) {
            case 0:
                return "_lumberaxe_head";
            case 1:
                return "_lumberaxe_head_broken";
            case 2:
                return "_lumberaxe_handle";
            case 3:
                return "_lumberaxe_shield";
            case 4:
                return "_lumberaxe_binding";
            default:
                return "";
        }
    }

    public String getEffectSuffix() {
        return "_lumberaxe_effect";
    }

    public String getDefaultFolder() {
        return "lumberaxe";
    }

    public int durabilityTypeAccessory() {
        return 2;
    }

    public int durabilityTypeExtra() {
        return 1;
    }

    static {
        materials = new Material[]{Material.wood, Material.vine, Material.circuits, Material.cactus, Material.gourd};
    }
}
