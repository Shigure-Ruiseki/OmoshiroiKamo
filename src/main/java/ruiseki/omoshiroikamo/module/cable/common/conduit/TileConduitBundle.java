package ruiseki.omoshiroikamo.module.cable.common.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.gtnewhorizon.gtnhlib.concurrent.cas.CasList;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadeType;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.CollidableCache;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.CollidableComponent;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.ConduitConnectorType;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.ConduitGeometryUtil;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.Offset;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.Offsets;

public class TileConduitBundle extends TileEntityOK implements IConduitBundle {

    public static final short NBT_VERSION = 1;

    private final List<IConduit> conduits = new CasList<>();

    private Block facadeId = null;
    private int facadeMeta = 0;
    private FacadeType facadeType = FacadeType.BASIC;

    private boolean facadeChanged;

    private final List<CollidableComponent> cachedCollidables = new ArrayList<>();

    private final List<CollidableComponent> cachedConnectors = new ArrayList<>();

    private boolean conduitsDirty = true;
    private boolean collidablesDirty = true;
    private boolean connectorsDirty = true;

    private boolean clientUpdated = false;

    private ConduitDisplayMode lastMode = ConduitDisplayMode.ALL;

    Object covers;

    public TileConduitBundle() {
        this.blockType = EnderIO.blockConduitBundle;
    }

    @Override
    public void dirty() {
        conduitsDirty = true;
        collidablesDirty = true;
    }

    @Override
    public void writeCommon(NBTTagCompound nbtRoot) {
        NBTTagList conduitTags = new NBTTagList();
        for (IConduit conduit : conduits) {
            NBTTagCompound conduitRoot = new NBTTagCompound();
            ConduitUtil.writeToNBT(conduit, conduitRoot);
            conduitTags.appendTag(conduitRoot);
        }
        nbtRoot.setTag("conduits", conduitTags);
        if (facadeId != null) {
            nbtRoot.setString("facadeId", Block.blockRegistry.getNameForObject(facadeId));
            nbtRoot.setString("facadeType", facadeType.name());
        } else {
            nbtRoot.setString("facadeId", "null");
        }
        nbtRoot.setInteger("facadeMeta", facadeMeta);
        nbtRoot.setShort("nbtVersion", NBT_VERSION);
    }

    @Override
    public void readCommon(NBTTagCompound nbtRoot) {
        short nbtVersion = nbtRoot.getShort("nbtVersion");

        conduits.clear();
        cachedCollidables.clear();
        NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");
        if (conduitTags != null) {
            for (int i = 0; i < conduitTags.tagCount(); i++) {
                NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
                IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
                if (conduit != null) {
                    conduit.setBundle(this);
                    conduits.add(conduit);
                }
            }
        }
        String fs = nbtRoot.getString("facadeId");
        if (fs == null || "null".equals(fs)) {
            facadeId = null;
            facadeType = FacadeType.BASIC;
        } else {
            facadeId = Block.getBlockFromName(fs);
            if (nbtRoot.hasKey("facadeType")) { // backwards compat, never true in freshly placed bundles
                facadeType = FacadeType.valueOf(nbtRoot.getString("facadeType"));
            }
        }
        facadeMeta = nbtRoot.getInteger("facadeMeta");

        if (worldObj != null && worldObj.isRemote) {
            boolean stableConduit = true;
            boolean itemConduitClientUpdated = false;
            for (Entity e : Minecraft.getMinecraft().theWorld.playerEntities) {
                if (e.getDistanceSq(this.xCoord, yCoord, zCoord) < 25) {
                    itemConduitClientUpdated = true;
                    break;
                }
            }
            if (itemConduitClientUpdated) clientUpdated = true;
        }
    }

    @Override
    public void onChunkUnload() {
        for (IConduit conduit : conduits) {
            conduit.onChunkUnload(worldObj);
        }
    }

    @Override
    public void doUpdate() {
        for (IConduit conduit : conduits) {
            conduit.updateEntity(worldObj);
        }

        if (conduitsDirty) {
            doConduitsDirty();
        }

        // client side only, check for changes in rendering of the bundle
        if (worldObj.isRemote) {
            updateEntityClient();
        }
    }

