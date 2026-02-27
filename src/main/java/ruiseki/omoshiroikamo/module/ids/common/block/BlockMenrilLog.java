package ruiseki.omoshiroikamo.module.ids.common.block;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.tree.BlockLogOK;

public class BlockMenrilLog extends BlockLogOK {

    public BlockMenrilLog() {
        super(ModObject.blockMenrilLog.unlocalisedName);
        setTextureName("ids/menril_log");
    }
}
