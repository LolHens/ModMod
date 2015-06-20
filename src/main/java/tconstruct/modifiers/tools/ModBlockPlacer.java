package tconstruct.modifiers.tools;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.tools.HarvestTool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by LolHens on 19.06.2015.
 */
public class ModBlockPlacer extends ModStringSet {
    public ModBlockPlacer(ItemStack[] items) {
        super(items, null, -1, "BlockPlacer", EnumChatFormatting.AQUA.toString(), "BlockPlacer");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        return tool.getItem() instanceof HarvestTool
                && super.canModify(tool, input);
    }

    public boolean matches(ItemStack[] recipe, ItemStack input) {
        ItemStack stack = getItemBlock(recipe);

        if (stack == null) return false;

        this.value = stack.getItem().getUnlocalizedName();

        return canModify(input, recipe);
    }

    private static ItemStack getItemBlock(ItemStack[] recipe) {
        ItemStack itemStack = null;
        boolean dispenser = false;

        for (ItemStack stack : recipe) {
            if (stack == null) continue;

            if (stack.getItem() == Item.getItemFromBlock(Blocks.dispenser) && dispenser == false)
                dispenser = true;
            else if (itemStack == null)
                itemStack = stack;
            else
                return null;
        }

        return dispenser ? itemStack : null;
    }

    public static Set<Item> getPreferredItems(ItemStack stack) {
        Set<String> itemNames = ModStringSet.getValues(stack, ModBlockPlacer.class);

        Set<Item> items = new HashSet<Item>();

        if (itemNames == null) return items;

        for (String itemName : itemNames) {
            Iterator<Item> i = Item.itemRegistry.iterator();

            while (i.hasNext()) {
                Item item = i.next();

                if (item == null || !(item instanceof ItemBlock)) continue;

                if (item.getUnlocalizedName().equals(itemName)) {
                    items.add((Item) item);
                    break;
                }
            }
        }

        return items;
    }
}
