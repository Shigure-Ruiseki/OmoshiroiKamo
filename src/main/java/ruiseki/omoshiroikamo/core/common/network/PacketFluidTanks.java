package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.fluid.SmartTank;
import ruiseki.omoshiroikamo.api.network.CodecField;
import ruiseki.omoshiroikamo.api.network.PacketCodec;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractStorageTE;

public class PacketFluidTanks extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private NBTTagCompound nbtRoot;

    public PacketFluidTanks() {}

    public PacketFluidTanks(AbstractStorageTE tile) {
        this.pos = tile.getPos();

        nbtRoot = new NBTTagCompound();
        NBTTagCompound tanksTag = new NBTTagCompound();

        for (int i = 0; i < tile.fluidTanks.size(); i++) {
            NBTTagCompound tankTag = new NBTTagCompound();
            tile.fluidTanks.get(i)
                .writeToNBT(tankTag);
            tanksTag.setTag("Tank" + i, tankTag);
        }

        nbtRoot.setTag("FluidTanks", tanksTag);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (!(te instanceof AbstractStorageTE storage)) return;

        NBTTagCompound tanksTag = nbtRoot.getCompoundTag("FluidTanks");

        for (int i = 0; i < storage.fluidTanks.size(); i++) {
            String key = "Tank" + i;
            SmartTank tank = storage.fluidTanks.get(i);

            if (tanksTag.hasKey(key)) {
                tank.readFromNBT(tanksTag.getCompoundTag(key));
            } else {
                tank.setFluid(null);
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
