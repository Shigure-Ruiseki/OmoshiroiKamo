package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression that counts blocks in a cubic range around the controller.
 * Usage: count_blocks(distance, [filter])
 */
public class CountBlocksFunctionExpression implements IExpression {

    private final List<IExpression> arguments;

    public CountBlocksFunctionExpression(List<IExpression> arguments) {
        this.arguments = arguments;
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null || arguments.isEmpty()) {
            return 0;
        }

        double distanceDouble = arguments.get(0)
            .evaluate(context);
        int distance = (int) Math.floor(distanceDouble);
        if (distance < 0) return 0;

        Set<Block> allowedBlocks = new HashSet<>();
        boolean hasFilter = false;

        if (arguments.size() > 1) {
            hasFilter = true;
            for (int i = 1; i < arguments.size(); i++) {
                IExpression arg = arguments.get(i);
                if (arg instanceof ArrayLiteralExpression array) {
                    for (IExpression element : array.getElements()) {
                        addBlockToFilter(element.evaluateString(context), allowedBlocks);
                    }
                } else {
                    addBlockToFilter(arg.evaluateString(context), allowedBlocks);
                }
            }
        }

        World world = context.getWorld();
        ChunkCoordinates pos = context.getRecipeContext()
            .getControllerPos();
        int cx = pos.posX;
        int cy = pos.posY;
        int cz = pos.posZ;

        long count = 0;
        for (int x = cx - distance; x <= cx + distance; x++) {
            for (int y = cy - distance; y <= cy + distance; y++) {
                for (int z = cz - distance; z <= cz + distance; z++) {
                    if (x == cx && y == cy && z == cz) continue; // skip controller itself

                    Block block = world.getBlock(x, y, z);
                    if (hasFilter) {
                        if (allowedBlocks.contains(block)) count++;
                    } else {
                        if (!block.isAir(world, x, y, z)) count++;
                    }
                }
            }
        }

        return count;
    }

    private void addBlockToFilter(String name, Set<Block> allowedBlocks) {
        if (name == null || name.isEmpty()) return;
        Block block = (Block) Block.blockRegistry.getObject(name);
        if (block != null) {
            allowedBlocks.add(block);
        }
    }
}
