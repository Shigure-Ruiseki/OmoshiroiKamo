package ruiseki.omoshiroikamo.api.structure.io;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Requirement for energy input/output.
 */
public class EnergyRequirement implements IStructureRequirement {

    private final String type;
    private final int min;
    private final int max;

    public EnergyRequirement(String type, int min, int max) {
        this.type = type;
        this.min = min;
        this.max = max;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getMinCount() {
        return min;
    }

    @Override
    public int getMaxCount() {
        return max;
    }

    @Override
    public boolean matches(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) return false;

        // 1. Check if it's a modular port of the correct type
        if (te instanceof IModularPort) {
            IModularPort port = (IModularPort) te;
            if (port.getPortType() == IPortType.Type.ENERGY) {
                return true;
            }
        }

        // 2. Fallback: Capability check (Note: in 1.7.10, we check specific interfaces)
        // Since Energy handling is complex (RF, EU, etc.), for now we mainly support
        // IModularPort
        // but this is where we would add cross-mod support.

        return false;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("min", min);
        json.addProperty("max", max);
        return json;
    }

    public static IStructureRequirement fromJson(String type, JsonObject json) {
        int min = json.has("min") ? json.get("min")
            .getAsInt() : 0;
        int max = json.has("max") ? json.get("max")
            .getAsInt() : Integer.MAX_VALUE;
        return new EnergyRequirement(type, min, max);
    }
}
