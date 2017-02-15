/*
 * Copyright (C) 2016 The MoonLake Authors
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
 
 
package com.minecraft.moonlake.manager;

import com.minecraft.moonlake.MoonLakeAPI;
import com.minecraft.moonlake.api.player.*;
import com.minecraft.moonlake.exception.IllegalBukkitVersionException;
import com.minecraft.moonlake.exception.MoonLakeException;
import com.minecraft.moonlake.reflect.Reflect;
import com.minecraft.moonlake.validate.Validate;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.minecraft.moonlake.reflect.Reflect.*;

/**
 * <h1>PlayerManager</h1>
 * 玩家管理实现类
 *
 * @version 1.0
 * @author Month_Light
 */
public class PlayerManager extends MoonLakeManager {

    private final static Class<? extends SimpleMoonLakePlayer> CLASS_SIMPLEMOONLAKEPLAYER;
    private final static Class<?> CLASS_CRAFTPLAYER;
    private final static Class<?> CLASS_ENTITYHUMAN;
    private final static Class<?> CLASS_ENTITYPLAYER;
    private final static Method METHOD_GETHANDLE;
    private final static Method METHOD_GETPROFILE;
    private final static Field FIELD_LOCALE;

    static {

        try {

            switch (getServerVersionNumber()) {

                case 8:     // Bukkit 1.8
                    CLASS_SIMPLEMOONLAKEPLAYER = SimpleMoonLakePlayer_v1_8.class;
                    break;
                case 9:     // Bukkit 1.9
                    CLASS_SIMPLEMOONLAKEPLAYER = SimpleMoonLakePlayer_v1_9.class;
                    break;
                case 10:    // Bukkit 1.10
                    CLASS_SIMPLEMOONLAKEPLAYER = SimpleMoonLakePlayer_v1_10.class;
                    break;
                case 11:    // Bukkit 1.11
                    CLASS_SIMPLEMOONLAKEPLAYER = SimpleMoonLakePlayer_v1_11.class;
                    break;
                default:    // Not Support
                    CLASS_SIMPLEMOONLAKEPLAYER = null;
            }
            CLASS_CRAFTPLAYER = PackageType.CRAFTBUKKIT_ENTITY.getClass("CraftPlayer");
            CLASS_ENTITYHUMAN = PackageType.MINECRAFT_SERVER.getClass("EntityHuman");
            CLASS_ENTITYPLAYER = PackageType.MINECRAFT_SERVER.getClass("EntityPlayer");
            METHOD_GETHANDLE = getMethod(CLASS_CRAFTPLAYER, "getHandle");
            METHOD_GETPROFILE = getMethod(CLASS_ENTITYHUMAN, "getProfile");
            FIELD_LOCALE = getField(CLASS_ENTITYPLAYER, true, "locale");
        }
        catch (Exception e) {

            throw new MoonLakeException("The player manager reflect raw exception.", e);
        }
    }

    /**
     * 玩家管理实现类构造函数
     */
    private PlayerManager() {

    }

    /**
     * 获取指定玩家对象从目标名称
     *
     * @param name 玩家名
     * @return 玩家对象 没有则返回 null
     * @throws IllegalArgumentException 如果玩家名字对象为 {@code null} 则抛出异常
     */
    public static Player fromName(String name) {

        return Bukkit.getServer().getPlayer(name);
    }

    /**
     * 获取指定玩家对象从目标 UUID
     *
     * @param uuid 玩家 UUID
     * @return 玩家对象 没有则返回 null
     * @throws IllegalArgumentException 如果玩家 UUID 对象为 {@code null} 则抛出异常
     */
    public static Player fromUUID(UUID uuid) {

        return Bukkit.getServer().getPlayer(uuid);
    }

    /**
     * 获取在线玩家的数组对象
     *
     * @return 在线玩家数组
     */
    public static Player[] getOnlines() {

        Collection<? extends Player> collection = Bukkit.getServer().getOnlinePlayers();

        return collection.toArray(new Player[collection.size()]);
    }

    /**
     * 获取在线玩家的数组对象除了目标玩家
     *
     * @param target 目标玩家
     * @return 在线玩家数组除了目标玩家
     * @throws IllegalArgumentException 如果目标玩家对象为 {@code null} 则抛出异常
     */
    public static Player[] getOnlinesExceptTarget(Player target) {

        Validate.notNull(target, "The player target object is null.");

        Player[] onlines = getOnlines();
        List<Player> playerList = new ArrayList<>();

        for(Player player : onlines) {

            if(!player.equals(target)) {

                playerList.add(player);
            }
        }
        return playerList.toArray(new Player[playerList.size()]);
    }

    /**
     * 将字符串玩家对象转换到 Bukkit 玩家对象
     *
     * @param players 字符串 玩家
     * @return Bukkit 玩家
     * @throws IllegalArgumentException 如果字符串玩家对象为 {@code null} 则抛出异常
     */
    public static Player[] adapter(String... players) {

        Validate.notNull(players, "The player object is null.");

        int index = 0;
        Player[] adapter = new Player[players.length];

        for(final String player : players) {

            adapter[index++] = fromName(player);
        }
        return adapter;
    }

