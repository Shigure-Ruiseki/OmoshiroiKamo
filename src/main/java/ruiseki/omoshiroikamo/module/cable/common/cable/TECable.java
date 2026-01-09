package ruiseki.omoshiroikamo.module.cable.common.cable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.ICustomCollision;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaTileInfoProvider;
import ruiseki.omoshiroikamo.module.cable.client.gui.CableGuiFactories;
import ruiseki.omoshiroikamo.module.cable.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.CablePartRegistry;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class TECable extends AbstractTE
    implements ICable, ICustomCollision, IWailaTileInfoProvider, CapabilityProvider, IGuiHolder<PosSideGuiData> {

    protected final EnumSet<ForgeDirection> connections = EnumSet.noneOf(ForgeDirection.class);

    private final Map<ForgeDirection, ICablePart> parts = new EnumMap<>(ForgeDirection.class);

    private Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> networks;

    private boolean clientUpdated = false;
    private boolean needsNetworkRebuild = false;

    public TECable() {
        networks = new HashMap<>();
    }

    @Override
    public void writeCommon(NBTTagCompound tag) {
        int[] dirs = new int[connections.size()];
        Iterator<ForgeDirection> cons = connections.iterator();
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = cons.next()
                .ordinal();
        }
        tag.setIntArray("connections", dirs);

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

        // connections
        connections.clear();
        int[] dirs = tag.getIntArray("connections");
        for (int i : dirs) {
            connections.add(ForgeDirection.values()[i]);
        }

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
        markDirty();
    }

    @Override
    public void removePart(ForgeDirection side) {
        ICablePart part = parts.remove(side);
        if (part != null) {
            part.onDetached();
            needsNetworkRebuild = true;
            markDirty();
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
    public boolean canConnect(TileEntity other, ForgeDirection side) {
        if (hasPart(side)) return false;
        if (!(other instanceof ICable otherCable)) return false;
        ForgeDirection opp = side.getOpposite();
        if (otherCable.hasPart(opp)) return false;
        return true;
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return connections.contains(side);
    }

    @Override
    public void updateConnections() {
        if (worldObj == null || worldObj.isRemote) return;

        boolean changed = false;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

            boolean canConnect = canConnect(te, dir);
            boolean wasConnected = isConnected(dir);

            if (canConnect != wasConnected) {
                if (canConnect) connect(dir);
                else disconnect(dir);
                changed = true;
            }
        }

        if (changed) {
            needsNetworkRebuild = true;
            markDirty();
        }
    }

    @Override
    public void disconnect(ForgeDirection side) {
        connections.remove(side);
    }

    @Override
    public void connect(ForgeDirection side) {
        connections.add(side);
    }

    @Override
    public void destroy() {
        if (worldObj == null || worldObj.isRemote) return;

        // 1. Drop parts
        for (Map.Entry<ForgeDirection, ICablePart> e : parts.entrySet()) {
            ICablePart part = e.getValue();

            // detach
            part.onDetached();

            // drop item
            ItemStack stack = part.getItemStack();
            if (stack != null) {
                dropStack(worldObj, xCoord, yCoord, zCoord, stack);
            }
        }

        parts.clear();

        // 2. Networks cleanup
        if (networks != null && !networks.isEmpty()) {
            for (AbstractCableNetwork<?> net : new ArrayList<>(networks.values())) {
                net.destroyNetwork();
            }
            networks.clear();
        }

        connections.clear();
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
        return connections.contains(side) || parts.containsKey(side);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, ForgeDirection side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        CableHit hit = this.rayTraceCable(player);
        if (hit != null && hit.type == CableHit.Type.PART) {
            ICablePart part = getPart(hit.side);

            if (part != null) {
                player.addChatMessage(new ChatComponentText("[Part]\n" + part));

                CableGuiFactories.tileEntity()
                    .open(player, x, y, z, hit.side);
            }

            return true;
        }

        for (AbstractCableNetwork<?> net : this.getNetworks()
            .values()) {
            player.addChatMessage(new ChatComponentText("[Network]\n" + net));
        }

        return true;
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
    public Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> getNetworks() {
        return networks;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICablePart> AbstractCableNetwork<T> getNetwork(Class<T> partType) {
        return (AbstractCableNetwork<T>) networks.get(partType);
    }

    @Override
    public <T extends ICablePart> void setNetworks(Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> networks) {
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

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        if (tile instanceof TECable cable) {
            Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> nets = cable.getNetworks();
            tag.setInteger("networkCount", nets.size());
            int i = 0;
            for (AbstractCableNetwork<?> n : nets.values()) {
                tag.setString(
                    "networkName" + i,
                    n.getClass()
                        .getSimpleName() + " ["
                        + n.getParts()
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
        parts.add(AxisAlignedBB.getBoundingBox(min, min, min, max, max, max));

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
        AxisAlignedBB core = getCoreBox();
        AxisAlignedBB wcore = core.getOffsetBoundingBox(xCoord, yCoord, zCoord);
        MovingObjectPosition mop = wcore.calculateIntercept(start, end);

        if (mop != null) {
            double d = mop.hitVec.distanceTo(start);
            if (d < bestDist) {
                best = new CableHit(CableHit.Type.CORE, null, null, core, mop.hitVec);
            }
        }

        return best;
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (amount <= 0) return 0;

        ICablePart part = getPart(side);
        if (part == null) return 0;
        if (!shouldDoWorkThisTick(part.getTickInterval())) return 0;
        if (!(part instanceof IEnergyPart energyPart)) return 0;

        if (!energyPart.getIO()
            .canInput()) return 0;

        int limit = energyPart.getTransferLimit();
        int toTransfer = Math.min(amount, limit);

        return energyPart.receiveEnergy(toTransfer, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (amount <= 0) return 0;

        ICablePart part = getPart(side);
        if (part == null) return 0;
        if (!shouldDoWorkThisTick(part.getTickInterval())) return 0;
        if (!(part instanceof IEnergyPart energyPart)) return 0;

        if (!energyPart.getIO()
            .canOutput()) return 0;

        int limit = energyPart.getTransferLimit();
        int toTransfer = Math.min(amount, limit);

        return energyPart.extractEnergy(toTransfer, simulate);
    }

    @Override
    public int getEnergyStored() {
        // NO OP
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        // NO OP
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
    public ModularPanel buildUI(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ICablePart part = getPart(data.getSide());
        return part.partPanel(data, syncManager, settings);
    }

    @Override
    public <T> @Nullable T getCapability(@NotNull Class<T> capability, @NotNull ForgeDirection side) {
        return null;
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
