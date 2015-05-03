package tconstruct.modifiers.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.items.tools.Scythe;
import tconstruct.library.tools.AOEHarvestTool;

/**
 * Created by LolHens on 30.04.2015.
 */
public class ModRange extends ModInteger {

    public ModRange(ItemStack[] items) {
        super(items, -1, "Range", 1, EnumChatFormatting.GOLD.toString(), "Range");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return (tool.getItem() instanceof AOEHarvestTool
                || tool.getItem() instanceof Scythe)
                && tags.getInteger("Modifiers") > 0;
    }

    public static int getRangeIncrease(ItemStack tool) {
        if (tool == null || !tool.hasTagCompound()) return 0;
        return tool.getTagCompound().getCompoundTag("InfiTool").getInteger("Range");
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        super.modify(input, tool);

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.hasKey("EnergyReceiveRate"))
            tags.setInteger("EnergyReceiveRate", tags.getInteger("EnergyReceiveRate") * 2);
    }
}