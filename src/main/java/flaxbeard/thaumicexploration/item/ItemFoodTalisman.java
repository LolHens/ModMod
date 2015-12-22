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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import flaxbeard.thaumicexploration.interop.AppleCoreInterop;
import flaxbeard.thaumicexploration.misc.FakePlayerPotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;

import java.lang.reflect.Field;
import java.util.*;

public class ItemFoodTalisman extends Item implements IBauble {
    public static List<String> foodBlacklist = new ArrayList();
    public static List<String> infiniteFood = new ArrayList();
    public static Map<String, Boolean> foodCache = new HashMap();

    public ItemFoodTalisman(int par1) {
        super.maxStackSize = 1;
        foodBlacklist.add(ConfigItems.itemManaBean.getUnlocalizedName());
        foodBlacklist.add(ConfigItems.itemZombieBrain.getUnlocalizedName());
        foodBlacklist.add("item.foodstuff.0.name");
        infiniteFood.add("item.infinitefruit");
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

    public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
        if (entity instanceof EntityPlayer && !world.isRemote && entity.ticksExisted % 20 == 0) {
            EntityPlayer player = (EntityPlayer) entity;

            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }

            if (!itemStack.stackTagCompound.hasKey("saturation")) {
                itemStack.stackTagCompound.setFloat("saturation", 0.0F);
            }

            if (!itemStack.stackTagCompound.hasKey("food")) {
                itemStack.stackTagCompound.setFloat("food", 0.0F);
            }

            boolean infEaten = false;

            final InventoryBaubles inventoryBaubles = PlayerHandler.getPlayerBaubles(player);

            for (int i = 0; i < inventoryBaubles.getSizeInventory(); ++i) {
                ItemStack stack = inventoryBaubles.getStackInSlot(i);
                if (stack == null) continue;

                if (infiniteFood.contains(stack.getUnlocalizedName().toLowerCase()))
                    if (eat(itemStack, player, 99.9F - itemStack.stackTagCompound.getFloat("food"), 100F))
                        infEaten = true;
            }

            for (int i = 0; i < 10; ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack == null) continue;

                if (infiniteFood.contains(stack.getUnlocalizedName().toLowerCase()))
                    if (eat(itemStack, player, 99.9F - itemStack.stackTagCompound.getFloat("food"), 100F))
                        infEaten = true;
            }

            if (!infEaten) {
                for (int i = 0; i < 10; ++i) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack == null) continue;

