package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.weaponry.ProjectileWeapon;

/**
 * Created by LolHens on 12.06.2015.
 */
public class ModSneakDetector extends ItemModifier {
    public ModSneakDetector(ItemStack[] items) {
        super(items, -1, "SneakDetector");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tool.getItem() instanceof ToolCore;
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        tags.setTag("SneakDetector", new NBTTagCompound());
    }
}
