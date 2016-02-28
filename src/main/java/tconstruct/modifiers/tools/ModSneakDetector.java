package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;
import tconstruct.tools.TinkerModification;

import java.util.HashSet;
import java.util.Map;

/**
 * Created by LolHens on 12.06.2015.
 */
public class ModSneakDetector extends ItemModifier {
    private boolean inverted = false;

    public ModSneakDetector(ItemStack[] items, boolean inverted) {
        super(items, -1, "SneakDetector");

        this.inverted = inverted;
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound modTag = TinkerModification.getModifierTag(tool, ModSneakDetector.class);
        return tool.getItem() instanceof ToolCore && (modTag == null || !modTag.hasKey("current"));
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        NBTTagCompound sneakDetector;

        if (tags.hasKey("SneakDetector"))
            sneakDetector = tags.getCompoundTag("SneakDetector");
        else {
            sneakDetector = new NBTTagCompound();
            tags.setTag("SneakDetector", sneakDetector);
        }

        {
            if (!sneakDetector.hasKey("extraModifiers"))
                sneakDetector.setInteger("extraModifiers", TinkerModification.toggleExtraModifiers);

            NBTTagCompound current = new NBTTagCompound();
            {
                NBTTagCompound modifiers = new NBTTagCompound();
                {
                    for (Map.Entry<String, NBTBase> entry : new HashSet<Map.Entry<String, NBTBase>>(TinkerModification.getModifierTags(tool).entrySet())) {
                        if (entry.getKey().equals("SneakDetector")) continue;

                        modifiers.setTag(entry.getKey(), entry.getValue());
                    }
                }
                current.setTag("modifiers", modifiers);

                int modifierCount = tags.getInteger("Modifiers");
                int extraModifierCount = sneakDetector.getInteger("extraModifiers");
                int tmpModifierCount = modifierCount + extraModifierCount;
                tags.setInteger("Modifiers", tmpModifierCount);
                current.setInteger("modifierCount", tmpModifierCount);

                current.setBoolean("inverted", inverted);
            }
            sneakDetector.setTag("current", current);

            if (!sneakDetector.hasKey("on")) sneakDetector.setTag("on", new NBTTagList() {
                {
                    appendTag(new NBTTagString());
                    removeTag(0);
                }
            });
            if (!sneakDetector.hasKey("off")) sneakDetector.setTag("off", new NBTTagList() {
                {
                    appendTag(new NBTTagString());
                    removeTag(0);
                }
            });

            if (!sneakDetector.hasKey("swap")) sneakDetector.setTag("swap", new NBTTagCompound());
        }
    }

    public void addTooltip(ItemStack tool, String modName) {
        addToolTip(tool, EnumChatFormatting.GOLD + (inverted ? "! " : "") + "Switchable: " + modName, key);
    }
}
