package ruiseki.omoshiroikamo.api.structure.io;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Requirement for fluid input/output.
 * Supports both IModularPort and standard Forge IFluidHandler.
 */
public class FluidRequirement implements IStructureRequirement {

    private final String type;
    private final int min;
    private final int max;

    public FluidRequirement(String type, int min, int max) {
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
            if (port.getPortType() == IPortType.Type.FLUID) {
                return true;
            }
        }

        // 2. Fallback: Check if it implements IFluidHandler (Forge standard)
        if (te instanceof IFluidHandler) {
            IFluidHandler handler = (IFluidHandler) te;
            // Check if it can actually interact with fluids from at least one side
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (handler.getTankInfo(dir) != null) {
                    return true;
                }
            }
        }

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
        return new FluidRequirement(type, min, max);
    }
}
