//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumcraft.common.items.wands;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.wands.ItemFocusPouch;

import java.util.LinkedList;
import java.util.List;

public class ItemFocusPouchBauble extends ItemFocusPouch implements IBauble {
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
}
