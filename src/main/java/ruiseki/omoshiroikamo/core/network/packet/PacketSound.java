package ruiseki.omoshiroikamo.core.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;

/**
 * Packet for playing a sound at a location.
 * Override this to enable your mod.
 * 
 * @author rubensworks
 *
 */
public class PacketSound extends PacketCodec {

    private static final int RANGE = 15;

    @CodecField
    private double x = 0;
    @CodecField
    private double y = 0;
    @CodecField
    private double z = 0;
    @CodecField
    private String mod = "";
    @CodecField
    private String sound = "";
    @CodecField
    private float volume = 0;
    @CodecField
    private float frequency = 0;

    public PacketSound() {}

    /**
     * Creates a packet with coordinates.
     * 
     * @param x         The X coordinate.
     * @param y         The Y coordinate.
     * @param z         The Z coordinate.
     * @param sound     The sound name to play.
     * @param volume    The volume of the sound.
     * @param frequency The pitch of the sound.
     */
    public PacketSound(double x, double y, double z, String sound, float volume, float frequency) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sound = sound;
        this.volume = volume;
        this.frequency = frequency;
    }

    /**
     * Creates a packet with coordinates.
     * 
     * @param x         The X coordinate.
     * @param y         The Y coordinate.
     * @param z         The Z coordinate.
     * @param sound     The sound name to play.
     * @param volume    The volume of the sound.
     * @param frequency The pitch of the sound.
     * @param mod       The mod id that has this sound.
     */
    public PacketSound(double x, double y, double z, String sound, float volume, float frequency, String mod) {
        this(x, y, z, sound, volume, frequency);
        this.mod = mod;
    }

    /**
     * Creates a packet which contains the location data.
     * 
     * @param location  The location data.
     * @param sound     The sound name to play.
     * @param volume    The volume of the sound.
     * @param frequency The pitch of the sound.
     */
    public PacketSound(BlockPos location, String sound, float volume, float frequency) {
        this(location.getX(), location.getY(), location.getZ(), sound, volume, frequency);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        // TODO: add after complete proxy
        // CyclopsCore._instance.getProxy().playSound(x, y, z, sound, volume, frequency, mod);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        // CyclopsCore._instance.getPacketHandler().sendToAllAround(new PacketSound(x, y, z, sound, volume, frequency,
        // mod),
        // new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, RANGE));
    }
}
