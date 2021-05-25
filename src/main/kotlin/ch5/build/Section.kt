package ch5.build

/**
 * Section
 * 一个段是 所有段的父类。
 * @constructor Create empty Section
 */
interface Section{
    /**
     * Get byte array
     * 获取这个段的所有字节数组。
     * @return
     */
    fun getByteArray():ByteArray

    /**
     * Get size
     * 获取这个段的所有字节的长度。
     * @return
     */
    fun getSize():Int

    /**
     * Get size
     * 获取这个段的所有字节的长度，按照align进行对齐。
     * @param align
     * @return
     */
    fun getSize(align: Int): Int {
        val size = getSize()
        if (size % align == 0) return size
        return size - size % align + align
    }
}