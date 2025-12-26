package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.STRUCTURE_TIER_5;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayStructure.STRUCTURE_DEFINITION_TIER_5;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.muliblock.SolarArrayConfig;

public class TESolarArrayT5 extends TESolarArray {

    public TESolarArrayT5() {
        super(getEnergyGen());
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_5;
    }

    @Override
    public int getTier() {
        return 5;
    }

    @Override
    protected IStructureDefinition<TESolarArrayT5> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_5;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    public static int getEnergyGen() {
        return SolarArrayConfig.peakEnergyTier5;
    }

    @Override
    public int getEnergyPerTick() {
        return getEnergyGen();
    }
}