                    if (this.isEdible(stack, player)) {
                        float sat;
                        float heal;
                        boolean inf = false;

                        if (Loader.isModLoaded("AppleCore")) {
                            sat = AppleCoreInterop.getSaturation(stack) * 2.0F;
                            heal = (float) AppleCoreInterop.getHeal(stack);
                        } else {
                            sat = ((ItemFood) stack.getItem()).getSaturationModifier(stack) * 2.0F;
                            heal = (float) ((ItemFood) stack.getItem()).getHealAmount(stack);
                        }

                        if (eat(itemStack, player, heal, sat)) {
                            if (stack.stackSize <= 1) {
                                player.inventory.setInventorySlotContents(i, (ItemStack) null);
                            }
                            player.inventory.decrStackSize(i, 1);
                        }
                    }
                }
            }

            float var11;
            float var12;
            if (player.getFoodStats().getFoodLevel() < 20 && 100.0F - itemStack.stackTagCompound.getFloat("food") > 0.0F) {
                var11 = itemStack.stackTagCompound.getFloat("food");
                var12 = 0.0F;
                if ((float) (20 - player.getFoodStats().getFoodLevel()) < var11) {
                    var12 = var11 - (float) (20 - player.getFoodStats().getFoodLevel());
                    var11 = (float) (20 - player.getFoodStats().getFoodLevel());
                }

                if (Loader.isModLoaded("AppleCore")) {
                    AppleCoreInterop.setHunger((int) var11, player);
                } else {
                    ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), Integer.valueOf((int) ((float) player.getFoodStats().getFoodLevel() + var11)), new String[]{"field_75127_a", "foodLevel"});
                }

                itemStack.stackTagCompound.setFloat("food", var12);
                itemStack.setMetadata(itemStack.getMetadata());
            }

            if (player.getFoodStats().getSaturationLevel() < (float) player.getFoodStats().getFoodLevel() && itemStack.stackTagCompound.getFloat("saturation") > 0.0F) {
                var11 = itemStack.stackTagCompound.getFloat("saturation");
                var12 = 0.0F;
                if ((float) player.getFoodStats().getFoodLevel() - player.getFoodStats().getSaturationLevel() < var11) {
                    var12 = var11 - ((float) player.getFoodStats().getFoodLevel() - player.getFoodStats().getSaturationLevel());
                    var11 = (float) player.getFoodStats().getFoodLevel() - player.getFoodStats().getSaturationLevel();
                }

                if (Loader.isModLoaded("AppleCore")) {
                    AppleCoreInterop.setSaturation(var11, player);
                } else {
                    ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), Float.valueOf((float) player.getFoodStats().getFoodLevel() + var11), new String[]{"field_75125_b", "foodSaturationLevel"});
                }

                itemStack.stackTagCompound.setFloat("saturation", var12);
                itemStack.setMetadata(itemStack.getMetadata());
            }
        }

    }

    private boolean eat(ItemStack itemStack, EntityPlayer player, float heal, float sat) {
        if (itemStack.stackTagCompound.getFloat("food") + (float) ((int) heal) < 100.0F) {
            if (itemStack.stackTagCompound.getFloat("saturation") + sat <= 100.0F) {
                itemStack.stackTagCompound.setFloat("saturation", itemStack.stackTagCompound.getFloat("saturation") + sat);
            } else {
                itemStack.stackTagCompound.setFloat("saturation", 100.0F);
            }

            player.playSound("random.eat", 0.5F + 0.5F * (float) player.worldObj.rand.nextInt(2), (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
            itemStack.stackTagCompound.setFloat("food", itemStack.stackTagCompound.getFloat("food") + (float) ((int) heal));

            return true;
        }
        return false;
    }

    private boolean isEdible(ItemStack food, EntityPlayer player) {
        String foodName = food.getUnlocalizedName().toLowerCase();
        if (foodCache.containsKey(foodName)) {
            return ((Boolean) foodCache.get(foodName)).booleanValue();
        } else {
            Iterator iterator = foodBlacklist.iterator();

            String elem;
            do {
                if (!iterator.hasNext()) {
                    if (food.getItem() instanceof ItemFood) {
                        try {
                            for (int i = 1; i < 25; ++i) {
                                FakePlayerPotion fakePlayer = new FakePlayerPotion(player.worldObj, new GameProfile((UUID) null, "foodTabletPlayer"));
                                fakePlayer.setPosition(0.0D, 999.0D, 0.0D);
                                ((ItemFood) food.getItem()).onItemUseFinish(food.copy(), player.worldObj, fakePlayer);
                                if (Loader.isModLoaded("HungerOverhaul")) {
                                    if (fakePlayer.getActivePotionEffects().size() > 1) {
                                        foodCache.put(foodName, Boolean.valueOf(false));
                                        return false;
                                    }

                                    if (fakePlayer.getActivePotionEffects().size() == 1) {
                                        Class clazz = Class.forName("iguanaman.hungeroverhaul.HungerOverhaul");
                                        Field fields = clazz.getField("potionWellFed");
                                        Potion effect = (Potion) fields.get((Object) null);
                                        if (effect != null && fakePlayer.getActivePotionEffect(effect) == null) {
                                            foodCache.put(foodName, Boolean.valueOf(false));
                                            return false;
                                        }
                                    }
                                } else if (fakePlayer.getActivePotionEffects().size() > 0) {
                                    foodCache.put(foodName, Boolean.valueOf(false));
                                    return false;
                                }
                            }

                            foodCache.put(foodName, Boolean.valueOf(true));
                            return true;
                        } catch (Exception e) {
                            foodCache.put(foodName, Boolean.valueOf(false));
                            return false;
                        }
                    }

                    foodCache.put(foodName, Boolean.valueOf(false));
                    return false;
                }

                elem = (String) iterator.next();
            } while (!elem.equalsIgnoreCase(foodName));

            foodCache.put(foodName, Boolean.valueOf(false));
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
