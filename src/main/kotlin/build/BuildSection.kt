package ch5.build

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileOutputStream

/**
 * Build section
 * 构建段 每一个子段可以被添加到当前段里面来构建出一个更大的段
 * @constructor Create empty Build section
 */
open class BuildSection : Section, Iterable<Section> {
    private var before: BuildSection? = null
    private val list = arrayListOf<Section>()
    private var after: BuildSection? = null
    private var parent: BuildSection? = null

    /**
     * Get parent
     * 获取父级段
     * @return
     */
    fun getParent(): BuildSection {
        return parent!!
    }

    /**
     * Get before
     * 在当前的段的前面可以进行的段操作
     * @return
     */
    fun getBefore(): BuildSection {
        if (before == null) before = BuildSection()
        return before!!
    }

    /**
     * Get after
     * 在当前的段的后面可以进行的段操作
     * @return
     */
    fun getAfter(): BuildSection {
        if (after == null) after = BuildSection()
        return after!!
    }

    /**
     * Offset
     * 浅遍历获取子段在当前的偏移值
     * @param section
     * @return
     */
    fun offset(section: Section): Int {
        var size = 0
        for (i in list) {
            if (i == section) return size
            size += i.getSize()
        }
        return offsetDeep(section)
//        throw Exception("?")
    }

    /**
     * Offset deep
     * 深度遍历获取子段在当前段的偏移值
     * @param section
     * @return
     */
    fun offsetDeep(section: Section): Int {
        val a = _offsetDeep(section)
        if (a == -1) throw Exception("offset不存在!")
        return a
    }

    /**
     * _offset deep
     * 内部函数 深度遍历获取子段在当前段的偏移值
     * @param section
     * @return
     */
    fun _offsetDeep(section: Section): Int {
        var size = 0
        before?.let {
            if (it == section) return size
            val result = it._offsetDeep(section)
            if (result != -1) return size + result
            size += it.getSize()
        }
        for (i in list) {
            if (i == section) return size
            if (i is BuildSection) {
                val result = i._offsetDeep(section)
                if (result != -1) return size + result
            }
            size += i.getSize()
        }
        after?.let {
            if (it == section) return size
            val result = it._offsetDeep(section)
            if (result != -1) return size + result
            size += it.getSize()
        }
        //深度搜索
        return -1
    }


    /**
     * Add
     * 在当前段下添加子段
     * @param T
     * @param section
     * @return
     */
    fun <T : Section> add(section: T): T {
        if (list.contains(section)) throw Exception("")
        list.add(section)
        if (section is BuildSection) {
            if (section.parent != null && section.parent != this) throw Exception("已经有其它父级，不允许重复添加")
            section.parent = this
        }
        return section
    }

    /**
     * Output stream
     * 将自己输出到输出流中
     * @param bw
     */
    private fun outputStream(bw: DataOutputStream) {
        before?.outputStream(bw)
        for (i in list) bw.write(i.getByteArray())
        after?.outputStream(bw)
    }

    /**
     * Output file
     * 输出为一个文件
     * @param file
     */
    fun outputFile(file: String) {
        val bw = DataOutputStream(FileOutputStream(file))
        outputStream(bw)
        bw.close()
    }

    /**
     * Get byte array
     * 获取所有字节数组
     * @return ByteArray
     */
    override fun getByteArray(): ByteArray {
        val bw = ByteArrayOutputStream()
        before?.let {
            bw.write(it.getByteArray())
        }
        for (i in list) bw.write(i.getByteArray())
        after?.let {
            bw.write(it.getByteArray())
        }
        return bw.toByteArray()
    }

    /**
     * Get size
     * 获取当前段的总大小
     * @return
     */
    override fun getSize(): Int {
        var size = 0
        size += before?.getSize() ?: 0
        for (i in list) size += i.getSize()
        size += after?.getSize() ?: 0
        return size
    }

    /**
     * Get
     * 获取当前段指定索引的子段
     * @param index
     */
    operator fun get(index: Int) = list[index]
    override fun iterator() = list.iterator()

    /**
     * Do fix
     * 进行修复
     * @param buildStruct
     */
    fun doFix(buildStruct: BuildStruct) {
        before?.doFix(buildStruct)
        for (i in list) if (i is FixableSection) i.doFix(buildStruct) else if (i is BuildSection) i.doFix(buildStruct)
        after?.doFix(buildStruct)
    }
}