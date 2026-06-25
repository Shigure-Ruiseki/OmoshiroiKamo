package ruiseki.omoshiroikamo.module.ids.common.block;

import net.minecraft.block.material.Material;

import ruiseki.okcore.block.BlockOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;

public class BlockMenrilPlanks extends BlockOK {

    public BlockMenrilPlanks() {
        super(ModObject.MENRIL_PLANKS.name, Material.wood);
        setTextureName(Reference.PREFIX_MOD + "ids/menril_planks");
    }
}
