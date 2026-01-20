package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;

import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

/**
 * Machine Casing - basic structural block for Modular Machinery.
 * Used as the main building block for machine structures.
 * 
 * TODO List:
 * - Implement BlockColor tinting for machine color customization
 * - Add variant types (reinforced, vented, circuitry, etc.)
 * - Add crafting recipe
 * - Consider adding TileEntity for dynamic color from controller
 */
public class BlockMachineCasing extends BlockOK implements IModularBlockTint {

    protected BlockMachineCasing() {
        super("modularMachineCasing");
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineCasing create() {
        return new BlockMachineCasing();
    }

    @Override
    public void init() {
        super.init();

        BlockColor.registerBlockColors(new IModularBlockTint() {

            @Override
            public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (tintIndex == 0) {
                    // Get color from cache
                    Integer structureColor = StructureTintCache.get(world, x, y, z);
                    if (structureColor != null) {
                        return structureColor;
                    }
                    // Fall back to config color
                    return MachineryConfig.getDefaultTintColorInt();
                }
                return 0xFFFFFFFF; // White for non-tinted layers
            }

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0) {
                    // Items always use config color (no structure context)
                    return MachineryConfig.getDefaultTintColorInt();
                }
                return 0xFFFFFFFF; // White for non-tinted layers
            }
        }, this);
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }
}
