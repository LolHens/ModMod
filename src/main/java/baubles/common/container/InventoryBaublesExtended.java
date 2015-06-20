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

    public synchronized StackRef[] getContainerBaubles() {
        if (cachedContainerBaubles != null) return cachedContainerBaubles;

        EntityPlayer entity = player.get();

        List<StackRef> baubles = new LinkedList<StackRef>();

        for (int i = 0; i < getStandardInvSize(); i++) {
            ItemStack slot = getStackInSlot(i);
            if (slot != null && slot.getItem() instanceof IBaubleContainer) {
                ItemStack[] baubleArray = ((IBaubleContainer) slot.getItem()).getBaubles(slot, entity);
                for (int i2 = 0; i2 < baubleArray.length; i2++) {
                    ItemStack bauble = baubleArray[i2];
                    baubles.add(new StackRef(slot, i2, bauble));
                }
            }
        }

        StackRef[] array = new StackRef[baubles.size()];
        baubles.toArray(array);
        return array;
    }

    @Override
    public int getSizeInventory() {
        StackRef[] containerBaubles = getContainerBaubles();
        try {
            return getStandardInvSize() + containerBaubles.length;
        } catch (NullPointerException e) {
            throw new NullPointerException("containerBaubles: " + containerBaubles);
        }
    }

    public int getStandardInvSize() {
        return super.getSizeInventory();
    }

    public ItemStack[] getStacks() {
        ItemStack[] stacks = new ItemStack[getSizeInventory()];
        cacheContainerBaubles();
        for (int i = 0; i < stacks.length; i++) stacks[i] = getStackInSlot(i);
        releaseCachedContainerBaubles();
        return stacks;
    }

    public boolean isContainerBauble(int index) {
        int baubleInvSize = getStandardInvSize();

        if (index < baubleInvSize) return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        int baubleInvSize = getStandardInvSize();

        if (index < baubleInvSize) {
            return stackList[index];
        } else {
            StackRef[] refs = getContainerBaubles();
            if (index - baubleInvSize >= refs.length) return null;
            return refs[index - baubleInvSize].get();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        int baubleInvSize = getStandardInvSize();

        if (index < baubleInvSize) {
            return super.decrStackSize(index, count);
        } else {
            StackRef[] refs = getContainerBaubles();
            if (index - baubleInvSize >= refs.length) return null;

            StackRef ref = refs[index - baubleInvSize];

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
        int baubleInvSize = getStandardInvSize();

        if (index < baubleInvSize) {
            return super.getStackInSlotOnClosing(index);
        } else {
            StackRef[] refs = getContainerBaubles();
            if (index - baubleInvSize >= refs.length) return null;

            StackRef ref = refs[index - baubleInvSize];
            ItemStack stack = ref.get();
            ref.set(null);
            return stack;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        int baubleInvSize = getStandardInvSize();

        if (index < baubleInvSize) {
            super.setInventorySlotContents(index, stack);
        } else {
            StackRef[] refs = getContainerBaubles();
            if (index - baubleInvSize >= refs.length) return;

            refs[index - baubleInvSize].set(stack);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        int baubleInvSize = getStandardInvSize();

        if (index < baubleInvSize) {
            return super.isItemValidForSlot(index, stack);
        } else {
            StackRef[] refs = getContainerBaubles();
            if (index - baubleInvSize >= refs.length) return false;

            return stack != null
                    && stack.getItem() instanceof IBauble
                    && ((IBauble) stack.getItem()).canEquip(stack, player.get())
                    && refs[index - baubleInvSize].isValid(stack);
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
