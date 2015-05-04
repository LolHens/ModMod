package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.IBaubleContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by LolHens on 04.05.2015.
 */
public class InventoryBaublesExtended extends InventoryBaubles {
    public InventoryBaublesExtended(EntityPlayer player) {
        super(player);
    }

    private StackRef[] cachedContainerBaubles;

    protected void cacheContainerBaubles() {
        cachedContainerBaubles = getContainerBaubles();
    }

    protected void releaseCachedContainerBaubles() {
        cachedContainerBaubles = null;
    }

    public StackRef[] getContainerBaubles() {
        if (cachedContainerBaubles != null) return cachedContainerBaubles;

        EntityPlayer entity = player.get();

        List<StackRef> baubles = new LinkedList<StackRef>();

        for (int i = 0; i < super.getSizeInventory(); i++) {
            superCall = true;
            ItemStack slot = getStackInSlot(i);
            superCall = false;
            if (slot != null && slot.getItem() instanceof IBaubleContainer) {
                ItemStack[] baubleArray = ((IBaubleContainer) slot.getItem()).getBaubles(slot, entity);
                for (int i2 = 0; i2 < baubleArray.length; i2++) {
                    ItemStack bauble = baubleArray[i2];
                    baubles.add(new StackRef(slot, i2, bauble));
                }
            }
        }

        return baubles.toArray(new StackRef[0]);
    }

    private boolean superCall = false;

    @Override
    public int getSizeInventory() {
        if (superCall) return super.getSizeInventory();
        return super.getSizeInventory() + getContainerBaubles().length;
    }

    public ItemStack[] getStacks() {
        ItemStack[] stacks = new ItemStack[getSizeInventory()];
        cacheContainerBaubles();
        for (int i = 0; i < stacks.length; i++) stacks[i] = getStackInSlot(i);
        releaseCachedContainerBaubles();
        return stacks;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        int baubleInvSize = super.getSizeInventory();

        if (index < baubleInvSize) {
            superCall = true;
            ItemStack itemStack = super.getStackInSlot(index);
            superCall = false;
            return itemStack;
        } else {
            return getContainerBaubles()[index - baubleInvSize].get();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        int baubleInvSize = super.getSizeInventory();

        if (index < baubleInvSize) {
            return super.decrStackSize(index, count);
        } else {
            StackRef ref = getContainerBaubles()[index - baubleInvSize];

            if (ref.get() != null) {
                if (ref.get().stackSize <= count) {
                    ItemStack itemStack = ref.get();

                    if (itemStack != null && itemStack.getItem() instanceof IBauble) {
                        ((IBauble) itemStack.getItem()).onUnequipped(itemStack, player.get());
                    }

                    ref.set(null);

                    return itemStack;
                } else {
                    ItemStack itemStack = ref.get().splitStack(count);

                    if (itemStack != null && itemStack.getItem() instanceof IBauble) {
                        ((IBauble) itemStack.getItem()).onUnequipped(itemStack, player.get());
                    }

                    if (ref.get().stackSize == 0) ref.set(null);

                    return itemStack;
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        int baubleInvSize = super.getSizeInventory();

        if (index < baubleInvSize) {
            return super.getStackInSlotOnClosing(index);
        } else {
            StackRef ref = getContainerBaubles()[index - baubleInvSize];
            ItemStack stack = ref.get();
            ref.set(null);
            return stack;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        int baubleInvSize = super.getSizeInventory();

        if (index < baubleInvSize) {
            super.setInventorySlotContents(index, stack);
        } else {
            getContainerBaubles()[index - baubleInvSize].set(stack);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        int baubleInvSize = super.getSizeInventory();

        if (index < baubleInvSize) {
            return super.isItemValidForSlot(index, stack);
        } else {
            return stack != null
                    && stack.getItem() instanceof IBauble
                    && ((IBauble) stack.getItem()).canEquip(stack, player.get())
                    && getContainerBaubles()[index - baubleInvSize].isValid(stack);
        }
    }

    public void syncSlotToClients(int slot) {
        if (slot < stackList.length) super.syncSlotToClients(slot);
    }

    private class StackRef {
        private ItemStack baubleContainer;
        private int num;
        private ItemStack content;

        public StackRef(ItemStack baubleContainer, int num, ItemStack content) {
            this.baubleContainer = baubleContainer;
            this.num = num;
            this.content = content;
        }

        public ItemStack get() {
            return content;
        }

        public boolean isValid(ItemStack itemStack) {
            return ((IBaubleContainer) baubleContainer.getItem()).canReplaceBauble(baubleContainer, player.get(), num, itemStack);
        }

        public void set(ItemStack itemStack) {
            ((IBaubleContainer) baubleContainer.getItem()).replaceBauble(baubleContainer, player.get(), num, itemStack);
            markDirty();
        }

        private void markDirty() {
            content = ((IBaubleContainer) baubleContainer.getItem()).getBaubles(baubleContainer, player.get())[num];
        }
    }
}
