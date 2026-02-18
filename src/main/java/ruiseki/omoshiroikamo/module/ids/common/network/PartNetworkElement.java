package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import ruiseki.omoshiroikamo.api.block.DimPos;
import ruiseki.omoshiroikamo.api.ids.network.IEnergyConsumingNetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.IPartNetwork;
import ruiseki.omoshiroikamo.api.ids.network.IPartNetworkElement;
import ruiseki.omoshiroikamo.api.ids.part.IPartContainer;
import ruiseki.omoshiroikamo.api.ids.part.IPartContainerFacade;
import ruiseki.omoshiroikamo.api.ids.part.IPartState;
import ruiseki.omoshiroikamo.api.ids.part.IPartType;
import ruiseki.omoshiroikamo.api.ids.part.PartTarget;
import ruiseki.omoshiroikamo.module.ids.common.util.CableHelpers;

/**
 * A network element for parts.
 *
 * @author rubensworks
 */
@Data
public class PartNetworkElement<P extends IPartType<P, S>, S extends IPartState<P>>
    implements IPartNetworkElement<P, S>, IEnergyConsumingNetworkElement<IPartNetwork> {

    private final P part;
    private final PartTarget target;

    protected static DimPos getCenterPos(PartTarget target) {
        return target.getCenter()
            .getPos();
    }

    protected static ForgeDirection getCenterSide(PartTarget target) {
        return target.getCenter()
            .getSide();
    }

    protected static DimPos getTargetPos(PartTarget target) {
        return target.getTarget()
            .getPos();
    }

    protected static ForgeDirection getTargetSide(PartTarget target) {
        return target.getTarget()
            .getSide();
    }

    @Override
    public IPartContainerFacade getPartContainerFacade() {
        return CableHelpers.getInterface(getCenterPos(getTarget()), IPartContainerFacade.class);
    }

    @Override
    public S getPartState() {
        IPartContainerFacade partContainerFacade = getPartContainerFacade();
        DimPos dimPos = getCenterPos(getTarget());
        if (partContainerFacade != null) {
            IPartContainer partContainer = partContainerFacade
                .getPartContainer(getCenterPos(getTarget()).getWorld(), dimPos.getBlockPos());
            if (partContainer != null) {
                return (S) partContainer.getPartState(getCenterSide(getTarget()));
            } else {
                throw new IllegalStateException(String.format("The part container at %s could not be found.", dimPos));
            }
        } else {
            throw new IllegalStateException(
                String.format(
                    "The part container facade at %s could not be found, instead %s was found.",
                    dimPos,
                    dimPos.getBlockPos()
                        .getBlock(dimPos.getWorld())));
        }
    }

    @Override
    public int getConsumptionRate() {
        return getPart().getConsumptionRate(getPartState());
    }

    @Override
    public void postUpdate(IPartNetwork network, boolean updated) {
        part.postUpdate(network, getTarget(), getPartState(), updated);
    }

    @Override
    public int getUpdateInterval() {
        return part.getUpdateInterval(getPartState());
    }

    @Override
    public boolean isUpdate() {
        return part.isUpdate(getPartState());
    }

    @Override
    public void update(IPartNetwork network) {
        part.update(network, getTarget(), getPartState());
    }

    @Override
    public void beforeNetworkKill(IPartNetwork network) {
        part.beforeNetworkKill(network, target, getPartState());
    }

    @Override
    public void afterNetworkAlive(IPartNetwork network) {
        part.afterNetworkAlive(network, target, getPartState());
    }

    @Override
    public void afterNetworkReAlive(IPartNetwork network) {
        part.afterNetworkReAlive(network, target, getPartState());
    }

    @Override
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement) {
        part.addDrops(getTarget(), getPartState(), itemStacks, dropMainElement);
    }

    @Override
    public boolean onNetworkAddition(IPartNetwork network) {
        boolean res = network.addPart(getPartState().getId(), getTarget().getCenter());
        if (res) {
            part.onNetworkAddition(network, target, getPartState());
        }
        return res;
    }

    @Override
    public void onNetworkRemoval(IPartNetwork network) {
        network.removePart(getPartState().getId());
        part.onNetworkRemoval(network, target, getPartState());
    }

    @Override
    public void onPreRemoved(IPartNetwork network) {
        part.onPreRemoved(network, target, getPartState());
    }

    @Override
    public void onNeighborBlockChange(IPartNetwork network, IBlockAccess world, Block neighborBlock) {
        part.onBlockNeighborChange(network, target, getPartState(), world, neighborBlock);
    }

    @Override
    public P getNetworkEventListener() {
        return getPart();
    }

    public boolean equals(Object o) {
        return o instanceof IPartNetworkElement && compareTo((INetworkElement) o) == 0;
    }

    @Override
    public int hashCode() {
        int result = part.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }

    @Override
    public int compareTo(INetworkElement o) {
        if (o instanceof IPartNetworkElement) {
            IPartNetworkElement p = (IPartNetworkElement) o;
            int compPart = Integer.compare(
                part.hashCode(),
                p.getPart()
                    .hashCode());
            if (compPart == 0) {
                int compPos = getCenterPos(getTarget()).compareTo(getCenterPos(p.getTarget()));
                if (compPos == 0) {
                    return getCenterSide(getTarget()).compareTo(getCenterSide(p.getTarget()));
                }
                return compPos;
            }
            return compPart;
        }
        return Integer.compare(hashCode(), o.hashCode());
    }
}
