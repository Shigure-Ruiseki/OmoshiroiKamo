package ruiseki.omoshiroikamo.module.cable.common.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.Util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.CollidableComponent;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.ConduitConnectorType;
import ruiseki.omoshiroikamo.module.cable.common.conduit.packet.PacketConnectionMode;
import ruiseki.omoshiroikamo.module.cable.common.conduit.packet.PacketExtractMode;

public class BlockConduitBundle extends BlockOK {

    private static final String KEY_CONNECTOR_ICON = "enderIO:conduitConnector";
    private static final String KEY_CONNECTOR_ICON_EXTERNAL = "enderIO:conduitConnectorExternal";

    public static BlockConduitBundle create() {

        PacketHandler.INSTANCE
            .registerMessage(PacketExtractMode.class, PacketExtractMode.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
            PacketConnectionMode.class,
            PacketConnectionMode.class,
            PacketHandler.nextID(),
            Side.SERVER);

        BlockConduitBundle result = new BlockConduitBundle();
        result.init();
        return result;
    }

    public static int rendererId = -1;

    private IIcon connectorIcon, connectorIconExternal;

    private IIcon lastRemovedComponetIcon = null;

    private final Random rand = new Random();

    protected BlockConduitBundle() {
        super(ModObject.blockConduitBundle.unlocalisedName, TileConduitBundle.class);
        setBlockBounds(0.334f, 0.334f, 0.334f, 0.667f, 0.667f, 0.667f);
        setHardness(1.5f);
        setResistance(10.0f);
        setCreativeTab(null);
        this.stepSound = new SoundType("silence", 0, 0) {

            @Override
            public String getBreakSound() {
                return "EnderIO:" + soundName + ".dig";
            }

            @Override
            public String getStepResourcePath() {
                return "EnderIO:" + soundName + ".step";
            }
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {

        IIcon tex = null;

        TileEntity cbe = world.getTileEntity(target.blockX, target.blockY, target.blockZ);
        if (!(cbe instanceof TileConduitBundle cb)) {
            return false;
        }
        if (target.hitInfo instanceof CollidableComponent cc) {
            IConduit con = cb.getConduit(cc.conduitType);
            if (con != null) {
                tex = con.getTextureForState(cc);
            }
        }
        if (tex == null) {
            tex = blockIcon;
        }
        lastRemovedComponetIcon = tex;
        addBlockHitEffects(world, effectRenderer, target.blockX, target.blockY, target.blockZ, target.sideHit, tex);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {

        IIcon tex = lastRemovedComponetIcon;
        byte b0 = 4;
        for (int j1 = 0; j1 < b0; ++j1) {
            for (int k1 = 0; k1 < b0; ++k1) {
                for (int l1 = 0; l1 < b0; ++l1) {
                    double d0 = x + (j1 + 0.5D) / b0;
                    double d1 = y + (k1 + 0.5D) / b0;
                    double d2 = z + (l1 + 0.5D) / b0;
                    int i2 = rand.nextInt(6);
                    EntityDiggingFX fx = new EntityDiggingFX(
                        world,
                        d0,
                        d1,
                        d2,
                        d0 - x - 0.5D,
                        d1 - y - 0.5D,
                        d2 - z - 0.5D,
                        this,
                        i2,
                        0).applyColourMultiplier(x, y, z);
                    fx.setParticleIcon(tex);
                    effectRenderer.addEffect(fx);
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void addBlockHitEffects(World world, EffectRenderer effectRenderer, int x, int y, int z, int side,
        IIcon tex) {
        float f = 0.1F;
        double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - f * 2.0F)
            + f
            + getBlockBoundsMinX();
        double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - f * 2.0F)
            + f
            + getBlockBoundsMinY();
        double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - f * 2.0F)
            + f
            + getBlockBoundsMinZ();
        if (side == 0) {
            d1 = y + getBlockBoundsMinY() - f;
        } else if (side == 1) {
            d1 = y + getBlockBoundsMaxY() + f;
        } else if (side == 2) {
            d2 = z + getBlockBoundsMinZ() - f;
        } else if (side == 3) {
            d2 = z + getBlockBoundsMaxZ() + f;
        } else if (side == 4) {
            d0 = x + getBlockBoundsMinX() - f;
        } else if (side == 5) {
            d0 = x + getBlockBoundsMaxX() + f;
        }
        EntityDiggingFX digFX = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, this, side, 0);
        digFX.applyColourMultiplier(x, y, z)
            .multiplyVelocity(0.2F)
            .multipleParticleScaleBy(0.6F);
        digFX.setParticleIcon(tex);
        effectRenderer.addEffect(digFX);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return getPickBlock(target, world, x, y, z, null);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        ItemStack ret = null;
        if (target != null && target.hitInfo instanceof CollidableComponent cc) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileConduitBundle bundle)) return null;
            IConduit conduit = bundle.getConduit(cc.conduitType);
            if (conduit != null) {
                ret = conduit.createItem();
            }
        }
        return ret;
    }

