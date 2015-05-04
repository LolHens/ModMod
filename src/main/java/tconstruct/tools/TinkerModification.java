package tconstruct.tools;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.armor.TinkerArmor;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.modifiers.tools.*;

/**
 * Created by LolHens on 30.04.2015.
 */
@GameRegistry.ObjectHolder("TConstruct")
@Pulse(
        id = "Tinkers\' Modification",
        description = "A Modification for Tinkers\' Construct."
)
public class TinkerModification {
    @Handler
    public void init(FMLInitializationEvent event) {
        ModifyBuilder.registerModifier(new ModSneakDetector(new ItemStack[]{new ItemStack(Items.comparator)}));
        ModifyBuilder.registerModifier(new ModRange(new ItemStack[]{new ItemStack(Items.ender_eye), new ItemStack(TinkerArmor.diamondApple)}));
        ModifyBuilder.registerModifier(new ModDepth(new ItemStack[]{new ItemStack(Items.ender_eye), new ItemStack(Items.golden_apple, 1, 1)}));
        ModifyBuilder.registerModifier(new ModConvenient(new ItemStack[]{new ItemStack(Items.apple), new ItemStack(Items.golden_apple), new ItemStack(TinkerArmor.diamondApple)}));
        ModifyBuilder.registerModifier(new ModFlightSpeed(new ItemStack[]{new ItemStack(Items.slime_ball)}));

        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[]{new ItemStack(Blocks.dragon_egg), new ItemStack(Items.diamond), new ItemStack(Blocks.gold_block)}, "Tier3Free"));
        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[]{new ItemStack(Blocks.dragon_egg), new ItemStack(Blocks.diamond_block), new ItemStack(Items.golden_apple, 1, 1)}, "Tier3.5Free"));
        ModifyBuilder.registerModifier(new ModExtraModifier(new ItemStack[]{new ItemStack(Blocks.dragon_egg), new ItemStack(Items.nether_star)}, "Tier4Free"));
    }

    public static boolean useToolStationItem(ItemStack stack) {
        return stack == null || stack.getItem() != Item.getItemFromBlock(Blocks.dragon_egg);
    }
}
