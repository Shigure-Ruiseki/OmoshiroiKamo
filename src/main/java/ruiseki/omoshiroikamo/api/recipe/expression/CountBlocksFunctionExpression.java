package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagList;
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
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null || arguments.isEmpty()) {
            return EvaluationValue.ZERO;
        }

        double distanceDouble = arguments.get(0)
            .evaluate(context)
            .asDouble();
        int distance = (int) Math.floor(distanceDouble);
        if (distance < 0) return EvaluationValue.ZERO;

        Set<Block> allowedBlocks = new HashSet<>();
        boolean hasFilter = false;

        if (arguments.size() > 1) {
            hasFilter = true;
            for (int i = 1; i < arguments.size(); i++) {
                IExpression arg = arguments.get(i);
                EvaluationValue eval = arg.evaluate(context);
                if (eval.isNbt() && eval.asNbt() instanceof NBTTagList list) {
                    for (int j = 0; j < list.tagCount(); j++) {
                        addBlockToFilter(list.getStringTagAt(j), allowedBlocks);
                    }
                } else {
                    addBlockToFilter(eval.asString(), allowedBlocks);
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
                    if (x == cx && y == cy && z == cz) continue;

                    Block block = world.getBlock(x, y, z);
                    if (hasFilter) {
                        if (allowedBlocks.contains(block)) count++;
                    } else {
                        if (!block.isAir(world, x, y, z)) count++;
                    }
                }
            }
        }

        return new EvaluationValue((double) count);
    }

    private void addBlockToFilter(String name, Set<Block> allowedBlocks) {
        if (name == null || name.isEmpty()) return;
        Block block = (Block) Block.blockRegistry.getObject(name);
        if (block != null) {
            allowedBlocks.add(block);
        }
    }

    @Override
    public String toString() {
        return "count_blocks(" + arguments.get(0) + ")";
    }
}
