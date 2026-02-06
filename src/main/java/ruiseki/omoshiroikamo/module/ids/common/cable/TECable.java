package ruiseki.omoshiroikamo.module.ids.common.cable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import cofh.api.item.IToolHammer;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.ICustomCollision;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICableEndpoint;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiFactories;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaTileInfoProvider;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.ids.common.network.CablePartRegistry;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.redstone.IRedstoneWriter;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.IEnergyPart;

public class TECable extends AbstractTE
    implements ICable, ICustomCollision, IWailaTileInfoProvider, CapabilityProvider, IGuiHolder<SidedPosGuiData> {

    private byte connectionMask;
    private byte blockedMask;

    private final Map<ForgeDirection, ICableEndpoint> endpoints = new EnumMap<>(ForgeDirection.class);
    private final Map<ForgeDirection, ICablePart> parts = new EnumMap<>(ForgeDirection.class);

    private Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> networks = new HashMap<>();

    private boolean hasCore = true;

    public boolean clientUpdated = false;
    public boolean needsNetworkRebuild = false;

    public TECable() {}

    @Override
    public void writeCommon(NBTTagCompound tag) {
        tag.setBoolean("hasCore", hasCore);
        tag.setByte("blockedMask", blockedMask);
        tag.setByte("connectionMask", connectionMask);

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

        tag.setTag("Parts", partsTag);
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        if (tag.hasKey("hasCore")) {
            hasCore = tag.getBoolean("hasCore");
        } else {
            hasCore = true;
        }

        blockedMask = tag.getByte("blockedMask");
        connectionMask = tag.getByte("connectionMask");

        // parts
        parts.clear();
        if (tag.hasKey("Parts")) {
            NBTTagCompound partsTag = tag.getCompoundTag("Parts");

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
            if (otherCable.hasPart(opp)) return false;
            return true;
        }

        if (other instanceof ICableEndpoint) {
            return true;
        }

        return false;
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
            for (AbstractCableNetwork<?> net : new ArrayList<>(networks.values())) {
                net.destroyNetwork();
            }
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
        ItemStack held = player.getHeldItem();

        CableHit hit = this.rayTraceCable(player);
        if (hit != null) {
            if (!worldObj.isRemote) {
                if (hit.type == CableHit.Type.CONNECTION) {
                    if (held != null && held.getItem() instanceof IToolHammer hammer) {
                        blockSide(hit.side);
                        hammer.toolUsed(held, player, x, y, z);
                        return true;
                    }
                    return false;
                }

                if (hit.type == CableHit.Type.CORE) {
                    if (held != null && held.getItem() instanceof IToolHammer hammer) {
                        unblockSide(side);
                        hammer.toolUsed(held, player, x, y, z);
                        return true;
                    }
                }

                if (hit.type == CableHit.Type.PART) {
                    ICablePart part = getPart(hit.side);
                    if (held != null && held.getItem() instanceof IToolHammer hammer) {
                        if (!hammer.isUsable(held, player, x, y, z)) {
                            return false;
                        }

                        ItemStack drop = part.getItemStack();
                        if (drop != null) {
                            dropStack(world, xCoord, yCoord, zCoord, drop);
                        }

                        removePart(hit.side);
                        hammer.toolUsed(held, player, x, y, z);
                        return true;
                    }

                    if (hasCore()) {
                        OKGuiFactories.tileEntity()
                            .setGuiContainer(part.getGuiContainer())
                            .open(player, x, y, z, hit.side);
                        return true;
                    }
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
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (!worldObj.isRemote && needsNetworkRebuild) {
            CableUtils.rebuildNetworks(this);
            needsNetworkRebuild = false;
        }

        for (ICablePart conduit : parts.values()) {
            conduit.doUpdate();
        }

        return worldObj.isRemote && clientUpdated;
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

    public int getRedstonePower(ForgeDirection side) {
        if (worldObj == null) return 0;
        if (isSideBlocked(side)) return 0;

        ICablePart part = getPart(side);
        if (!(part instanceof IRedstoneWriter rs)) {
            return 0;
        }

        return MathHelper.clamp_int(rs.getRedstoneOutput(), 0, 15);
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
    public Iterable<AxisAlignedBB> getSelectedBoundingBoxesFromPool(World world, int x, int y, int z, Entity entity,
        boolean visual) {

        if (!(entity instanceof EntityPlayer player) || !visual) {
            return getCableParts();
        }

        CableHit hit = rayTraceCable(player);
        if (hit != null) {
            return Collections.singletonList(hit.box);
        }

        return getCableParts();
    }

    @Override
    public void addCollidingBlockToList(World w, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> out,
        Entity e) {
        for (final AxisAlignedBB bx : this.getSelectedBoundingBoxesFromPool(w, x, y, z, e, false)) {
            out.add(AxisAlignedBB.getBoundingBox(bx.minX, bx.minY, bx.minZ, bx.maxX, bx.maxY, bx.maxZ));
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {

        EntityPlayer player = accessor.getPlayer();
        // HIT INFO
        CableHit hit = rayTraceCable(player);
        if (hit != null) {
            switch (hit.type) {
                case PART -> tooltip.add("Part: " + hit.part.getId() + " (" + hit.side + ")");
                case CONNECTION -> tooltip.add("Connection: " + hit.side);
                case CORE -> tooltip.add("Core cable");
            }
        }
        tooltip.add(getPos().toString());

        if (accessor.getPlayer()
            .isSneaking()) {
            // NETWORK INFO
            NBTTagCompound tag = accessor.getNBTData();
            int networkCount = tag.getInteger("networkCount");

            if (networkCount == 0) {
                tooltip.add("Networks: none");
                return;
            }

            tooltip.add("Networks: " + networkCount);
            for (int i = 0; i < networkCount; i++) {
                tooltip.add(" - " + tag.getString("networkName" + i));
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
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        EntityPlayer player = accessor.getPlayer();
        CableHit hit = rayTraceCable(player);

        if (hit != null && hit.type == CableHit.Type.PART && hit.part != null) {
            return hit.part.getItemStack();
        }

        return null;
    }

    private AxisAlignedBB getCoreBox() {
        float min = 6f / 16f;
        float max = 10f / 16f;
        return AxisAlignedBB.getBoundingBox(min, min, min, max, max, max);
    }

    private AxisAlignedBB getConnectionBox(ForgeDirection dir) {
        float min = 6f / 16f;
        float max = 10f / 16f;

        return switch (dir) {
            case WEST -> AxisAlignedBB.getBoundingBox(0, min, min, min, max, max);
            case EAST -> AxisAlignedBB.getBoundingBox(max, min, min, 1, max, max);
            case DOWN -> AxisAlignedBB.getBoundingBox(min, 0, min, max, min, max);
            case UP -> AxisAlignedBB.getBoundingBox(min, max, min, max, 1, max);
            case NORTH -> AxisAlignedBB.getBoundingBox(min, min, 0, max, max, min);
            case SOUTH -> AxisAlignedBB.getBoundingBox(min, min, max, max, max, 1);
            default -> null;
        };
    }

    public Iterable<AxisAlignedBB> getCableParts() {
        List<AxisAlignedBB> parts = new ArrayList<>();

        float min = 6f / 16f;
        float max = 10f / 16f;

        // core
        if (hasCore()) {
            parts.add(AxisAlignedBB.getBoundingBox(min, min, min, max, max, max));
        }

        if (hasVisualConnection(ForgeDirection.WEST))
            parts.add(AxisAlignedBB.getBoundingBox(0f, min, min, min, max, max));

        if (hasVisualConnection(ForgeDirection.EAST))
            parts.add(AxisAlignedBB.getBoundingBox(max, min, min, 1f, max, max));

        if (hasVisualConnection(ForgeDirection.DOWN))
            parts.add(AxisAlignedBB.getBoundingBox(min, 0f, min, max, min, max));

        if (hasVisualConnection(ForgeDirection.UP))
            parts.add(AxisAlignedBB.getBoundingBox(min, max, min, max, 1f, max));

        if (hasVisualConnection(ForgeDirection.NORTH))
            parts.add(AxisAlignedBB.getBoundingBox(min, min, 0f, max, max, min));

        if (hasVisualConnection(ForgeDirection.SOUTH))
            parts.add(AxisAlignedBB.getBoundingBox(min, min, max, max, max, 1f));

        for (ICablePart part : this.parts.values()) {
            AxisAlignedBB bb = part.getCollisionBox();
            if (bb != null) parts.add(bb);
        }

        return parts;
    }

    public CableHit rayTraceCable(EntityPlayer player) {
        Vec3 start = PlayerUtils.getEyePosition(player);
        Vec3 end = start
            .addVector(player.getLookVec().xCoord * 5, player.getLookVec().yCoord * 5, player.getLookVec().zCoord * 5);

        CableHit best = null;
        double bestDist = Double.MAX_VALUE;

        // PARTS
        for (Map.Entry<ForgeDirection, ICablePart> e : parts.entrySet()) {
            AxisAlignedBB bb = e.getValue()
                .getCollisionBox();
            if (bb == null) continue;

            AxisAlignedBB wbb = bb.getOffsetBoundingBox(xCoord, yCoord, zCoord);
            MovingObjectPosition mop = wbb.calculateIntercept(start, end);

            if (mop != null) {
                double d = mop.hitVec.distanceTo(start);
                if (d < bestDist) {
                    bestDist = d;
                    best = new CableHit(CableHit.Type.PART, e.getKey(), e.getValue(), bb, mop.hitVec);
                }
            }
        }

        // CONNECTIONS
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!hasVisualConnection(dir)) continue;

            AxisAlignedBB bb = getConnectionBox(dir);
            AxisAlignedBB wbb = bb.getOffsetBoundingBox(xCoord, yCoord, zCoord);
            MovingObjectPosition mop = wbb.calculateIntercept(start, end);

            if (mop != null) {
                double d = mop.hitVec.distanceTo(start);
                if (d < bestDist) {
                    bestDist = d;
                    best = new CableHit(CableHit.Type.CONNECTION, dir, null, bb, mop.hitVec);
                }
            }
        }

        // CORE
        if (hasCore()) {
            AxisAlignedBB core = getCoreBox();
            AxisAlignedBB wcore = core.getOffsetBoundingBox(xCoord, yCoord, zCoord);
            MovingObjectPosition mop = wcore.calculateIntercept(start, end);

            if (mop != null) {
                double d = mop.hitVec.distanceTo(start);
                if (d < bestDist) {
                    best = new CableHit(CableHit.Type.CORE, null, null, core, mop.hitVec);
                }
            }
        }

        return best;
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
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ICablePart part = getPart(data.getSide());
        if (part != null) {
            return part.partPanel(data, syncManager, settings);
        }

        return new ModularPanel("baseCable");
    }

    @Override
    public <T> @Nullable T getCapability(@NotNull Class<T> capability, @NotNull ForgeDirection side) {
        return null;
    }

    private static byte bit(ForgeDirection dir) {
        return (byte) (1 << dir.ordinal());
    }

    public static class CableHit {

        enum Type {
            PART,
            CONNECTION,
            CORE
        }

        final Type type;
        final ForgeDirection side;
        final ICablePart part;
        final AxisAlignedBB box;
        final Vec3 hitVec;

        CableHit(Type type, ForgeDirection side, ICablePart part, AxisAlignedBB box, Vec3 hitVec) {
            this.type = type;
            this.side = side;
            this.part = part;
            this.box = box;
            this.hitVec = hitVec;
        }
    }

}