    @Override
    public int quantityDropped(Random r) {
        return 0;
    }

    public IIcon getConnectorIcon(Object data) {
        return data == ConduitConnectorType.EXTERNAL ? connectorIconExternal : connectorIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister IIconRegister) {
        connectorIcon = IIconRegister.registerIcon(KEY_CONNECTOR_ICON);
        connectorIconExternal = IIconRegister.registerIcon(KEY_CONNECTOR_ICON_EXTERNAL);
        blockIcon = connectorIcon;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return rendererId;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IConduitBundle con)) return super.getLightValue(world, x, y, z);
        Collection<IConduit> conduits = con.getConduits();
        int result = 0;
        for (IConduit conduit : conduits) {
            result += conduit.getLightValue();
        }
        return result;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IConduitBundle ? blockHardness * 10 : blockHardness;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        float resist = getExplosionResistance(par1Entity);
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IConduitBundle ? resist * 10 : resist;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        TileEntity rte = world.getTileEntity(x, y, z);
        if (!(rte instanceof IConduitBundle te)) return true;
        boolean breakBlock;
        List<ItemStack> drop = new ArrayList<ItemStack>();

        List<RaytraceResult> results = doRayTraceAll(world, x, y, z, player);
        RaytraceResult.sort(Util.getEyePosition(player), results);
        for (RaytraceResult rt : results) {
            if (breakConduit(te, drop, rt, player)) {
                break;
            }
        }

        breakBlock = te.getConduits()
            .isEmpty();

        if (!breakBlock) {
            world.markBlockForUpdate(x, y, z);
        }

        if (!world.isRemote && !player.capabilities.isCreativeMode) {
            for (ItemStack st : drop) {
                Util.dropItems(world, st, x, y, z, false);
            }
        }

        if (breakBlock) {
            world.setBlockToAir(x, y, z);
            return true;
        }
        return false;
    }

    private boolean breakConduit(IConduitBundle te, List<ItemStack> drop, RaytraceResult rt, EntityPlayer player) {
        if (rt == null || rt.component == null) {
            return false;
        }
        Class<? extends IConduit> type = rt.component.conduitType;
        if (!ConduitUtil.renderConduit(player, type)) {
            return false;
        }

        if (type == null) {
            // broke a conector so drop any conduits with no connections as there
            // is no other way to remove these
            List<IConduit> cons = new ArrayList<IConduit>(te.getConduits());
            boolean droppedUnconected = false;
            for (IConduit con : cons) {
                if (con.getConduitConnections()
                    .isEmpty()
                    && con.getExternalConnections()
                        .isEmpty()
                    && ConduitUtil.renderConduit(player, con)) {
                    te.removeConduit(con);
                    drop.addAll(con.getDrops());
                    droppedUnconected = true;
                }
            }
            // If there isn't, then drop em all
            if (!droppedUnconected) {
                for (IConduit con : cons) {
                    if (ConduitUtil.renderConduit(player, con)) {
                        te.removeConduit(con);
                        drop.addAll(con.getDrops());
                    }
                }
            }
        } else {
            IConduit con = te.getConduit(type);
            if (con != null) {
                te.removeConduit(con);
                drop.addAll(con.getDrops());
            }
        }

        BlockPos bc = te.getPos();
        ConduitUtil.playBreakSound(Block.soundTypeMetal, te.getWorld(), bc.x, bc.y, bc.z);

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof IConduitBundle te)) return;
        te.onBlockRemoved();
        world.removeTileEntity(x, y, z);
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        ITool tool = ToolUtil.getEquippedTool(player);
        if (!player.isSneaking() || tool == null || !tool.canUse(player.getCurrentEquippedItem(), player, x, y, z)) {
            return;
        }
        ConduitUtil.openConduitGui(world, x, y, z, player);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7,
        float par8, float par9) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IConduitBundle bundle)) return false;

        ItemStack stack = player.getCurrentEquippedItem();
        if (ConduitUtil.isConduitEquipped(player)) {
            // Add conduit
            if (player.isSneaking()) {
                return false;
            }
            if (handleConduitClick(world, x, y, z, player, bundle, stack)) {
                return true;
            }

        } else if (ToolUtil.isToolEquipped(player) && player.isSneaking()) {
            // Break conduit with tool
            if (handleWrenchClick(world, x, y, z, player)) {
                return true;
            }
        }

        // Check conduit defined actions
        RaytraceResult closest = doRayTrace(world, x, y, z, player);
        List<RaytraceResult> all = null;
        if (closest != null) {
            all = doRayTraceAll(world, x, y, z, player);
        }

        if (closest != null && closest.component != null && closest.component.data instanceof ConduitConnectorType) {

            ConduitConnectorType conType = (ConduitConnectorType) closest.component.data;
            if (conType == ConduitConnectorType.INTERNAL) {
                boolean result = false;
                // if its a connector pass the event on to all conduits
                for (IConduit con : bundle.getConduits()) {
                    if (ConduitUtil.renderConduit(player, con.getCollidableType())
                        && con.onBlockActivated(player, getHitForConduitType(all, con.getCollidableType()), all)) {
                        bundle.getEntity()
                            .markDirty();
                        result = true;
                    }
                }
                if (result) {
                    return true;
                }
            } else {
                if (!world.isRemote) {
                    player.openGui(
                        EnderIO.instance,
                        GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + closest.component.dir.ordinal(),
                        world,
                        x,
                        y,
                        z);
                }
                return true;
            }
        }

        if (closest == null || closest.component == null || closest.component.conduitType == null && all == null) {
            // Nothing of interest hit
            return false;
        }

        // Conduit specific actions
        if (all != null) {
            RaytraceResult.sort(Util.getEyePosition(player), all);
            for (RaytraceResult rr : all) {
                if (ConduitUtil.renderConduit(player, rr.component.conduitType)
                    && !(rr.component.data instanceof ConduitConnectorType)) {

                    IConduit con = bundle.getConduit(rr.component.conduitType);
                    if (con != null && con.onBlockActivated(player, rr, all)) {
                        bundle.getEntity()
                            .markDirty();
                        return true;
                    }
                }
            }
        } else {
            IConduit closestConduit = bundle.getConduit(closest.component.conduitType);
            if (closestConduit != null && ConduitUtil.renderConduit(player, closestConduit)
                && closestConduit.onBlockActivated(player, closest, all)) {
                bundle.getEntity()
                    .markDirty();
                return true;
            }
        }
        return false;
    }

    private boolean handleWrenchClick(World world, int x, int y, int z, EntityPlayer player) {
        ITool tool = ToolUtil.getEquippedTool(player);
        if (tool != null) {
            if (tool.canUse(player.getCurrentEquippedItem(), player, x, y, z)) {
                if (!world.isRemote) {
                    removedByPlayer(world, player, x, y, z, true);
                    tool.used(player.getCurrentEquippedItem(), player, x, y, z);
                }
                return true;
            }
        }
        return false;
    }

    private boolean handleConduitClick(World world, int x, int y, int z, EntityPlayer player, IConduitBundle bundle,
        ItemStack stack) {
        IConduitItem equipped = (IConduitItem) stack.getItem();
        if (!bundle.hasType(equipped.getBaseConduitType())) {
            if (!world.isRemote) {
                bundle.addConduit(equipped.createConduit(stack, player));
                ConduitUtil.playBreakSound(soundTypeMetal, world, x, y, z);
                if (!player.capabilities.isCreativeMode) {
                    player.getCurrentEquippedItem().stackSize--;
                }
            }
            return true;
        }
        return false;
    }

    private RaytraceResult getHitForConduitType(List<RaytraceResult> all, Class<? extends IConduit> collidableType) {
        for (RaytraceResult rr : all) {
            if (rr.component != null && rr.component.conduitType == collidableType) {
                return rr;
            }
        }
        return null;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IConduitBundle bundle) {
            bundle.onNeighborBlockChange(blockId);
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        TileEntity conduit = world.getTileEntity(x, y, z);
        if (conduit instanceof IConduitBundle bundle) {
            bundle.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb,
        List<AxisAlignedBB> arraylist, Entity par7Entity) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IConduitBundle con)) return;
        Collection<CollidableComponent> bounds = con.getCollidableComponents();
        for (CollidableComponent bnd : bounds) {
            setBlockBounds(
                bnd.bound.minX,
                bnd.bound.minY,
                bnd.bound.minZ,
                bnd.bound.maxX,
                bnd.bound.maxY,
                bnd.bound.maxZ);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, par7Entity);
        }

        if (con.getConduits()
            .isEmpty()) { // just in case
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, par7Entity);
        }

        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {

        TileEntity te = world.getTileEntity(x, y, z);
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (!(te instanceof IConduitBundle con)) return null;

        BoundingBox minBB = new BoundingBox(1, 1, 1, 0, 0, 0);

        List<RaytraceResult> results = doRayTraceAll(world, x, y, z, player);
        Iterator<RaytraceResult> iter = results.iterator();
        while (iter.hasNext()) {
            CollidableComponent component = iter.next().component;
            if (component == null
                || (component.conduitType == null && component.data != ConduitConnectorType.EXTERNAL)) {
                iter.remove();
            }
        }

        if (!minBB.isValid()) {
            RaytraceResult hit = RaytraceResult.getClosestHit(Util.getEyePosition(player), results);
            if (hit != null && hit.component != null && hit.component.bound != null) {
                minBB = hit.component.bound;
                if (hit.component.conduitType == null) {
                    ForgeDirection dir = hit.component.dir.getOpposite();
                    float trans = 0.0125f;
                    minBB = minBB.translate(dir.offsetX * trans, dir.offsetY * trans, dir.offsetZ * trans);
                    float scale = 0.7f;
                    minBB = minBB.scale(
                        1 + Math.abs(dir.offsetX) * scale,
                        1 + Math.abs(dir.offsetY) * scale,
                        1 + Math.abs(dir.offsetZ) * scale);
                } else {
                    minBB = minBB.scale(1.09, 1.09, 1.09);
                }
            }
        }

        if (!minBB.isValid()) {
            minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
        }

        return AxisAlignedBB.getBoundingBox(
            x + minBB.minX,
            y + minBB.minY,
            z + minBB.minZ,
            x + minBB.maxX,
            y + minBB.maxY,
            z + minBB.maxZ);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {

        RaytraceResult raytraceResult = doRayTrace(world, x, y, z, origin, direction, null);
        MovingObjectPosition ret = null;
        if (raytraceResult != null) {
            ret = raytraceResult.movingObjectPosition;
            if (ret != null) {
                ret.hitInfo = raytraceResult.component;
            }
        }

        return ret;
    }

    public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer entityPlayer) {
        List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, entityPlayer);
        if (allHits == null) {
            return null;
        }
        Vec3 origin = Util.getEyePosition(entityPlayer);
        return RaytraceResult.getClosestHit(origin, allHits);
    }

    public List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, EntityPlayer entityPlayer) {
        double pitch = Math.toRadians(entityPlayer.rotationPitch);
        double yaw = Math.toRadians(entityPlayer.rotationYaw);

        double dirX = -Math.sin(yaw) * Math.cos(pitch);
        double dirY = -Math.sin(pitch);
        double dirZ = Math.cos(yaw) * Math.cos(pitch);

        double reachDistance = EnderIO.proxy.getReachDistanceForPlayer(entityPlayer);

        Vec3 origin = Util.getEyePosition(entityPlayer);
        Vec3 direction = origin.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);
        return doRayTraceAll(world, x, y, z, origin, direction, entityPlayer);
    }

    private RaytraceResult doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction,
        EntityPlayer entityPlayer) {
        List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, origin, direction, entityPlayer);
        if (allHits == null) {
            return null;
        }
        return RaytraceResult.getClosestHit(origin, allHits);
    }

    protected List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, Vec3 origin, Vec3 direction,
        EntityPlayer player) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IConduitBundle bundle)) return null;
        List<RaytraceResult> hits = new ArrayList<>();
        if (player == null) {
            player = OmoshiroiKamo.proxy.getClientPlayer();
        }

        ConduitDisplayMode mode = ConduitUtil.getDisplayMode(player);
        Collection<CollidableComponent> components = new ArrayList<>(bundle.getCollidableComponents());
        for (CollidableComponent component : components) {
            if ((component.conduitType != null || mode == ConduitDisplayMode.ALL)
                && ConduitUtil.renderConduit(player, component.conduitType)) {
                setBlockBounds(
                    component.bound.minX,
                    component.bound.minY,
                    component.bound.minZ,
                    component.bound.maxX,
                    component.bound.maxY,
                    component.bound.maxZ);
                MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z, origin, direction);
                if (hitPos != null) {
                    hits.add(new RaytraceResult(component, hitPos));
                }
            }
        }

        setBlockBounds(0, 0, 0, 1, 1, 1);

        return hits;
    }
}
