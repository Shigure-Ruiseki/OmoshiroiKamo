package ruiseki.omoshiroikamo.api.ids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractCableNetwork;

public interface ICableNode extends ICapabilityProvider {

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

    @Override
    @NotNull
    default <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        return LazyOptional.empty();
    }
}
