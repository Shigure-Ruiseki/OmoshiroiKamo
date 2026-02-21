package ruiseki.omoshiroikamo.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/**
 * The base packet for packets.
 *
 * @author rubensworks
 *
 */
public abstract class PacketBase implements IMessage {

    /**
     * @return If this packet can run on a thread other than the main-thread of Minecraft.
     *         If this is asynchronous, the player parameter inside the action is not guaranteed to be defined.
     */
    public abstract boolean isAsync();

    /**
     * Encode this packet.
     *
     * @param output The byte array to encode to.
     */
    public abstract void encode(ByteArrayDataOutput output);

    /**
     * Decode for this packet.
     *
     * @param input The byte array to decode from.
     */
    public abstract void decode(ByteArrayDataInput input);

    /**
     * Actions for client-side.
     *
     * @param world  The world.
     * @param player The player. Can be null if this packet is asynchronous.
     */
    @SideOnly(Side.CLIENT)
    public abstract void actionClient(World world, EntityPlayer player);

    /**
     * Actions for server-side.
     *
     * @param world  The world.
     * @param player The player.
     */
    public abstract void actionServer(World world, EntityPlayerMP player);

    @Override
    public void fromBytes(ByteBuf source) {
        byte[] bytes = new byte[source.readableBytes()];
        source.readBytes(bytes);
        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        decode(input);
    }

    @Override
    public void toBytes(ByteBuf target) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        encode(output);
        target.writeBytes(output.toByteArray());
    }
}
