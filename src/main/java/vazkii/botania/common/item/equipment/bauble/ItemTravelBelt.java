//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vazkii.botania.common.item.equipment.bauble;

import baubles.api.BaubleType;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.item.IBaubleRender;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ItemTravelBelt extends ItemBauble implements IBaubleRender, IManaUsingItem {
    private static final ResourceLocation texture = new ResourceLocation("botania:textures/model/travelBelt.png");
    @SideOnly(Side.CLIENT)
    private static ModelBiped model;
    private static final int COST = 1;
    private static final int COST_INTERVAL = 10;
    public static List<String> playersWithStepup = new ArrayList();
    final float speed;
    final float jump;
    final float fallBuffer;

    public ItemTravelBelt() {
        this("travelBelt", 0.035F, 0.2F, 2.0F);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public ItemTravelBelt(String name, float speed, float jump, float fallBuffer) {
        super(name);
        this.speed = speed;
        this.jump = jump;
        this.fallBuffer = fallBuffer;
    }

    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    private ItemStack getStack(EntityPlayer player) {
        for (ItemStack stack : PlayerHandler.getPlayerBaubles(player).getStacks())
            if (stack.getItem() == this)
                return stack;
        return null;
    }

    @SubscribeEvent
    public void updatePlayerStepStatus(LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            String s = playerStr(player);
            ItemStack belt = getStack(player);

            if (playersWithStepup.contains(s)) {
                if (this.shouldPlayerHaveStepup(player)) {
                    ItemTravelBelt beltItem = (ItemTravelBelt) belt.getItem();
                    if ((player.onGround || player.capabilities.isFlying) && player.moveForward > 0.0F && !player.isInsideOfMaterial(Material.water)) {
                        float speed = beltItem.getSpeed(belt);
                        player.moveFlying(0.0F, 1.0F, player.capabilities.isFlying ? speed : speed);
                        beltItem.onMovedTick(belt, player);
                        if (player.ticksExisted % 10 == 0) {
                            ManaItemHandler.requestManaExact(belt, player, 1, true);
                        }
                    } else {
                        beltItem.onNotMovingTick(belt, player);
                    }

                    if (player.isSneaking()) {
                        player.stepHeight = 0.50001F;
                    } else {
                        player.stepHeight = 1.0F;
                    }
                } else {
                    player.stepHeight = 0.5F;
                    playersWithStepup.remove(s);
                }
            } else if (this.shouldPlayerHaveStepup(player)) {
                playersWithStepup.add(s);
                player.stepHeight = 1.0F;
            }
        }

    }

    public float getSpeed(ItemStack stack) {
        return this.speed;
    }

    public void onMovedTick(ItemStack stack, EntityPlayer player) {
    }

    public void onNotMovingTick(ItemStack stack, EntityPlayer player) {
    }

    @SubscribeEvent
    public void onPlayerJump(LivingJumpEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            ItemStack belt = getStack(player);
            if (belt != null && belt.getItem() instanceof ItemTravelBelt && ManaItemHandler.requestManaExact(belt, player, 1, false)) {
                player.motionY += (double) ((ItemTravelBelt) belt.getItem()).jump;
                player.fallDistance = -((ItemTravelBelt) belt.getItem()).fallBuffer;
            }
        }

    }

    private boolean shouldPlayerHaveStepup(EntityPlayer player) {
        ItemStack armor = getStack(player);
        return armor != null && armor.getItem() instanceof ItemTravelBelt && ManaItemHandler.requestManaExact(armor, player, 1, false);
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerLoggedOutEvent event) {
        String username = event.player.getGameProfile().getName();
        playersWithStepup.remove(username + ":false");
        playersWithStepup.remove(username + ":true");
    }

    public static String playerStr(EntityPlayer player) {
        return player.getGameProfile().getName() + ":" + player.worldObj.isRemote;
    }

    @SideOnly(Side.CLIENT)
    ResourceLocation getRenderTexture() {
        return texture;
    }

    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, RenderType type) {
        if (type == RenderType.BODY) {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.getRenderTexture());
            Helper.rotateIfSneaking(event.entityPlayer);
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
            float s = 0.065625F;
            GL11.glScalef(s, s, s);
            if (model == null) {
                model = new ModelBiped();
            }

            model.bipedBody.render(1.0F);
        }

    }

    public boolean usesMana(ItemStack stack) {
        return true;
    }
}
