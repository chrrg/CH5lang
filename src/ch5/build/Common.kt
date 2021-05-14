package ch5.build

import java.nio.charset.Charset

class UTF8ByteArray(val value: String) : Section {
    private val bytes = "$value\u0000".toByteArray(Charsets.UTF_8)
    override fun getByteArray() = bytes
    override fun getSize() = bytes.size
}

class GBKByteArray(val value: String) : Section {
    private val bytes = "$value\u0000".toByteArray(Charset.forName("GBK"))
    override fun getByteArray() = bytes
    override fun getSize() = bytes.size
}

class ConstByteSection(val value: Int) : Section {
    override fun getByteArray() = byteArrayOf(value.toByte())
    override fun getSize() = 1
}

class ConstWordSection(val value: Int) : Section {
    override fun getByteArray() = byteArrayOf(value.toByte(), (value shr 8).toByte())
    override fun getSize() = 2
}

class ConstDwordSection(val value: Int) : Section {
    override fun getByteArray() = byteArrayOf(
        value.toByte(),
        (value shr 8).toByte(),
        (value shr 16).toByte(),
        (value shr 24).toByte()
    )

    override fun getSize() = 4
}