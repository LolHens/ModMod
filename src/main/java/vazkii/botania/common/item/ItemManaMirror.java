//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.awt.*;

public class ItemManaMirror extends ItemMod implements IManaItem, ICoordBoundItem, IBauble {
    IIcon[] icons;
    private static final String TAG_MANA = "mana";
    private static final String TAG_MANA_BACKLOG = "manaBacklog";
    private static final String TAG_POS_X = "posX";
    private static final String TAG_POS_Y = "posY";
    private static final String TAG_POS_Z = "posZ";
    private static final String TAG_DIM = "dim";
    private static final ItemManaMirror.DummyPool fallbackPool = new ItemManaMirror.DummyPool();

    public ItemManaMirror() {
        this.setMaxStackSize(1);
        this.setMaxDurability(1000);
        this.setUnlocalizedName("manaMirror");
        this.setNoRepair();
    }

    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        float mana = (float) this.getMana(par1ItemStack);
        return par2 == 1 ? Color.HSBtoRGB(0.528F, mana / 1000000.0F, 1.0F) : 16777215;
    }

    public int getDamage(ItemStack stack) {
        float mana = (float) this.getMana(stack);
        return 1000 - (int) (mana / 1000000.0F * 1000.0F);
    }

    public int getDisplayDamage(ItemStack stack) {
        return this.getDamage(stack);
    }

    public void registerIcons(IIconRegister par1IconRegister) {
        this.icons = new IIcon[2];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = IconHelper.forItem(par1IconRegister, this, i);
        }

    }

    public IIcon getIcon(ItemStack stack, int pass) {
        return this.icons[Math.min(1, pass)];
    }

    public boolean isFull3D() {
        return true;
    }

    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
        if (!par2World.isRemote) {
            IManaPool pool = this.getManaPool(par1ItemStack);
            if (!(pool instanceof ItemManaMirror.DummyPool)) {
                if (pool == null) {
                    this.setMana(par1ItemStack, 0);
                } else {
                    pool.recieveMana(this.getManaBacklog(par1ItemStack));
                    this.setManaBacklog(par1ItemStack, 0);
                    this.setMana(par1ItemStack, pool.getCurrentMana());
                }
            }

        }
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if (par2EntityPlayer.isSneaking() && !par3World.isRemote) {
            TileEntity tile = par3World.getTileEntity(par4, par5, par6);
            if (tile != null && tile instanceof IManaPool) {
                this.bindPool(par1ItemStack, tile);
                par3World.playSoundAtEntity(par2EntityPlayer, "botania:ding", 1.0F, 1.0F);
                return true;
            }
        }

        return false;
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public int getMana(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "mana", 0);
    }

    public void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, "mana", Math.max(0, mana));
    }

    public int getManaBacklog(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "manaBacklog", 0);
    }

    public void setManaBacklog(ItemStack stack, int backlog) {
        ItemNBTHelper.setInt(stack, "manaBacklog", backlog);
    }

    public int getMaxMana(ItemStack stack) {
        return 1000000;
    }

    public void addMana(ItemStack stack, int mana) {
        this.setMana(stack, this.getMana(stack) + mana);
        this.setManaBacklog(stack, this.getManaBacklog(stack) + mana);
    }

    public void bindPool(ItemStack stack, TileEntity pool) {
        ItemNBTHelper.setInt(stack, "posX", pool == null ? 0 : pool.xCoord);
        ItemNBTHelper.setInt(stack, "posY", pool == null ? -1 : pool.yCoord);
        ItemNBTHelper.setInt(stack, "posZ", pool == null ? 0 : pool.zCoord);
        ItemNBTHelper.setInt(stack, "dim", pool == null ? 0 : pool.getWorld().provider.dimensionId);
    }

    public ChunkCoordinates getPoolCoords(ItemStack stack) {
        int x = ItemNBTHelper.getInt(stack, "posX", 0);
        int y = ItemNBTHelper.getInt(stack, "posY", -1);
        int z = ItemNBTHelper.getInt(stack, "posZ", 0);
        return new ChunkCoordinates(x, y, z);
    }

    public int getDimension(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "dim", 0);
    }

    public IManaPool getManaPool(ItemStack stack) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return fallbackPool;
        } else {
            ChunkCoordinates coords = this.getPoolCoords(stack);
            if (coords.posY == -1) {
                return null;
            } else {
                int dim = this.getDimension(stack);
                WorldServer world = null;
                WorldServer[] tile = server.worldServers;
                int var7 = tile.length;

                for (int var8 = 0; var8 < var7; ++var8) {
                    WorldServer w = tile[var8];
                    if (w.provider.dimensionId == dim) {
                        world = w;
                        break;
                    }
                }

                if (world != null) {
                    TileEntity var10 = world.getTileEntity(coords.posX, coords.posY, coords.posZ);
                    if (var10 != null && var10 instanceof IManaPool) {
                        return (IManaPool) var10;
                    }
                }

                return null;
            }
        }
    }

    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return false;
    }

    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return false;
    }

    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return false;
    }

    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return true;
    }

    public boolean isNoExport(ItemStack stack) {
        return false;
    }

    public ChunkCoordinates getBinding(ItemStack stack) {
        IManaPool pool = this.getManaPool(stack);
        return pool != null && !(pool instanceof ItemManaMirror.DummyPool) ? this.getPoolCoords(stack) : null;
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

    private static class DummyPool implements IManaPool {
        private DummyPool() {
        }

        public boolean isFull() {
            return false;
        }

        public void recieveMana(int mana) {
        }

        public boolean canRecieveManaFromBursts() {
            return false;
        }

        public int getCurrentMana() {
            return 0;
        }

        public boolean isOutputtingPower() {
            return false;
        }
    }
}
