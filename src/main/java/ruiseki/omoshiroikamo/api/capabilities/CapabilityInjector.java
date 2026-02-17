/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package ruiseki.omoshiroikamo.api.capabilities;

import java.lang.reflect.Field;

public final class CapabilityInjector {

    public static void inject(Class<?> clazz) {

        for (Field field : clazz.getDeclaredFields()) {

            CapabilityInject annotation = field.getAnnotation(CapabilityInject.class);

            if (annotation == null) continue;

            Class<?> capClass = annotation.value();
            Capability<?> capability = CapabilityManager.INSTANCE.get(capClass);

            if (capability == null) {
                throw new RuntimeException("Capability not registered: " + capClass);
            }

            try {
                field.setAccessible(true);
                field.set(null, capability); // static field
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
