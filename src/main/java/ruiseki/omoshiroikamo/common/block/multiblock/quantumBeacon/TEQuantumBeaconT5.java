package ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.QuantumBeaconStructure.STRUCTURE_DEFINITION_TIER_5;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.QuantumBeaconStructure.STRUCTURE_TIER_5;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.QuantumBeaconStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class TEQuantumBeaconT5 extends TEQuantumBeacon {

    public TEQuantumBeaconT5() {
        super(400000);
    }

    @Override
    protected int maxPotionLevel(String attribute) {
        if (attribute == ModifierAttribute.P_SATURATION.getAttributeName()) {
            return 3;
        }
        if (attribute == ModifierAttribute.P_SPEED.getAttributeName()) {
            return 5;
        }
        if (attribute == ModifierAttribute.P_HASTE.getAttributeName()) {
            return 5;
        }
        if (attribute == ModifierAttribute.P_STRENGTH.getAttributeName()) {
            return 4;
        }
        if (attribute == ModifierAttribute.P_REGEN.getAttributeName()) {
            return 3;
        }
        if (attribute == ModifierAttribute.P_RESISTANCE.getAttributeName()) {
            return 4;
        }
        if (attribute == ModifierAttribute.P_FIRE_RESISTANCE.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_WATER_BREATHING.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_NIGHT_VISION.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_JUMP_BOOST.getAttributeName()) {
            return 5;
        }
        return 0;
    }

    @Override
    protected IStructureDefinition<TEQuantumBeaconT5> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_5;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_5;
    }

    @Override
    public int getTier() {
        return 5;
    }
}
