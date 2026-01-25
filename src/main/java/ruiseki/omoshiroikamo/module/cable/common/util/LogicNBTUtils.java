package ruiseki.omoshiroikamo.module.cable.common.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypeRegistry;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public final class LogicNBTUtils {

    private LogicNBTUtils() {}

    public static ILogicValue readLiteralValue(NBTTagCompound tag) {
        if (tag == null) return null;

        String type = tag.getString("ValueType");

        return switch (type) {
            case "boolean" -> LogicValues.of(tag.getBoolean("Value"));
            case "int" -> LogicValues.of(tag.getInteger("Value"));
            case "long" -> LogicValues.of(tag.getLong("Value"));
            case "float" -> LogicValues.of(tag.getFloat("Value"));
            case "double" -> LogicValues.of(tag.getDouble("Value"));
            case "string" -> LogicValues.of(tag.getString("Value"));
            default -> null;
        };
    }

    public static void addLiteralTooltip(NBTTagCompound tag, List<String> list) {
        list.add("§7Literal:");

        String type = tag.getString("ValueType");
        list.add(" §fType: §a" + type);

        switch (type) {
            case "boolean" -> list.add(" §fValue: §b" + tag.getBoolean("Value"));
            case "int" -> list.add(" §fValue: §b" + tag.getInteger("Value"));
            case "double" -> list.add(" §fValue: §b" + tag.getDouble("Value"));
            case "long" -> list.add(" §fValue: §b" + tag.getLong("Value"));
            case "string" -> list.add(" §fValue: §b\"" + tag.getString("Value") + "\"");
            default -> list.add(" §cInvalid value");
        }
    }

    public static void addTooltip(ItemStack stack, List<String> list) {
        NBTTagCompound logic = ItemNBTUtils.getCompound(stack, "Logic", false);

        String type = logic.getString("Type");
        list.add("§6Logic Type: §f" + type);

        switch (type) {
            case "READER" -> LogicNBTUtils.addReaderTooltip(logic, list);
            case "OP" -> LogicNBTUtils.addLogicTreeTooltip(logic, list);
            case "LITERAL" -> LogicNBTUtils.addLiteralTooltip(logic, list);

            default -> list.add("§cUnknown logic");
        }
    }

    public static void addReaderTooltip(NBTTagCompound tag, List<String> list) {
        list.add("§7Reader:");

        list.add(" §fKey: §a" + tag.getString("Key"));

        int x = tag.getInteger("X");
        int y = tag.getInteger("Y");
        int z = tag.getInteger("Z");
        list.add(" §fPos: §b" + x + ", " + y + ", " + z);

        int side = tag.getByte("Side");
        list.add(" §fSide: §e" + ForgeDirection.getOrientation(side));
        String value = tag.getString("Value");
        list.add(
            " §fValue: §e" + LogicTypeRegistry.get(value)
                .toString());
    }

    public static void addLogicTreeTooltip(NBTTagCompound tag, List<String> list) {
        list.add("§7Operator:");
        addLogicNode(tag, list, 0, true);
    }

    private static final int MAX_DEPTH = 6;

    private static void addLogicNode(NBTTagCompound tag, List<String> list, int depth, boolean isLast) {
        if (tag == null) return;

        if (depth > MAX_DEPTH) {
            list.add(prefix(depth, isLast) + "§7...");
            return;
        }

        String type = tag.getString("Type");

        switch (type) {
            case "READER" -> addReaderNode(tag, list, depth, isLast);
            case "LITERAL" -> addLiteralNode(tag, list, depth, isLast);
            case "OP" -> addOperatorNode(tag, list, depth, isLast);
            default -> list.add(prefix(depth, isLast) + "§cUnknown");
        }
    }

    private static void addReaderNode(NBTTagCompound tag, List<String> list, int depth, boolean isLast) {
        String key = tag.getString("Key");

        list.add(prefix(depth, isLast) + "§aReader§f: " + key);

        int side = tag.getByte("Side");
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        list.add(prefix(depth + 1, true) + "§7Side: §e" + dir);

        if (tag.hasKey("Value")) {
            String value = tag.getString("Value");
            Object type = LogicTypeRegistry.get(value);
            list.add(prefix(depth + 1, true) + "§7Type: §b" + (type != null ? type : "UNKNOWN"));
        }
    }

    private static void addLiteralNode(NBTTagCompound tag, List<String> list, int depth, boolean isLast) {
        String type = tag.getString("ValueType");

        String value = switch (type) {
            case "boolean" -> String.valueOf(tag.getBoolean("Value"));
            case "int" -> String.valueOf(tag.getInteger("Value"));
            case "long" -> String.valueOf(tag.getLong("Value"));
            case "float" -> String.valueOf(tag.getFloat("Value"));
            case "double" -> String.valueOf(tag.getDouble("Value"));
            case "string" -> "\"" + tag.getString("Value") + "\"";
            default -> "?";
        };

        list.add(prefix(depth, isLast) + "§bLiteral§f: " + type + " = " + value);
    }

    private static void addOperatorNode(NBTTagCompound tag, List<String> list, int depth, boolean isLast) {
        String op = tag.getString("Op");
        list.add(prefix(depth, isLast) + "§eOp§f: " + op);

        if (!tag.hasKey("Children", 9)) return;

        NBTTagList children = tag.getTagList("Children", 10);
        for (int i = 0; i < children.tagCount(); i++) {
            boolean last = i == children.tagCount() - 1;
            addLogicNode(children.getCompoundTagAt(i), list, depth + 1, last);
        }
    }

    private static String prefix(int depth, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            sb.append("§7│  ");
        }

        sb.append(isLast ? "§7└─ " : "§7├─ ");
        return sb.toString();
    }
}
