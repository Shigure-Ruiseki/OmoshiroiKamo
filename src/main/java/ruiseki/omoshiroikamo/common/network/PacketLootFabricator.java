package ruiseki.omoshiroikamo.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.common.block.deepMobLearning.lootFabricator.TELootFabricator;

public class PacketLootFabricator extends MessageTileEntity<TELootFabricator>
    implements IMessageHandler<PacketLootFabricator, IMessage> {

    private ItemStack outputItem;

    public PacketLootFabricator() {}

    public PacketLootFabricator(TELootFabricator tile) {
        super(tile);
        outputItem = tile.getOutputItem();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        ByteBufUtils.writeItemStack(buf, outputItem);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        outputItem = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public IMessage onMessage(PacketLootFabricator message, MessageContext ctx) {
        EntityPlayer player = OmoshiroiKamo.proxy.getClientPlayer();
        TELootFabricator tile = message.getTileEntity(player.worldObj);
        if (tile == null) {
            return null;
        }
        tile.setOutputItem(message.outputItem);
        return null;
    }
}
