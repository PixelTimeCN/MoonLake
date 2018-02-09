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

interface EntryTeam : Entry {

    var displayName: String

    var prefix: String

    var suffix: String

    var color: ChatColor

    var isAllowFriendlyFire: Boolean

    var isCanSeeFriendlyInvisibles: Boolean

    val members: Set<String>

    val size: Int

    fun addMember(entry: String)

    fun removeMember(entry: String): Boolean

    fun hasMember(entry: String): Boolean

    fun getOption(option: Option): OptionResponse

    fun setOption(option: Option, status: OptionStatus): OptionResponse

    data class OptionResponse(
            val option: Option,
            val status: OptionStatus?,
            val response: Response) {

        val isSuccess: Boolean
                = response == Response.SUCCESS

        enum class Response {
            SUCCESS,
            FAILED,
            NOT_COMPATIBLE,
            ;
        }
    }

    /**
     * # Represents an option which may be applied to this team.
     */
    enum class Option {

        /**
         * # How to display the name tags of players on this team.
         */
        NAME_TAG_VISIBILITY,
        /**
         * # How to display the death messages for players on this team.
         *
         * * Valid only at 1.9 or higher.
         */
        DEATH_MESSAGE_VISIBILITY,
        /**
         * # How players of this team collide with others.
         *
         * * Valid only at 1.9 or higher.
         */
        COLLISION_RULE
    }

    /**
     * # How an option may be applied to members of this team.
     */
    enum class OptionStatus {

        /**
         * # Apply this option to everyone.
         */
        ALWAYS,
        /**
         * # Never apply this option.
         */
        NEVER,
        /**
         * # Apply this option only for opposing teams.
         */
        FOR_OTHER_TEAMS,
        /**
         * # Apply this option for only team members.
         */
        FOR_OWN_TEAM
    }
}
