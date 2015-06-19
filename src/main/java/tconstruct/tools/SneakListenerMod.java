package tconstruct.tools;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModSneakDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LolHens on 18.06.2015.
 */
public class SneakListenerMod extends ActiveToolMod {
    @Override
    public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
        NBTTagCompound modTag = TinkerModification.getModifierTag(stack, ModSneakDetector.class);
        if (modTag == null) return;

        if (checkModifiersChanged(stack, modTag)) disableSneakModListener(modTag);

        swap(stack.getTagCompound().getCompoundTag("InfiTool"), modTag, entity.isSneaking());
    }

    private boolean checkModifiersChanged(ItemStack stack, NBTTagCompound modTag) {
        if (!modTag.hasKey("current", new NBTTagCompound().getId())) return false;
        NBTTagCompound current = modTag.getCompoundTag("current");

        boolean inverted = current.getBoolean("inverted");

        NBTTagCompound modifiers = current.getCompoundTag("modifiers");

        boolean foundModifier = false;

        for (Map.Entry<String, NBTBase> entry : TinkerModification.getModifierTags(stack).entrySet()) {
            if (entry.getKey().equals("SneakDetector")) continue;

            if (!modifiers.hasKey(entry.getKey(), entry.getValue().getId())
                    || !modifiers.getTag(entry.getKey()).equals(entry.getValue())) {
                foundModifier = true;
                applyToSneakListener(entry.getKey(), modTag, inverted);
            }
        }

        return foundModifier;
    }

    private void applyToSneakListener(String modifierKey, NBTTagCompound modTag, boolean inverted) {
        NBTTagList toggleList = modTag.getTagList(inverted ? "off" : "on", new NBTTagString().getId());

        toggleList.appendTag(new NBTTagString(modifierKey));
    }

    private void disableSneakModListener(NBTTagCompound modTag) {
        modTag.removeTag("current");
    }

    private void swap(NBTTagCompound tagCompound, NBTTagCompound modTag, boolean toggle) {
        NBTTagList toggleTagList = modTag.getTagList(toggle ? "off" : "on", new NBTTagString().getId());

        List<String> toggleList = new ArrayList<String>();
        for (int i = 0; i < toggleTagList.tagCount(); i++)
            toggleList.add(toggleTagList.getStringTagAt(i));

        NBTTagCompound swap = modTag.getCompoundTag("swap");

        for (String key : (Set<String>) swap.getKeySet())
            if (!toggleList.contains(key)) {
                tagCompound.setTag(key, swap.getTag(key));
                swap.removeTag(key);
            }

        for (String key : toggleList)
            if (tagCompound.hasKey(key)) {
                swap.setTag(key, tagCompound.getTag(key));
                tagCompound.removeTag(key);
            }
    }
}
