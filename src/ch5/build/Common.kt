package ch5.build

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

open class ByteArraySection : Section {
    class MyByteArrayOutputStream : ByteArrayOutputStream() {
        operator fun get(index: Int): Byte {
            return buf[index]
        }

        operator fun set(index: Int, value: Byte) {
            buf[index] = value
        }
    }

    protected val byte = MyByteArrayOutputStream()
    private var enable = true
    fun setEnable(status: Boolean) {
        enable = status
    }

    fun dword(value: Int) {
        byte.write(value)
        byte.write(value shr 8)
        byte.write(value shr 16)
        byte.write(value shr 24)
    }

    fun word(value: Int) {
        byte.write(value)
        byte.write(value shr 8)
    }

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

open class FixableSection : ByteArraySection() {
    class FixCore(val offset: Int, val size: Int, val symbol: String)
    class FixedCore(val value: Int, val symbol: String)

    private val waitFix = arrayListOf<FixCore>()
    private val fixed = arrayListOf<FixedCore>()

    private fun isFixed(symbol: String): FixedCore? {
        for (i in fixed) if (i.symbol == symbol) return i
        return null
    }

    fun dword(value: Int, symbol: String) {
        isFixed(symbol)?.let {
            super.dword(it.value)
        } ?: run {
            waitFix.add(FixCore(getSize(), 4, symbol))
            super.dword(value)
        }
    }

    fun word(value: Int, symbol: String) {
        isFixed(symbol)?.let {
            super.word(it.value)
        } ?: run {
            waitFix.add(FixCore(getSize(), 2, symbol))
            super.word(value)
        }
    }

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

    fun dwordFix(value: Int, symbol: String) {
        fixed.add(FixedCore(value, symbol))
        for (i in waitFix) if (i.symbol == symbol) fix(i.offset, 4, value)
    }

    fun wordFix(value: Int, symbol: String) {
        fixed.add(FixedCore(value, symbol))
        for (i in waitFix) if (i.symbol == symbol) fix(i.offset, 2, value)
    }

    fun byteFix(value: Int, symbol: String) {
        fixed.add(FixedCore(value, symbol))
        for (i in waitFix) if (i.symbol == symbol) fix(i.offset, 1, value)
    }
}

class UTF8ByteArray(var value: String) : Section {
    private val bytes = "$value\u0000".toByteArray(Charsets.UTF_8)
    override fun getByteArray() = bytes
    override fun getSize() = bytes.size
}

class GBKByteArray(var value: String) : Section {
    private val bytes = "$value\u0000".toByteArray(Charset.forName("GBK"))
    override fun getByteArray() = bytes
    override fun getSize() = bytes.size
}

class ByteSection(var value: Int) : Section {
    override fun getByteArray() = byteArrayOf(value.toByte())
    override fun getSize() = 1
}

class WordSection(var value: Int) : Section {
    override fun getByteArray() = byteArrayOf(value.toByte(), (value shr 8).toByte())
    override fun getSize() = 2
}

class DwordSection(var value: Int) : Section {
    override fun getByteArray() = byteArrayOf(
        value.toByte(),
        (value shr 8).toByte(),
        (value shr 16).toByte(),
        (value shr 24).toByte()
    )

    override fun getSize() = 4
}