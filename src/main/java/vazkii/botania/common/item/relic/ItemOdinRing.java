//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.relic;

import baubles.api.BaubleType;
import baubles.common.lib.PlayerHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class ItemOdinRing extends ItemRelicBauble {
    public static List<String> damageNegations = new ArrayList();
    Multimap<String, AttributeModifier> attributes = HashMultimap.create();

    public ItemOdinRing() {
        super("odinRing");
        MinecraftForge.EVENT_BUS.register(this);
        damageNegations.add(DamageSource.drown.damageType);
        damageNegations.add(DamageSource.fall.damageType);
        damageNegations.add(DamageSource.lava.damageType);
        if (ConfigHandler.ringOfOdinFireResist) {
            damageNegations.add(DamageSource.inFire.damageType);
            damageNegations.add(DamageSource.onFire.damageType);
        }

        damageNegations.add(DamageSource.inWall.damageType);
        damageNegations.add(DamageSource.starve.damageType);
    }

    public void onValidPlayerWornTick(ItemStack stack, EntityPlayer player) {
        if (player.isBurning()) {
            player.extinguish();
        }

    }

    @SubscribeEvent
    public void onPlayerAttacked(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            if (getOdinRing(player) != null && damageNegations.contains(event.source.damageType)) {
                event.setCanceled(true);
            }
        }

    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.RING;
    }

    public static ItemStack getOdinRing(EntityPlayer player) {
        ItemStack[] baubles = PlayerHandler.getPlayerBaubles(player).getStacks();
        for (ItemStack stack : baubles) {
            if (isOdinRing(stack)) return stack;
        }
        return null;
    }

    private static boolean isOdinRing(ItemStack stack) {
        return stack != null && (stack.getItem() == ModItems.odinRing || stack.getItem() == ModItems.aesirRing);
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        this.attributes.clear();
        this.fillModifiers(this.attributes, stack);
        player.getAttributeMap().applyAttributeModifiers(this.attributes);
    }

    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        this.attributes.clear();
        this.fillModifiers(this.attributes, stack);
        player.getAttributeMap().removeAttributeModifiers(this.attributes);
    }

    void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), new AttributeModifier(getBaubleUUID(stack), "Bauble modifier", 20.0D, 0));
    }
}
