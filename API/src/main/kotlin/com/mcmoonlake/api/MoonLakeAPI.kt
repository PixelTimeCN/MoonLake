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

@file:JvmName("MoonLakeAPI")

package com.mcmoonlake.api

import com.mcmoonlake.api.anvil.AnvilWindow
import com.mcmoonlake.api.anvil.AnvilWindows
import com.mcmoonlake.api.chat.ChatAction
import com.mcmoonlake.api.chat.ChatComponent
import com.mcmoonlake.api.chat.ChatSerializer
import com.mcmoonlake.api.depend.DependPlugin
import com.mcmoonlake.api.depend.DependPluginException
import com.mcmoonlake.api.depend.DependPlugins
import com.mcmoonlake.api.event.MoonLakeEvent
import com.mcmoonlake.api.event.MoonLakeListener
import com.mcmoonlake.api.exception.MoonLakeException
import com.mcmoonlake.api.funs.Function
import com.mcmoonlake.api.item.ItemBuilder
import com.mcmoonlake.api.nbt.NBTCompound
import com.mcmoonlake.api.nbt.NBTFactory
import com.mcmoonlake.api.nbt.NBTList
import com.mcmoonlake.api.packet.PacketOutChat
import com.mcmoonlake.api.packet.PacketOutTitle
import com.mcmoonlake.api.player.MoonLakePlayer
import com.mcmoonlake.api.player.MoonLakePlayerCached
import com.mcmoonlake.api.region.*
import com.mcmoonlake.api.task.MoonLakeRunnable
import com.mcmoonlake.api.util.Enums
import com.mcmoonlake.api.version.MinecraftBukkitVersion
import com.mcmoonlake.api.version.MinecraftVersion
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.ServicesManager
import org.bukkit.plugin.messaging.Messenger
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import java.io.Closeable
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/** MoonLake API Extended Function */

private var moonlake: MoonLake? = null

@Throws(MoonLakeException::class)
fun setMoonLake(obj: MoonLake) {
    if(moonlake != null)
        throw MoonLakeException("无法再次设置 MoonLakeAPI 的内部实例.")
    moonlake = obj
}

fun getMoonLake(): MoonLake
        = moonlake.notNull()

/** extended function */

fun <T> T?.notNull(message: String = "验证的对象值为 null 时异常."): T
        = this ?: throw IllegalArgumentException(message)

@Throws(IOException::class)
fun Closeable?.ioClose(swallow: Boolean) {
    if(this == null)
        return
    try {
        close()
    } catch(e: IOException) {
        if(!swallow)
            throw e
    }
}

fun <T, C: Comparable<T>> C.isLater(other: T): Boolean
        = compareTo(other) > 0

fun <T, C: Comparable<T>> C.isOrLater(other: T): Boolean
        = compareTo(other) >= 0

fun <T, C: Comparable<T>> C.isRange(min: T, max: T): Boolean
        = compareTo(min) > 0 && compareTo(max) < 0

fun <T, C: Comparable<T>> C.isOrRange(min: T, max: T): Boolean
        = compareTo(min) >= 0 && compareTo(max) <= 0

inline fun <V, reified T> ofValuable(value: V?, def: T? = null): T? where T: Enum<T>, T: Valuable<V>
        = Enums.ofValuable(T::class.java, value, def)

@Throws(IllegalArgumentException::class)
inline fun <V, reified T> ofValuableNotNull(value: V?): T where T: Enum<T>, T: Valuable<V>
        = ofValuable(value) ?: throw IllegalArgumentException("未知的枚举 ${T::class.java.canonicalName} 类型值: $value")

/** version function */

fun currentMCVersion(): MinecraftVersion
        = MinecraftVersion.currentVersion()

fun currentBukkitVersion(): MinecraftBukkitVersion
        = MinecraftBukkitVersion.currentVersion()

/** util function */

private val combatOrLaterVer = currentBukkitVersion().isOrLater(MinecraftBukkitVersion.V1_9_R1)
private val frostburnOrLaterVer = currentBukkitVersion().isOrLater(MinecraftBukkitVersion.V1_10_R1)
private val explorationOrLaterVer = currentBukkitVersion().isOrLater(MinecraftBukkitVersion.V1_11_R1)
private val colorWorldOrLaterVer = currentBukkitVersion().isOrLater(MinecraftBukkitVersion.V1_12_R1)
private val javaEditionOrLaterVer = currentMCVersion().isOrLater(MinecraftVersion(1, 12, 2))

