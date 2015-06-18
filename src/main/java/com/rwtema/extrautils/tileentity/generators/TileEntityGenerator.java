//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rwtema.extrautils.tileentity.generators;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import com.rwtema.extrautils.sounds.ISoundTile;
import com.rwtema.extrautils.sounds.Sounds;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.Random;

public abstract class TileEntityGenerator extends TileEntity implements IEnergyHandler, ISoundTile {
    public static final int capacity = 100000;
    public EnergyStorage storage = new EnergyStorage(100000);
    public byte rotation = 0;
    public double coolDown = 0.0D;
    int c = -1;
    public boolean initPower = true;
    public boolean playSound = false;
    private int multiplier = -1;
    private double divisor = -1.0D;
    public static ResourceLocation hum = new ResourceLocation("extrautils", "ambient.hum");
    private boolean shouldInit = true;
    private int connectionMask = 0;

    public int extraMultiplier = 1;

    public TileEntityGenerator() {
    }

    public EnergyStorage getStorage() {
        if (this.initPower && super.worldObj != null && !super.worldObj.isRemote && super.worldObj.blockExists(this.x(), this.y(), this.z())) {
            this.initPower = false;
            this.storage.setCapacity(100000 * this.getMultiplier());
        }

        return this.storage;
    }

    public int getMultiplier() {
        if (this.multiplier == -1) {
            Block b = this.getBlockType();
            if (b instanceof BlockGenerator) {
                this.multiplier = ((BlockGenerator) b).numGenerators;
            } else {
                this.multiplier = 1;
            }
        }

        return this.multiplier * extraMultiplier;
    }

    public void invalidate() {
        super.invalidate();
    }

    public double getDivisor() {
        if (this.divisor == -1.0D) {
            this.divisor = 1.0D / (double) this.getMultiplier();
        }

        return this.divisor;
    }

    public static int getFurnaceBurnTime(ItemStack item) {
        return item == null ? 0 : (item.getItem() == Items.lava_bucket ? 0 : TileEntityFurnace.getItemBurnTime(item));
    }

    public TileEntity getTile() {
        return this;
    }

    public ResourceLocation getSound() {
        return hum;
    }

    public boolean shouldSoundPlay() {
        return this.playSound;
    }

    public static String getCoolDownString(double time) {
        String s = String.format("%.1f", new Object[]{Double.valueOf(time % 60.0D)}) + "s";
        int t = (int) time / 60;
        if (t == 0) {
            return s;
        } else {
            s = t % 60 + "m " + s;
            t /= 60;
            if (t == 0) {
                return s;
            } else {
                s = t % 24 + "h " + s;
                t /= 24;
                if (t == 0) {
                    return s;
                } else {
                    s = t + "d " + s;
                    return s;
                }
            }
        }
    }

    public int x() {
        return super.xCoord;
    }

    public int y() {
        return super.yCoord;
    }

    public int z() {
        return super.zCoord;
    }

    public boolean isPowered() {
        return super.worldObj.isBlockIndirectlyGettingPowered(this.x(), this.y(), this.z());
    }

    public String getBlurb(double coolDown, double energy) {
        return coolDown == 0.0D ? "" : "PowerLevel:\n" + String.format("%.1f", new Object[]{Double.valueOf(energy)}) + "\nTime Remaining:\n" + getCoolDownString(coolDown);
    }

    public double stepCoolDown() {
        return 1.0D;
    }

    public int getCompLevel() {
        if (this.c == -1) {
            this.c = this.getStorage().getEnergyStored() * 15 / this.getStorage().getMaxEnergyStored();
        }

        return this.c;
    }

    public void checkCompLevel() {
        if (this.getCompLevel() != this.getStorage().getEnergyStored() * 15 / this.getStorage().getMaxEnergyStored()) {
            this.c = this.getStorage().getEnergyStored() * 15 / this.getStorage().getMaxEnergyStored();
            super.worldObj.notifyBlocksOfNeighborChange(this.x(), this.y(), this.z(), this.getBlockType());
        }

    }

    public abstract boolean shouldProcess();

    public void updateEntity() {
        if (super.worldObj.isRemote) {
            if (this.shouldInit) {
                this.shouldInit = false;
                Sounds.addGenerator(this);
            }

        } else {
            if (this.coolDown > 0.0D) {
                if (this.coolDown > 1.0D) {
                    this.getStorage().receiveEnergy((int) Math.floor(this.genLevel() * (double) this.getMultiplier()), false);
                    this.coolDown -= this.stepCoolDown();
                } else {
                    this.getStorage().receiveEnergy((int) Math.floor(this.coolDown * this.genLevel() * (double) this.getMultiplier()), false);
                    this.coolDown = 0.0D;
                }
            } else {
                this.coolDown = 0.0D;
            }

            this.doSpecial();
            if (this.shouldProcess() && (this.getStorage().getEnergyStored() == 0 || (double) this.getStorage().getEnergyStored() < Math.min((double) (this.getStorage().getMaxEnergyStored() - 1000), (double) this.getStorage().getMaxEnergyStored() - (double) this.getMultiplier() * this.genLevel()))) {
                this.processInput();
            }

            if (this.coolDown > 0.0D != this.playSound) {
                super.worldObj.markBlockForUpdate(this.x(), this.y(), this.z());
                this.playSound = this.coolDown > 0.0D;
            }

            if (this.shouldTransmit() && this.getStorage().getEnergyStored() > 0) {
                this.transmitEnergy();
            }

            this.checkCompLevel();
        }
    }

