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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.ICustomCollision;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaTileInfoProvider;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.CablePartRegistry;

public class TECable extends TileEntityOK implements ICable, ICustomCollision, IWailaTileInfoProvider {

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
        connections.clear();
        parts.clear();

        // connections
        int[] dirs = tag.getIntArray("connections");
        for (int i : dirs) {
            connections.add(ForgeDirection.values()[i]);
        }

        // parts
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
        if (networks != null) {
            for (AbstractCableNetwork<?> net : networks.values()) {
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
    protected void doUpdate() {
        super.doUpdate();

        if (!worldObj.isRemote && needsNetworkRebuild) {
            CableUtils.rebuildNetworks(this);
            needsNetworkRebuild = false;
        }

        for (ICablePart conduit : parts.values()) {
            conduit.doUpdate();
        }

        if (worldObj.isRemote && clientUpdated) updateClient();
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

    private void updateClient() {
        if (!clientUpdated) return;
        clientUpdated = false;
        if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

    private CableHit rayTraceCable(EntityPlayer player) {
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

    private static class CableHit {

        enum Type {
            PART,
            CONNECTION,
            CORE
        }

        final Type type;
        final ForgeDirection side; // PART hoặc CONNECTION
        final ICablePart part; // chỉ PART
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
