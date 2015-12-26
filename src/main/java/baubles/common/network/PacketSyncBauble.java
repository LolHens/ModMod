//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package baubles.common.network;

import baubles.common.Baubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.io.IOException;

public class PacketSyncBauble implements IMessage, IMessageHandler<PacketSyncBauble, IMessage> {
    int slot;
    int playerId;
    ItemStack bauble = null;

    public PacketSyncBauble() {
    }

    public PacketSyncBauble(EntityPlayer player, int slot) {
        this.slot = slot;
        this.bauble = PlayerHandler.getPlayerBaubles(player).getStackInSlot(slot);
        this.playerId = player.getEntityId();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(this.slot);
        buffer.writeInt(this.playerId);
        PacketBuffer pb = new PacketBuffer(buffer);

        try {
            pb.writeItemStackToBuffer(this.bauble);
        } catch (IOException var4) {
            ;
        }

    }

    public void fromBytes(ByteBuf buffer) {
        this.slot = buffer.readByte();
        this.playerId = buffer.readInt();
        PacketBuffer pb = new PacketBuffer(buffer);

        try {
            this.bauble = pb.readItemStackFromBuffer();
        } catch (IOException var4) {
            ;
        }

    }

    public IMessage onMessage(PacketSyncBauble message, MessageContext ctx) {
        World world = Baubles.proxy.getClientWorld();
        if (world == null) {
            return null;
        } else {
            Entity p = world.getEntityByID(message.playerId);
            if (p != null && p instanceof EntityPlayer) {
                PlayerHandler.getPlayerBaubles((EntityPlayer) p).setInventorySlotContents(message.slot, message.bauble);
            }

            return null;
        }
    }
}
