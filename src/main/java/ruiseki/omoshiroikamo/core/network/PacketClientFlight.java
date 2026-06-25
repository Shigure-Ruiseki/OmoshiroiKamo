package ruiseki.omoshiroikamo.core.network;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketClientFlight extends PacketCodec {

    @CodecField
    private UUID player;

    @CodecField
    private boolean enabled;

    public PacketClientFlight() {}

    public PacketClientFlight(UUID player, boolean enabledFlight) {
        this.player = player;
        this.enabled = enabledFlight;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer clientPlayer) {

        EntityPlayer target = PlayerHelpers.getPlayerFromWorldClient(world, player);

        if (target == null) return;

        target.capabilities.allowFlying = enabled;

        if (target.capabilities.isFlying && !enabled) {
            target.capabilities.isFlying = false;
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
