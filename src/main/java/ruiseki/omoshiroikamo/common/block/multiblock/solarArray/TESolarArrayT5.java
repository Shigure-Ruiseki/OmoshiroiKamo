package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import static ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayShapes.STRUCTURE_TIER_5;
import static ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayStructure.STRUCTURE_DEFINITION_TIER_5;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TESolarArrayT5 extends TESolarArray {

    public static EnvironmentalConfig.SolarArrayConfig config = EnvironmentalConfig.solarArrayConfig;

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
        return config.peakEnergyTier5;
    }

    @Override
    public int getEnergyPerTick() {
        return getEnergyGen();
    }
}
