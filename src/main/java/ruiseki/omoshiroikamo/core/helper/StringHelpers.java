package ruiseki.omoshiroikamo.core.helper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

/**
 * A collection of String helper methods.
 *
 * @author rubensworks
 *
 */
public final class StringHelpers {

    private static final String SPACE = " ";
    private static final String NEWLINE_PATTERN = "\\\\n";

    /**
     * Split the input string into lines while preserving the full words.
     * This will also forcefully add newlines when '\n' is found in the input string.
     *
     * @param input     The input sentence.
     * @param maxLength The maximum length of a line.
     * @param prefix    A prefix to add to each produced line. This will not increase the character
     *                  count per line.
     * @return The sentence split into lines.
     */
    public static List<String> splitLines(String input, int maxLength, String prefix) {
        List<String> list = Lists.newLinkedList();

        for (String partialInput : input.split(NEWLINE_PATTERN)) {
            StringBuilder buffer = new StringBuilder();
            for (String word : partialInput.split(SPACE)) {
                if (buffer.length() > 0) {
                    buffer.append(SPACE);
                }
                buffer.append(word);
                if (buffer.length() >= maxLength) {
                    list.add(prefix + buffer.toString());
                    buffer = new StringBuilder();
                }
            }
            if (buffer.length() > 0) {
                list.add(prefix + buffer.toString());
            }
        }

        return list;
    }

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
        return StringUtils.repeat('-', length);
    }

    public static String pad(String original, int targetLength) {
        int padLength = targetLength - original.length();
        if (padLength <= 0) return original;

        int leftPad = padLength / 2;
        String leftPadded = StringUtils.leftPad(original, original.length() + leftPad);
        return StringUtils.rightPad(leftPadded, targetLength);
    }

    public static String uppercaseFirst(String original) {
        return original.substring(0, 1)
            .toUpperCase() + original.substring(1);
    }
}
