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
 
 
package com.minecraft.moonlake;

import com.minecraft.moonlake.api.MoonLake;
import com.minecraft.moonlake.api.packet.listener.PacketListenerFactory;
import com.minecraft.moonlake.exception.MoonLakeException;
import com.minecraft.moonlake.logger.MLogger;
import com.minecraft.moonlake.logger.MLoggerWrapped;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * <hr />
 * <div>
 *     <h1>Minecraft MoonLake Core API Plugin</h1>
 *     <p>By Month_Light Ver: 1.8-a3</p>
 *     <p>Website: <a href="http://www.mcyszh.com" target="_blank" style="text-decoration: none;">MoonLake Website</a></p>
 *     <p>QQ Group: 377607025 -> <a href="http://jq.qq.com/?_wv=1027&k=2IfPFrH" target="_blank">Jump</a></p>
 *     <hr />
 *     <div>
 *         <h2>目前插件支持的服务端版本有:</h2>
 *         <ul>
 *             <li>Bukkit [1.8.x - 1.10.x] <span style="color: red">✔</span> 不完全支持</li>
 *             <li>Spigot [1.8.x - 1.10.x] <span style="color: rgb(85, 255, 85)">✔</span> 完完全全支持</li>
 *             <li>PaperSpigot [1.8.x - 1.10.x] <span style="color: rgb(85, 255, 85)"><s>✔</s></span> 部分功能不支持</li>
 *             <li>Cauldron | KCauldron [模组服务端] <span style="color: red">✘</span> 完全不支持</li>
 *         </ul>
 *     </div>
 * </div>
 * <hr />
 * <div>
 *     <h1>目前已经实现的功能有:</h1>
 *     <ul>
 *          <li>玩家支持库 {@link com.minecraft.moonlake.api.player.PlayerLibrary}</li>
 *          <li>物品栈支持库 {@link com.minecraft.moonlake.api.item.ItemLibrary}</li>
 *          <li>数据库支持库 {@link com.minecraft.moonlake.mysql.MySQLConnection}</li>
 *          <li>花式消息支持库 {@link com.minecraft.moonlake.api.fancy.FancyMessage}</li>
 *          <li>NMS 数据包发送 {@link com.minecraft.moonlake.nms.packet.Packet}</li>
 *          <li>NBT 操作支持库 {@link com.minecraft.moonlake.api.nbt.NBTLibrary}</li>
 *          <li>插件注解支持库 {@link com.minecraft.moonlake.api.annotation.plugin.PluginAnnotation}</li>
 *          <li>数据包通道监听器 {@link com.minecraft.moonlake.api.packet.listener.PacketListener}</li>
 *      </ul>
 *      <p>更多功能开发中 _(:з」∠)_</p>
 * </div>
 * <hr />
 * <div>
 *     <h1>此项目完全属于开源项目，如需修改和添加功能请到 <a href="https://github.com/u2g/MoonLake" target="_blank">GitHub</a> 进行 Fork 操作.</h1>
 *     <h1>修改操作请您遵守 <a href="https://github.com/u2g/MoonLake/blob/master/LICENSE" target="_blank">GPLv3</a> 协议，您必须公开修改过的所有代码！</h1>
 * </div>
 *
 * @version 1.8-a3
 * @author Month_Light
 */
public class MoonLakePlugin extends JavaPlugin implements MoonLake {

    private final MLogger mLogger;
    private final PluginDescriptionFile description;

    private static MoonLake MAIN;

    /**
     * 月色之湖插件类构造函数
     */
    public MoonLakePlugin() {

        this.description = getDescription();
        this.mLogger = new MLoggerWrapped("MoonLake");
    }

    @Override
    public void onEnable() {

        MAIN = this;

        try {

            Class.forName(PacketListenerFactory.class.getName());

            this.getMLogger().info("月色之湖数据包通道监听器(PCL)成功加载.");
        }
        catch (Exception e) {

            throw new MoonLakeException("The initialize packet channel listener exception.", e);
        }
        this.getMLogger().info("月色之湖核心 API 插件 v" + getPluginVersion() + " 成功加载.");
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public MoonLake getInstance() {

        return MAIN;
    }

    @Override
    public MoonLake getMoonLake() {

        return MAIN;
    }

    @Deprecated
    public static MoonLake getInstances() {

        return MAIN;
    }

    @Override
    public MLogger getMLogger() {

        return mLogger;
    }

    @Override
    public MoonLakePlugin getPlugin() {

        return this;
    }

    @Override
    public String getPluginPrefix() {

        return description.getPrefix();
    }

    @Override
    public String getPluginName() {

        return description.getName();
    }

    @Override
    public String getPluginMain() {

        return description.getMain();
    }

    @Override
    public String getPluginVersion() {

        return description.getVersion();
    }

    @Override
    public String getPluginWebsite() {

        return description.getWebsite();
    }

    @Override
    public String getPluginDescription() {

        return description.getDescription();
    }

    @Override
    public Set<String> getPluginAuthors() {

        return new HashSet<>(description.getAuthors());
    }

    @Override
    public Set<String> getPluginDepends() {

        return new HashSet<>(description.getDepend());
    }

    @Override
    public Set<String> getPluginSoftDepends() {

        return new HashSet<>(description.getSoftDepend());
    }

    @Override
    public String getBukkitVersion() {

        String packageName = getServer().getClass().getPackage().getName();
        String[] packageSplit = packageName.split("\\.");
        return packageSplit[packageSplit.length - 1];
    }

    @Override
    public int getReleaseNumber() {

        String version = getBukkitVersion();
        String[] versionSplit = version.split("_");
        String releaseVersion = versionSplit[1];
        return Integer.parseInt(releaseVersion);
    }
}
