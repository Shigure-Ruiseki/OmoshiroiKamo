package ruiseki.omoshiroikamo.common.block.energyConnector;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.TileEntityEnder;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectableISBRH;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockConnectable extends AbstractBlock<TEConnectable> {

    public static String[] blocks = new String[] { "insulator", "connectorULV", "connectorLV", "connectorMV",
        "connectorHV", "connectorEV", "connectorIV", "transformer" };

    public static final int META_insulator = 0;
    public static final int META_connectorULV = 1;
    public static final int META_connectorLV = 2;
    public static final int META_connectorMV = 3;
    public static final int META_connectorHV = 4;
    public static final int META_connectorEV = 5;
    public static final int META_connectorIV = 6;
    public static final int META_transformer = 7;

    protected BlockConnectable() {
        super(ModObject.blockConnectable, null);
    }

    public static BlockConnectable create() {
        return new BlockConnectable();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockConnectable.class, name);
        GameRegistry.registerTileEntity(TEInsulator.class, name + "_insulator");
        GameRegistry.registerTileEntity(TEConnectorULV.class, name + "_connectorULV");
        GameRegistry.registerTileEntity(TEConnectorLV.class, name + "_connectorLV");
        GameRegistry.registerTileEntity(TEConnectorMV.class, name + "_connectorMV");
        GameRegistry.registerTileEntity(TEConnectorHV.class, name + "_connectorHV");
        GameRegistry.registerTileEntity(TEConnectorEV.class, name + "_connectorEV");
        GameRegistry.registerTileEntity(TEConnectorIV.class, name + "_connectorIV");
        GameRegistry.registerTileEntity(TETransformer.class, name + "_transformer");

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < blocks.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        switch (meta) {
            case META_insulator:
                return new TEInsulator();
            case META_connectorULV:
                return new TEConnectorULV();
            case META_connectorLV:
                return new TEConnectorLV();
            case META_connectorMV:
                return new TEConnectorMV();
            case META_connectorHV:
                return new TEConnectorHV();
            case META_connectorEV:
                return new TEConnectorEV();
            case META_connectorIV:
                return new TEConnectorIV();
            case META_transformer:
                return new TETransformer();
        }
        return null;
    }

    @Override
    public int getRenderType() {
        return ConnectableISBRH.renderConnectableId;
    }

    private IIcon[] icons;

    @Override
    public void registerBlockIcons(IIconRegister iIconRegister) {
        icons = new IIcon[blocks.length];

        for (int i = 0; i < blocks.length; i++) {
            String iconName = LibResources.PREFIX_MOD + blocks[i];
            icons[i] = iIconRegister.registerIcon(iconName);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (icons == null || meta < 0 || meta >= icons.length) {
            return blockIcon;
        }
        return icons[meta];
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack stack) {

    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        if (world.isRemote) {
            return;
        }
        TileEntity ent = world.getTileEntity(x, y, z);
        if (!(ent instanceof AbstractTE te)) {
            return;
        }
        if (te instanceof TETransformer) {
            return;
        }
        ForgeDirection fd = ForgeDirection.getOrientation(te.getFacing())
            .getOpposite();
        int nx = x + fd.offsetX;
        int ny = y + fd.offsetY;
        int nz = z + fd.offsetZ;

        if (world.isAirBlock(nx, ny, nz)) {
            world.removeTileEntity(x, y, z);
            Block self = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            self.dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TETransformer transformer) {
            int yaw = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
            ForgeDirection dir;
            switch (yaw) {
                case 0:
                    dir = ForgeDirection.NORTH;
                    break;
                case 1:
                    dir = ForgeDirection.EAST;
                    break;
                case 2:
                    dir = ForgeDirection.SOUTH;
                    break;
                case 3:
                default:
                    dir = ForgeDirection.WEST;
                    break;
            }
            transformer.setFacing((short) dir.ordinal());

            if (!world.isRemote) {
                world.markBlockForUpdate(x, y, z);
            }
            return;
        }

        ForgeDirection direction = null;
        float pitch = player.rotationPitch;

        int dx = x, dy = y, dz = z;
        if (pitch > 60) {
            direction = ForgeDirection.UP;
        } else if (pitch < -60) {
            direction = ForgeDirection.DOWN;
        }
        if (direction == null) {
            int yaw = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
            switch (yaw) {
                case 0:
                    direction = ForgeDirection.NORTH;
                    break;
                case 1:
                    direction = ForgeDirection.EAST;
                    break;
                case 2:
                    direction = ForgeDirection.SOUTH;
                    break;
                case 3:
                default:
                    direction = ForgeDirection.WEST;
                    break;
            }

            ForgeDirection opposite = direction.getOpposite();
            int tx = x + opposite.offsetX;
            int ty = y + opposite.offsetY;
            int tz = z + opposite.offsetZ;
            Block targetBlock = world.getBlock(tx, ty, tz);

            if (targetBlock == null || targetBlock.isAir(world, tx, ty, tz)) {
                direction = null;
            }
        }

        if (direction == null) {
            dy = y + 1;
            if (world.isSideSolid(dx, dy, dz, ForgeDirection.UP, false)) {
                direction = ForgeDirection.DOWN;
            } else {
                dy = y - 1;
                if (world.isSideSolid(dx, dy, dz, ForgeDirection.DOWN, false)) {
                    direction = ForgeDirection.UP;
                } else {
                    world.setBlockToAir(x, y, z);
                    if (player instanceof EntityPlayerMP playerMP) {
                        if (!playerMP.inventory.addItemStackToInventory(stack.copy())) {
                            playerMP.dropPlayerItemWithRandomChoice(stack.copy(), false);
                        }
                    }
                    return;
                }
            }
        }

        if (te instanceof AbstractTE abstractTE) {
            abstractTE.setFacing((short) direction.ordinal());
        }

        if (!world.isRemote) {
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntity ent = world.getTileEntity(x, y, z);
        if (!(ent instanceof AbstractTE te)) {
            return;
        }

        if (te instanceof TETransformer) {
            setBlockBounds(0.125f, 0F, 0.125f, 0.875, 0.75f, 0.875);
            return;
        }

        ForgeDirection dir = ForgeDirection.getOrientation(te.getFacing());
        float length = te instanceof TEConnectorEV ? 0.4375f
            : te instanceof TEConnectorHV ? 0.4375f
                : te instanceof TEConnectorMV ? 0.34375f
                    : te instanceof TEConnectorLV ? 0.34375f : te instanceof TEConnectorULV ? 0.25f : 0.5f;
        switch (dir) {
            case UP:
                setBlockBounds(0.375F, 0F, 0.375F, 0.625F, length, 0.625F);
                break;
            case DOWN:
                setBlockBounds(0.375F, 1 - length, 0.375F, 0.625F, 1F, 0.625F);
                break;
            case NORTH:
                setBlockBounds(0.375F, 0.375F, 1 - length, 0.625F, 0.625F, 1F);
                break;
            case SOUTH:
                setBlockBounds(0.375F, 0.375F, 0F, 0.625F, 0.625F, length);
                break;
            case EAST:
                setBlockBounds(0F, 0.375F, 0.375F, length, 0.625F, 0.625F);
                break;
            case WEST:
                setBlockBounds(1 - length, 0.375F, 0.375F, 1F, 0.625F, 0.625F);
                break;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public static class ItemBlockConnectable extends ItemBlockOK {

        public ItemBlockConnectable(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int meta = stack.getItemDamage();
            String base = super.getUnlocalizedName(stack);

            if (meta >= 0 && meta < BlockConnectable.blocks.length) {
                return base + "." + BlockConnectable.blocks[meta];
            } else {
                return base;
            }
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
            for (int i = 0; i < BlockConnectable.blocks.length; i++) {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

}
