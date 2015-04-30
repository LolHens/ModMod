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
import tconstruct.tools.TinkerModification;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;

public class ToolForgeContainer extends ToolStationContainer {
    public ToolForgeContainer(InventoryPlayer inventoryplayer, ToolForgeLogic logic) {
        super(inventoryplayer, logic);
    }

    public void initializeContainer(InventoryPlayer inventoryplayer, ToolStationLogic builderlogic) {
        super.invPlayer = inventoryplayer;
        super.logic = builderlogic;
        super.toolSlot = new SlotToolForge(inventoryplayer.player, super.logic, 0, 225, 38);
        this.addSlotToContainer(super.toolSlot);
        super.slots = new Slot[]{new Slot(super.logic, 1, 167, 29), new Slot(super.logic, 2, 169, 29), new Slot(super.logic, 3, 167, 47), new Slot(super.logic, 4, 149, 47)};

        int column;
        for(column = 0; column < 4; ++column) {
            this.addSlotToContainer(super.slots[column]);
        }

        for(column = 0; column < 3; ++column) {
            for(int row = 0; row < 9; ++row) {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for(column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 118 + column * 18, 142));
        }

    }

    public void resetSlots(int[] posX, int[] posY) {
        super.inventorySlots.clear();
        super.inventoryItemStacks.clear();
        this.addSlotToContainer(super.toolSlot);

        int column;
        for(column = 0; column < 4; ++column) {
            super.slots[column].xDisplayPosition = posX[column] + 111;
            super.slots[column].yDisplayPosition = posY[column] + 1;
            this.addSlotToContainer(super.slots[column]);
        }

        for(column = 0; column < 3; ++column) {
            for(int row = 0; row < 9; ++row) {
                this.addSlotToContainer(new Slot(super.invPlayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for(column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(super.invPlayer, column, 118 + column * 18, 142));
        }

    }

    protected void craftTool(ItemStack stack) {
        int i;
        if(stack.getItem() instanceof IModifyable) {
            NBTTagCompound stack2 = stack.getTagCompound().getCompoundTag(((IModifyable)stack.getItem()).getBaseTagName());
            Boolean amount = Boolean.valueOf(super.logic.getStackInSlot(2) != null || super.logic.getStackInSlot(3) != null || super.logic.getStackInSlot(4) != null);

            for(i = 2; i <= 4; ++i) {
                if (TinkerModification.useToolStationItem(logic.getStackInSlot(i))) super.logic.decrStackSize(i, 1);
            }

            ItemStack var9 = super.logic.getStackInSlot(1);
            int amount1 = var9.getItem() instanceof IModifyable?var9.stackSize:1;
            super.logic.decrStackSize(1, amount1);
            EntityPlayer player = super.invPlayer.player;
            if(!player.worldObj.isRemote && amount.booleanValue()) {
                player.worldObj.playAuxSFX(1021, (int)player.posX, (int)player.posY, (int)player.posZ, 0);
            }

            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(super.logic, player, stack));
        } else {
            ItemStack var7 = super.logic.getStackInSlot(1);
            int var8 = super.logic.getStackInSlot(1).stackSize;
            super.logic.decrStackSize(1, var8);
            if(!ToolStationLogic.canRename(var7.getTagCompound(), var7)) {
                for(i = 0; i < super.logic.getSizeInventory(); ++i) {
                    if(super.logic.getStackInSlot(i) != null && super.logic.getStackInSlot(i).getItem() == Items.name_tag) {
                        super.logic.decrStackSize(i, 1);
                        break;
                    }
                }
            }
        }

    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        Block block = super.logic.getWorld().getBlock(super.logic.xCoord, super.logic.yCoord, super.logic.zCoord);
        return block != TinkerTools.toolForge && block != TinkerTools.craftingSlabWood?false:super.logic.isUseableByPlayer(entityplayer);
    }
}
