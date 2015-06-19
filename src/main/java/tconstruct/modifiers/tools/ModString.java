package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.tools.TinkerModification;

/**
 * Created by LolHens on 19.06.2015.
 */
public class ModString extends ItemModifier {
    String color;
    String tooltipName;
    String value;

    public ModString(ItemStack[] items, String value, int effect, String tag, String c, String tip) {
        super(items, effect, tag);
        this.value = value;
        this.color = c;
        this.tooltipName = tip;
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tags.getInteger("Modifiers") > 0 && !tags.hasKey(super.key, new NBTTagString().getId());
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setString(super.key, value);
        int modifiers = tags.getInteger("Modifiers");
        --modifiers;
        tags.setInteger("Modifiers", modifiers);
        this.addToolTip(tool, this.color + this.tooltipName, this.color + super.key);
    }

    public static String getValue(ItemStack stack, Class<? extends ModString> modifier) {
        NBTBase nbtBase = TinkerModification.getModifierTag(stack, modifier);

        if (nbtBase == null || !(nbtBase instanceof NBTTagString)) return null;

        return ((NBTTagString) nbtBase).getString();
    }
}
