package ruiseki.omoshiroikamo.module.ids.common.block.cable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import cofh.api.item.IToolHammer;
import lombok.Getter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICableEndpoint;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.core.block.ICollidable;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.light.CapabilityLight;
import ruiseki.omoshiroikamo.core.capabilities.redstone.CapabilityRedstone;
import ruiseki.omoshiroikamo.core.datastructure.EnumFacingMap;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaTileInfoProvider;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTickingTE;
import ruiseki.omoshiroikamo.module.ids.common.capabilities.SidedDynamicLight;
import ruiseki.omoshiroikamo.module.ids.common.capabilities.SidedDynamicRedstone;
import ruiseki.omoshiroikamo.module.ids.common.item.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.CablePartRegistry;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.IEnergyPart;

public class TECable extends AbstractTickingTE
    implements ICable, IWailaTileInfoProvider, CapabilityProvider, IGuiHolder<SidedPosGuiData> {

    @NBTPersist
    private byte connectionMask;
    @NBTPersist
    private byte blockedMask;

    private final Map<ForgeDirection, ICableEndpoint> endpoints = new EnumMap<>(ForgeDirection.class);
    private final Map<ForgeDirection, ICablePart> parts = new EnumMap<>(ForgeDirection.class);

    private Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> networks = new HashMap<>();

    @NBTPersist
    private boolean hasCore = true;

    public boolean clientUpdated = false;
    public boolean needsNetworkRebuild = false;

    @Getter
    @NBTPersist
    private EnumFacingMap<Integer> redstoneLevels = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Boolean> redstoneInputs = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Boolean> redstoneStrong = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Integer> lastRedstonePulses = EnumFacingMap.newMap();
    @Getter
    @NBTPersist
    private EnumFacingMap<Integer> lightLevels = EnumFacingMap.newMap();
    private EnumFacingMap<Integer> previousLightLevels;

    public TECable() {
        for (ForgeDirection facing : ForgeDirection.VALID_DIRECTIONS) {
            addCapabilitySided(CapabilityLight.DYNAMIC_LIGHT_CAPABILITY, facing, new SidedDynamicLight(this, facing));
            addCapabilitySided(
                CapabilityRedstone.DYNAMIC_REDSTONE_CAPABILITY,
                facing,
                new SidedDynamicRedstone(this, facing));
        }
    }

    @Override
    public boolean requiresTESR() {
        return true;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        NBTTagCompound partsTag = new NBTTagCompound();
        for (Map.Entry<ForgeDirection, ICablePart> entry : parts.entrySet()) {
            ForgeDirection dir = entry.getKey();
            ICablePart part = entry.getValue();

            NBTTagCompound partTag = new NBTTagCompound();
            partTag.setString("id", part.getId());

            NBTTagCompound data = new NBTTagCompound();
            part.writeToNBT(data);
            partTag.setTag("data", data);

            partsTag.setTag(dir.name(), partTag);
        }

        root.setTag("Parts", partsTag);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        // parts
        parts.clear();
        if (root.hasKey("Parts")) {
            NBTTagCompound partsTag = root.getCompoundTag("Parts");

            for (String key : partsTag.func_150296_c()) {
                ForgeDirection side = ForgeDirection.valueOf(key);
                NBTTagCompound partTag = partsTag.getCompoundTag(key);

                String id = partTag.getString("id");
                NBTTagCompound data = partTag.getCompoundTag("data");

                ICablePart part = CablePartRegistry.create(id);
                if (part == null) {
                    continue;
                }

                part.setCable(this, side);
                part.readFromNBT(data);
                parts.put(side, part);
            }
        }

        needsNetworkRebuild = true;
        if (worldObj != null && worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void onUpdateReceived() {
        if (!lightLevels.equals(previousLightLevels)) {
            previousLightLevels = lightLevels;
            updateTELight();
        }
    }

    @Override
    public ICablePart getPart(ForgeDirection side) {
        return parts.get(side);
    }

    @Override
    public void setPart(ForgeDirection side, ICablePart part) {
        if (part == null) return;

        parts.put(side, part);
        part.setCable(this, side);
        part.onAttached();
        needsNetworkRebuild = true;
    }

    @Override
    public void removePart(ForgeDirection side) {
        ICablePart part = parts.remove(side);
        if (part != null) {
            part.onDetached();
            needsNetworkRebuild = true;
        }
    }

    @Override
    public boolean hasPart(ForgeDirection side) {
        return parts.containsKey(side);
    }

    @Override
    public Collection<ICablePart> getParts() {
        return parts.values();
    }

    @Override
    public ICableEndpoint getEndpoint(ForgeDirection side) {
        return endpoints.get(side);
    }

    @Override
    public void setEndpoint(ICableEndpoint endpoint) {
        if (endpoint == null) return;
        ForgeDirection side = endpoint.getSide();
        endpoints.put(side, endpoint);
        endpoint.onAttached();
        needsNetworkRebuild = true;
    }

    @Override
    public void removeEndpoint(ICableEndpoint endpoint) {
        if (endpoint == null) return;
        ForgeDirection side = endpoint.getSide();
        if (endpoints.remove(side) != null) {
            endpoint.onDetached();
            needsNetworkRebuild = true;
        }
    }

    @Override
    public Collection<ICableEndpoint> getEndpoints() {
        return endpoints.values();
    }

    @Override
    public boolean canConnect(TileEntity other, ForgeDirection side) {
        if (isSideBlocked(side)) return false;
        if (hasPart(side)) return false;

        if (other instanceof ICable otherCable) {
            if (!otherCable.hasCore()) return false;
            ForgeDirection opp = side.getOpposite();
            if (otherCable.isSideBlocked(opp)) return false;
            return !otherCable.hasPart(opp);
        }

        return other instanceof ICableEndpoint;
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return (connectionMask & bit(side)) != 0;
    }

    @Override
    public void connect(ForgeDirection side) {
        connectionMask |= bit(side);
    }

    @Override
    public void disconnect(ForgeDirection side) {
        connectionMask &= (byte) ~bit(side);
    }

    @Override
    public void updateConnections() {
        if (!hasCore || worldObj == null || worldObj.isRemote) return;

        boolean changed = false;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = getPos().offset(dir)
                .getTileEntity(worldObj);

            boolean canCableConnect = canConnect(te, dir);
            boolean wasConnected = (connectionMask & bit(dir)) != 0;

            if (canCableConnect != wasConnected) {
                if (canCableConnect) {
                    connectionMask |= bit(dir);
                } else {
                    connectionMask &= (byte) ~bit(dir);
                }
                changed = true;
            }

            changed |= updateEndpointForSide(dir, te);
        }

        if (changed) {
            needsNetworkRebuild = true;
            markDirty();
        }
    }

    private boolean updateEndpointForSide(ForgeDirection dir, TileEntity te) {
        ICableEndpoint old = endpoints.get(dir);

        if (te instanceof ICableEndpoint ep && !isSideBlocked(dir)) {
            if (old != ep) {
                if (old != null) old.onDetached();
                endpoints.put(dir, ep);
                ep.setCable(this, dir);
                ep.onAttached();
                return true;
            }
        } else if (old != null) {
            old.onDetached();
            endpoints.remove(dir);
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        if (worldObj == null || worldObj.isRemote) return;

        for (Map.Entry<ForgeDirection, ICablePart> e : parts.entrySet()) {
            ICablePart part = e.getValue();
            part.onDetached();
        }
        parts.clear();

        for (ICableEndpoint ep : endpoints.values()) {
            ep.onDetached();
        }
        endpoints.clear();

        if (networks != null && !networks.isEmpty()) {
            networks.clear();
        }

        connectionMask = 0;
        blockedMask = 0;
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        float dx = world.rand.nextFloat() * 0.8F + 0.1F;
        float dy = world.rand.nextFloat() * 0.8F + 0.1F;
        float dz = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, stack.copy());

        float motion = 0.05F;
        entityItem.motionX = world.rand.nextGaussian() * motion;
        entityItem.motionY = world.rand.nextGaussian() * motion + 0.2F;
        entityItem.motionZ = world.rand.nextGaussian() * motion;

        world.spawnEntityInWorld(entityItem);
    }

    @Override
    public boolean hasVisualConnection(ForgeDirection side) {
        if (!hasCore) return false;
        return ((connectionMask & bit(side)) != 0) || parts.containsKey(side);
    }

    @Override
    public boolean isSideBlocked(ForgeDirection side) {
        return (blockedMask & bit(side)) != 0;
    }

    @Override
    public void blockSide(ForgeDirection side) {
        int b = bit(side);
        if ((blockedMask & b) != 0) return;

        blockedMask |= (byte) b;
        disconnect(side);

        TileEntity te = getPos().offset(side)
            .getTileEntity(worldObj);
        if (te instanceof ICable other) {
            other.blockSide(side.getOpposite());
        }

        needsNetworkRebuild = true;
        markDirty();
    }

    @Override
    public void unblockSide(ForgeDirection side) {
        int b = bit(side);
        if ((blockedMask & b) == 0) return;

        blockedMask &= (byte) ~b;

        TileEntity te = getPos().offset(side)
            .getTileEntity(worldObj);
        if (te instanceof ICable other) {
            other.unblockSide(side.getOpposite());
        }

        needsNetworkRebuild = true;
        markDirty();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, ForgeDirection side,
        float hitX, float hitY, float hitZ) {
        ICollidable.RayTraceResult<ForgeDirection> result = ((BlockCable) getBlock())
            .doRayTrace(worldObj, xCoord, yCoord, zCoord, player);

        if (result == null || result.getCollisionType() == null) {
            return false;
        }

        ForgeDirection positionHit = result.getPositionHit();
        ItemStack held = player.getHeldItem();
        if (!worldObj.isRemote) {
            if (result.getCollisionType() == BlockCable.PARTS_COMPONENT) {

                ICablePart part = getPart(positionHit);

                if (held != null && held.getItem() instanceof IToolHammer hammer) {
                    if (!hammer.isUsable(held, player, xCoord, yCoord, zCoord)) {
                        return false;
                    }

                    ItemStack drop = part.getItemStack();
                    if (drop != null) {
                        dropStack(worldObj, xCoord, yCoord, zCoord, drop);
                    }

                    removePart(positionHit);
                    hammer.toolUsed(held, player, xCoord, yCoord, zCoord);
                    return true;
                }

                GuiFactories.sidedTileEntity()
                    .open(player, xCoord, yCoord, zCoord, positionHit);
                return true;
            }

            if (result.getCollisionType() == BlockCable.CONNECTIONS_COMPONENT) {

                if (held != null && held.getItem() instanceof IToolHammer hammer) {
                    blockSide(positionHit);
                    hammer.toolUsed(held, player, xCoord, yCoord, zCoord);
                    return true;
                }

                return false;
            }

            if (result.getCollisionType() == BlockCable.CENTER_COMPONENT) {

                if (held != null && held.getItem() instanceof IToolHammer hammer) {
                    unblockSide(positionHit);
                    hammer.toolUsed(held, player, xCoord, yCoord, zCoord);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(Block blockId) {
        updateConnections();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        updateConnections();
    }

    @Override
    public void onBlockRemoved() {
        destroy();
    }

    @Override
    public void dirty() {
        markDirty();
        clientUpdated = true;
        if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public World getWorld() {
        return worldObj;
    }

    @Override
    public void notifyNeighbors() {
        if (worldObj == null) return;
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
    }

    @Override
    public Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> getNetworks() {
        return networks;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICableNode> AbstractCableNetwork<T> getNetwork(Class<T> partType) {
        return (AbstractCableNetwork<T>) networks.get(partType);
    }

    @Override
    public <T extends ICableNode> void setNetworks(Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> networks) {
        this.networks = networks;
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();
        if (!worldObj.isRemote && needsNetworkRebuild) {
            CableUtils.scheduleRebuild(this);
            needsNetworkRebuild = false;
        }

        for (ICablePart conduit : parts.values()) {
            conduit.doUpdate();
        }

        if (worldObj.isRemote && clientUpdated) {
            onSendUpdate();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();

        if (worldObj == null || worldObj.isRemote) return;

        if (networks != null) {
            for (AbstractCableNetwork<?> net : networks.values()) {
                if (net != null) {
                    net.destroyNetwork();
                }
            }
            networks.clear();
        }
    }

    @Override
    public boolean hasCore() {
        return hasCore;
    }

    @Override
    public void setHasCore(boolean hasCore) {
        if (this.hasCore != hasCore) {
            this.hasCore = hasCore;

            if (!hasCore) {
                connectionMask = 0;
            }

            if (hasCore) {
                needsNetworkRebuild = true;
            }

            dirty();
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {

        EntityPlayer player = accessor.getPlayer();

        if (player != null) {

            ICollidable.RayTraceResult<ForgeDirection> result = ((BlockCable) getBlock())
                .doRayTrace(worldObj, xCoord, yCoord, zCoord, player);

            if (result != null && result.getCollisionType() != null) {

                ForgeDirection side = result.getPositionHit();

                if (result.getCollisionType() == BlockCable.PARTS_COMPONENT) {

                    ICablePart part = getPart(side);
                    if (part != null) {
                        tooltip.add("Part: " + part.getId() + " (" + side + ")");
                    }

                } else if (result.getCollisionType() == BlockCable.CONNECTIONS_COMPONENT) {

                    tooltip.add("Connection: " + side);

                } else if (result.getCollisionType() == BlockCable.CENTER_COMPONENT) {

                    tooltip.add("Core cable");
                }
            }
        }

        tooltip.add(getPos().toString());

        if (player != null && player.isSneaking()) {

            NBTTagCompound tag = accessor.getNBTData();
            if (tag != null && tag.hasKey("networkCount")) {

                int networkCount = tag.getInteger("networkCount");

                if (networkCount <= 0) {
                    tooltip.add("Networks: none");
                    return;
                }

                tooltip.add("Networks: " + networkCount);

                for (int i = 0; i < networkCount; i++) {
                    String key = "networkName" + i;
                    if (tag.hasKey(key)) {
                        tooltip.add(" - " + tag.getString(key));
                    }
                }
            }
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        if (tile instanceof ICable cable) {
            Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> nets = cable.getNetworks();
            tag.setInteger("networkCount", nets.size());
            int i = 0;
            for (AbstractCableNetwork<?> n : nets.values()) {
                tag.setString(
                    "networkName" + i,
                    n.getClass()
                        .getSimpleName() + " ["
                        + n.getNodes()
                            .size()
                        + "]");
                i++;
            }
        }
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public void setEnergyStored(int stored) {
        // NO OP
    }

    @Override
    public int getEnergyTransfer() {
        // NO OP
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        ICablePart part = getPart(from);
        return part instanceof IEnergyPart energy && energy.getIO() != EnumIO.NONE;
    }

    @Override
    public ModularScreen createScreen(SidedPosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ICablePart part = getPart(data.getSide());
        if (part != null) {
            return part.partPanel(data, syncManager, settings);
        }

        return new ModularPanel("baseCable");
    }

    public void updateLightInfo() {
        sendUpdate();
    }

    public void updateRedstoneInfo(ForgeDirection side, boolean strongPower) {
        this.markDirty();

        World world = getWorldObj();
        if (world == null) return;

        int x = xCoord + side.offsetX;
        int y = yCoord + side.offsetY;
        int z = zCoord + side.offsetZ;

        if (world.blockExists(x, y, z)) {

            // equivalent of neighborChanged(...)
            world.notifyBlockOfNeighborChange(x, y, z, this.getBlockType());

            if (strongPower) {
                // strong power → notify all neighbours of that block
                world.notifyBlocksOfNeighborChange(x, y, z, this.getBlockType());
            }
        }
    }

    private static byte bit(ForgeDirection dir) {
        return (byte) (1 << dir.ordinal());
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, ForgeDirection facing) {
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, ForgeDirection facing) {
        return super.getCapability(capability, facing);
    }
}
