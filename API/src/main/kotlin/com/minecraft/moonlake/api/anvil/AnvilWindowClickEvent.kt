/*
 * Copyright (C) 2016-Present The MoonLake (mcmoonlake@hotmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.minecraft.moonlake.api.anvil

import com.minecraft.moonlake.api.event.Cancellable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AnvilWindowClickEvent(anvilWindow: AnvilWindow, player: Player, val clickSlot: AnvilWindowSlot, val clickItemStack: ItemStack) : AnvilWindowEvent(anvilWindow, player), Cancellable {

    private var cancel: Boolean = false

    override fun isCancelled(): Boolean
            = cancel

    override fun setCancelled(cancel: Boolean)
            { this.cancel = cancel }
}