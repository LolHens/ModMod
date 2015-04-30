package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.tools.AOEHarvestTool;
import tconstruct.library.tools.HarvestTool;

/**
 * Created by LolHens on 30.04.2015.
 */
public class ModConvenient extends ModBoolean {
    public ModConvenient(ItemStack[] items) {
        super(items, -1, "Convenient", EnumChatFormatting.GREEN.toString(), "Convenient");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tool.getItem() instanceof AOEHarvestTool
                && tags.getInteger("Modifiers") > 0
                && !tags.getBoolean("Universal")
                && !tags.getBoolean(key);
    }

    public static boolean isConvenient(ItemStack tool) {
        if (tool == null || !tool.hasTagCompound()) return false;
        return tool.getTagCompound().getCompoundTag("InfiTool").getBoolean("Convenient");
    }
}
