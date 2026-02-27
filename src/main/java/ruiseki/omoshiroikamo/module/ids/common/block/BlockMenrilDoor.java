package ruiseki.omoshiroikamo.module.ids.common.block;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.BlockDoorOK;

public class BlockMenrilDoor extends BlockDoorOK {

    public BlockMenrilDoor() {
        super(ModObject.blockMenrilDoor.unlocalisedName);
        setBlockTextureName("ids/door_menril");
    }
}
