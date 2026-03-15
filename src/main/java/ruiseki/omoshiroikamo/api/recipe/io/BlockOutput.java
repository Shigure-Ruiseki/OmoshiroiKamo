package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.INBTWriteExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.NBTListOperation;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

/**
 * Recipe output that changes blocks at structure positions.
 * Can optionally set TileEntity NBT data.
 */
public class BlockOutput extends AbstractJsonMaterial implements IRecipeOutput {

    private final char symbol;
    private final String block; // The block to set (New / Result)
    private final String replace; // The block to find (Old / Filter)
    private final int amount;
    private final boolean optional;
    private final Map<String, IExpression> dynamicNbt; // Legacy NBT system
    private List<IExpression> nbtExpressions; // New NBT system
    private NBTListOperation nbtListOp; // New NBT list system

    public BlockOutput(char symbol, String block, String replace, int amount, boolean optional,
        Map<String, IExpression> dynamicNbt) {
        this.symbol = symbol;
        this.block = block;
        this.replace = replace;
        this.amount = amount;
        this.optional = optional;
        this.dynamicNbt = dynamicNbt != null ? dynamicNbt : new HashMap<>();
    }

    public BlockOutput(char symbol, String block, String replace, int amount) {
        this(symbol, block, replace, amount, false, null);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public boolean checkCapacity(List<IModularPort> ports, int multiplier) {
        if (optional) return true;

        IRecipeContext context = findRecipeContext(ports);
        if (context == null) return false;

        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        if (positions == null) return false;

        World world = context.getWorld();
        int available = 0;
        int totalRequired = amount * multiplier;

        // Condition check
        for (ChunkCoordinates pos : positions) {
            Block currentBlock = world.getBlock(pos.posX, pos.posY, pos.posZ);
            int meta = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
            String blockId = getBlockId(currentBlock, meta);

            boolean match = false;
            if (replace != null) {
                if (matchesBlockId(blockId, replace)) match = true;
            } else if (block != null) {
                // Creation mode: check if replaceable or air
                if (currentBlock.isReplaceable(world, pos.posX, pos.posY, pos.posZ)
                    || currentBlock.isAir(world, pos.posX, pos.posY, pos.posZ)) match = true;
            } else {
                // Clear mode: check if non-air
                if (!currentBlock.isAir(world, pos.posX, pos.posY, pos.posZ)) match = true;
            }

            if (match) available++;
            if (available >= totalRequired) return true;
        }

        return false;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier) {
        // Find IRecipeContext from ports
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) {
            return;
        }

        apply(context, multiplier);
    }

    /**
     * Apply block changes to the structure.
     *
     * @param context    Recipe context with structure information
     * @param multiplier The batch size multiplier
     */
    public void apply(IRecipeContext context, int multiplier) {
        World world = context.getWorld();
        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        if (positions == null) return;

        int totalRequired = amount * multiplier;
        String block = getBlock();
        String replace = getReplace();
        NBTTagCompound nbtResult = getNbt(context);

        List<ChunkCoordinates> changedPositions = new ArrayList<>();
        int remaining = totalRequired;
        for (ChunkCoordinates pos : positions) {
            if (remaining <= 0) break;

            Block currentBlock = world.getBlock(pos.posX, pos.posY, pos.posZ);
            int meta = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
            String blockId = getBlockId(currentBlock, meta);

            boolean match = false;
            if (replace != null) {
                if (matchesBlockId(blockId, replace)) match = true;
            } else if (block != null) {
                if (currentBlock.isReplaceable(world, pos.posX, pos.posY, pos.posZ)
                    || currentBlock.isAir(world, pos.posX, pos.posY, pos.posZ)) match = true;
            } else {
                if (!currentBlock.isAir(world, pos.posX, pos.posY, pos.posZ)) match = true;
            }

            if (match) {
                if (setBlockAt(world, pos, block, nbtResult)) {
                    changedPositions.add(new ChunkCoordinates(pos.posX, pos.posY, pos.posZ));
                    remaining--;
                }
            }
        }

        // Defer neighbor notifications until all blocks are placed to prevent recursion
        // loops
        for (ChunkCoordinates pos : changedPositions) {
            Block b = world.getBlock(pos.posX, pos.posY, pos.posZ);
            world.func_147453_f(pos.posX, pos.posY, pos.posZ, b);
        }
    }

