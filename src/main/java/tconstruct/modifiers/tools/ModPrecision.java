package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.tools.HarvestTool;

/**
 * Created by LolHens on 30.04.2015.
 */
public class ModPrecision extends ModBoolean {
    private float precision;

    public ModPrecision(ItemStack[] items, float precision) { // TODO
        super(items, -1, "Precision", EnumChatFormatting.DARK_PURPLE.toString(), "Precision");

        this.precision = precision;
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tool.getItem() instanceof HarvestTool
                && tags.getInteger("Modifiers") > 0
                && !tags.getBoolean(key);
    }

    public static float getPrecision(ItemStack tool) {
        if (tool == null || !tool.hasTagCompound()) return -1;

        NBTTagCompound infiTool = tool.getTagCompound().getCompoundTag("InfiTool");
        if (infiTool.hasKey("Precision", 1) && infiTool.getBoolean("Precision"))
            return 0.9f;
        else if (infiTool.hasKey("Precision", 5))
            return infiTool.getFloat("Precision");
        else
            return -1;
    }
}
