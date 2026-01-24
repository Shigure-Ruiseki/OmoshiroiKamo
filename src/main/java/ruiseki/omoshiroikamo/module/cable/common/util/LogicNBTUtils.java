package ruiseki.omoshiroikamo.module.cable.common.util;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

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
            case "double" -> LogicValues.of(tag.getDouble("Value"));
            case "long" -> LogicValues.of(tag.getLong("Value"));
            case "string" -> LogicValues.of(tag.getString("Value"));
            default -> null;
        };
    }

    public static void writeLiteralValue(NBTTagCompound tag, ILogicValue value) {
        tag.setString("Type", "LITERAL");
        tag.setString(
            "ValueType",
            value.getType()
                .getId());

        switch (value.getType()
            .getId()) {
            case "boolean" -> tag.setBoolean("Value", value.asBoolean());
            case "int" -> tag.setInteger("Value", value.asInt());
            case "double" -> tag.setDouble("Value", value.asDouble());
            case "long" -> tag.setLong("Value", value.asLong());
            case "string" -> tag.setString("Value", value.asString());
        }
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

}
