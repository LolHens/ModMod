//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package baubles.common.event;

import baubles.api.IBauble;
import baubles.common.Baubles;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import com.google.common.io.Files;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;

import java.io.File;
import java.io.IOException;

public class EventHandlerEntity {
    public EventHandlerEntity() {
    }

    @SubscribeEvent
    public void playerTick(LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);

            for (int a = 0; a < baubles.getSizeInventory(); ++a) {
                if (baubles.getStackInSlot(a) != null && baubles.getStackInSlot(a).getItem() instanceof IBauble) {
                    ((IBauble) baubles.getStackInSlot(a).getItem()).onWornTick(baubles.getStackInSlot(a), player);
                }
            }
        }

    }

    @SubscribeEvent
    public void playerDeath(PlayerDropsEvent event) {
        if (event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote && !event.entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            PlayerHandler.getPlayerBaubles(event.entityPlayer).dropItemsAt(event.drops, event.entityPlayer);
        }

    }

    @SubscribeEvent
    public void playerLoad(LoadFromFile event) {
        PlayerHandler.clearPlayerBaubles(event.entityPlayer);
        File file1 = this.getPlayerFile("baub", event.playerDirectory, event.entityPlayer.getCommandSenderName());
        if (!file1.exists()) {
            File filep = event.getPlayerFile("baub");
            File filet;
            if (filep.exists()) {
                try {
                    Files.copy(filep, file1);
                    Baubles.log.info("Using and converting UUID Baubles savefile for " + event.entityPlayer.getCommandSenderName());
                    filep.delete();
                    filet = event.getPlayerFile("baubback");
                    if (filet.exists()) {
                        filet.delete();
                    }
                } catch (IOException var7) {
                    ;
                }
            } else {
                filet = getLegacyFileFromPlayer(event.entityPlayer);
                if (filet.exists()) {
                    try {
                        Files.copy(filet, file1);
                        Baubles.log.info("Using pre MC 1.7.10 Baubles savefile for " + event.entityPlayer.getCommandSenderName());
                    } catch (IOException var6) {
                        ;
                    }
                }
            }
        }

        PlayerHandler.loadPlayerBaubles(event.entityPlayer, file1, this.getPlayerFile("baubback", event.playerDirectory, event.entityPlayer.getCommandSenderName()));
        EventHandlerNetwork.syncBaubles(event.entityPlayer);
    }

    public File getPlayerFile(String suffix, File playerDirectory, String playername) {
        if ("dat".equals(suffix)) {
            throw new IllegalArgumentException("The suffix \'dat\' is reserved");
        } else {
            return new File(playerDirectory, playername + "." + suffix);
        }
    }

    public static File getLegacyFileFromPlayer(EntityPlayer player) {
        try {
            File e = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
            return new File(e, player.getCommandSenderName() + ".baub");
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    @SubscribeEvent
    public void playerSave(SaveToFile event) {
        PlayerHandler.savePlayerBaubles(event.entityPlayer, this.getPlayerFile("baub", event.playerDirectory, event.entityPlayer.getCommandSenderName()), this.getPlayerFile("baubback", event.playerDirectory, event.entityPlayer.getCommandSenderName()));
    }
}
