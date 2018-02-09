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

package com.mcmoonlake.api.scoreboard

import com.mcmoonlake.api.getScoreboardManager
import org.bukkit.scoreboard.*

open class ScoreboardSide(
        val name: String,
        fromMain: Boolean = false
) : ScoreboardAbstract() {

    final override val handle: Scoreboard =
            if(fromMain) getScoreboardManager().mainScoreboard
            else getScoreboardManager().newScoreboard

    protected val objective: Objective = handle.registerNewObjective(name, "dummy")

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
    }

    var displayName: String
        get() = objective.displayName
        set(value) { objective.displayName = value }

    @Throws(IllegalStateException::class)
    override fun registerNewEntry(name: String): EntrySide {
        var team: Team? = handle.getTeam(name)
        if(team != null)
            throw IllegalStateException("Entry name '$name' is already in use.")
        team = handle.registerNewTeam(name)
        team.addEntry(name)
        return EntrySideBase(name, team, objective.getScore(name))
    }

    override fun getEntryOrRegisterNew(name: String): EntrySide {
        var team: Team? = handle.getTeam(name)
        if(team == null) {
            team = handle.registerNewTeam(name)
            team.addEntry(name)
        }
        return EntrySideBase(name, team!!, objective.getScore(name))
    }

    @Throws(IllegalStateException::class)
    override fun getEntry(name: String): EntrySide? {
        val team: Team = handle.getTeam(name) ?: throw IllegalStateException("Entry name '$name' is not register.")
        return EntrySideBase(name, team, objective.getScore(name))
    }

    @Throws(IllegalStateException::class)
    fun unregisterEntry(name: String) {
        val team: Team = handle.getTeam(name) ?: throw IllegalStateException("Entry name '$name' is not register.")
        team.unregister()
        handle.resetScores(name)
    }

    protected open class EntrySideBase(
            override val name: String,
            private val handleTeam: Team,
            private val handleScore: Score
    ) : EntrySide {

        override var score: Int
            get() = handleScore.score
            set(value) { handleScore.score = value }

        override var prefix: String
            get() = handleTeam.prefix
            set(value) { handleTeam.prefix = value }

        override var suffix: String
            get() = handleTeam.suffix
            set(value) { handleTeam.suffix = value }
    }
}
