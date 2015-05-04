//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.relic;

import baubles.api.BaubleType;
import baubles.common.lib.PlayerHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.ModItems;

public class ItemThorRing extends ItemRelicBauble {
    public ItemThorRing() {
        super("thorRing");
    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.RING;
    }

    public static ItemStack getThorRing(EntityPlayer player) {
        ItemStack[] baubles = PlayerHandler.getPlayerBaubles(player).getStacks();
        for (ItemStack stack : baubles) {
            if (isThorRing(stack)) return stack;
        }
        return null;
    }

    private static boolean isThorRing(ItemStack stack) {
        return stack != null && (stack.getItem() == ModItems.thorRing || stack.getItem() == ModItems.aesirRing);
    }
}
