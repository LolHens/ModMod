//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package thaumcraft.common.lib.events;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.baubles.ItemAmuletRunic;
import thaumcraft.common.items.baubles.ItemGirdleRunic;
import thaumcraft.common.items.baubles.ItemRingRunic;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.network.playerdata.PacketRunicCharge;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.HashMap;

public class EventHandlerRunic {
    public HashMap<Integer, Integer> runicCharge = new HashMap();
    public HashMap<Integer, Integer[]> runicInfo = new HashMap();
    public boolean isDirty = true;
    private HashMap<Integer, Long> nextCycle = new HashMap();
    private HashMap<Integer, Integer> lastCharge = new HashMap();
    private HashMap<String, Long> upgradeCooldown = new HashMap();
    private int rechargeDelay = 0;

    public EventHandlerRunic() {
    }

    public static int getFinalCharge(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IRunicArmor)) {
            return 0;
        } else {
            IRunicArmor armor = (IRunicArmor) stack.getItem();
            int base = armor.getRunicCharge(stack);
            if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("RS.HARDEN")) {
                base += stack.stackTagCompound.getByte("RS.HARDEN");
            }

            return base;
        }
    }

    public static int getFinalWarp(ItemStack stack, EntityPlayer player) {
        if (stack != null && stack.getItem() instanceof IWarpingGear) {
            IWarpingGear armor = (IWarpingGear) stack.getItem();
            return armor.getWarp(stack, player);
        } else {
            return 0;
        }
    }

    public static int getHardening(ItemStack stack) {
        if (!(stack.getItem() instanceof IRunicArmor)) {
            return 0;
        } else {
            int base = 0;
            if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("RS.HARDEN")) {
                base += stack.stackTagCompound.getByte("RS.HARDEN");
            }

            return base;
        }
    }

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            int charge;
            if (this.isDirty || player.ticksExisted % 40 == 0) {
                int time = 0;
                int charged = 0;
                charge = 0;
                int interval = 0;
                int emergency = 0;
                this.isDirty = false;

                int charge1;
                for (int baubles = 0; baubles < 4; ++baubles) {
                    if (player.inventory.armorItemInSlot(baubles) != null && player.inventory.armorItemInSlot(baubles).getItem() instanceof IRunicArmor) {
                        charge1 = getFinalCharge(player.inventory.armorItemInSlot(baubles));
                        time += charge1;
                    }
                }

                IInventory baubleInv = BaublesApi.getBaubles(player);

                for (int slot = 0; slot < baubleInv.getSizeInventory(); slot++) {
                    if (baubleInv.getStackInSlot(slot) != null && baubleInv.getStackInSlot(slot).getItem() instanceof IRunicArmor) {
                        int amount = getFinalCharge(baubleInv.getStackInSlot(slot));
                        if (baubleInv.getStackInSlot(slot).getItem() instanceof ItemRingRunic) {
                            switch (baubleInv.getStackInSlot(slot).getMetadata()) {
                                case 2:
                                    charged++;
                                    break;
                                case 3:
                                    interval++;
                            }
                        } else if (baubleInv.getStackInSlot(slot).getItem() instanceof ItemAmuletRunic && baubleInv.getStackInSlot(slot).getMetadata() == 1) {
                            emergency++;
                        } else if (baubleInv.getStackInSlot(slot).getItem() instanceof ItemGirdleRunic && baubleInv.getStackInSlot(slot).getMetadata() == 1) {
                            charge++;
                        }

                        time += amount;
                    }
                }

                if (time > 0) {
                    runicInfo.put(player.getEntityId(), new Integer[]{time, charged, charge, interval, emergency});
                    if (runicCharge.containsKey(player.getEntityId())) {
                        int charge2 = runicCharge.get(player.getEntityId());
                        if (charge2 > time) {
                            this.runicCharge.put(player.getEntityId(), time);
                            PacketHandler.INSTANCE.sendTo(new PacketRunicCharge(player, (short) time, time), (EntityPlayerMP) player);
                        }
                    }
                } else {
                    this.runicInfo.remove(player.getEntityId());
                    this.runicCharge.put(player.getEntityId(), 0);
                    PacketHandler.INSTANCE.sendTo(new PacketRunicCharge(player, (short) 0, 0), (EntityPlayerMP) player);
                }
            }

            if (this.rechargeDelay > 0) {
                --this.rechargeDelay;
            } else if (this.runicInfo.containsKey(player.getEntityId())) {
                if (!this.lastCharge.containsKey(player.getEntityId())) {
                    this.lastCharge.put(player.getEntityId(), -1);
                }

                if (!this.runicCharge.containsKey(player.getEntityId())) {
                    this.runicCharge.put(player.getEntityId(), 0);
                }

                if (!this.nextCycle.containsKey(player.getEntityId())) {
                    this.nextCycle.put(player.getEntityId(), Long.valueOf(0L));
                }

                long var11 = System.currentTimeMillis();
                charge = this.runicCharge.get(player.getEntityId());
                if (charge > this.runicInfo.get(player.getEntityId())[0]) {
                    charge = this.runicInfo.get(player.getEntityId())[0];
                } else if (charge < this.runicInfo.get(player.getEntityId())[0] && this.nextCycle.get(player.getEntityId()) < var11 && WandManager.consumeVisFromInventory(player, (new AspectList()).add(Aspect.AIR, Config.shieldCost).add(Aspect.EARTH, Config.shieldCost))) {
                    long var12 = (long) (Config.shieldRecharge - this.runicInfo.get(player.getEntityId())[1] * 500);
                    this.nextCycle.put(player.getEntityId(), var11 + var12);
                    ++charge;
                    this.runicCharge.put(player.getEntityId(), charge);
                }

                if (this.lastCharge.get(player.getEntityId()) != charge) {
                    PacketHandler.INSTANCE.sendTo(new PacketRunicCharge(player, (short) charge, this.runicInfo.get(player.getEntityId())[0]), (EntityPlayerMP) player);
                    this.lastCharge.put(player.getEntityId(), charge);
                }
            }
        }

    }

    @SubscribeEvent
    public void entityHurt(LivingHurtEvent event) {
        if (event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityPlayer) {
            EntityPlayer mob = (EntityPlayer) event.source.getSourceOfDamage();
            ItemStack t = mob.inventory.armorInventory[3];
            if (t != null && t.getItem() instanceof ItemFortressArmor && t.hasTagCompound() && t.stackTagCompound.hasKey("mask") && t.stackTagCompound.getInteger("mask") == 2 && mob.worldObj.rand.nextFloat() < event.ammount / 12.0F) {
                mob.heal(1.0F);
            }
        }

        EntityMob mob1;
        int t2;
        if (event.entity instanceof EntityPlayer) {
            long mob2 = System.currentTimeMillis();
            EntityPlayer attacker2 = (EntityPlayer) event.entity;
            if (event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) event.source.getSourceOfDamage();
                ItemStack charge = attacker2.inventory.armorInventory[3];
                if (charge != null && charge.getItem() instanceof ItemFortressArmor && charge.hasTagCompound() && charge.stackTagCompound.hasKey("mask") && charge.stackTagCompound.getInteger("mask") == 1 && attacker2.worldObj.rand.nextFloat() < event.ammount / 10.0F) {
                    try {
                        target.addPotionEffect(new PotionEffect(Potion.wither.getId(), 80));
                    } catch (Exception var13) {
                    }
                }
            }

            if (event.source == DamageSource.drown || event.source == DamageSource.wither || event.source == DamageSource.outOfWorld || event.source == DamageSource.starve) {
                return;
            }

            if (this.runicInfo.containsKey(attacker2.getEntityId()) && this.runicCharge.containsKey(attacker2.getEntityId()) && this.runicCharge.get(attacker2.getEntityId()) > 0) {
                int target1 = -1;
                if (event.source.getEntity() != null) {
                    target1 = event.source.getEntity().getEntityId();
                }

                if (event.source == DamageSource.fall) {
                    target1 = -2;
                }

                if (event.source == DamageSource.fallingBlock) {
                    target1 = -3;
                }

                PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(event.entity.getEntityId(), target1), new TargetPoint(event.entity.worldObj.provider.dimensionId, event.entity.posX, event.entity.posY, event.entity.posZ, 64.0D));
                int charge1 = this.runicCharge.get(attacker2.getEntityId());
                if ((float) charge1 > event.ammount) {
                    charge1 = (int) ((float) charge1 - event.ammount);
                    event.ammount = 0.0F;
                } else {
                    event.ammount -= (float) charge1;
                    charge1 = 0;
                }

                String key = attacker2.getEntityId() + ":" + 2;
                if (charge1 <= 0 && this.runicInfo.get(attacker2.getEntityId())[2] > 0 && (!this.upgradeCooldown.containsKey(key) || this.upgradeCooldown.get(key) < mob2)) {
                    this.upgradeCooldown.put(key, mob2 + 20000L);
                    attacker2.worldObj.newExplosion(attacker2, attacker2.posX, attacker2.posY + (double) (attacker2.height / 2.0F), attacker2.posZ, 1.5F + (float) this.runicInfo.get(attacker2.getEntityId())[2] * 0.5F, false, false);
                }

                key = attacker2.getEntityId() + ":" + 3;
                if (charge1 <= 0 && this.runicInfo.get(attacker2.getEntityId())[3] > 0 && (!this.upgradeCooldown.containsKey(key) || this.upgradeCooldown.get(key) < mob2)) {
                    this.upgradeCooldown.put(key, mob2 + 20000L);
                    synchronized (attacker2) {
                        try {
                            attacker2.addPotionEffect(new PotionEffect(Potion.regeneration.id, 240, this.runicInfo.get(attacker2.getEntityId())[3]));
                        } catch (Exception var11) {
                        }
                    }

                    attacker2.worldObj.playSoundAtEntity(attacker2, "thaumcraft:runicShieldEffect", 1.0F, 1.0F);
                }

                key = attacker2.getEntityId() + ":" + 4;
                if (charge1 <= 0 && this.runicInfo.get(attacker2.getEntityId())[4] > 0 && (!this.upgradeCooldown.containsKey(key) || this.upgradeCooldown.get(key) < mob2)) {
                    this.upgradeCooldown.put(key, mob2 + 60000L);
                    int t1 = 8 * this.runicInfo.get(attacker2.getEntityId())[4];
                    charge1 = Math.min(this.runicInfo.get(attacker2.getEntityId())[0], t1);
                    this.isDirty = true;
                    attacker2.worldObj.playSoundAtEntity(attacker2, "thaumcraft:runicShieldCharge", 1.0F, 1.0F);
                }

                if (charge1 <= 0) {
                    this.rechargeDelay = Config.shieldWait;
                }

                this.runicCharge.put(attacker2.getEntityId(), charge1);
                PacketHandler.INSTANCE.sendTo(new PacketRunicCharge(attacker2, (short) charge1, this.runicInfo.get(attacker2.getEntityId())[0]), (EntityPlayerMP) attacker2);
            }
        } else if (event.entity instanceof EntityMob && (((EntityMob) event.entity).getEntityAttribute(EntityUtils.CHAMPION_MOD).getAttributeValue() >= 0.0D || event.entity instanceof IEldritchMob)) {
            mob1 = (EntityMob) event.entity;
            t2 = (int) ((EntityMob) event.entity).getEntityAttribute(EntityUtils.CHAMPION_MOD).getAttributeValue();
            if ((t2 == 5 || event.entity instanceof IEldritchMob) && mob1.getAbsorptionAmount() > 0.0F) {
                int attacker1 = -1;
                if (event.source.getEntity() != null) {
                    attacker1 = event.source.getEntity().getEntityId();
                }

                if (event.source == DamageSource.fall) {
                    attacker1 = -2;
                }

                if (event.source == DamageSource.fallingBlock) {
                    attacker1 = -3;
                }

                PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(mob1.getEntityId(), attacker1), new TargetPoint(event.entity.worldObj.provider.dimensionId, event.entity.posX, event.entity.posY, event.entity.posZ, 32.0D));
                event.entity.worldObj.playSoundEffect(event.entity.posX, event.entity.posY, event.entity.posZ, "thaumcraft:runicShieldEffect", 0.66F, 1.1F + event.entity.worldObj.rand.nextFloat() * 0.1F);
            } else if (t2 >= 0 && ChampionModifier.mods[t2].type == 2 && event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.source.getSourceOfDamage();
                event.ammount = ChampionModifier.mods[t2].effect.performEffect(mob1, attacker, event.source, event.ammount);
            }
        }

        if (event.ammount > 0.0F && event.source.getSourceOfDamage() != null && event.entity instanceof EntityLivingBase && event.source.getSourceOfDamage() instanceof EntityMob && ((EntityMob) event.source.getSourceOfDamage()).getEntityAttribute(EntityUtils.CHAMPION_MOD).getAttributeValue() >= 0.0D) {
            mob1 = (EntityMob) event.source.getSourceOfDamage();
            t2 = (int) mob1.getEntityAttribute(EntityUtils.CHAMPION_MOD).getAttributeValue();
            if (ChampionModifier.mods[t2].type == 1) {
                event.ammount = ChampionModifier.mods[t2].effect.performEffect(mob1, (EntityLivingBase) event.entity, event.source, event.ammount);
            }
        }

    }

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        int charge = getFinalCharge(event.itemStack);
        if (charge > 0) {
            event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.runic.charge") + " +" + charge);
        }

        int warp = getFinalWarp(event.itemStack, event.entityPlayer);
        if (warp > 0) {
            event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("item.warping") + " " + warp);
        }

    }
}
