package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class LivingRegistryItem {

    @Getter
    protected final int id;
    @Getter
    protected final String displayName;
    @Getter
    protected final String texture;
    @Getter
    protected final int xpValue;

    @Getter
    protected Map<String, String> lang;

    @Getter
    @Setter
    protected boolean enabled;

    public LivingRegistryItem(int id, String displayName, String texture, int xpValue) {
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.xpValue = xpValue;
    }

    public LivingRegistryItem setLang(String langCode, String value) {
        if (this.lang == null) {
            this.lang = new HashMap<>();
        }

        if (langCode != null && !langCode.isEmpty() && value != null && !value.isEmpty()) {
            this.lang.put(langCode, value);
        }

        return this;
    }

    public String getItemName() {
        return "item.living_matter." + displayName + ".name";
    }
}