/**
 * Returns if the server version is 1.9 or later.
 */
val isCombatOrLaterVer: Boolean
    get() = combatOrLaterVer

/**
 * Returns if the server version is 1.10 or later.
 */
val isFrostburnOrLaterVer: Boolean
    get() = frostburnOrLaterVer

/**
 * Returns if the server version is 1.11 or later.
 */
val isExplorationOrLaterVer: Boolean
    get() = explorationOrLaterVer

/**
 * Returns if the server version is 1.12 or later.
 */
val isColorWorldOrLaterVer: Boolean
    get() = colorWorldOrLaterVer

/**
 * Returns if the server version is 1.12.2 or later.
 */
val isJavaEditionOrLaterVer: Boolean
    get() = javaEditionOrLaterVer

private val spigotServer: Boolean by lazy {
    try {
        Class.forName("org.spigotmc.SpigotConfig")
        true
    } catch(e: Exception) {
        false
    }
}

/**
 * Returns true if the server is spigot.
 */
val isSpigotServer: Boolean
    get() = spigotServer

fun String.toColor(): String
        = com.mcmoonlake.api.chat.ChatColor.translateAlternateColorCodes('&', this)

fun String.toColor(altColorChar: Char): String
        = com.mcmoonlake.api.chat.ChatColor.translateAlternateColorCodes(altColorChar, this)

fun Array<out String>.toColor(): Array<out String>
        = toList().map { it -> it.toColor() }.toTypedArray()

fun Array<out String>.toColor(altColorChar: Char): Array<out String>
        = toList().map { it -> it.toColor(altColorChar) }.toTypedArray()

fun Iterable<String>.toColor(): List<String>
        = map { it -> it.toColor() }.let { ArrayList(it) }

fun Iterable<String>.toColor(altColorChar: Char): List<String>
        = map { it -> it.toColor(altColorChar) }

fun String.stripColor(): String
        = com.mcmoonlake.api.chat.ChatColor.stripColor(this)

fun Array<out String>.stripColor(): Array<out String>
        = toList().map { it -> it.stripColor() }.toTypedArray()

fun Iterable<String>.stripColor(): List<String>
        = map { it -> it.stripColor() }

fun String.messageFormat(vararg args: Any?): String
        = MessageFormat.format(this, args)

@Throws(MoonLakeException::class)
fun Throwable.throwMoonLake(): Nothing = when(this is MoonLakeException) {
    true -> throw this
    else -> throw MoonLakeException(this)
}

@Throws(Throwable::class)
inline fun <T: Throwable, reified R: Throwable> T.throwGiven(block: (T) -> R): Nothing
        = when(R::class.java.isInstance(this)) {
    true -> throw this
    else -> throw block(this)
}

/**
 * Returns true if this is null or true
 */
fun Boolean?.orTrue(): Boolean
        = this == null || this == true

/**
 * Returns false if this is null or false
 */
fun Boolean?.orFalse(): Boolean
        = !(this == null || this == false)

inline fun <reified T> T.consumer(block: (T) -> Unit)
        = block(this)

