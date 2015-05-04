//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aspects.IUntypedEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

import java.util.List;
import java.util.Random;

public class TileTube extends TileThaumcraft implements IEssentiaTransport, IUntypedEssentiaTransport, IWandable {
    public ForgeDirection facing;
    public boolean[] openSides;
    Aspect essentiaType;
    int essentiaAmount;
    int untypedSuction;
    int suction;
    Aspect suctionType;
    int venting;
    int count;
    static final int freq = 5;
    int ventColor;

    public TileTube() {
        this.facing = ForgeDirection.NORTH;
        this.openSides = new boolean[]{true, true, true, true, true, true};
        this.essentiaType = null;
        this.essentiaAmount = 0;
        this.untypedSuction = 0;
        this.suction = 0;
        this.suctionType = null;
        this.venting = 0;
        this.count = 0;
        this.ventColor = 0;
    }

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.essentiaType = Aspect.getAspect(nbttagcompound.getString("type"));
        this.essentiaAmount = nbttagcompound.getInteger("amount");
        this.facing = ForgeDirection.getOrientation(nbttagcompound.getInteger("side"));
        byte[] sides = nbttagcompound.getByteArray("open");
        if (sides != null && sides.length == 6) {
            for (int a = 0; a < 6; ++a) {
                this.openSides[a] = sides[a] == 1;
            }
        }

    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        if (this.essentiaType != null) {
            nbttagcompound.setString("type", this.essentiaType.getTag());
        }

        nbttagcompound.setInteger("amount", this.essentiaAmount);
        byte[] sides = new byte[6];

        for (int a = 0; a < 6; ++a) {
            sides[a] = (byte) (this.openSides[a] ? 1 : 0);
        }

        nbttagcompound.setInteger("side", this.facing.ordinal());
        nbttagcompound.setByteArray("open", sides);
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.suctionType = Aspect.getAspect(nbttagcompound.getString("stype"));
        this.suction = nbttagcompound.getInteger("samount");
        this.untypedSuction = nbttagcompound.getInteger("untypedSuction");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (this.suctionType != null) {
            nbttagcompound.setString("stype", this.suctionType.getTag());
        }

