//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.entity;

import baubles.common.lib.PlayerHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.item.ModItems;

import java.awt.*;
import java.util.*;
import java.util.List;

public class EntitySpark extends Entity implements ISparkEntity {
    private static final int TRANSFER_RATE = 1000;
    private static final String TAG_UPGRADE = "upgrade";
    private static final String TAG_TRANSFERS = "tfs";
    private static final String TAG_TRANSFER_ID = "id";
    int removeTransferants = 2;
    boolean firstTick = false;

    public EntitySpark(World world) {
        super(world);
        super.isImmuneToFire = true;
    }

    protected void entityInit() {
        this.setSize(0.5F, 0.5F);
        super.dataWatcher.addObject(28, Integer.valueOf(0));
        super.dataWatcher.addObject(29, new ItemStack(Blocks.stone, 0, 0));
        super.dataWatcher.setObjectWatched(28);
        super.dataWatcher.setObjectWatched(29);
    }

    public void onUpdate() {
        super.onUpdate();
        ISparkAttachable tile = this.getAttachedTile();
        if (tile == null) {
            if (!super.worldObj.isRemote) {
                this.setDead();
            }

        } else {
            boolean first = super.worldObj.isRemote && !this.firstTick;
            int upgrade = this.getUpgrade();
            List allSparks = null;
            if (first || upgrade == 2 || upgrade == 3) {
                allSparks = SparkHelper.getSparksAround(super.worldObj, super.posX, super.posY, super.posZ);
            }

            if (first) {
                first = true;
            }

            Collection transfers = this.getTransfers();
            int manaSpent;
            Iterator var26;
            if (upgrade != 0) {
                switch (upgrade) {
                    case 1:
                        List var19 = SparkHelper.getEntitiesAround(EntityPlayer.class, super.worldObj, super.posX, super.posY, super.posZ);
                        HashMap var22 = new HashMap();
                        ItemStack var25 = new ItemStack(ModItems.spark);
                        var26 = var19.iterator();

                        EntityPlayer spark;
                        while (var26.hasNext()) {
                            spark = (EntityPlayer) var26.next();
                            ArrayList attached = new ArrayList();
                            attached.addAll(Arrays.asList(spark.inventory.mainInventory));
                            attached.addAll(Arrays.asList(spark.inventory.armorInventory));
                            attached.addAll(Arrays.asList(PlayerHandler.getPlayerBaubles(spark).getStacks()));
                            Iterator spend = attached.iterator();

                            while (spend.hasNext()) {
                                ItemStack cost = (ItemStack) spend.next();
                                if (cost != null && cost.getItem() instanceof IManaItem) {
                                    IManaItem manaToPut = (IManaItem) cost.getItem();
                                    if (manaToPut.canReceiveManaFromItem(cost, var25)) {
                                        boolean add = false;
                                        Object receivingStacks;
                                        if (!var22.containsKey(spark)) {
                                            add = true;
                                            receivingStacks = new HashMap();
                                        } else {
                                            receivingStacks = (Map) var22.get(spark);
                                        }

                                        int recv = Math.min(this.getAttachedTile().getCurrentMana(), Math.min(1000, manaToPut.getMaxMana(cost) - manaToPut.getMana(cost)));
                                        if (recv > 0) {
                                            ((Map) receivingStacks).put(cost, Integer.valueOf(recv));
                                            if (add) {
                                                var22.put(spark, receivingStacks);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!var22.isEmpty()) {
                            ArrayList var27 = new ArrayList(var22.keySet());
                            Collections.shuffle(var27);
                            spark = (EntityPlayer) var27.iterator().next();
                            Map var29 = (Map) var22.get(spark);
                            ItemStack var31 = (ItemStack) var29.keySet().iterator().next();
                            int var33 = ((Integer) var29.get(var31)).intValue();
                            int var34 = Math.min(this.getAttachedTile().getCurrentMana(), var33);
                            ((IManaItem) var31.getItem()).addMana(var31, var34);
                            this.getAttachedTile().recieveMana(-var34);
                            this.particlesTowards(spark);
                        }
                        break;
                    case 2:
                        ArrayList var18 = new ArrayList();
                        Iterator var21 = allSparks.iterator();

                        while (var21.hasNext()) {
                            ISparkEntity var24 = (ISparkEntity) var21.next();
                            if (var24 != this) {
                                int upgrade_ = var24.getUpgrade();
                                if (upgrade_ == 0 && var24.getAttachedTile() instanceof IManaPool) {
                                    var18.add(var24);
                                }
                            }
                        }

                        if (var18.size() > 0) {
                            ((ISparkEntity) var18.get(super.worldObj.rand.nextInt(var18.size()))).registerTransfer(this);
                        }
                        break;
                    case 3:
                        Iterator manaTotal = allSparks.iterator();

                        while (manaTotal.hasNext()) {
                            ISparkEntity manaForEach = (ISparkEntity) manaTotal.next();
                            if (manaForEach != this) {
                                manaSpent = manaForEach.getUpgrade();
                                if (manaSpent != 2 && manaSpent != 3 && manaSpent != 4) {
                                    transfers.add(manaForEach);
                                }
                            }
                        }
                }
            }

            if (!transfers.isEmpty()) {
                int var20 = Math.min(1000 * transfers.size(), tile.getCurrentMana());
                int var23 = var20 / transfers.size();
                manaSpent = 0;
                if (var23 > transfers.size()) {
                    var26 = transfers.iterator();

                    while (true) {
                        while (var26.hasNext()) {
                            ISparkEntity var28 = (ISparkEntity) var26.next();
                            if (var28.getAttachedTile() != null && !var28.getAttachedTile().isFull() && !var28.areIncomingTransfersDone()) {
                                ISparkAttachable var30 = var28.getAttachedTile();
                                int var32 = Math.min(var30.getAvailableSpaceForMana(), var23);
                                var30.recieveMana(var32);
                                manaSpent += var32;
                                this.particlesTowards((Entity) var28);
                            } else {
                                var20 -= var23;
                            }
                        }

                        tile.recieveMana(-manaSpent);
                        break;
                    }
                }
            }

            if (this.removeTransferants > 0) {
                --this.removeTransferants;
            }

            this.getTransfers();
        }
    }

    void particlesTowards(Entity e) {
        Vector3 thisVec = Vector3.fromEntityCenter(this).add(0.0D, 0.0D, 0.0D);
        Vector3 receiverVec = Vector3.fromEntityCenter(e).add(0.0D, 0.0D, 0.0D);
        double rc = 0.45D;
        thisVec.add((Math.random() - 0.5D) * rc, (Math.random() - 0.5D) * rc, (Math.random() - 0.5D) * rc);
        receiverVec.add((Math.random() - 0.5D) * rc, (Math.random() - 0.5D) * rc, (Math.random() - 0.5D) * rc);
        Vector3 motion = receiverVec.copy().sub(thisVec);
        motion.multiply(0.03999999910593033D);
        float r = 0.4F + 0.3F * (float) Math.random();
        float g = 0.4F + 0.3F * (float) Math.random();
        float b = 0.4F + 0.3F * (float) Math.random();
        float size = 0.125F + 0.125F * (float) Math.random();
        Botania.proxy.wispFX(super.worldObj, thisVec.x, thisVec.y, thisVec.z, r, g, b, size, (float) motion.x, (float) motion.y, (float) motion.z);
    }

    public static void particleBeam(Entity e1, Entity e2) {
        if (e1 != null && e2 != null) {
            Vector3 orig = new Vector3(e1.posX, e1.posY + 0.25D, e1.posZ);
            Vector3 end = new Vector3(e2.posX, e2.posY + 0.25D, e2.posZ);
            Vector3 diff = end.copy().sub(orig);
            Vector3 movement = diff.copy().normalize().multiply(0.05D);
            int iters = (int) (diff.mag() / movement.mag());
            float huePer = 1.0F / (float) iters;
            float hueSum = (float) Math.random();
            Vector3 currentPos = orig.copy();

            for (int i = 0; i < iters; ++i) {
                float hue = (float) i * huePer + hueSum;
                Color color = Color.getHSBColor(hue, 1.0F, 1.0F);
                float r = Math.min(1.0F, (float) color.getRed() / 255.0F + 0.4F);
                float g = Math.min(1.0F, (float) color.getGreen() / 255.0F + 0.4F);
                float b = Math.min(1.0F, (float) color.getBlue() / 255.0F + 0.4F);
                Botania.proxy.setSparkleFXNoClip(true);
                Botania.proxy.sparkleFX(e1.worldObj, currentPos.x, currentPos.y, currentPos.z, r, g, b, 0.6F, 12);
                Botania.proxy.setSparkleFXNoClip(false);
                currentPos.add(movement);
            }

        }
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean interactFirst(EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null) {
            int upgrade = this.getUpgrade();
            if (stack.getItem() == ModItems.twigWand) {
                if (player.isSneaking()) {
                    if (upgrade > 0) {
                        if (!super.worldObj.isRemote) {
                            this.entityDropItem(new ItemStack(ModItems.sparkUpgrade, 1, upgrade - 1), 0.0F);
                        }

                        this.setUpgrade(0);
                        this.setTransferDataContainer(new ItemStack(Blocks.stone, 0, 0));
                        this.removeTransferants = 2;
                    } else {
                        this.setDead();
                    }

                    if (player.worldObj.isRemote) {
                        player.swingItem();
                    }

                    return true;
                }

                List var7 = SparkHelper.getSparksAround(super.worldObj, super.posX, super.posY, super.posZ);
                Iterator var5 = var7.iterator();

                while (var5.hasNext()) {
                    ISparkEntity spark = (ISparkEntity) var5.next();
                    particleBeam(this, (Entity) spark);
                }

                return true;
            }

            if (stack.getItem() == ModItems.sparkUpgrade && upgrade == 0) {
                int newUpgrade = stack.getMetadata() + 1;
                this.setUpgrade(newUpgrade);
                --stack.stackSize;
                if (player.worldObj.isRemote) {
                    player.swingItem();
                }

                return true;
            }
        }

        return false;
    }

    protected void readEntityFromNBT(NBTTagCompound cmp) {
        this.setUpgrade(cmp.getInteger("upgrade"));
    }

    protected void writeEntityToNBT(NBTTagCompound cmp) {
        cmp.setInteger("upgrade", this.getUpgrade());
    }

    public void setDead() {
        super.setDead();
        if (!super.worldObj.isRemote) {
            int upgrade = this.getUpgrade();
            this.entityDropItem(new ItemStack(ModItems.spark), 0.0F);
            if (upgrade > 0) {
                this.entityDropItem(new ItemStack(ModItems.sparkUpgrade, 1, upgrade - 1), 0.0F);
            }
        }

    }

    public ISparkAttachable getAttachedTile() {
        int x = MathHelper.floor_double(super.posX);
        int y = MathHelper.floor_double(super.posY) - 1;
        int z = MathHelper.floor_double(super.posZ);
        TileEntity tile = super.worldObj.getTileEntity(x, y, z);
        return tile != null && tile instanceof ISparkAttachable ? (ISparkAttachable) tile : null;
    }

    public Collection<ISparkEntity> getTransfers() {
        ArrayList entities = new ArrayList();
        ItemStack transferDataContainer = this.getTransferDataContainer();
        NBTTagList list = ItemNBTHelper.getList(transferDataContainer, "tfs", 10, false);
        NBTTagList newTransfers = new NBTTagList();

        for (int stack = 0; stack < list.tagCount(); ++stack) {
            NBTTagCompound cmp = list.getCompoundTagAt(stack);
            int id = cmp.getInteger("id");
            boolean added = false;
            Entity e = super.worldObj.getEntityByID(id);
            if (e != null && e instanceof ISparkEntity) {
                ISparkEntity cmp_ = (ISparkEntity) e;
                int upgr = this.getUpgrade();
                int supgr = cmp_.getUpgrade();
                ISparkAttachable atile = cmp_.getAttachedTile();
                if (cmp_ != this && !cmp_.areIncomingTransfersDone() && atile != null && !atile.isFull() && (upgr == 0 && supgr == 2 || upgr == 3 && (supgr == 0 || supgr == 1) || !(atile instanceof IManaPool))) {
                    entities.add((ISparkEntity) e);
                    added = true;
                }
            }

            if (added) {
                NBTTagCompound var15 = new NBTTagCompound();
                var15.setInteger("id", id);
                newTransfers.appendTag(var15);
            }
        }

        ItemStack var14 = transferDataContainer.copy();
        ItemNBTHelper.setList(var14, "tfs", list);
        this.setTransferDataContainer(var14);
        return entities;
    }

    private boolean hasTransfer(ISparkEntity entity) {
        ItemStack transferDataContainer = this.getTransferDataContainer();
        NBTTagList list = ItemNBTHelper.getList(transferDataContainer, "tfs", 10, false);
        new NBTTagList();
        int id = ((Entity) entity).getEntityId();

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound cmp = list.getCompoundTagAt(i);
            int tid = cmp.getInteger("id");
            if (id == tid) {
                return true;
            }
        }

        return false;
    }

    public void registerTransfer(ISparkEntity entity) {
        if (!super.worldObj.isRemote && !this.hasTransfer(entity)) {
            ItemStack transferDataContainer = this.getTransferDataContainer();
            NBTTagList list = ItemNBTHelper.getList(transferDataContainer, "tfs", 10, false);
            NBTTagCompound cmp_ = new NBTTagCompound();
            cmp_.setInteger("id", ((Entity) entity).getEntityId());
            list.appendTag(cmp_);
            ItemStack stack = transferDataContainer.copy();
            ItemNBTHelper.setList(stack, "tfs", list);
            this.setTransferDataContainer(stack);
        }
    }

    public int getUpgrade() {
        return super.dataWatcher.getWatchableObjectInt(28);
    }

    public void setUpgrade(int upgrade) {
        super.dataWatcher.updateObject(28, Integer.valueOf(upgrade));
    }

    public ItemStack getTransferDataContainer() {
        return super.dataWatcher.getWatchableObjectItemStack(29);
    }

    public void setTransferDataContainer(ItemStack stack) {
        super.dataWatcher.updateObject(29, stack);
    }

    public boolean areIncomingTransfersDone() {
        ISparkAttachable tile = this.getAttachedTile();
        return tile instanceof IManaPool ? this.removeTransferants > 0 : tile != null && tile.areIncomingTranfersDone();
    }
}
