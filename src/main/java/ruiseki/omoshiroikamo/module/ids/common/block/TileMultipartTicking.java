package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.api.ids.block.cable.ICable;
import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.IPartNetwork;
import ruiseki.omoshiroikamo.api.ids.part.IPartContainer;
import ruiseki.omoshiroikamo.api.ids.part.IPartContainerFacade;
import ruiseki.omoshiroikamo.api.ids.part.IPartState;
import ruiseki.omoshiroikamo.api.ids.part.IPartType;
import ruiseki.omoshiroikamo.api.ids.tileentity.ITileCableNetwork;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;
import ruiseki.omoshiroikamo.core.helper.ItemStackHelpers;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.CableNetworkComponent;
import ruiseki.omoshiroikamo.module.ids.common.util.CableHelpers;
import ruiseki.omoshiroikamo.module.ids.common.util.PartHelpers;

public class TileMultipartTicking extends TileEntityOK
    implements TileEntityOK.ITickingTile, IPartContainer, ITileCableNetwork, PartHelpers.IPartStateHolderCallback {

    private final Map<ForgeDirection, PartHelpers.PartStateHolder<?, ?>> partData = Maps.newHashMap();
    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    @NBTPersist
    private boolean realCable = true;
    @NBTPersist
    private Map<Integer, Boolean> connected = Maps.newHashMap();
    @NBTPersist
    private Map<Integer, Boolean> forceDisconnected = Maps.newHashMap();
    @NBTPersist
    private Map<Integer, Integer> redstoneLevels = Maps.newHashMap();
    @NBTPersist
    private Map<Integer, Boolean> redstoneInputs = Maps.newHashMap();
    @NBTPersist
    private Map<Integer, Integer> lightLevels = Maps.newHashMap();
    private Map<Integer, Integer> previousLightLevels;
    @NBTPersist
    private String facadeBlockName = null;
    @NBTPersist
    private int facadeMeta = 0;

    @Getter
    @Setter
    private IPartNetwork network;

    @Override
    public void writeCommon(NBTTagCompound tag) {
        this.markDirty();
        super.writeCommon(tag);
        PartHelpers.writePartsToNBT(getPos(), tag, this.partData);
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        PartHelpers.readPartsFromNBT(getNetwork(), getPos(), tag, this.partData);
        super.readCommon(tag);
    }

    /**
     * Indicate that this cable is not a real cable if false and should not allow any connections.
     * Parts can be added to it though.
     *
     * @param realCable If this cable is real and should accept connections.
     */
    public void setRealCable(boolean realCable) {
        this.realCable = realCable;
        sendUpdate();
    }

    /**
     * @return If this cable is real.
     */
    public boolean isRealCable() {
        return this.realCable;
    }

    @Override
    public DimPos getPosition() {
        return DimPos.of(getWorldObj().provider.dimensionId, getPos());
    }

    @Override
    public Map<ForgeDirection, IPartType<?, ?>> getParts() {
        return Maps.transformValues(partData, new Function<PartHelpers.PartStateHolder<?, ?>, IPartType<?, ?>>() {

            @Nullable
            @Override
            public IPartType<?, ?> apply(@Nullable PartHelpers.PartStateHolder<?, ?> input) {
                return input.getPart();
            }
        });
    }

    @Override
    public boolean hasParts() {
        return !partData.isEmpty();
    }

    @Override
    public <P extends IPartType<P, S>, S extends IPartState<P>> boolean canAddPart(ForgeDirection side,
        IPartType<P, S> part) {
        return !hasPart(side);
    }

    protected void onPartsChanged() {
        markDirty();
        sendUpdate();
    }

    @Override
    public void setPart(final ForgeDirection side, final IPartType part, final IPartState partState) {
        PartHelpers.setPart(
            getNetwork(),
            getWorldObj(),
            getPos(),
            side,
            Objects.requireNonNull(part),
            Objects.requireNonNull(partState),
            new PartHelpers.IPartStateHolderCallback() {

                @Override
                public void onSet(PartHelpers.PartStateHolder<?, ?> partStateHolder) {
                    partData.put(side, PartHelpers.PartStateHolder.of(part, partState));
                }
            });
        onPartsChanged();
    }

    @Override
    public IPartType getPart(ForgeDirection side) {
        if (!partData.containsKey(side)) return null;
        return partData.get(side)
            .getPart();
    }

    @Override
    public boolean hasPart(ForgeDirection side) {
        return partData.containsKey(side);
    }

    @Override
    public IPartType removePart(ForgeDirection side, @Nullable EntityPlayer player) {
        PartHelpers.PartStateHolder<?, ?> partStateHolder = partData.get(side); // Don't remove the state just yet! We
                                                                                // might need it in network removal.
        if (partStateHolder == null) {
            Logger.warn("Attempted to remove a part at a side where no part was.");
            return null;
        } else {
            IPartType removed = partStateHolder.getPart();
            if (getNetwork() != null) {
                INetworkElement networkElement = removed.createNetworkElement(
                    (IPartContainerFacade) getBlock(),
                    DimPos.of(getWorldObj().provider.dimensionId, getPos()),
                    side);
                if (!getNetwork().removeNetworkElementPre(networkElement)) {
                    return null;
                }

                // Drop all parts types as item.
                List<ItemStack> itemStacks = Lists.newLinkedList();
                networkElement.addDrops(itemStacks, true);
                for (ItemStack itemStack : itemStacks) {
                    if (player != null) {
                        ItemStackHelpers.spawnItemStackToPlayer(getWorldObj(), getPos(), itemStack, player);
                    } else {
                        // TODO: add blockDrop
                        // Block.spawnAsEntity(getWorldObj(), getPos(), itemStack);
                    }
                }

                // Remove the element from the network.
                getNetwork().removeNetworkElementPost(networkElement);
            } else {
                Logger.warn("Removing a part where no network reference was found.");
                ItemStackHelpers
                    .spawnItemStackToPlayer(getWorldObj(), getPos(), new ItemStack(removed.getItem()), player);
            }
            // Finally remove the part data from this tile.
            IPartType ret = partData.remove(side)
                .getPart();
            onPartsChanged();
            return ret;
        }
    }

    @Override
    public void setPartState(ForgeDirection side, IPartState partState) {
        PartHelpers.PartStateHolder<?, ?> partStateHolder = partData.get(side);
        if (partStateHolder == null) {
            throw new IllegalArgumentException(
                String.format("No part at position %s was found to update the state " + "for.", getPosition()));
        }
        partData.put(side, PartHelpers.PartStateHolder.of(partStateHolder.getPart(), partState));
        onPartsChanged();
    }

    @Override
    public IPartState getPartState(ForgeDirection side) {
        PartHelpers.PartStateHolder<?, ?> partStateHolder = partData.get(side);
        if (partStateHolder == null) {
            throw new IllegalArgumentException(
                String.format("No part at position %s was found to get the state from.", getPosition()));
        }
        return partStateHolder.getState();
    }

    @Override
    public void onUpdateReceived() {
        getWorldObj().markBlockRangeForRenderUpdate(
            getPos().getX(),
            getPos().getY(),
            getPos().getZ(),
            getPos().getX(),
            getPos().getY(),
            getPos().getZ());
        if (!lightLevels.equals(previousLightLevels)) {
            previousLightLevels = lightLevels;
            // TODO: add check light
            // getWorldObj().checkLight(getPos());
        }

    }

    public boolean isForceDisconnected(ForgeDirection side) {
        if (!isRealCable() || hasPart(side)) return true;
        if (!forceDisconnected.containsKey(side.ordinal())) return false;
        return forceDisconnected.get(side.ordinal());
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();

        // If the connection data were reset, update the cable connections
        if (connected.isEmpty()) {
            updateConnections();
        }

        if (!MinecraftHelpers.isClientSide()) {
            // Loop over all part states to check their dirtiness
            for (PartHelpers.PartStateHolder<?, ?> partStateHolder : partData.values()) {
                if (partStateHolder.getState()
                    .isDirtyAndReset()) {
                    markDirty();
                }
                if (partStateHolder.getState()
                    .isUpdateAndReset()) {
                    sendUpdate();
                }
            }
        }
    }

    protected void updateRedstoneInfo(ForgeDirection side) {
        sendUpdate();
        // TODO: add notify redstone
        // getWorldObj().notifyNeighborsOfStateChange(getPos(), getBlock());
        // getWorldObj().notifyNeighborsOfStateChange(getPos().offset(side.getOpposite()), getBlock());
    }

    public void setRedstoneLevel(ForgeDirection side, int level) {
        if (!getWorldObj().isRemote) {
            boolean sendUpdate = false;
            if (redstoneLevels.containsKey(side.ordinal())) {
                if (redstoneLevels.get(side.ordinal()) != level) {
                    sendUpdate = true;
                    redstoneLevels.put(side.ordinal(), level);
                }
            } else {
                sendUpdate = true;
                redstoneLevels.put(side.ordinal(), level);
            }
            if (sendUpdate) {
                updateRedstoneInfo(side);
            }
        }
    }

    public int getRedstoneLevel(ForgeDirection side) {
        if (redstoneLevels.containsKey(side.ordinal())) {
            return redstoneLevels.get(side.ordinal());
        }
        return -1;
    }

    public void setAllowRedstoneInput(ForgeDirection side, boolean allow) {
        redstoneInputs.put(side.ordinal(), allow);
    }

    public boolean isAllowRedstoneInput(ForgeDirection side) {
        if (redstoneInputs.containsKey(side.ordinal())) {
            return redstoneInputs.get(side.ordinal());
        }
        return false;
    }

    public void disableRedstoneLevel(ForgeDirection side) {
        if (!getWorldObj().isRemote) {
            redstoneLevels.remove(side.ordinal());
            updateRedstoneInfo(side);
        }
    }

    protected void updateLightInfo(ForgeDirection side) {
        sendUpdate();
    }

    public void setLightLevel(ForgeDirection side, int level) {
        if (!getWorldObj().isRemote) {
            boolean sendUpdate = false;
            if (lightLevels.containsKey(side.ordinal())) {
                if (lightLevels.get(side.ordinal()) != level) {
                    sendUpdate = true;
                    lightLevels.put(side.ordinal(), level);
                }
            } else {
                sendUpdate = true;
                lightLevels.put(side.ordinal(), level);
            }
            if (sendUpdate) {
                updateLightInfo(side);
            }
        }
    }

    public int getLightLevel(ForgeDirection side) {
        if (lightLevels.containsKey(side.ordinal())) {
            return lightLevels.get(side.ordinal());
        }
        return 0;
    }

    /**
     * Get the part container at the given position.
     *
     * @param pos The position.
     * @return The container or null.
     */
    public static IPartContainer get(DimPos pos) {
        IPartContainerFacade partContainerFacade = CableHelpers.getInterface(pos, IPartContainerFacade.class);
        return partContainerFacade.getPartContainer(pos.getWorld(), pos.getBlockPos());
    }

    @Override
    public void resetCurrentNetwork() {
        if (network != null) setNetwork(null);
    }

    @Override
    public boolean canConnect(ICable connector, ForgeDirection side) {
        return !isForceDisconnected(side);
    }

    @Override
    public void updateConnections() {
        World world = getWorldObj();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            boolean cableConnected = CableNetworkComponent.canSideConnect(world, getPos(), side, (ICable) getBlock());
            connected.put(side.ordinal(), cableConnected);

            // Remove any already existing force-disconnects for this side.
            if (!cableConnected) {
                forceDisconnected.put(side.ordinal(), false);
            }
        }
        markDirty();
        sendUpdate();
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return connected.containsKey(side.ordinal()) && connected.get(side.ordinal());
    }

    @Override
    public void disconnect(ForgeDirection side) {
        forceDisconnected.put(side.ordinal(), true);
    }

    @Override
    public void reconnect(ForgeDirection side) {
        forceDisconnected.remove(side.ordinal());
    }

    @Override
    public void onSet(PartHelpers.PartStateHolder<?, ?> partStateHolder) {

    }

    /**
     * @return The raw part data.
     */
    public Map<ForgeDirection, PartHelpers.PartStateHolder<?, ?>> getPartData() {
        return this.partData;
    }

    /**
     * Override the part data.
     *
     * @param partData The raw part data.
     */
    public void setPartData(Map<ForgeDirection, PartHelpers.PartStateHolder<?, ?>> partData) {
        this.partData.clear();
        this.partData.putAll(partData);
    }

    /**
     * @return The raw force disconnection data.
     */
    public Map<Integer, Boolean> getForceDisconnected() {
        return this.forceDisconnected;
    }

    public void setForceDisconnected(Map<Integer, Boolean> forceDisconnected) {
        this.forceDisconnected.clear();
        this.forceDisconnected.putAll(forceDisconnected);
    }

    /**
     * Reset the part data without signaling any neighbours or the network.
     * Is used in block conversion.
     */
    public void silentResetPartData() {
        this.partData.clear();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, ForgeDirection facing) {
        if (facing == null) {
            for (Map.Entry<ForgeDirection, PartHelpers.PartStateHolder<?, ?>> entry : partData.entrySet()) {
                IPartState partState = entry.getValue()
                    .getState();
                if (partState != null && partState.hasCapability(capability)) {
                    return true;
                }
            }
        } else {
            if (hasPart(facing)) {
                IPartState partState = getPartState(facing);
                if (partState != null && partState.hasCapability(capability)) {
                    return true;
                }
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        if (facing == null) {
            for (Map.Entry<ForgeDirection, PartHelpers.PartStateHolder<?, ?>> entry : partData.entrySet()) {
                IPartState partState = entry.getValue()
                    .getState();
                if (partState != null && partState.hasCapability(capability)) {
                    return (T) partState.getCapability(capability);
                }
            }
        } else {
            IPartState partState = getPartState(facing);
            if (partState != null && partState.hasCapability(capability)) {
                return (T) partState.getCapability(capability);
            }
        }
        return super.getCapability(capability, facing);
    }

}
