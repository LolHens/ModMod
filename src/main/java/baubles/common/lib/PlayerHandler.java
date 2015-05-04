//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package baubles.common.lib;

import baubles.common.Baubles;
import baubles.common.container.InventoryBaubles;
import baubles.common.container.InventoryBaublesExtended;
import com.google.common.io.Files;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

public class PlayerHandler {
    private static HashMap<String, InventoryBaubles> playerBaubles = new HashMap();

    public PlayerHandler() {
    }

    public static void clearPlayerBaubles(EntityPlayer player) {
        playerBaubles.remove(player.getCommandSenderName());
    }

    public static InventoryBaubles getPlayerBaubles(EntityPlayer player) {
        if (!playerBaubles.containsKey(player.getCommandSenderName())) {
            InventoryBaubles inventory = new InventoryBaublesExtended(player);
            playerBaubles.put(player.getCommandSenderName(), inventory);
        }

        return playerBaubles.get(player.getCommandSenderName());
    }

    public static void setPlayerBaubles(EntityPlayer player, InventoryBaubles inventory) {
        playerBaubles.put(player.getCommandSenderName(), inventory);
    }

    public static void loadPlayerBaubles(EntityPlayer player, File file1, File file2) {
        if (player != null && !player.worldObj.isRemote) {
            try {
                NBTTagCompound exception1 = null;
                boolean save = false;
                FileInputStream inventory;
                if (file1 != null && file1.exists()) {
                    try {
                        inventory = new FileInputStream(file1);
                        exception1 = CompressedStreamTools.readCompressed(inventory);
                        inventory.close();
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }
                }

                if (file1 == null || !file1.exists() || exception1 == null || exception1.hasNoTags()) {
                    Baubles.log.warn("Data not found for " + player.getCommandSenderName() + ". Trying to load backup data.");
                    if (file2 != null && file2.exists()) {
                        try {
                            inventory = new FileInputStream(file2);
                            exception1 = CompressedStreamTools.readCompressed(inventory);
                            inventory.close();
                            save = true;
                        } catch (Exception var6) {
                            var6.printStackTrace();
                        }
                    }
                }

                if (exception1 != null) {
                    InventoryBaubles inventory1 = new InventoryBaublesExtended(player);
                    inventory1.readNBT(exception1);
                    playerBaubles.put(player.getCommandSenderName(), inventory1);
                    if (save) {
                        savePlayerBaubles(player, file1, file2);
                    }
                }
            } catch (Exception var8) {
                Baubles.log.fatal("Error loading baubles inventory");
                var8.printStackTrace();
            }
        }

    }

    public static void savePlayerBaubles(EntityPlayer player, File file1, File file2) {
        if (player != null && !player.worldObj.isRemote) {
            try {
                if (file1 != null && file1.exists()) {
                    try {
                        Files.copy(file1, file2);
                    } catch (Exception var7) {
                        Baubles.log.error("Could not backup old baubles file for player " + player.getCommandSenderName());
                    }
                }

                try {
                    if (file1 != null) {
                        InventoryBaubles exception1 = getPlayerBaubles(player);
                        NBTTagCompound e2 = new NBTTagCompound();
                        exception1.saveNBT(e2);
                        FileOutputStream fileoutputstream = new FileOutputStream(file1);
                        CompressedStreamTools.writeCompressed(e2, fileoutputstream);
                        fileoutputstream.close();
                    }
                } catch (Exception var8) {
                    Baubles.log.error("Could not save baubles file for player " + player.getCommandSenderName());
                    var8.printStackTrace();
                    if (file1.exists()) {
                        try {
                            file1.delete();
                        } catch (Exception var6) {
                            ;
                        }
                    }
                }
            } catch (Exception var9) {
                Baubles.log.fatal("Error saving baubles inventory");
                var9.printStackTrace();
            }
        }

    }
}
