package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.STRUCTURE_TIER_4;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconStructure.STRUCTURE_DEFINITION_TIER_4;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.config.backport.muliblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class TEQuantumBeaconT4 extends TEQuantumBeacon {

    public TEQuantumBeaconT4() {
        super(400000);
    }

    @Override
    protected int maxPotionLevel(String attribute) {
        QuantumBeaconConfig.BeaconTierConfig config = QuantumBeaconConfig.tier4;
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
    protected IStructureDefinition<TEQuantumBeaconT4> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_4;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_4;
    }

    @Override
    public int getTier() {
        return 4;
    }
}
