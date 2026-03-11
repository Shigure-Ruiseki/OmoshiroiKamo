package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
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
    private final String replace; // The block to find (Old / Condition)
    private final String block; // The block to set (New / Result) or requirement
    private final int amount;
    private final boolean consume;
    private final boolean optional;

    public BlockInput(char symbol, String block, String replace, int amount, boolean consume, boolean optional) {
        this.symbol = symbol;
        this.block = block;
        this.replace = replace;
        this.amount = amount;
        this.consume = consume;
        this.optional = optional;
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
                found++;
                if (!simulate) {
                    // Actual manipulation
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
    public void read(JsonObject json) {}

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        json.addProperty("symbol", String.valueOf(symbol));
        if (block != null) json.addProperty("block", block);
        if (replace != null) json.addProperty("replace", replace);
        json.addProperty("amount", amount);
        if (consume) json.addProperty("consume", true);
        if (optional) json.addProperty("optional", true);
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

        return new BlockInput(symbol, block, replace, amount, consume, optional);
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
