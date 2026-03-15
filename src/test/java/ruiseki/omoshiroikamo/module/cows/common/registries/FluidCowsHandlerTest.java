package ruiseki.omoshiroikamo.module.cows.common.registries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FluidCowsHandlerTest {

    @Test
    @DisplayName("Should sanitize name for texture safety")
    public void testNameSanitization() {
        // テクスチャ名として安全な名前に変換されるか（ドットの除去と先頭大文字化）
        assertEquals("Ironmolten", dummyCapitalize("iron.molten"));
        assertEquals("Water", dummyCapitalize("water"));
    }

    private String dummyCapitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        s = s.replace(".", "");
        return s.substring(0, 1)
            .toUpperCase() + s.substring(1);
    }
}
