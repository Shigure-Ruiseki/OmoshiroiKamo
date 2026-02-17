package ruiseki.omoshiroikamo.module.machinery.common.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.network.CodecField;
import ruiseki.omoshiroikamo.api.network.PacketCodec;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

/**
 * Packet to synchronize structure tint colors from server to client.
 * Sent when a structure is formed (to set color) or unformed (to clear color).
 */
public class PacketStructureTint extends PacketCodec {

    @CodecField
    private int dimensionId;

    @CodecField
    private int color;

    @CodecField
    private boolean clear;

    @CodecField
    private List<ChunkCoordinates> positions;

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
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        if (world == null || world.provider.dimensionId != dimensionId) {
            return;
        }

        if (clear) {
            // Clear colors and trigger re-render
            StructureTintCache.clearAll(world, positions);
        } else {
            // Set colors
            for (ChunkCoordinates pos : positions) {
                StructureTintCache.put(world, pos.posX, pos.posY, pos.posZ, color);
            }
        }

        // Trigger block re-renders
        for (ChunkCoordinates pos : positions) {
            world.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
