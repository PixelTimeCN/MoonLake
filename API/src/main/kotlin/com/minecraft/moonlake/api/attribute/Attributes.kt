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

package com.minecraft.moonlake.api.attribute

import com.minecraft.moonlake.api.converter.ConverterEquivalent
import com.minecraft.moonlake.api.currentMCVersion
import com.minecraft.moonlake.api.entity.Entities
import com.minecraft.moonlake.api.exception.MoonLakeException
import com.minecraft.moonlake.api.isOrLater
import com.minecraft.moonlake.api.parseInt
import com.minecraft.moonlake.api.reflect.ExactReflect
import com.minecraft.moonlake.api.reflect.FuzzyReflect
import com.minecraft.moonlake.api.reflect.StructureModifier
import com.minecraft.moonlake.api.reflect.accessor.AccessorMethod
import com.minecraft.moonlake.api.reflect.accessor.Accessors
import com.minecraft.moonlake.api.util.Enums
import com.minecraft.moonlake.api.utility.MinecraftReflection
import com.minecraft.moonlake.api.version.IllegalBukkitVersionException
import org.bukkit.entity.LivingEntity
import java.lang.reflect.Modifier
import java.util.*

object Attributes {

    /**
     * AttributeType : GenericAttributes#IAttribute
     */
    @JvmStatic
    private val attributeSupportMap: MutableMap<AttributeType, Any> = HashMap()

    /** api */

    /**
     * Gets the attribute instance of the specified entity.
     *
     * @throws IllegalBukkitVersionException If the server does not support attribute type.
     * @throws NoSuchElementException If the entity does not have this attribute type.
     */
    @JvmStatic
    @JvmName("getEntityAttribute")
    @Throws(IllegalBukkitVersionException::class, NoSuchElementException::class)
    fun getEntityAttribute(livingEntity: LivingEntity, type: AttributeType): Attribute {
        val iAttribute = attributeSupportMap[type]
        if(iAttribute == null || (type.mcVer != null && !currentMCVersion().isOrLater(type.mcVer)))
            throw IllegalBukkitVersionException("当前 Bukkit 版本不支持此 $type 属性类型.")
        val attributeInstance = getAttributeInstance(livingEntity, iAttribute) ?: throw NoSuchElementException("此实体 $livingEntity 不存在类型为 $type 的属性实例.")
        return EntityAttributeImpl(attributeInstance, type)
    }

    /**
     * Gets the instance of the specified entity, which is added if the entity has no instance.
     *
     * @throws IllegalBukkitVersionException If the server does not support attribute type.
     */
    @JvmStatic
    @JvmName("getEntityAttributeOrPut")
    @Throws(IllegalBukkitVersionException::class)
    fun getEntityAttributeOrPut(livingEntity: LivingEntity, type: AttributeType): Attribute {
        val iAttribute = attributeSupportMap[type]
        if(iAttribute == null || (type.mcVer != null && !currentMCVersion().isOrLater(type.mcVer)))
            throw IllegalBukkitVersionException("当前 Bukkit 版本不支持此 $type 属性类型.")
        val attributeInstance = getAttributeInstanceOrPut(livingEntity, iAttribute)
        return EntityAttributeImpl(attributeInstance, type)
    }

    /** implement */

    private class EntityAttributeImpl(val handle: Any, override val type: AttributeType) : Attribute {
        override val defValue: Double
            get() = type.def
        override var baseValue: Double
            get() = attributeInstanceGetBaseValue.invoke(handle) as Double
            set(value) { attributeInstanceSetBaseValue.invoke(handle, value) }
        override val value: Double
            get() = attributeInstanceGetValue.invoke(handle) as Double
        override val modifiers: Collection<AttributeModifier>
            get() = getAttributeInstanceModifiers(handle)
        override fun addModifier(modifier: AttributeModifier)
                { attributeInstanceAddModifier.invoke(handle, convertModifier(modifier)) }
        override fun removeModifier(modifier: AttributeModifier)
                { attributeInstanceRemoveModifier.invoke(handle, convertModifier(modifier)) }
    }

    @JvmStatic
    @JvmName("getAttributeInstanceModifiers")
    internal fun getAttributeInstanceModifiers(handle: Any): Collection<AttributeModifier> {
        val result = ArrayList<AttributeModifier>()
        @Suppress("UNCHECKED_CAST")
        val iterator = (attributeInstanceGetModifiers.invoke(handle) as Collection<Any>).iterator()
        iterator.forEach {
            val sm = attributeModifierStructure.withTarget<Any>(it)
            val uuid = sm.withType<UUID>(UUID::class.java).readSafe(0) ?: UUID.randomUUID()
            val name = sm.withType<String>(String::class.java).readSafe(0) ?: "null"
            val amount = sm.withType<Double>(Double::class.java).readSafe(0) ?: .0
            val operation = sm.withType(Int::class.java, attributeModifierOperationConverter).readSafe(0) ?: Operation.ADD
            result.add(AttributeModifier(name, operation, amount, uuid))
        }
        return result
    }

    @JvmStatic
    @JvmName("convertModifier")
    internal fun convertModifier(modifier: AttributeModifier): Any {
        val constructor = ExactReflect.fromClass(getAttributeModifierClass(), true).getConstructor(arrayOf(UUID::class.java, String::class.java, Double::class.java, Int::class.java))
        return constructor.newInstance(modifier.uuid, modifier.name, modifier.amount, modifier.operation.value)
    }

    @JvmStatic
    @JvmName("getAttributeMapClass")
    @Throws(MoonLakeException::class)
    private fun getAttributeMapClass(): Class<*>
            = MinecraftReflection.getMinecraftClass("AttributeMap")

