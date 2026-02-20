package ruiseki.omoshiroikamo.core.persist.nbt;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Default implementation of {@link INBTProvider} as a component.
 *
 * @author rubensworks
 */
public class NBTProviderComponent implements INBTProvider {

    private final INBTProvider provider;

    private List<Field> nbtPersistedFields = null;

    public NBTProviderComponent(INBTProvider provider) {
        this.provider = provider;
        generateNBTPersistedFields();
    }

    private void generateNBTPersistedFields() {
        nbtPersistedFields = new LinkedList<Field>();
        for (Class<?> clazz = provider.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(NBTPersist.class)) {
                    nbtPersistedFields.add(field);
                }
            }
        }
    }

    private void writePersistedField(Field field, NBTTagCompound tag) {
        NBTClassType.performActionForField(provider, field, tag, true);
    }

    private void readPersistedField(Field field, NBTTagCompound tag) {
        NBTClassType.performActionForField(provider, field, tag, false);
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {
        for (Field field : nbtPersistedFields) {
            writePersistedField(field, tag);
        }
    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {
        for (Field field : nbtPersistedFields) {
            readPersistedField(field, tag);
        }
    }
}
