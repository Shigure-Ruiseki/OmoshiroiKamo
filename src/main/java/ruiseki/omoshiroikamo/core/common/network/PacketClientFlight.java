package ruiseki.omoshiroikamo.core.common.network;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;

public class PacketClientFlight implements IMessage, IMessageHandler<PacketClientFlight, IMessage> {

    protected UUID player;
    protected boolean enabled;

    public PacketClientFlight() {}

    public PacketClientFlight(UUID player, boolean enabledFlight) {
        this.player = player;
        this.enabled = enabledFlight;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.enabled = buf.readBoolean();
        long msb = buf.readLong();
        long lsb = buf.readLong();
        this.player = new UUID(msb, lsb);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.enabled);
        buf.writeLong(this.player.getMostSignificantBits());
        buf.writeLong(this.player.getLeastSignificantBits());
    }

    @Override
    public IMessage onMessage(PacketClientFlight message, MessageContext ctx) {
        EntityPlayer plr = PlayerUtils
            .getPlayerFromWorldClient(Minecraft.getMinecraft().thePlayer.worldObj, message.player);

        if (plr != null && plr.getUniqueID()
            .equals(message.player)) {
            plr.capabilities.allowFlying = message.enabled;
            if (plr.capabilities.isFlying && !message.enabled) {
                plr.capabilities.isFlying = false;
            }
        }

        return null;
    }
}
