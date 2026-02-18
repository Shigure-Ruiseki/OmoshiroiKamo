package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.energy.IOKEnergyTile;
import ruiseki.omoshiroikamo.api.network.CodecField;
import ruiseki.omoshiroikamo.api.network.PacketCodec;

public class PacketEnergy extends PacketCodec {

    @CodecField
    private BlockPos pos;

    @CodecField
    private int storedEnergy;

    public PacketEnergy() {}

    public PacketEnergy(IOKEnergyTile tile) {
        storedEnergy = tile.getEnergyStored();
        pos = tile.getPos();
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (te instanceof IOKEnergyTile energyTile) {
            energyTile.setEnergyStored(storedEnergy);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
