package ruiseki.omoshiroikamo.module.cows.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.network.CodecField;
import ruiseki.omoshiroikamo.api.network.PacketCodec;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;

public class PacketStall extends PacketCodec {

    @CodecField
    private NBTTagCompound nbtRoot;

    @CodecField
    protected BlockPos pos;

    public PacketStall() {}

    public PacketStall(TEStall tile) {
        nbtRoot = new NBTTagCompound();
        if (tile.tank.getFluidAmount() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            tile.tank.writeToNBT(tankRoot);
            nbtRoot.setTag("tank", tankRoot);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        TEStall tile = (TEStall) world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (nbtRoot.hasKey("tank")) {
            NBTTagCompound tankRoot = nbtRoot.getCompoundTag("tank");
            tile.tank.readFromNBT(tankRoot);
        } else {
            tile.tank.setFluid(null);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
