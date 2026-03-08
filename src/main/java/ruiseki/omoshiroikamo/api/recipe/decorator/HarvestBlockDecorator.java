package ruiseki.omoshiroikamo.api.recipe.decorator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.io.BlockInput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockOutput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.item.ItemUtils;

/**
 * Decorator that harvests blocks affected by the recipe's BlockInputs and
 * BlockOutputs.
 * Supports Fortune, Silk Touch, Shears, and Harvest Level requirements.
 */
public class HarvestBlockDecorator extends RecipeDecorator {

    private final int fortune;
    private final boolean silkTouch;
    @SuppressWarnings("unused")
    private final boolean shear;
    private final int harvestLevel;

    public HarvestBlockDecorator(IModularRecipe internal, int fortune, boolean silkTouch, boolean shear,
        int harvestLevel) {
        super(internal);
        this.fortune = fortune;
        this.silkTouch = silkTouch;
        this.shear = shear;
        this.harvestLevel = harvestLevel;
    }

    @Override
    public boolean processInputs(List<IModularPort> inputPorts, boolean simulate) {
        if (!simulate) {
            IRecipeContext context = findRecipeContext(inputPorts);
            if (context != null) {
                if (!checkAndHarvest(context, inputPorts, true)) {
                    return false;
                }
            }
        }
        return internal.processInputs(inputPorts, simulate);
    }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        if (!simulate) {
            IRecipeContext context = findRecipeContext(outputPorts);
            if (context != null) {
                if (!checkAndHarvest(context, outputPorts, false)) {
                    return false;
                }
            }
        }
        return internal.processOutputs(outputPorts, simulate);
    }

    /**
     * Harvest blocks from either inputs or outputs.
     * 
     * @param context    Recipe context
     * @param ports      Available ports (for item insertion)
     * @param fromInputs True if checking inputs, false for outputs
     * @return true if all harvest level requirements are met
     */
    private boolean checkAndHarvest(IRecipeContext context, List<IModularPort> ports, boolean fromInputs) {
        List<ChunkCoordinates> targetPositions = new ArrayList<>();
        World world = context.getWorld();

        // 1. Identify all affected positions
        if (fromInputs) {
            for (IRecipeInput input : internal.getInputs()) {
                if (input instanceof BlockInput) {
                    BlockInput bi = (BlockInput) input;
                    if (bi.isConsume()) {
                        targetPositions.addAll(
                            context.getSymbolPositions(
                                bi.toJson()
                                    .get("symbol")
                                    .getAsString()
                                    .charAt(0)));
                    }
                }
            }
        } else {
            for (IRecipeOutput output : internal.getOutputs()) {
                if (output instanceof BlockOutput) {
                    BlockOutput bo = (BlockOutput) output;
                    targetPositions.addAll(bo.getPositions(context));
                }
            }
        }

        // 2. Validate harvest level for all positions first
        for (ChunkCoordinates pos : targetPositions) {
            Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
            if (block.isAir(world, pos.posX, pos.posY, pos.posZ)) continue;

            String harvestTool = block.getHarvestTool(world.getBlockMetadata(pos.posX, pos.posY, pos.posZ));
            if (harvestTool != null) {
                int level = block.getHarvestLevel(world.getBlockMetadata(pos.posX, pos.posY, pos.posZ));
                if (level > harvestLevel) {
                    return false; // Requirement not met, abort recipe
                }
            }
        }

        // 3. Perform harvesting
        for (ChunkCoordinates pos : targetPositions) {
            harvestAt(world, pos, ports);
        }

        return true;
    }

    private void harvestAt(World world, ChunkCoordinates pos, List<IModularPort> ports) {
        int x = pos.posX;
        int y = pos.posY;
        int z = pos.posZ;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (block.isAir(world, x, y, z)) return;

        List<ItemStack> drops = new ArrayList<>();

        if (silkTouch && block.canSilkHarvest(world, null, x, y, z, meta)) {
            drops.add(new ItemStack(block, 1, meta));
        } else {
            // Standard drop with fortune
            drops.addAll(block.getDrops(world, x, y, z, meta, fortune));
        }

        if (drops.isEmpty()) return;

        // Try to insert into item ports
        List<IModularPort> itemPorts = filterByType(ports, IPortType.Type.ITEM);
        for (ItemStack drop : drops) {
            ItemStack remaining = insertIntoPorts(itemPorts, drop);
            if (remaining != null && remaining.stackSize > 0) {
                // Drop into world if port full
                ItemStackHandlerBase.dropStack(world, x, y, z, remaining);
            }
        }
    }

    private ItemStack insertIntoPorts(List<IModularPort> itemPorts, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (IModularPort port : itemPorts) {
            if (port instanceof IInventory) {
                IInventory inv = (IInventory) port;
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack inSlot = inv.getStackInSlot(i);
                    if (inSlot == null) {
                        inv.setInventorySlotContents(i, remaining);
                        return null;
                    } else if (ItemUtils.areStackMergable(inSlot, remaining)) {
                        int canAdd = Math.min(remaining.stackSize, inv.getInventoryStackLimit() - inSlot.stackSize);
                        inSlot.stackSize += canAdd;
                        remaining.stackSize -= canAdd;
                        if (remaining.stackSize <= 0) return null;
                    }
                }
            }
        }
        return remaining;
    }

    private List<IModularPort> filterByType(List<IModularPort> ports, IPortType.Type type) {
        List<IModularPort> filtered = new ArrayList<>();
        for (IModularPort port : ports) {
            if (port.getPortType() == type) {
                filtered.add(port);
            }
        }
        return filtered;
    }

    private IRecipeContext findRecipeContext(List<IModularPort> ports) {
        for (IModularPort port : ports) {
            if (port instanceof IRecipeContext) {
                return (IRecipeContext) port;
            }
        }
        return null;
    }

    public static IModularRecipe fromJson(IModularRecipe recipe, JsonObject json) {
        int fortune = json.has("fortune") ? json.get("fortune")
            .getAsInt() : 0;
        boolean silkTouch = json.has("silkTouch") && json.get("silkTouch")
            .getAsBoolean();
        boolean shear = json.has("shear") && json.get("shear")
            .getAsBoolean();
        int harvestLevel = json.has("harvestLevel") ? json.get("harvestLevel")
            .getAsInt() : 0;

        return new HarvestBlockDecorator(recipe, fortune, silkTouch, shear, harvestLevel);
    }
}