fun String.isInteger(): Boolean = when(isNullOrEmpty()) {
    true -> false
    else -> try {
        toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun String.isDouble(): Boolean = when(isNullOrEmpty()) {
    true -> false
    else -> try {
        toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun Any.parseInt(def: Int = 0): Int = when(this is Number) {
    true -> (this as Number).toInt()
    else -> try {
        toString().toInt()
    } catch (e: NumberFormatException) {
        def
    }
}

fun Any.parseLong(def: Long = 0L): Long = when(this is Number) {
    true -> (this as Number).toLong()
    else -> try {
        toString().toLong()
    } catch (e: NumberFormatException) {
        def
    }
}

fun Any.parseFloat(def: Float = .0f): Float = when(this is Number) {
    true -> (this as Number).toFloat()
    else -> try {
        toString().toFloat()
    } catch (e: NumberFormatException) {
        def
    }
}

fun Any.parseDouble(def: Double = .0): Double = when(this is Number) {
    true -> (this as Number).toDouble()
    else -> try {
        toString().toDouble()
    } catch (e: NumberFormatException) {
        def
    }
}

fun Any.parseBoolean(def: Boolean = false): Boolean = when(this is Boolean) {
    true -> this as Boolean
    else -> try {
        toString().toBoolean()
    } catch (e: Exception) {
        def
    }
}

@Throws(MoonLakeException::class)
fun <T: ConfigurationSerializable> Class<T>.deserialize(configuration: Configuration, key: String, def: T? = null): T? = configuration.get(key).let {
    when(it == null) {
        true -> def
        else -> when {
            isInstance(it) -> cast(it)
            it is Map<*, *> -> try {
                var method: Method? = getDeclaredMethod("deserialize", Map::class.java)
                if(method == null) method = getDeclaredMethod("valueOf", Map::class.java)
                if(method == null || !Modifier.isStatic(method.modifiers)) throw MoonLakeException("值为 Map 实例, 但是序列化类不存在 'deserialize' 或 'valueOf' 静态函数.")
                @Suppress("UNCHECKED_CAST")
                method.invoke(null, it) as T
            } catch (e: Exception) {
                e.throwMoonLake()
            }
            else -> def
        }
    }
}

@Throws(MoonLakeException::class)
fun <T: ConfigurationSerializable> Configuration.deserialize(clazz: Class<T>, key: String, def: T? = null): T?
        = clazz.deserialize(this, key, def)

fun getOnlinePlayers(): Collection<Player>
        = Bukkit.getOnlinePlayers()

fun createInventory(holder: InventoryHolder?, type: InventoryType): Inventory
        = Bukkit.createInventory(holder, type)

fun createInventory(holder: InventoryHolder?, type: InventoryType, title: String): Inventory
        = Bukkit.createInventory(holder, type, title)

fun createInventory(holder: InventoryHolder?, size: Int): Inventory
        = Bukkit.createInventory(holder, size)

fun createInventory(holder: InventoryHolder?, size: Int, title: String): Inventory
        = Bukkit.createInventory(holder, size, title)

fun getScoreboardManager(): ScoreboardManager
        = Bukkit.getScoreboardManager()

fun getScoreboardMain(): Scoreboard
        = Bukkit.getScoreboardManager().mainScoreboard

fun getScoreboardNew(): Scoreboard
        = Bukkit.getScoreboardManager().newScoreboard

fun getMessenger(): Messenger
        = Bukkit.getMessenger()

fun getServicesManager(): ServicesManager
        = Bukkit.getServicesManager()

fun getPluginManager(): PluginManager
        = Bukkit.getPluginManager()

fun getPlugin(name: String): Plugin?
        = Bukkit.getPluginManager().getPlugin(name)

fun getScheduler(): BukkitScheduler
        = Bukkit.getScheduler()

fun getPluginCommand(name: String): PluginCommand
        = Bukkit.getPluginCommand(name)

fun dispatchCommand(sender: CommandSender, command: String): Boolean
        = Bukkit.dispatchCommand(sender, command)

fun dispatchConsoleCmd(command: String): Boolean
        = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)

/** converter function */

fun UUID.toBukkitWorld(): World?
        = Bukkit.getWorld(this)

fun String.toBukkitWorld(): World?
        = Bukkit.getWorld(this)

fun UUID.toEntity(): Entity?
        = Bukkit.getEntity(this)

fun UUID.toPlayer(): Player?
        = Bukkit.getPlayer(this)

fun String.toPlayer(): Player?
        = Bukkit.getPlayer(this)

fun String.toPlayerExact(): Player?
        = Bukkit.getPlayerExact(this)

fun Player.toMoonLakePlayer(): MoonLakePlayer
        = MoonLakePlayerCached.of(this)

fun UUID.toMoonLakePlayer(): MoonLakePlayer?
        = toPlayer()?.toMoonLakePlayer()

fun String.toMoonLakePlayer(): MoonLakePlayer?
        = toPlayer()?.toMoonLakePlayer()

fun Location.toRegionVector(): RegionVector
        = RegionVector(x, y, z)

fun Location.toRegionVectorBlock(): RegionVectorBlock
        = RegionVectorBlock(x, y, z)

fun Location.toRegionVector2D(): RegionVector2D
        = RegionVector2D(x, z)

fun <T, R> ((T) -> R).toFunction(): Function<T, R> = object: Function<T, R> {
    override fun apply(param: T): R
            = this@toFunction(param)
}

fun <T> (() -> T).toCallable(): Callable<T>
        = Callable { this@toCallable() }

/** event function */

fun Event.callEvent()
        = Bukkit.getServer().pluginManager.callEvent(this)

fun Event.callEventAsync(plugin: Plugin)
        = plugin.runTaskAsync(Runnable { Bukkit.getServer().pluginManager.callEvent(this) })

fun MoonLakeEvent.callEvent()
        = Bukkit.getServer().pluginManager.callEvent(this)

fun MoonLakeEvent.callEventAsync(plugin: Plugin)
        = plugin.runTaskAsync(Runnable { Bukkit.getServer().pluginManager.callEvent(this) })

fun Listener.registerEvent(plugin: Plugin)
        = Bukkit.getServer().pluginManager.registerEvents(this, plugin)

fun MoonLakeListener.registerEvent(plugin: Plugin)
        = Bukkit.getServer().pluginManager.registerEvents(this, plugin)

fun <T: Event> Class<out T>.registerEvent(listener: Listener, priority: EventPriority, executor: EventExecutor, plugin: Plugin, ignoreCancelled: Boolean = false)
        = Bukkit.getServer().pluginManager.registerEvent(this, listener, priority, executor, plugin, ignoreCancelled)

fun <T: MoonLakeEvent> Class<out T>.registerEvent(listener: MoonLakeListener, priority: EventPriority, executor: EventExecutor, plugin: Plugin, ignoreCancelled: Boolean = false)
        = Bukkit.getServer().pluginManager.registerEvent(this, listener, priority, executor, plugin, ignoreCancelled)

fun unregisterAll()
        = HandlerList.unregisterAll()

fun Plugin.unregisterAll()
        = HandlerList.unregisterAll(this)

fun Listener.unregisterAll()
        = HandlerList.unregisterAll(this)

fun MoonLakeListener.unregisterAll()
        = HandlerList.unregisterAll(this)

/** task function */

fun Plugin.runTask(task: Runnable): BukkitTask
        = Bukkit.getScheduler().runTask(this, task)

fun Plugin.runTaskLater(task: Runnable, delay: Long): BukkitTask
        = Bukkit.getScheduler().runTaskLater(this, task, delay)

fun Plugin.runTaskTimer(task: Runnable, delay: Long, period: Long): BukkitTask
        = Bukkit.getScheduler().runTaskTimer(this, task, delay, period)

fun Plugin.runTaskAsync(task: Runnable): BukkitTask
        = Bukkit.getScheduler().runTaskAsynchronously(this, task)

fun Plugin.runTaskLaterAsync(task: Runnable, delay: Long): BukkitTask
        = Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay)

fun Plugin.runTaskTimerAsync(task: Runnable, delay: Long, period: Long): BukkitTask
        = Bukkit.getScheduler().runTaskTimerAsynchronously(this, task, delay, period)

fun Plugin.runTask(task: MoonLakeRunnable): BukkitTask
        = task.runTask(this)

fun Plugin.runTaskLater(task: MoonLakeRunnable, delay: Long): BukkitTask
        = task.runTaskLater(this, delay)

fun Plugin.runTaskTimer(task: MoonLakeRunnable, delay: Long, period: Long): BukkitTask
        = task.runTaskTimer(this, delay, period)

fun Plugin.runTaskAsync(task: MoonLakeRunnable): BukkitTask
        = task.runTaskAsynchronously(this)

fun Plugin.runTaskLaterAsync(task: MoonLakeRunnable, delay: Long): BukkitTask
        = task.runTaskLaterAsynchronously(this, delay)

fun Plugin.runTaskTimerAsync(task: MoonLakeRunnable, delay: Long, period: Long): BukkitTask
        = task.runTaskTimerAsynchronously(this, delay, period)

fun Plugin.runTask(task: () -> Unit): BukkitTask
        = Bukkit.getScheduler().runTask(this, task)

fun Plugin.runTaskLater(task: () -> Unit, delay: Long): BukkitTask
        = Bukkit.getScheduler().runTaskLater(this, task, delay)

fun Plugin.runTaskTimer(task: () -> Unit, delay: Long, period: Long): BukkitTask
        = Bukkit.getScheduler().runTaskTimer(this, task, delay, period)

fun Plugin.runTaskAsync(task: () -> Unit): BukkitTask
        = Bukkit.getScheduler().runTaskAsynchronously(this, task)

fun Plugin.runTaskLaterAsync(task: () -> Unit, delay: Long): BukkitTask
        = Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay)

fun Plugin.runTaskTimerAsync(task: () -> Unit, delay: Long, period: Long): BukkitTask
        = Bukkit.getScheduler().runTaskTimerAsynchronously(this, task, delay, period)

fun <T> Plugin.callTaskSyncMethod(callback: Callable<T>): Future<T>
        = Bukkit.getScheduler().callSyncMethod(this, callback)

fun <T> Plugin.callTaskSyncMethod(callback: () -> T): Future<T>
        = Bukkit.getScheduler().callSyncMethod(this, callback.toCallable())

fun <T> Plugin.callTaskSyncFuture(callback: Callable<T>): CompletableFuture<T>
        = callTaskFuture0(callback)

fun <T> Plugin.callTaskSyncFuture(callback: () -> T): CompletableFuture<T>
        = callTaskFuture0(callback.toCallable())

fun <T> Plugin.callTaskAsyncFuture(callback: Callable<T>): CompletableFuture<T>
        = callTaskFuture0(callback, -1, true)

fun <T> Plugin.callTaskAsyncFuture(callback: () -> T): CompletableFuture<T>
        = callTaskFuture0(callback.toCallable(), -1, true)

fun <T> Plugin.callTaskLaterSyncFuture(delay: Long, callback: Callable<T>): CompletableFuture<T>
        = callTaskFuture0(callback, delay, false)

fun <T> Plugin.callTaskLaterSyncFuture(delay: Long, callback: () -> T): CompletableFuture<T>
        = callTaskFuture0(callback.toCallable(), delay, false)

fun <T> Plugin.callTaskLaterAsyncFuture(delay: Long, callback: Callable<T>): CompletableFuture<T>
        = callTaskFuture0(callback, delay, true)

fun <T> Plugin.callTaskLaterAsyncFuture(delay: Long, callback: () -> T): CompletableFuture<T>
        = callTaskFuture0(callback.toCallable(), delay, true)

private fun <T> Plugin.callTaskFuture0(callback: Callable<T>, delay: Long = -1, async: Boolean = false): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    val futureTask = FutureTask(callback)
    val runnable = Runnable {
        try {
            futureTask.run()
            future.complete(futureTask.get())
        } catch(e: Exception) {
            future.completeExceptionally(e)
        }
    }
    when(delay <= 0) {
        true -> {
            if(async) runTaskAsync(runnable)
            else runnable.run() // Blocking of synchronization futures
        }
        else -> {
            if(async) runTaskLaterAsync(runnable, delay)
            else runTaskLater(runnable, delay)
        }
    }
    return future
}

