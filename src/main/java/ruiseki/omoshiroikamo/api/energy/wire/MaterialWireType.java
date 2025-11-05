package ruiseki.omoshiroikamo.api.energy.wire;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.init.ModItems;

/*
 * This file contains code adapted from Immersive Engineering by BluSunrize.
 * Original project: https://github.com/BluSunrize/ImmersiveEngineering
 * License: "Blu's License of Common Sense" (https://github.com/BluSunrize/ImmersiveEngineering/blob/master/LICENSE)
 * Modifications made by: [Louis]
 * This adapted version complies with the original license terms.
 * It is intended for use in a standalone mod inspired by Immersive Engineering.
 */

public class MaterialWireType extends WireType {

    private final MaterialEntry material;

    public static final Map<Integer, MaterialWireType> MATERIAL_WIRE_TYPES = new HashMap<>();

    public static void preInit() {
        for (MaterialEntry material : MaterialRegistry.all()) {
            if (material != null) {
                MATERIAL_WIRE_TYPES.put(material.getMeta(), new MaterialWireType(material.meta));
            }
        }
    }

    public MaterialWireType(int meta) {
        super();
        this.material = MaterialRegistry.fromMeta(meta);
    }

    public static MaterialWireType get(int meta) {
        return MATERIAL_WIRE_TYPES.get(meta);
    }

    @Override
    public String getUniqueName() {
        return material.getUnlocalizedName();
    }

    @Override
    public double getLossRatio() {
        double conductivity = material.getElectricalConductivity();
        return 1.0 / Math.max(1.0, conductivity / 1e6);
    }

    @Override
    public int getTransferRate() {
        return material.getMaxPowerTransfer() * 10;
    }

    @Override
    public int getColour(WireNetHandler.Connection connection) {
        return material.getColor();
    }

    @Override
    public double getSlack() {
        double elasticity = 1.0 / Math.sqrt(material.getMaxPressureMPa());
        return 1.001 + elasticity * 0.1;
    }

    @Override
    public IIcon getIcon(WireNetHandler.Connection connection) {
        return WireType.iconDefaultWire;
    }

    @Override
    public int getMaxLength() {
        return 16 + 8 * material.getVoltageTier()
            .ordinal();
    }

    @Override
    public ItemStack getWireCoil() {
        int meta = MaterialRegistry.indexOf(material);
        return ModItems.WIRE_COIL.newItemStack(1, meta);
    }

    @Override
    public double getRenderDiameter() {
        return 0.03125 + 0.01 * material.getVoltageTier()
            .ordinal();
    }

    @Override
    public boolean isEnergyWire() {
        return material.getElectricalConductivity() > 1e6;
    }

    public MaterialEntry getMaterial() {
        return material;
    }

}
