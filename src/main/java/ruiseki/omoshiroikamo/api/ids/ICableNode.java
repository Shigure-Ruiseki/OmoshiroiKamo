package ruiseki.omoshiroikamo.api.ids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractCableNetwork;

public interface ICableNode {

    String getId();

    ICable getCable();

    List<Class<? extends ICableNode>> getBaseNodeTypes();

    ForgeDirection getSide();

    BlockPos getPos();

    EnumIO getIO();

    TileEntity getTargetTE();

    void setSide(ForgeDirection side);

    void setCable(ICable cable, ForgeDirection side);

    void onAttached();

    void onDetached();

    int getTickInterval();

    void setTickInterval(int tickInterval);

    int getPriority();

    void setPriority(int priority);

    int getChannel();

    void setChannel(int chanel);

    default AbstractCableNetwork<?> getNetwork(Class<? extends ICableNode> network) {
        ICable cable = getCable();
        return cable != null ? cable.getNetwork(network) : null;
    }
}
