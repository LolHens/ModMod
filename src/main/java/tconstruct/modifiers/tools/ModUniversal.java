package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.tools.HarvestTool;
import tconstruct.tools.TinkerModification;

/**
 * Created by LolHens on 30.04.2015.
 */
public class ModUniversal extends ModBoolean {
    public ModUniversal(ItemStack[] items) {
        super(items, -1, "Universal", EnumChatFormatting.DARK_BLUE.toString(), "Universal");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tool.getItem() instanceof HarvestTool
                && tags.getInteger("Modifiers") > 0
                && !tags.getBoolean(key);
    }

    public static boolean isUniversal(ItemStack tool) {
        NBTBase.NBTPrimitive nbt = TinkerModification.getModifierTag(tool, ModUniversal.class);
        return nbt != null && nbt.getByte() != 0;
    }
}
