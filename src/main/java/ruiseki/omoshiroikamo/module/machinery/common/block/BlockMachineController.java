package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Controller - the brain of a Modular Machinery structure.
 * This block is mapped to the 'Q' symbol in structure definitions.
 * TODO: Working particles and sound effects
 * TODO: Completion effects (particles, sounds)
 * TODO: Block state visual changes based on status
 */
public class BlockMachineController extends AbstractBlock<TEMachineController> implements IModularBlockTint {

    protected BlockMachineController() {
        super("modularMachineController", TEMachineController.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineController create() {
        return new BlockMachineController();
    }

    private IIcon overlayIcon;
    private IIcon sideOverlayIcon;

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        // Get color from cache
        Integer structureColor = StructureTintCache.get(world, x, y, z);
        if (structureColor != null) {
            return structureColor;
        }
        // Fall back to config color
        return MachineryConfig.getDefaultTintColorInt();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        this.overlayIcon = reg.registerIcon("omoshiroikamo:modularmachineryOverlay/overlay_machine_controller");
        this.sideOverlayIcon = reg.registerIcon("omoshiroikamo:modularmachineryOverlay/base_modularports");
    }

    public IIcon getOverlayIcon() {
        return overlayIcon;
    }

    public IIcon getSideOverlayIcon() {
        return sideOverlayIcon;
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public int getRenderType() {
        return AbstractPortBlock.portRendererId;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TEMachineController te = (TEMachineController) world.getTileEntity(x, y, z);
        if (te == null) return;

        // Preserve TileEntity data but avoid block metadata-based facing.
        te.readFromItemStack(stack);
        world.markBlockForUpdate(x, y, z);

        ForgeDirection direction;
        Rotation rotation = Rotation.NORMAL;
        float pitch = placer.rotationPitch;
        int heading = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (pitch > 50.0F) {
            direction = ForgeDirection.UP;
            // Determine rotation based on heading for UP
            // 0: South (Z+), 1: West (X-), 2: North (Z-), 3: East (X+)
            rotation = switch (heading) {
                case 0 -> Rotation.UPSIDE_DOWN; // South
                case 1 -> Rotation.COUNTER_CLOCKWISE; // West
                case 2 -> Rotation.NORMAL; // North
                case 3 -> Rotation.CLOCKWISE; // East
                default -> Rotation.NORMAL;
            };
        } else if (pitch < -50.0F) {
            direction = ForgeDirection.DOWN;
            // Determine rotation based on heading for DOWN
            rotation = switch (heading) {
                case 0 -> Rotation.NORMAL; // South
                case 1 -> Rotation.COUNTER_CLOCKWISE; // West
                case 2 -> Rotation.UPSIDE_DOWN; // North
                case 3 -> Rotation.CLOCKWISE; // East
                default -> Rotation.NORMAL;
            };
        } else {
            // Horizontal facing
            direction = switch (heading) {
                case 0 -> ForgeDirection.NORTH;
                case 1 -> ForgeDirection.EAST;
                case 2 -> ForgeDirection.SOUTH;
                case 3 -> ForgeDirection.WEST;
                default -> ForgeDirection.NORTH;
            };
            rotation = Rotation.NORMAL;
        }

        te.setExtendedFacing(ExtendedFacing.of(direction, rotation, Flip.NONE));
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        // TODO: Display machine name and type
        // TODO: Show current state (Idle, Working, Error)
        // TODO: Show crafting progress percentage
        // TODO: Show energy consumption rate
        // TODO: Display error message if in error state
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TEMachineController controller = (TEMachineController) world.getTileEntity(x, y, z);
        if (controller != null) {
            ItemStack blueprint = controller.getBlueprintStack();
            if (blueprint != null && blueprint.stackSize > 0) {
                dropStack(world, x, y, z, blueprint.copy());
                controller.getInventory()
                    .setStackInSlot(TEMachineController.BLUEPRINT_SLOT, null);
            }
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(this, 1, 0);
    }

}
