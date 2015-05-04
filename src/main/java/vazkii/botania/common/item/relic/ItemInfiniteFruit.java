//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.relic;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.common.lib.LibObfuscation;

public class ItemInfiniteFruit extends ItemRelic implements IBauble {
    public ItemInfiniteFruit() {
        super("infiniteFruit");
    }

    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack p_77661_1_) {
        return EnumAction.eat;
    }

    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
        if (p_77659_3_.canEat(false) && isRightPlayer(p_77659_3_, p_77659_1_)) {
            p_77659_3_.setItemInUse(p_77659_1_, this.getMaxItemUseDuration(p_77659_1_));
        }

        return p_77659_1_;
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        super.onUsingTick(stack, player, count);
        if (count % 5 == 0) {
            player.getFoodStats().addStats(1, 0.1F);
        }

        if (count == 5 && player.canEat(false)) {
            ReflectionHelper.setPrivateValue(EntityPlayer.class, player, Integer.valueOf(20), LibObfuscation.ITEM_IN_USE_COUNT);
        }

    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.OTHER;
    }

    @Override
    public void onWornTick(ItemStack itemStack, EntityLivingBase entityLivingBase) {
    }

    @Override
    public void onEquipped(ItemStack itemStack, EntityLivingBase entityLivingBase) {
    }

    @Override
    public void onUnequipped(ItemStack itemStack, EntityLivingBase entityLivingBase) {
    }

    @Override
    public boolean canEquip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }
}
