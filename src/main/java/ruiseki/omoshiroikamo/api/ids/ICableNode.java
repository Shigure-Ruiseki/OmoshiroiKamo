package ruiseki.omoshiroikamo.api.ids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.ICapabilityProvider;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
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
    @Nullable
    default <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        return null;
    }

    @Override
    default boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        return false;
    }
}
