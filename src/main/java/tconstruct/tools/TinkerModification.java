package tconstruct.tools;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.armor.TinkerArmor;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.modifiers.tools.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by LolHens on 30.04.2015.
 */
@GameRegistry.ObjectHolder("TConstruct")
@Pulse(
        id = "Tinkers\' Modification",
        description = "A Modification for Tinkers\' Construct."
)
public class TinkerModification {
    public static int toggleExtraModifiers = 2;

    @Handler
    public void init(FMLInitializationEvent event) {
        ModifyBuilder.registerModifier(new ModSneakDetector(new ItemStack[]{
                new ItemStack(Items.comparator),
                new ItemStack(Items.redstone)
        }, false));
        ModifyBuilder.registerModifier(new ModSneakDetector(new ItemStack[]{
                new ItemStack(Items.comparator),
                new ItemStack(Blocks.redstone_torch)
        }, true));

        ModifyBuilder.registerModifier(new ModRange(new ItemStack[]{
                new ItemStack(Items.ender_eye),
                new ItemStack(TinkerArmor.diamondApple)
        }));

        ModifyBuilder.registerModifier(new ModDepth(new ItemStack[]{
                new ItemStack(Items.ender_eye),
                new ItemStack(Items.golden_apple, 1, 1)
        }));

        ModifyBuilder.registerModifier(new ModConvenient(new ItemStack[]{
                new ItemStack(Items.apple),
                new ItemStack(Items.golden_apple),
                new ItemStack(TinkerArmor.diamondApple)
        }));

        ModifyBuilder.registerModifier(new ModCareful(new ItemStack[]{
                new ItemStack(Blocks.glass_pane)
        }));

        ModifyBuilder.registerModifier(new ModPrecision(new ItemStack[]{
                new ItemStack(Blocks.wool)
        }));

        ModifyBuilder.registerModifier(new ModFlightSpeed(new ItemStack[]{
                new ItemStack(Items.slime_ball)
        }));

        ModifyBuilder.registerModifier(new ModBlockPlacer(new ItemStack[]{
                new ItemStack(Blocks.dispenser)
        }));

        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[]{
                new ItemStack(Blocks.dragon_egg),
                new ItemStack(Items.diamond),
                new ItemStack(Blocks.gold_block)
        }, "Tier3Free"));
        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[]{
                new ItemStack(Blocks.dragon_egg),
                new ItemStack(Blocks.diamond_block),
                new ItemStack(Items.golden_apple, 1, 1)
        }, "Tier3.5Free"));
        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[]{
                new ItemStack(Blocks.dragon_egg),
                new ItemStack(Items.nether_star)
        }, "Tier4Free"));

        TConstructRegistry.registerActiveToolMod(new SneakListenerMod());
    }

    public static boolean useToolStationItem(ItemStack stack) {
        return stack == null || stack.getItem() != Item.getItemFromBlock(Blocks.dragon_egg);
    }

    public static String getModifierKey(Class<? extends ItemModifier> modifier) {
        for (ItemModifier itemModifier : ModifyBuilder.instance.itemModifiers) {
            if (modifier.isInstance(itemModifier)) return itemModifier.key;
        }

        return null;
    }

    public static Set<String> getModifierKeys() {
        Set<String> keys = new HashSet<String>();

        for (ItemModifier itemModifier : ModifyBuilder.instance.itemModifiers) keys.add(itemModifier.key);

        return keys;
    }

    public static <T extends NBTBase> T getModifierTag(ItemStack tool, Class<? extends ItemModifier> modifier) {
        if (tool == null || !tool.hasTagCompound() || !tool.getTagCompound().hasKey("InfiTool")) return null;

        NBTTagCompound infiTool = tool.getTagCompound().getCompoundTag("InfiTool");

        String key = getModifierKey(modifier);

        if (key != null && infiTool.hasKey(key)) {
            NBTBase nbt = infiTool.getTag(key);

            try {
                return (T) nbt;
            } catch (ClassCastException e) {
            }
        }

        return null;
    }

    public static Map<String, NBTBase> getModifierTags(ItemStack tool) {
        if (tool == null || !tool.hasTagCompound() || !tool.getTagCompound().hasKey("InfiTool")) return null;

        NBTTagCompound infiTool = tool.getTagCompound().getCompoundTag("InfiTool");

        Set<String> keys = getModifierKeys();

        Map<String, NBTBase> nbts = new HashMap<String, NBTBase>();

        for (String key : keys)
            if (key != null && infiTool.hasKey(key)) nbts.put(key, infiTool.getTag(key));

        return nbts;
    }
}
