package tconstruct.modifiers.tools;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.tools.HarvestTool;

import java.util.Iterator;

/**
 * Created by LolHens on 19.06.2015.
 */
public class ModBlockPlacer extends ModString {
    public ModBlockPlacer(ItemStack[] items) {
        super(items, null, -1, "BlockPlacer", EnumChatFormatting.AQUA.toString(), null);
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tool.getItem() instanceof HarvestTool
                && tags.getInteger("Modifiers") > 0
                && !tags.hasKey(super.key, new NBTTagString().getId());
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        ItemStack stack = getItemBlock(input);

        this.tooltipName = "BlockPlacer (" + stack.getDisplayName() + ")";

        this.value = stack.getUnlocalizedName();

        super.modify(input, tool);
    }

    public boolean matches(ItemStack[] recipe, ItemStack input) {
        if (!this.canModify(input, recipe)) {
            return false;
        }
        return getItemBlock(recipe) != null;
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

        return itemStack;
    }

    public static Item getPreferredItem(ItemStack stack) {
        String itemName = ModString.getValue(stack, ModBlockPlacer.class);

        if (itemName == null) return null;

        Iterator<Item> i = Item.itemRegistry.iterator();

        while (i.hasNext()) {
            Item item = i.next();

            if (item == null || !(item instanceof ItemBlock)) continue;

            if (item.getUnlocalizedName().equals(itemName)) return (Item) item;
        }

        return null;
    }
}
