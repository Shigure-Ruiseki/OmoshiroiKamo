package ruiseki.omoshiroikamo.module.multiblock.common.init;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ruiseki.omoshiroikamo.api.multiblock.AttributeTotalLevel;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.AttributeAccuracy;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.AttributeSpeed;

public enum ModifierAttribute {

    SPEED(new AttributeSpeed()),
    ACCURACY(new AttributeAccuracy()),
    PIEZO(new AttributeTotalLevel("piezo")),
    E_FLIGHT_CREATIVE(new AttributeTotalLevel("e_flight_creative")),
    P_NIGHT_VISION(new AttributeTotalLevel("p_night_vision")),
    P_SPEED(new AttributeTotalLevel("p_speed")),
    P_HASTE(new AttributeTotalLevel("p_haste")),
    P_STRENGTH(new AttributeTotalLevel("p_strength")),
    P_WATER_BREATHING(new AttributeTotalLevel("p_water_breathing")),
    P_REGEN(new AttributeTotalLevel("p_regen")),
    P_SATURATION(new AttributeTotalLevel("p_saturation")),
    P_RESISTANCE(new AttributeTotalLevel("p_resistance")),
    P_JUMP_BOOST(new AttributeTotalLevel("p_jump_boost")),
    P_FIRE_RESISTANCE(new AttributeTotalLevel("p_fire_resistance")),
    NULL(new AttributeTotalLevel("null"));

    private final IModifierAttribute attribute;

    ModifierAttribute(IModifierAttribute attribute) {
        this.attribute = attribute;
    }

    public IModifierAttribute getAttribute() {
        return attribute;
    }

    public String getAttributeName() {
        return attribute.getAttributeName();
    }

    // --- Optional tiện ích ---
    private static final Map<String, ModifierAttribute> LOOKUP = Arrays.stream(values())
        .collect(
            Collectors.toMap(
                e -> e.getAttributeName()
                    .toLowerCase(Locale.ROOT),
                e -> e));

    public static ModifierAttribute fromName(String name) {
        if (name == null) {
            return NULL;
        }
        return LOOKUP.getOrDefault(name.toLowerCase(Locale.ROOT), NULL);
    }
}