fun cancelTask(task: BukkitTask?)
        = task?.cancel()

fun cancelTask(taskId: Int)
        = Bukkit.getScheduler().cancelTask(taskId)

fun Plugin.cancelTasks()
        = Bukkit.getScheduler().cancelTasks(this)

fun cancelAllTasks()
        = Bukkit.getScheduler().cancelAllTasks()

/** target function */

fun Entity.isInFront(target: Entity): Boolean {
    val facing = location.direction
    val relative = target.location.subtract(location).toVector().normalize()
    return facing.dot(relative) >= .0
}

fun Entity.isInFront(target: Entity, angle: Double): Boolean = angle.let {
    if(it <= .0) return false
    if(it >= 360.0) return true
    val dotTarget = Math.cos(it)
    val facing = location.direction
    val relative = target.location.subtract(location).toVector().normalize()
    return facing.dot(relative) >= dotTarget
}

fun Entity.isBehind(target: Entity, angle: Double): Boolean
        = !isInFront(target, angle)

fun <T: LivingEntity> LivingEntity.getLivingTargets(clazz: Class<T>, range: Double, tolerance: Double = 4.0): List<T> {
    val entityList = getNearbyEntities(range, range, range)
    val facing = location.direction
    val fLengthSq = facing.lengthSquared()
    return entityList.filter { clazz.isInstance(it) && isInFront(it) }.map { clazz.cast(it) }.filter {
        val  relative = it.location.subtract(location).toVector()
        val dot = relative.dot(facing)
        val rLengthSq = relative.lengthSquared()
        val cosSquared = dot * dot / (rLengthSq * fLengthSq)
        val sinSquared = 1.0 - cosSquared
        val dSquared = rLengthSq * sinSquared
        dSquared < tolerance
    }.toList()
}

