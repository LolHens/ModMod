package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.tools.HarvestTool;

/**
 * Created by LolHens on 30.04.2015.
 */
public class ModPrecision extends ModBoolean {
    public ModPrecision(ItemStack[] items) {
        super(items, -1, "Precision", EnumChatFormatting.DARK_PURPLE.toString(), "Precision");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tool.getItem() instanceof HarvestTool
                && tags.getInteger("Modifiers") > 0
                && !tags.getBoolean(key);
    }

    public static boolean isPrecise(ItemStack tool) {
        if (tool == null || !tool.hasTagCompound()) return false;
        return tool.getTagCompound().getCompoundTag("InfiTool").getBoolean("Precision");
    }
}
