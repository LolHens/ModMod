//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumic.tinkerer.common.item.kami;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemFocusPouch;
import thaumcraft.common.items.wands.ItemFocusPouchBauble;
import thaumic.tinkerer.client.core.helper.IconHelper;
import thaumic.tinkerer.common.ThaumicTinkerer;
import thaumic.tinkerer.common.core.handler.ConfigHandler;
import thaumic.tinkerer.common.core.handler.ModCreativeTab;
import thaumic.tinkerer.common.core.proxy.TTCommonProxy;
import thaumic.tinkerer.common.item.kami.ItemKamiResource;
import thaumic.tinkerer.common.registry.ITTinkererItem;
import thaumic.tinkerer.common.registry.ThaumicTinkererInfusionRecipe;
import thaumic.tinkerer.common.registry.ThaumicTinkererRecipe;
import thaumic.tinkerer.common.research.IRegisterableResearch;
import thaumic.tinkerer.common.research.KamiResearchItem;
import thaumic.tinkerer.common.research.ResearchHelper;

public class ItemIchorPouch extends ItemFocusPouchBauble implements ITTinkererItem {
    public ItemIchorPouch() {
        this.setCreativeTab(ModCreativeTab.INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.icon = IconHelper.forItem(par1IconRegister, this);
    }

    public IIcon getIconFromDamage(int par1) {
        return this.icon;
    }

    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return TTCommonProxy.kamiRarity;
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        par3EntityPlayer.openGui(ThaumicTinkerer.instance, 50, par2World, 0, 0, 0);
        return par1ItemStack;
    }

    public ItemStack[] getInventory(ItemStack item) {
        ItemStack[] stackList = new ItemStack[117];
        if (item.hasTagCompound()) {
            NBTTagList var2 = item.stackTagCompound.getTagList("Inventory", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
                NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                int var5 = var4.getByte("Slot") & 255;
                if (var5 >= 0 && var5 < stackList.length) {
                    stackList[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }

        return stackList;
    }

    public ArrayList<Object> getSpecialParameters() {
        return null;
    }

    public String getItemName() {
        return "ichorPouch";
    }

    public boolean shouldRegister() {
        return ConfigHandler.enableKami;
    }

    public boolean shouldDisplayInTab() {
        return true;
    }

    public IRegisterableResearch getResearchItem() {
        return (IRegisterableResearch) (new KamiResearchItem("ICHOR_POUCH", (new AspectList()).add(Aspect.VOID, 2).add(Aspect.CLOTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.MAN, 1), 13, 6, 5, new ItemStack(this))).setParents(new String[]{"ICHOR_CLOTH"}).setPages(new ResearchPage[]{new ResearchPage("0"), ResearchHelper.infusionPage("ICHOR_POUCH")});
    }

    public ThaumicTinkererRecipe getRecipeItem() {
        return new ThaumicTinkererInfusionRecipe("ICHOR_POUCH", new ItemStack(this), 9, (new AspectList()).add(Aspect.VOID, 64).add(Aspect.MAN, 32).add(Aspect.CLOTH, 32).add(Aspect.ELDRITCH, 32).add(Aspect.AIR, 64), new ItemStack(ConfigItems.itemFocusPouch), new ItemStack[]{new ItemStack(ThaumicTinkerer.registry.getFirstItemFromClass(ItemKamiResource.class), 1, 1), new ItemStack(ConfigItems.itemFocusPortableHole), new ItemStack(Items.diamond), new ItemStack(ThaumicTinkerer.registry.getFirstItemFromClass(ItemKamiResource.class), 1, 1), new ItemStack(ConfigBlocks.blockChestHungry), new ItemStack(ConfigBlocks.blockJar, 1, 3)});
    }
}
