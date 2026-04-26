package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.INBTWriteExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.NBTListOperation;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Recipe input that checks for specific blocks at structure positions.
 * Can optionally validate and modify TileEntity NBT data.
 */
public class BlockInput extends AbstractRecipeInput implements IModularRecipeInput {

    private int index = -1;

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private char symbol;
    private String replace; // The block to find (Old / Condition)
    private String block; // The block to set (New / Result) or requirement
    private int amount;
    private IExpression amountExpr;
    private boolean consume;
    private boolean optional;
    private List<IExpression> nbtExpressions;
    private NBTListOperation nbtListOp;
    private NBTMatchMode nbtMatchMode = NBTMatchMode.IGNORE;
    private int interval = 0;

    public BlockInput(char symbol, String block, String replace, int amount, boolean consume, boolean optional) {
        this.symbol = symbol;
        this.block = block;
        this.replace = replace;
        this.amount = amount;
        this.amountExpr = new ConstantExpression(amount);
        this.consume = consume;
        this.optional = optional;
    }

    /**
     * NBT reconstruction constructor
     */
    public BlockInput() {
        this('\0', null, null, 0, false, false);
    }

    /**
     * Legacy constructor/shorthand
     */
    public BlockInput(char symbol, String block, int amount, boolean consume) {
        this(symbol, block, null, amount, consume, false);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate, ConditionContext context) {
        IRecipeContext recipeContext = (context != null) ? context.getRecipeContext() : findRecipeContext(ports);
        if (recipeContext == null) return false;

        return check(recipeContext, multiplier, simulate, context);
    }

