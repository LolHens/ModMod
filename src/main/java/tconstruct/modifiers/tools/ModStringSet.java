package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.tools.TinkerModification;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LolHens on 19.06.2015.
 */
public class ModStringSet extends ItemModifier {
    String color;
    String tooltipName;
    String value;

    public ModStringSet(ItemStack[] items, String value, int effect, String tag, String c, String tip) {
        super(items, effect, tag);
        this.value = value;
        this.color = c;
        this.tooltipName = tip;
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        return (tags.getInteger("Modifiers") > 0 && !tags.hasKey(key, new NBTTagList().getId()))
                || !getValues(tool, (Class<? extends ModStringSet>) TinkerModification.getModifierClass(key)).contains(value);
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (!tags.hasKey(key)) {
            int modifiers = tags.getInteger("Modifiers");
            --modifiers;
            tags.setInteger("Modifiers", modifiers);

            tags.setTag(key, new NBTTagList() {
                {
                    appendTag(new NBTTagString());
                    removeTag(0);
                }
            });

            addToolTip(tool, this.color + this.tooltipName, this.color + super.key);
        }

        NBTTagList tagList = tags.getTagList(super.key, new NBTTagString().getId());

        tagList.appendTag(new NBTTagString(value));
    }

    public static Set<String> getValues(ItemStack stack, Class<? extends ModStringSet> modifier) {
        NBTBase nbtBase = TinkerModification.getModifierTag(stack, modifier);

        if (nbtBase == null || !(nbtBase instanceof NBTTagList)) return null;

        Set<String> values = new HashSet<String>();

        for (int i = 0; i < ((NBTTagList) nbtBase).tagCount(); i++)
            values.add(((NBTTagList) nbtBase).getStringTagAt(i));

        return values;
    }
}
