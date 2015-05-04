//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.TinkerModification;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.ToolStationLogic;

import java.util.Random;

public class ToolStationContainer extends ActiveContainer {
    public InventoryPlayer invPlayer;
    public ToolStationLogic logic;
    public Slot[] slots;
    public SlotTool toolSlot;
    public Random random = new Random();

    public ToolStationContainer(InventoryPlayer inventoryplayer, ToolStationLogic builderlogic) {
        this.initializeContainer(inventoryplayer, builderlogic);
    }

    public void initializeContainer(InventoryPlayer inventoryplayer, ToolStationLogic builderlogic) {
        this.invPlayer = inventoryplayer;
        this.logic = builderlogic;
        this.toolSlot = new SlotTool(inventoryplayer.player, builderlogic, 0, 225, 38);
        this.addSlotToContainer(this.toolSlot);
        this.slots = new Slot[]{new Slot(builderlogic, 1, 167, 29), new Slot(builderlogic, 2, 149, 38), new Slot(builderlogic, 3, 167, 47)};

        int column;
        for (column = 0; column < 3; ++column) {
            this.addSlotToContainer(this.slots[column]);
        }

        for (column = 0; column < 3; ++column) {
            for (int row = 0; row < 9; ++row) {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for (column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 118 + column * 18, 142));
        }

    }

    public void resetSlots(int[] posX, int[] posY) {
        super.inventorySlots.clear();
        super.inventoryItemStacks.clear();
        this.addSlotToContainer(this.toolSlot);

        int column;
        for (column = 0; column < 3; ++column) {
            this.slots[column].xDisplayPosition = posX[column] + 111;
            this.slots[column].yDisplayPosition = posY[column] + 1;
            this.addSlotToContainer(this.slots[column]);
        }

        for (column = 0; column < 3; ++column) {
            for (int row = 0; row < 9; ++row) {
                this.addSlotToContainer(new Slot(this.invPlayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for (column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(this.invPlayer, column, 118 + column * 18, 142));
        }

    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        Block block = this.logic.getWorld().getBlock(this.logic.xCoord, this.logic.yCoord, this.logic.zCoord);
        return block != TinkerTools.toolStationWood && block != TinkerTools.craftingSlabWood ? false : this.logic.isUseableByPlayer(entityplayer);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = null;
        Slot slot = (Slot) super.inventorySlots.get(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotID < this.logic.getSizeInventory()) {
                if (slotID == 0) {
                    if (!this.mergeCraftedStack(slotStack, this.logic.getSizeInventory(), super.inventorySlots.size(), true, player)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(slotStack, this.logic.getSizeInventory(), super.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(slotStack, 1, this.logic.getSizeInventory(), false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    protected void craftTool(ItemStack stack) {
        int i;
        if (stack.getItem() instanceof IModifyable) {
            NBTTagCompound stack2 = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean amount = Boolean.valueOf(this.logic.getStackInSlot(2) != null || this.logic.getStackInSlot(3) != null);

            for (i = 2; i <= 3; ++i) {
                if (TinkerModification.useToolStationItem(logic.getStackInSlot(i))) this.logic.decrStackSize(i, 1);
            }

            ItemStack var9 = this.logic.getStackInSlot(1);
            int amount1 = var9.getItem() instanceof IModifyable ? var9.stackSize : 1;
            this.logic.decrStackSize(1, amount1);
            EntityPlayer player = this.invPlayer.player;
            if (!player.worldObj.isRemote && amount.booleanValue()) {
                player.worldObj.playSoundEffect((double) this.logic.xCoord, (double) this.logic.yCoord, (double) this.logic.zCoord, "tinker:little_saw", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.logic, player, stack));
        } else {
            ItemStack var7 = this.logic.getStackInSlot(1);
            int var8 = this.logic.getStackInSlot(1).stackSize;
            this.logic.decrStackSize(1, var8);
            if (!ToolStationLogic.canRename(var7.getTagCompound(), var7)) {
                for (i = 0; i < this.logic.getSizeInventory(); ++i) {
                    if (this.logic.getStackInSlot(i) != null && this.logic.getStackInSlot(i).getItem() == Items.name_tag) {
                        this.logic.decrStackSize(i, 1);
                        break;
                    }
                }
            }
        }

    }

    protected boolean mergeCraftedStack(ItemStack stack, int slotsStart, int slotsTotal, boolean playerInventory, EntityPlayer player) {
        boolean failedToMerge = false;
        int slotIndex;
        if (playerInventory) {
            slotIndex = slotsTotal - 1;
        }

        ItemStack copyStack = null;
        if (stack.stackSize > 0) {
            if (playerInventory) {
                slotIndex = slotsTotal - 1;
            } else {
                slotIndex = slotsStart;
            }

            while (!playerInventory && slotIndex < slotsTotal || playerInventory && slotIndex >= slotsStart) {
                Slot otherInventorySlot = (Slot) super.inventorySlots.get(slotIndex);
                copyStack = otherInventorySlot.getStack();
                if (copyStack == null) {
                    this.craftTool(stack);
                    otherInventorySlot.putStack(stack.copy());
                    otherInventorySlot.onSlotChanged();
                    stack.stackSize = 0;
                    failedToMerge = true;
                    break;
                }

                if (playerInventory) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        return failedToMerge;
    }
}
