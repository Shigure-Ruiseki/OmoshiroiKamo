package ruiseki.omoshiroikamo.module.dml.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import ruiseki.okcore.block.BlockOK;
import ruiseki.omoshiroikamo.api.enums.ModObject;

public class BlockMachineCasing extends BlockOK {

    protected BlockMachineCasing() {
        super(ModObject.MACHINE_CASING.name, Material.rock);
        isOpaque = false;
    }

    public static BlockMachineCasing create() {
        return new BlockMachineCasing();
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return false;
    }
}