    /**
     * Apply this output to a specific position (used by decorators).
     */
    public void applyAt(IRecipeContext context, ChunkCoordinates pos) {
        setBlockAt(context.getWorld(), pos, block, null);
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        BlockOutput result = new BlockOutput(symbol, block, replace, amount * multiplier, optional, dynamicNbt);
        result.nbtExpressions = this.nbtExpressions;
        result.nbtListOp = this.nbtListOp;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("symbol", String.valueOf(symbol));
        if (block != null) nbt.setString("block", block);
        if (replace != null) nbt.setString("replace", replace);
        nbt.setInteger("amount", amount);
        nbt.setBoolean("optional", optional);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {}

    @Override
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    public void read(JsonObject json) {
        // BlockOutput is immutable, read is handled by fromJson()
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        json.addProperty("symbol", String.valueOf(symbol));
        if (block != null) json.addProperty("block", block);
        if (replace != null) json.addProperty("replace", replace);
        json.addProperty("amount", amount);
        if (optional) json.addProperty("optional", true);

        // Write new NBT system (preferred)
        if (nbtExpressions != null && !nbtExpressions.isEmpty()) {
            JsonArray nbtArray = new JsonArray();
            for (IExpression expr : nbtExpressions) {
                nbtArray.add(new com.google.gson.JsonPrimitive(expr.toString()));
            }
            json.add("nbt", nbtArray);
        } else if (!dynamicNbt.isEmpty()) {
            // Write legacy NBT system
            JsonObject nbtJson = new JsonObject();
            // Note: Expression serialization not implemented here for brevity
            json.add("nbt", nbtJson);
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

    /**
     * Create BlockOutput from JSON.
     */
    public static BlockOutput fromJson(JsonObject json) {
        char symbol = json.get("symbol")
            .getAsString()
            .charAt(0);
        String block = json.has("block") ? json.get("block")
            .getAsString() : null;
        String replace = json.has("replace") ? json.get("replace")
            .getAsString() : null;
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        boolean optional = json.has("optional") && json.get("optional")
            .getAsBoolean();

        Map<String, IExpression> dynamicNbt = new HashMap<>();
        List<IExpression> nbtExpressions = null;
        NBTListOperation nbtListOp = null;

        // Check NBT format: array (new) vs object (legacy)
        if (json.has("nbt")) {
            JsonElement nbtElement = json.get("nbt");

            if (nbtElement.isJsonArray()) {
                // New system: Expression array
                nbtExpressions = new ArrayList<>();
                JsonArray nbtArray = nbtElement.getAsJsonArray();
                for (JsonElement element : nbtArray) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                        .isString()) {
                        String exprStr = element.getAsString();
                        try {
                            IExpression expr = ExpressionParser.parseExpression(exprStr);
                            nbtExpressions.add(expr);
                        } catch (Exception e) {
                            Logger.error("Failed to parse NBT expression: " + exprStr + " - " + e.getMessage());
                        }
                    }
                }
            } else if (nbtElement.isJsonPrimitive() && nbtElement.getAsJsonPrimitive()
                .isString()) {
                    // New system: Single expression string
                    nbtExpressions = new ArrayList<>();
                    String exprStr = nbtElement.getAsString();
                    try {
                        IExpression expr = ExpressionParser.parseExpression(exprStr);
                        nbtExpressions.add(expr);
                    } catch (Exception e) {
                        Logger.error("Failed to parse NBT expression: " + exprStr + " - " + e.getMessage());
                    }
                } else if (nbtElement.isJsonObject()) {
                    // Legacy system: Map<String, IExpression>
                    JsonObject nbtObj = nbtElement.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : nbtObj.entrySet()) {
                        dynamicNbt.put(entry.getKey(), ExpressionsParser.parse(entry.getValue()));
                    }
                }
        }

        // Read NBT list operation
        if (json.has("nbtlist")) {
            try {
                nbtListOp = NBTListOperation.fromJson(json);
            } catch (Exception e) {
                Logger.error("Failed to parse NBT list operation: " + e.getMessage());
            }
        }

        BlockOutput output = new BlockOutput(symbol, block, replace, amount, optional, dynamicNbt);
        output.nbtExpressions = nbtExpressions;
        output.nbtListOp = nbtListOp;
        return output;
    }

    /**
     * Find IRecipeContext from port list.
     */
    private IRecipeContext findRecipeContext(List<IModularPort> ports) {
        for (IModularPort port : ports) {
            if (port instanceof IRecipeContext) {
                return (IRecipeContext) port;
            }
        }
        return null;
    }