    @Override
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        return process(ports, multiplier, simulate, null);
    }

    /**
     * Check and optionally manipulate blocks at symbol positions.
     */
    public boolean check(IRecipeContext context, int multiplier, boolean simulate, ConditionContext condContext) {
        if (optional && simulate) return true; // Always start if optional

        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        if (positions == null) return optional;

        World world = context.getWorld();
        int totalRequired = (int) (getRequiredAmount(condContext) * multiplier);
        int found = 0;
        int processed = 0;

        // Condition block: if replace is specified, we look for it. Otherwise we look
        // for block.
        String condition = (replace != null) ? replace : block;

        for (ChunkCoordinates pos : positions) {
            if (processed >= totalRequired) break;

            if (index != -1) {
                TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
                if (te instanceof IModularPort mp && mp.getAssignedIndex() != index) {
                    continue;
                }
            }

            Block currentBlock = world.getBlock(pos.posX, pos.posY, pos.posZ);
            int meta = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
            String blockId = getBlockId(currentBlock, meta);

            if (matchesBlockId(blockId, condition)) {
                // Check NBT conditions if present
                if (!checkNBTConditions(world, pos, context)) {
                    continue; // NBT conditions not met, skip this position
                }

                found++;
                if (!simulate) {
                    // Apply NBT modifications before block manipulation
                    applyNBTModifications(world, pos, context);

                    // Actual block manipulation
                    if (consume) {
                        world.setBlockToAir(pos.posX, pos.posY, pos.posZ);
                    } else if (replace != null && block != null) {
                        // In-place replacement: A -> B
                        setBlockById(world, pos, block);
                    }
                    processed++;
                }
            }
        }

        return optional || (found >= totalRequired);
    }

    /**
     * Check if the TileEntity's NBT matches all NBT conditions.
     */
    private boolean checkNBTConditions(World world, ChunkCoordinates pos, IRecipeContext context) {
        // Determine effective match mode
        NBTMatchMode effectiveMode = determineEffectiveMatchMode();

        switch (effectiveMode) {
            case IGNORE:
                return true;

            case NONE:
                TileEntity teNone = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
                if (teNone == null) return true; // No TileEntity = no NBT
                NBTTagCompound nbtNone = new NBTTagCompound();
                teNone.writeToNBT(nbtNone);
                // Check if NBT has no meaningful data (only x,y,z,id)
                return isEmptyNBT(nbtNone);

            case EXACT:
                // EXACT mode for blocks requires expression/listOp to define what to match
                // If no expressions, treat as NONE
                if (nbtExpressions != null || nbtListOp != null) {
                    return checkNBTConditionsPartial(world, pos, context);
                } else {
                    TileEntity teExact = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
                    if (teExact == null) return true;
                    NBTTagCompound nbtExact = new NBTTagCompound();
                    teExact.writeToNBT(nbtExact);
                    return isEmptyNBT(nbtExact);
                }

            case PARTIAL:
                return checkNBTConditionsPartial(world, pos, context);

            default:
                return true;
        }
    }

    /**
     * Determine the effective NBT match mode.
     */
    private NBTMatchMode determineEffectiveMatchMode() {
        if ((nbtExpressions != null && !nbtExpressions.isEmpty()) || nbtListOp != null) {
            return NBTMatchMode.PARTIAL;
        }
        return nbtMatchMode;
    }

    /**
     * Check NBT conditions using the existing expression system (PARTIAL mode).
     */
    private boolean checkNBTConditionsPartial(World world, ChunkCoordinates pos, IRecipeContext context) {
        if (nbtExpressions == null && nbtListOp == null) {
            return true;
        }

        TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te == null) {
            return (nbtExpressions == null || nbtExpressions.isEmpty()) && (nbtListOp == null);
        }

        NBTTagCompound nbt = new NBTTagCompound();
        te.writeToNBT(nbt);

        // Check expression-based NBT conditions
        if (nbtExpressions != null) {
            ConditionContext condContext = new ConditionContext(world, pos.posX, pos.posY, pos.posZ, context);
            for (IExpression expr : nbtExpressions) {
                if (expr.evaluate(condContext)
                    .isZero()) {
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

    /**
     * Check if NBT is essentially empty (only contains position and id).
     * TileEntity NBT always contains x, y, z, and id by default.
     */
    private boolean isEmptyNBT(NBTTagCompound nbt) {
        if (nbt == null) return true;

        // Count keys - if more than 4, there's additional data
        int keyCount = nbt.func_150296_c()
            .size();

        if (keyCount > 4) return false;

        // Check if all keys are standard TileEntity keys
        for (Object keyObj : nbt.func_150296_c()) {
            if (!keyObj.equals("x") && !keyObj.equals("y") && !keyObj.equals("z") && !keyObj.equals("id")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply NBT write operations to the TileEntity.
     */
    private void applyNBTModifications(World world, ChunkCoordinates pos, IRecipeContext context) {
        if (nbtExpressions == null && nbtListOp == null) {
            return; // No modifications
        }

        TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te == null) {
            return; // No TileEntity to modify
        }

        NBTTagCompound nbt = new NBTTagCompound();
        te.writeToNBT(nbt);

        ConditionContext condContext = new ConditionContext(world, pos.posX, pos.posY, pos.posZ, context);

        // Apply expression-based NBT writes
        if (nbtExpressions != null) {
            for (IExpression expr : nbtExpressions) {
                if (expr instanceof INBTWriteExpression) {
                    ((INBTWriteExpression) expr).applyToNBT(nbt, condContext);
                }
            }
        }

        // Apply NBT list operations
        if (nbtListOp != null) {
            nbtListOp.apply(nbt);
        }

        // Write back to TileEntity
        nbt.setInteger("x", pos.posX);
        nbt.setInteger("y", pos.posY);
        nbt.setInteger("z", pos.posZ);
        te.readFromNBT(nbt);
        te.markDirty();
        world.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
    }

    private void setBlockById(World world, ChunkCoordinates pos, String blockId) {
        String[] parts = blockId.split(":");
        if (parts.length < 2) return;
        Block b = GameRegistry.findBlock(parts[0], parts[1]);
        if (b == null) return;
        int meta = 0;
        if (parts.length == 3 && !parts[2].equals("*")) {
            try {
                meta = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {}
        }
        world.setBlock(pos.posX, pos.posY, pos.posZ, b, meta, 3);
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        return amountExpr != null ? (long) amountExpr.evaluateDouble(context) : amount;
    }

    @Override
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    public boolean isPerTick() {
        return interval > 0;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        BlockInput result = new BlockInput(symbol, block, replace, amount * multiplier, consume, optional);
        result.nbtExpressions = this.nbtExpressions;
        result.nbtListOp = this.nbtListOp;
        result.nbtMatchMode = this.nbtMatchMode;
        result.interval = this.interval;
        result.amountExpr = this.amountExpr;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "block");
        nbt.setString("symbol", String.valueOf(symbol));
        if (block != null) nbt.setString("block", block);
        if (replace != null) nbt.setString("replace", replace);
        if (amountExpr instanceof ConstantExpression) {
            nbt.setInteger("amount", amount);
        } else {
            nbt.setString("amountExpr", amountExpr.toString());
        }
        nbt.setBoolean("consume", consume);
        nbt.setBoolean("optional", optional);
        nbt.setInteger("interval", interval);

        // Save NBTMatchMode
        nbt.setString("nbtMatchMode", nbtMatchMode.name());

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

        // Restore NBT expressions
        if (nbt.hasKey("nbtExpressions")) {
            NBTTagList exprList = nbt.getTagList("nbtExpressions", 8);
            this.nbtExpressions = new ArrayList<>();
            for (int i = 0; i < exprList.tagCount(); i++) {
                String exprStr = exprList.getStringTagAt(i);
                try {
                    this.nbtExpressions.add(ExpressionParser.parseExpression(exprStr));
                } catch (Exception e) {
                    Logger.error("Failed to restore NBT expression: " + exprStr);
                }
            }
        }

        // Restore NBT list operation
        if (nbt.hasKey("nbtListOp")) {
            this.nbtListOp = NBTListOperation.readFromNBT(nbt.getCompoundTag("nbtListOp"));
        }

        // Restore amount expression
        this.amount = nbt.getInteger("amount");
        if (nbt.hasKey("amountExpr")) {
            this.amountExpr = ExpressionParser.parseExpression(nbt.getString("amountExpr"));
        } else {
            this.amountExpr = new ConstantExpression(amount);
        }
    }

    @Override
    public void read(JsonObject json) {
        // Common JSON parsing for interval
        if (json.has("pertick")) {
            JsonElement pt = json.get("pertick");
            if (pt.isJsonPrimitive()) {
                if (pt.getAsJsonPrimitive()
                    .isBoolean()) {
                    this.interval = pt.getAsBoolean() ? 1 : 0;
                } else {
                    this.interval = pt.getAsInt();
                }
            }
        }
        if (json.has("index")) {
            this.index = json.get("index")
                .getAsInt();
        }

        // Read NBTMatchMode
        if (json.has("nbtmatch")) {
            String modeStr = json.get("nbtmatch")
                .getAsString();
            this.nbtMatchMode = NBTMatchMode.fromString(modeStr);
        }

        if (json.has("amount")) {
            this.amountExpr = ExpressionsParser.parse(json.get("amount"));
            if (amountExpr instanceof ConstantExpression) {
                this.amount = (int) amountExpr.evaluateDouble(null);
            }
        } else {
            this.amount = 1;
            this.amountExpr = new ConstantExpression(1);
        }
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        json.addProperty("symbol", String.valueOf(symbol));
        if (block != null) json.addProperty("block", block);
        if (replace != null) json.addProperty("replace", replace);
        if (amountExpr instanceof ConstantExpression) {
            json.addProperty("amount", amount);
        } else {
            json.addProperty("amount", amountExpr.toString());
        }
        if (consume) json.addProperty("consume", true);
        if (optional) json.addProperty("optional", true);
        if (index != -1) json.addProperty("index", index);

        // Write NBTMatchMode (only if not default IGNORE)
        if (nbtMatchMode != NBTMatchMode.IGNORE) {
            json.addProperty("nbtmatch", nbtMatchMode.toJsonString());
        }

        // Write NBT expressions
        if (nbtExpressions != null && !nbtExpressions.isEmpty()) {
            JsonArray nbtArray = new JsonArray();
            for (IExpression expr : nbtExpressions) {
                nbtArray.add(new JsonPrimitive(expr.toString()));
            }
            json.add("nbt", nbtArray);
        }

        // Write NBT list operation
        if (nbtListOp != null) {
            json.addProperty("_has_nbtlist", true);
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        write(json);
        return json;
    }

    public static BlockInput fromJson(JsonObject json) {
        char symbol = json.get("symbol")
            .getAsString()
            .charAt(0);
        String block = json.has("block") ? json.get("block")
            .getAsString() : null;
        String replace = json.has("replace") ? json.get("replace")
            .getAsString() : null;
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        boolean consume = json.has("consume") && json.get("consume")
            .getAsBoolean();
        boolean optional = json.has("optional") && json.get("optional")
            .getAsBoolean();

        BlockInput input = new BlockInput(symbol, block, replace, amount, consume, optional);
        input.readPerTick(json, 0);

        // Read NBT expressions
        if (json.has("nbt")) {
            JsonElement nbtElement = json.get("nbt");
            input.nbtExpressions = new ArrayList<>();

            if (nbtElement.isJsonArray()) {
                JsonArray nbtArray = nbtElement.getAsJsonArray();
                for (JsonElement element : nbtArray) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                        .isString()) {
                        String exprStr = element.getAsString();
                        try {
                            IExpression expr = ExpressionParser.parseExpression(exprStr);
                            input.nbtExpressions.add(expr);
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
                        input.nbtExpressions.add(expr);
                    } catch (Exception e) {
                        Logger.error("Failed to parse NBT expression: " + exprStr + " - " + e.getMessage());
                    }
                }
        }

        // Read NBT list operation
        if (json.has("nbtlist")) {
            try {
                input.nbtListOp = NBTListOperation.fromJson(json);
            } catch (Exception e) {
                Logger.error("Failed to parse NBT list operation: " + e.getMessage());
            }
        }

        return input;
    }

    /**
     * Find IRecipeContext from port list.
     * TEMachineController implements both IModularPort and IRecipeContext.
     */
    private IRecipeContext findRecipeContext(List<IModularPort> ports) {
        for (IModularPort port : ports) {
            if (port instanceof IRecipeContext) {
                return (IRecipeContext) port;
            }
        }
        return null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.BLOCK_MISSING;
    }

    /**
     * Get block ID string in format "modid:blockname:meta"
     */
    private String getBlockId(Block block, int meta) {
        UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(block);
        if (ui == null) {
            return "minecraft:air:0";
        }
        return ui.modId + ":" + ui.name + ":" + meta;
    }

    /**
     * Check if block ID matches the pattern.
     * Supports wildcards like "modid:blockname:*" for any meta.
     */
    private boolean matchesBlockId(String blockId, String pattern) {
        if (pattern == null || blockId == null) return false;
        if (pattern.equals("*")) return true;

        String[] blockParts = blockId.split(":");
        String[] patternParts = pattern.split(":");

        // Handle different formats
        if (patternParts.length == 2) {
            // Pattern: "modid:blockname" - match any meta
            return blockParts.length >= 2 && blockParts[0].equals(patternParts[0])
                && blockParts[1].equals(patternParts[1]);
        } else if (patternParts.length == 3) {
            // Pattern: "modid:blockname:meta" or "modid:blockname:*"
            if (blockParts.length < 3) return false;

            if (!blockParts[0].equals(patternParts[0]) || !blockParts[1].equals(patternParts[1])) {
                return false;
            }

            // Check meta
            if (patternParts[2].equals("*")) {
                return true;
            }

            return blockParts[2].equals(patternParts[2]);
        }

        return blockId.equals(pattern);
    }

    // Getters
    public String getBlock() {
        return block;
    }

    public String getReplace() {
        return replace;
    }

    public boolean isConsume() {
        return consume;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getAmount() {
        return amount;
    }
}
