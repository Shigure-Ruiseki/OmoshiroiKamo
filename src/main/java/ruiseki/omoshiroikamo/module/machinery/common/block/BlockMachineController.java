package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Controller - the brain of a Modular Machinery structure.
 * This block is mapped to the 'Q' symbol in structure definitions.
 * When right-clicked, it validates and forms the multiblock structure.
 * Uses JSON model with base + overlay textures via GTNHLib.
 *
 * TODO List:
 * - Implement GUI for machine management and status display
 * - State management (IDLE, WORKING, PAUSED, ERROR)
 * - Redstone control modes (Ignore, Low, High, Pulse)
 * - Working particles and sound effects
 * - Completion effects (particles, sounds)
 * - Block state visual changes based on status
 * - NEI recipe integration
 * - Implement BlockColor tinting for machine-wide color customization
 * - Drop blueprint when broken
 * - Rotate controller texture
 * - Make controller face shows only front side
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

    @Override
    public void init() {
        super.init();
    }

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
    }

    public IIcon getOverlayIcon() {
        return overlayIcon;
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
        float pitch = placer.rotationPitch;
        if (pitch > 60.0F) {
            direction = ForgeDirection.UP;
        } else if (pitch < -60.0F) {
            direction = ForgeDirection.DOWN;
        } else {
            // Calculate facing from player's yaw (block front faces player)
            int facing = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            direction = switch (facing) {
                case 0 -> ForgeDirection.NORTH;
                case 1 -> ForgeDirection.EAST;
                case 2 -> ForgeDirection.SOUTH;
                case 3 -> ForgeDirection.WEST;
                default -> ForgeDirection.NORTH;
            };
        }

        te.setExtendedFacing(ExtendedFacing.of(direction));
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
            }
        }

        super.breakBlock(world, x, y, z, block, meta);
    }
}
