package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.items.tools.Scythe;
import tconstruct.library.tools.AOEHarvestTool;
import tconstruct.tools.TinkerModification;

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
        NBTBase.NBTPrimitive nbt = TinkerModification.getModifierTag(tool, ModRange.class);
        return nbt == null ? 0 : nbt.getInt();
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        super.modify(input, tool);

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.hasKey("EnergyReceiveRate"))
            tags.setInteger("EnergyReceiveRate", tags.getInteger("EnergyReceiveRate") * 2);
    }
}
