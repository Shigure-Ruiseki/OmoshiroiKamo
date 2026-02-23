package ruiseki.omoshiroikamo.core.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.energy.IOKEnergyTile;
import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;

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
