package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;

public class PacketCraftingState implements IMessage, IMessageHandler<PacketCraftingState, IMessage> {

    private BlockPos pos;
    private CraftingState craftingState = CraftingState.IDLE;

    public PacketCraftingState() {}

    public PacketCraftingState(ICraftingTile tile) {
        craftingState = tile.getCraftingState();
        pos = tile.getPos();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        craftingState = CraftingState.byIndex(buf.readInt());
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(craftingState.getIndex());
        buf.writeLong(pos.toLong());
    }

    @Override
    public IMessage onMessage(PacketCraftingState message, MessageContext ctx) {
        EntityPlayer player = OmoshiroiKamo.proxy.getClientPlayer();
        if (player == null) return null;
        TileEntity tile = player.worldObj.getTileEntity(message.pos.x, message.pos.y, message.pos.z);
        if (tile instanceof ICraftingTile te) {
            te.setCraftingState(message.craftingState);
        }
        return null;
    }
}
