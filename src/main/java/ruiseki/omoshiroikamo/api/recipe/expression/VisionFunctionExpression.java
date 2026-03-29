package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Advanced vision sensing function that checks for blocks in a given direction.
 * Supports can_see_sky and can_see_void with customizable filters.
 */
public class VisionFunctionExpression implements IExpression {

    public enum Direction {
        SKY,
        VOID
    }

    private final Direction direction;
    private final List<IExpression> filterParams;

    public VisionFunctionExpression(Direction direction, List<IExpression> filterParams) {
        this.direction = direction;
        this.filterParams = filterParams;
    }

    @Override
    public double evaluate(ConditionContext context) {
        World world = context.getWorld();
        if (world == null) return 0;

        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        // Prepare allowed filters
        boolean allowTransparent = true;
        Set<Block> allowedBlocks = new HashSet<>();

        for (IExpression filter : filterParams) {
            if (filter instanceof StringLiteralExpression SLE) {
                String val = SLE.getStringValue();
                if ("transparent".equalsIgnoreCase(val)) {
                    allowTransparent = true;
                } else if ("strict".equalsIgnoreCase(val)) {
                    allowTransparent = false;
                } else {
                    Block block = Block.getBlockFromName(val);
                    if (block != null) {
                        allowedBlocks.add(block);
                    }
                }
            } else if (filter instanceof ArrayLiteralExpression ALE) {
                for (IExpression element : ALE.getElements()) {
                    if (element instanceof StringLiteralExpression SLE) {
                        String val = SLE.getStringValue();
                        Block block = Block.getBlockFromName(val);
                        if (block != null) {
                            allowedBlocks.add(block);
                        }
                    }
                }
            }
        }

        if (direction == Direction.SKY) {
            return canSeeSky(world, x, y, z, allowTransparent, allowedBlocks) ? 1.0 : 0.0;
        } else {
            return canSeeVoid(world, x, y, z, allowTransparent, allowedBlocks) ? 1.0 : 0.0;
        }
    }

    private boolean canSeeSky(World world, int x, int y, int z, boolean allowTransparent, Set<Block> allowedBlocks) {
        // Minecraft 1.7.10 height varies, world.getHeight() is usually 256.
        int height = world.getHeight();
        for (int ty = y + 1; ty < height; ty++) {
            Block block = world.getBlock(x, ty, z);
            if (!isAllowed(block, allowTransparent, allowedBlocks)) {
                return false;
            }
        }
        return true;
    }

    private boolean canSeeVoid(World world, int x, int y, int z, boolean allowTransparent, Set<Block> allowedBlocks) {
        for (int ty = y - 1; ty >= 0; ty--) {
            Block block = world.getBlock(x, ty, z);
            if (!isAllowed(block, allowTransparent, allowedBlocks)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllowed(Block block, boolean allowTransparent, Set<Block> allowedBlocks) {
        // Air is always allowed (unless specified otherwise, but air is usuallyair)
        if (block.isAir(null, 0, 0, 0)) return true;

        // Check if block is in allowed list
        if (allowedBlocks.contains(block)) return true;

        // Check transparency if requested
        if (allowTransparent) {
            // Light opacity 0 usually means transparent (glass, etc.)
            // Note: in 1.7.10 glass opacity is often 0
            if (block.getLightOpacity() == 0) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "can_see_" + (direction == Direction.SKY ? "sky" : "void") + "(" + filterParams + ")";
    }
}