    public void doSpecial() {
    }

    @SideOnly(Side.CLIENT)
    public void doRandomDisplayTickR(Random random) {
    }

    private void transmitEnergy() {
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection side = arr$[i$];
            TileEntity tile = super.worldObj.getTileEntity(this.x() + side.offsetX, this.y() + side.offsetY, this.z() + side.offsetZ);
            if (!(tile instanceof TileEntityGenerator) && tile instanceof IEnergyReceiver) {
                this.getStorage().extractEnergy(((IEnergyReceiver) tile).receiveEnergy(side.getOpposite(), this.getStorage().extractEnergy(this.transferLimit() * this.getMultiplier(), true), false), false);
            }
        }

    }

    public int transferLimit() {
        return this.getStorage().getMaxEnergyStored();
    }

    public boolean shouldTransmit() {
        return true;
    }

    public int getMaxCoolDown() {
        return 200;
    }

    public double getNerfVisor() {
        return this.getMultiplier() == 1 ? 1.0D : (this.getMultiplier() <= 8 ? 1.0D : 1.0D);
    }

    public final boolean addCoolDown(double coolDown, boolean simulate) {
        if (!simulate) {
            this.coolDown += coolDown * this.getDivisor() * this.getNerfVisor();
        }

        return true;
    }

    public abstract boolean processInput();

    public double genLevel() {
        return 0.0D;
    }

    public FluidTank[] getTanks() {
        return new FluidTank[0];
    }

    public InventoryGeneric getInventory() {
        return null;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        int energy = nbt.getInteger("Energy");
        if (energy > this.storage.getMaxEnergyStored()) {
            this.storage.setCapacity(energy);
            this.initPower = true;
        }

        this.storage.setEnergyStored(energy);
        if (this.getInventory() != null) {
            this.getInventory().readFromNBT(nbt);
        }

        if (this.getTanks() != null) {
            for (int i = 0; i < this.getTanks().length; ++i) {
                this.getTanks()[i].readFromNBT(nbt.getCompoundTag("Tank_" + i));
            }
        }

        this.rotation = (byte) nbt.getInteger("rotation");
        this.coolDown = nbt.getDouble("coolDown");
        this.playSound = this.coolDown > 0.0D;

        if (nbt.hasKey("extraMultiplier")) extraMultiplier = nbt.getInteger("extraMultiplier");
    }

    public void writeToNBT(NBTTagCompound nbt) {
        this.getStorage().writeToNBT(nbt);
        if (this.getInventory() != null) {
            this.getInventory().writeToNBT(nbt);
        }

        if (this.getTanks() != null) {
            for (int backup = 0; backup < this.getTanks().length; ++backup) {
                NBTTagCompound t = new NBTTagCompound();
                this.getTanks()[backup].writeToNBT(t);
                nbt.setTag("Tank_" + backup, t);
            }
        }

        nbt.setInteger("rotation", this.rotation);
        nbt.setDouble("coolDown", this.coolDown);

        nbt.setInteger("extraMultiplier", extraMultiplier);

        super.writeToNBT(nbt);
        NBTTagCompound var4 = new NBTTagCompound();
        super.writeToNBT(var4);
        nbt.setTag("backup", var4);
    }

    public Packet getDescriptionPacket() {
        NBTTagCompound t = new NBTTagCompound();
        t.setByte("d", this.rotation);
        t.setBoolean("s", this.coolDown > 0.0D);
        this.playSound = this.coolDown > 0.0D;
        return new S35PacketUpdateTileEntity(this.x(), this.y(), this.z(), 4, t);
    }

    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (super.worldObj.isRemote) {
            NBTTagCompound tags = pkt.getNbtCompound();
            if (tags.hasKey("d")) {
                if (tags.getByte("d") != this.rotation) {
                    super.worldObj.markBlockForUpdate(this.x(), this.y(), this.z());
                }

                this.rotation = tags.getByte("d");
            }

            if (tags.hasKey("s")) {
                this.playSound = tags.getBoolean("s");
                Sounds.refresh();
            }

        }
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return 0;
    }

    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return this.shouldTransmit() ? this.getStorage().extractEnergy(Math.min(this.transferLimit() * this.getMultiplier(), maxExtract), simulate) : 0;
    }

    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    public int getEnergyStored(ForgeDirection from) {
        return this.getStorage().getEnergyStored();
    }

    public int getMaxEnergyStored(ForgeDirection from) {
        return this.getStorage().getMaxEnergyStored();
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int c = 0;
        FluidTank[] arr$ = this.getTanks();
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            FluidTank tank = arr$[i$];
            c += tank.fill(resource, doFill);
        }

        return c;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] info = new FluidTankInfo[this.getTanks().length];

        for (int i = 0; i < this.getTanks().length; ++i) {
            info[i] = this.getTanks()[i].getInfo();
        }

        return info;
    }

    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return true;
    }

    public void readInvFromTags(NBTTagCompound tags) {
        if (tags.hasKey("Energy")) {
            this.getStorage().readFromNBT(tags);
        }
        if (tags.hasKey("extraMultiplier")) extraMultiplier = tags.getInteger("extraMultiplier");
    }

    public void writeInvToTags(NBTTagCompound tags) {
        if (this.getStorage().getEnergyStored() > 0) {
            this.getStorage().writeToNBT(tags);
        }
        tags.setInteger("extraMultiplier", extraMultiplier);
    }
}
