package ruiseki.omoshiroikamo.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;

public class ItemBlockOK extends ItemBlockWithMetadata {

    public ItemBlockOK(Block blockA, Block blockB) {
        super(blockA, blockB);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public ItemBlockOK(Block blockA) {
        this(blockA, null);
    }
}
