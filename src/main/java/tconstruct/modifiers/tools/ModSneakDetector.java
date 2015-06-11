package tconstruct.modifiers.tools;

import net.minecraft.entity.player.EntityPlayer;
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
public class ModSneakDetector extends ModBoolean {
    public ModSneakDetector(ItemStack[] items) {
        super(items, -1, "Sneak Detector", EnumChatFormatting.GRAY.toString(), "Sneak Detector");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return (tool.getItem() instanceof AOEHarvestTool
                || tool.getItem() instanceof Scythe)
                && tags.getInteger("Modifiers") > 0
                && !tags.getBoolean(key);
    }

    public static boolean isAOE(ItemStack tool, EntityPlayer player) {
        NBTBase.NBTPrimitive nbt = TinkerModification.getModifierTag(tool, ModSneakDetector.class);
        return !(nbt != null && nbt.getByte() != 0 && player.isSneaking());
    }
}
