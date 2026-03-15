package ruiseki.omoshiroikamo.api.condition;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.json.ItemJson;

/**
 * Condition that checks the block at the current context position.
 */
public class BlockCondition implements ICondition {

    private final String targetBlockName;
    private final int targetMeta;
    private Block targetBlock;

    public BlockCondition(String targetBlockName, int targetMeta) {
        this.targetBlockName = targetBlockName;
        this.targetMeta = targetMeta;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        if (targetBlock == null) {
            ItemJson data = new ItemJson();
            data.name = targetBlockName;
            ItemStack stack = ItemJson.resolveItemStack(data);
            if (stack != null && stack.getItem() != null) {
                targetBlock = Block.getBlockFromItem(stack.getItem());
            }
        }

        if (targetBlock == null) return false;

        Block current = context.getWorld()
            .getBlock(context.getX(), context.getY(), context.getZ());
        int meta = context.getWorld()
            .getBlockMetadata(context.getX(), context.getY(), context.getZ());

        if (targetMeta == -1) {
            return current == targetBlock;
        }
        return current == targetBlock && meta == targetMeta;
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocalFormatted("omoshiroikamo.condition.block", targetBlockName);
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block");
        JsonObject blockObj = new JsonObject();
        blockObj.addProperty("name", targetBlockName);
        if (targetMeta == -1) {
            blockObj.addProperty("data", -1);
        } else {
            blockObj.addProperty("data", targetMeta);
        }
        json.add("block", blockObj);
    }

    public static ICondition fromJson(JsonObject json) {
        if (json.has("block")) {
            String name;
            int meta = 0;

            if (json.get("block")
                .isJsonObject()) {
                JsonObject obj = json.getAsJsonObject("block");
                name = obj.has("name") ? obj.get("name")
                    .getAsString() : "";
                meta = obj.has("data") ? obj.get("data")
                    .getAsInt() : 0;
            } else {
                name = json.get("block")
                    .getAsString();
            }

            if (!name.isEmpty()) {
                return new BlockCondition(name, meta);
            }
        }
        return null;
    }
}
