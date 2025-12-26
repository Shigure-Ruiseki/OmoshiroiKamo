package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.STRUCTURE_TIER_6;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayStructure.STRUCTURE_DEFINITION_TIER_6;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.multiblock.SolarArrayConfig;

public class TESolarArrayT6 extends TESolarArray {

    public TESolarArrayT6() {
        super(getEnergyGen());
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_6;
    }

    @Override
    public int getTier() {
        return 6;
    }

    @Override
    protected IStructureDefinition<TESolarArrayT6> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_6;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    public static int getEnergyGen() {
        return SolarArrayConfig.tierEnergy.tier6;
    }

    @Override
    public int getEnergyPerTick() {
        return getEnergyGen();
    }
}
