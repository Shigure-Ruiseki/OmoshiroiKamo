package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.world.IBlockAccess;

import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

/**
 * TODO List:
 * - Add variant types (reinforced, vented, circuitry, etc.)
 * - Add crafting recipe
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
    public int getRenderColor(int meta) {
        return MachineryConfig.getDefaultTintColorInt();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }
}
