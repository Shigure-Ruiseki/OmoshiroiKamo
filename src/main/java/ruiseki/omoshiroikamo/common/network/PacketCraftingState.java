package ruiseki.omoshiroikamo.common.network;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;

public class PacketCraftingState extends MessageTileEntity<TileEntity>
    implements IMessage, IMessageHandler<PacketCraftingState, IMessage> {

    private CraftingState craftingState = CraftingState.IDLE;

    public PacketCraftingState() {}

    public PacketCraftingState(ICraftingTile tile) {
        super(tile.getTileEntity());
        craftingState = tile.getCraftingState();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        craftingState = CraftingState.byIndex(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(craftingState.getIndex());
    }

    @Override
    public IMessage onMessage(PacketCraftingState message, MessageContext ctx) {
        TileEntity te = message.getTileEntity(OmoshiroiKamo.proxy.getClientWorld());
        if (te instanceof ICraftingTile me) {
            me.setCraftingState(message.craftingState);
        }
        return null;
    }
}
