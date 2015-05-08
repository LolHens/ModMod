//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.equipment.tool;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.recipe.TerraPickTippingRecipe;
import vazkii.botania.common.item.ItemSpark;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemAuraRing;
import vazkii.botania.common.item.equipment.bauble.ItemGreaterAuraRing;
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelPick;
import vazkii.botania.common.item.relic.ItemLokiRing;
import vazkii.botania.common.item.relic.ItemThorRing;

import java.awt.*;
import java.util.List;

public class ItemTerraPick extends ItemManasteelPick implements IManaItem, ISequentialBreaker {
    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_MANA = "mana";
    private static final String TAG_TIPPED = "tipped";
    private static final int MAX_MANA = 2147483647;
    private static final Material[] MATERIALS;
    private static final int[] LEVELS;
    private static final int[] CREATIVE_MANA;
    IIcon iconTool;
    IIcon iconOverlay;
    IIcon iconTipped;

    public ItemTerraPick() {
        super(BotaniaAPI.terrasteelToolMaterial, "terraPick");
        GameRegistry.addRecipe(new TerraPickTippingRecipe());
        RecipeSorter.register("botania:terraPickTipping", TerraPickTippingRecipe.class, Category.SHAPELESS, "");
    }

    public void getSubItems(Item item, CreativeTabs tab, List list) {
        int[] var4 = CREATIVE_MANA;
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            int mana = var4[var6];
            ItemStack stack = new ItemStack(item);
            setMana(stack, mana);
            list.add(stack);
        }

    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        String rankFormat = StatCollector.translateToLocal("botaniamisc.toolRank");
        String rank = StatCollector.translateToLocal("botania.rank" + getLevel(par1ItemStack));
        par3List.add(String.format(rankFormat, new Object[]{rank}).replaceAll("&", "\u00a7"));
        if (this.getMana(par1ItemStack) == 2147483647) {
            par3List.add(EnumChatFormatting.RED + StatCollector.translateToLocal("botaniamisc.getALife"));
        }

    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (!par3EntityPlayer.isSneaking()) {
            this.getMana(par1ItemStack);
            int level = getLevel(par1ItemStack);
            if (level != 0) {
                this.setEnabled(par1ItemStack, !this.isEnabled(par1ItemStack));
                if (!par2World.isRemote) {
                    par2World.playSoundAtEntity(par3EntityPlayer, "botania:terraPickMode", 0.5F, 0.4F);
                }
            }
        }
        return par1ItemStack;
    }

    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
        super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
        if (this.isEnabled(par1ItemStack)) {
            int level = getLevel(par1ItemStack);
            if (level == 0) {
                this.setEnabled(par1ItemStack, false);
            } else if (par3Entity instanceof EntityPlayer && !((EntityPlayer) par3Entity).isSwingInProgress) {
                this.addMana(par1ItemStack, -level);
            }
        }

    }

    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        MovingObjectPosition raycast = ToolCommons.raytraceFromEntity(player.worldObj, player, true, 10.0D);
        if (raycast != null) {
            this.breakOtherBlock(player, stack, x, y, z, x, y, z, raycast.sideHit);
            ItemLokiRing.breakOnAllCursors(player, this, stack, x, y, z, raycast.sideHit);
        }

        return false;
    }

    public void breakOtherBlock(EntityPlayer player, ItemStack stack, int x, int y, int z, int originX, int originY, int originZ, int side) {
        if (this.isEnabled(stack)) {
            World world = player.worldObj;
            Material mat = world.getBlock(x, y, z).getMaterial();
            if (ToolCommons.isRightMaterial(mat, MATERIALS)) {
                if (!world.isAirBlock(x, y, z)) {
                    ForgeDirection direction = ForgeDirection.getOrientation(side);
                    int fortune = EnchantmentHelper.getFortuneModifier(player);
                    boolean silk = EnchantmentHelper.getSilkTouchModifier(player);
                    boolean thor = ItemThorRing.getThorRing(player) != null;
                    boolean doX = thor || direction.offsetX == 0;
                    boolean doY = thor || direction.offsetY == 0;
                    boolean doZ = thor || direction.offsetZ == 0;
                    int level = getLevel(stack);
                    int range = Math.max(0, level - 1);
                    int rangeY = Math.max(1, range);
                    if (range != 0 || level == 1) {
                        ToolCommons.removeBlocksInIteration(player, stack, world, x, y, z, doX ? -range : 0, doY ? -1 : 0, doZ ? -range : 0, doX ? range + 1 : 1, doY ? rangeY * 2 : 1, doZ ? range + 1 : 1, (Block) null, MATERIALS, silk, fortune, isTipped(stack));
                        if (level == 5) {
                            player.addStat(ModAchievements.rankSSPick, 1);
                        }

                    }
                }
            }
        }
    }

    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 2147483647;
    }

    public void registerIcons(IIconRegister par1IconRegister) {
        this.iconTool = IconHelper.forItem(par1IconRegister, this, 0);
        this.iconOverlay = IconHelper.forItem(par1IconRegister, this, 1);
        this.iconTipped = IconHelper.forItem(par1IconRegister, this, 2);
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 1 && this.isEnabled(stack) ? this.iconOverlay : (isTipped(stack) ? this.iconTipped : this.iconTool);
    }

    public static boolean isTipped(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "tipped", false);
    }

    public static void setTipped(ItemStack stack) {
        ItemNBTHelper.setBoolean(stack, "tipped", true);
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        return par2 != 0 && this.isEnabled(par1ItemStack) ? Color.HSBtoRGB(0.375F, (float) Math.min(1.0D, Math.sin((double) System.currentTimeMillis() / 200.0D) * 0.5D + 1.0D), 1.0F) : 16777215;
    }

    boolean isEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "enabled", false);
    }

    void setEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, "enabled", enabled);
    }

    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, "mana", mana);
    }

    public int getMana(ItemStack stack) {
        return getMana_(stack);
    }

    public static int getMana_(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "mana", 0);
    }

    public static int getLevel(ItemStack stack) {
        int mana = getMana_(stack);

        for (int i = LEVELS.length - 1; i > 0; --i) {
            if (mana >= LEVELS[i]) {
                return i;
            }
        }

        return 0;
    }

    public int getMaxMana(ItemStack stack) {
        return 2147483647;
    }

    public void addMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(this.getMana(stack) + mana, 2147483647));
    }

    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return true;
    }

    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return !(otherStack.getItem() instanceof ItemSpark) && !(otherStack.getItem() instanceof ItemAuraRing) && !(otherStack.getItem() instanceof ItemGreaterAuraRing);
    }

    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return false;
    }

    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return false;
    }

    public boolean isNoExport(ItemStack stack) {
        return true;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return par2ItemStack.getItem() == ModItems.manaResource && par2ItemStack.getMetadata() == 4 ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    public boolean disposeOfTrashBlocks(ItemStack stack) {
        return isTipped(stack);
    }

    static {
        MATERIALS = new Material[]{Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil, Material.grass, Material.ground, Material.sand, Material.snow, Material.craftedSnow, Material.clay};
        LEVELS = new int[]{0, 10000, 1000000, 10000000, 100000000, 1000000000};
        CREATIVE_MANA = new int[]{9999, 999999, 9999999, 99999999, 999999999, 2147483646};
    }
}
