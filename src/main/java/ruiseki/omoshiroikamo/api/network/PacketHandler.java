package ruiseki.omoshiroikamo.api.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import com.ibm.icu.impl.Pair;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.ChannelHandler.Sharable;
import ruiseki.omoshiroikamo.api.util.MinecraftHelpers;

/**
 * Advanced packet handler of {@link PacketBase} instances.
 * An alternative would be {@link SimpleNetworkWrapper}.
 * Partially based on the SecretRooms mod packet handling:
 * https://github.com/AbrarSyed/SecretRoomsMod-forge
 * 
 * @author rubensworks
 *
 */
public class PacketHandler {

    private static Map<Pair<String, IDType>, Integer> ID_COUNTER = new HashMap<Pair<String, IDType>, Integer>();

    private SimpleNetworkWrapper networkWrapper = null;

    @SideOnly(Side.CLIENT)
    private HandlerClient handlerClient;

    private HandlerServer handlerServer;

    private final String mod;

    public PacketHandler(String mod) {
        this.mod = mod;
    }

    public void init() {
        if (networkWrapper == null) {
            networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(mod);
            if (MinecraftHelpers.isClientSide()) {
                handlerClient = new HandlerClient();
            }
            handlerServer = new HandlerServer();
        }
    }

    /**
     * Register a new packet.
     * 
     * @param packetType The class of the packet.
     */
    public void register(Class<? extends PacketBase> packetType) {
        int discriminator = getNewId(mod, IDType.PACKET);
        if (MinecraftHelpers.isClientSide()) {
            networkWrapper.registerMessage(handlerClient, packetType, discriminator, Side.CLIENT);
        }
        networkWrapper.registerMessage(handlerServer, packetType, discriminator, Side.SERVER);
    }

    /**
     * Send a packet to the server.
     * 
     * @param packet The packet.
     */
    public void sendToServer(PacketBase packet) {
        networkWrapper.sendToServer(packet);
    }

    /**
     * Send a packet to the player.
     * 
     * @param packet The packet.
     * @param player The player.
     */
    public void sendToPlayer(PacketBase packet, EntityPlayerMP player) {
        networkWrapper.sendTo(packet, player);
    }

    /**
     * Send a packet to all in the target range.
     * 
     * @param packet The packet.
     * @param point  The area to send to.
     */
    public void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point) {
        networkWrapper.sendToAllAround(packet, point);
    }

    /**
     * Send a packet to all players around a TileEntity.
     * 
     * @param packet The packet.
     */
    public void sendToAllAround(PacketBase packet, TileEntity te, int range) {
        sendToAllAround(
            packet,
            new NetworkRegistry.TargetPoint(
                te.getWorldObj().provider.dimensionId,
                te.xCoord + 0.5,
                te.yCoord + 0.5,
                te.zCoord + 0.5,
                range));
    }

    public void sendToAllAround(PacketBase packet, TileEntity te) {
        sendToAllAround(packet, te, 64);
    }

    /**
     * Send a packet to all players around a player.
     */
    public void sendToAllAround(PacketBase packet, EntityPlayer player, double range) {
        sendToAllAround(
            packet,
            new NetworkRegistry.TargetPoint(
                player.worldObj.provider.dimensionId,
                player.posX,
                player.posY,
                player.posZ,
                range));
    }

    public void sendToAllAround(PacketBase packet, EntityPlayer player) {
        sendToAllAround(packet, player, 64);
    }

    /**
     * Send a packet to everything in the given dimension.
     * 
     * @param packet    The packet.
     * @param dimension The dimension to send to.
     */
    public void sendToDimension(PacketBase packet, int dimension) {
        networkWrapper.sendToDimension(packet, dimension);
    }

    /**
     * Send a packet to everything.
     * 
     * @param packet The packet.
     */
    public void sendToAll(PacketBase packet) {
        networkWrapper.sendToAll(packet);
    }

    /**
     * Convert the given packet to a minecraft packet.
     * 
     * @param packet The packet.
     * @return The minecraft packet.
     */
    public Packet toMcPacket(PacketBase packet) {
        return networkWrapper.getPacketFrom(packet);
    }

    @Sharable
    @SideOnly(Side.CLIENT)
    private static final class HandlerClient implements IMessageHandler<PacketBase, IMessage> {

        @Override
        public IMessage onMessage(final PacketBase packet, MessageContext ctx) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (packet.isAsync()) {
                packet.actionClient(mc.theWorld, mc.thePlayer);
            } else {
                mc.func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        packet.actionClient(mc.theWorld, mc.thePlayer);
                    }
                });
            }
            return null;
        }
    }

    @Sharable
    private static final class HandlerServer implements IMessageHandler<PacketBase, IMessage> {

        @Override
        public IMessage onMessage(PacketBase packet, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                // nothing on the client thread
                return null;
            }

            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            packet.actionServer(player.worldObj, player);
            return null;
        }
    }

    public static int getNewId(String mod, IDType type) {
        Integer ID = ID_COUNTER.get(Pair.of(mod, type));
        if (ID == null) ID = 0;
        ID_COUNTER.put(Pair.of(mod, type), ID + 1);
        return ID;
    }

    public enum IDType {
        /**
         * Entity ID.
         */
        ENTITY,
        /**
         * GUI ID.
         */
        GUI,
        /**
         * Packet ID.
         */
        PACKET
    }
}
