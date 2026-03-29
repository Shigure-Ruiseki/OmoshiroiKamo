package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression that evaluates vision-related functions (e.g., can_see_sky).
 */
public class VisionFunctionExpression implements IExpression {

    public enum Direction {
        SKY,
        VOID
    }

    private final Direction direction;
    private final List<IExpression> arguments;

    public VisionFunctionExpression(Direction direction, List<IExpression> arguments) {
        this.direction = direction;
        this.arguments = arguments;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null) return EvaluationValue.FALSE;

        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        // Parse allowed-block arguments: "transparent", "strict", or "modid:block"
        boolean strict = false;
        Set<String> allowedBlockIds = new HashSet<>();

        if (arguments != null) {
            for (IExpression argExpr : arguments) {
                EvaluationValue eval = argExpr.evaluate(context);
                if (eval.isNbt() && eval.asNbt() instanceof NBTTagList list) {
                    for (int i = 0; i < list.tagCount(); i++) {
                        String arg = list.getStringTagAt(i);
                        if ("strict".equalsIgnoreCase(arg)) {
                            strict = true;
                        } else if (!"transparent".equalsIgnoreCase(arg) && arg != null && !arg.isEmpty()) {
                            allowedBlockIds.add(arg.toLowerCase());
                        }
                    }
                } else {
                    String arg = eval.asString();
                    if ("strict".equalsIgnoreCase(arg)) {
                        strict = true;
                    } else if (!"transparent".equalsIgnoreCase(arg) && arg != null && !arg.isEmpty()) {
                        allowedBlockIds.add(arg.toLowerCase());
                    }
                }
            }
        }

        World world = context.getWorld();
        boolean result = checkVision(world, x, y, z, direction, strict, allowedBlockIds);
        return result ? EvaluationValue.TRUE : EvaluationValue.FALSE;
    }

    private boolean checkVision(World world, int x, int y, int z, Direction dir, boolean strict,
        Set<String> allowedBlockIds) {
        if (dir == Direction.SKY) {
            for (int checkY = y + 1; checkY < 256; checkY++) {
                Block block = world.getBlock(x, checkY, z);
                if (block == null || block == Blocks.air || block.isAir(world, x, checkY, z)) continue;
                if (isAllowed(block, strict, allowedBlockIds)) continue;
                return false;
            }
            return true;
        } else { // VOID
            for (int checkY = y - 1; checkY >= 0; checkY--) {
                Block block = world.getBlock(x, checkY, z);
                if (block == null || block == Blocks.air || block.isAir(world, x, checkY, z)) continue;
                if (block == Blocks.bedrock) return true; // 岩盤に当たった場合は成功扱い
                if (isAllowed(block, strict, allowedBlockIds)) continue;
                return false;
            }
            return true;
        }
    }

    private boolean isAllowed(Block block, boolean strict, Set<String> allowedBlockIds) {
        if (!allowedBlockIds.isEmpty()) {
            String blockId = Block.blockRegistry.getNameForObject(block);
            if (blockId != null) {
                String idLower = blockId.toLowerCase();
                if (allowedBlockIds.contains(idLower) || allowedBlockIds.contains("minecraft:" + idLower)) {
                    return true;
                }
            }
        }
        if (strict) return false;
        if (!block.isOpaqueCube() || block.getLightOpacity() == 0) return true;
        return false;
    }

    @Override
    public String toString() {
        return direction.name()
            .toLowerCase() + "()";
    }
}
