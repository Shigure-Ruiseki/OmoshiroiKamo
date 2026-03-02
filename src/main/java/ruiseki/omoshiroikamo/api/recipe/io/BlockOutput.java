package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
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
 * Recipe output that changes blocks at structure positions.
 * Does not add items to inventory, only modifies world blocks.
 */
public class BlockOutput extends AbstractJsonMaterial implements IRecipeOutput {

    private final char symbol;
    private final String placeBlock; // format: "modid:blockname:meta" or null for air
    private final int count; // number of positions to change

    public BlockOutput(char symbol, String placeBlock, int count) {
        this.symbol = symbol;
        this.placeBlock = placeBlock;
        this.count = count;
    }

    public BlockOutput(char symbol, int count) {
        this(symbol, null, count);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public boolean checkCapacity(List<IModularPort> ports) {
        // Blocks don't have capacity constraints
        return true;
    }

    @Override
    public void apply(List<IModularPort> ports) {
        // Find IRecipeContext from ports
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) {
            return;
        }

        apply(context);
    }

    /**
     * Apply block changes to the structure.
     *
     * @param context Recipe context with structure information
     */
    public void apply(IRecipeContext context) {
        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        World world = context.getWorld();

        // Determine how many positions to change
        int targetCount = Math.min(count, positions.size());

        for (int i = 0; i < targetCount; i++) {
            ChunkCoordinates pos = positions.get(i);
            setBlockAt(world, pos, placeBlock);
        }
    }

    /**
     * Apply this output to a specific position (used by decorators).
     */
    public void applyAt(IRecipeContext context, ChunkCoordinates pos) {
        setBlockAt(context.getWorld(), pos, placeBlock);
    }

    @Override
    public IRecipeOutput copy() {
        return new BlockOutput(symbol, placeBlock, count);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("symbol", String.valueOf(symbol));
        if (placeBlock != null) {
            nbt.setString("placeBlock", placeBlock);
        }
        nbt.setInteger("count", count);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // Read-only object, NBT reading not fully supported
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
        // BlockOutput is immutable, read is handled by fromJson()
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        json.addProperty("symbol", String.valueOf(symbol));
        if (placeBlock != null) {
            json.addProperty("block", placeBlock);
        }
        json.addProperty("count", count);
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
        int count = json.has("count") ? json.get("count")
            .getAsInt() : 1;

        return new BlockOutput(symbol, block, count);
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
     * Set block at the given position.
     */
    private void setBlockAt(World world, ChunkCoordinates pos, String blockId) {
        if (blockId == null) {
            // Place air
            world.setBlockToAir(pos.posX, pos.posY, pos.posZ);
        } else {
            // Parse block ID
            String[] parts = blockId.split(":");
            if (parts.length >= 2) {
                String modid = parts[0];
                String blockName = parts[1];
                int meta = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;

                Block block = GameData.getBlockRegistry()
                    .getObject(modid + ":" + blockName);
                if (block != null) {
                    world.setBlock(pos.posX, pos.posY, pos.posZ, block, meta, 3);
                }
            }
        }
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

    public String getPlaceBlock() {
        return placeBlock;
    }

    public int getCount() {
        return count;
    }
}
