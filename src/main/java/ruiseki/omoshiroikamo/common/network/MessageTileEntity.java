package ruiseki.omoshiroikamo.common.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.api.block.BlockPos;

public abstract class MessageTileEntity<T extends TileEntity> implements IMessage {

    protected BlockPos pos;
    protected int dimension;

    protected MessageTileEntity() {}

    protected MessageTileEntity(T tile) {
        this.pos = new BlockPos(tile);
        this.dimension = tile.getWorldObj().provider.dimensionId;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
    }

    @SuppressWarnings("unchecked")
    protected T getTileEntity(World world) {
        if (world == null || pos == null) {
            return null;
        }

        TileEntity te = pos.getTileEntity(world);

        if (te == null) {
            return null;
        }

        TypeToken<?> teType = TypeToken.of(getClass())
            .resolveType(MessageTileEntity.class.getTypeParameters()[0]);

        if (teType.isAssignableFrom(te.getClass())) {
            return (T) te;
        }

        return null;
    }

    protected World getWorld(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity.worldObj;
    }
}
