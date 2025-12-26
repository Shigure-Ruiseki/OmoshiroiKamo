package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.STRUCTURE_TIER_3;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayStructure.STRUCTURE_DEFINITION_TIER_3;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.muliblock.SolarArrayConfig;

public class TESolarArrayT3 extends TESolarArray {

    public TESolarArrayT3() {
        super(getEnergyGen());
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_3;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    protected IStructureDefinition<TESolarArrayT3> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_3;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    public static int getEnergyGen() {
        return SolarArrayConfig.peakEnergyTier3;
    }

    @Override
    public int getEnergyPerTick() {
        return getEnergyGen();
    }
}
