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

package com.mcmoonlake.impl.scoreboard

import com.mcmoonlake.api.getScoreboardManager
import com.mcmoonlake.api.player.MoonLakePlayer
import com.mcmoonlake.api.scoreboard.EntrySide
import com.mcmoonlake.api.scoreboard.ScoreboardSide
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class ScoreboardSideImpl(
        name: String,
        scoreboard: Scoreboard? = null
) : ScoreboardSide {

    private val handle: Scoreboard = scoreboard ?: getScoreboardManager().newScoreboard
    private val objective = handle.registerNewObjective(name, "dummy")

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
    }

    override var displayName: String
        get() = objective.displayName
        set(value) { objective.displayName = value }

    override fun registerNewEntry(name: String): EntrySide {
        var team: Team? = handle.getTeam(name)
        if(team != null)
            throw IllegalStateException("Entry name '$name' is already in use.")
        team = handle.registerNewTeam(name)
        team.addEntry(name)
        return EntrySideImpl(name, team, objective.getScore(name))
    }

    override fun getEntryOrRegisterNew(name: String): EntrySide {
        var team: Team? = handle.getTeam(name)
        if(team == null) {
            team = handle.registerNewTeam(name)
            team.addEntry(name)
        }
        return EntrySideImpl(name, team!!, objective.getScore(name))
    }

    override fun getEntry(name: String): EntrySide? {
        val team: Team = handle.getTeam(name) ?: throw IllegalStateException("Entry name '$name' is not register.")
        return EntrySideImpl(name, team, objective.getScore(name))
    }

    override fun apply(player: Player) {
        player.scoreboard = handle
    }

    override fun apply(player: MoonLakePlayer)
            = apply(player.bukkitPlayer)
}
