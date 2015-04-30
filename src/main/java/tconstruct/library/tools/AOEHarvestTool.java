//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tconstruct.library.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import tconstruct.modifiers.tools.ModDepth;
import tconstruct.modifiers.tools.ModRange;
import tconstruct.modifiers.tools.ModSneakDetector;
import tconstruct.modifiers.tools.ModUniversal;
import tconstruct.tools.TinkerModification;

public abstract class AOEHarvestTool extends HarvestTool {
    public int breakRadius;
    public int breakDepth;

    public AOEHarvestTool(int baseDamage, int breakRadius, int breakDepth) {
        super(baseDamage);
        this.breakRadius = breakRadius;
        this.breakDepth = breakDepth;
    }

    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        Block block = player.worldObj.getBlock(x, y, z);
        int meta = player.worldObj.getBlockMetadata(x, y, z);
        if (block != null && (ModUniversal.isUniversal(stack) || this.isEffective(block, meta))) {
            MovingObjectPosition mop = AbilityHelper.raytraceFromEntity(player.worldObj, player, false, 4.5D);
            if (mop == null) {
                return super.onBlockStartBreak(stack, x, y, z, player);
            } else {
                if (ModSneakDetector.isAOE(stack, player)) {
                    int radiusIncrease = ModRange.getRangeIncrease(stack);
                    int depthIncrease = ModDepth.getDepthIncrease(stack);

                    int sideHit = mop.sideHit;
                    int xRange = this.breakRadius + radiusIncrease;
                    int yRange = this.breakRadius + radiusIncrease;
                    int zRange = this.breakDepth + depthIncrease;
                    switch (sideHit) {
                        case 0:
                        case 1:
                            yRange = this.breakDepth + depthIncrease;
                            zRange = this.breakRadius + radiusIncrease;
                            break;
                        case 2:
                        case 3:
                            xRange = this.breakRadius + radiusIncrease;
                            zRange = this.breakDepth + depthIncrease;
                            break;
                        case 4:
                        case 5:
                            xRange = this.breakDepth + depthIncrease;
                            zRange = this.breakRadius + radiusIncrease;
                    }

                    for (int xPos = x - xRange; xPos <= x + xRange; ++xPos) {
                        for (int yPos = y - yRange; yPos <= y + yRange; ++yPos) {
                            for (int zPos = z - zRange; zPos <= z + zRange; ++zPos) {
                                if ((xPos != x || yPos != y || zPos != z) && !super.onBlockStartBreak(stack, xPos, yPos, zPos, player)) {
                                    this.breakExtraBlock(player.worldObj, xPos, yPos, zPos, sideHit, player, x, y, z);
                                }
                            }
                        }
                    }
                }

                return super.onBlockStartBreak(stack, x, y, z, player);
            }
        } else {
            return super.onBlockStartBreak(stack, x, y, z, player);
        }
    }
}
