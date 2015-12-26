//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.relic;

import baubles.api.BaubleType;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSyncBauble;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vazkii.botania.api.item.IExtendedWireframeCoordinateListProvider;
import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemLokiRing extends ItemRelicBauble implements IExtendedWireframeCoordinateListProvider, IManaUsingItem {
    private static final String TAG_CURSOR_LIST = "cursorList";
    private static final String TAG_CURSOR_PREFIX = "cursor";
    private static final String TAG_CURSOR_COUNT = "cursorCount";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Y_OFFSET = "yOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_X_ORIGIN = "xOrigin";
    private static final String TAG_Y_ORIGIN = "yOrigin";
    private static final String TAG_Z_ORIGIN = "zOrigin";

    public ItemLokiRing() {
        super("lokiRing");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;

        ItemStack lokiRing = getLokiRing(player);
        if (lokiRing != null && !player.worldObj.isRemote) {
            int slot = -1;
            InventoryBaubles inv = PlayerHandler.getPlayerBaubles(player);

            for (int heldItemStack = 0; heldItemStack < inv.getSizeInventory(); ++heldItemStack) {
                ItemStack originCoords = inv.getStackInSlot(heldItemStack);
                if (ItemStack.areItemStacksEqual(lokiRing, originCoords)) {
                    slot = heldItemStack;
                    break;
                }
            }

            ItemStack heldItemStack = player.getCurrentEquippedItem();
            ChunkCoordinates var19 = getOriginPos(lokiRing);
            MovingObjectPosition lookPos = ToolCommons.raytraceFromEntity(player.worldObj, player, true, 10.0D);
            List cursors = getCursorList(lokiRing);
            int cursorCount = cursors.size();
            int cost = Math.min(cursorCount, (int) Math.pow(2.718281828459045D, (double) cursorCount * 0.25D));
            int x;
            if (heldItemStack != null && heldItemStack.getItem() == ModItems.terraPick && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
                if (var19.posY == -1 && lookPos != null) {
                    setOriginPos(lokiRing, lookPos.blockX, lookPos.blockY, lookPos.blockZ);
                    setCursorList(lokiRing, (List) null);
                    inv.setInventorySlotContents(slot, lokiRing);
                    if (player instanceof EntityPlayerMP) {
                        PacketHandler.INSTANCE.sendTo(new PacketSyncBauble(player, slot), (EntityPlayerMP) player);
                    }
                } else if (lookPos != null) {
                    if (var19.posX != lookPos.blockX || var19.posY != lookPos.blockY || var19.posZ != lookPos.blockZ) {
                        int var20 = lookPos.blockX - var19.posX;
                        int var21 = lookPos.blockY - var19.posY;
                        x = lookPos.blockZ - var19.posZ;
                        Iterator var22 = cursors.iterator();

                        while (true) {
                            if (!var22.hasNext()) {
                                addCursor(lokiRing, var20, var21, x);
                                inv.setInventorySlotContents(slot, lokiRing);
                                if (player instanceof EntityPlayerMP) {
                                    PacketHandler.INSTANCE.sendTo(new PacketSyncBauble(player, slot), (EntityPlayerMP) player);
                                }
                                break;
                            }

                            ChunkCoordinates var23 = (ChunkCoordinates) var22.next();
                            if (var23.posX == var20 && var23.posY == var21 && var23.posZ == x) {
                                cursors.remove(var23);
                                setCursorList(lokiRing, cursors);
                                inv.setInventorySlotContents(slot, lokiRing);
                                if (player instanceof EntityPlayerMP) {
                                    PacketHandler.INSTANCE.sendTo(new PacketSyncBauble(player, slot), (EntityPlayerMP) player);
                                }
                                break;
                            }
                        }
                    } else {
                        setOriginPos(lokiRing, 0, -1, 0);
                        inv.setInventorySlotContents(slot, lokiRing);
                        if (player instanceof EntityPlayerMP) {
                            PacketHandler.INSTANCE.sendTo(new PacketSyncBauble(player, slot), (EntityPlayerMP) player);
                        }
                    }
                }
            } else if (heldItemStack != null && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && lookPos != null && player.isSneaking()) {
                Iterator relX = cursors.iterator();

                while (relX.hasNext()) {
                    ChunkCoordinates cursor = (ChunkCoordinates) relX.next();
                    x = lookPos.blockX + cursor.posX;
                    int y = lookPos.blockY + cursor.posY;
                    int z = lookPos.blockZ + cursor.posZ;
                    Item item = heldItemStack.getItem();
                    if (!player.worldObj.isAirBlock(x, y, z) && ManaItemHandler.requestManaExact(lokiRing, player, cost, true)) {
                        item.onItemUse(player.capabilities.isCreativeMode ? heldItemStack.copy() : heldItemStack, player, player.worldObj, x, y, z, lookPos.sideHit, (float) lookPos.hitVec.xCoord - (float) x, (float) lookPos.hitVec.yCoord - (float) y, (float) lookPos.hitVec.zCoord - (float) z);
                        if (heldItemStack.stackSize == 0) {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }

        }
    }

    public static void breakOnAllCursors(EntityPlayer player, Item item, ItemStack stack, int x, int y, int z, int side) {
        ItemStack lokiRing = getLokiRing(player);
        if (lokiRing != null && !player.worldObj.isRemote && item instanceof ISequentialBreaker) {
            List cursors = getCursorList(lokiRing);
            ISequentialBreaker breaker = (ISequentialBreaker) item;
            World world = player.worldObj;
            boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0;
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
            boolean dispose = breaker.disposeOfTrashBlocks(stack);

            for (int i = 0; i < cursors.size(); ++i) {
                ChunkCoordinates coords = (ChunkCoordinates) cursors.get(i);
                int xp = x + coords.posX;
                int yp = y + coords.posY;
                int zp = z + coords.posZ;
                Block block = world.getBlock(xp, yp, zp);
                breaker.breakOtherBlock(player, stack, xp, yp, zp, x, y, z, side);
                ToolCommons.removeBlockWithDrops(player, stack, player.worldObj, xp, yp, zp, x, y, z, block, new Material[]{block.getMaterial()}, silk, fortune, block.getBlockHardness(world, xp, yp, zp), dispose);
            }

        }
    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.RING;
    }

    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        setCursorList(stack, (List) null);
    }

    @SideOnly(Side.CLIENT)
    public List<ChunkCoordinates> getWireframesToDraw(EntityPlayer player, ItemStack stack1) {
        ItemStack stack = getLokiRing(player);
        if (stack == null || !PlayerHandler.getPlayerBaubles(player).isRelatedTo(stack1, stack)) return null;

        MovingObjectPosition lookPos = Minecraft.getMinecraft().objectMouseOver;
        if (lookPos != null && !player.worldObj.isAirBlock(lookPos.blockX, lookPos.blockY, lookPos.blockZ) && lookPos.entityHit == null) {
            List list = getCursorList(stack);
            ChunkCoordinates origin = getOriginPos(stack);
            Iterator var6;
            ChunkCoordinates coords;
            if (origin.posY != -1) {
                for (var6 = list.iterator(); var6.hasNext(); coords.posZ += origin.posZ) {
                    coords = (ChunkCoordinates) var6.next();
                    coords.posX += origin.posX;
                    coords.posY += origin.posY;
                }
            } else {
                for (var6 = list.iterator(); var6.hasNext(); coords.posZ += lookPos.blockZ) {
                    coords = (ChunkCoordinates) var6.next();
                    coords.posX += lookPos.blockX;
                    coords.posY += lookPos.blockY;
                }
            }

            return list;
        } else {
            return null;
        }
    }

    public ChunkCoordinates getSourceWireframe(EntityPlayer player, ItemStack stack1) {
        ItemStack stack = getLokiRing(player);

        if (stack == null || !PlayerHandler.getPlayerBaubles(player).isRelatedTo(stack1, stack)) {
            return null;
        } else {
            return getOriginPos(stack);
        }
    }

    private static ItemStack getLokiRing(EntityPlayer player) {
        ItemStack[] baubles = PlayerHandler.getPlayerBaubles(player).getStacks();
        for (ItemStack stack : baubles) {
            if (isLokiRing(stack)) return stack;
        }
        return null;
    }

    private static boolean isLokiRing(ItemStack stack) {
        return stack != null && (stack.getItem() == ModItems.lokiRing || stack.getItem() == ModItems.aesirRing);
    }

    private static ChunkCoordinates getOriginPos(ItemStack stack) {
        int x = ItemNBTHelper.getInt(stack, "xOrigin", 0);
        int y = ItemNBTHelper.getInt(stack, "yOrigin", -1);
        int z = ItemNBTHelper.getInt(stack, "zOrigin", 0);
        return new ChunkCoordinates(x, y, z);
    }

    private static void setOriginPos(ItemStack stack, int x, int y, int z) {
        ItemNBTHelper.setInt(stack, "xOrigin", x);
        ItemNBTHelper.setInt(stack, "yOrigin", y);
        ItemNBTHelper.setInt(stack, "zOrigin", z);
    }

    private static List<ChunkCoordinates> getCursorList(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "cursorList", false);
        ArrayList cursors = new ArrayList();
        int count = cmp.getInteger("cursorCount");

        for (int i = 0; i < count; ++i) {
            NBTTagCompound cursorCmp = cmp.getCompoundTag("cursor" + i);
            int x = cursorCmp.getInteger("xOffset");
            int y = cursorCmp.getInteger("yOffset");
            int z = cursorCmp.getInteger("zOffset");
            cursors.add(new ChunkCoordinates(x, y, z));
        }

        return cursors;
    }

    private static void setCursorList(ItemStack stack, List<ChunkCoordinates> cursors) {
        NBTTagCompound cmp = new NBTTagCompound();
        if (cursors != null) {
            int i = 0;

            for (Iterator var4 = cursors.iterator(); var4.hasNext(); ++i) {
                ChunkCoordinates cursor = (ChunkCoordinates) var4.next();
                NBTTagCompound cursorCmp = cursorToCmp(cursor.posX, cursor.posY, cursor.posZ);
                cmp.setTag("cursor" + i, cursorCmp);
            }

            cmp.setInteger("cursorCount", i);
        }

        ItemNBTHelper.setCompound(stack, "cursorList", cmp);
    }

    private static NBTTagCompound cursorToCmp(int x, int y, int z) {
        NBTTagCompound cmp = new NBTTagCompound();
        cmp.setInteger("xOffset", x);
        cmp.setInteger("yOffset", y);
        cmp.setInteger("zOffset", z);
        return cmp;
    }

    private static void addCursor(ItemStack stack, int x, int y, int z) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "cursorList", false);
        int count = cmp.getInteger("cursorCount");
        cmp.setTag("cursor" + count, cursorToCmp(x, y, z));
        cmp.setInteger("cursorCount", count + 1);
        ItemNBTHelper.setCompound(stack, "cursorList", cmp);
    }

    public boolean usesMana(ItemStack stack) {
        return true;
    }
}
