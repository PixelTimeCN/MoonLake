package com.minecraft.moonlake.api;

/**
 * Created by MoonLake on 2016/5/2.
 * @version 1.0
 * @author Month_Light
 */
public interface BukkitInfo {

    /**
     * 获取 Bukkit 服务器的版本
     *
     * @return 版本
     */
    String getBukkitVersion();

    /**
     * 获取 Bukkit 服务器的版本号
     *
     * @return 版本号
     */
    int getReleaseNumber();
}