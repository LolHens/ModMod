//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumic.tinkerer.common.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;
import thaumic.tinkerer.common.ThaumicTinkerer;
import thaumic.tinkerer.common.block.BlockNitorGas;
import thaumic.tinkerer.common.registry.ItemBase;
import thaumic.tinkerer.common.registry.ThaumicTinkererCrucibleRecipe;
import thaumic.tinkerer.common.registry.ThaumicTinkererRecipe;
import thaumic.tinkerer.common.research.IRegisterableResearch;
import thaumic.tinkerer.common.research.ResearchHelper;
import thaumic.tinkerer.common.research.TTResearchItem;

public class ItemBrightNitor extends ItemBase implements IBauble {
    public static int meta = 0;

    public ItemBrightNitor() {
        this.setMaxStackSize(1);
    }

    public static void setBlock(int x, int y, int z, World world) {
        if ((world.getBlock(x, y, z) == Blocks.air || world.getBlock(x, y, z) == ThaumicTinkerer.registry.getFirstBlockFromClass(BlockNitorGas.class)) && !world.isRemote) {
            world.setBlock(x, y, z, ThaumicTinkerer.registry.getFirstBlockFromClass(BlockNitorGas.class), meta, 2);
        }

    }

    public boolean shouldDisplayInTab() {
        return true;
    }

    public IRegisterableResearch getResearchItem() {
        return (TTResearchItem) (new TTResearchItem("BRIGHT_NITOR", (new AspectList()).add(Aspect.LIGHT, 2).add(Aspect.FIRE, 1).add(Aspect.ENERGY, 1).add(Aspect.AIR, 1), 1, -5, 2, new ItemStack(this), new ResearchPage[0])).setParents(new String[]{"GASEOUS_LIGHT"}).setConcealed().setPages(new ResearchPage[]{new ResearchPage("0"), ResearchHelper.crucibleRecipePage("BRIGHT_NITOR")}).setSecondary();
    }

    public ThaumicTinkererRecipe getRecipeItem() {
        return new ThaumicTinkererCrucibleRecipe("BRIGHT_NITOR", new ItemStack(this), new ItemStack(ConfigItems.itemResource, 1, 1), (new AspectList()).add(Aspect.ENERGY, 25).add(Aspect.LIGHT, 25).add(Aspect.AIR, 10).add(Aspect.FIRE, 10));
    }

    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
        int x = (int) Math.floor(par3Entity.posX);
        int y = (int) par3Entity.posY + 1;
        int z = (int) Math.floor(par3Entity.posZ);
        setBlock(x, y, z, par2World);
    }

    public String getItemName() {
        return "brightNitor";
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.OTHER;
    }

    @Override
    public void onWornTick(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        onUpdate(itemStack, entityLivingBase.worldObj, entityLivingBase, 0, false);
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
