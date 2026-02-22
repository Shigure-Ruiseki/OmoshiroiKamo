package ruiseki.omoshiroikamo.core.block.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

public class BlockPropertyProviderComponent implements IBlockPropertyProvider {

    private final Block block;
    private List<Field> autoFields;

    public BlockPropertyProviderComponent(Block block) {
        this.block = block;
        this.autoFields = generateAutoPropertyFields(block.getClass());
    }

    private List<Field> generateAutoPropertyFields(Class<?> clazz) {
        List<Field> fields = new LinkedList<>();

        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {

                if (!field.isAnnotationPresent(AutoBlockProperty.class)) continue;

                field.setAccessible(true);
                fields.add(field);
            }
        }

        return fields;
    }

    @Override
    public void registerProperties() {
        for (Field field : autoFields) {
            try {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                Object value = field.get(isStatic ? null : block);

                if (value == null) continue;

                AutoBlockProperty annotation = field.getAnnotation(AutoBlockProperty.class);

                if (value instanceof BlockProperty<?>property) {

                    register(property, annotation);

                } else if (value instanceof BlockProperty<?>[]array) {

                    for (BlockProperty<?> property : array) {
                        if (property != null) {
                            register(property, annotation);
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void register(BlockProperty<?> property, AutoBlockProperty annotation) {
        if (annotation.byClass()) {
            BlockPropertyRegistry.registerProperty(block.getClass(), property);
        } else {
            BlockPropertyRegistry.registerProperty(block, property);
        }
    }
}
