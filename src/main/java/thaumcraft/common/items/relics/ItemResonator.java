//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumcraft.common.items.relics;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IUntypedEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileTubeBuffer;

import java.util.List;

public class ItemResonator extends Item {
    private IIcon icon;

    public ItemResonator() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setMaxDurability(0);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.icon = par1IconRegister.registerIcon("thaumcraft:resonator");
    }

    public IIcon getIconFromDamage(int par1) {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(this));
    }

    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.uncommon;
    }

    public boolean hasEffect(ItemStack par1ItemStack) {
        return par1ItemStack.hasTagCompound();
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof IEssentiaTransport) {
            if (world.isRemote) {
                player.swingItem();
                return super.onItemUseFirst(itemstack, player, world, x, y, z, side, par8, par9, par10);
            } else {
                IEssentiaTransport et = (IEssentiaTransport) tile;
                ForgeDirection face = ForgeDirection.getOrientation(side);
                MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
                if (hit != null && hit.subHit >= 0 && hit.subHit < 6) {
                    face = ForgeDirection.getOrientation(hit.subHit);
                }

                if (!(tile instanceof TileTubeBuffer) && et.getEssentiaType(face) != null) {
                    player.addChatMessage(new ChatComponentTranslation("tc.resonator1", new Object[]{"" + et.getEssentiaAmount(face), et.getEssentiaType(face).getName()}));
                } else if (tile instanceof TileTubeBuffer && ((IAspectContainer) tile).getAspects().size() > 0) {
                    Aspect[] s = ((IAspectContainer) tile).getAspects().getAspectsSorted();
                    int len$ = s.length;

                    for (int i$ = 0; i$ < len$; ++i$) {
                        Aspect aspect = s[i$];
                        player.addChatMessage(new ChatComponentTranslation("tc.resonator1", new Object[]{"" + ((IAspectContainer) tile).getAspects().getAmount(aspect), aspect.getName()}));
                    }
                }

                String typeString = StatCollector.translateToLocal("tc.resonator3");
                if (et.getSuctionType(face) != null) {
                    typeString = et.getSuctionType(face).getName();
                }

                if (et instanceof IUntypedEssentiaTransport) {
                    IUntypedEssentiaTransport untyped = (IUntypedEssentiaTransport) et;
                    typeString += " ("
                            + untyped.getTypedSuctionAmount(face) + " "
                            + (untyped.getTypedSuctionType(face) == null ? StatCollector.translateToLocal("tc.resonator3") : untyped.getTypedSuctionType(face).getName())
                            + "; "
                            + untyped.getUntypedSuctionAmount(face) + " "
                            + StatCollector.translateToLocal("tc.resonator3")
                            + ")";
                }

                player.addChatMessage(new ChatComponentTranslation("tc.resonator2", new Object[]{"" + et.getSuctionAmount(face), typeString}));
                world.playSoundEffect((double) x, (double) y, (double) z, "thaumcraft:alembicknock", 0.5F, 1.9F + world.rand.nextFloat() * 0.1F);
                return true;
            }
        } else {
            return false;
        }
    }
}
