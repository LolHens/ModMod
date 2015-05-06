//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import vazkii.botania.common.item.ModItems;

public class SpellClothRecipe implements IRecipe {
    public SpellClothRecipe() {
    }

    public boolean matches(InventoryCrafting var1, World var2) {
        boolean foundCloth = false;
        boolean foundEnchanted = false;

        for (int i = 0; i < var1.getSizeInventory(); ++i) {
            ItemStack stack = var1.getStackInSlot(i);
            if (stack != null) {
                if (isEnchanted(stack) && !foundEnchanted) {
                    foundEnchanted = true;
                } else {
                    if (stack.getItem() != ModItems.spellCloth || foundCloth) {
                        return false;
                    }

                    foundCloth = true;
                }
            }
        }

        return foundCloth && foundEnchanted;
    }

    public ItemStack getCraftingResult(InventoryCrafting var1) {
        ItemStack stackToDisenchant = null;

        for (int cmp = 0; cmp < var1.getSizeInventory(); ++cmp) {
            ItemStack stack = var1.getStackInSlot(cmp);
            if (stack != null && isEnchanted(stack)) {
                stackToDisenchant = stack.copy();
                break;
            }
        }

        if (stackToDisenchant == null) {
            return null;
        } else {
            NBTTagCompound var5 = (NBTTagCompound) stackToDisenchant.getTagCompound().copy();
            var5.removeTag("ench");
            var5.removeTag("RepairCost");
            stackToDisenchant.setTagCompound(var5);
            return stackToDisenchant;
        }
    }

    private boolean isEnchanted(ItemStack stack) {
        return stack.isItemEnchanted() ||
                (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("RepairCost"));
    }

    public int getRecipeSize() {
        return 10;
    }

    public ItemStack getRecipeOutput() {
        return null;
    }
}
