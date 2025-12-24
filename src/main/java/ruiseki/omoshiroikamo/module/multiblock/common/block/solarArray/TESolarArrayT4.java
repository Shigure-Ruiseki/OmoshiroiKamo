package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.STRUCTURE_TIER_4;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayStructure.STRUCTURE_DEFINITION_TIER_4;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TESolarArrayT4 extends TESolarArray {

    public static EnvironmentalConfig.SolarArrayConfig config = EnvironmentalConfig.solarArrayConfig;

    public TESolarArrayT4() {
        super(getEnergyGen());
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_4;
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    protected IStructureDefinition<TESolarArrayT4> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_4;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    public static int getEnergyGen() {
        return config.peakEnergyTier4;
    }

    @Override
    public int getEnergyPerTick() {
        return getEnergyGen();
    }
}
