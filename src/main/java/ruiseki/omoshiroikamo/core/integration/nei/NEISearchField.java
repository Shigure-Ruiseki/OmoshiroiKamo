package ruiseki.omoshiroikamo.core.integration.nei;

import java.lang.reflect.Field;

import org.jetbrains.annotations.Nullable;

import codechicken.nei.SearchField;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class NEISearchField {

    private static SearchField cached;

    private NEISearchField() {}

    @Nullable
    private static SearchField resolve() {
        if (cached != null) {
            return cached;
        }

        try {
            Class<?> layoutManager = ReflectionHelper
                .getClass(NEISearchField.class.getClassLoader(), "codechicken.nei.LayoutManager");

            Field f = layoutManager.getField("searchField");
            cached = (SearchField) f.get(null);
            return cached;
        } catch (Throwable t) {
            return null;
        }
    }

    public static boolean exists() {
        return resolve() != null;
    }

    @Nullable
    public static String getText() {
        SearchField sf = resolve();
        return sf != null ? sf.text() : null;
    }

    public static void setText(String text) {
        SearchField sf = resolve();
        if (sf == null) return;

        if (!sf.text()
            .equals(text)) {
            sf.setText(text);
        }
    }

    public static boolean isFocused() {
        SearchField sf = resolve();
        return sf != null && sf.focused();
    }

    public static void setFocused(boolean focused) {
        SearchField sf = resolve();
        if (sf != null) {
            sf.setFocus(focused);
        }
    }
}
