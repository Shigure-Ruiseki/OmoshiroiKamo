package ruiseki.omoshiroikamo.module.dml.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.entity.dml.DataModel;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.io.AbstractModularRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;

public class DataModelInput extends AbstractModularRecipeInput {

    private int modelId = -1;

    public DataModelInput() {}

    public DataModelInput(int modelId) {
        this.modelId = modelId;
    }

    public static DataModelInput fromJson(JsonObject json) {
        DataModelInput input = new DataModelInput();
        input.read(json);
        return input;
    }

    @Override
    public void read(JsonObject json) {
        this.modelId = getInt(json, "modelId", -1);
        if (this.modelId == -1) {
            String name = getString(json, "modelName", null);
            if (name != null) {
                ModelRegistryItem item = ModelRegistry.INSTANCE.getByName(name);
                if (item != null) {
                    this.modelId = item.getId();
                }
            }
        }
    }

    @Override
    public boolean validate() {
        if (modelId == -1) {
            logValidationError("DataModelInput must have a valid modelId or modelName");
            return false;
        }
        return true;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("modelId", modelId);
        ModelRegistryItem item = ModelRegistry.INSTANCE.getByType(modelId);
        if (item != null) {
            json.addProperty("modelName", item.getDisplayName());
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit((IRecipeInput) this);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public long getRequiredAmount() {
        return 1;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ITEM;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        if (!(port instanceof IInventory)) return 0;
        IInventory inv = (IInventory) port;

        // Find the slot containing the data model
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (DataModel.isModel(stack) && DataModel.getId(stack) == modelId) {
                if (!simulate) {
                    // Increase simulation count on actual processing
                    DataModel.increaseSimulationCount(stack);
                    inv.markDirty();
                }
                return 1;
            }
        }
        return 0;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        DataModelInput result = new DataModelInput(modelId);
        result.consume = this.consume;
        result.interval = this.interval;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "datamodel");
        nbt.setInteger("modelId", modelId);
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.modelId = nbt.getInteger("modelId");
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
    }
}
