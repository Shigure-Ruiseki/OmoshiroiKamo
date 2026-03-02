package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

/**
 * Recipe input that checks for specific blocks at structure positions.
 * Does not consume blocks, only validates their presence.
 */
public class BlockInput extends AbstractJsonMaterial implements IRecipeInput {

    private final char symbol;
    private final String requiredBlock; // format: "modid:blockname:meta" or null for any block
    private final int count; // minimum count required

    public BlockInput(char symbol, String requiredBlock, int count) {
        this.symbol = symbol;
        this.requiredBlock = requiredBlock;
        this.count = count;
    }

    public BlockInput(char symbol, int count) {
        this(symbol, null, count);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {
        // Find IRecipeContext from ports
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) {
            return false;
        }

        return check(context);
    }

    /**
     * Check if the required blocks exist at the symbol positions.
     *
     * @param context Recipe context with structure information
     * @return true if requirement is satisfied
     */
    public boolean check(IRecipeContext context) {
        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        World world = context.getWorld();

        int matchCount = 0;
        for (ChunkCoordinates pos : positions) {
            Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
            int meta = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
            String blockId = getBlockId(block, meta);

            if (requiredBlock == null || matchesBlockId(blockId, requiredBlock)) {
                matchCount++;
            }
        }

        return matchCount >= count;
    }

    @Override
    public long getRequiredAmount() {
        return count;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void read(JsonObject json) {
        // BlockInput is immutable, read is handled by fromJson()
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        json.addProperty("symbol", String.valueOf(symbol));
        if (requiredBlock != null) {
            json.addProperty("block", requiredBlock);
        }
        json.addProperty("count", count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        write(json);
        return json;
    }

    /**
     * Create BlockInput from JSON.
     */
    public static BlockInput fromJson(JsonObject json) {
        char symbol = json.get("symbol")
            .getAsString()
            .charAt(0);
        String block = json.has("block") ? json.get("block")
            .getAsString() : null;
        int count = json.has("count") ? json.get("count")
            .getAsInt() : 1;

        return new BlockInput(symbol, block, count);
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

    /**
     * Get block ID string in format "modid:blockname:meta"
     */
    private String getBlockId(Block block, int meta) {
        String name = GameData.getBlockRegistry()
            .getNameForObject(block);
        return name + ":" + meta;
    }

    /**
     * Check if block ID matches the pattern.
     * Supports wildcards like "modid:blockname:*" for any meta.
     */
    private boolean matchesBlockId(String blockId, String pattern) {
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
    public char getSymbol() {
        return symbol;
    }

    public String getRequiredBlock() {
        return requiredBlock;
    }

    public int getCount() {
        return count;
    }
}
