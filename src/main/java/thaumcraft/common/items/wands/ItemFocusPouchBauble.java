//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumcraft.common.items.wands;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.IBaubleContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class ItemFocusPouchBauble extends ItemFocusPouch implements IBauble, IBaubleContainer {
    public ItemFocusPouchBauble() {
    }

    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    public void onWornTick(ItemStack pouch, EntityLivingBase player) {
        for (ItemStack itemStack : getContainedBaubles(pouch)) {
            ((IBauble) itemStack.getItem()).onWornTick(itemStack, player);
        }
    }

    public void onEquipped(ItemStack pouch, EntityLivingBase player) {
        for (ItemStack itemStack : getContainedBaubles(pouch)) {
            ((IBauble) itemStack.getItem()).onEquipped(itemStack, player);
        }
    }

    public void onUnequipped(ItemStack pouch, EntityLivingBase player) {
        for (ItemStack itemStack : getContainedBaubles(pouch)) {
            ((IBauble) itemStack.getItem()).onUnequipped(itemStack, player);
        }
    }

    public boolean canEquip(ItemStack pouch, EntityLivingBase player) {
        for (ItemStack itemStack : getContainedBaubles(pouch)) {
            if (!((IBauble) itemStack.getItem()).canEquip(itemStack, player)) return false;
        }
        return true;
    }

    public boolean canUnequip(ItemStack pouch, EntityLivingBase player) {
        for (ItemStack itemStack : getContainedBaubles(pouch)) {
            if (!((IBauble) itemStack.getItem()).canUnequip(itemStack, player)) return false;
        }
        return true;
    }

    private List<ItemStack> getContainedBaubles(ItemStack pouch) {
        List<ItemStack> baubles = new LinkedList<ItemStack>();
        for (ItemStack itemStack : getInventory(pouch)) {
            if (itemStack != null && itemStack.getItem() instanceof IBauble) baubles.add(itemStack);
        }
        return baubles;
    }

    @Override
    public ItemStack[] getBaubles(ItemStack container, EntityLivingBase entity) {
        return getContainedBaubles(container).toArray(new ItemStack[0]);
    }

    @Override
    public boolean canReplaceBauble(ItemStack container, EntityLivingBase entity, int num, ItemStack newStack) {
        return true;
    }

    @Override
    public void replaceBauble(ItemStack container, EntityLivingBase entity, int num, ItemStack newStack) {
        int index = 0;
        ItemStack[] inv = getInventory(container);
        for (int i = 0; i < inv.length; i++) {
            if (inv[i] != null && inv[i].getItem() instanceof IBauble) {
                if (num == index) {
                    inv[i] = newStack;
                    break;
                }
                index++;
            }
        }
        setInventory(container, inv);
    }
}
