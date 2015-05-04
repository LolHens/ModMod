//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package flaxbeard.thaumicexploration.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import flaxbeard.thaumicexploration.misc.FakePlayerPotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;

import java.util.*;

public class ItemFoodTalisman extends Item implements IBauble {
    public static List<String> foodBlacklist = new ArrayList();
    public static Map<String, Boolean> foodCache = new HashMap();

    public static List<String> infiniteFood = Arrays.asList(
            "item.infiniteFruit"
    );

    public ItemFoodTalisman(int par1) {
        super.maxStackSize = 1;
        foodBlacklist.add(ConfigItems.itemManaBean.getUnlocalizedName());
        foodBlacklist.add(ConfigItems.itemZombieBrain.getUnlocalizedName());
    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (!par1ItemStack.hasTagCompound()) {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        if (!par1ItemStack.stackTagCompound.hasKey("saturation")) {
            par1ItemStack.stackTagCompound.setFloat("saturation", 0.0F);
        }

        if (!par1ItemStack.stackTagCompound.hasKey("food")) {
            par1ItemStack.stackTagCompound.setFloat("food", 0.0F);
        }

        par3List.add("Currently holds " + (int) par1ItemStack.stackTagCompound.getFloat("food") + " food points and " + (int) par1ItemStack.stackTagCompound.getFloat("saturation") + " saturation points.");
    }

    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
        if (par3Entity instanceof EntityPlayer && !par2World.isRemote && par3Entity.ticksExisted % 20 == 0) {
            final EntityPlayer player = (EntityPlayer) par3Entity;

            if (!par1ItemStack.hasTagCompound()) {
                par1ItemStack.setTagCompound(new NBTTagCompound());
            }

            if (!par1ItemStack.stackTagCompound.hasKey("saturation")) {
                par1ItemStack.stackTagCompound.setFloat("saturation", 0.0F);
            }

            if (!par1ItemStack.stackTagCompound.hasKey("food")) {
                par1ItemStack.stackTagCompound.setFloat("food", 0.0F);
            }

            final InventoryBaubles inventoryBaubles = PlayerHandler.getPlayerBaubles(player);

            for (int sat = 0; sat < inventoryBaubles.getSizeInventory(); ++sat) {
                final int finalSat = sat;
                eat(par1ItemStack, player, inventoryBaubles.getStackInSlot(sat), new InventoryModifier() {
                    @Override
                    public void removeStack() {
                        inventoryBaubles.setInventorySlotContents(finalSat, null);
                    }

                    @Override
                    public void decrStackSize() {
                        inventoryBaubles.decrStackSize(finalSat, 1);
                    }
                });
            }

            for (int sat = 0; sat < 10; ++sat) {
                final int finalSat = sat;
                eat(par1ItemStack, player, player.inventory.getStackInSlot(sat), new InventoryModifier() {
                    @Override
                    public void removeStack() {
                        player.inventory.setInventorySlotContents(finalSat, null);
                    }

                    @Override
                    public void decrStackSize() {
                        player.inventory.decrStackSize(finalSat, 1);
                    }
                });
            }

            float var11;
            float var12;
            if (player.getFoodStats().getFoodLevel() < 20 && 100.0F - par1ItemStack.stackTagCompound.getFloat("food") > 0.0F) {
                var11 = par1ItemStack.stackTagCompound.getFloat("food");
                var12 = 0.0F;
                if ((float) (20 - player.getFoodStats().getFoodLevel()) < var11) {
                    var12 = var11 - (float) (20 - player.getFoodStats().getFoodLevel());
                    var11 = (float) (20 - player.getFoodStats().getFoodLevel());
                }

                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), Integer.valueOf((int) ((float) player.getFoodStats().getFoodLevel() + var11)), "field_75127_a", "foodLevel");
                par1ItemStack.stackTagCompound.setFloat("food", var12);
                par1ItemStack.setMetadata(par1ItemStack.getMetadata());
            }

