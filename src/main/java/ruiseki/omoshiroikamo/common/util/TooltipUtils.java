package ruiseki.omoshiroikamo.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import lombok.Getter;

@Getter
public class TooltipUtils {

    private final List<String> lines;

    private TooltipUtils() {
        this.lines = new ArrayList<>();
    }

    public static TooltipUtils builder() {
        return new TooltipUtils();
    }

    public TooltipUtils add(String line) {
        if (line != null) {
            this.lines.add(line);
        }
        return this;
    }

    public TooltipUtils addIf(boolean condition, String line) {
        if (condition && line != null) {
            this.lines.add(line);
        }
        return this;
    }

    public TooltipUtils addFormatted(String format, Object... args) {
        this.lines.add(String.format(format, args));
        return this;
    }

    public TooltipUtils addAll(Collection<String> lines) {
        if (lines != null) {
            this.lines.addAll(lines);
        }
        return this;
    }

    public TooltipUtils addLang(String key, Object... args) {
        this.lines.add(new ChatComponentTranslation(key, args).getFormattedText());
        return this;
    }

    public TooltipUtils addLangIf(boolean condition, String key, Object... args) {
        if (condition) {
            this.lines.add(new ChatComponentTranslation(key, args).getFormattedText());
        }
        return this;
    }

    public TooltipUtils addColored(EnumChatFormatting color, String line) {
        if (line != null) {
            this.lines.add(color + line + EnumChatFormatting.RESET);
        }
        return this;
    }

    public TooltipUtils addColoredIf(boolean condition, EnumChatFormatting color, String line) {
        if (condition && line != null) {
            this.lines.add(color + line + EnumChatFormatting.RESET);
        }
        return this;
    }

    public TooltipUtils addColoredLang(EnumChatFormatting color, String key, Object... args) {
        this.lines.add(color + new ChatComponentTranslation(key, args).getFormattedText() + EnumChatFormatting.RESET);
        return this;
    }

    public TooltipUtils addColoredLangIf(boolean condition, EnumChatFormatting color, String key, Object... args) {
        if (condition) {
            this.lines
                .add(color + new ChatComponentTranslation(key, args).getFormattedText() + EnumChatFormatting.RESET);
        }
        return this;
    }

    public TooltipUtils addLabelWithValue(String label, EnumChatFormatting labelColor, String value,
        EnumChatFormatting valueColor) {
        if (label != null && value != null) {
            this.lines.add(labelColor + label + ": " + valueColor + value + EnumChatFormatting.RESET);
        }
        return this;
    }

    public TooltipUtils addLabelWithValueIf(boolean condition, String label, EnumChatFormatting labelColor,
        String value, EnumChatFormatting valueColor) {
        if (condition && label != null && value != null) {
            this.lines.add(labelColor + label + ": " + valueColor + value + EnumChatFormatting.RESET);
        }
        return this;
    }

    public TooltipUtils addLabelWithLangValue(String labelKey, EnumChatFormatting labelColor, String value,
        EnumChatFormatting valueColor) {
        if (value != null) {
            String label = new ChatComponentTranslation(labelKey).getFormattedText();
            this.lines.add(labelColor + label + ": " + valueColor + value + EnumChatFormatting.RESET);
        }
        return this;
    }

    public TooltipUtils addLabelWithLangValueIf(boolean condition, String labelKey, EnumChatFormatting labelColor,
        String value, EnumChatFormatting valueColor) {
        if (condition && value != null) {
            String label = new ChatComponentTranslation(labelKey).getFormattedText();
            this.lines.add(labelColor + label + ": " + valueColor + value + EnumChatFormatting.RESET);
        }
        return this;
    }

    public List<String> build() {
        return new ArrayList<>(this.lines);
    }
}