    /**
     * 将字符串玩家对象转换到 Bukkit 玩家对象
     *
     * @param player 字符串 玩家
     * @return Bukkit 玩家
     * @throws IllegalArgumentException 如果字符串玩家对象为 {@code null} 则抛出异常
     */
    public static Player adapter(String player) {

        Validate.notNull(player, "The player object is null.");

        return fromName(player);
    }

    /**
     * 将 Bukkit 玩家对象转换到 MoonLake 玩家对象
     *
     * @param players Bukkit 玩家
     * @return MoonLake 玩家
     * @throws IllegalArgumentException 如果 Bukkit 玩家对象为 {@code null} 则抛出异常
     * @throws IllegalBukkitVersionException 如果 Bukkit 服务器版本不支持则抛出异常
     */
    public static MoonLakePlayer[] adapter(Player... players) {

        Validate.notNull(players, "The player object is null.");

        //
        // 验证类是否为 null 则抛出非法 Bukkit 版本异常

        if(CLASS_SIMPLEMOONLAKEPLAYER == null) {

            throw new IllegalBukkitVersionException("The moonlake player class not support bukkit version.");
        }
        ///

        int index = 0;
        MoonLakePlayer[] adapter = new MoonLakePlayer[players.length];

        try {

            Constructor<? extends SimpleMoonLakePlayer> constructor = CLASS_SIMPLEMOONLAKEPLAYER.getConstructor(Player.class);

            for(final Player player : players) {

                adapter[index++] = constructor.newInstance(player);
            }
        }
        catch (Exception e) {

            throw new MoonLakeException("The adapter player to moonlake player exception.", e);
        }
        return adapter;
    }

    /**
     * 将 Bukkit 玩家对象转换到 MoonLake 玩家对象
     *
     * @param player Bukkit 玩家
     * @return MoonLake 玩家
     * @throws IllegalArgumentException 如果 Bukkit 玩家对象为 {@code null} 则抛出异常
     */
    public static MoonLakePlayer adapter(Player player) {

        Validate.notNull(player, "The player object is null.");

        return adapter(new Player[] { player })[0];
    }

    /**
     * 将 MoonLake 玩家对象转换到 Bukkit 玩家对象
     *
     * @param players MoonLake 玩家
     * @return Bukkit 玩家
     * @throws IllegalArgumentException 如果 MoonLake 玩家对象为 {@code null} 则抛出异常
     */
    public static Player[] adapter(MoonLakePlayer... players) {

        Validate.notNull(players, "The player object is null.");

        int index = 0;
        Player[] adapter = new Player[players.length];

        for(final MoonLakePlayer player : players) {

            adapter[index++] = player.getBukkitPlayer();
        }
        return adapter;
    }

    /**
     * 将 MoonLake 玩家对象转换到 Bukkit 玩家对象
     *
     * @param player MoonLake 玩家
     * @return Bukkit 玩家
     * @throws IllegalArgumentException 如果 MoonLake 玩家对象为 {@code null} 则抛出异常
     */
    public static Player adapter(MoonLakePlayer player) {

        Validate.notNull(player, "The player object is null.");

        return adapter(new MoonLakePlayer[] { player })[0];
    }

    /**
     * 获取指定玩家的游戏简介
     *
     * @param player 玩家
     * @return 游戏简介 异常则返回 null
     * @throws IllegalArgumentException 如果玩家对象为 {@code null} 则抛出异常
     */
    public static GameProfile getProfile(Player player) {

        Validate.notNull(player, "The player object is null.");

        try {

            Object nmsPlayer = METHOD_GETHANDLE.invoke(player);

            return (GameProfile) METHOD_GETPROFILE.invoke(nmsPlayer);
        }
        catch (Exception e) {

            throw new MoonLakeException("The get player profile exception.", e);
        }
    }

    /**
     * 获取指定玩家的本地化语言
     *
     * @param player 玩家
     * @return 本地化语言 异常则返回 null
     * @throws IllegalArgumentException 如果玩家对象为 {@code null} 则抛出异常
     */
    public static String getLanguage(Player player) {

        Validate.notNull(player, "The player object is null.");

        try {

            Object nmsPlayer = METHOD_GETHANDLE.invoke(player);

            return (String) FIELD_LOCALE.get(nmsPlayer);
        }
        catch (Exception e) {

            throw new MoonLakeException("The get player language exception.", e);
        }
    }

    /**
     * 安全的更新指定玩家的物品栏
     *
     * @param player 玩家
     */
    public static void updateInventorySafe(Plugin plugin, final Player player) {

        Validate.notNull(plugin, "The plugin object is null");
        Validate.notNull(player, "The player object is null.");

        if(Reflect.getServerVersionNumber() <= 8) {

            MoonLakeAPI.runTaskLaterAsync(plugin, new Runnable() {

                @Override
                public void run() {

                    player.updateInventory();
                }
            }, 1L);
        }
        else {

            player.updateInventory();
        }
    }
}