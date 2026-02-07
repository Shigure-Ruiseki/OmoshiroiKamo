package ruiseki.omoshiroikamo.module.ids.common.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicTypeRegistry;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicTypes;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.LogicValues;

public class LogicNBTUtils {

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
            case "list" -> LogicValues.of(tag.getString("Value"));
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
            case "long" -> list.add(" §fValue: §b" + tag.getLong("Value"));
            case "float" -> list.add(" §fValue: §b" + tag.getFloat("Value"));
            case "double" -> list.add(" §fValue: §b" + tag.getDouble("Value"));
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
        String value = tag.getString("ValueType");
        list.add(
            " §fType: §e" + LogicTypeRegistry.get(value)
                .toString());
    }

    public static void addLogicTreeTooltip(NBTTagCompound tag, List<String> list) {
        if (tag == null) {
            list.add("§7<empty>");
            return;
        }

        list.add("§7Logic Tree:");
        addLogicNode(tag, list, new ArrayList<>(), true, 0);
    }

    private static final int MAX_DEPTH = 6;

    private static void addLogicNode(NBTTagCompound tag, List<String> list, List<Boolean> branches, boolean isLast,
        int depth) {
        if (tag == null) return;

        if (depth > MAX_DEPTH) {
            list.add(prefix(branches, true) + "§8… (max depth)");
            return;
        }

        String type = tag.getString("Type");

        switch (type) {
            case "READER" -> addReaderNode(tag, list, branches, isLast);
            case "LITERAL" -> addLiteralNode(tag, list, branches, isLast);
            case "OP" -> addOperatorNode(tag, list, branches, isLast, depth);
            default -> list.add(prefix(branches, isLast) + "§cUnknown node");
        }
    }

    private static void addReaderNode(NBTTagCompound tag, List<String> list, List<Boolean> branches, boolean isLast) {
        String key = tag.getString("Key");
        list.add(prefix(branches, isLast) + "§aReader§f: " + key);

        List<Boolean> next = new ArrayList<>(branches);
        next.add(!isLast);

        if (tag.hasKey("Side")) {
            ForgeDirection dir = ForgeDirection.getOrientation(tag.getByte("Side"));
            list.add(prefix(next, false) + "§7Side: §e" + dir);
        }

        int x = tag.getInteger("X");
        int y = tag.getInteger("Y");
        int z = tag.getInteger("Z");
        list.add(prefix(next, false) + "§7Pos: §e" + x + ", " + y + ", " + z);

        if (tag.hasKey("ValueType")) {
            String id = tag.getString("ValueType");
            LogicType<?> t = LogicTypeRegistry.get(id);
            list.add(prefix(next, true) + "§7Type: §b" + (t != null ? t.getId() : "UNKNOWN"));
        }
    }

    private static void addLiteralNode(NBTTagCompound tag, List<String> list, List<Boolean> branches, boolean isLast) {
        String type = tag.getString("ValueType");

        String value;
        try {
            value = switch (type) {
                case "boolean" -> String.valueOf(tag.getBoolean("Value"));
                case "int" -> String.valueOf(tag.getInteger("Value"));
                case "long" -> String.valueOf(tag.getLong("Value"));
                case "float" -> String.valueOf(tag.getFloat("Value"));
                case "double" -> String.valueOf(tag.getDouble("Value"));
                case "string" -> "\"" + tag.getString("Value") + "\"";
                default -> "?";
            };
        } catch (Exception e) {
            value = "§c<invalid>";
        }

        list.add(prefix(branches, isLast) + "§bLiteral§f: " + type + " = " + value);
    }

    private static void addOperatorNode(NBTTagCompound tag, List<String> list, List<Boolean> branches, boolean isLast,
        int depth) {
        String op = tag.getString("Op");
        list.add(prefix(branches, isLast) + "§eOp§f: " + op);

        if (!tag.hasKey("Children", 9)) return;

        NBTTagList children = tag.getTagList("Children", 10);
        List<Boolean> next = new ArrayList<>(branches);
        next.add(!isLast);

        for (int i = 0; i < children.tagCount(); i++) {
            boolean last = i == children.tagCount() - 1;
            addLogicNode(children.getCompoundTagAt(i), list, next, last, depth + 1);
        }
    }

    private static String prefix(List<Boolean> branches, boolean isLast) {
        StringBuilder sb = new StringBuilder();

        for (boolean hasNext : branches) {
            sb.append(hasNext ? "§7│  " : "    ");
        }

        sb.append(isLast ? "§7└ " : "§7├ ");
        return sb.toString();
    }

    public static NBTTagCompound literal(String valueType) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Type", "LITERAL");
        tag.setString("ValueType", valueType);
        return tag;
    }

    public static NBTTagCompound booleanLiteral(boolean v) {
        NBTTagCompound t = literal(LogicTypes.BOOLEAN.getId());
        t.setBoolean("Value", v);
        return t;
    }

    public static NBTTagCompound intLiteral(int v) {
        NBTTagCompound t = literal(LogicTypes.INT.getId());
        t.setInteger("Value", v);
        return t;
    }

    public static NBTTagCompound longLiteral(long v) {
        NBTTagCompound t = literal(LogicTypes.LONG.getId());
        t.setLong("Value", v);
        return t;
    }

    public static NBTTagCompound floatLiteral(float v) {
        NBTTagCompound t = literal(LogicTypes.FLOAT.getId());
        t.setFloat("Value", v);
        return t;
    }

    public static NBTTagCompound doubleLiteral(double v) {
        NBTTagCompound t = literal(LogicTypes.DOUBLE.getId());
        t.setDouble("Value", v);
        return t;
    }

    public static NBTTagCompound stringLiteral(String v) {
        NBTTagCompound t = literal(LogicTypes.STRING.getId());
        t.setString("Value", v);
        return t;
    }

}
