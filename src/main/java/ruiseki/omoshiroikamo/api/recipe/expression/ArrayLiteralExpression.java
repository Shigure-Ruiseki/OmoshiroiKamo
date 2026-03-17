package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression representing an array literal: ['item1', 'item2', ...]
 * Used for setting NBT arrays like display.Lore.
 */
public class ArrayLiteralExpression implements IExpression {

    private final List<IExpression> elements;

    public ArrayLiteralExpression(List<IExpression> elements) {
        this.elements = new ArrayList<>(elements);
    }

    /**
     * Get the list of element expressions.
     * 
     * @return Unmodifiable list of elements
     */
    public List<IExpression> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Evaluate as numeric value (returns array length).
     */
    @Override
    public double evaluate(ConditionContext context) {
        return elements.size();
    }

    /**
     * Convert array literal to NBTTagList.
     * Supports both string and numeric elements.
     *
     * @param context The condition context
     * @return NBTTagList containing the elements
     */
    public NBTTagList toNBTList(ConditionContext context) {
        NBTTagList list = new NBTTagList();

        for (IExpression element : elements) {
            if (element instanceof StringLiteralExpression) {
                // String element
                String value = ((StringLiteralExpression) element).getStringValue();
                list.appendTag(new NBTTagString(value));
            } else {
                // Numeric element
                double value = element.evaluate(context);
                list.appendTag(NBTTypeInference.parseNumeric(value));
            }
        }

        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(
                elements.get(i)
                    .toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
