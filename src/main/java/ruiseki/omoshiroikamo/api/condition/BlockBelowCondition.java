package ruiseki.omoshiroikamo.api.condition;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.json.ItemJson;

/**
 * Condition that checks the block directly below the context position.
 */
public class BlockBelowCondition implements ICondition {

    private final Block targetBlock;
    private final int targetMeta;

    public BlockBelowCondition(Block targetBlock, int targetMeta) {
        this.targetBlock = targetBlock;
        this.targetMeta = targetMeta;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        Block below = context.getWorld()
            .getBlock(context.getX(), context.getY() - 1, context.getZ());
        int meta = context.getWorld()
            .getBlockMetadata(context.getX(), context.getY() - 1, context.getZ());

        if (targetMeta == -1) {
            return below == targetBlock;
        }
        return below == targetBlock && meta == targetMeta;
    }

    @Override
    public String getDescription() {
        ItemStack stack = new ItemStack(targetBlock, 1, targetMeta == -1 ? 0 : targetMeta);
        return StatCollector.translateToLocalFormatted("omoshiroikamo.condition.block_below", stack.getDisplayName());
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block_below");
        JsonObject blockObj = new JsonObject();
        ItemStack stack = new ItemStack(targetBlock, 1, targetMeta == -1 ? 0 : targetMeta);
        ItemJson itemJson = ItemJson.parseItemStack(stack);
        if (itemJson != null) {
            blockObj.addProperty("name", itemJson.name);
            if (targetMeta == -1) {
                blockObj.addProperty("data", -1);
            } else {
                blockObj.addProperty("data", itemJson.meta);
            }
            json.add("block", blockObj);
        }
    }

    public static ICondition fromJson(JsonObject json) {
        if (json.has("block")) {
            JsonObject blockJson = json.getAsJsonObject("block");
            ItemStack stack = ItemJson.resolveItemStack(ItemJson.fromJson(blockJson));
            if (stack != null && stack.getItem() != null) {
                Block block = Block.getBlockFromItem(stack.getItem());
                int meta = stack.getItemDamage();
                if (blockJson.has("data") && blockJson.get("data")
                    .getAsInt() == -1) {
                    meta = -1;
                }
                return new BlockBelowCondition(block, meta);
            }
        }
        return null;
    }
}
