package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import static ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayStructure.STRUCTURE_DEFINITION_TIER_3;
import static ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayStructure.STRUCTURE_TIER_3;
import static ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TESolarArrayT3 extends TESolarArray {

    public static EnvironmentalConfig.SolarArrayConfig config = EnvironmentalConfig.solarArrayConfig;

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
        return config.peakEnergyTier3;
    }

    @Override
    public int getEnergyPerTick() {
        return getEnergyGen();
    }
}
