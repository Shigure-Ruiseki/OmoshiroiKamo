package ruiseki.omoshiroikamo.core.common.util;

import com.mojang.realmsclient.gui.ChatFormatting;

public class StringUtils {

    public static String getFormattedString(String string, ChatFormatting formatting) {
        return formatting + string + ChatFormatting.RESET;
    }

    public static String getFormattedString(ChatFormatting pre, String string, ChatFormatting post) {
        return ChatFormatting.RESET + "" + pre + string + ChatFormatting.RESET + "" + post;
    }

    public static ChatFormatting getValidFormatting(String formatting) {
        ChatFormatting result = ChatFormatting.getByName(formatting);
        return (result != null) ? result : ChatFormatting.WHITE;
    }

    public static String getDashedLine(int length) {
        return org.apache.commons.lang3.StringUtils.repeat('-', length);
    }

    public static String pad(String original, int targetLength) {
        int padLength = targetLength - original.length();
        if (padLength <= 0) return original;

        int leftPad = padLength / 2;
        String leftPadded = org.apache.commons.lang3.StringUtils.leftPad(original, original.length() + leftPad);
        return org.apache.commons.lang3.StringUtils.rightPad(leftPadded, targetLength);
    }

    public static String uppercaseFirst(String original) {
        return original.substring(0, 1)
            .toUpperCase() + original.substring(1);
    }
}