    /**
     * Evaluate dynamic NBT once per apply.
     * Supports both legacy dynamicNbt and new nbtExpressions/nbtListOp systems.
     */
    private NBTTagCompound getNbt(IRecipeContext context) {
        boolean hasLegacy = !dynamicNbt.isEmpty();
        boolean hasNew = (nbtExpressions != null && !nbtExpressions.isEmpty()) || nbtListOp != null;

        if (!hasLegacy && !hasNew) {
            return null;
        }

        NBTTagCompound nbtResult = new NBTTagCompound();
        ConditionContext condContext = context.getConditionContext();

        // Legacy system: Map<String, IExpression>
        if (hasLegacy) {
            for (Map.Entry<String, IExpression> entry : dynamicNbt.entrySet()) {
                double val = entry.getValue()
                    .evaluate(condContext);
                nbtResult.setDouble(entry.getKey(), val);
            }
        }

        // New system: nbtExpressions
        if (nbtExpressions != null) {
            for (IExpression expr : nbtExpressions) {
                if (expr instanceof INBTWriteExpression) {
                    ((INBTWriteExpression) expr).applyToNBT(nbtResult, condContext);
                }
            }
        }

        // New system: nbtListOp
        if (nbtListOp != null) {
            nbtListOp.apply(nbtResult);
        }

        return nbtResult.hasNoTags() ? null : nbtResult;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Sets a block at the given position.
     * 
     * @return true if block was changed (or at least attempted)
     */
    private boolean setBlockAt(World world, ChunkCoordinates pos, String blockId, NBTTagCompound nbt) {
        if (blockId == null) {
            world.setBlockToAir(pos.posX, pos.posY, pos.posZ);
            return true;
        }

        String[] parts = blockId.split(":");
        if (parts.length < 2) return false;

        Block blockObj = GameRegistry.findBlock(parts[0], parts[1]);
        if (blockObj == null) return false;

        int meta = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;

        // Optimization: If no NBT and not a TileEntity, use simple flag 3
        if (nbt == null && !blockObj.hasTileEntity(meta)) {
            world.setBlock(pos.posX, pos.posY, pos.posZ, blockObj, meta, 3);
            return true;
        }

        // Use flag 2 (Send to clients, no neighbor notify) to prevent recursive
        // StackOverflow
        // especially important for blocks like Beacons that trigger updates in
        // invalidate()
        world.setBlock(pos.posX, pos.posY, pos.posZ, blockObj, meta, 2);

        if (nbt != null) {
            // BE CAREFUL: accessing TE immediately can trigger recursion in some mods (like
            // Et Futurum/Angelica)
            TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te != null && world.getBlock(pos.posX, pos.posY, pos.posZ) == blockObj) {
                NBTTagCompound baseNbt = new NBTTagCompound();
                te.writeToNBT(baseNbt);

                // Merge dynamic NBT into base
                for (Object keyObj : nbt.func_150296_c()) {
                    String key = (String) keyObj;
                    baseNbt.setTag(key, nbt.getTag(key));
                }

                // Set coordinates to ensure TE stays at correct position
                baseNbt.setInteger("x", pos.posX);
                baseNbt.setInteger("y", pos.posY);
                baseNbt.setInteger("z", pos.posZ);

                te.readFromNBT(baseNbt);
            }
        }

        // Always send update to client if we used flag 2
        world.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
        return true;
    }

    /**
     * Get all positions for this output's symbol.
     * Used by decorators for random selection.
     */
    public List<ChunkCoordinates> getPositions(IRecipeContext context) {
        return new ArrayList<>(context.getSymbolPositions(symbol));
    }

    // Getters
    public char getSymbol() {
        return symbol;
    }

    public String getReplace() {
        return replace;
    }

    public String getBlock() {
        return block;
    }

    public int getAmount() {
        return amount;
    }

    private String getBlockId(Block block, int meta) {
        UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(block);
        if (id == null) return "minecraft:air:0";
        return id.modId + ":" + id.name + ":" + meta;
    }

    private boolean matchesBlockId(String blockId, String pattern) {
        if (pattern.equals("*")) return true;

        String[] blockParts = blockId.split(":");
        String[] patternParts = pattern.split(":");

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

            return patternParts[2].equals("*") || blockParts[2].equals(patternParts[2]);
        }
        return blockId.equals(pattern);
    }
}
