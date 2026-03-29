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

    public List<IExpression> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        NBTTagList list = new NBTTagList();
        for (IExpression element : elements) {
            EvaluationValue eval = element.evaluate(context);
            if (eval.isString()) {
                list.appendTag(new NBTTagString(eval.asString()));
            } else if (eval.isNumeric()) {
                list.appendTag(NBTTypeInference.parseNumeric(eval.asDouble()));
            } else if (eval.isNbt()) {
                list.appendTag(eval.asNbt());
            }
        }
        return new EvaluationValue(list);
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

    // Legacy method but keeping for compatibility if needed
    public NBTTagList toNBTList(ConditionContext context) {
        EvaluationValue val = evaluate(context);
        return (NBTTagList) val.asNbt();
    }
}
