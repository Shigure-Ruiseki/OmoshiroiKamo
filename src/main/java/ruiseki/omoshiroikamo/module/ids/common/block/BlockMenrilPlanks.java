package ruiseki.omoshiroikamo.module.ids.common.block;

import net.minecraft.block.material.Material;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.BlockOK;

public class BlockMenrilPlanks extends BlockOK {

    public BlockMenrilPlanks() {
        super(ModObject.blockMenrilPlanks.unlocalisedName, Material.wood);
        setTextureName("ids/menril_planks");
    }
}
