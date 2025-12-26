package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.STRUCTURE_TIER_2;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconStructure.STRUCTURE_DEFINITION_TIER_2;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.muliblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class TEQuantumBeaconT2 extends TEQuantumBeacon {

    public TEQuantumBeaconT2() {
        super(200000);
    }

    @Override
    protected int maxPotionLevel(String attribute) {
        QuantumBeaconConfig.BeaconTierConfig config = QuantumBeaconConfig.tier2;
        if (attribute == ModifierAttribute.P_SATURATION.getAttributeName()) {
            return config.saturationLevel;
        }
        if (attribute == ModifierAttribute.P_SPEED.getAttributeName()) {
            return config.speedLevel;
        }
        if (attribute == ModifierAttribute.P_HASTE.getAttributeName()) {
            return config.hasteLevel;
        }
        if (attribute == ModifierAttribute.P_STRENGTH.getAttributeName()) {
            return config.strengthLevel;
        }
        if (attribute == ModifierAttribute.P_REGEN.getAttributeName()) {
            return config.regenerationLevel;
        }
        if (attribute == ModifierAttribute.P_RESISTANCE.getAttributeName()) {
            return config.resistanceLevel;
        }
        if (attribute == ModifierAttribute.P_FIRE_RESISTANCE.getAttributeName()) {
            return config.fireResistanceLevel;
        }
        if (attribute == ModifierAttribute.P_WATER_BREATHING.getAttributeName()) {
            return config.waterBreathingLevel;
        }
        if (attribute == ModifierAttribute.P_NIGHT_VISION.getAttributeName()) {
            return config.nightVisionLevel;
        }
        if (attribute == ModifierAttribute.P_JUMP_BOOST.getAttributeName()) {
            return config.jumpBoostLevel;
        }
        return 0;
    }

    @Override
    protected IStructureDefinition<TEQuantumBeaconT2> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_2;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_2;
    }

    @Override
    public int getTier() {
        return 2;
    }
}
