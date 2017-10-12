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

@file:JvmName("Security")

package com.minecraft.moonlake.api.security

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Base64 Encoder & Decoder
 * @see [java.util.Base64]
 */

fun base64E(src: ByteArray): ByteArray
        = Base64.getEncoder().encode(src)

fun base64EString(src: ByteArray): String
        = Base64.getEncoder().encodeToString(src)

fun base64EString(src: String, charset: Charset = Charsets.ISO_8859_1): String
        = Base64.getEncoder().encodeToString(src.toByteArray(charset))

fun base64D(src: ByteArray): ByteArray
        = Base64.getDecoder().decode(src)

fun base64DString(src: String, charset: Charset = Charsets.ISO_8859_1): ByteArray
        = Base64.getDecoder().decode(src.toByteArray(charset))

/**
 * Message Digest
 * @see [java.security.MessageDigest]
 */

internal object Digests {

    @JvmStatic
    @JvmName("getDigest")
    @Throws(IllegalArgumentException::class)
    fun getDigest(type: String): MessageDigest = try {
        MessageDigest.getInstance(type)
    } catch (e: NoSuchAlgorithmException) {
        throw IllegalArgumentException(e)
    }
}

val md5Digest: MessageDigest
    get() = Digests.getDigest("MD5")

val sha1Digest: MessageDigest
    get() = Digests.getDigest("SHA-1")

val sha256Digest: MessageDigest
    get() = Digests.getDigest("SHA-256")

val sha384Digest: MessageDigest
    get() = Digests.getDigest("SHA-384")

val sha512Digest: MessageDigest
    get() = Digests.getDigest("SHA-512")

fun md5Byte(src: ByteArray): ByteArray
        = md5Digest.digest(src)

fun md5(src: ByteArray): String
        = Hex.encodeHexString(md5Byte(src))

fun md5(src: String, charset: Charset = Charsets.ISO_8859_1): String
        = md5(src.toByteArray(charset))

fun sha1Byte(src: ByteArray): ByteArray
        = sha1Digest.digest(src)

fun sha1(src: ByteArray): String
        = Hex.encodeHexString(sha1Byte(src))

fun sha1(src: String, charset: Charset = Charsets.ISO_8859_1): String
        = sha1(src.toByteArray(charset))

fun sha256Byte(src: ByteArray): ByteArray
        = sha256Digest.digest(src)

fun sha256(src: ByteArray): String
        = Hex.encodeHexString(sha256Byte(src))

fun sha256(src: String, charset: Charset = Charsets.ISO_8859_1): String
        = sha256(src.toByteArray(charset))

fun sha384Byte(src: ByteArray): ByteArray
        = sha384Digest.digest(src)

fun sha384(src: ByteArray): String
        = Hex.encodeHexString(sha384Byte(src))

fun sha384(src: String, charset: Charset = Charsets.ISO_8859_1): String
        = sha384(src.toByteArray(charset))

fun sha512Byte(src: ByteArray): ByteArray
        = sha512Digest.digest(src)

fun sha512(src: ByteArray): String
        = Hex.encodeHexString(sha512Byte(src))

fun sha512(src: String, charset: Charset = Charsets.ISO_8859_1): String
        = sha512(src.toByteArray(charset))

fun hexByte(src: ByteArray): CharArray
        = Hex.encodeHex(src)

/**
 * Hex Util
 */

internal object Hex {

    @JvmStatic
    private val TABLE = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    @JvmStatic
    @JvmName("encodeHex")
    fun encodeHex(src: ByteArray): CharArray {
        val length = src.size
        val buffer = CharArray(length shl 1)
        var index = 0; var k = 0
        while(index < length) {
            buffer[k++] = TABLE[(240 and src[index].toInt()).ushr(4)]
            buffer[k++] = TABLE[15 and src[index].toInt()]
            ++index
        }
        return buffer
    }

    @JvmStatic
    @JvmName("encodeHexString")
    fun encodeHexString(src: ByteArray): String
            = String(encodeHex(src))
}

/**
 * Cipher Util
 */
object Ciphers {

    @JvmStatic
    @JvmName("decodePublicKey")
    @Throws(IOException::class)
    fun decodePublicKey(encodedKey: ByteArray): PublicKey = try {
        KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(encodedKey))
    } catch(e: GeneralSecurityException) {
        throw IOException("无法解密公钥.", e)
    }

    @JvmStatic
    @JvmName("decryptSharedKey")
    @Throws(IOException::class)
    fun decryptSharedKey(privateKey: PrivateKey, sharedKey: ByteArray): SecretKey
            = SecretKeySpec(decryptData(privateKey, sharedKey), "AES")

    @JvmStatic
    @JvmName("encryptData")
    @Throws(IOException::class)
    fun encryptData(key: Key, data: ByteArray): ByteArray
            = runEncryption(Cipher.ENCRYPT_MODE, key, data)

    @JvmStatic
    @JvmName("decryptData")
    @Throws(IOException::class)
    fun decryptData(key: Key, data: ByteArray): ByteArray
            = runEncryption(Cipher.DECRYPT_MODE, key, data)

    @JvmStatic
    @JvmName("encryptServerId")
    @Throws(IOException::class)
    fun encryptServerId(serverId: String, publicKey: PublicKey, secretKey: SecretKey): ByteArray = try {
        val digest = sha1Digest
        digest.update(serverId.toByteArray(Charsets.ISO_8859_1))
        digest.update(secretKey.encoded)
        digest.update(publicKey.encoded)
        digest.digest()
    } catch(e: UnsupportedEncodingException) {
        throw IOException("无法生成服务器 Id 哈希值.", e)
    }

    @JvmStatic
    @JvmName("runEncryption")
    @Throws(IOException::class)
    private fun runEncryption(mode: Int, key: Key, data: ByteArray): ByteArray = try {
        val cipher = Cipher.getInstance(key.algorithm)
        cipher.init(mode, key)
        cipher.doFinal(data)
    } catch(e: GeneralSecurityException) {
        throw IOException("无法运行加密.", e)
    }
}
