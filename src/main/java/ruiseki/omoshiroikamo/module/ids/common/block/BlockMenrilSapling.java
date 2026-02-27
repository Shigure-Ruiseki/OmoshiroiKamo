package ruiseki.omoshiroikamo.module.ids.common.block;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.tree.BlockSaplingOK;
import ruiseki.omoshiroikamo.module.ids.common.world.gen.WorldGeneratorMenrilTree;

public class BlockMenrilSapling extends BlockSaplingOK {

    public BlockMenrilSapling() {
        super(ModObject.blockMenrilSapling.unlocalisedName, new WorldGeneratorMenrilTree(false));
        setTextureName("ids/menril_sapling");
    }
}
