//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package baubles.common.container;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.Baubles;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSyncBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class InventoryBaubles implements IInventory {
    public ItemStack[] stackList = new ItemStack[4];
    private Container eventHandler;
    public WeakReference<EntityPlayer> player;
    public boolean blockEvents = false;

    public InventoryBaubles(EntityPlayer player) {
        this.player = new WeakReference(player);
    }

    public ItemStack[] getStacks() {
        return stackList;
    }

    public boolean isContainerBauble(int index) {
        return false;
    }

    public Container getEventHandler() {
        return this.eventHandler;
    }

    public void setEventHandler(Container eventHandler) {
        this.eventHandler = eventHandler;
    }

    public int getSizeInventory() {
        return this.stackList.length;
    }

    public ItemStack getStackInSlot(int par1) {
        return par1 >= this.getSizeInventory() ? null : this.stackList[par1];
    }

    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean isCustomInventoryName() {
        return hasCustomInventoryName();
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.stackList[par1] != null) {
            ItemStack itemstack = this.stackList[par1];
            this.stackList[par1] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public ItemStack decrStackSize(int par1, int par2) {
        if (this.stackList[par1] != null) {
            ItemStack itemstack;
            if (this.stackList[par1].stackSize <= par2) {
                itemstack = this.stackList[par1];
                if (itemstack != null && itemstack.getItem() instanceof IBauble) {
                    ((IBauble) itemstack.getItem()).onUnequipped(itemstack, (EntityLivingBase) this.player.get());
                }

                this.stackList[par1] = null;
                if (this.eventHandler != null) {
                    this.eventHandler.onCraftMatrixChanged(this);
                }

                this.syncSlotToClients(par1);
                return itemstack;
            } else {
                itemstack = this.stackList[par1].splitStack(par2);
                if (itemstack != null && itemstack.getItem() instanceof IBauble) {
                    ((IBauble) itemstack.getItem()).onUnequipped(itemstack, (EntityLivingBase) this.player.get());
                }

                if (this.stackList[par1].stackSize == 0) {
                    this.stackList[par1] = null;
                }

                if (this.eventHandler != null) {
                    this.eventHandler.onCraftMatrixChanged(this);
                }

                this.syncSlotToClients(par1);
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack stack) {
        if (!this.blockEvents && this.stackList[par1] != null) {
            ((IBauble) this.stackList[par1].getItem()).onUnequipped(this.stackList[par1], (EntityLivingBase) this.player.get());
        }

        this.stackList[par1] = stack;
        if (!this.blockEvents && stack != null && stack.getItem() instanceof IBauble) {
            ((IBauble) stack.getItem()).onEquipped(stack, (EntityLivingBase) this.player.get());
        }

        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged(this);
        }

        this.syncSlotToClients(par1);
    }

    public int getInventoryStackLimit() {
        return 1;
    }

    public void markDirty() {
        try {
            ((EntityPlayer) this.player.get()).inventory.markDirty();
        } catch (Exception var2) {
            ;
        }

    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return true;
    }

    @Override
    public void openChest() {
        openInventory();
    }

    @Override
    public void closeChest() {
        closeInventory();
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return stack != null && stack.getItem() instanceof IBauble && ((IBauble) stack.getItem()).canEquip(stack, (EntityLivingBase) this.player.get()) ? (i == 0 && ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.AMULET ? true : ((i == 1 || i == 2) && ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.RING ? true : i == 3 && ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.BELT)) : false;
    }

    public void saveNBT(EntityPlayer player) {
        NBTTagCompound tags = player.getEntityData();
        this.saveNBT(tags);
    }

    public void saveNBT(NBTTagCompound tags) {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.stackList.length; ++i) {
            if (this.stackList[i] != null) {
                NBTTagCompound invSlot = new NBTTagCompound();
                invSlot.setByte("Slot", (byte) i);
                this.stackList[i].writeToNBT(invSlot);
                tagList.appendTag(invSlot);
            }
        }

        tags.setTag("Baubles.Inventory", tagList);
    }

    public void readNBT(EntityPlayer player) {
        NBTTagCompound tags = player.getEntityData();
        this.readNBT(tags);
    }

    public void readNBT(NBTTagCompound tags) {
        NBTTagList tagList = tags.getTagList("Baubles.Inventory", 10);

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
            if (itemstack != null) {
                this.stackList[j] = itemstack;
            }
        }

    }

    public void dropItems(ArrayList<EntityItem> drops) {
        for (int i = 0; i < 4; ++i) {
            if (this.stackList[i] != null) {
                EntityItem ei = new EntityItem(((EntityPlayer) this.player.get()).worldObj, ((EntityPlayer) this.player.get()).posX, ((EntityPlayer) this.player.get()).posY + (double) ((EntityPlayer) this.player.get()).eyeHeight, ((EntityPlayer) this.player.get()).posZ, this.stackList[i].copy());
                ei.delayBeforeCanPickup = 40;
                float f1 = ((EntityPlayer) this.player.get()).worldObj.rand.nextFloat() * 0.5F;
                float f2 = ((EntityPlayer) this.player.get()).worldObj.rand.nextFloat() * 3.1415927F * 2.0F;
                ei.motionX = (double) (-MathHelper.sin(f2) * f1);
                ei.motionZ = (double) (MathHelper.cos(f2) * f1);
                ei.motionY = 0.20000000298023224D;
                drops.add(ei);
                this.stackList[i] = null;
                this.syncSlotToClients(i);
            }
        }

    }

    public void dropItemsAt(ArrayList<EntityItem> drops, Entity e) {
        for (int i = 0; i < 4; ++i) {
            if (this.stackList[i] != null) {
                EntityItem ei = new EntityItem(e.worldObj, e.posX, e.posY + (double) e.getEyeHeight(), e.posZ, this.stackList[i].copy());
                ei.delayBeforeCanPickup = 40;
                float f1 = e.worldObj.rand.nextFloat() * 0.5F;
                float f2 = e.worldObj.rand.nextFloat() * 3.1415927F * 2.0F;
                ei.motionX = (double) (-MathHelper.sin(f2) * f1);
                ei.motionZ = (double) (MathHelper.cos(f2) * f1);
                ei.motionY = 0.20000000298023224D;
                drops.add(ei);
                this.stackList[i] = null;
                this.syncSlotToClients(i);
            }
        }

    }

    public void syncSlotToClients(int slot) {
        try {
            if (Baubles.proxy.getClientWorld() == null) {
                PacketHandler.INSTANCE.sendToAll(new PacketSyncBauble((EntityPlayer) this.player.get(), slot));
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
