package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.weaponry.ProjectileWeapon;

/**
 * Created by LolHens on 01.05.2015.
 */
public class ModFlightSpeed extends ItemModifier {
    public ModFlightSpeed(ItemStack[] items) {
        super(items, -1, "FlightSpeed");
    }

    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return (tool.getItem() instanceof ProjectileWeapon)
                && tags.getInteger("Modifiers") > 0;
    }

    public void modify(ItemStack[] input, ItemStack tool) {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        tags.setInteger("Modifiers", tags.getInteger("Modifiers") - 1);
        tags.setFloat("FlightSpeed", tags.getFloat("FlightSpeed") + 1);
    }
}