        nbttagcompound.setInteger("samount", this.suction);
        nbttagcompound.setInteger("untypedSuction", this.untypedSuction);
    }

    public boolean canUpdate() {
        return true;
    }

    public void updateEntity() {
        if (this.venting > 0) {
            --this.venting;
        }

        if (this.count == 0) {
            this.count = super.worldObj.rand.nextInt(10);
        }

        if (!super.worldObj.isRemote) {
            if (this.venting <= 0) {
                if (++this.count % 2 == 0) {
                    this.calculateSuction((Aspect) null, false, false);
                    this.checkVenting();
                    if (this.essentiaType != null && this.essentiaAmount == 0) {
                        this.essentiaType = null;
                    }
                }

                if (this.count % 5 == 0 && (suction > 0 || untypedSuction > 0)) {
                    this.equalizeWithNeighbours(false);
                }
            }
        } else if (this.venting > 0) {
            Random r = new Random((long) (this.hashCode() * 4));
            float rp = r.nextFloat() * 360.0F;
            float ry = r.nextFloat() * 360.0F;
            double fx = (double) (-MathHelper.sin(ry / 180.0F * 3.1415927F) * MathHelper.cos(rp / 180.0F * 3.1415927F));
            double fz = (double) (MathHelper.cos(ry / 180.0F * 3.1415927F) * MathHelper.cos(rp / 180.0F * 3.1415927F));
            double fy = (double) (-MathHelper.sin(rp / 180.0F * 3.1415927F));
            Thaumcraft.proxy.drawVentParticles(super.worldObj, (double) super.xCoord + 0.5D, (double) super.yCoord + 0.5D, (double) super.zCoord + 0.5D, fx / 5.0D, fy / 5.0D, fx / 5.0D, this.ventColor);
        }

    }

    void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        untypedSuction = 0;
        suction = 0;
        suctionType = null;
        ForgeDirection loc = null;

        for (int dir = 0; dir < 6; ++dir) {
            loc = ForgeDirection.getOrientation(dir);
            if ((!directional || this.facing == loc.getOpposite()) && this.isConnectable(loc)) {
                TileEntity e = ThaumcraftApiHelper.getConnectableTile(super.worldObj, super.xCoord, super.yCoord, super.zCoord, loc);
                if (e != null) {
                    IEssentiaTransport ic = (IEssentiaTransport) e;

                    int myUntypedSuctionAmount = 0;
                    int myTypedSuctionAmount = 0;
                    Aspect myTypedSuctionType = null;

                    int icUntypedSuctionAmount = 0;
                    int icTypedSuctionAmount = 0;
                    Aspect icTypedSuctionType = null;

                    if (ic instanceof IUntypedEssentiaTransport) {
                        icUntypedSuctionAmount = ((IUntypedEssentiaTransport) ic).getUntypedSuctionAmount(loc.getOpposite());
                        icTypedSuctionAmount = ((IUntypedEssentiaTransport) ic).getTypedSuctionAmount(loc.getOpposite());
                        icTypedSuctionType = ((IUntypedEssentiaTransport) ic).getTypedSuctionType(loc.getOpposite());
                    } else if (ic.getSuctionType(loc.getOpposite()) == null) {
                        icUntypedSuctionAmount = ic.getSuctionAmount(loc.getOpposite());
                    } else {
                        icTypedSuctionAmount = ic.getSuctionAmount(loc.getOpposite());
                        icTypedSuctionType = ic.getSuctionType(loc.getOpposite());
                    }

                    myUntypedSuctionAmount = restrict ? icUntypedSuctionAmount / 2 : icUntypedSuctionAmount - 1;
                    myTypedSuctionAmount = restrict ? icTypedSuctionAmount / 2 : icTypedSuctionAmount - 1;
                    myTypedSuctionType = icTypedSuctionType;

                    if (filter != null) {
                        if (myTypedSuctionType != filter) {
                            myTypedSuctionAmount = 0;
                            myTypedSuctionType = null;
                        }
                        myTypedSuctionAmount = Math.max(myUntypedSuctionAmount, myTypedSuctionAmount);
                        myTypedSuctionType = filter;
                        myUntypedSuctionAmount = 0;
                    }

                    if (myUntypedSuctionAmount > untypedSuction) setSuction(null, myUntypedSuctionAmount);
                    if (myTypedSuctionAmount > suction) setSuction(myTypedSuctionType, myTypedSuctionAmount);
                }
            }
        }
    }

    void checkVenting() {
        ForgeDirection loc = null;

        for (int dir = 0; dir < 6; ++dir) {
            loc = ForgeDirection.getOrientation(dir);
            if (this.isConnectable(loc)) {
                TileEntity e = ThaumcraftApiHelper.getConnectableTile(super.worldObj, super.xCoord, super.yCoord, super.zCoord, loc);
                if (e != null) {
                    IEssentiaTransport ic = (IEssentiaTransport) e;
                    int suck = ic.getSuctionAmount(loc.getOpposite());
                    if (suction > 0 && (suck == suction || suck == suction - 1) && suctionType != ic.getSuctionType(loc.getOpposite())) {
                        int c = -1;
                        if (this.suctionType != null) {
                            c = Config.aspectOrder.indexOf(this.suctionType);
                        }

                        super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, ConfigBlocks.blockTube, 1, c);
                        this.venting = 40;
                    }
                }
            }
        }

    }

    void equalizeWithNeighbours(boolean directional) {
        if (essentiaAmount > 0) return;

        for (int dir = 0; dir < 6; ++dir) {
            ForgeDirection loc = ForgeDirection.getOrientation(dir);

            if (!isConnectable(loc)) continue;
            if (directional && facing == loc.getOpposite()) continue;

            IEssentiaTransport ic = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(super.worldObj, super.xCoord, super.yCoord, super.zCoord, loc);

            if (ic == null) continue;

            if (!ic.canOutputTo(loc.getOpposite())) continue;

            int myUntypedSuctionAmount = untypedSuction;
            int myTypedSuctionAmount = suction;
            Aspect myTypedSuctionType = suctionType;

            int icEssentiaAmount = ic.getEssentiaAmount(loc.getOpposite());
            Aspect icEssentiaType = null;
            if (icEssentiaType == null) icEssentiaType = ic.getEssentiaType(ForgeDirection.UNKNOWN);

            if (icEssentiaAmount <= 0 || icEssentiaType == null) continue;

            if (myTypedSuctionAmount > 0 && ic instanceof IAspectContainer) {
                IAspectContainer aspectContainer = (IAspectContainer) ic;
                int newEssentiaAmount = aspectContainer.containerContains(myTypedSuctionType);
                if (newEssentiaAmount > 0) {
                    icEssentiaAmount = newEssentiaAmount;
                    icEssentiaType = myTypedSuctionType;
                }
            }

            int icSuctionAmount = ic.getSuctionAmount(loc.getOpposite());

            if (ic instanceof IUntypedEssentiaTransport) {
                if (ic.getSuctionType(loc.getOpposite()) == icEssentiaType) {
                    icSuctionAmount = ((IUntypedEssentiaTransport) ic).getTypedSuctionAmount(loc.getOpposite());
                } else {
                    icSuctionAmount = ((IUntypedEssentiaTransport) ic).getUntypedSuctionAmount(loc.getOpposite());
                }
            }

            if ((myTypedSuctionAmount > 0
                    && myTypedSuctionType == icEssentiaType
                    && myTypedSuctionAmount > icSuctionAmount
                    && myTypedSuctionAmount >= ic.getMinimumSuction())
                    || (myUntypedSuctionAmount > 0
                    && myUntypedSuctionAmount > icSuctionAmount
                    && myUntypedSuctionAmount >= ic.getMinimumSuction())) {
                if (addEssentia(icEssentiaType, ic.takeEssentia(icEssentiaType, 1, loc.getOpposite()), loc) > 0) {
                    if (worldObj.rand.nextInt(100) == 0)
                        worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, ConfigBlocks.blockTube, 0, 0);
                    return;
                }
            }
        }

    }

    public boolean isConnectable(ForgeDirection face) {
        return face == ForgeDirection.UNKNOWN ? false : this.openSides[face.ordinal()];
    }

    public boolean canInputFrom(ForgeDirection face) {
        return face == ForgeDirection.UNKNOWN ? false : this.openSides[face.ordinal()];
    }

    public boolean canOutputTo(ForgeDirection face) {
        return face == ForgeDirection.UNKNOWN ? false : this.openSides[face.ordinal()];
    }

    public void setSuction(Aspect aspect, int amount) {
        if (aspect == null) {
            untypedSuction = amount;
        } else {
            suction = amount;
            suctionType = aspect;
        }
    }

    private IEssentiaTransport getEssentiaMachine(ForgeDirection loc) {
        if (loc != null && loc != ForgeDirection.UNKNOWN) {
            IEssentiaTransport ic = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(super.worldObj, super.xCoord, super.yCoord, super.zCoord, loc);

            if (ic != null && !(ic instanceof IUntypedEssentiaTransport)) return ic;
        }
        return null;
    }

    public int getSuctionAmount(ForgeDirection loc) {
        int untypedSuctionAmount = getUntypedSuctionAmount(loc);
        int typedSuctionAmount = getTypedSuctionAmount(loc);

        IEssentiaTransport essentiaMachine = getEssentiaMachine(loc);
        if (essentiaMachine != null && essentiaMachine.getEssentiaType(loc.getOpposite()) != suctionType)
            return untypedSuction > 0 ? untypedSuctionAmount : typedSuctionAmount;
        return untypedSuction > suction ? untypedSuctionAmount : typedSuctionAmount;
    }

    public Aspect getSuctionType(ForgeDirection loc) {
        Aspect typedSuctionType = getTypedSuctionType(loc);

        IEssentiaTransport essentiaMachine = getEssentiaMachine(loc);
        if (essentiaMachine != null && essentiaMachine.getEssentiaType(loc.getOpposite()) != suctionType)
            return untypedSuction > 0 ? null : typedSuctionType;
        return untypedSuction > suction ? null : typedSuctionType;
    }

    public int getEssentiaAmount(ForgeDirection loc) {
        IEssentiaTransport essentiaMachine = getEssentiaMachine(loc);
        if (essentiaMachine != null) {
            if (suctionType == null || essentiaMachine.getSuctionType(loc.getOpposite()) != suctionType) {
                int essentiaAmount = getUntypedEssentiaAmount(loc);
                if (essentiaAmount <= 0 && getUntypedSuctionAmount(loc) >= getTypedSuctionAmount(loc))
                    essentiaAmount = getTypedEssentiaAmount(loc);
                return essentiaAmount;
            } else {
                return getTypedEssentiaAmount(loc);
            }
        }

        return essentiaAmount;
    }

    public Aspect getEssentiaType(ForgeDirection loc) {
        IEssentiaTransport essentiaMachine = getEssentiaMachine(loc);
        if (essentiaMachine != null) {
            if (suctionType == null || essentiaMachine.getSuctionType(loc.getOpposite()) != suctionType) {
                int essentiaAmount = getUntypedEssentiaAmount(loc);
                if (essentiaAmount <= 0 && getUntypedSuctionAmount(loc) >= getTypedSuctionAmount(loc))
                    return getTypedEssentiaType(loc);
                return getUntypedEssentiaType(loc);
            } else {
                return getTypedEssentiaType(loc);
            }
        }

        return essentiaType;
    }


    public int getUntypedSuctionAmount(ForgeDirection loc) {
        return untypedSuction;
    }

    public int getTypedSuctionAmount(ForgeDirection loc) {
        return suction;
    }

    public Aspect getTypedSuctionType(ForgeDirection loc) {
        return suctionType;
    }

    public int getUntypedEssentiaAmount(ForgeDirection loc) {
        return essentiaType != suctionType ? essentiaAmount : 0;
    }

    public Aspect getUntypedEssentiaType(ForgeDirection loc) {
        return essentiaType != suctionType ? essentiaType : null;
    }

    public int getTypedEssentiaAmount(ForgeDirection loc) {
        return essentiaType == suctionType ? essentiaAmount : 0;
    }

    public Aspect getTypedEssentiaType(ForgeDirection loc) {
        return essentiaType == suctionType ? essentiaType : null;
    }


    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        if (this.canOutputTo(face) && this.essentiaType == aspect && this.essentiaAmount > 0 && amount > 0) {
            --this.essentiaAmount;
            if (this.essentiaAmount <= 0) {
                this.essentiaType = null;
            }

            this.markDirty();
            return 1;
        } else {
            return 0;
        }
    }

    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        if (this.canInputFrom(face) && this.essentiaAmount == 0 && amount > 0) {
            this.essentiaType = aspect;
            ++this.essentiaAmount;
            this.markDirty();
            return 1;
        } else {
            return 0;
        }
    }

    public int getMinimumSuction() {
        return 0;
    }

    public boolean renderExtendedTube() {
        return false;
    }

    public boolean receiveClientEvent(int i, int j) {
        if (i == 0) {
            if (super.worldObj.isRemote) {
                super.worldObj.playSound((double) super.xCoord + 0.5D, (double) super.yCoord + 0.5D, (double) super.zCoord + 0.5D, "thaumcraft:creak", 1.0F, 1.3F + super.worldObj.rand.nextFloat() * 0.2F, false);
            }

            return true;
        } else if (i != 1) {
            return super.receiveClientEvent(i, j);
        } else {
            if (super.worldObj.isRemote) {
                if (this.venting <= 0) {
                    super.worldObj.playSound((double) super.xCoord + 0.5D, (double) super.yCoord + 0.5D, (double) super.zCoord + 0.5D, "random.fizz", 0.1F, 1.0F + super.worldObj.rand.nextFloat() * 0.1F, false);
                }

                this.venting = 50;
                if (j != -1 && j < Config.aspectOrder.size()) {
                    this.ventColor = ((Aspect) Config.aspectOrder.get(j)).getColor();
                } else {
                    this.ventColor = 11184810;
                }
            }

            return true;
        }
    }

    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
        if (hit == null) {
            return 0;
        } else {
            if (hit.subHit >= 0 && hit.subHit < 6) {
                player.worldObj.playSound((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "thaumcraft:tool", 0.5F, 0.9F + player.worldObj.rand.nextFloat() * 0.2F, false);
                player.swingItem();
                this.markDirty();
                world.markBlockForUpdate(x, y, z);
                this.openSides[hit.subHit] = !this.openSides[hit.subHit];
                ForgeDirection a = ForgeDirection.getOrientation(hit.subHit);
                TileEntity tile = super.worldObj.getTileEntity(super.xCoord + a.offsetX, super.yCoord + a.offsetY, super.zCoord + a.offsetZ);
                if (tile != null && tile instanceof TileTube) {
                    ((TileTube) tile).openSides[a.getOpposite().ordinal()] = this.openSides[hit.subHit];
                    world.markBlockForUpdate(super.xCoord + a.offsetX, super.yCoord + a.offsetY, super.zCoord + a.offsetZ);
                    tile.markDirty();
                }
            }

            if (hit.subHit == 6) {
                player.worldObj.playSound((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "thaumcraft:tool", 0.5F, 0.9F + player.worldObj.rand.nextFloat() * 0.2F, false);
                player.swingItem();
                int var12 = this.facing.ordinal();
                this.markDirty();

                while (true) {
                    ++var12;
                    if (var12 >= 20) {
                        break;
                    }

                    if (this.canConnectSide(ForgeDirection.getOrientation(var12 % 6).getOpposite().ordinal()) && this.isConnectable(ForgeDirection.getOrientation(var12 % 6).getOpposite())) {
                        var12 %= 6;
                        this.facing = ForgeDirection.getOrientation(var12);
                        world.markBlockForUpdate(x, y, z);
                        break;
                    }
                }
            }

            return 0;
        }
    }

    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    public MovingObjectPosition rayTrace(World world, Vec3 vec3d, Vec3 vec3d1, MovingObjectPosition fullblock) {
        return fullblock;
    }

    protected boolean canConnectSide(int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        TileEntity tile = super.worldObj.getTileEntity(super.xCoord + dir.offsetX, super.yCoord + dir.offsetY, super.zCoord + dir.offsetZ);
        return tile != null && tile instanceof IEssentiaTransport;
    }

    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        float min = 0.42F;
        float max = 0.58F;
        if (this.canConnectSide(0)) {
            cuboids.add(new IndexedCuboid6(Integer.valueOf(0), new Cuboid6((double) ((float) super.xCoord + min), (double) super.yCoord, (double) ((float) super.zCoord + min), (double) ((float) super.xCoord + max), (double) super.yCoord + 0.5D, (double) ((float) super.zCoord + max))));
        }

        if (this.canConnectSide(1)) {
            cuboids.add(new IndexedCuboid6(Integer.valueOf(1), new Cuboid6((double) ((float) super.xCoord + min), (double) super.yCoord + 0.5D, (double) ((float) super.zCoord + min), (double) ((float) super.xCoord + max), (double) (super.yCoord + 1), (double) ((float) super.zCoord + max))));
        }

        if (this.canConnectSide(2)) {
            cuboids.add(new IndexedCuboid6(Integer.valueOf(2), new Cuboid6((double) ((float) super.xCoord + min), (double) ((float) super.yCoord + min), (double) super.zCoord, (double) ((float) super.xCoord + max), (double) ((float) super.yCoord + max), (double) super.zCoord + 0.5D)));
        }

        if (this.canConnectSide(3)) {
            cuboids.add(new IndexedCuboid6(Integer.valueOf(3), new Cuboid6((double) ((float) super.xCoord + min), (double) ((float) super.yCoord + min), (double) super.zCoord + 0.5D, (double) ((float) super.xCoord + max), (double) ((float) super.yCoord + max), (double) (super.zCoord + 1))));
        }

        if (this.canConnectSide(4)) {
            cuboids.add(new IndexedCuboid6(Integer.valueOf(4), new Cuboid6((double) super.xCoord, (double) ((float) super.yCoord + min), (double) ((float) super.zCoord + min), (double) super.xCoord + 0.5D, (double) ((float) super.yCoord + max), (double) ((float) super.zCoord + max))));
        }

        if (this.canConnectSide(5)) {
            cuboids.add(new IndexedCuboid6(Integer.valueOf(5), new Cuboid6((double) super.xCoord + 0.5D, (double) ((float) super.yCoord + min), (double) ((float) super.zCoord + min), (double) (super.xCoord + 1), (double) ((float) super.yCoord + max), (double) ((float) super.zCoord + max))));
        }

        cuboids.add(new IndexedCuboid6(Integer.valueOf(6), new Cuboid6((double) super.xCoord + 0.34375D, (double) super.yCoord + 0.34375D, (double) super.zCoord + 0.34375D, (double) super.xCoord + 0.65625D, (double) super.yCoord + 0.65625D, (double) super.zCoord + 0.65625D)));
    }
}
