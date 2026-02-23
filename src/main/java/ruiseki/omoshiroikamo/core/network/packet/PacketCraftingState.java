package ruiseki.omoshiroikamo.core.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.CraftingState;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;
import ruiseki.omoshiroikamo.core.tileentity.ICraftingTile;

public class PacketCraftingState extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private int craftingState = 0;

    public PacketCraftingState() {}

    public PacketCraftingState(ICraftingTile tile) {
        craftingState = tile.getCraftingState()
            .getIndex();
        pos = tile.getPos();
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

        TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (tile instanceof ICraftingTile te) {
            te.setCraftingState(CraftingState.byIndex(craftingState));
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
