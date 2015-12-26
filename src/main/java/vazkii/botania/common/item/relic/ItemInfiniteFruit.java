//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.relic;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.lib.LibObfuscation;

public class ItemInfiniteFruit extends ItemRelic implements IManaUsingItem, IBauble {
    public static IIcon dasBootIcon;

    public ItemInfiniteFruit() {
        super("infiniteFruit");
    }

    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack p_77661_1_) {
        return this.isBoot(p_77661_1_) ? EnumAction.drink : EnumAction.eat;
    }

    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
        if (p_77659_3_.canEat(false) && isRightPlayer(p_77659_3_, p_77659_1_)) {
            p_77659_3_.setItemInUse(p_77659_1_, this.getMaxItemUseDuration(p_77659_1_));
        }

        return p_77659_1_;
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        super.onUsingTick(stack, player, count);
        if (ManaItemHandler.requestManaExact(stack, player, 500, true)) {
            if (count % 5 == 0) {
                player.getFoodStats().addStats(1, 1.0F);
            }

            if (count == 5 && player.canEat(false)) {
                ReflectionHelper.setPrivateValue(EntityPlayer.class, player, Integer.valueOf(20), LibObfuscation.ITEM_IN_USE_COUNT);
            }
        }

    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        super.itemIcon = IconHelper.forItem(par1IconRegister, this);
        dasBootIcon = IconHelper.forName(par1IconRegister, "dasBoot");
    }

    public IIcon getIconIndex(ItemStack par1ItemStack) {
        return this.isBoot(par1ItemStack) ? dasBootIcon : super.getIconIndex(par1ItemStack);
    }

    private boolean isBoot(ItemStack par1ItemStack) {
        String name = par1ItemStack.getDisplayName().toLowerCase().trim();
        return name.equals("das boot");
    }

    public boolean usesMana(ItemStack stack) {
        return true;
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
