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

package com.mcmoonlake.api.player

import com.mcmoonlake.api.nbt.NBTCompound
import com.mcmoonlake.api.nbt.NBTFactory
import com.mcmoonlake.api.nbt.NBTReadable
import com.mcmoonlake.api.nbt.NBTSavable
import com.mcmoonlake.api.parseBoolean
import com.mcmoonlake.api.parseFloat
import com.mcmoonlake.api.util.ComparisonChain
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player

data class PlayerAbilities(
        var isInvulnerable: Boolean,
        var isFlying: Boolean,
        var canFly: Boolean,
        var canInstantlyBuild: Boolean,
        var mayBuild: Boolean = true,
        var flySpeed: Float = .05f,
        var walkSpeed: Float = .1f
) : ConfigurationSerializable,
        NBTSavable,
        NBTReadable,
        Comparable<PlayerAbilities> {

    override fun save(root: NBTCompound) {
        val abilities = NBTFactory.ofCompound("abilities")
        abilities.putBoolean("invulnerable", isInvulnerable)
        abilities.putBoolean("flying", isFlying)
        abilities.putBoolean("mayfly", canFly)
        abilities.putBoolean("instabuild", canInstantlyBuild)
        abilities.putBoolean("mayBuild", mayBuild)
        abilities.putFloat("flySpeed", flySpeed)
        abilities.putFloat("walkSpeed", walkSpeed)
        root.put(abilities)
    }

    override fun read(root: NBTCompound) {
        val abilities = root.getCompoundOrNull("abilities") ?: return
        isInvulnerable = abilities.getBooleanOrFalse("invulnerable")
        isFlying = abilities.getBooleanOrFalse("flying")
        canFly = abilities.getBooleanOrFalse("mayfly")
        canInstantlyBuild = abilities.getBooleanOrFalse("instabuild")
        mayBuild = abilities.getBooleanOrNull("mayBuild") ?: true
        abilities.getFloatOrNull("flySpeed").also { if(it != null) flySpeed = it }
        abilities.getFloatOrNull("walkSpeed").also { if(it != null) walkSpeed = it }
    }

    override fun compareTo(other: PlayerAbilities): Int
            = ComparisonChain.start()
            .compare(isInvulnerable, other.isInvulnerable)
            .compare(isFlying, other.isFlying)
            .compare(canFly, other.canFly)
            .compare(canInstantlyBuild, other.canInstantlyBuild)
            .compare(mayBuild, other.mayBuild)
            .compare(flySpeed, other.flySpeed)
            .compare(walkSpeed, other.walkSpeed)
            .result

    override fun serialize(): MutableMap<String, Any> {
        val result = LinkedHashMap<String, Any>()
        result["invulnerable"] = isInvulnerable
        result["flying"] = isFlying
        result["mayfly"] = canFly
        result["instabuild"] = canInstantlyBuild
        result["mayBuild"] = mayBuild
        result["flySpeed"] = flySpeed
        result["walkSpeed"] = walkSpeed
        return result
    }

    companion object {

        @JvmStatic
        @JvmName("ofPlayer")
        fun ofPlayer(player: Player): PlayerAbilities
                = PlayerAbilities(false, false, false, false).also { it.read(NBTFactory.readEntityTag(player)) }

        @JvmStatic
        @JvmName("deserialize")
        fun deserialize(args: Map<String, Any>): PlayerAbilities {
            val isInvulnerable = args["invulnerable"]?.parseBoolean() ?: false
            val isFlying = args["flying"]?.parseBoolean() ?: false
            val canFly = args["mayfly"]?.parseBoolean() ?: false
            val canInstantlyBuild = args["instabuild"]?.parseBoolean() ?: false
            val mayBuild = args["mayBuild"]?.parseBoolean() ?: false
            val flySpeed = args["flySpeed"]?.parseFloat() ?: 0f
            val walkSpeed = args["walkSpeed"]?.parseFloat() ?: 0f
            return PlayerAbilities(isInvulnerable, isFlying, canFly, canInstantlyBuild, mayBuild, flySpeed, walkSpeed)
        }
    }
}
