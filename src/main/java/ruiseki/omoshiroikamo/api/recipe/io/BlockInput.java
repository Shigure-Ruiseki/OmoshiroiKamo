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
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
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

    private char symbol;
    private String replace; // The block to find (Old / Condition)
    private String block; // The block to set (New / Result) or requirement
    private int amount;
    private boolean consume;
    private boolean optional;
    private List<IExpression> nbtExpressions;
    private NBTListOperation nbtListOp;
    private int interval = 0;

    public BlockInput(char symbol, String block, String replace, int amount, boolean consume, boolean optional) {
        this.symbol = symbol;
        this.block = block;
        this.replace = replace;
        this.amount = amount;
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
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) return false;

        return check(context, multiplier, simulate);
    }

    /**
     * Check and optionally manipulate blocks at symbol positions.
     */
    public boolean check(IRecipeContext context, int multiplier, boolean simulate) {
        if (optional && simulate) return true; // Always start if optional

        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        if (positions == null) return optional;

        World world = context.getWorld();
        int totalRequired = amount * multiplier;
        int found = 0;
        int processed = 0;

        // Condition block: if replace is specified, we look for it. Otherwise we look
        // for block.
        String condition = (replace != null) ? replace : block;

        for (ChunkCoordinates pos : positions) {
            if (processed >= totalRequired) break;

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
        if (nbtExpressions == null && nbtListOp == null) {
            return true; // No NBT conditions
        }

        TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te == null) {
            // No TileEntity - only matches if no NBT requirements
            return (nbtExpressions == null || nbtExpressions.isEmpty()) && (nbtListOp == null);
        }

        NBTTagCompound nbt = new NBTTagCompound();
        te.writeToNBT(nbt);

        // Check expression-based NBT conditions
        if (nbtExpressions != null) {
            ConditionContext condContext = new ConditionContext(world, pos.posX, pos.posY, pos.posZ, context);
            for (IExpression expr : nbtExpressions) {
                // For condition checking, evaluate as boolean (non-zero = true)
                if (expr.evaluate(condContext) == 0) {
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
        result.interval = this.interval;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "block");
        nbt.setString("symbol", String.valueOf(symbol));
        if (block != null) nbt.setString("block", block);
        if (replace != null) nbt.setString("replace", replace);
        nbt.setInteger("amount", amount);
        nbt.setBoolean("consume", consume);
        nbt.setBoolean("optional", optional);
        nbt.setInteger("interval", interval);

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
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        json.addProperty("symbol", String.valueOf(symbol));
        if (block != null) json.addProperty("block", block);
        if (replace != null) json.addProperty("replace", replace);
        json.addProperty("amount", amount);
        if (consume) json.addProperty("consume", true);
        if (optional) json.addProperty("optional", true);

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
