/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package ruiseki.omoshiroikamo.api.capabilities;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

public interface ICapabilityProvider extends CapabilityProvider {

    /**
     * Determines if this object has support for the capability in question on the specific side.
     * The return value of this MIGHT change during runtime if this object gains or loses support
     * for a capability. It is not required to call this function before calling
     * {@link #getCapability(Capability, ForgeDirection)}.
     * <p>
     * Basically, this method functions analogously to {@link Map#containsKey(Object)}.
     * <p>
     * <em>Example:</em>
     *   A Pipe getting a cover placed on one side causing it lose the Inventory attachment function for that side.
     * </p><p>
     * This is a light weight version of getCapability, intended for metadata uses.
     * </p>
     * @param capability The capability to check
     * @param facing The Side to check from:
     *   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability. If true, then {@link #getCapability(Capability, ForgeDirection)}
     * must not return null.
     */
    boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing);

    /**
     * Retrieves the handler for the capability requested on the specific side.
     * <ul>
     * <li>The return value <strong>CAN</strong> be null if the object does not support the capability.</il>
     * <li>The return value <strong>CAN</strong> be the same for multiple faces.</li>
     * </ul>
     * <p>
     * Basically, this method functions analogously to {@link Map#get(Object)}.
     *
     * @param capability The capability to check
     * @param facing The Side to check from,
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return The requested capability. Must <strong>NOT</strong> be null when {@link #hasCapability(Capability, ForgeDirection)}
     * would return true.
     */
    @Nullable
    <T> T getCapability(Capability<T> capability, ForgeDirection facing);

    @Override
    default @Nullable <T> T getCapability(@NotNull Class<T> capabilityClass,
                                          @NotNull ForgeDirection facing) {

        Capability<T> cap = CapabilityManager.INSTANCE.get(capabilityClass);
        if (cap == null) return null;

        if (!hasCapability(cap, facing)) return null;

        return getCapability(cap, facing);
    }
}
