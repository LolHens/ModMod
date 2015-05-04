package baubles.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Created by LolHens on 04.05.2015.
 */
public interface IBaubleContainer {
    ItemStack[] getBaubles(ItemStack container, EntityLivingBase entity);

    boolean canReplaceBauble(ItemStack container, EntityLivingBase entity, int num, ItemStack newStack);

    void replaceBauble(ItemStack container, EntityLivingBase entity, int num, ItemStack newStack);
}
