package ruiseki.omoshiroikamo.module.ids.common.block;

import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.tree.BlockSaplingOK;
import ruiseki.omoshiroikamo.module.ids.common.world.gen.WorldGeneratorMenrilTree;

public class BlockMenrilSapling extends BlockSaplingOK {

    public BlockMenrilSapling() {
        super(ModObject.MENRIL_SAPLING.name, new WorldGeneratorMenrilTree(false));
        setTextureName(Reference.PREFIX_MOD + "ids/menril_sapling");
    }
}
