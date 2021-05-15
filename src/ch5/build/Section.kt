package ch5.build

interface Section{
    fun getByteArray():ByteArray
    fun getSize():Int
    fun getSize(align: Int): Int {
        val size = getSize()
        if (size % align == 0) return size
        return size - size % align + align
    }
}