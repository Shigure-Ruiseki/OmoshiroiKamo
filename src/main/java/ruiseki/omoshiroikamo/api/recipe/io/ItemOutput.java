package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.INBTWriteExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.NBTListOperation;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.ItemJson;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

public class ItemOutput extends AbstractModularRecipeOutput {

    private ItemStack output;
    private int count = 0;
    private List<IExpression> nbtExpressions;
    private NBTListOperation nbtListOp;
    private NBTMatchMode nbtMatchMode = NBTMatchMode.IGNORE;

    public ItemOutput(ItemStack output) {
        this.output = output != null ? output.copy() : null;
        if (this.output != null) this.count = this.output.stackSize;
    }

    public ItemOutput(Item item, int count) {
        this(new ItemStack(item, count));
    }

    public ItemOutput(Item item, int count, int meta) {
        this(new ItemStack(item, count, meta));
    }

    public ItemStack getOutput() {
        return output != null ? output.copy() : null;
    }

    public List<ItemStack> getItems() {
        return output != null ? Collections.singletonList(output) : Collections.emptyList();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier) {
        if (output == null) return;
        int remaining = output.stackSize * multiplier;
        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ITEM) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (!(port instanceof IInventory)) {
                throw new IllegalStateException(
                    "ITEM OUTPUT port must implement IInventory, got: " + port.getClass()
                        .getName());
            }

            IInventory itemPort = (IInventory) port;

            int startSlot = 0;
            int endSlot = itemPort.getSizeInventory() - 1;
            if (itemPort instanceof AbstractItemIOPortTE) {
                startSlot = ((AbstractItemIOPortTE) itemPort).getSlotDefinition()
                    .getMinItemOutput();
                endSlot = ((AbstractItemIOPortTE) itemPort).getSlotDefinition()
                    .getMaxItemOutput();
            }

            for (int i = startSlot; i <= endSlot && remaining > 0; i++) {
                ItemStack stack = itemPort.getStackInSlot(i);
                if (stack != null && stacksMatch(stack, output)) {
                    int space = stack.getMaxStackSize() - stack.stackSize;
                    if (space > 0) {
                        int insert = Math.min(remaining, space);
                        stack.stackSize += insert;
                        itemPort.setInventorySlotContents(i, stack); // Trigger updates
                        remaining -= insert;
                    }
                }
            }
            for (int i = startSlot; i <= endSlot && remaining > 0; i++) {
                if (itemPort.getStackInSlot(i) == null) {
                    int insert = Math.min(remaining, output.getMaxStackSize());
                    ItemStack newStack = createOutputStack(insert);
                    itemPort.setInventorySlotContents(i, newStack);
                    remaining -= insert;
                }
            }

