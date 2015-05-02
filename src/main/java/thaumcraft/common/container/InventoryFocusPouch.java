//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumcraft.common.container;

import baubles.api.IBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemFocusPouch;

public class InventoryFocusPouch implements IInventory {
    public ItemStack[] stackList = new ItemStack[18];
    private Container eventHandler;

    public InventoryFocusPouch(Container par1Container) {
        this.eventHandler = par1Container;
    }

    public int getSizeInventory() {
        return this.stackList.length;
    }

    public ItemStack getStackInSlot(int par1) {
        return par1 >= this.getSizeInventory() ? null : this.stackList[par1];
    }

    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.stackList[par1] != null) {
            ItemStack var2 = this.stackList[par1];
            this.stackList[par1] = null;
            return var2;
        } else {
            return null;
        }
    }

    public ItemStack decrStackSize(int par1, int par2) {
        if (this.stackList[par1] != null) {
            ItemStack var3;
            if (this.stackList[par1].stackSize <= par2) {
                var3 = this.stackList[par1];
                this.stackList[par1] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return var3;
            } else {
                var3 = this.stackList[par1].splitStack(par2);
                if (this.stackList[par1].stackSize == 0) {
                    this.stackList[par1] = null;
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return var3;
            }
        } else {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack;
        this.eventHandler.onCraftMatrixChanged(this);
    }

    public int getInventoryStackLimit() {
        return 1;
    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return true;
    }

    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return itemstack != null && (itemstack.getItem() instanceof ItemFocusBasic || (itemstack.getItem() instanceof IBauble && !(itemstack.getItem() instanceof ItemFocusPouch)));
    }

    public String getInventoryName() {
        return "container.focuspouch";
    }

    public boolean isCustomInventoryName() {
        return false;
    }

    public void markDirty() {
    }

    public void openChest() {
    }

    public void closeChest() {
    }
}