            if (player.getFoodStats().getSaturationLevel() < (float) player.getFoodStats().getFoodLevel() && par1ItemStack.stackTagCompound.getFloat("saturation") > 0.0F) {
                var11 = par1ItemStack.stackTagCompound.getFloat("saturation");
                var12 = 0.0F;
                if ((float) player.getFoodStats().getFoodLevel() - player.getFoodStats().getSaturationLevel() < var11) {
                    var12 = var11 - ((float) player.getFoodStats().getFoodLevel() - player.getFoodStats().getSaturationLevel());
                    var11 = (float) player.getFoodStats().getFoodLevel() - player.getFoodStats().getSaturationLevel();
                }

                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), Float.valueOf((float) player.getFoodStats().getFoodLevel() + var11), "field_75125_b", "foodSaturationLevel");
                par1ItemStack.stackTagCompound.setFloat("saturation", var12);
                par1ItemStack.setMetadata(par1ItemStack.getMetadata());
            }
        }

    }

    private void eat(ItemStack par1ItemStack, EntityPlayer player, ItemStack finalSat, InventoryModifier invMod) {
        if (finalSat != null) {
            if (this.isEdible(finalSat, player)) {
                float sat1 = ((ItemFood) finalSat.getItem()).getSaturationModifier(finalSat) * 2.0F;
                float heal = (float) ((ItemFood) finalSat.getItem()).getHealAmount(finalSat);
                if (par1ItemStack.stackTagCompound.getFloat("food") + (float) ((int) heal) < 100.0F) {
                    if (par1ItemStack.stackTagCompound.getFloat("saturation") + sat1 <= 100.0F) {
                        par1ItemStack.stackTagCompound.setFloat("saturation", par1ItemStack.stackTagCompound.getFloat("saturation") + sat1);
                    } else {
                        par1ItemStack.stackTagCompound.setFloat("saturation", 100.0F);
                    }

                    if (finalSat.stackSize <= 1) {
                        invMod.removeStack();
                    } else {
                        invMod.decrStackSize();
                    }

                    player.playSound("random.eat", 0.5F + 0.5F * (float) player.worldObj.rand.nextInt(2), (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
                    par1ItemStack.stackTagCompound.setFloat("food", par1ItemStack.stackTagCompound.getFloat("food") + (float) ((int) heal));
                }
            } else if (infiniteFood.contains(finalSat.getItem().getUnlocalizedName())) {
                par1ItemStack.stackTagCompound.setFloat("food", 100.0F);
                par1ItemStack.stackTagCompound.setFloat("saturation", 100.0F);
            }
        }
    }

    private interface InventoryModifier {
        void removeStack();

        void decrStackSize();
    }

    private boolean isEdible(ItemStack food, EntityPlayer player) {
        String foodName = food.getUnlocalizedName();
        if (foodCache.containsKey(foodName.toLowerCase())) {
            return foodCache.get(foodName.toLowerCase()).booleanValue();
        } else {
            Iterator i = foodBlacklist.iterator();

            String fakePlayer;
            do {
                if (!i.hasNext()) {
                    if (food.getItem() instanceof ItemFood) {
                        for (int var6 = 1; var6 < 25; ++var6) {
                            FakePlayerPotion var7 = new FakePlayerPotion(player.worldObj, new GameProfile(null, "foodTabletPlayer"));
                            var7.setPosition(0.0D, 999.0D, 0.0D);
                            food.getItem().onItemUseFinish(food.copy(), player.worldObj, var7);
                            if (var7.getActivePotionEffects().size() > 0) {
                                foodCache.put(foodName.toLowerCase(), Boolean.valueOf(false));
                                return false;
                            }
                        }

                        foodCache.put(foodName.toLowerCase(), Boolean.valueOf(true));
                        return true;
                    }

                    foodCache.put(foodName.toLowerCase(), Boolean.valueOf(false));
                    return false;
                }

                fakePlayer = (String) i.next();
            } while (!fakePlayer.equalsIgnoreCase(foodName));

            foodCache.put(foodName.toLowerCase(), Boolean.valueOf(false));
            return false;
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
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
