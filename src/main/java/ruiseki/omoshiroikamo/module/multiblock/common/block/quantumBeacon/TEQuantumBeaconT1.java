package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.STRUCTURE_TIER_1;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconStructure.STRUCTURE_DEFINITION_TIER_1;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class TEQuantumBeaconT1 extends TEQuantumBeacon {

    public TEQuantumBeaconT1() {
        super(100000);
    }

    @Override
    protected int maxPotionLevel(String attribute) {
        if (attribute == ModifierAttribute.P_SATURATION.getAttributeName()) {
            return 0;
        }
        if (attribute == ModifierAttribute.P_SPEED.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_HASTE.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_STRENGTH.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_REGEN.getAttributeName()) {
            return 1;
        }
        if (attribute == ModifierAttribute.P_RESISTANCE.getAttributeName()) {
            return 1;
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
            return 1;
        }
        return 0;
    }

    @Override
    protected IStructureDefinition<TEQuantumBeaconT1> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_1;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_1;
    }

    @Override
    public int getTier() {
        return 1;
    }
}
