//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.util.Color;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.Map.Entry;

public class InkEffects {
    private static final HashMap<String, Color> colormap = new HashMap();
    private static Map<ItemStack, Map<String, Float>> itemstack_bindings = new TreeMap(new InkEffects.CompareItemStack());
    private static Map<String, Map<String, Float>> oredict_bindings = new HashMap();
    private static Map<Item, Map<String, Float>> itemId_bindings = new HashMap();

    public InkEffects() {
    }

    public static Set<String> getProperties() {
        return colormap.keySet();
    }

    public static void registerProperty(String key, Color color) {
        colormap.put(key, color);
    }

    public static Color getPropertyColor(String key) {
        return (Color) colormap.get(key);
    }

    public static String getLocalizedName(String property) {
        return StatCollector.translateToLocal(getUnlocalizedName(property));
    }

    public static String getUnlocalizedName(String property) {
        return "linkeffect." + property.replaceAll(" ", "").toLowerCase() + ".name";
    }

    public static Map<String, Float> getItemEffects(ItemStack itemstack) {
        ItemStack clone = itemstack.copy();
        clone.stackSize = 1;
        Map map = (Map) itemstack_bindings.get(clone);
        int[] ids = OreDictionary.getOreIDs(itemstack);
        int[] arr$ = ids;
        int len$ = ids.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            int id = arr$[i$];
            if (map == null) {
                map = (Map) oredict_bindings.get(OreDictionary.getOreName(id));
            }
        }

        if (map == null) {
            map = (Map) itemId_bindings.get(itemstack.getItem());
        }

        if (map == null) {
            return null;
        } else {
            return Collections.unmodifiableMap(map);
        }
    }

    private static void addPropertyToMap(Map<String, Float> itemmap, String property, float probability) {
        Float f = (Float) itemmap.get(property);
        if (f != null) {
            probability += f.floatValue();
        }

        itemmap.put(property, Float.valueOf(probability));
        float total = 0.0F;

        Entry entry;
        for (Iterator i$ = itemmap.entrySet().iterator(); i$.hasNext(); total += ((Float) entry.getValue()).floatValue()) {
            entry = (Entry) i$.next();
        }

        if (total > 1.0F) {
            throw new RuntimeException("ERROR: Total of all ink property probabilities from an item cannot exceed 1!");
        }
    }

    public static void addPropertyToItem(ItemStack itemstack, String property, float probability) {
        itemstack = itemstack.copy();
        itemstack.stackSize = 1;
        Map<String, Float> itemmap = itemstack_bindings.get(itemstack);
        if (itemmap == null) {
            itemmap = new HashMap();
            itemstack_bindings.put(itemstack, itemmap);
        }

        addPropertyToMap((Map) itemmap, property, probability);
    }

    public static void addPropertyToItem(String name, String property, float probability) {
        Map<String, Float> itemmap = oredict_bindings.get(name);
        if (itemmap == null) {
            itemmap = new HashMap();
            oredict_bindings.put(name, itemmap);
        }

        addPropertyToMap((Map) itemmap, property, probability);
    }

    public static void addPropertyToItem(Item item, String property, float probability) {
        Map<String, Float> itemmap = itemId_bindings.get(item);
        if (itemmap == null) {
            itemmap = new HashMap();
            itemId_bindings.put(item, itemmap);
        }

        addPropertyToMap((Map) itemmap, property, probability);
    }

    public static void init() {
        registerProperty("Intra Linking", new Color(0.0F, 1.0F, 0.0F));
        registerProperty("Generate Platform", new Color(0.5F, 0.5F, 0.5F));
        registerProperty("Maintain Momentum", new Color(0.0F, 0.0F, 1.0F));
        registerProperty("Disarm", new Color(1.0F, 0.0F, 0.0F));
        registerProperty("Relative", new Color(0.6F, 0.0F, 0.6F));
        registerProperty("Following", new Color(0.0F, 1.0F, 1.0F));
        addPropertyToItem(new ItemStack(Items.gunpowder), "Disarm", 0.2F);
        addPropertyToItem(new ItemStack(Items.mushroom_stew), "Disarm", 0.05F);
        addPropertyToItem(new ItemStack(Items.clay_ball), "Generate Platform", 0.25F);
        addPropertyToItem(new ItemStack(Items.experience_bottle), "Intra Linking", 0.15F);
        addPropertyToItem("dyeBlack", "", 0.5F);
        addPropertyToItem(new ItemStack(Items.ender_pearl), "Intra Linking", 0.15F);
        addPropertyToItem(new ItemStack(Items.ender_pearl), "Disarm", 0.15F);
        addPropertyToItem(new ItemStack(Items.feather), "Maintain Momentum", 0.15F);
        addPropertyToItem(Items.fire_charge, "Disarm", 0.25F);
        addPropertyToItem("dustBrass", "Disarm", 0.15F);
        addPropertyToItem("dustBronze", "Disarm", 0.15F);
        addPropertyToItem("dustTin", "Generate Platform", 0.1F);
        addPropertyToItem("dustTin", "Intra Linking", 0.1F);
        addPropertyToItem("dustIron", "Generate Platform", 0.15F);
        addPropertyToItem("dustIron", "Intra Linking", 0.15F);
        addPropertyToItem("dustLead", "Disarm", 0.2F);
        addPropertyToItem("dustLead", "Intra Linking", 0.2F);
        addPropertyToItem("dustSilver", "Generate Platform", 0.2F);
        addPropertyToItem("dustSilver", "Intra Linking", 0.2F);
        addPropertyToItem("dustDiamond", "Intra Linking", 0.25F);
        addPropertyToItem("dustDiamond", "Maintain Momentum", 0.1F);
        addPropertyToItem("dustDiamond", "Generate Platform", 0.1F);
        addPropertyToItem("dustGold", "Intra Linking", 0.25F);
        addPropertyToItem("dustGold", "Generate Platform", 0.1F);
        addPropertyToItem("dustGold", "Disarm", 0.1F);
        addPropertyToItem("gemEmerald", "Following", 0.15F);
    }

    public static class CompareItemStack implements Comparator<ItemStack> {
        public CompareItemStack() {
        }

        public int compare(ItemStack paramT1, ItemStack paramT2) {
            return paramT1 == paramT2 ? 0 : (ItemStack.areItemStacksEqual(paramT1, paramT2) ? 0 : (Item.getIdFromItem(paramT1.getItem()) < Item.getIdFromItem(paramT2.getItem()) ? -1 : (Item.getIdFromItem(paramT1.getItem()) > Item.getIdFromItem(paramT2.getItem()) ? 1 : (paramT1.getMetadata() < paramT2.getMetadata() ? -1 : (paramT1.getMetadata() > paramT2.getMetadata() ? 1 : (paramT1.stackSize < paramT2.stackSize ? -1 : (paramT1.stackSize > paramT2.stackSize ? 1 : (paramT1.stackTagCompound == null ? -1 : (paramT2.stackTagCompound == null ? 1 : paramT1.toString().compareTo(paramT2.toString()))))))))));
        }
    }
}