    @JvmStatic
    @JvmName("getIAttributeClass")
    @Throws(MoonLakeException::class)
    private fun getIAttributeClass(): Class<*>
            = MinecraftReflection.getMinecraftClass("IAttribute")

    @JvmStatic
    @JvmName("getAttributeInstanceClass")
    @Throws(MoonLakeException::class)
    private fun getAttributeInstanceClass(): Class<*>
            = MinecraftReflection.getMinecraftClass("AttributeInstance")

    @JvmStatic
    @JvmName("getAttributeModifierClass")
    @Throws(MoonLakeException::class)
    private fun getAttributeModifierClass(): Class<*>
            = MinecraftReflection.getMinecraftClass("AttributeModifier")

    @JvmStatic
    @JvmName("getGenericAttributesClass")
    @Throws(MoonLakeException::class)
    private fun getGenericAttributesClass(): Class<*>
            = MinecraftReflection.getMinecraftClass("GenericAttributes")

    @JvmStatic
    private val iAttributeGetName: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getIAttributeClass(), String::class.java, true, arrayOf()) }

    @JvmStatic
    private val entityLivingGetAttributeMap: AccessorMethod by lazy {
        Accessors.getAccessorMethod(FuzzyReflect.fromClass(MinecraftReflection.getEntityLivingClass(), true)
                .getMethodByParameters("getAttributeMap", getAttributeMapClass(), arrayOf())) }

    @JvmStatic
    @JvmName("getAttributeMap")
    private fun getAttributeMap(livingEntity: LivingEntity): Any?
            = entityLivingGetAttributeMap.invoke(Entities.asNMSEntity(livingEntity))

    @JvmStatic
    private val entityLivingGetAttributeInstance: AccessorMethod by lazy {
        Accessors.getAccessorMethod(FuzzyReflect.fromClass(MinecraftReflection.getEntityLivingClass(), true)
                .getMethodByParameters("getAttributeInstance", getAttributeInstanceClass(), arrayOf(getIAttributeClass()))) }

    @JvmStatic
    @JvmName("getAttributeInstance")
    private fun getAttributeInstance(livingEntity: LivingEntity, iAttribute: Any?): Any?
            = entityLivingGetAttributeInstance.invoke(Entities.asNMSEntity(livingEntity), iAttribute)

    @JvmStatic
    private val attributeMapGetInstance: AccessorMethod by lazy {
        Accessors.getAccessorMethod(FuzzyReflect.fromClass(getAttributeMapClass(), true)
                .getMethodByParameters("getInstance", getAttributeInstanceClass(), arrayOf(getIAttributeClass()))) }

    @JvmStatic
    private val attributeMapPutInstance: AccessorMethod by lazy {
        Accessors.getAccessorMethod(FuzzyReflect.fromClass(getAttributeMapClass(), true)
                .getMethodListByParameters(getAttributeInstanceClass(), arrayOf(getIAttributeClass()))
                .filterIndexed { index, method -> Modifier.isPublic(method.modifiers) && (method.name == "b" || index == 1) }
                .first()) }

    @JvmStatic
    @JvmName("getAttributeInstanceOrPut")
    private fun getAttributeInstanceOrPut(livingEntity: LivingEntity, iAttribute: Any?): Any {
        val attributeMap = getAttributeMap(livingEntity)
        var attributeInstance = attributeMapGetInstance.invoke(attributeMap, iAttribute)
        if(attributeInstance == null)
            attributeInstance = attributeMapPutInstance.invoke(attributeMap, iAttribute)
        return attributeInstance as Any
    }

    @JvmStatic
    private val attributeInstanceGetBaseValue: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getAttributeInstanceClass(), "b", true) }

    @JvmStatic
    private val attributeInstanceSetBaseValue: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getAttributeInstanceClass(), "setValue", true, Double::class.java) }

    @JvmStatic
    private val attributeInstanceGetValue: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getAttributeInstanceClass(), "getValue", true) }

    @JvmStatic
    private val attributeInstanceGetModifiers: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getAttributeInstanceClass(), Collection::class.java, true, arrayOf()) }

    @JvmStatic
    private val attributeInstanceAddModifier: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getAttributeInstanceClass(), "b", true, getAttributeModifierClass()) }

    @JvmStatic
    private val attributeInstanceRemoveModifier: AccessorMethod by lazy {
        Accessors.getAccessorMethod(getAttributeInstanceClass(), "c", true, getAttributeModifierClass()) }

    @JvmStatic
    private val attributeModifierStructure: StructureModifier<*> by lazy {
        StructureModifier.of(getAttributeModifierClass(), null, null) }

    @JvmStatic
    private val attributeModifierOperationConverter: ConverterEquivalent<Operation> by lazy {
        object: ConverterEquivalent<Operation> {
            override fun getGeneric(specific: Operation?): Any?
                    = specific?.value ?: 0
            override fun getSpecific(generic: Any?): Operation?
                    = Enums.ofValuable(Operation::class.java, generic?.parseInt()) ?: Operation.ADD
            override fun getSpecificType(): Class<Operation>
                    = Operation::class.java
        } }

    init {
        FuzzyReflect.fromClass(getGenericAttributesClass(), true).getFieldListByType(getIAttributeClass())
                .forEach {
                    it.isAccessible = true
                    val iAttribute = it.get(null)
                    val name = iAttributeGetName.invoke(iAttribute) as String
                    val type = Enums.ofValuable(AttributeType::class.java, name)
                    if(type != null && (type.mcVer == null || currentMCVersion().isOrLater(type.mcVer)))
                        attributeSupportMap.put(type, iAttribute)
                }
    }
}
