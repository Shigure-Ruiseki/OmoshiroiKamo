package ruiseki.omoshiroikamo.module.ids.common.block;

import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.tree.BlockLogOK;

public class BlockMenrilLog extends BlockLogOK {

    public BlockMenrilLog() {
        super(ModObject.MENRIL_LOG.name);
        setTextureName(Reference.PREFIX_MOD + "ids/menril_log");
    }
}