fun LivingEntity.getLivingTargets(range: Double, tolerance: Double = 4.0): List<LivingEntity>
        = getLivingTargets(LivingEntity::class.java, range, tolerance)

fun <T: LivingEntity> LivingEntity.getLivingTarget(clazz: Class<T>, range: Double, tolerance: Double = 4.0): T? {
    val targets = getLivingTargets(clazz, range, tolerance)
    if(targets.isEmpty()) return null
    var target = targets.first()
    var minDistance = target.location.distanceSquared(location)
    targets.forEach {
        val distance = it.location.distanceSquared(location)
        if(distance < minDistance) {
            minDistance = distance
            target = it
        }
    }
    return target
}

fun LivingEntity.getLivingTarget(range: Double, tolerance: Double = 4.0): LivingEntity?
        = getLivingTarget(LivingEntity::class.java, range, tolerance)

fun <T: LivingEntity> MoonLakePlayer.getLivingTargets(clazz: Class<T>, range: Double, tolerance: Double = 4.0): List<T>
        = bukkitPlayer.getLivingTargets(clazz, range, tolerance)

fun MoonLakePlayer.getLivingTargets(range: Double, tolerance: Double = 4.0): List<LivingEntity>
        = bukkitPlayer.getLivingTargets(range, tolerance)

