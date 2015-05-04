//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package baubles.common.container;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.lib.PlayerHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayerExpanded extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public InventoryBaubles baubles;
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;

    public ContainerPlayerExpanded(final InventoryPlayer playerInv, boolean par2, EntityPlayer player) {
        this.isLocalWorld = par2;
        this.thePlayer = player;
        this.baubles = new InventoryBaublesExtended(player);
        this.baubles.setEventHandler(this);
        if (!player.worldObj.isRemote) {
            this.baubles.stackList = PlayerHandler.getPlayerBaubles(player).stackList;
        }

        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 144, 36));

        int i;
        int j;
        for (i = 0; i < 2; ++i) {
            for (j = 0; j < 2; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 106 + j * 18, 26 + i * 18));
            }
        }

        for (i = 0; i < 4; ++i) {
            final int ii = i;
            this.addSlotToContainer(new Slot(playerInv, playerInv.getSizeInventory() - 1 - i, 8, 8 + i * 18) {
                public int getSlotStackLimit() {
                    return 1;
                }

                public boolean isItemValid(ItemStack par1ItemStack) {
                    return par1ItemStack == null ? false : par1ItemStack.getItem().isValidArmor(par1ItemStack, ii, ContainerPlayerExpanded.this.thePlayer);
                }
            });
        }

        this.addSlotToContainer(new SlotBauble(this.baubles, BaubleType.AMULET, 0, 80, 8));
        this.addSlotToContainer(new SlotBauble(this.baubles, BaubleType.RING, 1, 80, 26));
        this.addSlotToContainer(new SlotBauble(this.baubles, BaubleType.RING, 2, 80, 44));
        this.addSlotToContainer(new SlotBauble(this.baubles, BaubleType.BELT, 3, 80, 62));

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public void onCraftMatrixChanged(IInventory par1IInventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        for (int i = 0; i < 4; ++i) {
            ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);
            if (itemstack != null) {
                player.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, (ItemStack) null);
        if (!player.worldObj.isRemote) {
            PlayerHandler.setPlayerBaubles(player, this.baubles);
        }

    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (par2 == 0) {
                if (!this.mergeItemStack(itemstack1, 13, 49, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (par2 >= 1 && par2 < 5) {
                if (!this.mergeItemStack(itemstack1, 13, 49, false)) {
                    return null;
                }
            } else if (par2 >= 5 && par2 < 9) {
                if (!this.mergeItemStack(itemstack1, 13, 49, false)) {
                    return null;
                }
            } else if (itemstack.getItem() instanceof ItemArmor && !((Slot) this.inventorySlots.get(5 + ((ItemArmor) itemstack.getItem()).armorType)).getHasStack()) {
                int j1 = 5 + ((ItemArmor) itemstack.getItem()).armorType;
                if (!this.mergeItemStack(itemstack1, j1, j1 + 1, false)) {
                    return null;
                }
            } else {
                byte j;
                if (itemstack.getItem() instanceof IBauble && ((IBauble) itemstack.getItem()).getBaubleType(itemstack) == BaubleType.AMULET && ((IBauble) itemstack.getItem()).canEquip(itemstack, this.thePlayer) && !((Slot) this.inventorySlots.get(9)).getHasStack()) {
                    j = 9;
                    if (!this.mergeItemStack(itemstack1, j, j + 1, false)) {
                        return null;
                    }
                } else if (par2 > 11 && itemstack.getItem() instanceof IBauble && ((IBauble) itemstack.getItem()).getBaubleType(itemstack) == BaubleType.RING && ((IBauble) itemstack.getItem()).canEquip(itemstack, this.thePlayer) && !((Slot) this.inventorySlots.get(10)).getHasStack()) {
                    j = 10;
                    if (!this.mergeItemStack(itemstack1, j, j + 1, false)) {
                        return null;
                    }
                } else if (par2 > 11 && itemstack.getItem() instanceof IBauble && ((IBauble) itemstack.getItem()).getBaubleType(itemstack) == BaubleType.RING && ((IBauble) itemstack.getItem()).canEquip(itemstack, this.thePlayer) && !((Slot) this.inventorySlots.get(11)).getHasStack()) {
                    j = 11;
                    if (!this.mergeItemStack(itemstack1, j, j + 1, false)) {
                        return null;
                    }
                } else if (itemstack.getItem() instanceof IBauble && ((IBauble) itemstack.getItem()).getBaubleType(itemstack) == BaubleType.BELT && ((IBauble) itemstack.getItem()).canEquip(itemstack, this.thePlayer) && !((Slot) this.inventorySlots.get(12)).getHasStack()) {
                    j = 12;
                    if (!this.mergeItemStack(itemstack1, j, j + 1, false)) {
                        return null;
                    }
                } else if (par2 >= 13 && par2 < 40) {
                    if (!this.mergeItemStack(itemstack1, 40, 49, false)) {
                        return null;
                    }
                } else if (par2 >= 40 && par2 < 49) {
                    if (!this.mergeItemStack(itemstack1, 13, 40, false)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(itemstack1, 13, 49, false, slot)) {
                    return null;
                }
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    private void unequipBauble(ItemStack stack) {
    }

    public void putStacksInSlots(ItemStack[] p_75131_1_) {
        this.baubles.blockEvents = true;
        super.putStacksInSlots(p_75131_1_);
    }

    protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4, Slot ss) {
        boolean flag1 = false;
        int k = par2;
        if (par4) {
            k = par3 - 1;
        }

        Slot slot;
        ItemStack itemstack1;
        if (par1ItemStack.isStackable()) {
            while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2)) {
                slot = (Slot) this.inventorySlots.get(k);
                itemstack1 = slot.getStack();
                if (itemstack1 != null && itemstack1.getItem() == par1ItemStack.getItem() && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getCurrentDurability() == itemstack1.getCurrentDurability()) && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)) {
                    int l = itemstack1.stackSize + par1ItemStack.stackSize;
                    if (l <= par1ItemStack.getMaxStackSize()) {
                        if (ss instanceof SlotBauble) {
                            this.unequipBauble(par1ItemStack);
                        }

                        par1ItemStack.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        flag1 = true;
                    } else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize()) {
                        if (ss instanceof SlotBauble) {
                            this.unequipBauble(par1ItemStack);
                        }

                        par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = par1ItemStack.getMaxStackSize();
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (par4) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        if (par1ItemStack.stackSize > 0) {
            if (par4) {
                k = par3 - 1;
            } else {
                k = par2;
            }

            while (!par4 && k < par3 || par4 && k >= par2) {
                slot = (Slot) this.inventorySlots.get(k);
                itemstack1 = slot.getStack();
                if (itemstack1 == null) {
                    if (ss instanceof SlotBauble) {
                        this.unequipBauble(par1ItemStack);
                    }

                    slot.putStack(par1ItemStack.copy());
                    slot.onSlotChanged();
                    par1ItemStack.stackSize = 0;
                    flag1 = true;
                    break;
                }

                if (par4) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        return flag1;
    }

    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot) {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }
}
