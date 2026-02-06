package ruiseki.omoshiroikamo.module.machinery.common.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ChunkCoordinates;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;

/**
 * Packet to synchronize structure tint colors from server to client.
 * Sent when a structure is formed (to set color) or unformed (to clear color).
 */
public class PacketStructureTint implements IMessage {

    private int dimensionId;
    private int color;
    private boolean clear;
    private List<ChunkCoordinates> positions;

    public int getDimensionId() {
        return dimensionId;
    }

    public int getColor() {
        return color;
    }

    public boolean isClear() {
        return clear;
    }

    public List<ChunkCoordinates> getPositions() {
        return positions;
    }

    public PacketStructureTint() {
        this.positions = new ArrayList<>();
    }

    /**
     * Create a packet to set tint color for positions.
     */
    public PacketStructureTint(int dimensionId, int color, List<ChunkCoordinates> positions) {
        this.dimensionId = dimensionId;
        this.color = color;
        this.clear = false;
        this.positions = new ArrayList<>(positions);
    }

    /**
     * Create a packet to clear tint color for positions.
     */
    public static PacketStructureTint createClear(int dimensionId, List<ChunkCoordinates> positions) {
        PacketStructureTint packet = new PacketStructureTint();
        packet.dimensionId = dimensionId;
        packet.color = 0;
        packet.clear = true;
        packet.positions = new ArrayList<>(positions);
        return packet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionId = buf.readInt();
        color = buf.readInt();
        clear = buf.readBoolean();

        int count = buf.readInt();
        positions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            positions.add(new ChunkCoordinates(x, y, z));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(color);
        buf.writeBoolean(clear);

        buf.writeInt(positions.size());
        for (ChunkCoordinates pos : positions) {
            buf.writeInt(pos.posX);
            buf.writeInt(pos.posY);
            buf.writeInt(pos.posZ);
        }
    }

    public static class Handler implements IMessageHandler<PacketStructureTint, IMessage> {

        @Override
        public IMessage onMessage(PacketStructureTint message, MessageContext ctx) {
            // Handle via proxy to avoid server-side class loading issues with client-only
            // classes
            OmoshiroiKamo.proxy.handleStructureTint(message);
            return null;
        }
    }
}