fun <T: LivingEntity> MoonLakePlayer.getLivingTarget(clazz: Class<T>, range: Double, tolerance: Double = 4.0): T?
        = bukkitPlayer.getLivingTarget(clazz, range, tolerance)

fun MoonLakePlayer.getLivingTarget(range: Double, tolerance: Double = 4.0): LivingEntity?
        = bukkitPlayer.getLivingTarget(range, tolerance)

/** region function */

fun World.createCuboidRegion(pos1: Location, pos2: Location): RegionCuboid
        = RegionCuboid(this, pos1.toRegionVector(), pos2.toRegionVector())

fun World.createCuboidRegion(pos1: RegionVector, pos2: RegionVector): RegionCuboid
        = RegionCuboid(this, pos1, pos2)

fun Region.createWorldBorder(): WorldBorder {
    val worldBorder = world.worldBorder
    worldBorder.setSize(length.toDouble(), 0L)
    worldBorder.center = center.toLocation(world)
    return worldBorder
}

/** anvil window function */

fun Plugin.newAnvilWindow(): AnvilWindow
        = AnvilWindows.create(this)

/** item builder function */

fun ItemStack.newItemBuilder(): ItemBuilder
        = ItemBuilder.of(this)

fun Material.newItemBuilder(amount: Int = 1, durability: Int = 0): ItemBuilder
        = ItemBuilder.of(this, amount, durability)

fun Material.newItemStack(amount: Int = 1, durability: Int = 0): ItemStack
        = ItemStack(this, amount, durability.toShort())

fun ItemStack?.isAir(): Boolean
        = this == null || this.type == Material.AIR

fun ItemStack?.isEmpty(): Boolean
        = this == null || this.type == Material.AIR || !hasItemMeta()

fun ItemStack.givePlayer(player: Player): Boolean
        = player.inventory.addItem(this).isEmpty() // the result is empty, indicating the success

fun ItemStack.givePlayer(player: MoonLakePlayer): Boolean
        = givePlayer(player.bukkitPlayer)

