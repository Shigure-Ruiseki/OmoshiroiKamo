package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

public class ItemInput extends AbstractModularRecipeInput {

    private String oreDict;
    private ItemStack required;
    private int count = 1;
    private List<IExpression> nbtExpressions;
    private NBTListOperation nbtListOp;

    public ItemInput(ItemStack required) {
        this.required = required != null ? required.copy() : null;
        this.oreDict = null;
        if (this.required != null) this.count = this.required.stackSize;
    }

    public ItemInput(String oreDict, int count) {
        this.required = null;
        this.oreDict = oreDict;
        this.count = count;
    }

    public ItemInput(Item item, int count) {
        this(new ItemStack(item, count));
    }

    public ItemInput(Item item, int count, int meta) {
        this(new ItemStack(item, count, meta));
    }

    public ItemStack getRequired() {
        return required != null ? required.copy() : null;
    }

    public List<ItemStack> getItems() {
        if (required != null) {
            return Collections.singletonList(required);
        } else if (oreDict != null) {
            try {
                List<ItemStack> ores = OreDictionary.getOres(oreDict);
                if (ores == null) return Collections.emptyList();
                List<ItemStack> result = new ArrayList<>();
                for (ItemStack ore : ores) {
                    if (ore == null) continue;
                    ItemStack copy = ore.copy();
                    copy.stackSize = count;
                    result.add(copy);
                }
                return result;
            } catch (Exception e) {
                Logger.error("Error getting ores for: " + oreDict + " - " + e.getMessage());
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public long getRequiredAmount() {
        return required != null ? required.stackSize : count;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ITEM && port instanceof IInventory;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        IInventory itemPort = (IInventory) port;
        long consumed = 0;

        int min = 0;
        int max = itemPort.getSizeInventory() - 1;

        if (itemPort instanceof AbstractItemIOPortTE iiPort) {
            min = iiPort.getSlotDefinition()
                .getMinItemInput();
            max = iiPort.getSlotDefinition()
                .getMaxItemInput();
        }

        if (min < 0) return 0;

        for (int i = min; i <= max && remaining > 0; i++) {
            ItemStack stack = itemPort.getStackInSlot(i);
            if (stack != null && stacksMatch(stack)) {
                int consume = (int) Math.min(stack.stackSize, remaining);
                if (!simulate) {
                    // Apply NBT modifications before consuming
                    applyNBTModifications(stack);

                    stack.stackSize -= consume;
                    if (stack.stackSize <= 0) {
                        itemPort.setInventorySlotContents(i, null);
                    }
                }
                remaining -= consume;
                consumed += consume;
            }
        }
        return consumed;
    }

    /**
     * Apply NBT write operations to the ItemStack.
     */
    private void applyNBTModifications(ItemStack stack) {
        if (nbtExpressions == null && nbtListOp == null) {
            return; // No modifications
        }

        // Ensure NBT exists
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();
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
    }

    private boolean stacksMatch(ItemStack input) {
        if (input == null) return false;

        if (oreDict != null) {
            int[] ids = OreDictionary.getOreIDs(input);
            int targetId = OreDictionary.getOreID(oreDict);
            boolean found = false;
            for (int id : ids) {
                if (id == targetId) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        } else {
            if (required == null) return false;
            if (required.getItem() != input.getItem()) return false;
            // 32767 is wildcard
            if (required.getItemDamage() != 32767 && required.getItemDamage() != input.getItemDamage()) return false;
        }

        // Check NBT conditions
        if (!checkNBTConditions(input)) {
            return false;
        }

        return true;
    }

    /**
     * Check if the ItemStack's NBT matches all NBT conditions.
     */
    private boolean checkNBTConditions(ItemStack stack) {
        if (nbtExpressions == null && nbtListOp == null) {
            return true; // No NBT conditions
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            // No NBT on item - only matches if no NBT requirements
            return (nbtExpressions == null || nbtExpressions.isEmpty()) && (nbtListOp == null);
        }

        // Check expression-based NBT conditions
        if (nbtExpressions != null) {
            ConditionContext context = new ConditionContext(null, 0, 0, 0);
            for (IExpression expr : nbtExpressions) {
                // For condition checking, evaluate as boolean (non-zero = true)
                if (expr.evaluate(context) == 0) {
                    return false;
                }
            }
        }

        // Check NBT list conditions
        if (nbtListOp != null) {
            if (!nbtListOp.matches(nbt)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();

        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

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

        if (json.has("ore")) {
            this.required = null;
            this.oreDict = json.get("ore")
                .getAsString();
            this.count = json.has("amount") ? json.get("amount")
                .getAsInt() : 1;
            return;
        }

        ItemJson itemJson = new ItemJson();
        itemJson.read(json);

        if (itemJson.name != null && itemJson.name.startsWith("ore:")) {
            this.required = null;
            this.oreDict = itemJson.name.substring(4);
            this.count = itemJson.amount;
        } else if (itemJson.ore != null) {
            this.required = null;
            this.oreDict = itemJson.ore;
            this.count = itemJson.amount;
        } else {
            // First set the count so it's preserved even if resolution fails
            this.count = itemJson.amount > 0 ? itemJson.amount : 1;
            ItemStack stack = ItemJson.resolveItemStack(itemJson);
            if (stack != null) {
                this.required = stack;
                this.oreDict = null;
                this.count = stack.stackSize;
            } else {
                this.required = null;
                this.oreDict = null;
                Logger.warn("ItemInput failed to resolve item: {}", json);
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (!consume) json.addProperty("consume", false);
        if (interval > 0) json.addProperty("pertick", interval);

        if (oreDict != null) {
            json.addProperty("ore", oreDict);
            if (count != 1) json.addProperty("amount", count);
        } else if (required != null) {
            ItemJson data = ItemJson.parseItemStack(required);
            if (data != null) {
                json.addProperty("item", data.name);
                if (data.amount != 1) json.addProperty("amount", data.amount);
                if (data.meta != 0) json.addProperty("meta", data.meta);
            }
        }

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
            // NBTListOperation should implement its own write method
            // For now, just indicate it exists
            json.addProperty("_has_nbtlist", true);
        }
    }

    @Override
    public boolean validate() {
        return required != null || oreDict != null;
    }

    @Override
    public void set(String key, Object value) {}

    @Override
    public Object get(String key) {
        return null;
    }

    public static ItemInput fromJson(JsonObject json) {
        ItemInput input = new ItemInput((ItemStack) null);
        input.read(json);
        return input;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        ItemInput result = required != null ? new ItemInput(required) : new ItemInput(oreDict, count);
        result.count *= multiplier;
        if (result.required != null) result.required.stackSize *= multiplier;
        result.consume = this.consume;
        result.interval = this.interval;
        result.nbtExpressions = this.nbtExpressions;
        result.nbtListOp = this.nbtListOp;
        result.index = this.index;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "item");
        nbt.setInteger("count", count);
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
        nbt.setInteger("index", index);
        if (oreDict != null) {
            nbt.setString("ore", oreDict);
        }
        if (required != null) {
            NBTTagCompound stackTag = new NBTTagCompound();
            required.writeToNBT(stackTag);
            nbt.setTag("required", stackTag);
        }

        // Save NBT expressions
        if (nbtExpressions != null && !nbtExpressions.isEmpty()) {
            net.minecraft.nbt.NBTTagList exprList = new net.minecraft.nbt.NBTTagList();
            for (IExpression expr : nbtExpressions) {
                exprList.appendTag(new net.minecraft.nbt.NBTTagString(expr.toString()));
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
        this.count = nbt.getInteger("count");
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
        if (nbt.hasKey("ore")) {
            this.oreDict = nbt.getString("ore");
        }
        if (nbt.hasKey("required")) {
            this.required = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("required"));
        }

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
        return RecipeTickResult.NO_INPUT;
    }
}
