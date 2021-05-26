package ch5.build

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

/**
 * Byte array section
 * 让段作为一个字节数组
 * @constructor Create empty Byte array section
 */
open class ByteArraySection : Section {
    /**
     * My byte array output stream
     * 继承原生类以获取buf
     * @constructor Create empty My byte array output stream
     */
    class MyByteArrayOutputStream : ByteArrayOutputStream() {
        /**
         * Get
         *
         * @param index
         * @return
         */
        operator fun get(index: Int): Byte {
            return buf[index]
        }

        /**
         * Set
         *
         * @param index
         * @param value
         */
        operator fun set(index: Int, value: Byte) {
            buf[index] = value
        }
    }

    protected val byte = MyByteArrayOutputStream()
    private var enable = true

    /**
     * Set enable
     * 设置启用，如果禁用，将不会输出
     * @param status
     */
    fun setEnable(status: Boolean) {
        enable = status
    }

    /**
     * Dword
     * 写出4个字节
     * @param value
     */
    fun dword(value: Int) {
        byte.write(value)
        byte.write(value shr 8)
        byte.write(value shr 16)
        byte.write(value shr 24)
    }

    /**
     * Word
     * 写入两个字节
     * @param value
     */
    fun word(value: Int) {
        byte.write(value)
        byte.write(value shr 8)
    }

    /**
     * Byte
     * 写入一个字节
     * @param value
     */
    fun byte(value: Int) {
        byte.write(value)
    }
    override fun getByteArray(): ByteArray {
        if (!enable) return byteArrayOf()
        return byte.toByteArray()
    }

    override fun getSize(): Int {
        if (!enable) return 0
        return byte.size()
    }
}

/**
 * Fixable section
 * 这是一个可以修复的段。代表你可以先插入默认的字节。可以延迟或提前进行修复。
 * 支持使用Symbol进行修复，支持回调函数修复。
 * @constructor Create empty Fixable section
 */
open class FixableSection : ByteArraySection() {
    /**
     * Fix core
     *
     * @property offset
     * @property size
     * @property symbol
     * @constructor Create empty Fix core
     */
    class FixCore(val offset: Int, val size: Int, val symbol: String)

    /**
     * Fixed core
     *
     * @property value
     * @property symbol
     * @constructor Create empty Fixed core
     */
    open class FixedCore(val value: Int, val symbol: String)

    /**
     * Fixed fun core
     *
     * @property fn
     * @constructor
     *
     * @param value
     * @param symbol
     */
    class FixedFunCore(value: Int, symbol: String, val fn: (value: Int, buildStruct: BuildStruct) -> Int) :
        FixedCore(value, symbol)


    private val waitFix = arrayListOf<FixCore>()
    private val fixed = arrayListOf<FixedCore>()

    private fun isFixed(symbol: String): FixedCore? {
        for (i in fixed) if (i.symbol == symbol) return i
        return null
    }

    /**
     * Offset
     *
     * @param symbol
     * @return
     */
    fun offset(symbol: String): Int {
        return waitFix.find { it.symbol == symbol }!!.offset
    }

    /**
     * Dword
     *
     * @param value
     * @param symbol
     */
    fun dword(value: Int, symbol: String) {
        isFixed(symbol)?.let {
            super.dword(it.value)
        } ?: run {
            waitFix.add(FixCore(getSize(), 4, symbol))
            super.dword(value)
        }
    }

    /**
     * Dword
     *
     * @param value
     * @param fn
     * @receiver
     */
    fun dword(value: Int, fn: (Int, BuildStruct) -> Int) {
        val symbol = "fix_${fn.hashCode()}_${Math.random()}"
        waitFix.add(FixCore(getSize(), 4, symbol))
        fixed.add(FixedFunCore(value, symbol, fn))
        super.dword(value)
    }

    /**
     * Word
     *
     * @param value
     * @param symbol
     */
    fun word(value: Int, symbol: String) {
        isFixed(symbol)?.let {
            super.word(it.value)
        } ?: run {
            waitFix.add(FixCore(getSize(), 2, symbol))
            super.word(value)
        }
    }

    /**
     * Byte
     *
     * @param value
     * @param symbol
     */
    fun byte(value: Int, symbol: String) {
        isFixed(symbol)?.let {
            super.byte(it.value)
        } ?: run {
            waitFix.add(FixCore(getSize(), 1, symbol))
            super.byte(value)
        }
    }

    private fun fix(offset: Int, size: Int, value: Int) {
        when (size) {
            4 -> {
                byte[offset]
                byte[offset] = value.toByte()
                byte[offset + 1] = (value shr 8).toByte()
                byte[offset + 2] = (value shr 16).toByte()
                byte[offset + 3] = (value shr 24).toByte()
            }
            2 -> {
                byte[offset] = value.toByte()
                byte[offset + 1] = (value shr 8).toByte()
            }
            1 -> byte[offset] = (value.toByte())
            else -> throw Exception("compiler: size is not valid")
        }
    }

    /**
     * Fix
     *
     * @param value
     * @param symbol
     */
    fun fix(value: Int, symbol: String) {
        fixed.add(FixedCore(value, symbol))
    }

    /**
     * Fix
     *
     * @param value
     * @param symbol
     * @param fn
     * @receiver
     */
    fun fix(value: Int = 0, symbol: String, fn: (Int, BuildStruct) -> Int) {
        fixed.add(FixedFunCore(value, symbol, fn))
    }

    /**
     * Do fix
     *
     * @param buildStruct
     */
    fun doFix(buildStruct: BuildStruct) {
        for (i in fixed)
            for (j in waitFix.filter { it.symbol == i.symbol }) {
                if (i is FixedFunCore) {
                    fix(j.offset, j.size, i.fn(i.value, buildStruct))
                } else {
                    fix(j.offset, j.size, i.value)
                }
            }
    }
}

/**
 * Utf8 byte array
 *  Utf8编码的字符串的字节数组。
 * @property value
 * @constructor Create empty U t f8byte array
 */
class UTF8ByteArray(var value: String) : Section {
    private val bytes = "$value\u0000".toByteArray(Charsets.UTF_8)
    override fun getByteArray() = bytes
    override fun getSize() = bytes.size
}

/**
 * Gbk byte array
 * Gbk编码的字符串的字节数组。
 * @property value
 * @constructor Create empty G b k byte array
 */
class GBKByteArray(var value: String) : Section {
    private val bytes = "$value\u0000".toByteArray(Charset.forName("GBK"))
    override fun getByteArray() = bytes
    override fun getSize() = bytes.size
}

/**
 * Byte section
 * 固定一个字节的段
 * @property value
 * @constructor Create empty Byte section
 */
class ByteSection(var value: Int) : Section {
    override fun getByteArray() = byteArrayOf(value.toByte())
    override fun getSize() = 1
}

/**
 * Word section
 * 固定两个字节的段
 * @property value
 * @constructor Create empty Word section
 */
class WordSection(var value: Int) : Section {
    override fun getByteArray() = byteArrayOf(value.toByte(), (value shr 8).toByte())
    override fun getSize() = 2
}

/**
 * Dword section
 * 固定4个字节的段
 * @property value
 * @constructor Create empty Dword section
 */
class DwordSection(var value: Int) : Section {
    override fun getByteArray() = byteArrayOf(
        value.toByte(),
        (value shr 8).toByte(),
        (value shr 16).toByte(),
        (value shr 24).toByte()
    )

    override fun getSize() = 4
}