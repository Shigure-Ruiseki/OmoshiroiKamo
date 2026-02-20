package ruiseki.omoshiroikamo.core.persist.nbt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

/**
 * If this field should be persisted in Tile Entities.
 * Fields that are a subtype of {@link INBTSerializable} must not be null, they should
 * have a dummy value that will then be populated with the actual values.
 * It will automatically be added to
 * {@link TileEntityOK#writeToNBT(net.minecraft.nbt.NBTTagCompound)}
 * and {@link TileEntityOK#readFromNBT(net.minecraft.nbt.NBTTagCompound)}.
 *
 * @author rubensworks
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NBTPersist {

    String value() default "";
}
