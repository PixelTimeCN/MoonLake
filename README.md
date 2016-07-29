# MoonLake
Minecraft MoonLake Core API Plugin
By Month_Light
## 简介
这个插件提供了大量的 API 功能，实现了一些 Bukkit 无法<br />
做到的 NMS 功能，例如数据包封装、物品栈操作类、玩家操作类。
## 功能
* 玩家 `Player` 操作
* 数据库 `MySQL` 操作
* 管理类 `Manager` 操作
* 加密类 `Encrypt` 操作
* 物品栈 `ItemStack` 操作
* 粒子效果 `Particle` 操作
* Java 反射 `Reflect` 操作
* 数据包 `Packet` 以及 `NMS` 操作
* 提供玩家 `MoonLakePlayer` 接口

## 未来计划
* 这是我们未来即将开发的 API 功能
* 1. 更多数据包的封装
* 2. 玩家的网络操作类封装
* 3. 修改玩家的皮肤和披风的封装 :point_right:[GO](http://github.com/u2g/MoonLakeSkinme "MoonLake Skinme Plugin")

## 使用方法
注意将您的插件内 `plugin.yml` 添加 `depend: [MoonLake]` 前置支持
```java
private MoonLake moonlake;

/**
 * 加载月色之湖前置核心 API 插件
 *
 * @return 是否加载成功
 */
private boolean setupMoonLake() {
  
  Plugin plugin = this.getServer().getPluginManager().getPlugin("MoonLake");
  return plugin != null && plugin instanceof MoonLakePlugin && (this.moonLake = ((MoonLakePlugin)plugin).getInstance()) != null;
}
```
调用的话就在主类的 `onEnable` 函数里面
```java
@Override
public void onEnable() {
  
  if(!setupMoonLake()) {
    // 前置插件 MoonLake 加载失败
    return;
  }
  // 前置插件 MoonLake 加载成功
}
```
数据库的简单 demo 测试
```java
// 获取 MySQL 连接对象: 地址、端口、数据库、用户名、用户密码, [编码] (默认 utf-8)
MySQLConnection mysql = MySQLManager.getConnection("127.0.0.1", 3306, "mydb", "myuser", "mypwd");
MySQLDatabase database = mysql.getDatabase();
MySQLTable myTable = database.getTable("myTable");

// 获取结果集
MySQLResultSet resultSet = myTable.querySelect().where("name", "yourname").execute();

// 判断是否拥有此数据
if(!resultSet.next()) {
  // 没有数据则插入新的数据
  myTable.queryInsert().field("name").value("yourname").execute();
}

// 更新数据
myTable.queryUpdate().set("name", "newname").where("name", "yourname").execute();
// 删除数据
myTable.queryDelete().where("name", "newname").execute();

// 关闭结果集
resultSet.close();
// 关闭连接对象
mysql.close();
```
## 其他插件
* `MoonLakeKitPvP` 职业战争插件 :point_right:[GO](http://github.com/u2g/MoonLakeKitPvP "MoonLake KitPvP Plugin")
* `MoonLakeSkinme` 玩家皮肤披风操作插件 :point_right:[GO](http://github.com/u2g/MoonLakeSkinme "MoonLake Skinme Plugin")
* `MoonLakeEconomy` 基于 `MySQL` 的经济插件 :point_right:[GO](http://github.com/u2g/MoonLakeEconomy "MoonLake Economy Plugin")

Website: [MoonLake](http://www.mcyszh.com "MoonLake Website")<br />
Minecraft MoonLake Core API Plugin
By Month_Light
