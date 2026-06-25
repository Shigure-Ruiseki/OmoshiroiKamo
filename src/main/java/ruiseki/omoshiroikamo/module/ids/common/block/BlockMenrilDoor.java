package ruiseki.omoshiroikamo.module.ids.common.block;

import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.BlockDoorOK;

public class BlockMenrilDoor extends BlockDoorOK {

    public BlockMenrilDoor() {
        super(ModObject.MENRIL_DOOR.name);
        setBlockTextureName(Reference.PREFIX_MOD + "ids/door_menril");
    }
}