    private void doConduitsDirty() {
        if (!worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
        conduitsDirty = false;
    }

    private void updateEntityClient() {
        boolean markForUpdate = false;
        if (clientUpdated) {
            markForUpdate = true;
            clientUpdated = false;
        }

        ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(
            EnderIO.proxy.getClientPlayer()
                .getCurrentEquippedItem());
        if (curMode != lastMode) {
            markForUpdate = true;
            lastMode = curMode;
        }

        if (markForUpdate) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void onNeighborBlockChange(Block blockId) {
        boolean needsUpdate = false;
        for (IConduit conduit : conduits) {
            needsUpdate |= conduit.onNeighborBlockChange(blockId);
        }
        if (needsUpdate) {
            dirty();
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        boolean needsUpdate = false;
        for (IConduit conduit : conduits) {
            needsUpdate |= conduit.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
        }
        if (needsUpdate) {
            dirty();
        }
    }

    @Override
    public TileConduitBundle getEntity() {
        return this;
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(this);
    }

    @Override
    public World getWorld() {
        return getWorldObj();
    }

    @Override
    public boolean hasType(Class<? extends IConduit> type) {
        return getConduit(type) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IConduit> T getConduit(Class<T> type) {
        if (type == null) {
            return null;
        }
        for (IConduit conduit : conduits) {
            if (type.isInstance(conduit)) {
                return (T) conduit;
            }
        }
        return null;
    }

    @Override
    public void addConduit(IConduit conduit) {
        if (worldObj.isRemote) {
            return;
        }
        conduits.add(conduit);
        conduit.setBundle(this);
        conduit.onAddedToBundle();
        dirty();
    }

    @Override
    public void removeConduit(IConduit conduit) {
        if (conduit != null) {
            removeConduit(conduit, true);
        }
    }

    public void removeConduit(IConduit conduit, boolean notify) {
        if (worldObj.isRemote) {
            return;
        }
        conduit.onRemovedFromBundle();
        conduits.remove(conduit);
        conduit.setBundle(null);
        if (notify) {
            dirty();
        }
    }

    @Override
    public void onBlockRemoved() {
        if (worldObj.isRemote) {
            return;
        }
        List<IConduit> copy = new ArrayList<>(conduits);
        for (IConduit con : copy) {
            removeConduit(con, false);
        }
        dirty();
    }

    @Override
    public Collection<IConduit> getConduits() {
        return conduits;
    }

    @Override
    public Set<ForgeDirection> getConnections(Class<? extends IConduit> type) {
        IConduit con = getConduit(type);
        if (con != null) {
            return con.getConduitConnections();
        }
        return null;
    }

    @Override
    public boolean containsConnection(Class<? extends IConduit> type, ForgeDirection dir) {
        IConduit con = getConduit(type);
        if (con != null) {
            return con.containsConduitConnection(dir);
        }
        return false;
    }

    @Override
    public boolean containsConnection(ForgeDirection dir) {
        for (IConduit con : conduits) {
            if (con.containsConduitConnection(dir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<ForgeDirection> getAllConnections() {
        EnumSet<ForgeDirection> result = EnumSet.noneOf(ForgeDirection.class);
        for (IConduit con : conduits) {
            result.addAll(con.getConduitConnections());
        }
        return result;
    }

    // Geometry

    @Override
    public Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir) {
        if (getConnectionCount(dir) < 2) {
            return Offset.NONE;
        }
        return Offsets.get(type, dir);
    }

    @Override
    public List<CollidableComponent> getCollidableComponents() {

        for (IConduit con : conduits) {
            collidablesDirty = collidablesDirty || con.haveCollidablesChangedSinceLastCall();
        }
        if (collidablesDirty) {
            connectorsDirty = true;
        }
        if (!collidablesDirty && !cachedCollidables.isEmpty()) {
            return cachedCollidables;
        }
        cachedCollidables.clear();
        for (IConduit conduit : conduits) {
            cachedCollidables.addAll(conduit.getCollidableComponents());
        }

        addConnectors(cachedCollidables);

        collidablesDirty = false;

        return cachedCollidables;
    }

    @Override
    public List<CollidableComponent> getConnectors() {
        List<CollidableComponent> result = new ArrayList<>();
        addConnectors(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void addConnectors(List<CollidableComponent> result) {

        if (conduits.isEmpty()) {
            return;
        }

        for (IConduit con : conduits) {
            boolean b = con.haveCollidablesChangedSinceLastCall();
            collidablesDirty = collidablesDirty || b;
            connectorsDirty = connectorsDirty || b;
        }

        if (!connectorsDirty && !cachedConnectors.isEmpty()) {
            result.addAll(cachedConnectors);
            return;
        }

        cachedConnectors.clear();

        // TODO: What an unholly mess! (and it doesn't even work correctly...)
        List<CollidableComponent> coreBounds = new ArrayList<CollidableComponent>();
        for (IConduit con : conduits) {
            addConduitCores(coreBounds, con);
        }
        cachedConnectors.addAll(coreBounds);
        result.addAll(coreBounds);

        // 1st algorithm
        List<CollidableComponent> conduitsBounds = new ArrayList<CollidableComponent>();
        for (IConduit con : conduits) {
            conduitsBounds.addAll(con.getCollidableComponents());
            addConduitCores(conduitsBounds, con);
        }

        Set<Class<IConduit>> collidingTypes = new HashSet<Class<IConduit>>();
        for (CollidableComponent conCC : conduitsBounds) {
            for (CollidableComponent innerCC : conduitsBounds) {
                if (conCC != innerCC && conCC.bound.intersects(innerCC.bound)) {
                    collidingTypes.add((Class<IConduit>) conCC.conduitType);
                }
            }
        }

        // TODO: Remove the core geometries covered up by this as no point in rendering these
        if (!collidingTypes.isEmpty()) {
            List<CollidableComponent> colCores = new ArrayList<CollidableComponent>();
            for (Class<IConduit> c : collidingTypes) {
                IConduit con = getConduit(c);
                if (con != null) {
                    addConduitCores(colCores, con);
                }
            }

            BoundingBox bb = null;
            for (CollidableComponent cBB : colCores) {
                if (bb == null) {
                    bb = cBB.bound;
                } else {
                    bb = bb.expandBy(cBB.bound);
                }
            }
            if (bb != null) {
                bb = bb.scale(1.05, 1.05, 1.05);
                CollidableComponent cc = new CollidableComponent(
                    null,
                    bb,
                    ForgeDirection.UNKNOWN,
                    ConduitConnectorType.INTERNAL);
                result.add(cc);
                cachedConnectors.add(cc);
            }
        }

        // 2nd algorithm
        for (IConduit con : conduits) {

            if (con.hasConnections()) {
                List<CollidableComponent> cores = new ArrayList<CollidableComponent>();
                addConduitCores(cores, con);
                if (cores.size() > 1) {
                    BoundingBox bb = cores.get(0).bound;
                    float area = bb.getArea();
                    for (CollidableComponent cc : cores) {
                        bb = bb.expandBy(cc.bound);
                    }
                    if (bb.getArea() > area * 1.5f) {
                        bb = bb.scale(1.05, 1.05, 1.05);
                        CollidableComponent cc = new CollidableComponent(
                            null,
                            bb,
                            ForgeDirection.UNKNOWN,
                            ConduitConnectorType.INTERNAL);
                        result.add(cc);
                        cachedConnectors.add(cc);
                    }
                }
            }
        }

        // Merge all internal conduit connectors into one box
        BoundingBox conBB = null;
        for (int i = 0; i < result.size(); i++) {
            CollidableComponent cc = result.get(i);
            if (cc.conduitType == null && cc.data == ConduitConnectorType.INTERNAL) {
                conBB = conBB == null ? cc.bound : conBB.expandBy(cc.bound);
                result.remove(i);
                i--;
                cachedConnectors.remove(cc);
            }
        }

        if (conBB != null) {
            CollidableComponent cc = new CollidableComponent(
                null,
                conBB,
                ForgeDirection.UNKNOWN,
                ConduitConnectorType.INTERNAL);
            result.add(cc);
            cachedConnectors.add(cc);
        }

        // External Connectors
        EnumSet<ForgeDirection> externalDirs = EnumSet.noneOf(ForgeDirection.class);
        for (IConduit con : conduits) {
            Set<ForgeDirection> extCons = con.getExternalConnections();
            if (extCons != null) {
                for (ForgeDirection dir : extCons) {
                    if (con.getConnectionMode(dir) != ConnectionMode.DISABLED) {
                        externalDirs.add(dir);
                    }
                }
            }
        }
        for (ForgeDirection dir : externalDirs) {
            BoundingBox bb = ConduitGeometryUtil.instance.getExternalConnectorBoundingBox(dir);
            CollidableComponent cc = new CollidableComponent(null, bb, dir, ConduitConnectorType.EXTERNAL);
            result.add(cc);
            cachedConnectors.add(cc);
        }

        connectorsDirty = false;
    }

    private void addConduitCores(List<CollidableComponent> result, IConduit con) {
        CollidableCache cc = CollidableCache.instance;
        Class<? extends IConduit> type = con.getCollidableType();
        if (con.hasConnections()) {
            for (ForgeDirection dir : con.getExternalConnections()) {
                result.addAll(
                    cc.getCollidables(
                        cc.createKey(type, getOffset(con.getBaseConduitType(), dir), ForgeDirection.UNKNOWN, false),
                        con));
            }
            for (ForgeDirection dir : con.getConduitConnections()) {
                result.addAll(
                    cc.getCollidables(
                        cc.createKey(type, getOffset(con.getBaseConduitType(), dir), ForgeDirection.UNKNOWN, false),
                        con));
            }
        } else {
            result.addAll(
                cc.getCollidables(
                    cc.createKey(
                        type,
                        getOffset(con.getBaseConduitType(), ForgeDirection.UNKNOWN),
                        ForgeDirection.UNKNOWN,
                        false),
                    con));
        }
    }

    private int getConnectionCount(ForgeDirection dir) {
        if (dir == ForgeDirection.UNKNOWN) {
            return conduits.size();
        }
        int result = 0;
        for (IConduit con : conduits) {
            if (con.containsConduitConnection(dir) || con.containsExternalConnection(dir)) {
                result++;
            }
        }
        return result;
    }
}
