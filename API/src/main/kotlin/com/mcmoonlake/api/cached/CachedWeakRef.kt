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

package com.mcmoonlake.api.cached

/**
 * ## CachedWeakRef (高速缓存弱引用)
 *
 * @see [CachedReferenceAbstract]
 * @see [CachedWeakReference]
 * @author lgou2w
 * @since 2.0
 * @constructor CachedWeakRef
 * @param map Cache map. If `null` then use a non-thread-safe [HashMap].
 * @param map 缓存映射. 如果为 `null` 则使用非线程安全的 [HashMap].
 */
abstract class CachedWeakRef<K, V>(
        map: MutableMap<K, CachedWeakReference<K, V>>?
) : CachedReferenceAbstract<K, V, CachedWeakReference<K, V>>(map) {

    /**
     * @constructor CachedWeakRef
     */
    constructor() : this(null)

    final override fun produceRef(key: K, value: V, queue: CachedReferenceQueue<V>): CachedWeakReference<K, V>
            = CachedWeakReference(key, value, queue)
}
