//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.library.tools;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModConvenient;
import tconstruct.modifiers.tools.ModUniversal;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

public abstract class HarvestTool extends ToolCore {
    public HarvestTool(int baseDamage) {
        super(baseDamage);
    }

    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if (stack != null && stack.getItem() instanceof HarvestTool) {
            if (toolClass != null && this.getHarvestType().equals(toolClass)) {
                if (!stack.hasTagCompound()) {
                    return -1;
                } else {
                    NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
                    return tags.getBoolean("Broken") ? -1 : tags.getInteger("HarvestLevel");
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (!stack.hasTagCompound()) {
            return 1.0F;
        } else {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            return tags.getBoolean("Broken") ? 0.1F :
                    ((ModConvenient.isConvenient(stack)
                            || ModUniversal.isUniversal(stack)
                            || this.isEffective(block, meta)) ? this.calculateStrength(tags, block, meta) :
                            super.getDigSpeed(stack, block, meta));
        }
    }

    public float calculateStrength(NBTTagCompound tags, Block block, int meta) {
        int hlvl = block.getHarvestLevel(meta);
        return hlvl > tags.getInteger("HarvestLevel") ? 0.1F : AbilityHelper.calcToolSpeed(this, tags);
    }

    public float breakSpeedModifier() {
        return 1.0F;
    }

    public float stoneboundModifier() {
        return 72.0F;
    }

    public boolean canItemHarvestBlock(Block block) {
        return this.isEffective(block.getMaterial());
    }

    public String[] getTraits() {
        return new String[]{"harvest"};
    }

    protected abstract Material[] getEffectiveMaterials();

    protected abstract String getHarvestType();

    public Set<String> getToolClasses(ItemStack stack) {
        HashSet set = new HashSet();
        if (stack != null && stack.getItem() instanceof HarvestTool) {
            set.add(((HarvestTool) stack.getItem()).getHarvestType());
        }

        return set;
    }

    public boolean isEffective(Block block, int meta) {
        return this.getHarvestType().equals(block.getHarvestTool(meta)) ? true : this.isEffective(block.getMaterial());
    }

    public boolean isEffective(Material material) {
        Material[] arr$ = this.getEffectiveMaterials();
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            Material m = arr$[i$];
            if (m == material) {
                return true;
            }
        }

        return false;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ) {
        boolean used = false;
        int hotbarSlot = player.inventory.currentItem;
        int itemSlot = hotbarSlot == 0 ? 8 : hotbarSlot + 1;
        ItemStack nearbyStack = null;
        if (hotbarSlot < 8) {
            nearbyStack = player.inventory.getStackInSlot(itemSlot);
            if (nearbyStack != null) {
                Item item = nearbyStack.getItem();
                if (item instanceof ItemBlock || item != null && item == TinkerTools.openBlocksDevNull) {
                    int posX = x;
                    int posY = y;
                    int posZ = z;
                    int playerPosX = (int) Math.floor(player.posX);
                    int playerPosY = (int) Math.floor(player.posY);
                    int playerPosZ = (int) Math.floor(player.posZ);
                    if (side == 0) {
                        posY = y - 1;
                    }

                    if (side == 1) {
                        ++posY;
                    }

                    if (side == 2) {
                        posZ = z - 1;
                    }

                    if (side == 3) {
                        ++posZ;
                    }

                    if (side == 4) {
                        posX = x - 1;
                    }

                    if (side == 5) {
                        ++posX;
                    }

                    if (posX == playerPosX && (posY == playerPosY || posY == playerPosY + 1 || posY == playerPosY - 1) && posZ == playerPosZ) {
                        return false;
                    }

                    int dmg = nearbyStack.getMetadata();
                    int count = nearbyStack.stackSize;
                    if (item == TinkerTools.openBlocksDevNull) {
                        player.inventory.currentItem = itemSlot;
                        item.onItemUse(nearbyStack, player, world, x, y, z, side, clickX, clickY, clickZ);
                        player.inventory.currentItem = hotbarSlot;
                        player.swingItem();
                    } else {
                        used = item.onItemUse(nearbyStack, player, world, x, y, z, side, clickX, clickY, clickZ);
                    }

                    if (player.capabilities.isCreativeMode) {
                        nearbyStack.setMetadata(dmg);
                        nearbyStack.stackSize = count;
                    }

                    if (nearbyStack.stackSize < 1) {
                        nearbyStack = null;
                        player.inventory.setInventorySlotContents(itemSlot, (ItemStack) null);
                    }
                }
            }
        }

        return used;
    }

    protected void breakExtraBlock(World world, int x, int y, int z, int sidehit, EntityPlayer playerEntity, int refX, int refY, int refZ) {
        if (!world.isAirBlock(x, y, z)) {
            if (playerEntity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) playerEntity;
                Block block = world.getBlock(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);
                if (ModUniversal.isUniversal(playerEntity.getHeldItem()) || this.isEffective(block, meta)) {
                    Block refBlock = world.getBlock(refX, refY, refZ);
                    float refStrength = ForgeHooks.blockStrength(refBlock, player, world, refX, refY, refZ);
                    float strength = ForgeHooks.blockStrength(block, player, world, x, y, z);
                    if (ForgeHooks.canHarvestBlock(block, player, meta) && refStrength / strength <= 10.0F) {
                        BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z);
                        if (!event.isCanceled()) {
                            if (player.capabilities.isCreativeMode) {
                                block.onBlockHarvested(world, x, y, z, meta, player);
                                if (block.removedByPlayer(world, player, x, y, z, false)) {
                                    block.onBlockDestroyedByPlayer(world, x, y, z, meta);
                                }

                                if (!world.isRemote) {
                                    player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
                                }

                            } else {
                                player.getCurrentEquippedItem().onBlockDestroyed(world, block, x, y, z, player);
                                if (!world.isRemote) {
                                    block.onBlockHarvested(world, x, y, z, meta, player);
                                    if (block.removedByPlayer(world, player, x, y, z, true)) {
                                        block.onBlockDestroyedByPlayer(world, x, y, z, meta);
                                        block.harvestBlock(world, player, x, y, z, meta);
                                        block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
                                    }

                                    player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
                                } else {
                                    world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
                                    if (block.removedByPlayer(world, player, x, y, z, true)) {
                                        block.onBlockDestroyedByPlayer(world, x, y, z, meta);
                                    }

                                    ItemStack itemstack = player.getCurrentEquippedItem();
                                    if (itemstack != null) {
                                        itemstack.onBlockDestroyed(world, block, x, y, z, player);
                                        if (itemstack.stackSize == 0) {
                                            player.destroyCurrentEquippedItem();
                                        }
                                    }

                                    if (PHConstruct.extraBlockUpdates) {
                                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
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
