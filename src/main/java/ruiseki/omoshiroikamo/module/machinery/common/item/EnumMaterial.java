package ruiseki.omoshiroikamo.module.machinery.common.item;

import lombok.Getter;

/**
 * Define the type of intermediate material.
 * This is a template of new materials.
 */
public enum EnumMaterial {

    OSMIRIDIUM(10, "osmiridium", "Osmiridium")
    // HIKARIUM(200, "hikarium", "Hikarium"),
    // TAIRITHIUM(201, "tairithium", "Tairithium"),
    // VITALIUM(202, "vitarium", "Vitarium")
    ;

    @Getter
    private final int meta;
    @Getter
    private final String name; // internal name, texture name, translation key
    @Getter
    private final String oreName; // suffix for ore dictionary

    EnumMaterial(int meta, String name, String oreName) {
        this.meta = meta;
        this.name = name;
        this.oreName = oreName;
    }

    public static EnumMaterial byMetadata(int meta) {
        for (EnumMaterial material : values()) {
            if (material.getMeta() == meta) {
                return material;
            }
        }
        return null;
    }
}
