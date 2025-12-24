package ruiseki.omoshiroikamo.module.dml.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;

public class BlockMachineCasing extends BlockOK {

    protected BlockMachineCasing() {
        super(ModObject.blockMachineCasing.unlocalisedName, Material.rock);
    }

    public static BlockMachineCasing create() {
        return new BlockMachineCasing();
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return false;
    }
}
