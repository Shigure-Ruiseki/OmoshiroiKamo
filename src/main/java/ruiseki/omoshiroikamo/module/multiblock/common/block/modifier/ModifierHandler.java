package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;

public class ModifierHandler {

    private List<IModifierBlock> modifiers = new ArrayList<>();
    private final Map<String, Float> attributeTotals = new HashMap<>();

    public void setModifiers(List<IModifierBlock> modifiers) {
        this.modifiers = (modifiers != null) ? modifiers : Collections.emptyList();
    }

    public void calculateAttributeMultipliers() {
        if (modifiers.isEmpty()) {
            attributeTotals.clear();
            return;
        }

        Map<String, Float> attributeFactors = new HashMap<>();
        Map<String, IModifierAttribute> baseAttributes = new HashMap<>();

        for (IModifierBlock modifier : modifiers) {
            List<IModifierAttribute> attributes = modifier.getAttributes();
            if (attributes == null) {
                continue;
            }

            for (IModifierAttribute attr : attributes) {
                String name = attr.getAttributeName();
                if (name == null || name.isEmpty()) {
                    continue;
                }

                attributeFactors.merge(name, attr.getModificationFactor(), Float::sum);
                baseAttributes.putIfAbsent(name, attr);
            }
        }

        attributeTotals.clear();
        attributeFactors.forEach((name, totalFactor) -> {
            IModifierAttribute base = baseAttributes.get(name);
            if (base != null) {
                float totalMultiplier = base.getMultiplier(totalFactor);
                attributeTotals.put(name, totalMultiplier);
            }
        });

    }

    public boolean hasAttribute(String attributeName) {
        return attributeTotals.keySet()
            .stream()
            .anyMatch(name -> name.equalsIgnoreCase(attributeName));
    }

    public float getAttributeMultiplier(String attributeName) {
        return getAttributeMultiplier(attributeName, 1.0F);
    }

    public float getAttributeMultiplier(String attributeName, float defaultValue) {
        return attributeTotals.entrySet()
            .stream()
            .filter(
                e -> e.getKey()
                    .equalsIgnoreCase(attributeName))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(defaultValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ModifierHandler{");

        if (attributeTotals.isEmpty()) {
            sb.append("no modifiers");
        } else {
            attributeTotals.forEach(
                (key, value) -> sb.append(key)
                    .append("=")
                    .append(String.format("%.2f", value))
                    .append(", "));
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");
        return sb.toString();
    }

}
