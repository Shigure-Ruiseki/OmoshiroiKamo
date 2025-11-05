package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

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
    private boolean hasCalculated = false;

    public void setModifiers(List<IModifierBlock> modifiers) {
        this.modifiers = (modifiers != null) ? modifiers : Collections.emptyList();
        this.hasCalculated = false;
    }

    public void calculateAttributeMultipliers() {
        if (modifiers.isEmpty()) {
            attributeTotals.clear();
            hasCalculated = true;
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

        hasCalculated = true;
    }

    public boolean hasAttribute(String attributeName) {
        return attributeTotals.keySet()
            .stream()
            .anyMatch(name -> name.equalsIgnoreCase(attributeName));
    }

    public float getAttributeMultiplier(String attributeName) {
        return attributeTotals.entrySet()
            .stream()
            .filter(
                e -> e.getKey()
                    .equalsIgnoreCase(attributeName))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(1.0F);
    }
}
