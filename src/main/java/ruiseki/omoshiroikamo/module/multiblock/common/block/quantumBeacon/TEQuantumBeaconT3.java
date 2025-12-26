package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.STRUCTURE_TIER_3;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconStructure.STRUCTURE_DEFINITION_TIER_3;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class TEQuantumBeaconT3 extends TEQuantumBeacon {

    public TEQuantumBeaconT3() {
        super(300000);
    }

    @Override
    protected int maxPotionLevel(String attribute) {
        QuantumBeaconConfig.BeaconTierConfig config = QuantumBeaconConfig.tier3;
        if (attribute == ModifierAttribute.P_SATURATION.getAttributeName()) {
            return config.getSaturationLevel();
        }
        if (attribute == ModifierAttribute.P_SPEED.getAttributeName()) {
            return config.getSpeedLevel();
        }
        if (attribute == ModifierAttribute.P_HASTE.getAttributeName()) {
            return config.getHasteLevel();
        }
        if (attribute == ModifierAttribute.P_STRENGTH.getAttributeName()) {
            return config.getStrengthLevel();
        }
        if (attribute == ModifierAttribute.P_REGEN.getAttributeName()) {
            return config.getRegenerationLevel();
        }
        if (attribute == ModifierAttribute.P_RESISTANCE.getAttributeName()) {
            return config.getResistanceLevel();
        }
        if (attribute == ModifierAttribute.P_FIRE_RESISTANCE.getAttributeName()) {
            return config.getFireResistanceLevel();
        }
        if (attribute == ModifierAttribute.P_WATER_BREATHING.getAttributeName()) {
            return config.getWaterBreathingLevel();
        }
        if (attribute == ModifierAttribute.P_NIGHT_VISION.getAttributeName()) {
            return config.getNightVisionLevel();
        }
        if (attribute == ModifierAttribute.P_JUMP_BOOST.getAttributeName()) {
            return config.getJumpBoostLevel();
        }
        return 0;
    }

    @Override
    protected IStructureDefinition<TEQuantumBeaconT3> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_3;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_3;
    }

    @Override
    public int getTier() {
        return 3;
    }
}
