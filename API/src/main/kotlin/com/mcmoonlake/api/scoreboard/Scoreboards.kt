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
import org.bukkit.scoreboard.Scoreboard

object Scoreboards {

    private val impl: Class<ScoreboardSide> by lazy {
        @Suppress("UNCHECKED_CAST")
        Class.forName("com.mcmoonlake.impl.scoreboard.ScoreboardSideImpl") as Class<ScoreboardSide> }

    @JvmStatic
    @JvmName("registerNewScoreboardSide")
    fun registerNewScoreboardSide(name: String, fromMain: Boolean = false): ScoreboardSide
            = impl.getConstructor(String::class.java, Scoreboard::class.java)
            .newInstance(name, if(fromMain) getScoreboardManager().mainScoreboard else null)
}
