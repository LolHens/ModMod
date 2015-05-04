//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.TinkerModification;

import java.util.Random;

public class SlotToolForge extends SlotTool {
    Random random = new Random();

    public SlotToolForge(EntityPlayer entityplayer, IInventory builder, int par3, int par4, int par5) {
        super(entityplayer, builder, par3, par4, par5);
    }

    protected void onCrafting(ItemStack stack) {
        if (stack.getItem() instanceof IModifyable) {
            NBTTagCompound amount = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean i = Boolean.valueOf(super.inventory.getStackInSlot(2) != null || super.inventory.getStackInSlot(3) != null || super.inventory.getStackInSlot(4) != null);

            for (int compare = 2; compare <= 4; ++compare) {
                if (TinkerModification.useToolStationItem(inventory.getStackInSlot(compare)))
                    super.inventory.decrStackSize(compare, 1);
            }

            ItemStack var8 = super.inventory.getStackInSlot(1);
            int amount1 = var8.getItem() instanceof IModifyable ? var8.stackSize : 1;
            super.inventory.decrStackSize(1, amount1);
            if (!super.player.worldObj.isRemote && i.booleanValue()) {
                super.player.worldObj.playAuxSFX(1021, (int) super.player.posX, (int) super.player.posY, (int) super.player.posZ, 0);
            }

            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(super.inventory, super.player, stack));
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
