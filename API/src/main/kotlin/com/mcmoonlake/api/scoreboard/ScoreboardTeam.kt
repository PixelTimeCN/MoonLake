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

import com.mcmoonlake.api.chat.ChatColor
import com.mcmoonlake.api.getScoreboardManager
import com.mcmoonlake.api.isCombatOrLaterVer
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

open class ScoreboardTeam(
        fromMain: Boolean = false
) : ScoreboardAbstract() {

    final override val handle: Scoreboard =
            if(fromMain) getScoreboardManager().mainScoreboard
            else getScoreboardManager().newScoreboard

    @Throws(IllegalStateException::class)
    override fun registerNewEntry(name: String): EntryTeam {
        var team: Team? = handle.getTeam(name)
        if(team != null)
            throw IllegalStateException("Entry name '$name' is already in use.")
        team = handle.registerNewTeam(name)
        team.addEntry(name)
        return EntryTeamBase(name, team)
    }

    override fun getEntryOrRegisterNew(name: String): EntryTeam {
        var team: Team? = handle.getTeam(name)
        if(team == null) {
            team = handle.registerNewTeam(name)
            team.addEntry(name)
        }
        return EntryTeamBase(name, team!!)
    }

    @Throws(IllegalStateException::class)
    override fun getEntry(name: String): EntryTeam? {
        val team: Team = handle.getTeam(name) ?: throw IllegalStateException("Entry name '$name' is not register.")
        return EntryTeamBase(name, team)
    }

    @Throws(IllegalStateException::class)
    fun unregisterEntry(name: String) {
        val team: Team = handle.getTeam(name) ?: throw IllegalStateException("Entry name '$name' is not register.")
        team.unregister()
    }

    protected open class EntryTeamBase(
            override val name: String,
            private val handle: Team
    ) : EntryTeam {

        override var displayName: String
            get() = handle.displayName
            set(value) { handle.displayName = value }

        override var prefix: String
            get() = handle.prefix
            set(value) { handle.prefix = value }

        override var suffix: String
            get() = handle.suffix
            set(value) { handle.suffix = value }

        override var color: ChatColor
            get() = ChatColor.fromBukkit(handle.color)
            set(value) { handle.color = value.toBukkit() }


        override var isAllowFriendlyFire: Boolean
            get() = handle.allowFriendlyFire()
            set(value) { handle.setAllowFriendlyFire(value) }

        override var isCanSeeFriendlyInvisibles: Boolean
            get() = handle.canSeeFriendlyInvisibles()
            set(value) { handle.setCanSeeFriendlyInvisibles(value) }

        override val members: Set<String>
            get() = handle.entries

        override val size: Int
            get() = handle.size

        override fun addMember(entry: String)
                = handle.addEntry(entry)

        override fun removeMember(entry: String)
                = handle.removeEntry(entry)

        override fun hasMember(entry: String): Boolean
                = handle.hasEntry(entry)

        override fun getOption(option: EntryTeam.Option): EntryTeam.OptionResponse {
            if(!isCombatOrLaterVer) {
                return if(option != EntryTeam.Option.NAME_TAG_VISIBILITY) {
                    EntryTeam.OptionResponse(option, null,
                            EntryTeam.OptionResponse.Response.NOT_COMPATIBLE)
                }
                else {
                    @Suppress("DEPRECATION") // 1.8.x Not Deprecated
                    EntryTeam.OptionResponse(option,
                            EntryTeam.OptionStatus.valueOf(handle.nameTagVisibility.name),
                            EntryTeam.OptionResponse.Response.SUCCESS)
                }
            } else {
                // 1.9+
                val optionOb = org.bukkit.scoreboard.Team.Option.valueOf(option.name)
                val statusOb = handle.getOption(optionOb)
                return EntryTeam.OptionResponse(EntryTeam.Option.valueOf(optionOb.name),
                        EntryTeam.OptionStatus.valueOf(statusOb.name),
                        EntryTeam.OptionResponse.Response.SUCCESS)
            }
        }

        override fun setOption(option: EntryTeam.Option, status: EntryTeam.OptionStatus): EntryTeam.OptionResponse {
            if(!isCombatOrLaterVer) {
                return if(option != EntryTeam.Option.NAME_TAG_VISIBILITY) {
                    EntryTeam.OptionResponse(option, status,
                            EntryTeam.OptionResponse.Response.NOT_COMPATIBLE)
                }
                else {
                    @Suppress("DEPRECATION") // 1.8.x Not Deprecated
                    handle.nameTagVisibility = org.bukkit.scoreboard.NameTagVisibility.valueOf(status.name)
                    EntryTeam.OptionResponse(option, status,
                            EntryTeam.OptionResponse.Response.SUCCESS)
                }
            } else {
                // 1.9+
                val optionOb = Team.Option.valueOf(option.name)
                handle.setOption(optionOb,Team.OptionStatus.valueOf(status.name))
                return EntryTeam.OptionResponse(EntryTeam.Option.valueOf(optionOb.name),
                        status, EntryTeam.OptionResponse.Response.SUCCESS)
            }
        }
    }
}