fun ItemStack.dropLocation(location: Location): Item
        = location.world.dropItemNaturally(location, this)

/** nbt function */

fun newNBTCompound(name: String = ""): NBTCompound
        = NBTFactory.ofCompound(name)

fun <T> newNBTList(name: String = ""): NBTList<T>
        = NBTFactory.ofList(name)

fun Material.newItemStack(amount: Int = 1, durability: Int = 0, tag: NBTCompound? = null): ItemStack
        = NBTFactory.createStack(this, amount, durability, tag)

inline fun ItemStack.readTag(consumer: (tag: NBTCompound?) -> Unit): ItemStack
        { consumer(NBTFactory.readStackTag(this)); return this; }

inline fun <R> ItemStack.readTagLet(consumer: (tag: NBTCompound?) -> R): R
        = consumer(NBTFactory.readStackTag(this))

inline fun ItemStack.readTagSafe(consumer: (tag: NBTCompound) -> Unit): ItemStack
        { consumer(NBTFactory.readStackTagSafe(this)); return this; }

inline fun <R> ItemStack.readTagSafeLet(consumer: (tag: NBTCompound) -> R): R
        = consumer(NBTFactory.readStackTagSafe(this))

fun ItemStack.writeTag(tag: NBTCompound?): ItemStack
        = NBTFactory.writeStackTag(this, tag)

inline fun <T: Entity> T.readTag(consumer: (tag: NBTCompound) -> Unit): T
        { consumer(NBTFactory.readEntityTag(this)); return this; }

inline fun <T: Entity, R> T.readTagLet(consumer: (tag: NBTCompound) -> R): R
        = consumer(NBTFactory.readEntityTag(this))

fun <T: Entity> T.writeTag(tag: NBTCompound): T
        = NBTFactory.writeEntityTag(this, tag)

/** depend plugin function */

@Throws(DependPluginException::class)
inline fun <T: DependPlugin> Class<T>.useDepend(consumer: (depend: T) -> Unit): T
        = DependPlugins.of(this).also(consumer)

@Throws(DependPluginException::class)
inline fun <T: DependPlugin, R> Class<T>.useDependLet(consumer: (depend: T) -> R): R
        = consumer(DependPlugins.of(this))

inline fun <T: DependPlugin> Class<T>.useDependSafe(consumer: (depend: T?) -> Unit): T?
        = DependPlugins.ofSafe(this).also(consumer)

inline fun <T: DependPlugin, R> Class<T>.useDependSafeLet(consumer: (depend: T?) -> R): R
        = consumer(DependPlugins.ofSafe(this))

/** entity function */

fun <T: Entity> Class<T>.spawn(location: Location): T
        = location.world.spawn(location, this)

inline fun <T: Entity> Class<T>.spawn(location: Location, consumer: (entity: T) -> Unit): T
        = spawn(location).also(consumer)

inline fun <T: Entity, R> Class<T>.spawnLet(location: Location, consumer: (entity: T) -> R): R
        = spawn(location).let(consumer)

/** packet function */

fun Player.sendPacketChat(raw: String, action: ChatAction = ChatAction.CHAT)
        = sendPacketChat(ChatSerializer.fromRaw(raw), action)

fun Player.sendPacketChat(component: ChatComponent, action: ChatAction = ChatAction.CHAT)
        = PacketOutChat(component, action).send(this)

fun Player.sendPacketTitle(title: String, subTitle: String? = null, fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20)
        = sendPacketTitle(ChatSerializer.fromRaw(title), if(subTitle == null) null else ChatSerializer.fromRaw(subTitle), fadeIn, stay, fadeOut)

fun Player.sendPacketTitle(title: ChatComponent, subTitle: ChatComponent? = null, fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20) {
    var packet = PacketOutTitle(fadeIn, stay, fadeOut)
    packet.send(this)
    if(subTitle != null) {
        packet = PacketOutTitle(PacketOutTitle.Action.SUBTITLE, subTitle)
        packet.send(this)
    }
    packet = PacketOutTitle(PacketOutTitle.Action.TITLE, title)
    packet.send(this)
}

fun Player.sendPacketTitleReset()
        = PacketOutTitle(PacketOutTitle.Action.RESET, null).send(this)
