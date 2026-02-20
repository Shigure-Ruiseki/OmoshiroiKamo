package ruiseki.omoshiroikamo.core.client.icon;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;

/**
 * An alternative way of registering icons.
 * 
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class IconProvider {

    private final ClientProxyComponent clientProxy;
    private final List<Pair<Pair<Object, Field>, String>> toRegister = Lists.newLinkedList();

    public IconProvider(ClientProxyComponent clientProxy) {
        this.clientProxy = clientProxy;
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected void registerIcon(Object object, Field field, String location) {
        toRegister.add(Pair.of(Pair.of(object, field), location));
    }

    protected IIcon registerIcon(TextureMap textureMap, String location) {
        return textureMap.registerIcon(
            new ResourceLocation(
                clientProxy.getMod()
                    .getModId(),
                location).toString());
    }

    @SubscribeEvent
    public void onPreTextureStitch(TextureStitchEvent.Pre event) {
        for (Pair<Pair<Object, Field>, String> entry : toRegister) {
            IIcon icon = registerIcon(event.map, entry.getValue());
            Object object = entry.getLeft()
                .getLeft();
            Field field = entry.getLeft()
                .getRight();
            try {
                field.setAccessible(true);
                field.set(object, icon);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                    String.format(
                        "The icon field %s in class %s could not be set.",
                        field.getName(),
                        object.getClass()
                            .getCanonicalName()));
            }
        }
    }

    /**
     * Scan all icon fields for the given object.
     * This will automatically populate any {@link Icon} fields.
     * This should be called before pre-texture stitching.
     * 
     * @param object The object to scan.
     */
    public void registerIconHolderObject(Object object) {
        for (Class<?> clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Icon.class)) {
                    Icon annotation = field.getAnnotation(Icon.class);
                    registerIcon(object, field, annotation.location());
                }
            }
        }
    }
}