            if (remaining <= 0) break;
        }
    }

    /**
     * Create an output ItemStack with NBT data applied.
     */
    private ItemStack createOutputStack(int stackSize) {
        if (output == null) return null;

        ItemStack newStack = output.copy();
        newStack.stackSize = stackSize;

        // Apply NBT modifications
        if (nbtExpressions != null || nbtListOp != null) {
            NBTTagCompound nbt = newStack.getTagCompound();
            boolean hadNBT = (nbt != null);

            if (nbt == null) {
                nbt = new NBTTagCompound();
            }

            ConditionContext context = new ConditionContext(null, 0, 0, 0);

            // Apply expression-based NBT writes
            if (nbtExpressions != null) {
                for (IExpression expr : nbtExpressions) {
                    if (expr instanceof INBTWriteExpression) {
                        ((INBTWriteExpression) expr).applyToNBT(nbt, context);
                    }
                }
            }

            // Apply NBT list operations
            if (nbtListOp != null) {
                nbtListOp.apply(nbt);
            }

            if (!nbt.hasNoTags() || hadNBT) {
                newStack.setTagCompound(nbt);
            }
        }

        return newStack;
    }

    private boolean stacksMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;

        // Apply NBTMatchMode for output merging
        switch (nbtMatchMode) {
            case IGNORE:
                return true;

            case EXACT:
                return ItemStack.areItemStackTagsEqual(a, b);

            case NONE:
                return a.getTagCompound() == null && b.getTagCompound() == null;

            case PARTIAL:
                return ItemStack.areItemStackTagsEqual(a, b);

            default:
                return ItemStack.areItemStackTagsEqual(a, b);
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ITEM && port instanceof IInventory
            && (port.getPortDirection() == IPortType.Direction.OUTPUT
                || port.getPortDirection() == IPortType.Direction.BOTH);
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (output == null) return 0;
        IInventory itemPort = (IInventory) port;
        int maxStackSize = output.getMaxStackSize();
        int invLimit = itemPort.getInventoryStackLimit();
        int limit = Math.min(invLimit, maxStackSize);
        int min = 0;
        int max = itemPort.getSizeInventory();
        if (itemPort instanceof AbstractItemIOPortTE) {
            min = ((AbstractItemIOPortTE) itemPort).getSlotDefinition()
                .getMinItemOutput();
            max = ((AbstractItemIOPortTE) itemPort).getSlotDefinition()
                .getMaxItemOutput();
        }

        long available = 0;
        for (int i = min; i <= max; i++) {
            ItemStack stack = itemPort.getStackInSlot(i);
            if (stack == null) {
                available += limit;
            } else if (stacksMatch(stack, output)) {
                int space = limit - stack.stackSize;
                if (space > 0) available += space;
            }
        }
        return available;
    }

    @Override
    public long getRequiredAmount() {
        return output != null ? output.stackSize : count;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();

        // Read NBTMatchMode
        if (json.has("nbtmatch")) {
            String modeStr = json.get("nbtmatch")
                .getAsString();
            this.nbtMatchMode = NBTMatchMode.fromString(modeStr);
        }

        ItemJson itemJson = new ItemJson();
        itemJson.read(json);
        this.count = itemJson.amount;
        this.output = ItemJson.resolveItemStack(itemJson);

        // Read NBT expressions
        if (json.has("nbt")) {
            JsonElement nbtElement = json.get("nbt");
            this.nbtExpressions = new ArrayList<>();

            if (nbtElement.isJsonArray()) {
                JsonArray nbtArray = nbtElement.getAsJsonArray();
                for (JsonElement element : nbtArray) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                        .isString()) {
                        String exprStr = element.getAsString();
                        try {
                            IExpression expr = ExpressionParser.parseExpression(exprStr);
                            this.nbtExpressions.add(expr);
                        } catch (Exception e) {
                            Logger.error("Failed to parse NBT expression: " + exprStr + " - " + e.getMessage());
                        }
                    }
                }
            } else if (nbtElement.isJsonPrimitive() && nbtElement.getAsJsonPrimitive()
                .isString()) {
                    String exprStr = nbtElement.getAsString();
                    try {
                        IExpression expr = ExpressionParser.parseExpression(exprStr);
                        this.nbtExpressions.add(expr);
                    } catch (Exception e) {
                        Logger.error("Failed to parse NBT expression: " + exprStr + " - " + e.getMessage());
                    }
                }
        }

        // Read NBT list operation
        if (json.has("nbtlist")) {
            try {
                this.nbtListOp = NBTListOperation.fromJson(json);
            } catch (Exception e) {
                Logger.error("Failed to parse NBT list operation: " + e.getMessage());
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);

        // Write NBTMatchMode (only if not default IGNORE)
        if (nbtMatchMode != NBTMatchMode.IGNORE) {
            json.addProperty("nbtmatch", nbtMatchMode.toJsonString());
        }

        if (output != null) {
            json.addProperty(
                "item",
                GameData.getItemRegistry()
                    .getNameForObject(output.getItem()));
            if (output.stackSize != 1) json.addProperty("amount", output.stackSize);
            if (output.getItemDamage() != 0) json.addProperty("meta", output.getItemDamage());
        }
        if (interval > 0) json.addProperty("pertick", interval);

        // Write NBT expressions
        if (nbtExpressions != null && !nbtExpressions.isEmpty()) {
            JsonArray nbtArray = new JsonArray();
            for (IExpression expr : nbtExpressions) {
                nbtArray.add(new com.google.gson.JsonPrimitive(expr.toString()));
            }
            json.add("nbt", nbtArray);
        }

        // Write NBT list operation
        if (nbtListOp != null) {
            json.addProperty("_has_nbtlist", true);
        }
    }

    @Override
    public boolean validate() {
        return output != null;
    }

    @Override
    public void set(String key, Object value) {}

    @Override
    public Object get(String key) {
        return null;
    }

    public static ItemOutput fromJson(JsonObject json) {
        ItemOutput out = new ItemOutput((ItemStack) null);
        out.read(json);
        return out;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        if (output == null) return new ItemOutput((ItemStack) null);
        ItemStack copy = output.copy();
        copy.stackSize *= multiplier;
        ItemOutput result = new ItemOutput(copy);

        result.interval = this.interval;
        result.nbtExpressions = this.nbtExpressions;
        result.nbtListOp = this.nbtListOp;
        result.nbtMatchMode = this.nbtMatchMode;
        result.index = this.index;

        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "item");
        nbt.setInteger("interval", interval);

        // Save NBTMatchMode
        nbt.setString("nbtMatchMode", nbtMatchMode.name());

        if (output != null) {
            NBTTagCompound stackTag = new NBTTagCompound();
            output.writeToNBT(stackTag);
            nbt.setTag("output", stackTag);
        }
        nbt.setInteger("index", index);

        // Save NBT expressions
        if (nbtExpressions != null && !nbtExpressions.isEmpty()) {
            NBTTagList exprList = new NBTTagList();
            for (IExpression expr : nbtExpressions) {
                exprList.appendTag(new NBTTagString(expr.toString()));
            }
            nbt.setTag("nbtExpressions", exprList);
        }

        // Save NBT list operation
        if (nbtListOp != null) {
            NBTTagCompound opTag = new NBTTagCompound();
            nbtListOp.writeToNBT(opTag);
            nbt.setTag("nbtListOp", opTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");

        // Restore NBTMatchMode
        if (nbt.hasKey("nbtMatchMode")) {
            try {
                this.nbtMatchMode = NBTMatchMode.valueOf(nbt.getString("nbtMatchMode"));
            } catch (IllegalArgumentException e) {
                this.nbtMatchMode = NBTMatchMode.IGNORE;
                Logger.warn("Invalid NBTMatchMode in NBT, defaulting to IGNORE");
            }
        }

        if (nbt.hasKey("output")) {
            this.output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("output"));
        }
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;

        // Restore NBT expressions
        if (nbt.hasKey("nbtExpressions")) {
            NBTTagList exprList = nbt.getTagList("nbtExpressions", 8); // 8 is TAG_STRING
            this.nbtExpressions = new ArrayList<>();
            for (int i = 0; i < exprList.tagCount(); i++) {
                String exprStr = exprList.getStringTagAt(i);
                try {
                    this.nbtExpressions.add(ExpressionParser.parseExpression(exprStr));
                } catch (Exception e) {
                    Logger.error("Failed to restore NBT expression from NBT: " + exprStr);
                }
            }
        }

        // Restore NBT list operation
        if (nbt.hasKey("nbtListOp")) {
            this.nbtListOp = NBTListOperation.readFromNBT(nbt.getCompoundTag("nbtListOp"));
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.OUTPUT_FULL;
    }
}
