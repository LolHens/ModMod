//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.tools.inventory;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.TinkerModification;

public class SlotTool extends Slot {
    public EntityPlayer player;
    Random random = new Random();

    public SlotTool(EntityPlayer entityplayer, IInventory builder, int par3, int par4, int par5) {
        super(builder, par3, par4, par5);
        this.player = entityplayer;
    }

    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack stack) {
        this.onCrafting(stack);
        super.onPickupFromSlot(par1EntityPlayer, stack);
    }

    protected void onCrafting(ItemStack stack, int par2) {
        this.onCrafting(stack);
    }

    protected void onCrafting(ItemStack stack) {
        if (stack.getItem() instanceof IModifyable) {
            NBTTagCompound amount = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean i = Boolean.valueOf(super.inventory.getStackInSlot(2) != null || super.inventory.getStackInSlot(3) != null);

            for (int compare = 2; compare <= 3; ++compare) {
                if (TinkerModification.useToolStationItem(inventory.getStackInSlot(compare)))
                    super.inventory.decrStackSize(compare, 1);
            }

            ItemStack var8 = super.inventory.getStackInSlot(1);
            int amount1 = var8.getItem() instanceof IModifyable ? var8.stackSize : 1;
            super.inventory.decrStackSize(1, amount1);
            if (!this.player.worldObj.isRemote && i.booleanValue()) {
                this.player.worldObj.playSoundEffect(this.player.posX, this.player.posY, this.player.posZ, "tinker:little_saw", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(super.inventory, this.player, stack));
        } else {
            int var6 = super.inventory.getStackInSlot(1).stackSize;
            super.inventory.decrStackSize(1, var6);

            for (int var7 = 0; var7 < super.inventory.getSizeInventory(); ++var7) {
                if (super.inventory.getStackInSlot(var7) != null && super.inventory.getStackInSlot(var7).getItem() == Items.name_tag) {
                    super.inventory.decrStackSize(var7, 1);
                    break;
                }
            }
        }

    }
}
