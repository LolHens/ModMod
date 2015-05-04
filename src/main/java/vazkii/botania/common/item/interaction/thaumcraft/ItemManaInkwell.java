//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.interaction.thaumcraft;

import cpw.mods.fml.common.Optional.Interface;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.IScribeTools;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ItemMod;

import java.util.List;

@Interface(
        modid = "Thaumcraft",
        iface = "thaumcraft.api.IScribeTools"
)
public class ItemManaInkwell extends ItemMod implements IManaItem, IScribeTools {
    private static final int COST_PER_USE = 50;
    private static final int USES = 150;
    protected static final int MAX_MANA = 7500;
    private static final String TAG_MANA = "mana";

    public ItemManaInkwell() {
        this.setUnlocalizedName("manaInkwell");
        this.setMaxDurability(150);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 150));
        list.add(new ItemStack(item));
    }

    public int getDamage(ItemStack stack) {
        float mana = (float) this.getMana(stack);
        return 150 - (int) (mana / (float) this.getMaxMana(stack) * 150.0F);
    }

    public void setDamage(ItemStack stack, int damage) {
        int currentDamage = stack.getMetadata();
        if (damage > currentDamage) {
            int cost = (damage - currentDamage) * 50;
            int mana = this.getMana(stack);
            if (mana >= cost) {
                this.addMana(stack, -cost);
                return;
            }
        }

        super.setDamage(stack, damage);
    }

    public int getDisplayDamage(ItemStack stack) {
        return this.getDamage(stack);
    }

    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 2147483647;
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, "mana", mana);
    }

    public int getMana(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "mana", 0);
    }

    public int getMaxMana(ItemStack stack) {
        return 7500;
    }

    public void addMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(this.getMana(stack) + mana, this.getMaxMana(stack)));
        stack.setMetadata(this.getDamage(stack));
    }

    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return true;
    }

    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return true;
    }

    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return false;
    }

    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return false;
    }

    public boolean isNoExport(ItemStack stack) {
        return true;
    }
}
