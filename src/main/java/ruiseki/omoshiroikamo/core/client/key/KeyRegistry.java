package ruiseki.omoshiroikamo.core.client.key;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.settings.KeyBinding;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Enum that contains all custom hotkeys that
 * are added. Every key also has a
 * {@link KeyBinding} for that specific key.
 *
 * @author immortaleeb
 *
 */
public class KeyRegistry implements IKeyRegistry {

    private final Multimap<KeyBinding, IKeyHandler> keyHandlerMap = HashMultimap.create();
    private final Set<KeyBinding> registeredKeys = new HashSet<>();

    /**
     * Create a new keybinding.
     *
     * @param mod        The mod.
     * @param name       The unique name.
     * @param defaultKey The keycode.
     * @return A new keybinding.
     */
    public static KeyBinding newKeyBinding(ModBase mod, String name, int defaultKey) {
        String id = LibMisc.LANG.localize("key." + mod.getModId() + "." + name);
        String category = LibMisc.LANG.localize("key.categories." + mod.getModId());
        return new KeyBinding(id, defaultKey, category);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerKeyInput(InputEvent.KeyInputEvent event) {
        for (KeyBinding kb : keyHandlerMap.keySet()) {
            if (kb.isPressed()) {
                fireKeyPressed(kb);
            }
        }
    }

    private void fireKeyPressed(KeyBinding kb) {
        for (IKeyHandler h : keyHandlerMap.get(kb)) {
            h.onKeyPressed(kb);
        }
    }

    @Override
    public void addKeyHandler(KeyBinding kb, IKeyHandler handler) {
        if (!registeredKeys.contains(kb)) {
            ClientRegistry.registerKeyBinding(kb);
            registeredKeys.add(kb);
        }
        keyHandlerMap.put(kb, handler);
    }

}
